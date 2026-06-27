package com.xingqi.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xingqi.common.BusinessException;
import com.xingqi.entity.OperationTask;
import com.xingqi.entity.Room;
import com.xingqi.mapper.OperationTaskMapper;
import com.xingqi.mapper.RoomMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class OperationTaskService {

    private final OperationTaskMapper operationTaskMapper;
    private final RoomMapper roomMapper;

    public Page<OperationTask> page(Integer pageNum, Integer pageSize, Long roomId, String taskType, String status) {
        Page<OperationTask> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<OperationTask> wrapper = new LambdaQueryWrapper<>();
        if (roomId != null) {
            wrapper.eq(OperationTask::getRoomId, roomId);
        }
        if (taskType != null && !taskType.isBlank()) {
            wrapper.eq(OperationTask::getTaskType, taskType);
        }
        if (status != null && !status.isBlank()) {
            wrapper.eq(OperationTask::getStatus, status);
        }
        wrapper.orderByAsc(OperationTask::getStatus)
                .orderByDesc(OperationTask::getPriority)
                .orderByDesc(OperationTask::getCreatedAt);
        return operationTaskMapper.selectPage(page, wrapper);
    }

    public OperationTask getById(Long id) {
        OperationTask task = operationTaskMapper.selectById(id);
        if (task == null) {
            throw BusinessException.notFound("任务不存在");
        }
        return task;
    }

    @Transactional(rollbackFor = Exception.class)
    public OperationTask create(OperationTask task) {
        fillRoomInfo(task);
        LocalDateTime now = LocalDateTime.now();
        task.setCreatedAt(now);
        task.setUpdatedAt(now);
        if (task.getStatus() == null || task.getStatus().isBlank()) {
            task.setStatus("pending");
        }
        if (task.getPriority() == null || task.getPriority().isBlank()) {
            task.setPriority("normal");
        }
        operationTaskMapper.insert(task);
        log.info("创建运营任务成功: {}", task.getTitle());
        return task;
    }

    @Transactional(rollbackFor = Exception.class)
    public OperationTask update(Long id, OperationTask task) {
        OperationTask existing = getById(id);
        task.setId(id);
        task.setCreatedAt(existing.getCreatedAt());
        task.setCompletedAt(existing.getCompletedAt());
        task.setUpdatedAt(LocalDateTime.now());
        fillRoomInfo(task);

        if ("completed".equals(task.getStatus()) && existing.getCompletedAt() == null) {
            task.setCompletedAt(LocalDateTime.now());
        }
        operationTaskMapper.updateById(task);
        return task;
    }

    @Transactional(rollbackFor = Exception.class)
    public OperationTask updateStatus(Long id, String status) {
        OperationTask task = getById(id);
        task.setStatus(status);
        task.setUpdatedAt(LocalDateTime.now());
        if ("completed".equals(status)) {
            task.setCompletedAt(LocalDateTime.now());
            markCleaningRoomAvailable(task);
        } else {
            task.setCompletedAt(null);
        }
        operationTaskMapper.updateById(task);
        return task;
    }

    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        getById(id);
        operationTaskMapper.deleteById(id);
    }

    private void fillRoomInfo(OperationTask task) {
        if (task.getRoomId() == null) {
            return;
        }
        Room room = roomMapper.selectById(task.getRoomId());
        if (room == null) {
            throw BusinessException.badRequest("房间不存在");
        }
        task.setRoomNo(room.getRoomNo());
    }

    private void markCleaningRoomAvailable(OperationTask task) {
        if (!"cleaning".equals(task.getTaskType()) || task.getRoomId() == null) {
            return;
        }
        Room room = roomMapper.selectById(task.getRoomId());
        if (room != null && "cleaning".equals(room.getStatus())) {
            room.setStatus("available");
            room.setUpdatedAt(LocalDateTime.now());
            roomMapper.updateById(room);
        }
    }
}
