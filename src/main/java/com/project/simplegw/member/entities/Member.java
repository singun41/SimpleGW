package com.project.simplegw.member.entities;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.project.simplegw.common.vos.Constants;
import com.project.simplegw.member.vos.MemberRole;
import com.project.simplegw.system.security.PwEncoder;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// builder를 사용할 때는 deleteAll을 사용하기 위해서 NoArgs와 AllArgs 모두 필요.
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
// @ToString(exclude = "password")
@Entity
@Table(name = "sgw_member", indexes = @Index(name = "member_index_1", columnList = "user_id"))
public class Member {
    @Id
    @Column(name = "id", nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false, length = Constants.COLUMN_LENGTH_USER_ID, unique = true)
    private String userId;   // Repository에서 findById 메서드를 사용할 때 헷갈릴 수 있어 userId로 작성.

    @Column(name = "password", nullable = false, length = Constants.COLUMN_LENGTH_PW)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false, length = Constants.COLUMN_LENGTH_ROLE)
    private MemberRole role;

    @Column(name = "enabled", nullable = false)
    private boolean enabled;

    @Transient
    private int failureCount;

    @Transient
    public static final PwEncoder pwEncoder = new PwEncoder();


    // ----- ----- ----- ----- ----- password update ----- ----- ----- ----- ----- //
    public static boolean checkPwPolicy(String pw) {
        String passwordPolicy = "((?=.*[a-z])(?=.*[0-9])(?=.*[^a-zA-Z0-9]).{" + Constants.PW_UPDATE_AT_LEAST_LENGTH + ",})";
        Pattern pattern = Pattern.compile(passwordPolicy);
        Matcher matcher = pattern.matcher(pw);
        return matcher.matches();
    }
    public static boolean checkOriginalPw(String encodedOriginalPw, String enteredOriginalPw) {
        return pwEncoder.matches(enteredOriginalPw, encodedOriginalPw);
    }
    public boolean updatePassword(String enteredOriginalPw, String newPw) {
        if(newPw == null || newPw.isBlank()) {
            return false;
        }
        if(!checkPwPolicy(newPw)) {
            return false;
        }
        if(!checkOriginalPw(this.getPassword(), enteredOriginalPw)) {
            return false;
        }
        this.password = pwEncoder.encode(newPw);
        return true;
    }
    public Member updatePasswordForAdmin(String newPw) {
        if(newPw != null && !newPw.isBlank()) {
            this.password = pwEncoder.encode(newPw);
        }
        return this;
    }
    // ----- ----- ----- ----- ----- password update ----- ----- ----- ----- ----- //


    // ----- ----- ----- ----- ----- role update ----- ----- ----- ----- ----- //
    public Member updateRole(MemberRole role) {
        if(role != null)
            this.role = role;
        return this;
    }
    // ----- ----- ----- ----- ----- role update ----- ----- ----- ----- ----- //


    // ----- ----- ----- ----- ----- enabled update ----- ----- ----- ----- ----- //
    public Member updateEnabled(boolean enabled) {
        this.enabled = enabled;
        return this;
    }
    // ----- ----- ----- ----- ----- enabled update ----- ----- ----- ----- ----- //


    // 패스워드 실패시 failure count 증가
    public Member increaseFailureCount() {
        this.failureCount++;
        if(this.failureCount >= 5) {   // 실패 카운트 5번이 되면 계정을 잠금.
            this.enabled = false;
        }
        return this;
    }
    public Member clearFailureCount() {
        this.failureCount = 0;
        return this;
    }
}
