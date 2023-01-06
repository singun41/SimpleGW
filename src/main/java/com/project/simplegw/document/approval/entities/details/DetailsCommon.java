package com.project.simplegw.document.approval.entities.details;

import javax.persistence.FetchType;
// import javax.persistence.Inheritance;
// import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import com.project.simplegw.document.entities.Docs;
import com.project.simplegw.system.entities.EntitiesCommon;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString(callSuper = true, exclude = "docs")   // lazy loading 이기 때문에 제외하지 않으면 no session 에러가 난다.
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@MappedSuperclass
// @Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public abstract class DetailsCommon <T> extends EntitiesCommon {
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "docs_id", referencedColumnName = "id", nullable = false, updatable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    protected Docs docs;

    public abstract T bindDocs(Docs docs);   // 연관관계 매핑 메서드는 bind엔티티명 으로 작성한다.
}
