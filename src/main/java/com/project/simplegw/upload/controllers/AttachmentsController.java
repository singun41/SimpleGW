package com.project.simplegw.upload.controllers;

import java.nio.charset.StandardCharsets;

import com.project.simplegw.system.helpers.ResponseConverter;
import com.project.simplegw.system.vos.ResponseMsg;
import com.project.simplegw.system.vos.ServiceMsg;
import com.project.simplegw.system.vos.ServiceResult;
import com.project.simplegw.upload.services.AttachmentsService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;         
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/attachments")
public class AttachmentsController {
    private final AttachmentsService attachmentsService;

    @Autowired
    public AttachmentsController(AttachmentsService attachmentsService) {
        this.attachmentsService = attachmentsService;
        log.info("Component '" + this.getClass().getName() + "' has been created.");
    }



    @PostMapping("/{docsId}")
    public ResponseEntity<Object> upload(@PathVariable Long docsId, MultipartHttpServletRequest req) {
        return ResponseConverter.message(
            attachmentsService.upload(docsId, req), ResponseMsg.INSERTED
        );
    }



    @GetMapping("/{docsId}/{seq}/{conversionName}")
    public ResponseEntity<Object> download(@PathVariable Long docsId, @PathVariable int seq, @PathVariable String conversionName) {
        ServiceMsg serviceMsg = attachmentsService.download(docsId, seq, conversionName);

        if(serviceMsg.getResult() == ServiceResult.SUCCESS) {
            Resource fileStream = (Resource) serviceMsg.getReturnObj();

            // attachmentsService.download 로 호출한 엔티티가 1차 캐시에 남아있기 때문에 셀렉트 쿼리는 총 1번만 발생한다.
            String originalName = attachmentsService.getEntity(docsId, seq, conversionName).getOriginalName();

            ContentDisposition contentDisposition = ContentDisposition.builder("attachments").filename(originalName, StandardCharsets.UTF_8).build();

            return ResponseEntity.ok().contentType(MediaType.parseMediaType("application/octet-stream")).header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition.toString()).body(fileStream);
        
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(serviceMsg.getMsg());
        }
    }



    @DeleteMapping("/{docsId}/{seq}/{conversionName}")
    public ResponseEntity<Object> delete(@PathVariable Long docsId, @PathVariable int seq, @PathVariable String conversionName) {
        return ResponseConverter.message(
            attachmentsService.delete(docsId, seq, conversionName), ResponseMsg.DELETED
        );
    }
}
