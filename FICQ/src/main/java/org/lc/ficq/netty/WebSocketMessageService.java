package org.lc.ficq.netty;

import cn.hutool.core.collection.CollUtil;
import io.netty.channel.ChannelHandlerContext;
import org.lc.ficq.enums.CmdType;
import org.lc.ficq.enums.ListenerType;
import org.lc.ficq.enums.SendCode;
import org.lc.ficq.enums.TerminalType;
import org.lc.ficq.listener.MessageListenerMulticaster;
import org.lc.ficq.model.*;
import org.lc.ficq.service.MessageService;
import org.lc.ficq.util.OnlineSessionUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class WebSocketMessageService implements MessageService {

    private final MessageListenerMulticaster listenerMulticaster;

    @Autowired
    public WebSocketMessageService(MessageListenerMulticaster listenerMulticaster) {
        this.listenerMulticaster = listenerMulticaster;
    }

    @Override
    public <T> void sendPrivateMessage(PrivateMessageModel<T> message) {
        // 发送给接收方
        if (message.getRecvId() != null) {
            sendToUser(message.getRecvId(), message.getRecvTerminals(),
                      message.getSender(), message.getData(),
                      CmdType.PRIVATE_MESSAGE, ListenerType.PRIVATE_MESSAGE);
        }
        // 发送给自己的其他终端
        if (message.getSendToSelf()) {
            sendToSelfTerminals(message.getSender(), message.getData(), CmdType.PRIVATE_MESSAGE);
        }
    }

    @Override
    public <T> void sendGroupMessage(GroupMessageModel<T> message) {
        if (CollUtil.isEmpty(message.getRecvIds())) {
            return;
        }

        // 发送给群成员
        message.getRecvIds().forEach(userId -> 
            sendToUser(userId, message.getRecvTerminals(), 
                      message.getSender(), message.getData(), 
                      CmdType.GROUP_MESSAGE, ListenerType.GROUP_MESSAGE)
        );

        // 发送给自己的其他终端
        if (message.getSendToSelf()) {
            sendToSelfTerminals(message.getSender(), message.getData(), CmdType.GROUP_MESSAGE);
        }
    }

    @Override
    public <T> void sendSystemMessage(SystemMessage<T> message) {
        if (CollUtil.isEmpty(message.getRecvIds())) {
            return;
        }
        // 发送给指定用户
        message.getRecvIds().forEach(userId -> 
            sendToUser(userId, message.getRecvTerminals(), 
                      null, message.getData(), 
                      CmdType.SYSTEM_MESSAGE, ListenerType.SYSTEM_MESSAGE)
        );
    }

    @Override
    public boolean isOnline(Long userId) {
        return OnlineSessionUtil.isUserOnline(userId);
    }

    /**
     * 发送消息给指定用户
     */
    private <T> void sendToUser(Long userId, List<Integer> terminals, 
                              UserInfo sender, T data, 
                              CmdType cmdType, ListenerType listenerType) {
        terminals.forEach(terminal -> {
            ChannelHandlerContext ctx = OnlineSessionUtil.getSession(userId, terminal);
            if (ctx != null && ctx.channel().isActive()) {
                // 在线发送
                sendViaWebSocket(ctx, cmdType, data);
            } else {
                // 离线处理
                handleOffline(sender, new UserInfo(userId, terminal), data, listenerType);
            }
        });
    }

    /**
     * 通过WebSocket发送消息
     */
    private <T> void sendViaWebSocket(ChannelHandlerContext ctx, CmdType cmdType, T data) {
        try {
            SendInfo sendInfo=new SendInfo();
            sendInfo.setCmd(cmdType.code());
            sendInfo.setData(data);
            ctx.channel().writeAndFlush(sendInfo);
        } catch (Exception e) {
            log.error("WebSocket消息发送失败", e);
        }
    }

    /**
     * 处理离线消息
     */
    private <T> void handleOffline(UserInfo sender, UserInfo receiver, T data, ListenerType listenerType) {
        SendResult<T> result = new SendResult<>();
        result.setSender(sender);
        result.setReceiver(receiver);
        result.setCode(SendCode.NOT_ONLINE.code());
        result.setData(data);
        listenerMulticaster.multicast(listenerType, Collections.singletonList(result));
    }

    /**
     * 发送消息给自己的其他终端
     */
    private <T> void sendToSelfTerminals(UserInfo sender, T data, CmdType cmdType) {
        TerminalType.codes().stream()
                .filter(terminal -> !terminal.equals(sender.getTerminal()))
                .forEach(terminal -> {
                    ChannelHandlerContext ctx = OnlineSessionUtil.getSession(sender.getId(), terminal);
                    if (ctx != null && ctx.channel().isActive()) {
                        sendViaWebSocket(ctx, cmdType, data);
                    }
                });
    }

    /**
     * 获取在线用户列表
     */
    public List<Long> getOnlineUser(List<Long> userIds) {
        return userIds.stream()
                .filter(OnlineSessionUtil::isUserOnline)
                .collect(Collectors.toList());
    }

    /**
     * 获取用户的在线终端
     */
    public Map<Long, List<TerminalType>> getOnlineTerminal(List<Long> userIds) {
        if (CollUtil.isEmpty(userIds)) {
            return Collections.emptyMap();
        }
        return userIds.stream()
                .filter(OnlineSessionUtil::isUserOnline)
                .collect(Collectors.toMap(
                    userId -> userId,
                    userId -> TerminalType.codes().stream()
                            .filter(terminal -> OnlineSessionUtil.getSession(userId, terminal) != null)
                            .map(TerminalType::fromCode)
                            .collect(Collectors.toList())
                ));
    }
}