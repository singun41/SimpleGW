package com.project.simplegw.schedule.services;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import com.project.simplegw.schedule.dtos.admin.receive.DtorColor;
import com.project.simplegw.schedule.dtos.admin.send.DtosColor;
import com.project.simplegw.schedule.entities.Color;
import com.project.simplegw.schedule.helpers.ScheduleConverter;
import com.project.simplegw.schedule.repositories.ColorRepo;
import com.project.simplegw.schedule.vos.ScheduleFixedPersonalCode;
import com.project.simplegw.schedule.vos.ScheduleType;
import com.project.simplegw.system.vos.Constants;
import com.project.simplegw.system.vos.ServiceMsg;
import com.project.simplegw.system.vos.ServiceResult;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional(rollbackFor = Exception.class, isolation = Isolation.READ_COMMITTED)
public class ColorService {
    private final ColorRepo repo;
    private final ScheduleConverter converter;
    
    @Autowired
    public ColorService(ColorRepo repo, ScheduleConverter converter) {
        this.repo = repo;
        this.converter = converter;
        init();

        log.info("Component '" + this.getClass().getName() + "' has been created.");
    }





    private void init() {
        // 개인 일정 부분 색상 초기값 세팅
        if(getList(ScheduleType.PERSONAL).size() == 0) {
            DtorColor dayoff = new DtorColor().setCode(ScheduleFixedPersonalCode.DAYOFF.getCode()).setHexValue("edb6b6");
            DtorColor halfAm = new DtorColor().setCode(ScheduleFixedPersonalCode.HALF_AM.getCode()).setHexValue("b9dcf5");
            DtorColor halfPm = new DtorColor().setCode(ScheduleFixedPersonalCode.HALF_PM.getCode()).setHexValue("f5e699");
            DtorColor outOnBusiness = new DtorColor().setCode(ScheduleFixedPersonalCode.OUT_ON_BUSINESS.getCode()).setHexValue("b9f9ee");
            DtorColor outOnBusinessDirect = new DtorColor().setCode(ScheduleFixedPersonalCode.OUT_ON_BUSINESS_DIRECT.getCode()).setHexValue("fcbffd");
            DtorColor education = new DtorColor().setCode(ScheduleFixedPersonalCode.EDUCATION.getCode()).setHexValue("c1ecc3");
            DtorColor businessTrip = new DtorColor().setCode(ScheduleFixedPersonalCode.BUSINESS_TRIP.getCode()).setHexValue("d5c8c8");

            List<DtorColor> colors = Arrays.asList(dayoff, halfAm, halfPm, outOnBusiness, outOnBusinessDirect, education, businessTrip);
            
            try {
                List<Color> entities = colors.stream().map(e -> Color.builder().type(ScheduleType.PERSONAL).code(e.getCode()).hexValue(e.getHexValue()).build()).collect(Collectors.toList());
                repo.saveAll(entities);

            } catch(Exception e) {
                e.printStackTrace();
                log.warn("init() method exception.");
            }

            log.info("Personal schedule color initialized.");
        }
    }

    
    private List<Color> getEntities(ScheduleType type) {
        return repo.findByTypeOrderByCode(type);
    }


    @Cacheable(cacheManager = Constants.CACHE_MANAGER, cacheNames = Constants.CACHE_SCHEDULE_COLOR_LIST, key = "#type.name()")
    public List<DtosColor> getList(ScheduleType type) {   // Cacheable 처리를 위해 public으로 전환
        log.info("Cacheable method getColorList() called.");
        return getEntities(type).stream().map(converter::getDtosColor).collect(Collectors.toList());
    }


    @CacheEvict(cacheManager = Constants.CACHE_MANAGER, cacheNames = Constants.CACHE_SCHEDULE_COLOR_LIST, allEntries = false, key = "#type.name()")
    public ServiceMsg update(ScheduleType type, List<DtorColor> dtos) {   // CacheEvict 처리를 위해 public으로 전환
        log.info("CacheEvict method update() called.");

        // 기본적으로 코드는 저장되어 있고, 색상 기본값이 저장되어 있으니, update로만 동작한다.
        try {
            Map<String, String> colorMap = dtos.stream().collect(Collectors.toMap(DtorColor::getCode, DtorColor::getHexValue));

            List<Color> entities = getEntities(type);
            entities.forEach(e -> e.updateColor( colorMap.get(e.getCode()) ));
            repo.saveAll(entities);
            
            return new ServiceMsg().setResult(ServiceResult.SUCCESS);

        } catch(Exception e) {
            e.printStackTrace();
            log.warn("save exception.");
            log.warn("ScheduleType: {}, parameters: {}", type.name(), dtos.toString());
            return new ServiceMsg().setResult(ServiceResult.FAILURE).setMsg("저장 오류입니다. 로그를 확인하세요.");
        }
    }




    public void saveDefaultColor(ScheduleType type, String code) {   // BasecodeService 클래스에서 호출. 코드가 추가되면 색상 테이블에도 저장함.
        String defaultColor = "cee3f0";
        repo.save( Color.builder().type(type).code(code).hexValue(defaultColor).build() );
    }
}
