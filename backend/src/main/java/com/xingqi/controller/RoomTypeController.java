package com.xingqi.controller;

import com.xingqi.common.ApiResponse;
import com.xingqi.common.PageResponse;
import com.xingqi.dto.request.RoomTypeRequest;
import com.xingqi.entity.RoomType;
import com.xingqi.service.RoomTypeService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 房型控制器
 */
@RestController
@RequestMapping("/api/room-types")
public class RoomTypeController {

    @Autowired
    private RoomTypeService roomTypeService;

    /**
     * 分页查询房型
     */
    @GetMapping
    public ApiResponse<PageResponse<RoomType>> list(
            @RequestParam(defaultValue = "1") long page,
            @RequestParam(defaultValue = "10") long pageSize,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String status
    ) {
        PageResponse<RoomType> result = roomTypeService.list(page, pageSize, keyword, status);
        return ApiResponse.success(result);
    }

    /**
     * 获取房型详情
     */
    @GetMapping("/{id}")
    public ApiResponse<RoomType> getById(@PathVariable Long id) {
        RoomType roomType = roomTypeService.getById(id);
        return ApiResponse.success(roomType);
    }

    /**
     * 创建房型
     */
    @PostMapping
    public ApiResponse<RoomType> create(@Valid @RequestBody RoomTypeRequest request) {
        RoomType roomType = roomTypeService.create(request);
        return ApiResponse.success("创建成功", roomType);
    }

    /**
     * 更新房型
     */
    @PutMapping("/{id}")
    public ApiResponse<RoomType> update(
            @PathVariable Long id,
            @Valid @RequestBody RoomTypeRequest request
    ) {
        RoomType roomType = roomTypeService.update(id, request);
        return ApiResponse.success("更新成功", roomType);
    }

    /**
     * 删除房型
     */
    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        roomTypeService.delete(id);
        return ApiResponse.success("删除成功", null);
    }
}
