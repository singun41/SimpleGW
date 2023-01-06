package com.project.simplegw.system.vos;

public enum ResponseMsg {
    // 컨트롤러에서 Front에 응답할 때 사용할 메시지 정의 클래스, DtoResponse 클래스의 msg에 바인딩 됨.

    INSERTED("등록하였습니다."),
    SAVED("저장하였습니다."),
    UPDATED("수정하였습니다."),
    DELETED("삭제하였습니다."),

    UNAUTHORIZED("권한이 없습니다."),

    CONFIRMED("승인하였습니다."),
    REJECTED("반려하였습니다.")
    ;

    private String title;
    private ResponseMsg(String title) {
        this.title = title;
    }

    public String getTitle() {
        return this.title;
    }
}
