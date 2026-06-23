package com.xingqi.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * 本地文件存储服务
 */
@Slf4j
@Service
public class FileStorageService {

    @Value("${file.upload.path:uploads}")
    private String uploadPath;

    @Value("${file.upload.base-url:http://localhost:8888/uploads}")
    private String baseUrl;

    /**
     * 上传文件
     */
    public String uploadFile(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("文件不能为空");
        }

        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null) {
            throw new IllegalArgumentException("文件名不能为空");
        }

        // 获取文件扩展名
        String extension = "";
        int lastDotIndex = originalFilename.lastIndexOf(".");
        if (lastDotIndex > 0) {
            extension = originalFilename.substring(lastDotIndex);
        }

        // 按日期组织目录: uploads/2026/06/23/
        String datePath = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        String relativePath = datePath + "/" + UUID.randomUUID().toString() + extension;

        // 创建完整的文件路径
        Path uploadDir = Paths.get(uploadPath, datePath);
        if (!Files.exists(uploadDir)) {
            Files.createDirectories(uploadDir);
        }

        Path filePath = Paths.get(uploadPath, relativePath);

        // 保存文件
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        // 返回访问 URL
        String fileUrl = baseUrl + "/" + relativePath.replace("\\", "/");
        log.info("文件上传成功: {}", fileUrl);
        return fileUrl;
    }

    /**
     * 删除文件
     */
    public void deleteFile(String fileUrl) {
        if (fileUrl == null || !fileUrl.startsWith(baseUrl)) {
            return;
        }

        try {
            // 从 URL 中提取相对路径
            String relativePath = fileUrl.substring(baseUrl.length() + 1);
            Path filePath = Paths.get(uploadPath, relativePath);

            if (Files.exists(filePath)) {
                Files.delete(filePath);
                log.info("文件删除成功: {}", relativePath);
            }
        } catch (Exception e) {
            log.error("文件删除失败: {}", fileUrl, e);
        }
    }

    /**
     * 获取文件
     */
    public File getFile(String relativePath) {
        Path filePath = Paths.get(uploadPath, relativePath);
        return filePath.toFile();
    }
}
