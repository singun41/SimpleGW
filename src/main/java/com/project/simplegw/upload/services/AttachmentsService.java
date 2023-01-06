package com.project.simplegw.upload.services;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import com.project.simplegw.system.vos.Constants;
import com.project.simplegw.system.vos.ServiceMsg;
import com.project.simplegw.system.vos.ServiceResult;
import com.project.simplegw.upload.dtos.DtosAttachements;
import com.project.simplegw.upload.entities.Attachments;
import com.project.simplegw.upload.helpers.AttachmentsConverter;
import com.project.simplegw.upload.helpers.UploadPath;
import com.project.simplegw.upload.repositories.AttachmentsRepo;
import com.project.simplegw.upload.vos.UploadType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class AttachmentsService {
    private final AttachmentsRepo repo;
    private final AttachmentsConverter converter;

    @Autowired
    public AttachmentsService(AttachmentsRepo repo, AttachmentsConverter converter) {
        this.repo = repo;
        this.converter = converter;
        log.info("Component '" + this.getClass().getName() + "' has been created.");
    }
    


    public ServiceMsg upload(Long docsId, MultipartHttpServletRequest req) {
        List<MultipartFile> files = req.getFiles("files");
        if(docsId == null || files == null || files.size() == 0)
            return new ServiceMsg().setResult(ServiceResult.SUCCESS);
        
        long uploadedFileCount = repo.countByDocsId(docsId);   // 기존에 업로드했던 파일이 있으면 다음 순번으로 추가해야 한다.
        int fileSeq = (int)uploadedFileCount + 1;

        try {
            Path fileDir = Paths.get(UploadPath.get(UploadType.ATTACHMENTS));
            if(Files.notExists(fileDir))
                Files.createDirectories(fileDir);

            for(MultipartFile file : files) {
                String fileName = file.getOriginalFilename();

                if(file.getSize() > Constants.ATTACHMENTS_UPLOAD_MAX_SIZE)
                    return new ServiceMsg().setResult(ServiceResult.FAILURE).setMsg(new StringBuilder("파일 '").append(fileName).append("' 의 크기가 제한 용량을 초과하였습니다.").toString());

                String conversionName = UUID.randomUUID().toString();
                file.transferTo(new File( new StringBuilder(fileDir.toString()).append(File.separator).append(conversionName).toString() ));   // 위에서 Paths.get 해서 가져온 경로가 맨 끝에 / 가 없어서 추가해줘야 함..

                Attachments attachments = Attachments.builder().docsId(docsId).seq(fileSeq++).conversionName(conversionName).originalName(fileName).path(UploadPath.file()).build();
                
                repo.save(attachments);
            }
            return new ServiceMsg().setResult(ServiceResult.SUCCESS);


        } catch(IOException e) {
            log.warn("upload IOExcetion.");
            log.warn("{}", e.getMessage());
            return new ServiceMsg().setResult(ServiceResult.FAILURE).setMsg("파일 업로드 에러입니다. 관리자에게 문의하세요.");


        } catch(IllegalStateException e) {
            log.warn("upload IllegalStateException.");
            log.warn("{}", e.getMessage());
            return new ServiceMsg().setResult(ServiceResult.FAILURE).setMsg("파일 업로드 에러입니다. 관리자에게 문의하세요.");
        }
    }



    public ServiceMsg download(Long docsId, int seq, String conversionName) {
        Attachments entity = getEntity(docsId, seq, conversionName);

        if(entity.getId() == null) {
            return new ServiceMsg().setResult(ServiceResult.FAILURE).setMsg("파일이 삭제되었습니다.");

        } else {
            try {
                Path file = Paths.get(new StringBuilder(Constants.ATTACHMENTS_UPLOAD_PATH).append(entity.getPath()).append(entity.getConversionName()).toString());
                Resource fileStream = new InputStreamResource(Files.newInputStream(file));

                return new ServiceMsg().setResult(ServiceResult.SUCCESS).setReturnObj(fileStream);
                

            } catch(IOException e) {
                log.warn("download IOException.");
                log.warn("{}", e.getMessage());
                log.warn("parameters: docsId={}, seq={}, conversionName={}", docsId, seq, conversionName);

                return new ServiceMsg().setResult(ServiceResult.FAILURE).setMsg("첨부파일 다운로드 에러입니다. 관리자에게 문의하세요.");
            }
        }
    }

    public Attachments getEntity(Long docsId, int seq, String conversionName) {
        return repo.findByDocsIdOrderBySeq(docsId).stream().filter(e -> e.getSeq() == seq && e.getConversionName().equals(conversionName)).findAny().orElseGet(Attachments::new);
    }


    public List<DtosAttachements> getAttachmentsList(Long docsId) {   // ViewController에서 문서의 첨부파일 리스트 보여줄 때
        return repo.findByDocsIdOrderBySeq(docsId).stream().map(converter::getDto).collect(Collectors.toList());
    }



    public ServiceMsg delete(Long docsId, int seq, String conversionName) {
        Attachments entity = getEntity(docsId, seq, conversionName);

        if(entity.getId() == null) {
            return new ServiceMsg().setResult(ServiceResult.FAILURE).setMsg("삭제 요청한 파일이 없습니다.");

        } else {
            File file = new File(new StringBuilder(UploadPath.get(UploadType.ATTACHMENTS)).append(entity.getPath()).append(entity.getConversionName()).toString());
            
            if(file.exists())
                file.delete();

            repo.delete(entity);

            return new ServiceMsg().setResult(ServiceResult.SUCCESS);
        }
    }
}
