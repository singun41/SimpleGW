package com.project.simplegw.member.entities;

import java.time.LocalDate;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.project.simplegw.member.dtos.admin.receive.DtorMemberUpdate;
import com.project.simplegw.member.dtos.receive.DtorProfile;
import com.project.simplegw.system.entities.EntitiesCommon;
import com.project.simplegw.system.vos.Constants;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@Builder
@ToString(callSuper = true, exclude = "member")   // lazy loading 일 때 제외하지 않으면 no session 에러가 난다.
@NoArgsConstructor(access = AccessLevel.PUBLIC)   // entity의 기본 생성자는 반드시 public or protected 이어야 한다.
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Entity
@Table(name = "member_details", indexes = @Index(columnList = "member_id"))
public class MemberDetails extends EntitiesCommon {

    // One to one 설정해서 memberDetails를 저장할 때 member 엔티티도 자동으로 저장하기 위해 cascade를 설정.... 했으나
    // 비즈니스 로직에서 필요할 때마다 이 엔티티를 호출하는데 거기서 계속 발생할 N+1 문제때문에 Many To One으로 변경한다.
    // 멤버를 신규 등록할 때 반드시 Member 엔티티를 먼저 저장한 뒤 바인딩 시키도록 로직을 구현한다면 문제는 없다.
    // @OneToOne(fetch = FetchType.EAGER, optional = false, cascade = CascadeType.ALL)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "member_id", referencedColumnName = "id", nullable = false, updatable = false, unique = true)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Member member;

    @Column(name = "team", nullable = false, updatable = true, columnDefinition = Constants.COLUMN_DEFINE_TEAM)
    private String team;

    @Column(name = "job_title", nullable = false, updatable = true, columnDefinition = Constants.COLUMN_DEFINE_JOB_TITLE)
    private String jobTitle;

    @Column(name = "name", nullable = false, updatable = true, columnDefinition = Constants.COLUMN_DEFINE_NAME)
    private String name;

    @Column(name = "name_eng", nullable = true, updatable = true, length = Constants.COLUMN_LENGTH_NAME)   // 영문이름이므로 nvarchar이 아니다. 길이만 설정한다.
    private String nameEng;

    @Column(name = "mobile", nullable = true, updatable = true, length = Constants.COLUMN_LENGTH_MOBILE_NO)
    private String mobile;

    @Column(name = "email", nullable = true, updatable = true, length = Constants.COLUMN_LENGTH_MAIL_ADDRESS)
    private String email;

    @Column(name = "email_use", nullable = true, updatable = true)
    private boolean emailUse;

    @Column(name = "tel", nullable = true, updatable = true, length = Constants.COLUMN_LENGTH_MOBILE_NO)   // 길이는 전화번호 공용으로 처리.
    private String tel;   // 내선

    @Column(name = "birthday", nullable = true, updatable = true, columnDefinition = Constants.COLUMN_DEFINE_DATE)
    private LocalDate birthday;

    @Column(name = "date_hire", nullable = true, updatable = true, columnDefinition = Constants.COLUMN_DEFINE_DATE)
    private LocalDate dateHire;

    @Column(name = "date_resign", nullable = true, updatable = true, columnDefinition = Constants.COLUMN_DEFINE_DATE)
    private LocalDate dateResign;

    @Column(name = "resigned", nullable = true, updatable = true)
    private boolean resigned;

    @Column(name = "created_datetime", nullable = false, updatable = false, columnDefinition = Constants.COLUMN_DEFINE_DATETIME)
    @CreationTimestamp
    private LocalDateTime createdDatetime;





    public MemberDetails bindMember(Member member) {   // 연관관계 매핑 메서드는 bind엔티티명 으로 작성한다.
        if(member != null)
            this.member = member;
        return this;
    }

    public MemberDetails updateDetails(DtorMemberUpdate dto) {   // 관리자가 수정할 때
        return updateTeam(dto.getTeam())
            .updateJobTitle(dto.getJobTitle())
            .updateName(dto.getName())
            .updateNameEng(dto.getNameEng())
            .updateBirthday(dto.getBirthday())
            .updateMobile(dto.getMobile())
            .updateEmail(dto.getEmail())
            .updateEmailUse(dto.isEmailUse())
            .updateTel(dto.getTel())
            .updateHireDate(dto.getDateHire())
            .updateResignDate(dto.getDateResign())
            .updateResigned(dto.isResigned());
    }

    public MemberDetails updateDetails(DtorProfile dto) {   // 사용자가 수정할 때
        return updateNameEng(dto.getNameEng())
            .updateMobile(dto.getMobile())
            .updateBirthday(dto.getBirthday());
    }


    
    private MemberDetails updateTeam(String team) {
        if(team != null && !team.isBlank() && team.length() <= Constants.COLUMN_LENGTH_TEAM)
            this.team = team;
        return this;
    }
    private MemberDetails updateJobTitle(String jobTitle) {
        if(jobTitle != null && !jobTitle.isBlank() && jobTitle.length() <= Constants.COLUMN_LENGTH_JOB_TITLE)
            this.jobTitle = jobTitle;
        return this;
    }
    private MemberDetails updateName(String name) {
        if(name != null && !name.isBlank() && name.length() <= Constants.COLUMN_LENGTH_NAME)
            this.name = name;
        return this;
    }
    private MemberDetails updateNameEng(String nameEng) {
        if(nameEng != null && !nameEng.isBlank() && nameEng.length() <= Constants.COLUMN_LENGTH_NAME)
            this.nameEng = nameEng;
        return this;
    }
    private MemberDetails updateBirthday(LocalDate date) {
        this.birthday = (date == null ? null : date);
        return this;
    }
    private MemberDetails updateMobile(String mobile) {
        if(mobile != null && !mobile.isBlank() && mobile.length() <= Constants.COLUMN_LENGTH_MOBILE_NO)
            this.mobile = mobile;
        return this;
    }
    private MemberDetails updateEmail(String email) {
        if(email != null && !email.isBlank() && email.length() <= Constants.COLUMN_LENGTH_MAIL_ADDRESS)
            this.email = email;
        return this;
    }
    private MemberDetails updateEmailUse(boolean use) {
        this.emailUse = use;
        return this;
    }
    private MemberDetails updateTel(String tel) {
        if(tel != null && !tel.isBlank() && tel.length() <= Constants.COLUMN_LENGTH_MOBILE_NO)
            this.tel = tel;
        return this;
    }
    private MemberDetails updateHireDate(LocalDate date) {
        this.dateHire = (date == null ? null : date);
        return this;
    }
    private MemberDetails updateResignDate(LocalDate date) {
        this.dateResign = (date == null ? null : date);
        return this;
    }
    private MemberDetails updateResigned(boolean resigned) {
        this.resigned = resigned;
        return this;
    }
}
