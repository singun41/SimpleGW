package com.project.simplegw.schedule.vos;

public enum SearchOption {
    MINE("내 일정"),
    TEAM("팀원 일정"),
    AROUND_10_DAYS("전체(10일 이내)"),
    ALL("전체");

    private String title;
    private SearchOption(String title) {
        this.title = title;
    }
    public String getTitle() {
        return this.title;
    }
}
