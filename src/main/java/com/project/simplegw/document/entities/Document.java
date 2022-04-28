package com.project.simplegw.document.entities;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.project.simplegw.common.vos.Constants;
import com.project.simplegw.document.vos.DocumentKind;
import com.project.simplegw.document.vos.DocumentType;
import com.project.simplegw.member.entities.MemberDetails;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import lombok.AllArgsConstructor;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
// @ToString(exclude = "memberDetails")   // lazy loading 이기 때문에 제외하지 않으면 no session 에러가 난다.
@Entity
@Table(name = "sgw_document", indexes = {
    @Index(name = "sgw_board_index_1", columnList = "type, kind, created_date"),   // 유형별, 종류별
    @Index(name = "sgw_board_index_2", columnList = "member_id, type, kind, created_date"),   // 멤버별 작성한 문서 종류별로 보기
    @Index(name = "sgw_board_index_3", columnList = "created_date, type, kind")   // 결재, 참조로 수신받은 문서 모두 보기 위해 sgw_approval_line table 과 join 할 때 사용
})
public class Document {
    @Id
    @Column(name = "id", nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false) // memberDetails를 참조만 하므로 CascadeType은 설정하지 않는다.
    @JoinColumn(name = "member_id", referencedColumnName = "id", nullable = false, updatable = false)
    private MemberDetails member;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", length = Constants.COLUMN_LENGTH_DOCU_TYPE, nullable = false, updatable = false)
    private DocumentType type;

    @Enumerated(EnumType.STRING)
    @Column(name = "kind", length = Constants.COLUMN_LENGTH_DOCU_KIND, nullable = false, updatable = false)
    private DocumentKind kind;

    @Column(name = "title", columnDefinition = Constants.COLUMN_DEFINE_TITLE, nullable = false)
    private String title;

    // 내용 컬럼을 별도로 분리한다. 하이버네이트가 엔티티를 가져올 때 전체를 가져오므로, 내용이 큰 컬럼은 분리해서 필요할 때 가져오도록 한다.
    // @Column(name = "content", columnDefinition = ConstantsSystem.COLUMN_DEFINE_CONTENT)
    // private String content;

    // 아래 3개 작성자 필드는 데이터 자체가 히스토리를 볼 수 있도록 하는 역할.
    // 예시로 팀, 직위는 시간에 따라 변경이 될 수 있음.
    // 그리고 이렇게 하면 memberDetails를 조회하는 쿼리를 실행하지 않아도 된다.
    @Column(name = "writer_name", columnDefinition = Constants.COLUMN_DEFINE_NAME, nullable = false, updatable = false)
    private String writerName;

    @Column(name = "writer_team", columnDefinition = Constants.COLUMN_DEFINE_TEAM, nullable = false, updatable = false)
    private String writerTeam;

    @Column(name = "writer_job_title", columnDefinition = Constants.COLUMN_DEFINE_JOB_TITLE, nullable = false, updatable = false)
    private String writerJobTitle;
    
    // 작성 날짜로 검색을 용이하게 하기 위해서 생성 날짜 및 시간은 컬럼을 분리한다.
    @Column(name = "created_date", columnDefinition = Constants.COLUMN_DEFINE_DATE, nullable = false, updatable = false)
    @CreationTimestamp
    private LocalDate createdDate;

    @Column(name = "created_time", columnDefinition = Constants.COLUMN_DEFINE_TIME, nullable = false, updatable = false)
    @CreationTimestamp // 저장시 자동으로 현재 시간을 등록해줌. updatable = false 이므로 document entity가 수정될 때에는 update 되지 않음.
    private LocalTime createdTime;

    @Column(name = "updated_datetime", columnDefinition = Constants.COLUMN_DEFINE_DATETIME)
    @UpdateTimestamp
    private LocalDateTime updatedDatetime;

    @Column(name = "registered", nullable = false)
    private boolean registered;
    
    
    public Document updateTitle(String title) {
        if(title != null && !title.isBlank()) {
            this.title = title;
        } else {
            this.title = "empty title";
        }
        return this;
    }
    public Document updateRegistered(boolean isRegistered) {
        this.registered = isRegistered;
        return this;
    }

    // 새 문서를 등록할 때 memberDetails가 있어야 하기 때문에
    public Document insertMemberDetails(MemberDetails memberDetails) {
        this.member = memberDetails;
        this.writerTeam = memberDetails.getTeam();
        this.writerName = memberDetails.getName();
        this.writerJobTitle = memberDetails.getJobTitle();
        return this;
    }
    // 임시저장 후 등록: registered false --> true
    public Document setRegistered() {
        this.registered = true;
        return this;
    }
}
