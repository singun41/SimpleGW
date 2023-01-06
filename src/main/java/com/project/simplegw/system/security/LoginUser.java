package com.project.simplegw.system.security;

import com.project.simplegw.member.entities.Member;

import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;

public class LoginUser extends User {
    private static final long serialVersionUID = 1L;
    private Member member;

    public LoginUser(Member member) {
        super(member.getUserId(), member.getPassword(), member.isEnabled(), true, true, true, AuthorityUtils.createAuthorityList(member.getRole().name()));
        this.member = member;
    }

    public Member getMember() {
        return member;
    }
}