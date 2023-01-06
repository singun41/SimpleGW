package com.project.simplegw.system.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Index;
import javax.persistence.Table;

import com.project.simplegw.system.vos.AuthorityValue;
import com.project.simplegw.system.vos.Constants;
import com.project.simplegw.system.vos.Menu;
import com.project.simplegw.system.vos.Role;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
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
@Table(name = "menu_authority", indexes = @Index(columnList = "menu"))
public class MenuAuthority extends EntitiesCommon {

    @Column(name = "menu", nullable = true, updatable = false, length = Constants.COLUMN_LENGTH_MENU)
    @Enumerated(EnumType.STRING)
    private Menu menu;

    @Column(name = "role", nullable = false, updatable = false, length = Constants.COLUMN_LENGTH_ROLE)
    @Enumerated(EnumType.STRING)
    private Role role;

    @Column(name = "accessible", nullable = false, updatable = true)
    private boolean accessible;   // 특정 페이지나 페이지 그룹을 접근할 수 있는지 여부, 예시: 부서 업무일지나 업무일지 전체는 USER 권한은 접근하지 못하게 해야 하므로 false.

    @Column(name = "rwd_role", nullable = false, updatable = true, length = Constants.COLUMN_LENGTH_RWD)
    @Enumerated(EnumType.STRING)
    private AuthorityValue rwdRole;

    @Column(name = "rwd_other", nullable = false, updatable = true, length = Constants.COLUMN_LENGTH_RWD)
    @Enumerated(EnumType.STRING)
    private AuthorityValue rwdOther;





    public MenuAuthority updateAccessible(boolean accessible) {
        this.accessible = accessible;
        check();
        return this;
    }

    public MenuAuthority updateRwdRole(AuthorityValue auth) {
        if(auth != null)
            this.rwdRole = auth;
        check();
        return this;
    }

    public MenuAuthority updateRwdOther(AuthorityValue auth) {
        if(auth != null)
            this.rwdOther = auth;
        check();
        return this;
    }



    private void check() {   // 접근 권한이 없으면 모든 권한을 제거.
        if( ! this.accessible ) {
            this.rwdRole = AuthorityValue.NONE;
            this.rwdOther = AuthorityValue.NONE;
        }
    }
}
