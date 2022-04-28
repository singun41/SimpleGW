package com.project.simplegw.common.services;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import com.project.simplegw.common.dtos.BasecodeDTO;
import com.project.simplegw.common.dtos.CodeForAdminDTO;
import com.project.simplegw.common.entities.Basecode;
import com.project.simplegw.common.repositories.BasecodeRepository;
import com.project.simplegw.common.vos.BasecodeType;
import com.project.simplegw.common.vos.Constants;
import com.project.simplegw.common.vos.RequestResult;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(rollbackFor = Exception.class, isolation = Isolation.READ_COMMITTED)
public class BasecodeService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final BasecodeRepository repo;
    private final BasecodeConverter converter;

    private static final ConcurrentHashMap<BasecodeType, List<BasecodeDTO>> codeStorage = new ConcurrentHashMap<>();

    @Autowired
    public BasecodeService(BasecodeRepository repo, BasecodeConverter converter) {
        this.repo = repo;
        this.converter = converter;

        setBasecodeList();
        setFrequentlyUsedCode();
    }

    // ----- ----- ----- ----- ----- Converting ----- ----- ----- ----- ----- //
    private List<BasecodeDTO> entitiesToDtos(List<Basecode> codeList) {
        return codeList.stream().map(converter::getDto).collect(Collectors.toList());
    }
    // ----- ----- ----- ----- ----- Converting ----- ----- ----- ----- ----- //



    // ----- ----- ----- ----- ----- frequently used code ----- ----- ----- ----- ----- //
    private void setFrequentlyUsedCode() {
        codeStorage.put(BasecodeType.DAYOFF, new ArrayList<>(entitiesToDtos(getEntities(BasecodeType.DAYOFF))));
        codeStorage.put(BasecodeType.OVERTIME, new ArrayList<>(entitiesToDtos(getEntities(BasecodeType.OVERTIME))));
        codeStorage.put(BasecodeType.PERSONAL, new ArrayList<>(entitiesToDtos(getEntities(BasecodeType.PERSONAL))));

        logger.info("자주 사용하는 코드 리스트를 메모리에 로드하였습니다.");
    }
    // ----- ----- ----- ----- ----- frequently used code ----- ----- ----- ----- ----- //



    // ----- ----- ----- ----- ----- Searching ----- ----- ----- ----- ----- //
    // 사용할 때는 enabled true인 것들만 가져온다.
    private List<Basecode> getEntities(BasecodeType type) {
        return repo.findAllByTypeAndEnabledOrderBySeq(type, true);
    }

    public List<BasecodeDTO> getCodeList(BasecodeType type) {
        // 자주 사용하는 기초코드는 메모리에 로드해서 필요할 때 바로 리턴한다.
        // getOrDefault를 사용하게 되면 키에 대응하는 밸류가 없을 경우 리턴할 값을 가지고 있어야 하므로 map에 값이 있더라도 repo를 호출한다.
        // 따라서 computeIfAbsent를 사용해야 키에 대응하는 값이 없는 경우에 값을 찾는다.

        // return codeStorage.getOrDefault(type, entitiesToDtos(getEntities(type)));
        return codeStorage.computeIfAbsent(type, e -> entitiesToDtos(getEntities(e)));
    }

    public String getValue(BasecodeType type, String code) {   // 비활성화된 기초코드도 찾기 위해서 codeStorage에 없을시 DB에서 찾는다.
        if(codeStorage.containsKey(type)) {
            Optional<BasecodeDTO> codeDto = codeStorage.get(type).stream().filter(e -> e.getCode().equals(code)).findFirst();
            return codeDto.orElseGet(() -> converter.getDto(repo.findByTypeAndCode(type, code).orElseGet(Basecode::new))).getValue();

        } else {
            return repo.findByTypeAndCode(type, code).orElseGet(Basecode::new).getValue();
        }
    }

    public List<CodeForAdminDTO> getAllTypes() {
        return Arrays.stream(BasecodeType.values()).map(CodeForAdminDTO::setBasecodeType).collect(Collectors.toList());
    }

    public List<CodeForAdminDTO> getAllCodes(String type) {
        if(type == null || type.isBlank()) {
            return null;
        }

        try {
            BasecodeType codeType = BasecodeType.valueOf(type);
            return repo.findAllByTypeOrderBySeq(codeType).stream().map(converter::getDtoForAdmin).collect(Collectors.toList());

        } catch(Exception e) {
            logger.warn("{}{}코드 리스트 조회시 에러가 발생하였습니다. type에 해당하는 코드가 없습니다. type 값: {}", e.getMessage(), System.lineSeparator(), type);
            return null;
        }
    }
    // ----- ----- ----- ----- ----- Searching ----- ----- ----- ----- ----- //



    // ----- ----- ----- ----- ----- Handling ----- ----- ----- ----- ----- //
    public RequestResult saveCode(CodeForAdminDTO dto) {
        if(dto.getCode().length() > Constants.COLUMN_LENGTH_BASE_CODE)
            return RequestResult.getDefaultFail("코드 문자 길이가 초과하였습니다. " + Constants.COLUMN_LENGTH_BASE_CODE + " 자 이하로 작성하세요.");
        if(dto.getValue().length() > Constants.COLUMN_LENGTH_BASE_CODE_VALUE)
            return RequestResult.getDefaultFail("값 문자 길이가 초과하였습니다. " + Constants.COLUMN_LENGTH_BASE_CODE_VALUE + " 자 이하로 작성하세요.");

        Basecode entity;
        Optional<Basecode> findResult = repo.findByTypeAndCode(dto.getType(), dto.getCode());
        String returnMsg = null;

        if(dto.getRemarks().isBlank()) {
            dto.setRemarks(null);
        }

        if(findResult.isPresent()) {   // type과 code는 insert only
            entity = findResult.get();
            entity.updateValue(dto.getValue()).updateRemarks(dto.getRemarks()).updateSeq(dto.getSeq());

            if(dto.isEnabled()) {
                entity.changeToEnabled();
            } else {
                entity.changeToDisabled();
            }
            returnMsg = Constants.RESULT_MESSAGE_UPDATED;

        } else {
            entity = converter.getEntity(dto);
            returnMsg = Constants.RESULT_MESSAGE_INSERTED;
        }

        repo.save(entity);
        setFrequentlyUsedCode();
        return RequestResult.getDefaultSuccess(returnMsg);
    }
    // ----- ----- ----- ----- ----- Handling ----- ----- ----- ----- ----- //



    // ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- Default Settings ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- //
    private void setBasecodeList() {
        setJobTitle();
        setDayoff();
        setOvertime();
        setCompany();
        setPersonal();
    }

    private void codeListSave(Basecode[] codeList, BasecodeType type) {
        // 기존에 저장된 코드는 넘어가고 신규 코드가 등록되거나, 코드가 없는 경우 초기값으로 세팅한다.

        Map<String, Basecode> savedCodeMap = repo.findAllByTypeOrderBySeq(type).stream().collect(Collectors.toMap(Basecode::getCode, bc -> bc));
        for(Basecode code : codeList) {
            Basecode savedCode = savedCodeMap.get(code.getCode());
            if(savedCode == null) {
                repo.save(code);
                logger.info("new code saved: {}", code.toString());
            }
        }
    }

    private void setJobTitle() {
        Basecode A10 = Basecode.builder().type(BasecodeType.JOB_TITLE).code("A10").value("회장").enabled(true).seq(1).build();
        Basecode A20 = Basecode.builder().type(BasecodeType.JOB_TITLE).code("A20").value("부회장").enabled(true).seq(2).build();

        Basecode B10 = Basecode.builder().type(BasecodeType.JOB_TITLE).code("B10").value("사장").enabled(true).seq(3).build();
        Basecode B20 = Basecode.builder().type(BasecodeType.JOB_TITLE).code("B20").value("부사장").enabled(true).seq(4).build();

        Basecode C10 = Basecode.builder().type(BasecodeType.JOB_TITLE).code("C10").value("전무").enabled(true).seq(5).build();
        Basecode C20 = Basecode.builder().type(BasecodeType.JOB_TITLE).code("C20").value("상무").enabled(true).seq(6).build();

        Basecode D10 = Basecode.builder().type(BasecodeType.JOB_TITLE).code("D10").value("이사").enabled(true).seq(7).build();
        Basecode D20 = Basecode.builder().type(BasecodeType.JOB_TITLE).code("D20").value("이사대우").enabled(true).seq(8).build();

        Basecode E10 = Basecode.builder().type(BasecodeType.JOB_TITLE).code("E10").value("부장").enabled(true).seq(9).build();
        Basecode E20 = Basecode.builder().type(BasecodeType.JOB_TITLE).code("E20").value("차장").enabled(true).seq(10).build();
        Basecode E30 = Basecode.builder().type(BasecodeType.JOB_TITLE).code("E30").value("과장").enabled(true).seq(11).build();
        Basecode E40 = Basecode.builder().type(BasecodeType.JOB_TITLE).code("E40").value("대리").enabled(true).seq(12).build();
        Basecode E50 = Basecode.builder().type(BasecodeType.JOB_TITLE).code("E50").value("사원").enabled(true).seq(13).build();

        Basecode F10 = Basecode.builder().type(BasecodeType.JOB_TITLE).code("F10").value("책임연구원").enabled(true).seq(14).build();
        Basecode F20 = Basecode.builder().type(BasecodeType.JOB_TITLE).code("F20").value("선임연구원").enabled(true).seq(15).build();
        Basecode F30 = Basecode.builder().type(BasecodeType.JOB_TITLE).code("F30").value("연구원").enabled(true).seq(16).build();
        Basecode F40 = Basecode.builder().type(BasecodeType.JOB_TITLE).code("F40").value("연구조원").enabled(true).seq(17).build();

        Basecode G10 = Basecode.builder().type(BasecodeType.JOB_TITLE).code("G10").value("기장").enabled(true).seq(18).build();
        Basecode G20 = Basecode.builder().type(BasecodeType.JOB_TITLE).code("G20").value("기선").enabled(true).seq(19).build();
        Basecode G30 = Basecode.builder().type(BasecodeType.JOB_TITLE).code("G30").value("기사").enabled(true).seq(20).build();
        Basecode G40 = Basecode.builder().type(BasecodeType.JOB_TITLE).code("G40").value("기원").enabled(true).seq(21).build();

        Basecode X10 = Basecode.builder().type(BasecodeType.JOB_TITLE).code("X10").value("별정").enabled(true).seq(99).build();


        Basecode[] codeList = {A10, A20, B10, B20, C10, C20, D10, D20, E10, E20, E30, E40, E50, F10, F20, F30, F40, G10, G20, G30, G40, X10};
        codeListSave(codeList, BasecodeType.JOB_TITLE);
    }

    private void setDayoff() {
        Basecode code100 = Basecode.builder().type(BasecodeType.DAYOFF).code("100").value("연차").enabled(true).seq(1).build();

        // 반차 코드로 구분해 연차 갯수를 카운트하도록 dayoff class에 하드코딩 되어 있음. 반차 코드 변경시 dayoff class에도 적용할 것.
        Basecode code101 = Basecode.builder().type(BasecodeType.DAYOFF).code("101").value("반차").enabled(false).seq(2).remarks("오전 오후 구분이 되지 않는 코드이므로 비활성화").build();
        Basecode code110 = Basecode.builder().type(BasecodeType.DAYOFF).code("110").value("반차(오전)").enabled(true).seq(3).build();
        Basecode code120 = Basecode.builder().type(BasecodeType.DAYOFF).code("120").value("반차(오후)").enabled(true).seq(4).build();

        Basecode code190 = Basecode.builder().type(BasecodeType.DAYOFF).code("190").value("장기근속휴가").enabled(true).seq(5).build();

        Basecode code200 = Basecode.builder().type(BasecodeType.DAYOFF).code("200").value("대체휴가").enabled(true).seq(6).build();
        Basecode code201 = Basecode.builder().type(BasecodeType.DAYOFF).code("201").value("단축근무일").enabled(true).seq(7).build();

        Basecode code300 = Basecode.builder().type(BasecodeType.DAYOFF).code("300").value("임신 12주 이내(단축근무)").enabled(true).seq(8).build();
        Basecode code301 = Basecode.builder().type(BasecodeType.DAYOFF).code("301").value("임신 36주 이후(단축근무)").enabled(true).seq(9).build();
        Basecode code302 = Basecode.builder().type(BasecodeType.DAYOFF).code("302").value("산전 후 휴가").enabled(true).seq(10).build();
        Basecode code303 = Basecode.builder().type(BasecodeType.DAYOFF).code("303").value("산전 후 휴가(무급)").enabled(true).seq(11).build();
        Basecode code304 = Basecode.builder().type(BasecodeType.DAYOFF).code("304").value("배우자 출산휴가").enabled(true).seq(12).build();
        Basecode code305 = Basecode.builder().type(BasecodeType.DAYOFF).code("305").value("태아 검진 휴가").enabled(true).seq(13).build();

        Basecode code400 = Basecode.builder().type(BasecodeType.DAYOFF).code("400").value("육아휴직").enabled(true).seq(14).build();
        Basecode code401 = Basecode.builder().type(BasecodeType.DAYOFF).code("401").value("육아휴직(무급)").enabled(true).seq(15).build();
        Basecode code402 = Basecode.builder().type(BasecodeType.DAYOFF).code("402").value("돌봄휴가(무급)").enabled(true).seq(16).build();

        Basecode code500 = Basecode.builder().type(BasecodeType.DAYOFF).code("500").value("유급휴가").enabled(true).seq(17).build();
        Basecode code501 = Basecode.builder().type(BasecodeType.DAYOFF).code("501").value("무급휴가").enabled(true).seq(18).build();
        Basecode code502 = Basecode.builder().type(BasecodeType.DAYOFF).code("502").value("병가").enabled(true).seq(19).build();
        Basecode code503 = Basecode.builder().type(BasecodeType.DAYOFF).code("503").value("산재").enabled(true).seq(20).build();

        Basecode code600 = Basecode.builder().type(BasecodeType.DAYOFF).code("600").value("조퇴").enabled(true).seq(20).build();
        Basecode code601 = Basecode.builder().type(BasecodeType.DAYOFF).code("601").value("외출").enabled(true).seq(21).build();
        Basecode code602 = Basecode.builder().type(BasecodeType.DAYOFF).code("602").value("훈련").enabled(true).seq(22).build();

        Basecode code900 = Basecode.builder().type(BasecodeType.DAYOFF).code("900").value("보건휴가").enabled(true).seq(90).build();

        Basecode[] codeList = {
            code100, code101, code110, code120, code190,
            code200, code201,
            code300, code301, code302, code303, code304, code305,
            code400, code401, code402,
            code500, code501, code502, code503,
            code600, code601, code602,
            
            code900
        };

        codeListSave(codeList, BasecodeType.DAYOFF);
    }

    private void setOvertime() {
        Basecode code100 = Basecode.builder().type(BasecodeType.OVERTIME).code("100").value("조기 출근").enabled(true).seq(1).build();
        Basecode code110 = Basecode.builder().type(BasecodeType.OVERTIME).code("110").value("평일 연장").enabled(true).seq(2).build();
        Basecode code120 = Basecode.builder().type(BasecodeType.OVERTIME).code("120").value("휴일 근무").enabled(true).seq(3).build();

        Basecode[] codeList = {
            code100, code110, code120
        };

        codeListSave(codeList, BasecodeType.OVERTIME);
    }

    private void setCompany() {
        Basecode code100 = Basecode.builder().type(BasecodeType.COMPANY).code("100").value("사내 공사").enabled(true).seq(1).build();
        Basecode code110 = Basecode.builder().type(BasecodeType.COMPANY).code("110").value("안전 교육").enabled(true).seq(2).build();

        Basecode code200 = Basecode.builder().type(BasecodeType.COMPANY).code("200").value("단체 건강검진").enabled(true).seq(3).build();
        Basecode code210 = Basecode.builder().type(BasecodeType.COMPANY).code("210").value("단체 휴가(하계)").enabled(true).seq(4).build();

        Basecode code300 = Basecode.builder().type(BasecodeType.COMPANY).code("300").value("사내 행사").enabled(true).seq(5).build();

        Basecode[] codeList = {
            code100, code110, code200, code210, code300
        };

        codeListSave(codeList, BasecodeType.COMPANY);
    }

    private void setPersonal() {
        Basecode code100 = Basecode.builder().type(BasecodeType.PERSONAL).code("100").value("외근").enabled(true).seq(1).build();
        Basecode code110 = Basecode.builder().type(BasecodeType.PERSONAL).code("110").value("직출/직퇴").enabled(true).seq(2).build();
        Basecode code120 = Basecode.builder().type(BasecodeType.PERSONAL).code("120").value("교육").enabled(true).seq(3).build();
        Basecode code130 = Basecode.builder().type(BasecodeType.PERSONAL).code("130").value("휴가").enabled(true).seq(4).build();
        Basecode code140 = Basecode.builder().type(BasecodeType.PERSONAL).code("140").value("출장").enabled(true).seq(5).build();

        Basecode[] codeList = {
            code100, code110, code120, code130, code140
        };

        codeListSave(codeList, BasecodeType.PERSONAL);
    }
    // ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- Default Settings ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- //
}
