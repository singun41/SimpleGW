package com.project.simplegw.document.vos;

public enum DocumentKind {
    ALL("ALL"),

    // 일반문서
    NOTICE("공지사항"), FREEBOARD("자유게시판"), MEETING("회의록"), ARCHIVE("자료실"), SUGGESTION("제안 게시판"),
    

    // 결재문서
    DEFAULT_REPORT("기안서"), INCIDENT_REPORT("사고 보고서"), DAYOFF("휴가 신청서"), OVERTIME("연장 근무 신청서"), PURCHASE("물품 구매 신청서"), NAMECARD("명함 신청서");


    private String title;

    private DocumentKind() { }

    private DocumentKind(String title) {
        this.title = title;
    }
    public String getTitle() {
        return this.title;
    }
}
