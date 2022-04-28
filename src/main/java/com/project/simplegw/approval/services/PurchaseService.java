package com.project.simplegw.approval.services;

import java.util.List;
import java.util.stream.Collectors;

import com.project.simplegw.approval.dtos.PurchaseDTO;
import com.project.simplegw.approval.dtos.PurchaseDocsDTO;
import com.project.simplegw.approval.entities.Purchase;
import com.project.simplegw.approval.repositories.PurchaseRepository;
import com.project.simplegw.common.services.Regex;
import com.project.simplegw.common.vos.Constants;
import com.project.simplegw.common.vos.RequestResult;
import com.project.simplegw.document.entities.Document;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(rollbackFor = Exception.class, isolation = Isolation.READ_COMMITTED)
public class PurchaseService extends SubListService<Purchase, PurchaseDocsDTO> {
    
    private final PurchaseRepository purchaseRepo;
    private final ApprovalConverter approvalConverter;

    @Autowired
    public PurchaseService(PurchaseRepository purchaseRepo, ApprovalConverter approvalConverter) {
        this.purchaseRepo = purchaseRepo;
        this.approvalConverter = approvalConverter;
    }

    @Override
    public void saveEntities(List<Purchase> entities) {
        purchaseRepo.saveAll(entities);
    }

    @Override
    public List<Purchase> searchByDocsId(Long docsId) {
        return purchaseRepo.findAllByDocsIdOrderBySeq(docsId);
    }

    @Override
    public void deleteEntities(Long docsId) {
        purchaseRepo.deleteByDocsId(docsId);
    }

    @Override
    public RequestResult checkSubList(PurchaseDocsDTO dto) {
        List<PurchaseDTO> dtos = dto.getPurchaseDtoList();
        int size = dtos.size();

        for(int i=0; i<size; i++) {
            PurchaseDTO purchaseDTO = dtos.get(i);
            if(purchaseDTO.getItemName() == null || purchaseDTO.getItemName().strip().isBlank()) {
                return RequestResult.getDefaultFail((i + 1) + " 번 행의 제품명 또는 상품명을 입력하세요.");
            }
            if(purchaseDTO.getQty() == 0) {
                return RequestResult.getDefaultFail((i + 1) + " 번 행의 수량을 입력하세요.");
            }
            if(purchaseDTO.getUrl() != null && !purchaseDTO.getUrl().strip().isBlank()) {
                if(!Regex.isUrl(purchaseDTO.getUrl())) {
                    return RequestResult.getDefaultFail((i + 1) + " 번 행의 URL이 인터넷 주소형식이 아닙니다.");
                }
            }
            if(purchaseDTO.getItemName() != null) {
                purchaseDTO.setItemName(purchaseDTO.getItemName().strip());
            }
            if(purchaseDTO.getItemSpec() != null) {
                purchaseDTO.setItemSpec(purchaseDTO.getItemSpec().strip());
            }
            if(purchaseDTO.getStore() != null) {
                purchaseDTO.setStore(purchaseDTO.getStore().strip());
            }
            if(purchaseDTO.getUrl() != null) {
                purchaseDTO.setUrl(purchaseDTO.getUrl().strip());
            }
        }

        return RequestResult.getDefaultSuccess(Constants.RESULT_MESSAGE_OK);
    }

    @Override
    public void subListSave(PurchaseDocsDTO dto, Document docs) {
        deleteEntities(docs.getId());
        List<Purchase> entities = dto.getPurchaseDtoList().stream().map(approvalConverter::getEntity).collect(Collectors.toList());
        saveEntities(bindDocsToEntities(entities, docs));
    }

    @Override
    public List<PurchaseDTO> getSubDtoList(Long docsId) {
        return searchByDocsId(docsId).stream().map(approvalConverter::getDto).collect(Collectors.toList());
    }
}
