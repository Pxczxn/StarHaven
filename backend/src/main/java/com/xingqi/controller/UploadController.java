package com.xingqi.controller;

import com.xingqi.common.ApiResponse;
import com.xingqi.service.FileStorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

/**
 * 文件上传控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/upload")
@RequiredArgsConstructor
public class UploadController {

    private final FileStorageService fileStorageService;

    /**
     * 上传文件
     */
    @PostMapping
    public ApiResponse<Map<String, String>> upload(@RequestParam("file") MultipartFile file) {
        try {
            String url = fileStorageService.uploadFile(file);
            Map<String, String> result = new HashMap<>();
            result.put("url", url);
            result.put("name", file.getOriginalFilename());
            return ApiResponse.success(result);
        } catch (Exception e) {
            log.error("文件上传失败", e);
            return ApiResponse.error("文件上传失败: " + e.getMessage());
        }
    }

    /**
     * 删除文件
     */
    @DeleteMapping
    public ApiResponse<Void> delete(@RequestParam("url") String url) {
        try {
            fileStorageService.deleteFile(url);
            return ApiResponse.success();
        } catch (Exception e) {
            log.error("文件删除失败", e);
            return ApiResponse.error("文件删除失败: " + e.getMessage());
        }
    }
}
