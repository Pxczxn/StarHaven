package com.xingqi.service;

import com.xingqi.config.FileUploadProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Slf4j
@Service
public class FileUploadService {

    private final FileUploadProperties fileUploadProperties;

    public FileUploadService(FileUploadProperties fileUploadProperties) {
        this.fileUploadProperties = fileUploadProperties;
        initUploadDirectory();
    }

    private void initUploadDirectory() {
        try {
            Path uploadPath = Paths.get(fileUploadProperties.getPath());
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
                log.info("创建上传目录: {}", uploadPath.toAbsolutePath());
            }
        } catch (IOException e) {
            log.error("创建上传目录失败", e);
        }
    }

    public String uploadFile(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("文件不能为空");
        }

        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null) {
            throw new IllegalArgumentException("文件名不能为空");
        }

        String extension = "";
        int lastDotIndex = originalFilename.lastIndexOf(".");
        if (lastDotIndex > 0) {
            extension = originalFilename.substring(lastDotIndex);
        }

        String fileName = UUID.randomUUID().toString() + extension;
        Path filePath = Paths.get(fileUploadProperties.getPath(), fileName);

        file.transferTo(filePath.toFile());

        String fileUrl = fileUploadProperties.getBaseUrl() + fileName;
        log.info("文件上传成功: {}", fileUrl);
        return fileUrl;
    }

    public void deleteFile(String fileUrl) {
        if (fileUrl == null || !fileUrl.startsWith(fileUploadProperties.getBaseUrl())) {
            return;
        }

        String fileName = fileUrl.substring(fileUploadProperties.getBaseUrl().length());
        Path filePath = Paths.get(fileUploadProperties.getPath(), fileName);

        try {
            Files.deleteIfExists(filePath);
            log.info("文件删除成功: {}", fileName);
        } catch (IOException e) {
            log.error("文件删除失败", e);
        }
    }
}
