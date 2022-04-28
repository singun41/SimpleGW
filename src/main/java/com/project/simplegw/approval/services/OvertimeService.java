package com.project.simplegw.approval.services;


import java.util.List;
import java.util.stream.Collectors;

import com.project.simplegw.approval.dtos.OvertimeDTO;
import com.project.simplegw.approval.dtos.OvertimeDocsDTO;
import com.project.simplegw.approval.entities.Overtime;
import com.project.simplegw.approval.repositories.OvertimeRepository;
import com.project.simplegw.common.services.BasecodeService;
import com.project.simplegw.common.vos.BasecodeType;
import com.project.simplegw.common.vos.Constants;
import com.project.simplegw.common.vos.RequestResult;
import com.project.simplegw.document.entities.Document;
import com.project.simplegw.member.services.MemberService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(rollbackFor = Exception.class, isolation = Isolation.READ_COMMITTED)
public class OvertimeService extends SubListService<Overtime, OvertimeDocsDTO> {
    
    private final OvertimeRepository overtimeRepo;
    private final ApprovalConverter approvalConverter;
    private final MemberService memberService;
    private final BasecodeService basecodeService;

    @Autowired
    public OvertimeService(OvertimeRepository overtimeRepo, ApprovalConverter approvalConverter, MemberService memberService, BasecodeService basecodeService) {
        this.overtimeRepo = overtimeRepo;
        this.approvalConverter = approvalConverter;
        this.memberService = memberService;
        this.basecodeService = basecodeService;
    }

    @Override
    public void saveEntities(List<Overtime> entities) {
        overtimeRepo.saveAll(entities);
    }
    
    @Override
    public List<Overtime> searchByDocsId(Long docsId) {
        return overtimeRepo.findAllByDocsIdOrderBySeq(docsId);
    }

    @Override
    public void deleteEntities(Long docsId) {
        overtimeRepo.deleteByDocsId(docsId);
    }

    @Override
    public RequestResult checkSubList(OvertimeDocsDTO dto) {
        List<OvertimeDTO> dtos = dto.getOvertimeDtoList();
        int size = dtos.size();

        for(int i=0; i<size; i++) {
            OvertimeDTO overtimeDTO = dtos.get(i);

            if(overtimeDTO.getMemberId() == null) {
                return RequestResult.getDefaultFail("근무자를 선택하세요.");
            }
            if(overtimeDTO.getCode() == null || overtimeDTO.getCode().isBlank()) {
                return RequestResult.getDefaultFail("근무 유형을 선택하세요.");
            }
            if(overtimeDTO.getWorkDate() == null) {
                return RequestResult.getDefaultFail("근무일을 선택하세요.");
            }
            if(overtimeDTO.getTimeStart() == null) {
                return RequestResult.getDefaultFail("시작 시간을 선택하세요.");
            }
            if(overtimeDTO.getTimeEnd() == null) {
                return RequestResult.getDefaultFail("종료 시간을 선택하세요.");
            }
            if(overtimeDTO.getRemarks() != null) {
                overtimeDTO.setRemarks(overtimeDTO.getRemarks().strip());
            }
        }

        return RequestResult.getDefaultSuccess(Constants.RESULT_MESSAGE_OK);
    }

    @Override
    public void subListSave(OvertimeDocsDTO dto, Document docs) {
        deleteEntities(docs.getId());

        List<Overtime> entities = dto.getOvertimeDtoList().stream().map(elem -> {
            Overtime entity = approvalConverter.getEntity(elem);
            return entity.insertMember(memberService.searchMemberDetailsById(elem.getMemberId()));
        }).collect(Collectors.toList());

        saveEntities(bindDocsToEntities(entities, docs));
    }

    @Override
    public List<OvertimeDTO> getSubDtoList(Long docsId) {
        return searchByDocsId(docsId).stream().map(entity -> {
            OvertimeDTO dto = approvalConverter.getDto(entity);
            dto.setValue(basecodeService.getValue(BasecodeType.OVERTIME, dto.getCode()));
            return dto.setMemberId(entity.getMember().getId());
        }).collect(Collectors.toList());
    }
}
