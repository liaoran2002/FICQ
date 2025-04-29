package org.lc.ficq.enums;

import lombok.AllArgsConstructor;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
public enum TerminalType {

    /**
     * web
     */
    WEB(0, "web"),
    /**
     * app
     */
    APP(1, "app"),
    /**
     * pc
     */
    PC(2, "pc"),
    /**
     * 未知
     */
    UNKNOW(-1, "未知");

    private final Integer code;

    private final String desc;


    public static TerminalType fromCode(Integer code) {
        for (TerminalType typeEnum : values()) {
            if (typeEnum.code.equals(code)) {
                return typeEnum;
            }
        }
        return null;
    }

    public static List<Integer> codes() {
        return Arrays.stream(values()).map(TerminalType::code).collect(Collectors.toList());
    }

    public Integer code() {
        return this.code;
    }

}
