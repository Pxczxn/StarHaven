package com.xingqi.common.enums;

import lombok.Getter;

/**
 * 房间状态枚举
 */
@Getter
public enum RoomStatus {

    AVAILABLE("available", "空闲"),
    RESERVED("reserved", "已预订"),
    OCCUPIED("occupied", "已入住"),
    CLEANING("cleaning", "清洁中"),
    MAINTENANCE("maintenance", "维修中"),
    DISABLED("disabled", "停用");

    private final String code;
    private final String desc;

    RoomStatus(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static RoomStatus fromCode(String code) {
        for (RoomStatus status : values()) {
            if (status.code.equals(code)) {
                return status;
            }
        }
        throw new IllegalArgumentException("未知的房间状态: " + code);
    }
}
