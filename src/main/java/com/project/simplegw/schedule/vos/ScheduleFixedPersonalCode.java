package com.project.simplegw.schedule.vos;

public enum ScheduleFixedPersonalCode {
    /*
        코드 내에서 사용할 기본 지정 코드

        휴가신청서를 통해 스케줄에 등록되는 로직에서 사용.
        일정별 색상 설정에서 사용.
        메인화면 근무 현황 테이블에서 사용.
    */
    DAYOFF("100", "휴가"),
    HALF_AM("101", "반차(오전)"),
    HALF_PM("102", "반차(오후)"),

    OUT_ON_BUSINESS("200", "외근"),
    OUT_ON_BUSINESS_DIRECT("201", "직출/직퇴"),
    
    BUSINESS_TRIP("300", "출장"),

    EDUCATION("400", "교육")
    ;

    private String code;
    private String title;

    private ScheduleFixedPersonalCode(String code, String title) {
        this.code = code;
        this.title = title;
    }

    public String getCode() {
        return this.code;
    }

    public String getTitle() {
        return this.title;
    }
}
