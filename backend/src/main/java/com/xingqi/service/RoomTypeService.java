package com.xingqi.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xingqi.common.BusinessException;
import com.xingqi.entity.Room;
import com.xingqi.entity.RoomType;
import com.xingqi.mapper.RoomMapper;
import com.xingqi.mapper.RoomTypeMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 房型服务
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RoomTypeService {

    private final RoomTypeMapper roomTypeMapper;
    private final RoomMapper roomMapper;

    /**
     * 分页查询房型
     */
    public Page<RoomType> page(Integer pageNum, Integer pageSize, String keyword, String status) {
        Page<RoomType> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<RoomType> wrapper = new LambdaQueryWrapper<>();

        // 关键词搜索
        if (keyword != null && !keyword.trim().isEmpty()) {
            wrapper.and(w -> w.like(RoomType::getName, keyword)
                    .or().like(RoomType::getDescription, keyword));
        }

        // 状态筛选
        if (status != null && !status.trim().isEmpty()) {
            wrapper.eq(RoomType::getStatus, status);
        }

        // 按创建时间倒序
        wrapper.orderByDesc(RoomType::getCreatedAt);

        return roomTypeMapper.selectPage(page, wrapper);
    }

    /**
     * 查询所有启用的房型（用于下拉选择）
     */
    public List<RoomType> listEnabled() {
        LambdaQueryWrapper<RoomType> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(RoomType::getStatus, "enabled");
        wrapper.orderByDesc(RoomType::getCreatedAt);
        return roomTypeMapper.selectList(wrapper);
    }

    /**
     * 根据 ID 查询房型
     */
    public RoomType getById(Long id) {
        RoomType roomType = roomTypeMapper.selectById(id);
        if (roomType == null) {
            throw BusinessException.notFound("房型不存在");
        }
        return roomType;
    }

    /**
     * 新增房型
     */
    @Transactional(rollbackFor = Exception.class)
    public RoomType create(RoomType roomType) {
        // 检查名称是否重复
        LambdaQueryWrapper<RoomType> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(RoomType::getName, roomType.getName());
        if (roomTypeMapper.selectCount(wrapper) > 0) {
            throw BusinessException.badRequest("房型名称已存在");
        }

        roomType.setCreatedAt(LocalDateTime.now());
        roomType.setUpdatedAt(LocalDateTime.now());
        
        if (roomType.getStatus() == null) {
            roomType.setStatus("enabled");
        }

        roomTypeMapper.insert(roomType);
        log.info("创建房型成功: {}", roomType.getName());
        return roomType;
    }

    /**
     * 更新房型
     */
    @Transactional(rollbackFor = Exception.class)
    public RoomType update(Long id, RoomType roomType) {
        RoomType existing = getById(id);

        // 检查名称是否与其他房型重复
        if (!existing.getName().equals(roomType.getName())) {
            LambdaQueryWrapper<RoomType> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(RoomType::getName, roomType.getName());
            wrapper.ne(RoomType::getId, id);
            if (roomTypeMapper.selectCount(wrapper) > 0) {
                throw BusinessException.badRequest("房型名称已存在");
            }
        }

        roomType.setId(id);
        roomType.setCreatedAt(existing.getCreatedAt());
        roomType.setUpdatedAt(LocalDateTime.now());

        roomTypeMapper.updateById(roomType);
        log.info("更新房型成功: {}", roomType.getName());
        return roomType;
    }

    /**
     * 删除房型
     */
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        RoomType roomType = getById(id);

        // 检查是否有房间使用该房型
        LambdaQueryWrapper<Room> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Room::getRoomTypeId, id);
        long count = roomMapper.selectCount(wrapper);
        if (count > 0) {
            throw BusinessException.badRequest("该房型下有 " + count + " 个房间，无法删除");
        }

        roomTypeMapper.deleteById(id);
        log.info("删除房型成功: {}", roomType.getName());
    }

    /**
     * 启用/停用房型
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateStatus(Long id, String status) {
        RoomType roomType = getById(id);
        roomType.setStatus(status);
        roomType.setUpdatedAt(LocalDateTime.now());
        roomTypeMapper.updateById(roomType);
        log.info("更新房型状态: {} -> {}", roomType.getName(), status);
    }
}
