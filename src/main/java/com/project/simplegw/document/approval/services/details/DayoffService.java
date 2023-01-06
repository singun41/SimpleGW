package com.project.simplegw.document.approval.services.details;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import com.project.simplegw.code.services.BasecodeService;
import com.project.simplegw.document.approval.dtos.receive.details.dayoff.DtorDayoff;
import com.project.simplegw.document.approval.dtos.receive.details.dayoff.DtorTempDayoff;
import com.project.simplegw.document.approval.dtos.send.details.dayoff.DtosDayoff;
import com.project.simplegw.document.approval.dtos.send.details.dayoff.DtosDayoffDetails;
import com.project.simplegw.document.approval.dtos.send.details.dayoff.DtosTempDayoff;
import com.project.simplegw.document.approval.entities.details.dayoff.Dayoff;
import com.project.simplegw.document.approval.entities.details.dayoff.TempDayoff;
import com.project.simplegw.document.approval.helpers.details.DayoffConverter;
import com.project.simplegw.document.approval.repositories.details.dayoff.DayoffRepo;
import com.project.simplegw.document.approval.repositories.details.dayoff.TempDayoffRepo;
import com.project.simplegw.document.approval.services.ApprovalDocsService;
import com.project.simplegw.document.approval.services.ApprovalDocsTempService;
import com.project.simplegw.document.entities.Docs;
import com.project.simplegw.document.entities.TempDocs;
import com.project.simplegw.document.vos.DocsType;
import com.project.simplegw.system.security.LoginUser;
import com.project.simplegw.system.vos.ServiceMsg;
import com.project.simplegw.system.vos.ServiceResult;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional(rollbackFor = Exception.class, isolation = Isolation.READ_COMMITTED)
public class DayoffService {
    private final DayoffRepo repo;
    private final DayoffConverter converter;
    private final ApprovalDocsService approvalDocsService;
    private final BasecodeService basecodeService;
    private final TempDayoffRepo tempRepo;
    private final ApprovalDocsTempService tempService;

    private final DocsType DAYOFF = DocsType.DAYOFF;

    @Autowired
    public DayoffService(
        DayoffRepo repo, DayoffConverter converter, ApprovalDocsService approvalDocsService, BasecodeService basecodeService,
        TempDayoffRepo tempRepo, ApprovalDocsTempService tempService
    ) {
        this.repo = repo;
        this.converter = converter;
        this.approvalDocsService = approvalDocsService;
        this.basecodeService = basecodeService;
        this.tempRepo = tempRepo;
        this.tempService = tempService;

        log.info("Component '" + this.getClass().getName() + "' has been created.");
    }



    private List<Dayoff> createEntities(DtorDayoff dto, Docs docs) {
        return dto.getDetails().stream().map(e -> converter.getEntity(e).bindDocs(docs).updateSeq(dto.getDetails().indexOf(e)).updateDuration()).collect(Collectors.toList());
    }
    

    
    public ServiceMsg create(DtorDayoff dto, LoginUser loginUser) {
        ServiceMsg result = approvalDocsService.create(converter.getDtorDocs(dto), DAYOFF, dto.getArrApproverId(), dto.getArrReferrerId(), loginUser);
        
        if(result.getResult() == ServiceResult.SUCCESS) {
            Long docsId = (Long) result.getReturnObj();
            Docs docs = approvalDocsService.getDocsEntity(docsId, DAYOFF);

            try {
                repo.saveAll( createEntities(dto, docs) );
                return new ServiceMsg().setResult(ServiceResult.SUCCESS).setReturnObj(docsId);

            } catch(Exception e) {
                e.printStackTrace();
                log.warn("create dayoff details exception.");
                log.warn("parameters: {}, user: {}", dto.toString(), loginUser.getMember().getId());

                return new ServiceMsg().setResult(ServiceResult.FAILURE).setMsg( new StringBuilder(DAYOFF.getTitle()).append(" 디테일 등록 에러입니다. 관리자에게 문의하세요.").toString() );
            }

        } else {
            return result;
        }
    }


    public ServiceMsg update(Long docsId, DtorDayoff dto, LoginUser loginUser) {
        ServiceMsg result = approvalDocsService.update(docsId, converter.getDtorDocs(dto), DAYOFF, dto.getArrApproverId(), dto.getArrReferrerId(), loginUser);

        if(result.getResult() == ServiceResult.SUCCESS) {
            try {
                List<Dayoff> oldEntities = repo.findByDocsId(docsId);
                repo.deleteAllInBatch(oldEntities);
                repo.saveAll( createEntities(dto, approvalDocsService.getDocsEntity(docsId, DAYOFF)) );

                return new ServiceMsg().setResult(ServiceResult.SUCCESS).setReturnObj(docsId);

            } catch(Exception e) {
                e.printStackTrace();
                log.warn("update dayoff details exception.");
                log.warn("parameters: {}, user: {}", dto.toString(), loginUser.getMember().getId());

                return new ServiceMsg().setResult(ServiceResult.FAILURE).setMsg( new StringBuilder(DAYOFF.getTitle()).append(" 디테일 수정 에러입니다. 관리자에게 문의하세요.").toString() );
            }
        } else {
            return result;
        }
    }
    
    
    
    // docs가 삭제되면 cascade로 dayoff entity들은 자동 삭제되므로 delete 메서드는 필요하지 않음.


    public DtosDayoff getDocs(Long docsId, LoginUser loginUser) {
        DtosDayoff docs = converter.getDocs( approvalDocsService.getDocs(docsId, DAYOFF, loginUser) );
        return docs.setDetails(getDetails(docsId));
    }

    public List<DtosDayoffDetails> getDetails(Long docsId) {   // 문서 수정시 컨트롤러에서 디테일만 별도 호출하기 위해 public으로 선언.
        return repo.findByDocsId(docsId).stream().map(
            e -> converter.getDetails(e).setValue(
                basecodeService.getDayoffCodes().stream().filter( code -> e.getCode().equals(code.getKey()) ).findFirst().get().getValue()
            )
        ).collect(Collectors.toList());
    }



    
    // ↓ ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- temp ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- ↓ //
    public DtosTempDayoff getTempDocs(Long docsId) {
        DtosTempDayoff docs = converter.getDtosTempDayoff( tempService.getTemp(DAYOFF, docsId) );
        return docs.setDetails(getTempDetails(docsId));
    }

    public List<DtosDayoffDetails> getTempDetails(Long docsId) {   // 문서 수정시 컨트롤러에서 디테일만 별도 호출하기 위해 public으로 선언.
        return tempRepo.findByDocsId(docsId).stream().map(
            e -> converter.getDetails(e).setValue(
                basecodeService.getDayoffCodes().stream().filter( code -> e.getCode().equals(code.getKey()) ).findFirst().get().getValue()
            )
        ).collect(Collectors.toList());
    }


    private List<TempDayoff> createTempEntities(DtorTempDayoff dto, TempDocs docs) {
        return dto.getDetails().stream().map(e -> converter.getTempEntity(e).bindDocs(docs).updateSeq(dto.getDetails().indexOf(e)).updateDuration()).collect(Collectors.toList());
    }

    public ServiceMsg createTemp(DtorTempDayoff dto, LoginUser loginUser) {
        ServiceMsg msg = tempService.createTemp(DAYOFF, dto, loginUser);
        
        if(msg.getResult() == ServiceResult.SUCCESS) {
            Long docsId = (Long) msg.getReturnObj();
            TempDocs docs = tempService.getTempDocsEntity(docsId, DAYOFF);

            try {
                tempRepo.saveAll( createTempEntities(dto, docs) );
                return new ServiceMsg().setResult(ServiceResult.SUCCESS).setReturnObj(docsId);

            } catch (Exception e) {
                e.printStackTrace();
                log.warn("create temp dayoff details exception.");
                log.warn("parameters: {}, user: {}", dto.toString(), loginUser.getMember().getId());

                return new ServiceMsg().setResult(ServiceResult.FAILURE).setMsg( new StringBuilder(DAYOFF.getTitle()).append(" 디테일 임시저장 에러입니다. 관리자에게 문의하세요.").toString() );
            }

        } else {
            return msg;
        }
    }


    public ServiceMsg updateTemp(Long docsId, DtorTempDayoff dto, LoginUser loginUser) {
        ServiceMsg msg = tempService.updateTemp(DAYOFF, docsId, dto, loginUser);

        if(msg.getResult() == ServiceResult.SUCCESS) {
            TempDocs docs = (TempDocs) msg.getReturnObj();

            try {
                tempRepo.deleteAllInBatch( tempRepo.findByDocsId(docs.getId()) );
                tempRepo.saveAll( createTempEntities(dto, docs) );
                return new ServiceMsg().setResult(ServiceResult.SUCCESS);

            } catch (Exception e) {
                e.printStackTrace();
                log.warn("create temp dayoff details exception.");
                log.warn("parameters: {}, user: {}", dto.toString(), loginUser.getMember().getId());

                return new ServiceMsg().setResult(ServiceResult.FAILURE).setMsg( new StringBuilder(DAYOFF.getTitle()).append(" 임시 저장문서 수정 에러입니다. 관리자에게 문의하세요.").toString() );
            }

        } else {
            return msg;
        }
    }


    public ServiceMsg deleteTemp(Long docsId, LoginUser loginUser) {
        // TempDocs가 삭제되면 디테일은 cascade로 자동 삭제된다.
        return tempService.deleteTemp(DAYOFF, docsId, loginUser);
    }
    // ↑ ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- temp ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- ↑ //
}
