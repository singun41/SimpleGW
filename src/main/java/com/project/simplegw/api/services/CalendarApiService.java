package com.project.simplegw.api.services;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.project.simplegw.api.dtos.send.DtosHoliday;
import com.project.simplegw.system.security.LoginUser;
import com.project.simplegw.system.vos.Constants;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class CalendarApiService {
    private final ApiService apiService;

    @Autowired
    public CalendarApiService(ApiService apiService) {
        this.apiService = apiService;
        log.info("Component '" + this.getClass().getName() + "' has been created.");
    }


    @Value("${api.url.holiday}")   // 컴포넌트로 생성된 후에 값이 바인딩됨.
    private String urlHoliday;

    @Value("${api.key.holiday}")   // 컴포넌트로 생성된 후에 값이 바인딩됨.
    private String keyHoliday;

    @Value("${api.holiday.use}")   // 개발 완료 후에는 배포시에만 사용하므로 application-oper.properties에서 true, application-dev.properties는 false
    private boolean useHolidayApi;



    private String getHolidayApiUrl(int year, int month) {
        return
            new StringBuilder(urlHoliday)
                .append("?").append("serviceKey=").append(keyHoliday)
                .append("&").append("solYear=").append(year)
                .append("&").append("solMonth=").append( month < 10 ? "0" + month : month )
                .append("&").append("_type=json")
                .toString();
    }


    @Cacheable(cacheManager = Constants.CACHE_MANAGER, cacheNames = Constants.CACHE_HOLIDAYS, key = "#year + '-' + #month")
    public List<DtosHoliday> getHolidays(int year, int month) {
        List<DtosHoliday> holidays = new ArrayList<>();

        if( ! useHolidayApi )
            return holidays;

        String responseStr = apiService.call( getHolidayApiUrl(year, month) );
        log.info("response string: {}", responseStr);
        
        if(responseStr == null)
            return holidays;

        try {
            JSONObject json = new JSONObject(responseStr);
            JSONObject response = json.getJSONObject("response");
            JSONObject body = response.getJSONObject("body");
            
            int count = body.getInt("totalCount");
            if(count == 1) {
                JSONObject items = body.getJSONObject("items");   // count == 0 인 경우에는 items="" 이렇게 응답값이 오고, 이러면 Object로 처리가 안 되기 때문에 count가 있는 경우에만 진행하도록 if 문 안에서 선언한다.
                JSONObject data = items.getJSONObject("item");   // 1 건이므로 Object로 만듦.

                DtosHoliday holiday = new DtosHoliday();
                holiday.setTitle( data.getString("dateName") )
                        .setDateFrom( LocalDate.parse(data.get("locdate").toString(), DateTimeFormatter.ofPattern("yyyyMMdd")) )
                        .setDateTo( LocalDate.parse(data.get("locdate").toString(), DateTimeFormatter.ofPattern("yyyyMMdd")) )
                        .setHoliday( data.getString("isHoliday").toUpperCase().equals("Y") );

                holidays.add(holiday);

            } else if(count > 1) {
                JSONObject items = body.getJSONObject("items");
                JSONArray arrItem = items.getJSONArray("item");   // 배열이므로 Array로 만듦.

                arrItem.forEach(e -> {
                    JSONObject data = (JSONObject) e;
    
                    // 현재 공휴일 정보가 직전에 추가한 데이터와 같고 날짜만 다음 날짜인 경우에는 날짜만 업데이트 하고 넘어간다.
                    // 연속된 공휴일 정보가 from~to 로 들어오는 게 아니라 locdate 라는 키값에 해당 날짜 하나씩 여러개가 들어오기 때문에..
                    // 예를 들면 22년 추석은 22. 09. 09. ~ 22. 09. 11. 까지 3일이어서 데이터가 3개가 들어옴. 그래서 from ~ to로 하나의 데이터로 합치는 작업.
                    if( holidays.size() > 0 && data.getString("dateName").equals(holidays.get(holidays.size() - 1).getTitle()) ) {
                        holidays.get(holidays.size() - 1).setDateTo( LocalDate.parse(data.get("locdate").toString(), DateTimeFormatter.ofPattern("yyyyMMdd")) );
                        return;
                    }

                    DtosHoliday holiday = new DtosHoliday();
                    holiday.setTitle( data.getString("dateName") )
                            .setDateFrom( LocalDate.parse(data.get("locdate").toString(), DateTimeFormatter.ofPattern("yyyyMMdd")) )
                            .setDateTo( LocalDate.parse(data.get("locdate").toString(), DateTimeFormatter.ofPattern("yyyyMMdd")) )
                            .setHoliday( data.getString("isHoliday").toUpperCase().equals("Y") );
    
                    holidays.add(holiday);
                });
            }

        } catch(Exception e) {
            e.printStackTrace();
            log.warn("api result parsing exception.");
        }
        return holidays;
    }


    @CacheEvict(cacheManager = Constants.CACHE_MANAGER, cacheNames = Constants.CACHE_HOLIDAYS, allEntries = true)
    public void cacheManualRemove(LoginUser loginUser) {
        log.info("user: {}, manualCacheRemove() called. holidays all caches are removed.", loginUser.getMember().getId());
    }
}
