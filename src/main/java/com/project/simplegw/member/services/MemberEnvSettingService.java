package com.project.simplegw.member.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import com.project.simplegw.member.dtos.receive.DtorEnvSetting;
import com.project.simplegw.member.dtos.send.DtosEnvSetting;
import com.project.simplegw.member.entities.MemberEnvSetting;
import com.project.simplegw.member.helpers.MemberConverter;
import com.project.simplegw.member.repositories.MemberEnvSettingRepo;
import com.project.simplegw.system.security.LoginUser;
import com.project.simplegw.system.vos.Constants;
import com.project.simplegw.system.vos.ServiceMsg;
import com.project.simplegw.system.vos.ServiceResult;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional(rollbackFor = Exception.class, isolation = Isolation.READ_COMMITTED)
public class MemberEnvSettingService {
    private final MemberEnvSettingRepo repo;
    private final MemberConverter converter;

    @Autowired
    public MemberEnvSettingService(MemberEnvSettingRepo repo, MemberConverter converter) {
        this.repo = repo;
        this.converter = converter;
        log.info("Component '" + this.getClass().getName() + "' has been created.");
    }





    private MemberEnvSetting getEntity(LoginUser loginUser) {
        return repo.findByMemberId(loginUser.getMember().getId()).orElseGet(MemberEnvSetting::new);
    }


    @Cacheable(cacheManager = Constants.CACHE_MANAGER, cacheNames = Constants.CACHE_USER_ENVIRONMENT_SETTING, key = "#loginUser.getMember().getId()")
    public DtosEnvSetting getEnvSetting(LoginUser loginUser) {
        return converter.getDtosEnvSetting(getEntity(loginUser));
    }


    @CacheEvict(cacheManager = Constants.CACHE_MANAGER, cacheNames = Constants.CACHE_USER_ENVIRONMENT_SETTING, allEntries = false, key = "#loginUser.getMember().getId()")
    public ServiceMsg update(DtorEnvSetting dto, LoginUser loginUser) {
        try {
            MemberEnvSetting entity = getEntity(loginUser);

            if(entity.getId() == null) {
                log.warn("update target is not exists.");
                log.warn("parameters: {}, user: {}", dto.toString(), loginUser.getMember().getId());
                return new ServiceMsg().setResult(ServiceResult.FAILURE).setMsg("환경설정 저장 에러입니다. 관리자에게 문의하세요.");
            }

            repo.save( entity.update(dto) );
            return new ServiceMsg().setResult(ServiceResult.SUCCESS);

        } catch(Exception e) {
            e.printStackTrace();
            log.warn("update exception.");
            log.warn("parameters: {}, user: {}", dto.toString(), loginUser.getMember().getId());

            return new ServiceMsg().setResult(ServiceResult.FAILURE).setMsg("환경설정 저장 에러입니다. 관리자에게 문의하세요.");
        }
    }
}
