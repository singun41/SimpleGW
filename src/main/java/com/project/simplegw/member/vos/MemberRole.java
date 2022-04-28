package com.project.simplegw.member.vos;

public enum MemberRole {
    USER,       // 일반 사용자
    MANAGER,    // 관리자    --> 공지사항, 자유 게시판, 자료실 CRUD
    LEADER,     // 팀장      --> 공지사항, 자유 게시판, 자료실 CRUD, 팀원 업무일지
    DIRECTOR,   // 임원      --> 공지사항, 자유 게시판, 자료실 CRUD, 팀원, 부서 업무일지
    MASTER,     // 최고 임원 --> 공지사항, 자유 게시판, 자료실 CRUD, 팀원, 부서 업무일지, 그 외 필요한 모든 권한(admin 권한 제외)
    ADMIN;      // 시스템 관리자   --> 시스템 전체 권한
}
