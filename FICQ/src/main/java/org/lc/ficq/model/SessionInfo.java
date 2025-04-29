package org.lc.ficq.model;

import lombok.Data;

@Data
public class SessionInfo {
    /**
     * 用户id
     */
    private Long userId;

    /**
     * 终端类型
     */
    private Integer terminal;

}
