package com.project.simplegw.schedule.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import com.project.simplegw.schedule.dtos.receive.DtorSchedule;
import com.project.simplegw.schedule.vos.ScheduleType;
import com.project.simplegw.system.security.LoginUser;
import com.project.simplegw.system.vos.ResponseMsg;
import com.project.simplegw.system.vos.Role;
import com.project.simplegw.system.vos.ServiceMsg;
import com.project.simplegw.system.vos.ServiceResult;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional(rollbackFor = Exception.class, isolation = Isolation.READ_COMMITTED)
public class CompanyScheduleService {
    private final ScheduleType COMPANY = ScheduleType.COMPANY;
    private final ScheduleService service;

    @Autowired
    public CompanyScheduleService(ScheduleService service) {
        this.service = service;
        log.info("Component '" + this.getClass().getName() + "' has been created.");
    }




    public ServiceMsg save(DtorSchedule dto, LoginUser loginUser) {
        if(loginUser.getMember().getRole() == Role.USER)
            return new ServiceMsg().setResult(ServiceResult.FAILURE).setMsg( ResponseMsg.UNAUTHORIZED.getTitle() );

        return service.save(COMPANY, dto, loginUser);
    }


    public ServiceMsg update(Long id, DtorSchedule dto, LoginUser loginUser) {
        if(loginUser.getMember().getRole() == Role.USER)
            return new ServiceMsg().setResult(ServiceResult.FAILURE).setMsg( ResponseMsg.UNAUTHORIZED.getTitle() );

        return service.update(COMPANY, id, dto, loginUser);
    }


    public ServiceMsg delete(Long id, LoginUser loginUser) {
        if(loginUser.getMember().getRole() == Role.USER)
            return new ServiceMsg().setResult(ServiceResult.FAILURE).setMsg( ResponseMsg.UNAUTHORIZED.getTitle() );
            
        return service.delete(COMPANY, id, loginUser);
    }
}
