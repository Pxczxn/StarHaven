package com.xingqi.controller;

import com.xingqi.common.ApiResponse;
import com.xingqi.dto.SiteSetting;
import com.xingqi.service.SiteSettingService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/settings")
@RequiredArgsConstructor
public class SettingsController {

    private final SiteSettingService siteSettingService;

    @GetMapping("/site")
    public ApiResponse<SiteSetting> getSiteSetting() {
        return ApiResponse.success(siteSettingService.getSiteSetting());
    }

    @PutMapping("/site")
    public ApiResponse<SiteSetting> updateSiteSetting(@RequestBody SiteSetting setting) {
        return ApiResponse.success(siteSettingService.updateSiteSetting(setting));
    }
}
