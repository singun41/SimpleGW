package com.project.simplegw.member.entities;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.project.simplegw.common.vos.Constants;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import lombok.AllArgsConstructor;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
// @ToString(exclude = "member")
@Entity
@Table(name = "sgw_member_details", indexes = @Index(name = "sgw_member_details_index_1", columnList = "member_id"))
public class MemberDetails {
    @Id
    @Column(name = "id", nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // One to one 설정한 이유: member와 memberDetails는 1:1 관계이면서, memberDetails를 한 번 저장할 때 member가 자동으로 저장되게 하기 위해서. 그리고 그렇게 동작하게 하려고 cascade를 설정함.
    // One to one으로 cascade를 설정하지 않을 것이라면 Many To One 을 사용하자.
    // Member와 MemberDetails를 해시맵에 담아두고 사용하기 위해서 LAZY --> EAGER로 변경.
    @OneToOne(optional = false, fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "member_id", referencedColumnName = "id", nullable = false, updatable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Member member;

    @Column(name = "team", columnDefinition = Constants.COLUMN_DEFINE_TEAM, nullable = false)
    private String team;

    @Column(name = "job_title", columnDefinition = Constants.COLUMN_DEFINE_JOB_TITLE, nullable = false)
    private String jobTitle;

    @Column(name = "name", columnDefinition = Constants.COLUMN_DEFINE_NAME, nullable = false)
    private String name;

    @Column(name = "name_eng", length = Constants.COLUMN_LENGTH_NAME)   // 영문이름이므로 nvarchar이 아니다. 길이만 설정한다.
    private String nameEng;

    @Column(name = "mobile_no", length = Constants.COLUMN_LENGTH_MOBILE_NO)
    private String mobileNo;

    @Column(name = "mail_address", length = Constants.COLUMN_LENGTH_MAIL_ADDRESS)
    private String mailAddress;

    @Column(name = "mail_use")
    private boolean mailUse;

    @Column(name = "tel", length = Constants.COLUMN_LENGTH_MOBILE_NO)   // 길이는 전화번호 공용으로 처리.
    private String tel;   // 내선

    @Column(name = "birthday", columnDefinition = Constants.COLUMN_DEFINE_DATE)
    private LocalDate birthday;

    @Column(name = "date_hire", columnDefinition = Constants.COLUMN_DEFINE_DATE)
    private LocalDate dateHire;

    @Column(name = "date_retire", columnDefinition = Constants.COLUMN_DEFINE_DATE)
    private LocalDate dateRetire;

    @Column(name = "is_retired")
    private boolean retired;

    @Column(name = "created_datetime", nullable = false, updatable = false, columnDefinition = Constants.COLUMN_DEFINE_DATETIME)
    @CreationTimestamp
    private LocalDateTime createdDatetime;

    
    public static boolean checkMobileNo(String mobileNo) {
        String mobileNoPattern = "\\d{3}-\\d{3,4}-\\d{4}";
        Pattern pattern = Pattern.compile(mobileNoPattern);
        Matcher matcher = pattern.matcher(mobileNo);
        return matcher.matches();
    }
    public static boolean checkMailAddress(String mailAddress) {
        // \w+@\w+\.+(\.\w+)?  // \w = [a-zA-Z0-9] , + = 한 개 이상 , @ = @ , \. = . , * = 없거나 또는 한 개 이상
        String mailAddressPattern = "\\w+@\\w+\\.\\w+(\\.\\w+)*";
        Pattern pattern = Pattern.compile(mailAddressPattern);
        Matcher matcher = pattern.matcher(mailAddress);
        return matcher.matches();
    }
    public static boolean checkTel(String tel) {
        String telPattern = "\\d{1,4}";   // 1 ~ 4자리 번호
        Pattern pattern = Pattern.compile(telPattern);
        Matcher matcher = pattern.matcher(tel);
        return matcher.matches() || checkMobileNo(tel);   // 두 양식 모두 허용
    }

    public MemberDetails updateTeam(String team) {
        if(team != null && !team.isBlank() && team.length() <= Constants.COLUMN_LENGTH_TEAM) {
            this.team = team;
        }
        return this;
    }
    public MemberDetails updateJobTitle(String jobTitle) {
        if(jobTitle != null && !jobTitle.isBlank() && jobTitle.length() <= Constants.COLUMN_LENGTH_JOB_TITLE) {
            this.jobTitle = jobTitle;
        }
        return this;
    }
    public MemberDetails updateName(String name) {
        if(name != null && !name.isBlank() && name.length() <= Constants.COLUMN_LENGTH_NAME) {
            this.name = name;
        }
        return this;
    }
    public MemberDetails updateNameEng(String nameEng) {
        if(nameEng != null && !nameEng.isBlank() && nameEng.length() <= Constants.COLUMN_LENGTH_NAME) {
            this.nameEng = nameEng;
        }
        return this;
    }
    public MemberDetails updateBirthday(LocalDate birthday) {
        this.birthday = birthday;
        return this;
    }
    public MemberDetails updateMobileNo(String mobileNo) {
        if(mobileNo != null && !mobileNo.isBlank() && mobileNo.length() <= Constants.COLUMN_LENGTH_MOBILE_NO && checkMobileNo(mobileNo)) {
            this.mobileNo = mobileNo;
        }
        return this;
    }
    public MemberDetails updateMailAddress(String mailAddress) {
        if(mailAddress != null && !mailAddress.isBlank() && mailAddress.length() <= Constants.COLUMN_LENGTH_MAIL_ADDRESS && checkMailAddress(mailAddress)) {
            this.mailAddress = mailAddress;
        }
        return this;
    }
    public MemberDetails updateMailUse(boolean use) {
        this.mailUse = use;
        return this;
    }
    public MemberDetails updateTel(String tel) {
        if(tel != null && !tel.isBlank() && tel.length() <= Constants.COLUMN_LENGTH_MOBILE_NO && checkTel(tel)) {
            this.tel = tel;
        }
        return this;
    }
    public MemberDetails updateHireDate(LocalDate date) {
        this.dateHire = date;
        return this;
    }
    public MemberDetails updateRetireDate(LocalDate date) {
        this.dateRetire = date;
        return this;
    }
    public MemberDetails updateRetired(boolean retired) {
        this.retired = retired;
        return this;
    }
    public MemberDetails updateMember(Member member) {
        if(member != null) {
            this.member = member;
        }
        return this;
    }
}
