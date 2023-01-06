package com.project.simplegw.document.entities;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Index;
import javax.persistence.Table;

import com.project.simplegw.document.vos.DocsType;
import com.project.simplegw.member.data.MemberData;
import com.project.simplegw.system.entities.EntitiesCommon;
import com.project.simplegw.system.vos.Constants;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import lombok.AllArgsConstructor;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@Builder
@ToString(callSuper = true)
@NoArgsConstructor(access = AccessLevel.PUBLIC)   // entity의 기본 생성자는 반드시 public or protected 이어야 한다.
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Entity
@Table(name = "docs" , indexes = {
    @Index(columnList = "created_date, type"),   // 테스트 결과 컬럼의 순서를 옵티마이저가 변경하는 경우가 있다. 두 개 모두 설정.
    @Index(columnList = "type, created_date"),
    @Index(columnList = "writer_id, created_date")   // 완결된 결재문서 찾을 때 nativeQuery에서 필요한 인덱스
})
public class Docs extends EntitiesCommon {

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, updatable = false, length = Constants.COLUMN_LENGTH_DOCU_TYPE)
    private DocsType type;

    @Column(name = "title", nullable = false, updatable = true, columnDefinition = Constants.COLUMN_DEFINE_TITLE)
    private String title;

    @Column(name = "writer_id", nullable = false, updatable = false)
    private Long writerId;   // 작성자를 찾기 위해서 Member 클래스를 바인딩해두면 소유자 검증할 때마다 Member를 찾는 쿼리가 발생함. id값으로만 해두면 equals 한 번에 처리가 가능함. 성능이점이 있음.

    @Column(name = "writer_team", nullable = false, updatable = false, columnDefinition = Constants.COLUMN_DEFINE_TEAM)
    private String writerTeam;

    @Column(name = "writer_job_title", nullable = false, updatable = false, columnDefinition = Constants.COLUMN_DEFINE_JOB_TITLE)
    private String writerJobTitle;

    @Column(name = "writer_name", nullable = false, updatable = false, columnDefinition = Constants.COLUMN_DEFINE_NAME)
    private String writerName;
    
    // 작성 날짜로 검색을 용이하게 하기 위해서 생성 날짜 및 시간은 컬럼을 분리한다.
    @Column(name = "created_date", nullable = false, updatable = false, columnDefinition = Constants.COLUMN_DEFINE_DATE)
    @CreationTimestamp
    private LocalDate createdDate;

    @Column(name = "created_time", nullable = false, updatable = false, columnDefinition = Constants.COLUMN_DEFINE_TIME)
    @CreationTimestamp // 저장시 자동으로 현재 시간을 등록해줌. updatable = false 이므로 document entity가 수정될 때에는 update 되지 않음.
    private LocalTime createdTime;

    @Column(name = "updated_datetime", nullable = true, updatable = true, columnDefinition = Constants.COLUMN_DEFINE_DATETIME)
    @UpdateTimestamp
    private LocalDateTime updatedDatetime;

    



    public Docs updateTitle(String title) {
        if(title != null && !title.isBlank()) {
            this.title = title;
        } else {
            this.title = "empty title";
        }
        return this;
    }
    
    public Docs setMemberData(MemberData memberData) {
        this.writerId = memberData.getId();
        this.writerTeam = memberData.getTeam();
        this.writerJobTitle = memberData.getJobTitle();
        this.writerName = memberData.getName();
        return this;
    }
}
