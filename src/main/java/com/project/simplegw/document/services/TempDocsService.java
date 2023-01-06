package com.project.simplegw.document.services;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import com.project.simplegw.document.dtos.receive.DtorDocs;
import com.project.simplegw.document.dtos.send.DtosDocs;
import com.project.simplegw.document.dtos.send.DtosTempDocs;
import com.project.simplegw.document.entities.TempContent;
import com.project.simplegw.document.entities.TempDocs;
import com.project.simplegw.document.helpers.DocsConverter;
import com.project.simplegw.document.repositories.TempContentRepo;
import com.project.simplegw.document.repositories.TempDocsRepo;
import com.project.simplegw.document.vos.DocsType;
import com.project.simplegw.system.security.LoginUser;
import com.project.simplegw.system.vos.Constants;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional(rollbackFor = Exception.class, isolation = Isolation.READ_COMMITTED)
public class TempDocsService {
    private final TempDocsRepo docsRepo;
    private final TempContentRepo contentRepo;
    private final DocsConverter docsConverter;

    @Autowired
    public TempDocsService(TempDocsRepo docsRepo, TempContentRepo contentRepo, DocsConverter docsConverter) {
        this.docsRepo = docsRepo;
        this.contentRepo = contentRepo;
        this.docsConverter = docsConverter;
        
        log.info("Component '" + this.getClass().getName() + "' has been created.");
    }


    // ↓ ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- temp docs ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- ↓ //
    private List<DtosTempDocs> getDtosTempDocs(List<TempDocs> entities) {
        return entities.stream().map( e -> docsConverter.getDtosTempDocs(e).setTypeTitle(e.getType().getTitle()).updateGroup() ).collect(Collectors.toList());
    }

    public List<DtosTempDocs> getTempDocs(LoginUser loginUser) {
        return getDtosTempDocs( docsRepo.findByWriterId( loginUser.getMember().getId() ) );
    }
    
    public TempDocs getTempDocsEntity(Long docsId, DocsType type) {
        return docsRepo.findByIdAndType(docsId, type).orElseGet(TempDocs::new);
    }

    @Cacheable(cacheManager = Constants.CACHE_MANAGER, cacheNames = Constants.CACHE_TEMPDOCS_COUNT, key = "#loginUser.getMember().getId()")
    public long getTempDocsCount(LoginUser loginUser) {
        return docsRepo.countByWriterId( loginUser.getMember().getId() );
    }

    public DtosDocs getDtosDocsFromTempDocs(Long docsId, DocsType type) {
        TempContent content = contentRepo.findByTempDocsId(docsId).orElseGet(TempContent::new);
        TempDocs docs = content.getTempDocs();
        return docsConverter.getDtosDocs( docs ).setContent( content.getContent() );
    }

    public boolean isOwner(TempDocs tempDocs, LoginUser loginUser) {
        return tempDocs == null ? false : tempDocs.getWriterId().equals( loginUser.getMember().getId() );
    }


    @CacheEvict(cacheManager = Constants.CACHE_MANAGER, cacheNames = Constants.CACHE_TEMPDOCS_COUNT, allEntries = false, key = "#loginUser.getMember().getId()")
    public TempDocs create(DtorDocs dto, DocsType type, LoginUser loginUser) {
        TempDocs tempDocs =  TempDocs.builder().title(dto.getTitle()).type(type).build().setWriterId(loginUser.getMember());

        try {
            TempDocs savedTempDocs = docsRepo.save(tempDocs);
            TempContent contentEntity = TempContent.builder().content(dto.getContent()).build().bindTempDocs( savedTempDocs );

            contentRepo.save(contentEntity);
            return savedTempDocs;

        } catch(Exception e) {
            e.printStackTrace();
            log.warn("createDocs exception.");
            log.warn("parameters: {}, {}, user: {}", dto.toString(), type.toString(), loginUser.getMember().getId());
            log.warn("Docs value: {}", tempDocs.toString());
            return null;
        }
    }

    public TempDocs update(Long docsId, DtorDocs dto, DocsType type) {
        Optional<TempDocs> targetDocs = docsRepo.findById(docsId);
        Optional<TempContent> targetContent = contentRepo.findByTempDocsId(docsId);

        try {
            if(targetDocs.isPresent()) {
                TempDocs tempDocs = targetDocs.get();
                tempDocs.updateTitle(dto.getTitle());
    
                TempContent tempContent = targetContent.get();
                tempContent.updateContent(dto.getContent());
    
                TempDocs tempSavedDocs = docsRepo.save(tempDocs);
                contentRepo.save( tempContent.bindTempDocs(tempSavedDocs) );
    
                return tempSavedDocs;
    
            } else {
                log.info("docs not exists.");
                log.warn("parameters: {}, {}", dto.toString(), type.toString());
                return null;
            }

        } catch(Exception e) {
            e.printStackTrace();
            log.warn("updateDocs exception.");
            log.warn("parameters: {}, {}", dto.toString(), type.toString());
            return null;
        }
    }

    @CacheEvict(cacheManager = Constants.CACHE_MANAGER, cacheNames = Constants.CACHE_TEMPDOCS_COUNT, allEntries = false, key = "#loginUser.getMember().getId()")
    public void delete(TempDocs tempDocs, LoginUser loginUser) {
        if(tempDocs == null || tempDocs.getId() == null)
            return;

        try {
            docsRepo.delete(tempDocs);

        } catch(Exception e) {
            e.printStackTrace();
            log.warn("deleteDocs exception.");
            log.warn("parameters: {}", tempDocs.toString());
        }
    }
    // ↑ ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- temp docs ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- ↑ //
}
