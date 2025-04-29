package org.lc.ficq.netty.processor;

import cn.hutool.core.bean.BeanUtil;
import com.alibaba.fastjson.JSON;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.AttributeKey;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.lc.ficq.contant.ChannelAttrKey;
import org.lc.ficq.enums.CmdType;
import org.lc.ficq.model.LoginInfo;
import org.lc.ficq.model.SendInfo;
import org.lc.ficq.model.SessionInfo;
import org.lc.ficq.util.JwtUtil;
import org.lc.ficq.util.OnlineSessionUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashMap;

/**
 * 登录处理器
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class LoginProcessor extends AbstractMessageProcessor<LoginInfo> {

    @Value("${jwt.accessToken.secret}")
    private String accessTokenSecret;

    private static final AttributeKey<Long> USER_ID_KEY = AttributeKey.valueOf(ChannelAttrKey.USER_ID);
    private static final AttributeKey<Integer> TERMINAL_TYPE_KEY = AttributeKey.valueOf(ChannelAttrKey.TERMINAL_TYPE);
    private static final AttributeKey<Long> HEARTBEAT_TIMES = AttributeKey.valueOf(ChannelAttrKey.HEARTBEAT_TIMES);

    @Override
    public void process(ChannelHandlerContext ctx, LoginInfo loginInfo) {
        try {
            if (loginInfo == null) {
                log.error("Received null loginInfo in process method");
                ctx.channel().close();
                return;
            }

            if (loginInfo.getAccessToken() == null) {
                log.error("AccessToken is null in loginInfo");
                ctx.channel().close();
                return;
            }

            // 验证 JWT token
            if (!JwtUtil.checkSign(loginInfo.getAccessToken(), accessTokenSecret)) {
                ctx.channel().close();
                log.warn("用户token校验不通过，强制下线, token:{}", loginInfo.getAccessToken());
                return;
            }

            // 解析 token 获取用户信息
            String strInfo = JwtUtil.getInfo(loginInfo.getAccessToken());
            if (strInfo == null) {
                log.error("Failed to get info from token");
                ctx.channel().close();
                return;
            }

            SessionInfo sessionInfo = JSON.parseObject(strInfo, SessionInfo.class);
            if (sessionInfo == null) {
                ctx.channel().close();
                log.warn("token解析失败，强制下线, token:{}", loginInfo.getAccessToken());
                return;
            }

            Long userId = sessionInfo.getUserId();
            Integer terminal = sessionInfo.getTerminal();

            if (userId == null || terminal == null) {
                log.error("Invalid session info: userId={}, terminal={}", userId, terminal);
                ctx.channel().close();
                return;
            }

            // 检查是否已在其他地方登录
            ChannelHandlerContext existingCtx = OnlineSessionUtil.getSession(userId, terminal);
            if (existingCtx != null && !ctx.channel().id().equals(existingCtx.channel().id())) {
                // 不允许多地登录,强制下线
                SendInfo sendInfo = new SendInfo();
                sendInfo.setCmd(CmdType.FORCE_LOGOUT.code());
                sendInfo.setData("您已在其他地方登录，将被强制下线");
                existingCtx.channel().writeAndFlush(sendInfo);
                
                // 移除旧的会话
                OnlineSessionUtil.removeSession(userId, terminal);
                
                log.info("异地登录，强制下线, userId:{}", userId);
            }

            // 设置channel属性
            ctx.channel().attr(USER_ID_KEY).set(userId);
            ctx.channel().attr(TERMINAL_TYPE_KEY).set(terminal);
            ctx.channel().attr(HEARTBEAT_TIMES).set(0L);

            // 添加会话
            OnlineSessionUtil.addSession(userId, terminal, ctx);

            // 构建响应消息
            SendInfo sendInfo = new SendInfo();
            sendInfo.setCmd(CmdType.LOGIN.code());
            sendInfo.setData(JSON.toJSONString(loginInfo));

            // 发送响应
            ctx.writeAndFlush(sendInfo);
            log.info("用户登录成功: userId={}, terminal={}", userId, terminal);
        } catch (Exception e) {
            log.error("Error processing login request: {}", e.getMessage(), e);
            ctx.channel().close();
        }
    }

    @Override
    public LoginInfo transForm(Object o) {
        HashMap map = (HashMap) o;
        return BeanUtil.fillBeanWithMap(map, new LoginInfo(), false);
    }
}
