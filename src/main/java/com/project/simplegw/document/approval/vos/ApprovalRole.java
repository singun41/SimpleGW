package com.project.simplegw.document.approval.vos;

public enum ApprovalRole {   // 결재라인 저장시 구분하기 위한 값
    APPROVER("결재"), REFERRER("참조");

    private String title;
    private ApprovalRole(String title) {
        this.title = title;
    }
    public String getTitle() {
        return this.title;
    }
}
