package com.xingqi.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xingqi.common.ApiResponse;
import com.xingqi.common.PageResponse;
import com.xingqi.entity.Room;
import com.xingqi.service.RoomService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 房间控制器
 */
@RestController
@RequestMapping("/api/rooms")
@RequiredArgsConstructor
public class RoomController {

    private final RoomService roomService;

    /**
     * 分页查询房间
     */
    @GetMapping
    public ApiResponse<PageResponse<Room>> page(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Long roomTypeId
    ) {
        Page<Room> result = roomService.page(page, pageSize, keyword, status, roomTypeId);
        return ApiResponse.success(PageResponse.of(result));
    }

    /**
     * 查询所有房间（用于下拉选择）
     */
    @GetMapping("/all")
    public ApiResponse<List<Room>> listAll() {
        return ApiResponse.success(roomService.listAll());
    }

    /**
     * 根据 ID 查询房间
     */
    @GetMapping("/{id}")
    public ApiResponse<Room> getById(@PathVariable Long id) {
        return ApiResponse.success(roomService.getById(id));
    }

    /**
     * 新增房间
     */
    @PostMapping
    public ApiResponse<Room> create(@Valid @RequestBody Room room) {
        return ApiResponse.success(roomService.create(room));
    }

    /**
     * 更新房间
     */
    @PutMapping("/{id}")
    public ApiResponse<Room> update(@PathVariable Long id, @Valid @RequestBody Room room) {
        return ApiResponse.success(roomService.update(id, room));
    }

    /**
     * 删除房间
     */
    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        roomService.delete(id);
        return ApiResponse.success("删除成功", null);
    }

    /**
     * 更新房间状态
     */
    @PutMapping("/{id}/status")
    public ApiResponse<Void> updateStatus(@PathVariable Long id, @RequestParam String status) {
        roomService.updateStatus(id, status);
        return ApiResponse.success("状态更新成功", null);
    }
}
