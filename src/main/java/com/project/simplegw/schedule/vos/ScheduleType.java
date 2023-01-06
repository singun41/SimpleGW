package com.project.simplegw.schedule.vos;

public enum ScheduleType {
    PERSONAL("개인 일정"), COMPANY("회사 일정");

    private String title;
    private ScheduleType(String title) {
        this.title = title;
    }

    public String getTitle() {
        return this.title;
    }
}
