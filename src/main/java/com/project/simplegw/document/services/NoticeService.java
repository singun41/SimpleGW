package com.project.simplegw.document.services;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.project.simplegw.document.dtos.receive.DtorDocs;
import com.project.simplegw.document.dtos.receive.DtorDocsOptions;
import com.project.simplegw.document.dtos.send.DtosDocs;
import com.project.simplegw.document.dtos.send.DtosDocsMin;
import com.project.simplegw.document.dtos.send.DtosDocsOptions;
import com.project.simplegw.document.dtos.send.DtosDocsTitle;
import com.project.simplegw.document.entities.Docs;
import com.project.simplegw.document.entities.DocsOptions;
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
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional(rollbackFor = Exception.class, isolation = Isolation.READ_COMMITTED)
public class NoticeService {
    private static final DocsType NOTICE = DocsType.NOTICE;

    private final DocsService docsService;
    private final TempDocsService tempDocsService;
    private final DocsConverter docsConverter;
    private final MenuAuthorityService authService;
    
    private final SseDocsService sseDocsService;
    

    @Autowired
    public NoticeService(DocsService docsService, TempDocsService tempDocsService, DocsConverter docsConverter, SseDocsService sseDocsService, MenuAuthorityService authService) {
        this.docsService = docsService;
        this.tempDocsService = tempDocsService;
        this.docsConverter = docsConverter;
        this.sseDocsService = sseDocsService;
        this.authService = authService;

        log.info("Component '" + this.getClass().getName() + "' has been created.");
    }


    
    @Cacheable(cacheManager = Constants.CACHE_MANAGER, cacheNames = Constants.CACHE_NOTICE)
    public List<DtosDocsTitle> getMainPageList() {
        log.info("Cacheable method 'getMainPageList()' called.");

        long limitSize = 9L;   // 메인 페이지에 띄워줄 리스트 개수: 9

        List<Docs> docslist = docsService.getRecentDocs20(NOTICE);
        List<DocsOptions> optionList = docsService.getOptions();   // 옵션 설정은 극히 적으므로 단순하게 전체 조회해서 올리고 아래 for문에서 찾는다. option entity를 찾는 쿼리를 여러번 날리지 않기 위함.
        
        // DocsOptions 게시 일자 설정된 경우는 제외한다.
        return docslist.stream().filter(e -> {
            Optional<DocsOptions> target = optionList.stream().filter(opt -> opt.getDocs().equals(e)).findAny();

            if(target.isPresent()) {
                DocsOptions options = target.get();

                // predicate가 true를 집계하므로 not(!)을 써야 원하는 결과만 추출할 수 있다.
                // 게시 기한이 어제였으면 오늘은 isAfter가 true이고 그러면 집계하지 않아야 하므로 false 처리를 위해 not(!)을 써준다.
                return options.getDueDate() == null ? true : ! LocalDate.now().isAfter(options.getDueDate());
            }
            return true;

        }).limit(limitSize).map(e -> docsConverter.getDtosDocsTitle(e).updateIsNew()).collect(Collectors.toList());
    }

    public List<DtosDocsMin> getList(LocalDate dateFrom, LocalDate dateTo) {
        return docsService.getDocs(NOTICE, dateFrom, dateTo);
    }




    
    // ↓ ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- docs ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- ↓ //
    @CacheEvict(cacheManager = Constants.CACHE_MANAGER, cacheNames = Constants.CACHE_NOTICE, allEntries = true)
    public ServiceMsg create(DtorDocs dto, LoginUser loginUser) {
        log.info("CacheEvict method 'create()' called. user: {}", loginUser.getMember().getId());

        if( ! authService.isWritable(Menu.NOTICE, loginUser) )
            return new ServiceMsg().setResult(ServiceResult.FAILURE).setMsg( ResponseMsg.UNAUTHORIZED.getTitle() );

        Long docsId = docsService.create(dto, NOTICE, loginUser).getId();

        if(docsId == null) {
            return new ServiceMsg().setResult(ServiceResult.FAILURE).setMsg( new StringBuilder(NOTICE.getTitle()).append(" 등록 에러입니다. 관리자에게 문의하세요.").toString() );
        
        } else {
            sseDocsService.sendNotice();
            return new ServiceMsg().setResult(ServiceResult.SUCCESS).setReturnObj(docsId);
        }
    }


    @CacheEvict(cacheManager = Constants.CACHE_MANAGER, cacheNames = Constants.CACHE_NOTICE, allEntries = true)
    public ServiceMsg update(Long docsId, DtorDocs dto, LoginUser loginUser) {
        log.info("CacheEvict method 'update()' called. user: {}", loginUser.getMember().getId());

        Docs docs = docsService.getDocsEntity(docsId, NOTICE);

        if( ! authService.isUpdatable(Menu.NOTICE, loginUser, docs.getWriterId()) )
            return new ServiceMsg().setResult(ServiceResult.FAILURE).setMsg( ResponseMsg.UNAUTHORIZED.getTitle() );

        docsService.update(docsId, dto, NOTICE);
        sseDocsService.sendNotice();
        return new ServiceMsg().setResult(ServiceResult.SUCCESS);
    }


    @CacheEvict(cacheManager = Constants.CACHE_MANAGER, cacheNames = Constants.CACHE_NOTICE, allEntries = true)
    public ServiceMsg delete(Long docsId, LoginUser loginUser) {
        log.info("CacheEvict method 'delete()' called. user: {}", loginUser.getMember().getId());
        
        Docs docs = docsService.getDocsEntity(docsId, NOTICE);

        if( ! authService.isDeletable(Menu.NOTICE, loginUser, docs.getWriterId()) )
            return new ServiceMsg().setResult(ServiceResult.FAILURE).setMsg( ResponseMsg.UNAUTHORIZED.getTitle() );

        if( NOTICE != docs.getType() )
            return new ServiceMsg().setResult(ServiceResult.FAILURE).setMsg( new StringBuilder("삭제 대상 문서가 ").append(NOTICE.getTitle()).append("문서가 아닙니다.").toString() );

        docsService.delete(docs);
        sseDocsService.sendNotice();
        return new ServiceMsg().setResult(ServiceResult.SUCCESS);
    }



    public DtosDocs getDocs(Long docsId) {
        return docsService.getDtosDocs(docsId, NOTICE);
    }
    // ↑ ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- docs ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- ↑ //




    
    // ↓ ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- docs options ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- ↓ //
    @Async
    public void updateOptions(Long docsId, DtorDocsOptions dto) {
        docsService.updateOptions( docsId, dto );
    }

    public DtosDocsOptions getOptions(Long docsId) {
        return docsService.getOptions(docsId);
    }
    // ↑ ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- docs options ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- ↑ //





    // ↓ ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- temp docs ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- ↓ //
    public ServiceMsg createTemp(DtorDocs dto, LoginUser loginUser) {
        Long docsId = tempDocsService.create(dto, NOTICE, loginUser).getId();

        if(docsId == null)
            return new ServiceMsg().setResult(ServiceResult.FAILURE).setMsg( new StringBuilder(NOTICE.getTitle()).append(" 임시저장 에러입니다. 관리자에게 문의하세요.").toString() );
        
        else
            return new ServiceMsg().setResult(ServiceResult.SUCCESS).setReturnObj(docsId);
    }
    

    public ServiceMsg updateTemp(Long docsId, DtorDocs dto, LoginUser loginUser) {
        TempDocs tempDocs = tempDocsService.getTempDocsEntity(docsId, NOTICE);

        if( ! tempDocsService.isOwner(tempDocs, loginUser) )   // 임시저장 문서는 본인만 수정 가능.
            return new ServiceMsg().setResult(ServiceResult.FAILURE).setMsg(ResponseMsg.UNAUTHORIZED.getTitle());

        tempDocsService.update(docsId, dto, NOTICE);
        return new ServiceMsg().setResult(ServiceResult.SUCCESS);
    }


    public ServiceMsg deleteTemp(Long docsId, LoginUser loginUser) {
        TempDocs tempDocs = tempDocsService.getTempDocsEntity(docsId, NOTICE);

        if( ! tempDocsService.isOwner(tempDocs, loginUser) )   // 임시저장 문서는 본인만 삭제 가능.
            return new ServiceMsg().setResult(ServiceResult.FAILURE).setMsg(ResponseMsg.UNAUTHORIZED.getTitle());

        if( NOTICE != tempDocs.getType() )
            return new ServiceMsg().setResult(ServiceResult.FAILURE).setMsg( new StringBuilder("삭제 대상 문서가 ").append(NOTICE.getTitle()).append("문서가 아닙니다.").toString() );

        tempDocsService.delete(tempDocs, loginUser);
        return new ServiceMsg().setResult(ServiceResult.SUCCESS);
    }



    public DtosDocs getTempDocs(Long docsId) {
        return tempDocsService.getDtosDocsFromTempDocs(docsId, NOTICE);
    }
    // ↑ ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- temp docs ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- ↑ //
}
