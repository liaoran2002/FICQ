package org.lc.ficq.netty.processor;

import cn.hutool.core.bean.BeanUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.AttributeKey;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.lc.ficq.contant.ChannelAttrKey;
import org.lc.ficq.enums.CmdType;
import org.lc.ficq.model.HeartbeatInfo;
import org.lc.ficq.model.SendInfo;
import org.springframework.stereotype.Component;

import java.util.HashMap;

@Slf4j
@Component
@RequiredArgsConstructor
public class HeartbeatProcessor extends AbstractMessageProcessor<HeartbeatInfo> {

    @Override
    public void process(ChannelHandlerContext ctx, HeartbeatInfo beatInfo) {
        // 响应ws
        SendInfo<Object> sendInfo = new SendInfo<>();
        sendInfo.setCmd(CmdType.HEART_BEAT.code());
        ctx.channel().writeAndFlush(sendInfo);
        // 获取当前心跳次数
        AttributeKey<Long> heartBeatAttr = AttributeKey.valueOf(ChannelAttrKey.HEARTBEAT_TIMES);
        Long heartbeatTimes = ctx.channel().attr(heartBeatAttr).get();
        // 如果为空，初始化为0
        if (heartbeatTimes == null) {
            heartbeatTimes = 0L;
        }
        // 递增心跳次数
        ctx.channel().attr(heartBeatAttr).set(++heartbeatTimes);
        if (heartbeatTimes % 10 == 0) {
            // 每心跳10次，用户在线状态续一次命
            AttributeKey<Long> userIdAttr = AttributeKey.valueOf(ChannelAttrKey.USER_ID);
            Long userId = ctx.channel().attr(userIdAttr).get();
            AttributeKey<Integer> terminalAttr = AttributeKey.valueOf(ChannelAttrKey.TERMINAL_TYPE);
            Integer terminal = ctx.channel().attr(terminalAttr).get();
        }
        AttributeKey<Long> userIdAttr = AttributeKey.valueOf(ChannelAttrKey.USER_ID);
        Long userId = ctx.channel().attr(userIdAttr).get();
        log.debug("心跳,userId:{},{}",userId,ctx.channel().id().asLongText());
    }

    @Override
    public HeartbeatInfo transForm(Object o) {
        HashMap map = (HashMap) o;
        return BeanUtil.fillBeanWithMap(map, new HeartbeatInfo(), false);
    }
}
