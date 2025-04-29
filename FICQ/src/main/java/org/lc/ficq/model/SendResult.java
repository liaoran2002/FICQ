package org.lc.ficq.model;

import lombok.Data;

@Data
public class SendResult<T> {

    /**
     * 发送方
     */
    private UserInfo sender;

    /**
     * 接收方
     */
    private UserInfo receiver;

    /**
     * 发送状态编码 IMSendCode
     */
    private Integer code;

    /**
     * 消息内容
     */
    private T data;

}
