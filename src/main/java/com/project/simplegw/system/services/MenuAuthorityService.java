package com.project.simplegw.system.services;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.annotation.Isolation;

import com.project.simplegw.system.dtos.receive.DtorMenuAuthority;
import com.project.simplegw.system.dtos.send.DtosMenuAuthority;
import com.project.simplegw.system.entities.MenuAuthority;
import com.project.simplegw.system.helpers.MenuAuthorityConverter;
import com.project.simplegw.system.repositories.MenuAuthorityRepo;
import com.project.simplegw.system.security.LoginUser;
import com.project.simplegw.system.vos.AuthorityValue;
import com.project.simplegw.system.vos.Menu;
import com.project.simplegw.system.vos.Role;
import com.project.simplegw.system.vos.ServiceMsg;
import com.project.simplegw.system.vos.ServiceResult;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional(rollbackFor = Exception.class, isolation = Isolation.READ_COMMITTED)
public class MenuAuthorityService {
    private final MenuAuthorityRepo repo;
    private final MenuAuthorityConverter converter;

    private final Queue<DtosMenuAuthority> authorities = new ConcurrentLinkedQueue<>();

    @Autowired
    public MenuAuthorityService(MenuAuthorityRepo repo, MenuAuthorityConverter converter) {
        this.repo = repo;
        this.converter = converter;
        log.info("Component '" + this.getClass().getName() + "' has been created.");

        defaultSetting();
    }


    private void defaultSetting() {
        List<MenuAuthority> list = getAll();

        if(list.size() == 0) {
            log.info("The authorities not exists. set default configurations.");

            Arrays.asList(Role.values()).forEach(role -> {
                MenuAuthority notice = MenuAuthority.builder().menu(Menu.NOTICE).role(role).build();
                MenuAuthority freeboard = MenuAuthority.builder().menu(Menu.FREEBOARD).role(role).build();
                MenuAuthority suggestion = MenuAuthority.builder().menu(Menu.SUGGESTION).role(role).build();
                MenuAuthority archive = MenuAuthority.builder().menu(Menu.ARCHIVE).role(role).build();
                
                MenuAuthority workRecord = MenuAuthority.builder().menu(Menu.WORK_RECORD).role(role).build();
                MenuAuthority workRecordTeam = MenuAuthority.builder().menu(Menu.WORK_RECORD_TEAM).role(role).build();
                MenuAuthority workRecordList = MenuAuthority.builder().menu(Menu.WORK_RECORD_LIST).role(role).build();
                MenuAuthority minutes = MenuAuthority.builder().menu(Menu.MINUTES).role(role).build();
                
                MenuAuthority approvalSearch = MenuAuthority.builder().menu(Menu.APPROVAL_SEARCH).role(role).build();
                MenuAuthority approvalDefault = MenuAuthority.builder().menu(Menu.APPROVAL_DEFAULT).role(role).build();
                MenuAuthority approvalCooperation = MenuAuthority.builder().menu(Menu.APPROVAL_COOPERATION).role(role).build();
                MenuAuthority approvalDayoff = MenuAuthority.builder().menu(Menu.APPROVAL_DAYOFF).role(role).build();

                MenuAuthority schedule = MenuAuthority.builder().menu(Menu.SCHEDULE).role(role).build();
                MenuAuthority employees = MenuAuthority.builder().menu(Menu.EMPLOYEES).role(role).build();


                switch(role) {
                    case ADMIN -> {
                        notice.updateAccessible(true).updateRwdRole(AuthorityValue.RWD).updateRwdOther(AuthorityValue.RWD);
                        freeboard.updateAccessible(true).updateRwdRole(AuthorityValue.RWD).updateRwdOther(AuthorityValue.RD);
                        suggestion.updateAccessible(true).updateRwdRole(AuthorityValue.RWD).updateRwdOther(AuthorityValue.RD);
                        archive.updateAccessible(true).updateRwdRole(AuthorityValue.RWD).updateRwdOther(AuthorityValue.RWD);

                        workRecord.updateAccessible(true).updateRwdRole(AuthorityValue.RW).updateRwdOther(AuthorityValue.NONE);
                        workRecordTeam.updateAccessible(true).updateRwdRole(AuthorityValue.R).updateRwdOther(AuthorityValue.R);
                        workRecordList.updateAccessible(true).updateRwdRole(AuthorityValue.R).updateRwdOther(AuthorityValue.R);
                        minutes.updateAccessible(true).updateRwdRole(AuthorityValue.RWD).updateRwdOther(AuthorityValue.R);

                        approvalSearch.updateAccessible(true).updateRwdRole(AuthorityValue.R).updateRwdOther(AuthorityValue.R);
                        approvalDefault.updateAccessible(true).updateRwdRole(AuthorityValue.RWD).updateRwdOther(AuthorityValue.R);
                        approvalCooperation.updateAccessible(true).updateRwdRole(AuthorityValue.RWD).updateRwdOther(AuthorityValue.R);
                        approvalDayoff.updateAccessible(true).updateRwdRole(AuthorityValue.RWD).updateRwdOther(AuthorityValue.R);

                        schedule.updateAccessible(true).updateRwdRole(AuthorityValue.RWD).updateRwdOther(AuthorityValue.R);
                        employees.updateAccessible(true).updateRwdRole(AuthorityValue.R).updateRwdOther(AuthorityValue.R);
                    }
                    


                    case USER -> {
                        notice.updateAccessible(true).updateRwdRole(AuthorityValue.R).updateRwdOther(AuthorityValue.R);
                        freeboard.updateAccessible(true).updateRwdRole(AuthorityValue.RWD).updateRwdOther(AuthorityValue.R);
                        suggestion.updateAccessible(true).updateRwdRole(AuthorityValue.RWD).updateRwdOther(AuthorityValue.R);
                        archive.updateAccessible(true).updateRwdRole(AuthorityValue.R).updateRwdOther(AuthorityValue.R);

                        workRecord.updateAccessible(true).updateRwdRole(AuthorityValue.RW).updateRwdOther(AuthorityValue.NONE);
                        workRecordTeam.updateAccessible(false).updateRwdRole(AuthorityValue.NONE).updateRwdOther(AuthorityValue.NONE);
                        workRecordList.updateAccessible(false).updateRwdRole(AuthorityValue.NONE).updateRwdOther(AuthorityValue.NONE);
                        minutes.updateAccessible(true).updateRwdRole(AuthorityValue.RWD).updateRwdOther(AuthorityValue.R);

                        approvalSearch.updateAccessible(false).updateRwdRole(AuthorityValue.NONE).updateRwdOther(AuthorityValue.NONE);
                        approvalDefault.updateAccessible(true).updateRwdRole(AuthorityValue.RWD).updateRwdOther(AuthorityValue.R);
                        approvalCooperation.updateAccessible(true).updateRwdRole(AuthorityValue.RWD).updateRwdOther(AuthorityValue.R);
                        approvalDayoff.updateAccessible(true).updateRwdRole(AuthorityValue.RWD).updateRwdOther(AuthorityValue.R);

                        schedule.updateAccessible(true).updateRwdRole(AuthorityValue.RWD).updateRwdOther(AuthorityValue.R);
                        employees.updateAccessible(true).updateRwdRole(AuthorityValue.R).updateRwdOther(AuthorityValue.R);
                    }
                    


                    case MANAGER -> {
                        notice.updateAccessible(true).updateRwdRole(AuthorityValue.RWD).updateRwdOther(AuthorityValue.RWD);
                        freeboard.updateAccessible(true).updateRwdRole(AuthorityValue.RWD).updateRwdOther(AuthorityValue.R);
                        suggestion.updateAccessible(true).updateRwdRole(AuthorityValue.RWD).updateRwdOther(AuthorityValue.R);
                        archive.updateAccessible(true).updateRwdRole(AuthorityValue.RWD).updateRwdOther(AuthorityValue.RWD);

                        workRecord.updateAccessible(true).updateRwdRole(AuthorityValue.RW).updateRwdOther(AuthorityValue.NONE);
                        workRecordTeam.updateAccessible(false).updateRwdRole(AuthorityValue.NONE).updateRwdOther(AuthorityValue.NONE);
                        workRecordList.updateAccessible(false).updateRwdRole(AuthorityValue.NONE).updateRwdOther(AuthorityValue.NONE);
                        minutes.updateAccessible(true).updateRwdRole(AuthorityValue.RWD).updateRwdOther(AuthorityValue.R);

                        approvalSearch.updateAccessible(true).updateRwdRole(AuthorityValue.R).updateRwdOther(AuthorityValue.R);
                        approvalDefault.updateAccessible(true).updateRwdRole(AuthorityValue.RWD).updateRwdOther(AuthorityValue.R);
                        approvalCooperation.updateAccessible(true).updateRwdRole(AuthorityValue.RWD).updateRwdOther(AuthorityValue.R);
                        approvalDayoff.updateAccessible(true).updateRwdRole(AuthorityValue.RWD).updateRwdOther(AuthorityValue.R);

                        schedule.updateAccessible(true).updateRwdRole(AuthorityValue.RWD).updateRwdOther(AuthorityValue.R);
                        employees.updateAccessible(true).updateRwdRole(AuthorityValue.R).updateRwdOther(AuthorityValue.R);
                    }
                


                    case LEADER -> {
                        notice.updateAccessible(true).updateRwdRole(AuthorityValue.RWD).updateRwdOther(AuthorityValue.R);
                        freeboard.updateAccessible(true).updateRwdRole(AuthorityValue.RWD).updateRwdOther(AuthorityValue.R);
                        suggestion.updateAccessible(true).updateRwdRole(AuthorityValue.RWD).updateRwdOther(AuthorityValue.R);
                        archive.updateAccessible(true).updateRwdRole(AuthorityValue.RWD).updateRwdOther(AuthorityValue.R);

                        workRecord.updateAccessible(true).updateRwdRole(AuthorityValue.RW).updateRwdOther(AuthorityValue.NONE);
                        workRecordTeam.updateAccessible(true).updateRwdRole(AuthorityValue.R).updateRwdOther(AuthorityValue.R);
                        workRecordList.updateAccessible(false).updateRwdRole(AuthorityValue.NONE).updateRwdOther(AuthorityValue.NONE);
                        minutes.updateAccessible(true).updateRwdRole(AuthorityValue.RWD).updateRwdOther(AuthorityValue.R);

                        approvalSearch.updateAccessible(true).updateRwdRole(AuthorityValue.R).updateRwdOther(AuthorityValue.R);
                        approvalDefault.updateAccessible(true).updateRwdRole(AuthorityValue.RWD).updateRwdOther(AuthorityValue.R);
                        approvalCooperation.updateAccessible(true).updateRwdRole(AuthorityValue.RWD).updateRwdOther(AuthorityValue.R);
                        approvalDayoff.updateAccessible(true).updateRwdRole(AuthorityValue.RWD).updateRwdOther(AuthorityValue.R);

                        schedule.updateAccessible(true).updateRwdRole(AuthorityValue.RWD).updateRwdOther(AuthorityValue.R);
                        employees.updateAccessible(true).updateRwdRole(AuthorityValue.R).updateRwdOther(AuthorityValue.R);
                    }
                    


                    case DIRECTOR -> {
                        notice.updateAccessible(true).updateRwdRole(AuthorityValue.R).updateRwdOther(AuthorityValue.R);
                        freeboard.updateAccessible(true).updateRwdRole(AuthorityValue.RWD).updateRwdOther(AuthorityValue.R);
                        suggestion.updateAccessible(true).updateRwdRole(AuthorityValue.RWD).updateRwdOther(AuthorityValue.R);
                        archive.updateAccessible(true).updateRwdRole(AuthorityValue.R).updateRwdOther(AuthorityValue.R);

                        workRecord.updateAccessible(true).updateRwdRole(AuthorityValue.RW).updateRwdOther(AuthorityValue.NONE);
                        workRecordTeam.updateAccessible(true).updateRwdRole(AuthorityValue.R).updateRwdOther(AuthorityValue.R);
                        workRecordList.updateAccessible(true).updateRwdRole(AuthorityValue.R).updateRwdOther(AuthorityValue.R);
                        minutes.updateAccessible(true).updateRwdRole(AuthorityValue.RWD).updateRwdOther(AuthorityValue.R);

                        approvalSearch.updateAccessible(true).updateRwdRole(AuthorityValue.R).updateRwdOther(AuthorityValue.R);
                        approvalDefault.updateAccessible(true).updateRwdRole(AuthorityValue.RWD).updateRwdOther(AuthorityValue.R);
                        approvalCooperation.updateAccessible(true).updateRwdRole(AuthorityValue.RWD).updateRwdOther(AuthorityValue.R);
                        approvalDayoff.updateAccessible(true).updateRwdRole(AuthorityValue.RWD).updateRwdOther(AuthorityValue.R);

                        schedule.updateAccessible(true).updateRwdRole(AuthorityValue.RWD).updateRwdOther(AuthorityValue.R);
                        employees.updateAccessible(true).updateRwdRole(AuthorityValue.R).updateRwdOther(AuthorityValue.R);
                    }
                    


                    case MASTER -> {
                        notice.updateAccessible(true).updateRwdRole(AuthorityValue.R).updateRwdOther(AuthorityValue.R);
                        freeboard.updateAccessible(true).updateRwdRole(AuthorityValue.RWD).updateRwdOther(AuthorityValue.R);
                        suggestion.updateAccessible(true).updateRwdRole(AuthorityValue.RWD).updateRwdOther(AuthorityValue.R);
                        archive.updateAccessible(true).updateRwdRole(AuthorityValue.R).updateRwdOther(AuthorityValue.R);

                        workRecord.updateAccessible(true).updateRwdRole(AuthorityValue.RW).updateRwdOther(AuthorityValue.NONE);
                        workRecordTeam.updateAccessible(true).updateRwdRole(AuthorityValue.R).updateRwdOther(AuthorityValue.R);
                        workRecordList.updateAccessible(true).updateRwdRole(AuthorityValue.R).updateRwdOther(AuthorityValue.R);
                        minutes.updateAccessible(true).updateRwdRole(AuthorityValue.RWD).updateRwdOther(AuthorityValue.R);

                        approvalSearch.updateAccessible(true).updateRwdRole(AuthorityValue.R).updateRwdOther(AuthorityValue.R);
                        approvalDefault.updateAccessible(true).updateRwdRole(AuthorityValue.RWD).updateRwdOther(AuthorityValue.R);
                        approvalCooperation.updateAccessible(true).updateRwdRole(AuthorityValue.RWD).updateRwdOther(AuthorityValue.R);
                        approvalDayoff.updateAccessible(true).updateRwdRole(AuthorityValue.RWD).updateRwdOther(AuthorityValue.R);

                        schedule.updateAccessible(true).updateRwdRole(AuthorityValue.RWD).updateRwdOther(AuthorityValue.R);
                        employees.updateAccessible(true).updateRwdRole(AuthorityValue.R).updateRwdOther(AuthorityValue.R);
                    }
                }

                repo.saveAll(
                    Arrays.asList(
                        notice, freeboard, suggestion, archive,
                        workRecord, workRecordTeam, workRecordList, minutes,
                        approvalSearch, approvalDefault, approvalCooperation, approvalDayoff,
                        schedule, employees
                    )
                ).stream().map( converter::getDto ).forEach( authorities::add );
            });
        
        } else {
            list.stream().map( converter::getDto ).forEach( authorities::add );
            log.info("Load authorities configurations completed.");
        }
    }


    private List<MenuAuthority> getAll() {
        return repo.findAll();
    }

    private void getAuthorities() {
        authorities.clear();
        getAll().stream().map( converter::getDto ).forEach( authorities::add );
        log.info("Load authorities configurations completed.");
    }





    public List<DtosMenuAuthority> getAuths(Menu menu) {
        return authorities.stream().filter( e -> e.getMenu() == menu ).collect(Collectors.toList());
    }

    public DtosMenuAuthority get(Long id) {
        return authorities.stream().filter( e -> e.getId().equals( id ) ).findFirst().orElseGet(DtosMenuAuthority::new);
    }

    public ServiceMsg update(Long id, DtorMenuAuthority dto) {
        Optional<MenuAuthority> target = repo.findById(id);
        if( ! target.isPresent() )
            return new ServiceMsg().setResult(ServiceResult.FAILURE).setMsg("해당하는 권한 데이터가 없습니다.");

        try {
            repo.save( target.get().updateAccessible( dto.isAccessible() ).updateRwdRole( dto.getRwdRole() ).updateRwdOther( dto.getRwdOther() ) );
            log.info("menu authority updated.");

            getAuthorities();
            return new ServiceMsg().setResult(ServiceResult.SUCCESS);

        } catch(Exception e) {
            e.printStackTrace();
            log.warn("update exception.");
            log.warn("id: {}, parameters: {}", id, dto.toString());

            return new ServiceMsg().setResult(ServiceResult.FAILURE).setMsg("권한 업데이트 에러입니다. 로그를 확인하세요");
        }
    }





    public boolean isAccessible(Menu menu, LoginUser loginUser) {
        return getAuthority(menu, loginUser).isAccessible();
    }

    private DtosMenuAuthority getAuthority(Menu menu, LoginUser loginUser) {
        return authorities.stream().filter( e -> e.getMenu() == menu && e.getRole() == loginUser.getMember().getRole() ).findFirst().orElseGet( DtosMenuAuthority::new );
    }



    public boolean isReadable(Menu menu, LoginUser loginUser, Long ownerId) {
        DtosMenuAuthority authority = getAuthority(menu, loginUser);

        if(loginUser.getMember().getId().equals( ownerId )) {   // Role에 대한 권한
            if(authority.getRwdRole() == AuthorityValue.R || authority.getRwdRole() == AuthorityValue.RW || authority.getRwdRole() == AuthorityValue.RD || authority.getRwdRole() == AuthorityValue.RWD)
                return true;

            else
                return false;
        
        
        } else {   // 타 유저 데이터에 대한 권한
            if(authority.getRwdOther() == AuthorityValue.R || authority.getRwdOther() == AuthorityValue.RW || authority.getRwdOther() == AuthorityValue.RD || authority.getRwdOther() == AuthorityValue.RWD)
                return true;

            else
                return false;
        }
    }



    // isAccessible과 함께 write.html, modify.html 페이지를 접근할 수 있는 권한 체크에도 사용한다.
    public boolean isWritable(Menu menu, LoginUser loginUser) {
        DtosMenuAuthority authority = getAuthority(menu, loginUser);
        
        if(authority.getRwdRole() == AuthorityValue.RW || authority.getRwdRole() == AuthorityValue.RWD)
            return true;

        else
            return false;
    }



    public boolean isUpdatable(Menu menu, LoginUser loginUser, Long ownerId) {
        if(loginUser.getMember().getId().equals( ownerId )) {   // Role에 대한 권한
            return isWritable(menu, loginUser);
        
        } else {   // 타 유저 데이터에 대한 권한
            DtosMenuAuthority authority = getAuthority(menu, loginUser);

            if(authority.getRwdOther() == AuthorityValue.RW || authority.getRwdOther() == AuthorityValue.RWD)
                return true;

            else
                return false;
        }
    }



    public boolean isDeletable(Menu menu, LoginUser loginUser, Long ownerId) {
        DtosMenuAuthority authority = getAuthority(menu, loginUser);

        if(loginUser.getMember().getId().equals( ownerId )) {   // Role에 대한 권한
            if(authority.getRwdRole() == AuthorityValue.RD || authority.getRwdRole() == AuthorityValue.RWD)
                return true;

            else
                return false;

        
        } else {   // 타 유저 데이터에 대한 권한
            if(authority.getRwdOther() == AuthorityValue.RD || authority.getRwdOther() == AuthorityValue.RWD)
                return true;

            else
                return false;
        }
    }
}
