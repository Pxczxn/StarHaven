package com.xingqi.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xingqi.common.BusinessException;
import com.xingqi.common.PageResponse;
import com.xingqi.dto.request.RoomRequest;
import com.xingqi.entity.BookingOrder;
import com.xingqi.entity.Room;
import com.xingqi.entity.RoomType;
import com.xingqi.mapper.BookingOrderMapper;
import com.xingqi.mapper.RoomMapper;
import com.xingqi.mapper.RoomTypeMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

/**
 * 房间服务
 */
@Slf4j
@Service
public class RoomService {

    @Autowired
    private RoomMapper roomMapper;

    @Autowired
    private RoomTypeMapper roomTypeMapper;

    @Autowired
    private BookingOrderMapper bookingOrderMapper;

    /**
     * 分页查询房间
     */
    public PageResponse<Room> list(long page, long pageSize, String keyword, String status, Long roomTypeId) {
        Page<Room> pageParam = new Page<>(page, pageSize);
        LambdaQueryWrapper<Room> wrapper = new LambdaQueryWrapper<>();

        // 关键词搜索（房间号或房间名称）
        if (StringUtils.hasText(keyword)) {
            wrapper.and(w -> w.like(Room::getRoomNo, keyword).or().like(Room::getName, keyword));
        }

        // 状态筛选
        if (StringUtils.hasText(status)) {
            wrapper.eq(Room::getStatus, status);
        }

        // 房型筛选
        if (roomTypeId != null) {
            wrapper.eq(Room::getRoomTypeId, roomTypeId);
        }

        // 按房间号排序
        wrapper.orderByAsc(Room::getRoomNo);

        IPage<Room> result = roomMapper.selectPage(pageParam, wrapper);

        return new PageResponse<>(
                result.getRecords(),
                result.getTotal(),
                result.getCurrent(),
                result.getSize()
        );
    }

    /**
     * 获取房间详情
     */
    public Room getById(Long id) {
        Room room = roomMapper.selectById(id);
        if (room == null) {
            throw BusinessException.notFound("房间不存在");
        }
        return room;
    }

    /**
     * 创建房间
     */
    public Room create(RoomRequest request) {
        // 检查房间号是否重复
        LambdaQueryWrapper<Room> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Room::getRoomNo, request.getRoomNo());
        if (roomMapper.selectCount(wrapper) > 0) {
            throw BusinessException.badRequest("房间号已存在");
        }

        // 检查房型是否存在
        RoomType roomType = roomTypeMapper.selectById(request.getRoomTypeId());
        if (roomType == null) {
            throw BusinessException.badRequest("房型不存在");
        }

        Room room = new Room();
        BeanUtils.copyProperties(request, room);

        // 设置默认状态
        if (!StringUtils.hasText(room.getStatus())) {
            room.setStatus("available");
        }

        LocalDateTime now = LocalDateTime.now();
        room.setCreatedAt(now);
        room.setUpdatedAt(now);

        roomMapper.insert(room);

        log.info("创建房间成功: {}", room.getRoomNo());
        return room;
    }

    /**
     * 更新房间
     */
    public Room update(Long id, RoomRequest request) {
        Room room = getById(id);

        // 检查房间号是否与其他房间重复
        if (!room.getRoomNo().equals(request.getRoomNo())) {
            LambdaQueryWrapper<Room> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(Room::getRoomNo, request.getRoomNo());
            wrapper.ne(Room::getId, id);
            if (roomMapper.selectCount(wrapper) > 0) {
                throw BusinessException.badRequest("房间号已存在");
            }
        }

        // 检查房型是否存在
        RoomType roomType = roomTypeMapper.selectById(request.getRoomTypeId());
        if (roomType == null) {
            throw BusinessException.badRequest("房型不存在");
        }

        BeanUtils.copyProperties(request, room);
        room.setUpdatedAt(LocalDateTime.now());

        roomMapper.updateById(room);

        log.info("更新房间成功: {}", room.getRoomNo());
        return room;
    }

    /**
     * 修改房间状态
     */
    public Room updateStatus(Long id, String status) {
        Room room = getById(id);

        // 验证状态值
        List<String> validStatuses = Arrays.asList("available", "reserved", "occupied", "cleaning", "maintenance", "disabled");
        if (!validStatuses.contains(status)) {
            throw BusinessException.badRequest("无效的房间状态");
        }

        room.setStatus(status);
        room.setUpdatedAt(LocalDateTime.now());

        roomMapper.updateById(room);

        log.info("更新房间状态成功: {} -> {}", room.getRoomNo(), status);
        return room;
    }

    /**
     * 删除房间
     */
    public void delete(Long id) {
        Room room = getById(id);

        // 检查是否有未完成的订单
        LambdaQueryWrapper<BookingOrder> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BookingOrder::getRoomId, id);
        wrapper.in(BookingOrder::getStatus, "pending", "reserved", "occupied");
        long count = bookingOrderMapper.selectCount(wrapper);
        if (count > 0) {
            throw BusinessException.conflict("该房间还有未完成的订单，无法删除");
        }

        roomMapper.deleteById(id);

        log.info("删除房间成功: {}", room.getRoomNo());
    }
}
