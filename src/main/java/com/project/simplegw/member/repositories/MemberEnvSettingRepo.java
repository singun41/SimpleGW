package com.project.simplegw.member.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.project.simplegw.member.entities.MemberEnvSetting;

@Repository
public interface MemberEnvSettingRepo extends JpaRepository<MemberEnvSetting, Long> {
    Optional<MemberEnvSetting> findByMemberId(Long memberId);
}
