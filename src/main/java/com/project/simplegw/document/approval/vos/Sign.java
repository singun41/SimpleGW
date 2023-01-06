package com.project.simplegw.document.approval.vos;

public enum Sign {
    // SUBMITTED("제출"),
    PROCEED("진행"), CONFIRMED("승인"), REJECTED("반려");

    private String title;
    private Sign(String title) {
        this.title = title;
    }

    public String getTitle() {
        return this.title;
    }
}
