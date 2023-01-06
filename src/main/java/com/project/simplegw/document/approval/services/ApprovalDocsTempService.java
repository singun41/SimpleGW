package com.project.simplegw.document.approval.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import com.project.simplegw.document.approval.dtos.receive.DtorTempDefaultReport;
import com.project.simplegw.document.dtos.receive.DtorDocs;
import com.project.simplegw.document.dtos.send.DtosDocs;
import com.project.simplegw.document.entities.TempDocs;
import com.project.simplegw.document.services.TempDocsService;
import com.project.simplegw.document.vos.DocsType;
import com.project.simplegw.system.security.LoginUser;
import com.project.simplegw.system.vos.ResponseMsg;
import com.project.simplegw.system.vos.ServiceMsg;
import com.project.simplegw.system.vos.ServiceResult;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional(rollbackFor = Exception.class, isolation = Isolation.READ_COMMITTED)
public class ApprovalDocsTempService {   // 모든 결재문서의 임시저장시 공통 사용 서비스.
    private final TempDocsService tempDocsService;

    @Autowired
    public ApprovalDocsTempService(TempDocsService tempDocsService) {
        this.tempDocsService = tempDocsService;
        log.info("Component '" + this.getClass().getName() + "' has been created.");
    }


    public TempDocs getTempDocsEntity(Long docsId, DocsType type) {
        return tempDocsService.getTempDocsEntity(docsId, type);
    }


    public ServiceMsg createTemp(DocsType type, DtorTempDefaultReport dto, LoginUser loginUser) {
        TempDocs docs = tempDocsService.create(new DtorDocs().setTitle(dto.getTitle()).setContent(dto.getContent()), type, loginUser);

        if(docs == null)
            return new ServiceMsg().setResult(ServiceResult.FAILURE).setMsg( new StringBuilder(type.getTitle()).append(" 임시저장 에러입니다. 관리자에게 문의하세요.").toString() );
        
        else
            return new ServiceMsg().setResult(ServiceResult.SUCCESS).setReturnObj(docs.getId());
    }


    public ServiceMsg updateTemp(DocsType type, Long docsId, DtorTempDefaultReport dto, LoginUser loginUser) {
        TempDocs tempDocs = getTempDocsEntity(docsId, type);

        if(tempDocs == null)
            return new ServiceMsg().setResult(ServiceResult.FAILURE).setMsg("임시문서가 없습니다. 관리자에게 문의하세요.");

        if( ! tempDocsService.isOwner(tempDocs, loginUser) )   // 수정은 본인만 가능.
            return new ServiceMsg().setResult(ServiceResult.FAILURE).setMsg(ResponseMsg.UNAUTHORIZED.getTitle());

        TempDocs updatedDocs = tempDocsService.update(docsId, new DtorDocs().setTitle(dto.getTitle()).setContent(dto.getContent()), type);
        return new ServiceMsg().setResult(ServiceResult.SUCCESS).setReturnObj(updatedDocs);
    }


    public ServiceMsg deleteTemp(DocsType type, Long docsId, LoginUser loginUser) {
        TempDocs tempDocs = getTempDocsEntity(docsId, type);

        if(tempDocs == null)
            return new ServiceMsg().setResult(ServiceResult.FAILURE).setMsg("이미 삭제된 임시문서입니다.");

        if( type != tempDocs.getType() )
            return new ServiceMsg().setResult(ServiceResult.FAILURE).setMsg( new StringBuilder("삭제 대상 문서가 ").append(type.getTitle()).append("문서가 아닙니다.").toString() );


        if( tempDocsService.isOwner(tempDocs, loginUser) ) {
            tempDocsService.delete(tempDocs, loginUser);
            return new ServiceMsg().setResult(ServiceResult.SUCCESS);
            
        } else {
            return new ServiceMsg().setResult(ServiceResult.FAILURE).setMsg(ResponseMsg.UNAUTHORIZED.getTitle());
        }
    }


    public DtosDocs getTemp(DocsType docsType, Long docsId) {
        return tempDocsService.getDtosDocsFromTempDocs(docsId, docsType);
    }
}
