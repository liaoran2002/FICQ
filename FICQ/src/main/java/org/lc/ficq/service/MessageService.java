package org.lc.ficq.service;

import org.lc.ficq.model.GroupMessageModel;
import org.lc.ficq.model.PrivateMessageModel;
import org.lc.ficq.model.SystemMessage;

public interface MessageService {
    /**
     * 发送私聊消息
     */
    <T> void sendPrivateMessage(PrivateMessageModel<T> message);

    /**
     * 发送群组消息
     */
    <T> void sendGroupMessage(GroupMessageModel<T> message);

    /**
     * 发送系统消息
     */
    <T> void sendSystemMessage(SystemMessage<T> message);

    /**
     * 检查用户是否在线
     */
    boolean isOnline(Long userId);
}
