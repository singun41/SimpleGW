package com.project.simplegw.system.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.project.simplegw.system.entities.MenuAuthority;
import com.project.simplegw.system.vos.Menu;

@Repository
public interface MenuAuthorityRepo extends JpaRepository<MenuAuthority, Long> {
    List<MenuAuthority> findByMenu(Menu menu);
}
