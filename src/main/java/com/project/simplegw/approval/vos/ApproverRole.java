package com.project.simplegw.approval.vos;

public enum ApproverRole {
    /*
        결재문서에서 사용
        submitter: 제출자(상신자)
        approver: 결재자(결재라인)
        referrer: 참조자
    */
    SUBMITTER("제출"), APPROVER("결재"), REFERRER("참조");

    private String title;

    private ApproverRole() { };

    private ApproverRole(String title) {
        this.title = title;
    }
    public String getTitle() {
        return this.title;
    }
}
