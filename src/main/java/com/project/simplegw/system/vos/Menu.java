package com.project.simplegw.system.vos;

public enum Menu {
    // 관리자 전용 메뉴는 여기에 작성하지 않는다. (Users, Auths, Codes, Forms)

    NOTICE("공지사항"), FREEBOARD("자유게시판"), SUGGESTION("제안게시판"), ARCHIVE("자료실"),
    MINUTES("회의록"),
    WORK_RECORD("업무일지"), WORK_RECORD_TEAM("부서 업무일지"), WORK_RECORD_LIST("업무일지 전체"),
    
    APPROVAL_SEARCH("결재문서 검색"),
    APPROVAL_RECEIVED("수신 문서"),
    APPROVAL_DEFAULT("기안서"), APPROVAL_COOPERATION("업무협조전"),
    APPROVAL_DAYOFF("휴가신청서"),

    SCHEDULE("일정"),
    EMPLOYEES("임직원 정보")
    ;

    private String title;
    private Menu(String title) {
        this.title = title;
    }

    public String getTitle() {
        return this.title;
    }
}
