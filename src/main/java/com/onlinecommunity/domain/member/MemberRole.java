package com.onlinecommunity.domain.member;

public enum MemberRole {
    ROLE_USER("ROLE_USER"),
    ROLE_ADMIN("ROLE_ADMIN");

    final private String name;

    public String getName() {
        return name;
    }

    MemberRole(String name) {
        this.name = name;
    }

    public static MemberRole getEnum(String name) {
        if(name.compareTo("ROLE_USER") == 0) {
            return ROLE_USER;
        } else if (name.compareTo("ROLE_ADMIN") == 0) {
            return ROLE_ADMIN;
        }

        throw new RuntimeException("권한 정보가 올바르지 않습니다. : " + name);
    }
}
