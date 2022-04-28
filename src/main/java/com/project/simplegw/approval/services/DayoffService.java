package com.project.simplegw.approval.services;


import java.util.List;
import java.util.stream.Collectors;

import com.project.simplegw.approval.dtos.DayoffDTO;
import com.project.simplegw.approval.dtos.DayoffDocsDTO;
import com.project.simplegw.approval.entities.Dayoff;
import com.project.simplegw.approval.repositories.DayoffRepository;
import com.project.simplegw.common.services.BasecodeService;
import com.project.simplegw.common.vos.BasecodeType;
import com.project.simplegw.common.vos.Constants;
import com.project.simplegw.common.vos.RequestResult;
import com.project.simplegw.document.entities.Document;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(rollbackFor = Exception.class, isolation = Isolation.READ_COMMITTED)
public class DayoffService extends SubListService<Dayoff, DayoffDocsDTO> {
    
    private final DayoffRepository dayoffRepo;
    private final ApprovalConverter approvalConverter;
    private final BasecodeService basecodeService;

    @Autowired
    public DayoffService(DayoffRepository dayoffRepo, ApprovalConverter approvalConverter, BasecodeService basecodeService) {
        this.dayoffRepo = dayoffRepo;
        this.approvalConverter = approvalConverter;
        this.basecodeService = basecodeService;
    }

    @Override
    public void saveEntities(List<Dayoff> entities) {
        dayoffRepo.saveAll(entities);
    }

    @Override
    public List<Dayoff> searchByDocsId(Long docsId) {
        return dayoffRepo.findAllByDocsIdOrderBySeq(docsId);
    }

    @Override
    public void deleteEntities(Long docsId) {
        dayoffRepo.deleteByDocsId(docsId);
    }

    @Override
    public RequestResult checkSubList(DayoffDocsDTO dto) {
        List<DayoffDTO> dtos = dto.getDayoffDtoList();
        int size = dtos.size();

        for(int i=0; i<size; i++) {
            DayoffDTO dayoffDTO = dtos.get(i);

            if(dayoffDTO.getCode() == null || dayoffDTO.getCode().strip().isBlank()) {
                return RequestResult.getDefaultFail((i + 1) + " 번 행의 휴가 종류를 선택하세요.");
            }

            if(dayoffDTO.getDateStart() == null || dayoffDTO.getDateEnd() == null) {
                return RequestResult.getDefaultFail((i + 1) + " 번 행의 날짜를 선택하세요.");
            }
        }

        return RequestResult.getDefaultSuccess(Constants.RESULT_MESSAGE_OK);
    }

    @Override
    public void subListSave(DayoffDocsDTO dto, Document docs) {
        deleteEntities(docs.getId());
        List<Dayoff> entities = dto.getDayoffDtoList().stream().map(approvalConverter::getEntity).collect(Collectors.toList());
        saveEntities(bindDocsToEntities(entities, docs));
    }

    @Override
    public List<DayoffDTO> getSubDtoList(Long docsId) {
        return searchByDocsId(docsId).stream().map(entity -> {
            DayoffDTO dto = approvalConverter.getDto(entity);
            return dto.setValue(basecodeService.getValue(BasecodeType.DAYOFF, dto.getCode()));
        }).collect(Collectors.toList());
    }
}
