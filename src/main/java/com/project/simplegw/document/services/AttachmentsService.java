package com.project.simplegw.document.services;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.project.simplegw.common.vos.Constants;
import com.project.simplegw.common.vos.RequestResult;
import com.project.simplegw.document.dtos.AttachmentsDTO;
import com.project.simplegw.document.entities.Attachments;
import com.project.simplegw.document.repositories.AttachmentsRepository;
import com.project.simplegw.document.vos.AttachmentsType;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

@Service
@Transactional(rollbackFor = Exception.class, isolation = Isolation.READ_COMMITTED)
public class AttachmentsService {
    
    private static final String UPLOAD_ROOT = "D:/webapp/SpringBoot/SimpleGW/attachments/";

    private static final String FILE_PATH = UPLOAD_ROOT + "files/";
    private static final String IMAGE_PATH = UPLOAD_ROOT + "images/";
    private static final String MEMBER_PICTURE_PATH = Constants.SYSTEM_PATH + "member_picture/";

    public static final String ATTACHMENTS_IMAGE_URL = "/attachments/images/";
    
    private static String DAILY_PATH = null;   // 매일 1번 변경되므로 final로 선언하지 않는다.

    private static final long IMAGE_MAX_SIZE = 1024 * 1024 * 20;
    private static final long FILE_MAX_SIZE  = 1024 * 1024 * 50;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final AttachmentsRepository attachmentsRepo;
    private final AttachmentsConverter attachmentsConverter;
    private final ImageService imgService;

    @Autowired
    public AttachmentsService(AttachmentsRepository attachmentsRepo, AttachmentsConverter attachmentsConverter, ImageService imgService) {
        this.attachmentsRepo = attachmentsRepo;
        this.attachmentsConverter = attachmentsConverter;
        this.imgService = imgService;

        setDailyPath();
    }

    // ----- ----- ----- ----- ----- Converting ----- ----- ----- ----- ----- //
    private AttachmentsDTO attachesToDto(Attachments entity) {
        return attachmentsConverter.getDTO(entity);
    }
    // ----- ----- ----- ----- ----- Converting ----- ----- ----- ----- ----- //

    public void setDailyPath() {   // 스케줄러로 매일 한 번씩 저장 경로를 변경해주기 위해서 public으로 선언한다.
        LocalDate now = LocalDate.now();
        DAILY_PATH = new StringBuilder().append(now.getYear()).append("/").append(now.getMonthValue()).append("/").append(now.getDayOfMonth()).append("/").toString();
        logger.info("첨부파일, 이미지 업로드 경로를 설정하였습니다.");
    }

    private String getPath(AttachmentsType type) {
        String path = null;
        switch(type) {
            case FILE:
                path = new StringBuilder(FILE_PATH).append(DAILY_PATH).toString();
                break;
            case IMAGE:
                path = new StringBuilder(IMAGE_PATH).append(DAILY_PATH).toString();
                break;
        }
        return path;
    }

    public HashMap<String, Object> uploadImages(HttpServletRequest request, HttpServletResponse response, MultipartFile imgFile) {
        String imgName = imgFile.getOriginalFilename();
        String imgExtension = FilenameUtils.getExtension(imgName);

        // 중복방지를 위해 파일명 변경
        String conversionName = new StringBuilder(UUID.randomUUID().toString()).append(".").append(imgExtension).toString();

        // 업로드 후 이미지 불러오는 url을 리턴해줘야 한다.
        String imgLoadUrl = new StringBuilder(request.getContextPath()).append(ATTACHMENTS_IMAGE_URL).append(DAILY_PATH).append(conversionName.toString()).toString();

        // https://ckeditor.com/docs/ckeditor4/latest/guide/dev_file_upload.html
        HashMap<String, Object> result = new HashMap<>();
        HashMap<String, String> errorMsg = new HashMap<>();

        result.put("fileName", conversionName);
        result.put("url", imgLoadUrl);

        if(imgFile.getSize() > IMAGE_MAX_SIZE) {
            errorMsg.put("message", "파일이 업로드 제한 용량을 초과하여 업로드 실패하였습니다.");
            result.put("uploaded", 0);
            result.put("error", errorMsg);

            return result;
        }

        String imgExtensionPatterns = "jpg|jpeg|png|gif|bmp|tiff|tif";
        String exts = Arrays.toString(imgExtensionPatterns.split("[|]"));   // 특수문자를 구분자로 할 땐 []로 감싸주자.

        if(!imgExtension.matches(imgExtensionPatterns)) {
            errorMsg.put("message", new StringBuilder("이미지 파일이 아닙니다.").append("허용 확장자: ").append(exts).toString());
            result.put("uploaded", 0);
            result.put("error", errorMsg);

            return result;
        }

        try {
            Path imgDir = Paths.get(getPath(AttachmentsType.IMAGE));
            imgService.compressAndSave(imgFile, imgDir, conversionName);

            result.put("uploaded", 1);   // ckeditor4에서 이미지 업로드 성공은 1, 실패는 0

        } catch(IOException e) {
            logger.warn("{}{}파일 IO 에러 발생", e.getMessage(), System.lineSeparator());
            errorMsg.put("message", "파일 IO 에러 발생");
            result.put("uploaded", 0);
            result.put("error", errorMsg);

        } catch(IllegalStateException e) {
            logger.warn("{}{}파일 상태 에러 발생", e.getMessage(), System.lineSeparator());
            errorMsg.put("message", "파일 상태 에러 발생");
            result.put("uploaded", 0);
            result.put("error", errorMsg);

        } catch(Exception e) {
            logger.warn("{}{}", e.getMessage(), System.lineSeparator());
            errorMsg.put("message", "이미지 업로드시 예외 발생");
            result.put("uploaded", 0);
            result.put("error", errorMsg);
        }
        return result;
    }

    public byte[] getImgByteStream(String pathYear, String pathMonth, String pathDay, String conversionName) {
        String imgPath = new StringBuilder(IMAGE_PATH).append(pathYear).append("/").append(pathMonth).append("/").append(pathDay).append("/").append(conversionName).toString();
        
        try (
            InputStream imgByteStream = new FileInputStream(imgPath);
        ) {
            return IOUtils.toByteArray(imgByteStream);
        } catch(FileNotFoundException e) {
            logger.warn("{}{}파일을 찾지 못했습니다.", e.getMessage(), System.lineSeparator());
            return null;
        } catch(IOException e) {
            logger.warn("{}{}파일 IO 에러 발생", e.getMessage(), System.lineSeparator());
            return null;
        }
    }

    public RequestResult uploadFiles(MultipartHttpServletRequest request, HttpServletResponse response) {
        List<MultipartFile> files = request.getFiles("files");
        if(files.size() == 0) {
            return RequestResult.getDefaultSuccess(Constants.RESULT_MESSAGE_OK);
        }

        // response.setCharacterEncoding("utf-8");
        // response.setContentType("text/html;charset=utf-8");
        
        long docsId = Long.valueOf(request.getParameter("docsId"));
        long uploadedFileCount = attachmentsRepo.countByDocsId(docsId);   // 기존에 업로드했던 파일이 있으면 다음 순번으로 추가해야 한다.
        int fileSeq = (int)uploadedFileCount + 1;

        try {
            Path fileDir = Paths.get(getPath(AttachmentsType.FILE));
            if(Files.notExists(fileDir))
                Files.createDirectories(fileDir);

            for(MultipartFile file : files) {
                String fileName = file.getOriginalFilename();

                if(file.getSize() > FILE_MAX_SIZE)
                    return RequestResult.getDefaultFail("파일 용량이 제한 용량을 초과하였습니다.");

                String conversionName = UUID.randomUUID().toString();
                file.transferTo(new File(fileDir.toString() + "/" + conversionName));   // 위에서 Paths.get 해서 가져온 경로가 맨 끝에 / 가 없어서 추가해줘야 함..

                Attachments attachments = Attachments.builder()
                                                        .docsId(docsId)
                                                        .seq(fileSeq)
                                                        .conversionName(conversionName)
                                                        .originalName(fileName)
                                                        .path(DAILY_PATH)
                                                    .build();
                attachmentsRepo.save(attachments);
                fileSeq++;
            }
        } catch(IOException e) {
            logger.warn("{}{}파일 IO 에러 발생", e.getMessage(), System.lineSeparator());
            return RequestResult.getDefaultError("파일 IO 에러 발생");
        } catch(IllegalStateException e) {
            logger.warn("{}{}파일 상태 에러 발생", e.getMessage(), System.lineSeparator());
            return RequestResult.getDefaultError("파일 상태 에러 발생");
        }
        return RequestResult.getDefaultSuccess(Constants.RESULT_MESSAGE_OK);
    }

    public List<AttachmentsDTO> getFileList(Long docsId) {
        List<Attachments> attachmentsList = attachmentsRepo.findAllByDocsIdOrderBySeq(docsId);
        if(!attachmentsList.isEmpty()) {
            List<AttachmentsDTO> fileList = new ArrayList<>();
            
            attachmentsList.forEach(file -> fileList.add(attachesToDto(file).setDocsId(docsId)));
            return fileList;
        }
        return null;
    }

    public ResponseEntity<Resource> downloadFile(AttachmentsDTO dto) {
        Optional<Attachments> result = attachmentsRepo.findByDocsIdAndSeqAndConversionName(dto.getDocsId(), dto.getSeq(), dto.getConversionName());

        if(result.isPresent()) {
            Attachments attachments = result.get();

            try {
                Path file = Paths.get(new StringBuilder(FILE_PATH).append(attachments.getPath()).append(attachments.getConversionName()).toString());
                Resource fileStream = new InputStreamResource(Files.newInputStream(file));
                ContentDisposition contentDisposition = ContentDisposition.builder("attachments").filename(attachments.getOriginalName(), StandardCharsets.UTF_8).build();

                return ResponseEntity.ok().contentType(MediaType.parseMediaType("application/octet-stream"))
                        .header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition.toString()).body(fileStream);
            } catch(IOException e) {
                logger.warn("{}{}파일 IO 에러 발생", e.getMessage(), System.lineSeparator());
                return null;
            }

        } else {
            logger.warn("다운로드 요청한 파일이 없습니다.");
            return null;
        }
    }

    public RequestResult deleteFile(Long docsId, int seq, String conversionName) {
        Optional<Attachments> result = attachmentsRepo.findByDocsIdAndSeqAndConversionName(docsId, seq, conversionName);

        if(result.isPresent()) {
            Attachments attachments = result.get();

            File file = new File(new StringBuilder(FILE_PATH).append(attachments.getPath()).append(attachments.getConversionName()).toString());

            if(file.exists()) { file.delete(); }

            attachmentsRepo.delete(attachments);
            return RequestResult.getDefaultSuccess(Constants.RESULT_MESSAGE_DELETED);
        } else {
            return RequestResult.getDefaultError("삭제 요청한 파일이 없습니다.");
        }
    }

    public byte[] getMemberPicture(Long memberId) {
        String imgPath = new StringBuilder(MEMBER_PICTURE_PATH).append(memberId.toString()).append(".png").toString();
        
        try (
            InputStream imgByteStream = new FileInputStream(imgPath);
        ) {
            return IOUtils.toByteArray(imgByteStream);
        } catch(FileNotFoundException e) {
            return null;
        } catch(IOException e) {
            logger.warn("{}{}로그인 유저 사진 파일 IO 에러 발생", e.getMessage(), System.lineSeparator());
            return null;
        }
    }

    public RequestResult setMemberPicture(MultipartHttpServletRequest request, HttpServletResponse response, Long memberId) {
        MultipartFile imgFile = request.getFile("img");
        if(imgFile == null) {   // 파일이 없으면 종료
            return RequestResult.getDefaultSuccess(Constants.RESULT_MESSAGE_OK);
        }

        String imgExtension = ".png";
        String conversionName = new StringBuilder(memberId.toString()).append(imgExtension).toString();
        
        if(imgFile.getSize() > IMAGE_MAX_SIZE) {
            return RequestResult.getDefaultFail("파일이 업로드 제한 용량을 초과하여 업로드 실패하였습니다.");
        }

        try {
            Path imgDir = Paths.get(MEMBER_PICTURE_PATH);
            imgService.compressAndSave(imgFile, imgDir, conversionName);
            return RequestResult.getDefaultSuccess(Constants.RESULT_MESSAGE_OK);
            
        } catch(Exception e) {
            logger.warn("{}{}유저 사진 파일 업로드 에러가 발생하였습니다.{}에러 발생 유저 ID: {}", e.getMessage(), System.lineSeparator(), memberId.toString());
            return RequestResult.getDefaultError(e.getMessage());
        }
    }
}
