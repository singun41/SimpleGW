package com.project.simplegw.member.services;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.project.simplegw.member.dtos.admin.send.DtosMember;
import com.project.simplegw.system.security.LoginUser;
import com.project.simplegw.system.vos.Constants;
import com.project.simplegw.system.vos.ServiceMsg;
import com.project.simplegw.system.vos.ServiceResult;
import com.project.simplegw.upload.services.ImageService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional(rollbackFor = Exception.class, isolation = Isolation.READ_COMMITTED)
public class MemberPortraitService {
    private final ImageService imgService;
    private final MemberService memberService;


    @Autowired
    public MemberPortraitService(ImageService imgService, MemberService memberService) {
        this.imgService = imgService;
        this.memberService = memberService;

        log.info("Component '" + this.getClass().getName() + "' has been created.");
    }





    @Cacheable(cacheManager = Constants.CACHE_MANAGER, cacheNames = Constants.CACHE_IMG_USER_PORTRAIT, key = "#loginUser.getMember().getId()")
    public byte[] getPortrait(LoginUser loginUser) {
        return getPortrait(loginUser.getMember().getId());
    }


    @Cacheable(cacheManager = Constants.CACHE_MANAGER, cacheNames = Constants.CACHE_IMG_USER_PORTRAIT, key = "#memberId")
    public byte[] getPortrait(Long memberId) {
        log.info("Cacheable method 'getPortrait()' called. user: {}", memberId);
        String imgPath = new StringBuilder(Constants.USER_PORTRAIT_PATH).append(memberId.toString()).append(Constants.PORTRAIT_IMAGE_EXTENSION).toString();

        try( InputStream imgByteStream = new FileInputStream(imgPath) ) {
            return IOUtils.toByteArray(imgByteStream);

        } catch(FileNotFoundException e) {
            log.warn("getPortrait() FileNotFoundException.");
            log.warn("{}", e.getMessage());
            log.warn("user: {}", memberId);
            return null;

        } catch(IOException e) {
            log.warn("getPortrait() IOException.");
            log.warn("{}", e.getMessage());
            log.warn("user: {}", memberId);
            return null;
        }
    }


    @CacheEvict(cacheManager = Constants.CACHE_MANAGER, cacheNames = Constants.CACHE_IMG_USER_PORTRAIT, key = "#loginUser.getMember().getId()")
    public ServiceMsg uploadPortrait(MultipartHttpServletRequest req, LoginUser loginUser) {
        log.info("CacheEvict method 'uploadPortrait()' called. user: {}", loginUser.getMember().getId());

        MultipartFile imgFile = req.getFile("img");
        
        if(imgFile == null)   // 파일이 없으면 종료
            return new ServiceMsg().setResult(ServiceResult.SUCCESS);

        String memberId = loginUser.getMember().getId().toString();
        String imgExtension = Constants.PORTRAIT_IMAGE_EXTENSION;
        String conversionName = new StringBuilder(memberId).append(imgExtension).toString();
        
        if(imgFile.getSize() > Constants.IMAGE_UPLOAD_MAX_SIZE)
            return new ServiceMsg().setResult(ServiceResult.FAILURE).setMsg("파일이 업로드 제한 용량을 초과하여 업로드 실패하였습니다.");
        
        try {
            Path imgDir = Paths.get(Constants.USER_PORTRAIT_PATH);
            imgService.compressAndSave(imgFile, imgDir, conversionName);
            
            return new ServiceMsg().setResult(ServiceResult.SUCCESS);
            
        } catch(Exception e) {
            e.printStackTrace();
            log.warn("uploadPortrait() exception.");
            log.warn("user: {}", memberId);

            return new ServiceMsg().setResult(ServiceResult.FAILURE).setMsg("사진 업로드 에러입니다. 관리자에게 문의하세요.");
        }
    }


    @CacheEvict(cacheManager = Constants.CACHE_MANAGER, cacheNames = Constants.CACHE_IMG_USER_PORTRAIT, key = "#memberId")
    public void userPortraitCacheRefresh(Long memberId) {
        log.info("User {} portrait image cache refreshed.", memberId);
    }





    // SystemScheduler 클래스에서 호출
    @CacheEvict(cacheManager = Constants.CACHE_MANAGER, cacheNames = Constants.CACHE_IMG_USER_PORTRAIT, allEntries = true)
    public void deleteResignedPortrait() {
        log.info("CacheEvict method 'deleteResignedPortrait()' called.");
        
        Path imgDir = Paths.get(Constants.USER_PORTRAIT_PATH);
        if(Files.notExists(imgDir)) {
            log.info("user portrait directory not exists.");
            return;
        }

        List<DtosMember> list = memberService.getMembers(true);
        list.forEach(resignedId -> {
            File portraitImgFile = new File(imgDir.toString() + File.separatorChar + resignedId.getId().toString() + Constants.PORTRAIT_IMAGE_EXTENSION);

            if(portraitImgFile.exists()) {
                portraitImgFile.delete();
                log.info("resigned user portrait image file deleted. {}", portraitImgFile.toString());
            }
        });
    }
}
