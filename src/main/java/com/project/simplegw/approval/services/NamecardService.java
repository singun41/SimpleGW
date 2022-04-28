package com.project.simplegw.approval.services;

import java.util.List;
import java.util.stream.Collectors;

import com.project.simplegw.approval.dtos.NamecardDTO;
import com.project.simplegw.approval.dtos.NamecardDocsDTO;
import com.project.simplegw.approval.entities.Namecard;
import com.project.simplegw.approval.repositories.NamecardRepository;
import com.project.simplegw.common.vos.Constants;
import com.project.simplegw.common.vos.RequestResult;
import com.project.simplegw.document.entities.Document;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(rollbackFor = Exception.class, isolation = Isolation.READ_COMMITTED)
public class NamecardService extends SubListService<Namecard, NamecardDocsDTO> {
    
    private final NamecardRepository namecardRepo;
    private final ApprovalConverter approvalConverter;

    @Autowired
    public NamecardService(NamecardRepository namecardRepo, ApprovalConverter approvalConverter) {
        this.namecardRepo = namecardRepo;
        this.approvalConverter = approvalConverter;
    }

    @Override
    public void saveEntities(List<Namecard> entities) {
        namecardRepo.saveAll(entities);
    }

    @Override
    public List<Namecard> searchByDocsId(Long docsId) {
        return namecardRepo.findAllByDocsId(docsId);
    }

    @Override
    public void deleteEntities(Long docsId) {
        namecardRepo.deleteByDocsId(docsId);
    }

    @Override
    public RequestResult checkSubList(NamecardDocsDTO dto) {
        List<NamecardDTO> dtos = dto.getNamecardDtoList();
        int size = dtos.size();

        for(int i=0; i<size; i++) {
            NamecardDTO namecardDTO = dtos.get(i);

            if(namecardDTO.getTeam() == null) {
                return RequestResult.getDefaultFail("부서명을 입력하세요.");
            }
            if(namecardDTO.getJobTitle() == null) {
                return RequestResult.getDefaultFail("직위를 입력하세요.");
            }
            if(namecardDTO.getName() == null) {
                return RequestResult.getDefaultFail("이름을 입력하세요.");
            }
            if(namecardDTO.getNameEng() == null) {
                return RequestResult.getDefaultFail("영문이름을 입력하세요.");
            }
            if(namecardDTO.getMailAddress() == null) {
                return RequestResult.getDefaultFail("메일 주소를 입력하세요.");
            }
            if(namecardDTO.getTel() == null) {
                return RequestResult.getDefaultFail("전화번호를 입력하세요.");
            }
            if(namecardDTO.getMobileNo() == null) {
                return RequestResult.getDefaultFail("모바일 번호를 입력하세요.");
            }
            if(!NamecardDTO.isValidMobileNo(namecardDTO.getMobileNo())) {
                return RequestResult.getDefaultFail("모바일 번호를 000-0000-0000 형식으로 입력하세요.");
            }
            if(!NamecardDTO.isValidMailAddress(namecardDTO.getMailAddress())) {
                return RequestResult.getDefaultFail("메일주소를 올바르게 입력하세요.");
            }
            if(!NamecardDTO.isValidTel(namecardDTO.getTel())) {
                return RequestResult.getDefaultFail("전화번호를 올바르게 입력하세요.");
            }
        }

        return RequestResult.getDefaultSuccess(Constants.RESULT_MESSAGE_OK);
    }

    @Override
    public void subListSave(NamecardDocsDTO dto, Document docs) {
        deleteEntities(docs.getId());

        List<Namecard> entities = dto.getNamecardDtoList().stream().map(approvalConverter::getEntity).collect(Collectors.toList());

        saveEntities(bindDocsToEntities(entities, docs));
    }

    @Override
    public List<NamecardDTO> getSubDtoList(Long docsId) {
        return searchByDocsId(docsId).stream().map(approvalConverter::getDto).collect(Collectors.toList());
    }
}
