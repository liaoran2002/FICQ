package org.lc.ficq.netty.processor;

import io.netty.channel.ChannelHandlerContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.lc.ficq.enums.CmdType;
import org.lc.ficq.enums.SendCode;
import org.lc.ficq.model.*;
import org.lc.ficq.netty.UserChannelCtxMap;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

@Slf4j
@Component
@RequiredArgsConstructor
public class GroupMessageProcessor extends AbstractMessageProcessor<RecvInfo> {

    @Override
    public void process(ChannelHandlerContext ctx, RecvInfo recvInfo) {
        UserInfo sender = recvInfo.getSender();
        List<UserInfo> receivers = recvInfo.getReceivers();
        log.info("接收到群消息，发送者:{},接收用户数量:{}，内容:{}", sender.getId(), receivers.size(), recvInfo.getData());
        for (UserInfo receiver : receivers) {
            try {
                ChannelHandlerContext channelCtx = UserChannelCtxMap.getChannelCtx(receiver.getId(), receiver.getTerminal());
                if (!Objects.isNull(channelCtx)) {
                    // 推送消息到用户
                    SendInfo<Object> sendInfo = new SendInfo<>();
                    sendInfo.setCmd(CmdType.GROUP_MESSAGE.code());
                    sendInfo.setData(recvInfo.getData());
                    channelCtx.channel().writeAndFlush(sendInfo);
                    // 消息发送成功确认
                    sendResult(recvInfo, receiver, SendCode.SUCCESS);
                } else {
                    // 消息发送成功确认
                    sendResult(recvInfo, receiver, SendCode.NOT_FIND_CHANNEL);
                    log.error("未找到channel,发送者:{},接收id:{}，内容:{}", sender.getId(), receiver.getId(), recvInfo.getData());
                }
            } catch (Exception e) {
                // 消息发送失败确认
                sendResult(recvInfo, receiver, SendCode.UNKONW_ERROR);
                log.error("发送消息异常,发送者:{},接收id:{}，内容:{}", sender.getId(), receiver.getId(), recvInfo.getData());
            }
        }
    }

    private void sendResult(RecvInfo recvInfo, UserInfo receiver, SendCode sendCode) {
        if (recvInfo.getSendResult()) {
            SendResult<Object> result = new SendResult<>();
            result.setSender(recvInfo.getSender());
            result.setReceiver(receiver);
            result.setCode(sendCode.code());
            result.setData(recvInfo.getData());
            // 推送到结果队列
        }
    }
}
