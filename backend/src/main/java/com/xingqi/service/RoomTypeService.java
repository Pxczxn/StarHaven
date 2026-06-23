package com.xingqi.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xingqi.common.BusinessException;
import com.xingqi.common.PageResponse;
import com.xingqi.dto.request.RoomTypeRequest;
import com.xingqi.entity.Room;
import com.xingqi.entity.RoomType;
import com.xingqi.mapper.RoomMapper;
import com.xingqi.mapper.RoomTypeMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 房型服务
 */
@Slf4j
@Service
public class RoomTypeService {

    @Autowired
    private RoomTypeMapper roomTypeMapper;

    @Autowired
    private RoomMapper roomMapper;

    /**
     * 分页查询房型
     */
    public PageResponse<RoomType> list(long page, long pageSize, String keyword, String status) {
        Page<RoomType> pageParam = new Page<>(page, pageSize);
        LambdaQueryWrapper<RoomType> wrapper = new LambdaQueryWrapper<>();

        // 关键词搜索
        if (StringUtils.hasText(keyword)) {
            wrapper.like(RoomType::getName, keyword);
        }

        // 状态筛选
        if (StringUtils.hasText(status)) {
            wrapper.eq(RoomType::getStatus, status);
        }

        // 按创建时间倒序
        wrapper.orderByDesc(RoomType::getCreatedAt);

        IPage<RoomType> result = roomTypeMapper.selectPage(pageParam, wrapper);

        return new PageResponse<>(
                result.getRecords(),
                result.getTotal(),
                result.getCurrent(),
                result.getSize()
        );
    }

    /**
     * 获取房型详情
     */
    public RoomType getById(Long id) {
        RoomType roomType = roomTypeMapper.selectById(id);
        if (roomType == null) {
            throw BusinessException.notFound("房型不存在");
        }
        return roomType;
    }

    /**
     * 创建房型
     */
    public RoomType create(RoomTypeRequest request) {
        // 检查房型名称是否重复
        LambdaQueryWrapper<RoomType> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(RoomType::getName, request.getName());
        if (roomTypeMapper.selectCount(wrapper) > 0) {
            throw BusinessException.badRequest("房型名称已存在");
        }

        RoomType roomType = new RoomType();
        BeanUtils.copyProperties(request, roomType);

        // 设置默认状态
        if (!StringUtils.hasText(roomType.getStatus())) {
            roomType.setStatus("enabled");
        }

        LocalDateTime now = LocalDateTime.now();
        roomType.setCreatedAt(now);
        roomType.setUpdatedAt(now);

        roomTypeMapper.insert(roomType);

        log.info("创建房型成功: {}", roomType.getName());
        return roomType;
    }

    /**
     * 更新房型
     */
    public RoomType update(Long id, RoomTypeRequest request) {
        RoomType roomType = getById(id);

        // 检查房型名称是否与其他房型重复
        if (!roomType.getName().equals(request.getName())) {
            LambdaQueryWrapper<RoomType> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(RoomType::getName, request.getName());
            wrapper.ne(RoomType::getId, id);
            if (roomTypeMapper.selectCount(wrapper) > 0) {
                throw BusinessException.badRequest("房型名称已存在");
            }
        }

        BeanUtils.copyProperties(request, roomType);
        roomType.setUpdatedAt(LocalDateTime.now());

        roomTypeMapper.updateById(roomType);

        log.info("更新房型成功: {}", roomType.getName());
        return roomType;
    }

    /**
     * 删除房型
     */
    public void delete(Long id) {
        RoomType roomType = getById(id);

        // 检查是否有房间使用该房型
        LambdaQueryWrapper<Room> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Room::getRoomTypeId, id);
        long count = roomMapper.selectCount(wrapper);
        if (count > 0) {
            throw BusinessException.conflict("该房型下还有 " + count + " 个房间，无法删除");
        }

        roomTypeMapper.deleteById(id);

        log.info("删除房型成功: {}", roomType.getName());
    }

    /**
     * 获取启用的房型列表（供 public 接口使用）
     */
    public List<RoomType> listEnabled() {
        LambdaQueryWrapper<RoomType> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(RoomType::getStatus, "enabled");
        wrapper.orderByDesc(RoomType::getCreatedAt);
        return roomTypeMapper.selectList(wrapper);
    }
}
