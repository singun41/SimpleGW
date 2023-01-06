package com.project.simplegw.code.services;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.project.simplegw.code.dtos.receive.DtorBasecode;
import com.project.simplegw.code.dtos.send.DtosBasecode;
import com.project.simplegw.code.dtos.send.DtosCodeValue;
import com.project.simplegw.code.entities.Basecode;
import com.project.simplegw.code.helper.BasecodeConverter;
import com.project.simplegw.code.repositories.BasecodeRepo;
import com.project.simplegw.code.vos.BasecodeType;
import com.project.simplegw.schedule.services.ColorService;
import com.project.simplegw.schedule.vos.ScheduleFixedPersonalCode;
import com.project.simplegw.schedule.vos.ScheduleType;
import com.project.simplegw.system.vos.Constants;
import com.project.simplegw.system.vos.ServiceMsg;
import com.project.simplegw.system.vos.ServiceResult;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional(rollbackFor = Exception.class, isolation = Isolation.READ_COMMITTED)
public class BasecodeService {
    private final BasecodeRepo repo;
    private final BasecodeConverter converter;
    private final ColorService colorService;

    @Autowired
    public BasecodeService(BasecodeRepo repo, BasecodeConverter converter, ColorService colorService) {
        this.repo = repo;
        this.converter = converter;
        this.colorService = colorService;
        
        initialization();

        log.info("Component '" + this.getClass().getName() + "' has been created.");
    }



    // ↓ ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- initialization ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- ↓ //
    private void initialization() {
        setJobTitle();
        setDayoff();
        setOvertime();
        setSchedulePersonal();
        setScehduleCompany();
    }

    private void setJobTitle() {
        if(getCodeStream(BasecodeType.JOB_TITLE).count() > 0)
            return;
        
        DtorBasecode A10 = new DtorBasecode().setCode("A10").setValue("회장").setSeq(1).setEnabled(true);
        DtorBasecode A20 = new DtorBasecode().setCode("A20").setValue("부회장").setSeq(1).setEnabled(true);

        DtorBasecode B10 = new DtorBasecode().setCode("B10").setValue("사장").setSeq(1).setEnabled(true);
        DtorBasecode B20 = new DtorBasecode().setCode("B20").setValue("부사장").setSeq(1).setEnabled(true);

        DtorBasecode C10 = new DtorBasecode().setCode("C10").setValue("전무").setSeq(1).setEnabled(true);
        DtorBasecode C20 = new DtorBasecode().setCode("C20").setValue("상무").setSeq(1).setEnabled(true);

        DtorBasecode D10 = new DtorBasecode().setCode("D10").setValue("이사").setSeq(1).setEnabled(true);
        DtorBasecode D20 = new DtorBasecode().setCode("D20").setValue("이사대우").setSeq(1).setEnabled(true);

        DtorBasecode E10 = new DtorBasecode().setCode("E10").setValue("부장").setSeq(1).setEnabled(true);
        DtorBasecode E20 = new DtorBasecode().setCode("E20").setValue("차장").setSeq(1).setEnabled(true);
        DtorBasecode E30 = new DtorBasecode().setCode("E30").setValue("과장").setSeq(1).setEnabled(true);
        DtorBasecode E40 = new DtorBasecode().setCode("E40").setValue("대리").setSeq(1).setEnabled(true);
        DtorBasecode E50 = new DtorBasecode().setCode("E50").setValue("사원").setSeq(1).setEnabled(true);

        List<DtorBasecode> dtos = Arrays.asList(A10, A20, B10, B20, C10, C20, D10, D20, E10, E20, E30, E40, E50);

        try {
            repo.saveAll( dtos.stream().map(e -> converter.getBasecode( e.setSeq(dtos.indexOf(e) + 1) ).setType(BasecodeType.JOB_TITLE)).collect(Collectors.toList()) );
            log.info("job title basecode created.");

        } catch(Exception e) {
            e.printStackTrace();
            log.warn("job title basecode initialization expcetion.");
        }
    }

    private void setDayoff() {
        if(getCodeStream(BasecodeType.DAYOFF).count() > 0)
            return;
        
        // 휴가신청서 최종 승인시 스케줄에 자동 등록되는데, 지정한 기본 코드를 그대로 사용하기 위해서 3개 코드만 동일하게 처리.
        DtorBasecode code100 = new DtorBasecode().setCode(ScheduleFixedPersonalCode.DAYOFF.getCode()).setValue("연차").setSeq(1).setEnabled(true);   // value는 연차로 지정한다.
        DtorBasecode code101 = new DtorBasecode().setCode(ScheduleFixedPersonalCode.HALF_AM.getCode()).setValue(ScheduleFixedPersonalCode.HALF_AM.getTitle()).setSeq(1).setEnabled(true);
        DtorBasecode code102 = new DtorBasecode().setCode(ScheduleFixedPersonalCode.HALF_PM.getCode()).setValue(ScheduleFixedPersonalCode.HALF_PM.getTitle()).setSeq(1).setEnabled(true);
        DtorBasecode code110 = new DtorBasecode().setCode("110").setValue("대체휴가").setSeq(1).setEnabled(true);
        DtorBasecode code111 = new DtorBasecode().setCode("111").setValue("경조휴가").setSeq(1).setEnabled(true);
        DtorBasecode code112 = new DtorBasecode().setCode("112").setValue("장기근속휴가").setSeq(1).setEnabled(true);
        DtorBasecode code150 = new DtorBasecode().setCode("150").setValue("유급휴가").setSeq(1).setEnabled(true);
        DtorBasecode code151 = new DtorBasecode().setCode("151").setValue("무급휴가").setSeq(1).setEnabled(true);
        DtorBasecode code190 = new DtorBasecode().setCode("190").setValue("보건휴가").setSeq(1).setEnabled(true);
        DtorBasecode code200 = new DtorBasecode().setCode("200").setValue("단축근무일").setSeq(1).setEnabled(true);
        DtorBasecode code300 = new DtorBasecode().setCode("300").setValue("임신 12주 이내(단축근무)").setSeq(1).setEnabled(true);
        DtorBasecode code301 = new DtorBasecode().setCode("301").setValue("임신 36주 이후(단축근무)").setSeq(1).setEnabled(true);
        DtorBasecode code302 = new DtorBasecode().setCode("302").setValue("출산 전/후 휴가").setSeq(1).setEnabled(true);
        DtorBasecode code303 = new DtorBasecode().setCode("303").setValue("출산 전/후 휴가(무급)").setSeq(1).setEnabled(true);
        DtorBasecode code304 = new DtorBasecode().setCode("304").setValue("배우자 출산휴가").setSeq(1).setEnabled(true);
        DtorBasecode code305 = new DtorBasecode().setCode("305").setValue("태아 검진 휴가").setSeq(1).setEnabled(true);
        DtorBasecode code310 = new DtorBasecode().setCode("310").setValue("육아휴직").setSeq(1).setEnabled(true);
        DtorBasecode code311 = new DtorBasecode().setCode("311").setValue("육아휴직(무급)").setSeq(1).setEnabled(true);
        DtorBasecode code312 = new DtorBasecode().setCode("312").setValue("돌봄휴가(무급)").setSeq(1).setEnabled(true);
        DtorBasecode code400 = new DtorBasecode().setCode("400").setValue("조퇴").setSeq(1).setEnabled(true);
        DtorBasecode code401 = new DtorBasecode().setCode("401").setValue("외출").setSeq(1).setEnabled(true);
        DtorBasecode code402 = new DtorBasecode().setCode("402").setValue("훈련").setSeq(1).setEnabled(true);
        DtorBasecode code500 = new DtorBasecode().setCode("500").setValue("병가").setSeq(1).setEnabled(true);
        DtorBasecode code501 = new DtorBasecode().setCode("501").setValue("산재").setSeq(1).setEnabled(true);

        List<DtorBasecode> dtos = Arrays.asList(
            code100, code101, code102, code110, code111, code112, code150, code151, code190,
            code200,
            code300, code301, code302, code303, code304, code305, code310, code311, code312,
            code400, code401, code402,
            code500, code501
        );

        try {
            repo.saveAll( dtos.stream().map(e -> converter.getBasecode( e.setSeq(dtos.indexOf(e) + 1) ).setType(BasecodeType.DAYOFF)).collect(Collectors.toList()) );
            log.info("dayoff basecode created.");

        } catch(Exception e) {
            e.printStackTrace();
            log.warn("dayoff basecode initialization expcetion.");
        }
    }

    private void setOvertime() {
        if(getCodeStream(BasecodeType.OVERTIME).count() > 0)
            return;

        DtorBasecode code100 = new DtorBasecode().setCode("100").setValue("조기 출근").setSeq(1).setEnabled(true);
        DtorBasecode code110 = new DtorBasecode().setCode("110").setValue("평일 연장").setSeq(2).setEnabled(true);
        DtorBasecode code120 = new DtorBasecode().setCode("120").setValue("휴일 근무").setSeq(3).setEnabled(true);

        List<DtorBasecode> dtos = Arrays.asList(code100, code110, code120);

        try {
            repo.saveAll( dtos.stream().map(e -> converter.getBasecode(e).setType(BasecodeType.OVERTIME)).collect(Collectors.toList()) );
            log.info("overtime basecode created.");

        } catch(Exception e) {
            e.printStackTrace();
            log.warn("overtime basecode initialization expcetion.");
        }
    }

    private void setSchedulePersonal() {
        if(getCodeStream(BasecodeType.PERSONAL).count() > 0)
            return;
        
        DtorBasecode dayoff = new DtorBasecode().setCode(ScheduleFixedPersonalCode.DAYOFF.getCode()).setValue(ScheduleFixedPersonalCode.DAYOFF.getTitle()).setSeq(1).setEnabled(true);
        DtorBasecode halfAm = new DtorBasecode().setCode(ScheduleFixedPersonalCode.HALF_AM.getCode()).setValue(ScheduleFixedPersonalCode.HALF_AM.getTitle()).setSeq(1).setEnabled(true);
        DtorBasecode halfPm = new DtorBasecode().setCode(ScheduleFixedPersonalCode.HALF_PM.getCode()).setValue(ScheduleFixedPersonalCode.HALF_PM.getTitle()).setSeq(1).setEnabled(true);
        DtorBasecode outOnBisuness = new DtorBasecode().setCode(ScheduleFixedPersonalCode.OUT_ON_BUSINESS.getCode()).setValue(ScheduleFixedPersonalCode.OUT_ON_BUSINESS.getTitle()).setSeq(1).setEnabled(true);
        DtorBasecode outOnBisunessDirect = new DtorBasecode().setCode(ScheduleFixedPersonalCode.OUT_ON_BUSINESS_DIRECT.getCode()).setValue(ScheduleFixedPersonalCode.OUT_ON_BUSINESS_DIRECT.getTitle()).setSeq(1).setEnabled(true);
        DtorBasecode education = new DtorBasecode().setCode(ScheduleFixedPersonalCode.EDUCATION.getCode()).setValue(ScheduleFixedPersonalCode.EDUCATION.getTitle()).setSeq(1).setEnabled(true);
        DtorBasecode businessTrip = new DtorBasecode().setCode(ScheduleFixedPersonalCode.BUSINESS_TRIP.getCode()).setValue(ScheduleFixedPersonalCode.BUSINESS_TRIP.getTitle()).setSeq(1).setEnabled(true);

        List<DtorBasecode> dtos = Arrays.asList(dayoff, halfAm, halfPm, outOnBisuness, outOnBisunessDirect, education, businessTrip);

        try {
            repo.saveAll( dtos.stream().map(e -> converter.getBasecode( e.setSeq(dtos.indexOf(e) + 1) ).setType(BasecodeType.PERSONAL)).collect(Collectors.toList()) );
            log.info("personal(schedule only) basecode created.");

        } catch(Exception e) {
            e.printStackTrace();
            log.warn("personal(schedule only) basecode initialization expcetion.");
        }
    }

    private void setScehduleCompany() {
        if(getCodeStream(BasecodeType.COMPANY).count() > 0)
            return;

        DtorBasecode code100 = new DtorBasecode().setCode("100").setValue("사내 행사").setEnabled(true);
        DtorBasecode code110 = new DtorBasecode().setCode("110").setValue("사외 행사").setEnabled(true);
        DtorBasecode code200 = new DtorBasecode().setCode("200").setValue("건강 검진").setEnabled(true);
        DtorBasecode code300 = new DtorBasecode().setCode("300").setValue("법정 교육").setEnabled(true);
        DtorBasecode code400 = new DtorBasecode().setCode("400").setValue("하계 휴가").setEnabled(true);

        List<DtorBasecode> dtos = Arrays.asList(code100, code110, code200, code300, code400);

        try {
            repo.saveAll( dtos.stream().map(e -> converter.getBasecode( e.setSeq(dtos.indexOf(e) + 1) ).setType(BasecodeType.COMPANY)).collect(Collectors.toList()) );

            dtos.forEach(e -> colorService.saveDefaultColor(ScheduleType.COMPANY, e.getCode()));   // 캘린더에 렌더링할 기본 색상값 저장.

            log.info("company(schedule only) basecode created.");

        } catch(Exception e) {
            e.printStackTrace();
            log.warn("company(schedule only) basecode initialization expcetion.");
        }
    }
    // ↑ ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- initialization ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- ↑ //




    
    private Stream<Basecode> getCodeStream(BasecodeType type) {
        return repo.findByType(type).stream().sorted(Comparator.comparing(Basecode::getSeq));
    }


    public List<BasecodeType> getAllTypes() {
        return Arrays.stream(BasecodeType.values()).sorted(Comparator.comparing(BasecodeType::getTitle)).collect(Collectors.toList());
    }

    @Cacheable(cacheManager = Constants.CACHE_MANAGER, cacheNames = Constants.CACHE_BASECODE, key = "#type.name()")
    public List<DtosBasecode> getCodes(BasecodeType type) {
        log.info("Cacheable method getCodes() called. type: {}", type.name());
        return getCodeStream(type).map(converter::getDtosBasecode).collect(Collectors.toList());
    }

    @Cacheable(cacheManager = Constants.CACHE_MANAGER, cacheNames = Constants.CACHE_BASECODE, key = "#id")
    public DtosBasecode getCode(Long id) {
        log.info("Cacheable method getCode() called. id: {}", id);
        return converter.getDtosBasecode( repo.findById(id).get() );
    }



    private boolean isDuplicated(BasecodeType type, String code) {
        return getCodeStream(type).filter(e -> e.getCode().equals(code)).findAny().isPresent();
    }

    @CacheEvict(cacheManager = Constants.CACHE_MANAGER, cacheNames = {Constants.CACHE_BASECODE, Constants.CACHE_JOB_TITLES, Constants.CACHE_DAYOFF_CODES, Constants.CACHE_SCHEDULE_CODE_PERSONAL}, allEntries = true)
    public ServiceMsg create(BasecodeType type, DtorBasecode dto) {
        try {
            if(isDuplicated(type, dto.getCode())) {
                return new ServiceMsg().setResult(ServiceResult.FAILURE).setMsg("중복된 코드입니다.");
            } else {
                Basecode entity = converter.getBasecode(dto);
                repo.save( entity.setType(type) );
    

                // full-calendar에서 사용하는 ScheduleType이면 기본 컬러를 저장하고 없으면 무시.
                try {
                    ScheduleType scheduleType = ScheduleType.valueOf(type.name());
                    colorService.saveDefaultColor(scheduleType, dto.getCode());
                    log.info("ScheduleType cole color added. type: {}, code: {}, value: {}", scheduleType.name(), dto.getCode(), dto.getValue());
                } catch(Exception e) {
                    
                }

                return new ServiceMsg().setResult(ServiceResult.SUCCESS);
            }

        } catch(Exception e) {
            e.printStackTrace();
            log.warn("create exception");
            log.warn("parameters: {}, {}", type.toString(), dto.toString());
            return new ServiceMsg().setResult(ServiceResult.FAILURE).setMsg("기초코드 등록 에러입니다. 로그를 확인하세요.");
        }
    }

    @CacheEvict(cacheManager = Constants.CACHE_MANAGER, cacheNames = {Constants.CACHE_BASECODE, Constants.CACHE_JOB_TITLES, Constants.CACHE_DAYOFF_CODES, Constants.CACHE_SCHEDULE_CODE_PERSONAL}, allEntries = true)
    public ServiceMsg update(Long id, DtorBasecode dto) {
        try {
            Optional<Basecode> findCode = repo.findById(id);
            if(findCode.isPresent()) {
                Basecode entity = findCode.get();
    
                entity.updateValue(dto.getValue()).updateSeq(dto.getSeq()).updateRemarks(dto.getRemarks());
                if(dto.isEnabled())
                    entity.setEnabled();
                else
                    entity.setDisabled();
                
                repo.save(entity);
                return new ServiceMsg().setResult(ServiceResult.SUCCESS);
            
            } else {
                return new ServiceMsg().setResult(ServiceResult.FAILURE).setMsg("코드 데이터가 없습니다.");
            }

        } catch(Exception e) {
            e.printStackTrace();
            log.warn("update exception");
            log.warn("parameters: {}, {}", id.toString(), dto.toString());
            return new ServiceMsg().setResult(ServiceResult.FAILURE).setMsg("기초코드 업데이트 에러입니다. 로그를 확인하세요.");
        }
    }


    
    @Cacheable(cacheManager = Constants.CACHE_MANAGER, cacheNames = Constants.CACHE_JOB_TITLES)
    public List<String> getJobTitles() {
        return getCodeStream(BasecodeType.JOB_TITLE).filter(Basecode::isEnabled).map(Basecode::getValue).collect(Collectors.toList());
    }

    @Cacheable(cacheManager = Constants.CACHE_MANAGER, cacheNames = Constants.CACHE_DAYOFF_CODES)
    public List<DtosCodeValue> getDayoffCodes() {
        return getCodeStream(BasecodeType.DAYOFF).filter(Basecode::isEnabled).map(converter::getDtosCodeValue).collect(Collectors.toList());
    }

    @Cacheable(cacheManager = Constants.CACHE_MANAGER, cacheNames = Constants.CACHE_SCHEDULE_CODE_PERSONAL)
    public List<DtosCodeValue> getPersonalScheduleCodes() {
        return getCodeStream(BasecodeType.PERSONAL).filter(Basecode::isEnabled).map(converter::getDtosCodeValue).collect(Collectors.toList());
    }
}
