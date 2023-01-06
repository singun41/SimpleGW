package com.project.simplegw.document.services;

import java.time.LocalDate;
import java.util.List;

import com.project.simplegw.document.dtos.receive.DtorDocs;
import com.project.simplegw.document.dtos.send.DtosDocs;
import com.project.simplegw.document.dtos.send.DtosDocsMin;
import com.project.simplegw.document.entities.Docs;
import com.project.simplegw.document.vos.DocsType;
import com.project.simplegw.system.security.LoginUser;
import com.project.simplegw.system.services.MenuAuthorityService;
import com.project.simplegw.system.vos.Menu;
import com.project.simplegw.system.vos.ResponseMsg;
import com.project.simplegw.system.vos.ServiceMsg;
import com.project.simplegw.system.vos.ServiceResult;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional(rollbackFor = Exception.class, isolation = Isolation.READ_COMMITTED)
public class ArchiveService {
    private static final DocsType ARCHIVE = DocsType.ARCHIVE;

    private final DocsService docsService;
    private final MenuAuthorityService authService;
    

    @Autowired
    public ArchiveService(DocsService docsService, MenuAuthorityService authService) {
        this.docsService = docsService;
        this.authService = authService;
        log.info("Component '" + this.getClass().getName() + "' has been created.");
    }


    public List<DtosDocsMin> getList(LocalDate dateFrom, LocalDate dateTo) {
        return docsService.getDocs(ARCHIVE, dateFrom, dateTo);
    }

    


    
    // ↓ ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- docs ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- ↓ //
    public ServiceMsg create(DtorDocs dto, LoginUser loginUser) {
        if( ! authService.isWritable(Menu.ARCHIVE, loginUser) )
            return new ServiceMsg().setResult(ServiceResult.FAILURE).setMsg( ResponseMsg.UNAUTHORIZED.getTitle() );
        
        Long docsId = docsService.create(dto, ARCHIVE, loginUser).getId();

        if(docsId == null)
            return new ServiceMsg().setResult(ServiceResult.FAILURE).setMsg( new StringBuilder(ARCHIVE.getTitle()).append(" 등록 에러입니다. 관리자에게 문의하세요.").toString() );
        
        else
            return new ServiceMsg().setResult(ServiceResult.SUCCESS).setReturnObj(docsId);
    }


    public ServiceMsg update(Long docsId, DtorDocs dto, LoginUser loginUser) {
        Docs docs = docsService.getDocsEntity(docsId, ARCHIVE);

        if( ! authService.isUpdatable(Menu.ARCHIVE, loginUser, docs.getWriterId()) )
            return new ServiceMsg().setResult(ServiceResult.FAILURE).setMsg(ResponseMsg.UNAUTHORIZED.getTitle());

        docsService.update(docsId, dto, ARCHIVE);
        return new ServiceMsg().setResult(ServiceResult.SUCCESS);
    }


    public ServiceMsg delete(Long docsId, LoginUser loginUser) {
        Docs docs = docsService.getDocsEntity(docsId, ARCHIVE);

        if( ! authService.isDeletable(Menu.SUGGESTION, loginUser, docs.getWriterId()))
            return new ServiceMsg().setResult(ServiceResult.FAILURE).setMsg(ResponseMsg.UNAUTHORIZED.getTitle());

        if( ARCHIVE != docs.getType() )
            return new ServiceMsg().setResult(ServiceResult.FAILURE).setMsg( new StringBuilder("삭제 대상 문서가 ").append(ARCHIVE.getTitle()).append("문서가 아닙니다.").toString() );

        docsService.delete(docs);
        return new ServiceMsg().setResult(ServiceResult.SUCCESS);
    }



    public DtosDocs getDocs(Long docsId) {
        return docsService.getDtosDocs(docsId, ARCHIVE);
    }
    // ↑ ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- docs ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- ↑ //




    // ↓ ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- temp docs ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- ↓ //
    // Archive(자료실)은 임시저장 기능을 제공하지 않는다.
    // ↑ ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- temp docs ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- ↑ //
}
