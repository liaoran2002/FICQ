package org.lc.ficq.model;

import lombok.Data;

@Data
public class SendInfo<T> {

    /**
     * 命令
     */
    private Integer cmd;

    /**
     * 推送消息体
     */
    private T data;

}
