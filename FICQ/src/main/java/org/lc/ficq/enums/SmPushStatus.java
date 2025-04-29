package org.lc.ficq.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum SmPushStatus {

    /**
     * 待发送
     */
    WAIT_SEND(1),
    /**
     * 发送中
     */
    SENDING(2),
    /**
     * 已取消
     */
    SENDED(3),
    /**
     * 已取消
     */
    CANCEL(4);

    private final Integer value;

}