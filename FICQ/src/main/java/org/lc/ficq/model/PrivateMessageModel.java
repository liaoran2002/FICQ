package org.lc.ficq.model;

import lombok.Data;
import org.lc.ficq.enums.TerminalType;

import java.util.List;


@Data
public class PrivateMessageModel<T> {

    /**
     * 发送方
     */
    private UserInfo sender;

    /**
     * 接收者id
     */
    private Long recvId;


    /**
     * 接收者终端类型,默认全部
     */
    private List<Integer> recvTerminals = TerminalType.codes();

    /**
     * 是否同步消息给自己的其他终端,默认true
     */
    private Boolean sendToSelf = true;

    /**
     * 是否需要回推发送结果,默认true
     */
    private Boolean sendResult = true;

    /**
     * 消息内容
     */
    private T data;


}
