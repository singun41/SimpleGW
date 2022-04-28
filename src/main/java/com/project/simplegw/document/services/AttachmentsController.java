package com.project.simplegw.document.services;

import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.project.simplegw.document.dtos.AttachmentsDTO;
import com.project.simplegw.system.security.SecurityUser;
import com.project.simplegw.system.services.ResponseEntityConverter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

@RestController
public class AttachmentsController {
    
    private final AttachmentsService attachmentsService;

    @Autowired
    public AttachmentsController(AttachmentsService attachmentsService) {
        this.attachmentsService = attachmentsService;
    }

    @PostMapping("/attachments/images")
    public HashMap<String, Object> uploadImages(HttpServletRequest request, HttpServletResponse response, @RequestParam("upload") MultipartFile imgFile) {
        return attachmentsService.uploadImages(request, response, imgFile);
    }

    @GetMapping(AttachmentsService.ATTACHMENTS_IMAGE_URL + "{pathYear}/{pathMonth}/{pathDay}/{conversionName}")
    public byte[] getImages(@PathVariable String pathYear, @PathVariable String pathMonth, @PathVariable String pathDay, @PathVariable String conversionName) {
        return attachmentsService.getImgByteStream(pathYear, pathMonth, pathDay, conversionName);
    }

    @PostMapping("/attachments/files")
    public ResponseEntity<Object> uploadFiles(MultipartHttpServletRequest request, HttpServletResponse response) {
        return ResponseEntityConverter.getFromRequestResult(attachmentsService.uploadFiles(request, response));
    }

    @GetMapping("/attachments/{docsId}/{seq}/{conversionName}")
    public ResponseEntity<Resource> downloadFile(@PathVariable(value = "docsId") Long docsId, @PathVariable(value = "seq") int seq, @PathVariable(value = "conversionName") String conversionName) {
        return attachmentsService.downloadFile(new AttachmentsDTO().setDocsId(docsId).setSeq(seq).setConversionName(conversionName));
    }

    @DeleteMapping("/attachments/{docsId}/{seq}/{conversionName}")
    public ResponseEntity<Object> deleteFile(@PathVariable Long docsId, @PathVariable int seq, @PathVariable String conversionName) {
        return ResponseEntityConverter.getFromRequestResult(attachmentsService.deleteFile(docsId, seq, conversionName));
    }

    @GetMapping("/picture")
    public byte[] getMemberPicture(@AuthenticationPrincipal SecurityUser loginUser) {
        return attachmentsService.getMemberPicture(loginUser.getMember().getId());
    }

    @PostMapping("/picture")
    public ResponseEntity<Object> setMemberPicture(MultipartHttpServletRequest request, HttpServletResponse response, MultipartFile imgFile, @AuthenticationPrincipal SecurityUser loginUser) {
        return ResponseEntityConverter.getFromRequestResult(attachmentsService.setMemberPicture(request, response, loginUser.getMember().getId()));
    }
}
