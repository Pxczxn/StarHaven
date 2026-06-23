package com.xingqi.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xingqi.common.ApiResponse;
import com.xingqi.common.PageResponse;
import com.xingqi.entity.RoomType;
import com.xingqi.service.RoomTypeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 房型控制器
 */
@RestController
@RequestMapping("/api/room-types")
@RequiredArgsConstructor
public class RoomTypeController {

    private final RoomTypeService roomTypeService;

    /**
     * 分页查询房型
     */
    @GetMapping
    public ApiResponse<PageResponse<RoomType>> page(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String status
    ) {
        Page<RoomType> result = roomTypeService.page(page, pageSize, keyword, status);
        return ApiResponse.success(PageResponse.of(result));
    }

    /**
     * 查询所有启用的房型（用于下拉选择）
     */
    @GetMapping("/enabled")
    public ApiResponse<List<RoomType>> listEnabled() {
        return ApiResponse.success(roomTypeService.listEnabled());
    }

    /**
     * 根据 ID 查询房型
     */
    @GetMapping("/{id}")
    public ApiResponse<RoomType> getById(@PathVariable Long id) {
        return ApiResponse.success(roomTypeService.getById(id));
    }

    /**
     * 新增房型
     */
    @PostMapping
    public ApiResponse<RoomType> create(@Valid @RequestBody RoomType roomType) {
        return ApiResponse.success(roomTypeService.create(roomType));
    }

    /**
     * 更新房型
     */
    @PutMapping("/{id}")
    public ApiResponse<RoomType> update(@PathVariable Long id, @Valid @RequestBody RoomType roomType) {
        return ApiResponse.success(roomTypeService.update(id, roomType));
    }

    /**
     * 删除房型
     */
    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        roomTypeService.delete(id);
        return ApiResponse.success("删除成功", null);
    }

    /**
     * 启用/停用房型
     */
    @PutMapping("/{id}/status")
    public ApiResponse<Void> updateStatus(@PathVariable Long id, @RequestParam String status) {
        roomTypeService.updateStatus(id, status);
        return ApiResponse.success("状态更新成功", null);
    }
}
