package com.xingqi.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.xingqi.common.BusinessException;
import com.xingqi.dto.SiteSetting;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Service
@RequiredArgsConstructor
public class SiteSettingService {

    private final ObjectMapper objectMapper;
    private final Path settingPath = Path.of("data", "site-setting.json");

    public SiteSetting getSiteSetting() {
        if (!Files.exists(settingPath)) {
            return defaultSetting();
        }
        try {
            SiteSetting setting = objectMapper.readValue(settingPath.toFile(), SiteSetting.class);
            return mergeDefault(setting);
        } catch (IOException e) {
            throw BusinessException.badRequest("读取站点设置失败");
        }
    }

    public SiteSetting updateSiteSetting(SiteSetting setting) {
        SiteSetting merged = mergeDefault(setting);
        try {
            Files.createDirectories(settingPath.getParent());
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(settingPath.toFile(), merged);
            return merged;
        } catch (IOException e) {
            throw BusinessException.badRequest("保存站点设置失败");
        }
    }

    private SiteSetting mergeDefault(SiteSetting setting) {
        SiteSetting defaults = defaultSetting();
        if (setting == null) {
            return defaults;
        }
        if (isBlank(setting.getBrandName())) setting.setBrandName(defaults.getBrandName());
        if (isBlank(setting.getSlogan())) setting.setSlogan(defaults.getSlogan());
        if (isBlank(setting.getPhone())) setting.setPhone(defaults.getPhone());
        if (isBlank(setting.getAddress())) setting.setAddress(defaults.getAddress());
        if (isBlank(setting.getDescription())) setting.setDescription(defaults.getDescription());
        if (setting.getHeroImageUrl() == null) setting.setHeroImageUrl(defaults.getHeroImageUrl());
        return setting;
    }

    private SiteSetting defaultSetting() {
        SiteSetting setting = new SiteSetting();
        setting.setBrandName("星栖民宿");
        setting.setSlogan("星辰为引，栖心而居");
        setting.setPhone("400-888-8888");
        setting.setAddress("某市某区某街道123号");
        setting.setDescription("在星空下，找到心灵的栖息地");
        setting.setHeroImageUrl("");
        return setting;
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
