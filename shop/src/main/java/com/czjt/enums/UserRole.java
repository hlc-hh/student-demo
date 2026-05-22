package com.czjt.enums;

public enum UserRole {
    USER(0, "普通用户"),
    ADMIN(1, "管理员");

    private final int code;
    private final String desc;

    UserRole(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public int getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    public static String getDescByCode(int code) {
        for (UserRole role : values()) {
            if (role.code == code) {
                return role.desc;
            }
        }
        return "未知角色";
    }
}
