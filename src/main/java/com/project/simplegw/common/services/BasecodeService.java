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

        logger.info("?????? ???????????? ?????? ???????????? ???????????? ?????????????????????.");
    }
    // ----- ----- ----- ----- ----- frequently used code ----- ----- ----- ----- ----- //



    // ----- ----- ----- ----- ----- Searching ----- ----- ----- ----- ----- //
    // ????????? ?????? enabled true??? ????????? ????????????.
    private List<Basecode> getEntities(BasecodeType type) {
        return repo.findAllByTypeAndEnabledOrderBySeq(type, true);
    }

    public List<BasecodeDTO> getCodeList(BasecodeType type) {
        // ?????? ???????????? ??????????????? ???????????? ???????????? ????????? ??? ?????? ????????????.
        // getOrDefault??? ???????????? ?????? ?????? ???????????? ????????? ?????? ?????? ????????? ?????? ????????? ????????? ????????? map??? ?????? ???????????? repo??? ????????????.
        // ????????? computeIfAbsent??? ???????????? ?????? ???????????? ?????? ?????? ????????? ?????? ?????????.

        // return codeStorage.getOrDefault(type, entitiesToDtos(getEntities(type)));
        return codeStorage.computeIfAbsent(type, e -> entitiesToDtos(getEntities(e)));
    }

    public String getValue(BasecodeType type, String code) {   // ??????????????? ??????????????? ?????? ????????? codeStorage??? ????????? DB?????? ?????????.
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
            logger.warn("{}{}?????? ????????? ????????? ????????? ?????????????????????. type??? ???????????? ????????? ????????????. type ???: {}", e.getMessage(), System.lineSeparator(), type);
            return null;
        }
    }
    // ----- ----- ----- ----- ----- Searching ----- ----- ----- ----- ----- //



    // ----- ----- ----- ----- ----- Handling ----- ----- ----- ----- ----- //
    public RequestResult saveCode(CodeForAdminDTO dto) {
        if(dto.getCode().length() > Constants.COLUMN_LENGTH_BASE_CODE)
            return RequestResult.getDefaultFail("?????? ?????? ????????? ?????????????????????. " + Constants.COLUMN_LENGTH_BASE_CODE + " ??? ????????? ???????????????.");
        if(dto.getValue().length() > Constants.COLUMN_LENGTH_BASE_CODE_VALUE)
            return RequestResult.getDefaultFail("??? ?????? ????????? ?????????????????????. " + Constants.COLUMN_LENGTH_BASE_CODE_VALUE + " ??? ????????? ???????????????.");

        Basecode entity;
        Optional<Basecode> findResult = repo.findByTypeAndCode(dto.getType(), dto.getCode());
        String returnMsg = null;

        if(dto.getRemarks().isBlank()) {
            dto.setRemarks(null);
        }

        if(findResult.isPresent()) {   // type??? code??? insert only
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
        // ????????? ????????? ????????? ???????????? ?????? ????????? ???????????????, ????????? ?????? ?????? ??????????????? ????????????.

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
        Basecode A10 = Basecode.builder().type(BasecodeType.JOB_TITLE).code("A10").value("??????").enabled(true).seq(1).build();
        Basecode A20 = Basecode.builder().type(BasecodeType.JOB_TITLE).code("A20").value("?????????").enabled(true).seq(2).build();

        Basecode B10 = Basecode.builder().type(BasecodeType.JOB_TITLE).code("B10").value("??????").enabled(true).seq(3).build();
        Basecode B20 = Basecode.builder().type(BasecodeType.JOB_TITLE).code("B20").value("?????????").enabled(true).seq(4).build();

        Basecode C10 = Basecode.builder().type(BasecodeType.JOB_TITLE).code("C10").value("??????").enabled(true).seq(5).build();
        Basecode C20 = Basecode.builder().type(BasecodeType.JOB_TITLE).code("C20").value("??????").enabled(true).seq(6).build();

        Basecode D10 = Basecode.builder().type(BasecodeType.JOB_TITLE).code("D10").value("??????").enabled(true).seq(7).build();
        Basecode D20 = Basecode.builder().type(BasecodeType.JOB_TITLE).code("D20").value("????????????").enabled(true).seq(8).build();

        Basecode E10 = Basecode.builder().type(BasecodeType.JOB_TITLE).code("E10").value("??????").enabled(true).seq(9).build();
        Basecode E20 = Basecode.builder().type(BasecodeType.JOB_TITLE).code("E20").value("??????").enabled(true).seq(10).build();
        Basecode E30 = Basecode.builder().type(BasecodeType.JOB_TITLE).code("E30").value("??????").enabled(true).seq(11).build();
        Basecode E40 = Basecode.builder().type(BasecodeType.JOB_TITLE).code("E40").value("??????").enabled(true).seq(12).build();
        Basecode E50 = Basecode.builder().type(BasecodeType.JOB_TITLE).code("E50").value("??????").enabled(true).seq(13).build();

        Basecode F10 = Basecode.builder().type(BasecodeType.JOB_TITLE).code("F10").value("???????????????").enabled(true).seq(14).build();
        Basecode F20 = Basecode.builder().type(BasecodeType.JOB_TITLE).code("F20").value("???????????????").enabled(true).seq(15).build();
        Basecode F30 = Basecode.builder().type(BasecodeType.JOB_TITLE).code("F30").value("?????????").enabled(true).seq(16).build();
        Basecode F40 = Basecode.builder().type(BasecodeType.JOB_TITLE).code("F40").value("????????????").enabled(true).seq(17).build();

        Basecode G10 = Basecode.builder().type(BasecodeType.JOB_TITLE).code("G10").value("??????").enabled(true).seq(18).build();
        Basecode G20 = Basecode.builder().type(BasecodeType.JOB_TITLE).code("G20").value("??????").enabled(true).seq(19).build();
        Basecode G30 = Basecode.builder().type(BasecodeType.JOB_TITLE).code("G30").value("??????").enabled(true).seq(20).build();
        Basecode G40 = Basecode.builder().type(BasecodeType.JOB_TITLE).code("G40").value("??????").enabled(true).seq(21).build();

        Basecode X10 = Basecode.builder().type(BasecodeType.JOB_TITLE).code("X10").value("??????").enabled(true).seq(99).build();


        Basecode[] codeList = {A10, A20, B10, B20, C10, C20, D10, D20, E10, E20, E30, E40, E50, F10, F20, F30, F40, G10, G20, G30, G40, X10};
        codeListSave(codeList, BasecodeType.JOB_TITLE);
    }

    private void setDayoff() {
        Basecode code100 = Basecode.builder().type(BasecodeType.DAYOFF).code("100").value("??????").enabled(true).seq(1).build();

        // ?????? ????????? ????????? ?????? ????????? ?????????????????? dayoff class??? ???????????? ?????? ??????. ?????? ?????? ????????? dayoff class?????? ????????? ???.
        Basecode code101 = Basecode.builder().type(BasecodeType.DAYOFF).code("101").value("??????").enabled(false).seq(2).remarks("?????? ?????? ????????? ?????? ?????? ??????????????? ????????????").build();
        Basecode code110 = Basecode.builder().type(BasecodeType.DAYOFF).code("110").value("??????(??????)").enabled(true).seq(3).build();
        Basecode code120 = Basecode.builder().type(BasecodeType.DAYOFF).code("120").value("??????(??????)").enabled(true).seq(4).build();

        Basecode code190 = Basecode.builder().type(BasecodeType.DAYOFF).code("190").value("??????????????????").enabled(true).seq(5).build();

        Basecode code200 = Basecode.builder().type(BasecodeType.DAYOFF).code("200").value("????????????").enabled(true).seq(6).build();
        Basecode code201 = Basecode.builder().type(BasecodeType.DAYOFF).code("201").value("???????????????").enabled(true).seq(7).build();

        Basecode code300 = Basecode.builder().type(BasecodeType.DAYOFF).code("300").value("?????? 12??? ??????(????????????)").enabled(true).seq(8).build();
        Basecode code301 = Basecode.builder().type(BasecodeType.DAYOFF).code("301").value("?????? 36??? ??????(????????????)").enabled(true).seq(9).build();
        Basecode code302 = Basecode.builder().type(BasecodeType.DAYOFF).code("302").value("?????? ??? ??????").enabled(true).seq(10).build();
        Basecode code303 = Basecode.builder().type(BasecodeType.DAYOFF).code("303").value("?????? ??? ??????(??????)").enabled(true).seq(11).build();
        Basecode code304 = Basecode.builder().type(BasecodeType.DAYOFF).code("304").value("????????? ????????????").enabled(true).seq(12).build();
        Basecode code305 = Basecode.builder().type(BasecodeType.DAYOFF).code("305").value("?????? ?????? ??????").enabled(true).seq(13).build();

        Basecode code400 = Basecode.builder().type(BasecodeType.DAYOFF).code("400").value("????????????").enabled(true).seq(14).build();
        Basecode code401 = Basecode.builder().type(BasecodeType.DAYOFF).code("401").value("????????????(??????)").enabled(true).seq(15).build();
        Basecode code402 = Basecode.builder().type(BasecodeType.DAYOFF).code("402").value("????????????(??????)").enabled(true).seq(16).build();

        Basecode code500 = Basecode.builder().type(BasecodeType.DAYOFF).code("500").value("????????????").enabled(true).seq(17).build();
        Basecode code501 = Basecode.builder().type(BasecodeType.DAYOFF).code("501").value("????????????").enabled(true).seq(18).build();
        Basecode code502 = Basecode.builder().type(BasecodeType.DAYOFF).code("502").value("??????").enabled(true).seq(19).build();
        Basecode code503 = Basecode.builder().type(BasecodeType.DAYOFF).code("503").value("??????").enabled(true).seq(20).build();

        Basecode code600 = Basecode.builder().type(BasecodeType.DAYOFF).code("600").value("??????").enabled(true).seq(20).build();
        Basecode code601 = Basecode.builder().type(BasecodeType.DAYOFF).code("601").value("??????").enabled(true).seq(21).build();
        Basecode code602 = Basecode.builder().type(BasecodeType.DAYOFF).code("602").value("??????").enabled(true).seq(22).build();

        Basecode code900 = Basecode.builder().type(BasecodeType.DAYOFF).code("900").value("????????????").enabled(true).seq(90).build();

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
        Basecode code100 = Basecode.builder().type(BasecodeType.OVERTIME).code("100").value("?????? ??????").enabled(true).seq(1).build();
        Basecode code110 = Basecode.builder().type(BasecodeType.OVERTIME).code("110").value("?????? ??????").enabled(true).seq(2).build();
        Basecode code120 = Basecode.builder().type(BasecodeType.OVERTIME).code("120").value("?????? ??????").enabled(true).seq(3).build();

        Basecode[] codeList = {
            code100, code110, code120
        };

        codeListSave(codeList, BasecodeType.OVERTIME);
    }

    private void setCompany() {
        Basecode code100 = Basecode.builder().type(BasecodeType.COMPANY).code("100").value("?????? ??????").enabled(true).seq(1).build();
        Basecode code110 = Basecode.builder().type(BasecodeType.COMPANY).code("110").value("?????? ??????").enabled(true).seq(2).build();

        Basecode code200 = Basecode.builder().type(BasecodeType.COMPANY).code("200").value("?????? ????????????").enabled(true).seq(3).build();
        Basecode code210 = Basecode.builder().type(BasecodeType.COMPANY).code("210").value("?????? ??????(??????)").enabled(true).seq(4).build();

        Basecode code300 = Basecode.builder().type(BasecodeType.COMPANY).code("300").value("?????? ??????").enabled(true).seq(5).build();

        Basecode[] codeList = {
            code100, code110, code200, code210, code300
        };

        codeListSave(codeList, BasecodeType.COMPANY);
    }

    private void setPersonal() {
        Basecode code100 = Basecode.builder().type(BasecodeType.PERSONAL).code("100").value("??????").enabled(true).seq(1).build();
        Basecode code110 = Basecode.builder().type(BasecodeType.PERSONAL).code("110").value("??????/??????").enabled(true).seq(2).build();
        Basecode code120 = Basecode.builder().type(BasecodeType.PERSONAL).code("120").value("??????").enabled(true).seq(3).build();
        Basecode code130 = Basecode.builder().type(BasecodeType.PERSONAL).code("130").value("??????").enabled(true).seq(4).build();
        Basecode code140 = Basecode.builder().type(BasecodeType.PERSONAL).code("140").value("??????").enabled(true).seq(5).build();

        Basecode[] codeList = {
            code100, code110, code120, code130, code140
        };

        codeListSave(codeList, BasecodeType.PERSONAL);
    }
    // ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- Default Settings ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- //
}
