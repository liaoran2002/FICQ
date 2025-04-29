package org.lc.ficq.netty.processor;

import io.netty.channel.ChannelHandlerContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.lc.ficq.enums.CmdType;
import org.lc.ficq.enums.SendCode;
import org.lc.ficq.model.RecvInfo;
import org.lc.ficq.model.SendInfo;
import org.lc.ficq.model.SendResult;
import org.lc.ficq.model.UserInfo;
import org.lc.ficq.netty.UserChannelCtxMap;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Slf4j
@Component
@RequiredArgsConstructor
public class PrivateMessageProcessor extends AbstractMessageProcessor<RecvInfo> {

    @Override
    public void process(ChannelHandlerContext ctx, RecvInfo recvInfo) {
        UserInfo sender = recvInfo.getSender();
        UserInfo receiver = recvInfo.getReceivers().get(0);
        log.info("接收到私聊消息，发送者:{},接收者:{}，内容:{}", sender.getId(), receiver.getId(), recvInfo.getData());
        try {
            ChannelHandlerContext channelCtx = UserChannelCtxMap.getChannelCtx(receiver.getId(), receiver.getTerminal());
            if (!Objects.isNull(channelCtx)) {
                // 推送消息到用户
                SendInfo<Object> sendInfo = new SendInfo<>();
                sendInfo.setCmd(CmdType.PRIVATE_MESSAGE.code());
                sendInfo.setData(recvInfo.getData());
                channelCtx.channel().writeAndFlush(sendInfo);
                // 消息发送成功确认
                sendResult(recvInfo, SendCode.SUCCESS);
            } else {
                // 消息推送失败确认
                sendResult(recvInfo, SendCode.NOT_FIND_CHANNEL);
                log.error("未找到channel，发送者:{},接收者:{}，内容:{}", sender.getId(), receiver.getId(), recvInfo.getData());
            }
        } catch (Exception e) {
            // 消息推送失败确认
            sendResult(recvInfo, SendCode.UNKONW_ERROR);
            log.error("发送异常，发送者:{},接收者:{}，内容:{}", sender.getId(), receiver.getId(), recvInfo.getData(), e);
        }
    }

    private void sendResult(RecvInfo recvInfo, SendCode sendCode) {
        if (recvInfo.getSendResult()) {
            SendResult<Object> result = new SendResult<>();
            result.setSender(recvInfo.getSender());
            result.setReceiver(recvInfo.getReceivers().get(0));
            result.setCode(sendCode.code());
            result.setData(recvInfo.getData());
            // 推送到结果队列
        }
    }
}
