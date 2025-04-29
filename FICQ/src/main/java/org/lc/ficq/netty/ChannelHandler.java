package org.lc.ficq.netty;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.AttributeKey;
import lombok.extern.slf4j.Slf4j;
import org.lc.ficq.contant.ChannelAttrKey;
import org.lc.ficq.enums.CmdType;
import org.lc.ficq.model.SendInfo;
import org.lc.ficq.netty.processor.AbstractMessageProcessor;
import org.lc.ficq.netty.processor.ProcessorFactory;
import org.lc.ficq.util.OnlineSessionUtil;

import java.util.Objects;

/**
 * WebSocket 长连接下 文本帧的处理器
 * 实现浏览器发送文本回写
 * 浏览器连接状态监控
 */
@Slf4j
public class ChannelHandler extends SimpleChannelInboundHandler<SendInfo> {

    /**
     * 读取到消息后进行处理
     *
     * @param ctx      channel上下文
     * @param sendInfo 发送消息
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, SendInfo sendInfo) {
        try {
            if (sendInfo == null) {
                log.error("Received null sendInfo");
                ctx.close();
                return;
            }

            if (sendInfo.getData() == null) {
                log.error("Received null data in sendInfo");
                ctx.close();
                return;
            }
            // 创建处理器进行处理
            AbstractMessageProcessor processor = ProcessorFactory.createProcessor(Objects.requireNonNull(CmdType.fromCode(sendInfo.getCmd())));
            processor.process(ctx, processor.transForm(sendInfo.getData()));
        } catch (Exception e) {
            log.error("Error processing message: {}", e.getMessage(), e);
            ctx.close();
        }
    }

    /**
     * 出现异常的处理 打印报错日志
     *
     * @param ctx   channel上下文
     * @param cause 异常信息
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        log.error(cause.getMessage());
        //关闭上下文
        ctx.close();
    }

    /**
     * 监控浏览器上线
     *
     * @param ctx channel上下文
     */
    @Override
    public void handlerAdded(ChannelHandlerContext ctx) {
        log.info("新连接建立: {}", ctx.channel().id().asLongText());
    }

    /**
     * 监控浏览器下线
     *
     * @param ctx channel上下文
     */
    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) {
        // 获取用户ID和终端类型
        AttributeKey<Long> userIdAttr = AttributeKey.valueOf(ChannelAttrKey.USER_ID);
        AttributeKey<Integer> terminalAttr = AttributeKey.valueOf(ChannelAttrKey.TERMINAL_TYPE);
        
        Long userId = ctx.channel().attr(userIdAttr).get();
        Integer terminal = ctx.channel().attr(terminalAttr).get();
        
        if (userId != null && terminal != null) {
            // 移除会话
            OnlineSessionUtil.removeSession(userId, terminal);
        }
        
        log.info("连接断开: {}", ctx.channel().id().asLongText());
    }

    /**
     * 处理空闲状态
     */
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) {
        if (evt instanceof IdleStateEvent event) {
            if (event.state() == IdleState.READER_IDLE) {
                // 读空闲，关闭连接
                ctx.close();
            }
        }
    }
}