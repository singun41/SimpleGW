package com.project.simplegw.approval.vos;

public enum ApproverStatus {
    /*
        결재자 상태 정보
        submitted: 등록(제출) - 등록자(제출자) only
        confirmed: 승인 - 결재라인 전용
        rejected: 반려 - 결재라인 전용
        checked: 확인 - 참조자 전용
        
        proceed: 진행중 - 현재 순번 결재자
        waiting: 대기 - 결재라인 전용 (아직 순번이 오지 않은 결재자들의 상태)
    */
    SUBMITTED, CONFIRMED("승인"), REJECTED("반려"), CHECKED, PROCEED, WAITING;

    private String title;

    private ApproverStatus() { };

    private ApproverStatus(String title) {
        this.title = title;
    }
    public String getTitle() {
        return this.title;
    }
}
