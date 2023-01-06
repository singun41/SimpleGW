package com.project.simplegw.document.services;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import com.project.simplegw.document.dtos.receive.DtorDocs;
import com.project.simplegw.document.dtos.send.DtosDocs;
import com.project.simplegw.document.dtos.send.DtosDocsMin;
import com.project.simplegw.document.dtos.send.DtosDocsTitle;
import com.project.simplegw.document.entities.Docs;
import com.project.simplegw.document.entities.TempDocs;
import com.project.simplegw.document.helpers.DocsConverter;
import com.project.simplegw.document.vos.DocsType;
import com.project.simplegw.system.security.LoginUser;
import com.project.simplegw.system.services.MenuAuthorityService;
import com.project.simplegw.system.services.SseDocsService;
import com.project.simplegw.system.vos.Constants;
import com.project.simplegw.system.vos.Menu;
import com.project.simplegw.system.vos.ResponseMsg;
import com.project.simplegw.system.vos.ServiceResult;
import com.project.simplegw.system.vos.ServiceMsg;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional(rollbackFor = Exception.class, isolation = Isolation.READ_COMMITTED)
public class FreeboardService {
    private static final DocsType FREEBOARD = DocsType.FREEBOARD;

    private final DocsService docsService;
    private final TempDocsService tempDocsService;
    private final DocsConverter docsConverter;
    private final MenuAuthorityService authService;

    private final SseDocsService sseDocsService;
    

    @Autowired
    public FreeboardService(DocsService docsService, TempDocsService tempDocsService, DocsConverter docsConverter, SseDocsService sseDocsService, MenuAuthorityService authService) {
        this.docsService = docsService;
        this.tempDocsService = tempDocsService;
        this.docsConverter = docsConverter;
        this.sseDocsService = sseDocsService;
        this.authService = authService;

        log.info("Component '" + this.getClass().getName() + "' has been created.");
    }



    @Cacheable(cacheManager = Constants.CACHE_MANAGER, cacheNames = Constants.CACHE_FREEBOARD)
    public List<DtosDocsTitle> getMainPageList() {
        log.info("Cacheable method 'getMainPageList()' called.");

        long limitSize = 5L;   // 메인 페이지에 띄워줄 리스트 개수: 5
        return docsService.getRecentDocs20(FREEBOARD).stream().limit(limitSize).map(e -> docsConverter.getDtosDocsTitle(e).updateIsNew()).collect(Collectors.toList());
    }

    public List<DtosDocsMin> getList(LocalDate dateFrom, LocalDate dateTo) {
        return docsService.getDocs(FREEBOARD, dateFrom, dateTo);
    }




    
    // ↓ ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- docs ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- ↓ //
    @CacheEvict(cacheManager = Constants.CACHE_MANAGER, cacheNames = Constants.CACHE_FREEBOARD, allEntries = true)
    public ServiceMsg create(DtorDocs dto, LoginUser loginUser) {
        log.info("CacheEvict method 'create()' called. user: {}", loginUser.getMember().getId());
        
        if( ! authService.isWritable(Menu.FREEBOARD, loginUser) )
            return new ServiceMsg().setResult(ServiceResult.FAILURE).setMsg( ResponseMsg.UNAUTHORIZED.getTitle() );

        Long docsId = docsService.create(dto, FREEBOARD, loginUser).getId();

        if(docsId == null){
            return new ServiceMsg().setResult(ServiceResult.FAILURE).setMsg( new StringBuilder(FREEBOARD.getTitle()).append(" 등록 에러입니다. 관리자에게 문의하세요.").toString() );
        
        } else {
            sseDocsService.sendFreeboard();
            return new ServiceMsg().setResult(ServiceResult.SUCCESS).setReturnObj(docsId);
        }
    }


    @CacheEvict(cacheManager = Constants.CACHE_MANAGER, cacheNames = Constants.CACHE_FREEBOARD, allEntries = true)
    public ServiceMsg update(Long docsId, DtorDocs dto, LoginUser loginUser) {
        log.info("CacheEvict method 'update()' called. user: {}", loginUser.getMember().getId());

        Docs docs = docsService.getDocsEntity(docsId, FREEBOARD);

        if( ! authService.isUpdatable(Menu.FREEBOARD, loginUser, docs.getWriterId()) )
            return new ServiceMsg().setResult(ServiceResult.FAILURE).setMsg(ResponseMsg.UNAUTHORIZED.getTitle());

        docsService.update(docsId, dto, FREEBOARD);
        sseDocsService.sendFreeboard();
        return new ServiceMsg().setResult(ServiceResult.SUCCESS);
    }


    @CacheEvict(cacheManager = Constants.CACHE_MANAGER, cacheNames = Constants.CACHE_FREEBOARD, allEntries = true)
    public ServiceMsg delete(Long docsId, LoginUser loginUser) {
        log.info("CacheEvict method 'delete()' called. user: {}", loginUser.getMember().getId());
        
        Docs docs = docsService.getDocsEntity(docsId, FREEBOARD);

        if( ! authService.isDeletable(Menu.FREEBOARD, loginUser, docs.getWriterId()) )
            return new ServiceMsg().setResult(ServiceResult.FAILURE).setMsg(ResponseMsg.UNAUTHORIZED.getTitle());

        if( FREEBOARD != docs.getType() )
            return new ServiceMsg().setResult(ServiceResult.FAILURE).setMsg( new StringBuilder("삭제 대상 문서가 ").append(FREEBOARD.getTitle()).append("문서가 아닙니다.").toString() );

        docsService.delete(docs);
        sseDocsService.sendFreeboard();
        return new ServiceMsg().setResult(ServiceResult.SUCCESS);
    }



    public DtosDocs getDocs(Long docsId) {
        return docsService.getDtosDocs(docsId, FREEBOARD);
    }
    // ↑ ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- docs ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- ↑ //





    // ↓ ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- temp docs ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- ↓ //
    public ServiceMsg createTemp(DtorDocs dto, LoginUser loginUser) {
        Long docsId = tempDocsService.create(dto, FREEBOARD, loginUser).getId();

        if(docsId == null)
            return new ServiceMsg().setResult(ServiceResult.FAILURE).setMsg( new StringBuilder(FREEBOARD.getTitle()).append(" 임시저장 에러입니다. 관리자에게 문의하세요.").toString() );
        
        else
            return new ServiceMsg().setResult(ServiceResult.SUCCESS).setReturnObj(docsId);
    }


    public ServiceMsg updateTemp(Long docsId, DtorDocs dto, LoginUser loginUser) {
        TempDocs tempDocs = tempDocsService.getTempDocsEntity(docsId, FREEBOARD);

        if( ! tempDocsService.isOwner(tempDocs, loginUser) )   // 임시저장 문서는 본인만 수정 가능.
            return new ServiceMsg().setResult(ServiceResult.FAILURE).setMsg(ResponseMsg.UNAUTHORIZED.getTitle());

        tempDocsService.update(docsId, dto, FREEBOARD);
        return new ServiceMsg().setResult(ServiceResult.SUCCESS);
    }


    public ServiceMsg deleteTemp(Long docsId, LoginUser loginUser) {
        TempDocs tempDocs = tempDocsService.getTempDocsEntity(docsId, FREEBOARD);

        if( ! tempDocsService.isOwner(tempDocs, loginUser) )   // 임시저장 문서는 본인만 삭제 가능.
            return new ServiceMsg().setResult(ServiceResult.FAILURE).setMsg(ResponseMsg.UNAUTHORIZED.getTitle());

        if( FREEBOARD != tempDocs.getType() )
            return new ServiceMsg().setResult(ServiceResult.FAILURE).setMsg( new StringBuilder("삭제 대상 문서가 ").append(FREEBOARD.getTitle()).append("문서가 아닙니다.").toString() );

        tempDocsService.delete(tempDocs, loginUser);
        return new ServiceMsg().setResult(ServiceResult.SUCCESS);
    }

    

    public DtosDocs getTempDocs(Long docsId) {
        return tempDocsService.getDtosDocsFromTempDocs(docsId, FREEBOARD);
    }
    // ↑ ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- temp docs ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- ↑ //
}
