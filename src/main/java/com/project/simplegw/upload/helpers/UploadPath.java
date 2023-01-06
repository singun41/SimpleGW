package com.project.simplegw.upload.helpers;

import java.io.File;
import java.time.LocalDate;

import com.project.simplegw.system.vos.Constants;
import com.project.simplegw.upload.vos.UploadType;

public class UploadPath {
    public static String file() {   // yyyy/mm/dd/ 형식으로 리턴하되, 월(m)과 일(d)는 1자리 또는 2자리이다.
        LocalDate now = LocalDate.now();
        return new StringBuilder()
            .append(now.getYear()).append(File.separator)   // upload path는 OS의 파일 구분문자를 사용한다.(대부분 역슬래쉬)
            .append(now.getMonthValue()).append(File.separator)
            .append(now.getDayOfMonth()).append(File.separator).toString();
    }

    public static String iamage() {   // yyyy/mm/dd/ 형식으로 리턴하되, 월(m)과 일(d)는 1자리 또는 2자리이다.
        LocalDate now = LocalDate.now();
        return new StringBuilder()
            .append(now.getYear()).append("/")   // 이미지를 로드할 때 필요한 url을 리턴하므로 구분문자는 슬래쉬로 사용한다.
            .append(now.getMonthValue()).append("/")
            .append(now.getDayOfMonth()).append("/").toString();
    }


    public static String get(UploadType type) {
        return switch(type) {
            case ATTACHMENTS -> new StringBuilder(Constants.ATTACHMENTS_UPLOAD_PATH).append(file()).toString();
            
            case IMAGE -> new StringBuilder(Constants.IMAGE_UPLOAD_PATH).append(iamage()).toString();

            default -> "";
        };
    }
}
