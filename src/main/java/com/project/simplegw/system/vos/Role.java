package com.project.simplegw.system.vos;

public enum Role {
    ADMIN("시스템 관리자"),   // 시스템 전체 권한
    USER("일반 유저"), MANAGER("중간 관리자"),
    LEADER("팀장"), DIRECTOR("임원"), MASTER("최고 임원");

    private String title;
    private Role(String title) {
        this.title = title;
    }

    public String getTitle() {
        return this.title;
    }
}
