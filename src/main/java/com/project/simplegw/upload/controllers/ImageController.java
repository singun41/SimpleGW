package com.project.simplegw.upload.controllers;

import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;

import com.project.simplegw.system.vos.Constants;
import com.project.simplegw.upload.services.ImageService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
public class ImageController {
    private final ImageService imageService;

    @Autowired
    public ImageController(ImageService imageService) {
        this.imageService = imageService;
        log.info("Component '" + this.getClass().getName() + "' has been created.");
    }


    @PostMapping("/images")
    public HashMap<String, Object> uploadImages(HttpServletRequest req, @RequestParam("upload") MultipartFile imgFile) {   // @RequestBody가 아니고 @RequestParam이다.
        return imageService.upload(req, imgFile);
    }


    @GetMapping(Constants.IMAGE_GET_URL + "{pathYear}/{pathMonth}/{pathDay}/{conversionName}")
    public ResponseEntity<byte[]> getImages(@PathVariable String pathYear, @PathVariable String pathMonth, @PathVariable String pathDay, @PathVariable String conversionName) {
        return ResponseEntity.ok().contentType(MediaType.IMAGE_PNG).body(imageService.getImageByteStream(pathYear, pathMonth, pathDay, conversionName));
    }
}
