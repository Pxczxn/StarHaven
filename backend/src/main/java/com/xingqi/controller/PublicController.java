package com.xingqi.controller;

import com.xingqi.common.ApiResponse;
import com.xingqi.entity.RoomType;
import com.xingqi.service.RoomTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Public 接口控制器（供 H5 使用）
 */
@RestController
@RequestMapping("/api/public")
public class PublicController {

    @Autowired
    private RoomTypeService roomTypeService;

    /**
     * 获取启用的房型列表
     */
    @GetMapping("/room-types")
    public ApiResponse<List<RoomType>> listRoomTypes() {
        List<RoomType> roomTypes = roomTypeService.listEnabled();
        return ApiResponse.success(roomTypes);
    }

    /**
     * 获取房型详情
     */
    @GetMapping("/room-types/{id}")
    public ApiResponse<RoomType> getRoomType(@PathVariable Long id) {
        RoomType roomType = roomTypeService.getById(id);

        // 只返回启用的房型
        if (!"enabled".equals(roomType.getStatus())) {
            return ApiResponse.error(404, "房型不存在或已下架");
        }

        return ApiResponse.success(roomType);
    }
}
