package com.xingqi.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xingqi.common.ApiResponse;
import com.xingqi.common.PageResponse;
import com.xingqi.entity.OperationTask;
import com.xingqi.service.OperationTaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/operation-tasks")
@RequiredArgsConstructor
public class OperationTaskController {

    private final OperationTaskService operationTaskService;

    @GetMapping
    public ApiResponse<PageResponse<OperationTask>> page(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) Long roomId,
            @RequestParam(required = false) String taskType,
            @RequestParam(required = false) String status
    ) {
        Page<OperationTask> result = operationTaskService.page(page, pageSize, roomId, taskType, status);
        return ApiResponse.success(PageResponse.of(result));
    }

    @GetMapping("/{id}")
    public ApiResponse<OperationTask> getById(@PathVariable Long id) {
        return ApiResponse.success(operationTaskService.getById(id));
    }

    @PostMapping
    public ApiResponse<OperationTask> create(@RequestBody OperationTask task) {
        return ApiResponse.success(operationTaskService.create(task));
    }

    @PutMapping("/{id}")
    public ApiResponse<OperationTask> update(@PathVariable Long id, @RequestBody OperationTask task) {
        return ApiResponse.success(operationTaskService.update(id, task));
    }

    @PutMapping("/{id}/status")
    public ApiResponse<OperationTask> updateStatus(@PathVariable Long id, @RequestParam String status) {
        return ApiResponse.success(operationTaskService.updateStatus(id, status));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        operationTaskService.delete(id);
        return ApiResponse.success();
    }
}
