package com.project.simplegw.code.vos;

public enum BasecodeType {
    JOB_TITLE("직위"), DAYOFF("휴가"), OVERTIME("연장 근무"),
    
    // 캘린더에서 사용하는 코드
    PERSONAL("개인 일정"), COMPANY("회사 일정");

    private String title;

    private BasecodeType() {};

    private BasecodeType(String title) {
        this.title = title;
    }
    public String getTitle() {
        return this.title;
    }
}
