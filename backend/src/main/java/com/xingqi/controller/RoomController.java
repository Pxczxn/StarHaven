package com.xingqi.controller;

import com.xingqi.common.ApiResponse;
import com.xingqi.common.PageResponse;
import com.xingqi.dto.request.RoomRequest;
import com.xingqi.dto.request.RoomStatusRequest;
import com.xingqi.entity.Room;
import com.xingqi.service.RoomService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 房间控制器
 */
@RestController
@RequestMapping("/api/rooms")
public class RoomController {

    @Autowired
    private RoomService roomService;

    /**
     * 分页查询房间
     */
    @GetMapping
    public ApiResponse<PageResponse<Room>> list(
            @RequestParam(defaultValue = "1") long page,
            @RequestParam(defaultValue = "10") long pageSize,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Long roomTypeId
    ) {
        PageResponse<Room> result = roomService.list(page, pageSize, keyword, status, roomTypeId);
        return ApiResponse.success(result);
    }

    /**
     * 获取房间详情
     */
    @GetMapping("/{id}")
    public ApiResponse<Room> getById(@PathVariable Long id) {
        Room room = roomService.getById(id);
        return ApiResponse.success(room);
    }

    /**
     * 创建房间
     */
    @PostMapping
    public ApiResponse<Room> create(@Valid @RequestBody RoomRequest request) {
        Room room = roomService.create(request);
        return ApiResponse.success("创建成功", room);
    }

    /**
     * 更新房间
     */
    @PutMapping("/{id}")
    public ApiResponse<Room> update(
            @PathVariable Long id,
            @Valid @RequestBody RoomRequest request
    ) {
        Room room = roomService.update(id, request);
        return ApiResponse.success("更新成功", room);
    }

    /**
     * 修改房间状态
     */
    @PutMapping("/{id}/status")
    public ApiResponse<Room> updateStatus(
            @PathVariable Long id,
            @Valid @RequestBody RoomStatusRequest request
    ) {
        Room room = roomService.updateStatus(id, request.getStatus());
        return ApiResponse.success("状态更新成功", room);
    }

    /**
     * 删除房间
     */
    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        roomService.delete(id);
        return ApiResponse.success("删除成功", null);
    }
}
