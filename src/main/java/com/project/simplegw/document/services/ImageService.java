package com.project.simplegw.document.services;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;

import org.apache.commons.io.FilenameUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ImageService {
    // 다른 부가적인 기능없이 단순히 이미지를 압축하고 저장하는 기능을 담당하는 클래스

    private static final float QUALITY = 0.5f;

    public void compressAndSave(MultipartFile mpFile, Path path, String filename) throws Exception {
        if(Files.notExists(path))
            Files.createDirectories(path);

        // Appending a trailing slash if needed(맨 끝에 붙는 슬래시를 trailing slash 이라고 한다. URL리소스가 디렉토리를 의미함.)
        String pathStr = path.toString();
        if(pathStr.charAt(pathStr.length() - 1) != File.separatorChar)
            pathStr += File.separator;

        File imgFile = new File(pathStr + filename);

        InputStream is = mpFile.getInputStream();
        OutputStream os = new FileOutputStream(imgFile);
        ImageOutputStream imgOutputStream = ImageIO.createImageOutputStream(os);

        BufferedImage image = ImageIO.read(is);

        ImageWriter imgWriter = ImageIO.getImageWritersByFormatName(FilenameUtils.getExtension(mpFile.getOriginalFilename())).next();
        ImageWriteParam writeParam = imgWriter.getDefaultWriteParam();

        writeParam.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
        writeParam.setCompressionQuality(QUALITY);

        imgWriter.setOutput(imgOutputStream);
        imgWriter.write(null, new IIOImage(image, null, null), writeParam);

        is.close();
        os.flush();
        os.close();
        imgOutputStream.flush();
        imgOutputStream.close();
    }
}
