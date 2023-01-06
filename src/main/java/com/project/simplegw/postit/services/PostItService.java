package com.project.simplegw.postit.services;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import com.project.simplegw.member.entities.Member;
import com.project.simplegw.postit.dtos.DtoPostIt;
import com.project.simplegw.postit.entities.PostIt;
import com.project.simplegw.postit.helpers.PostItConverter;
import com.project.simplegw.postit.repositories.PostItRepo;
import com.project.simplegw.system.security.LoginUser;
import com.project.simplegw.system.vos.Constants;
import com.project.simplegw.system.vos.ServiceResult;

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
public class PostItService {
    private final PostItRepo repo;
    private final PostItConverter converter;

    @Autowired
    public PostItService(PostItRepo repo, PostItConverter converter) {
        this.repo = repo;
        this.converter = converter;
        log.info("Component '" + this.getClass().getName() + "' has been created.");
    }




    private List<PostIt> getEntities(Member member) {
        return repo.findByMemberId(member.getId());
    }

    private List<PostIt> getEntities(List<DtoPostIt> dtos) {
        return dtos.stream().map(converter::getPostIt).collect(Collectors.toList());
    }

    private List<DtoPostIt> getDtos(List<PostIt> entities) {
        return entities.stream().map(converter::getDtoPostIt).collect(Collectors.toList());
    }

    
    @Cacheable(cacheManager = Constants.CACHE_MANAGER, cacheNames = Constants.CACHE_POST_IT, key = "#loginUser.getMember().getId()")
    public List<DtoPostIt> getList(LoginUser loginUser) {
        log.info("Cacheable method 'getList()' called. user: {}", loginUser.getMember().getId());
        return getDtos(getEntities(loginUser.getMember()));
    }


    @CacheEvict(cacheManager = Constants.CACHE_MANAGER, cacheNames = Constants.CACHE_POST_IT, allEntries = false, key = "#loginUser.getMember().getId()")
    public ServiceResult save(List<DtoPostIt> dtos, LoginUser loginUser) {
        log.info("CacheEvict method 'save()' called. user: {}", loginUser.getMember().getId());

        List<PostIt> entities = getEntities(loginUser.getMember());
        entities.sort(Comparator.comparing(PostIt::getSeq));
        
        if(entities.size() > 0) {
            dtos.sort(Comparator.comparing(DtoPostIt::getSeq));

            for(PostIt e : entities) {
                e.updateTitle( dtos.get( entities.indexOf(e) ).getTitle() );
                e.updateContent( dtos.get( entities.indexOf(e) ).getContent() );
            }

        } else {
            entities = getEntities(dtos);
            entities.forEach(e -> e.bindMember(loginUser.getMember()));
        }

        repo.saveAll(entities);
        return ServiceResult.SUCCESS;
    }
}
