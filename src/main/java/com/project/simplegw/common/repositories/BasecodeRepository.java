package com.project.simplegw.common.repositories;

import java.util.List;
import java.util.Optional;

import com.project.simplegw.common.entities.Basecode;
import com.project.simplegw.common.vos.BasecodeType;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BasecodeRepository extends JpaRepository<Basecode, Long> {
    Optional<Basecode> findByTypeAndCode(BasecodeType type, String code);
    List<Basecode> findAllByTypeOrderBySeq(BasecodeType type);
    List<Basecode> findAllByTypeAndEnabledOrderBySeq(BasecodeType type, boolean enabled);
}