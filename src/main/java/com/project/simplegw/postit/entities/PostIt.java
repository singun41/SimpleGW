package com.project.simplegw.postit.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.project.simplegw.member.entities.Member;
import com.project.simplegw.system.entities.EntitiesCommon;
import com.project.simplegw.system.vos.Constants;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

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
@Table(name = "post_it", indexes = @Index(columnList = "member_id"))
public class PostIt extends EntitiesCommon {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "member_id", referencedColumnName = "id", nullable = false, updatable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Member member;

    @Column(name = "seq", nullable = false, updatable = false)
    private int seq;

    @Column(name = "title", nullable = true, updatable = true, columnDefinition = Constants.COLUMN_DEFINE_TITLE)
    private String title;

    @Column(name = "content", nullable = true, updatable = true, columnDefinition = Constants.COLUMN_DEFINE_CONTENT)
    private String content;



    public PostIt bindMember(Member member) {   // 연관관계 매핑 메서드는 bind엔티티명 으로 작성한다.
        this.member = member;
        return this;
    }

    public PostIt updateTitle(String title) {
        this.title = title;
        return this;
    }

    public PostIt updateContent(String content) {
        this.content = content;
        return this;
    }
}
