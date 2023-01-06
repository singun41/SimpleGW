package com.project.simplegw.document.services;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.project.simplegw.document.dtos.admin.receive.DtorEditorForm;
import com.project.simplegw.document.vos.EditorDocs;
import com.project.simplegw.system.vos.Constants;
import com.project.simplegw.system.vos.ServiceMsg;
import com.project.simplegw.system.vos.ServiceResult;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class DocsFormService {
    private final Path formPath = Paths.get(Constants.EDITOR_FORMS_PATH);

    public DocsFormService() {
        defaultSetting();
        log.info("Component '" + this.getClass().getName() + "' has been created.");
    }




    private String getFormPath(EditorDocs docs) {
        return new StringBuilder(formPath.toString()).append(File.separator).append(docs.name()).append(".txt").toString();
    }


    private void defaultSetting() {
        try {
            if( ! Files.exists(formPath) )
                Files.createDirectories(formPath);
        
            for(EditorDocs docs : EditorDocs.values()) {
                Path formFile = Paths.get( getFormPath(docs) );

                if( ! Files.exists(formFile) )
                    Files.writeString(Files.createFile(formFile), "", StandardOpenOption.CREATE );
            }

        } catch(Exception e) {
            e.printStackTrace();
            log.warn("Editor default form file creation exception.");
        }
    }



    @Cacheable(cacheManager = Constants.CACHE_MANAGER, cacheNames = Constants.CACHE_EDITOR_FORMS, key = "#docs.name()")
    public String getForm(EditorDocs docs) {
        log.info("Cacheable method 'getForm()' called. docs type: {}", docs.name());

        try {
            return Files.readString( Paths.get( getFormPath(docs) ) );

        } catch(Exception e) {
            e.printStackTrace();
            log.warn("form {}.txt file load exception.", docs.name());
            return null;
        }
    }


    @CacheEvict(cacheManager = Constants.CACHE_MANAGER, cacheNames = Constants.CACHE_EDITOR_FORMS, allEntries = false, key = "#docs.name()")
    public ServiceMsg saveForm(EditorDocs docs, DtorEditorForm form) {
        log.info("CacheEvict method 'saveForm()' called. docs type: {}", docs.name());

        try {
            Files.writeString( Paths.get( getFormPath(docs) ), form.getContent(), StandardOpenOption.TRUNCATE_EXISTING );
            return new ServiceMsg().setResult(ServiceResult.SUCCESS);

        } catch(Exception e) {
            e.printStackTrace();
            log.warn("form {}.txt file save exception.", docs.name());
            return new ServiceMsg().setResult(ServiceResult.FAILURE).setMsg("양식 저장 오류입니다. 로그를 확인하세요.");
        }
    }
}
