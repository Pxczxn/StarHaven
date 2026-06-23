package com.xingqi.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xingqi.common.BusinessException;
import com.xingqi.entity.BookingOrder;
import com.xingqi.entity.Room;
import com.xingqi.entity.RoomType;
import com.xingqi.mapper.BookingOrderMapper;
import com.xingqi.mapper.RoomMapper;
import com.xingqi.mapper.RoomTypeMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 房间服务
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RoomService {

    private final RoomMapper roomMapper;
    private final RoomTypeMapper roomTypeMapper;
    private final BookingOrderMapper bookingOrderMapper;

    /**
     * 分页查询房间
     */
    public Page<Room> page(Integer pageNum, Integer pageSize, String keyword, String status, Long roomTypeId) {
        Page<Room> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Room> wrapper = new LambdaQueryWrapper<>();

        // 关键词搜索（房间号、名称）
        if (keyword != null && !keyword.trim().isEmpty()) {
            wrapper.and(w -> w.like(Room::getRoomNo, keyword)
                    .or().like(Room::getName, keyword));
        }

        // 房态筛选
        if (status != null && !status.trim().isEmpty()) {
            wrapper.eq(Room::getStatus, status);
        }

        // 房型筛选
        if (roomTypeId != null) {
            wrapper.eq(Room::getRoomTypeId, roomTypeId);
        }

        // 按房间号排序
        wrapper.orderByAsc(Room::getRoomNo);

        return roomMapper.selectPage(page, wrapper);
    }

    /**
     * 根据 ID 查询房间
     */
    public Room getById(Long id) {
        Room room = roomMapper.selectById(id);
        if (room == null) {
            throw BusinessException.notFound("房间不存在");
        }
        return room;
    }

    /**
     * 新增房间
     */
    @Transactional(rollbackFor = Exception.class)
    public Room create(Room room) {
        // 检查房间号是否重复
        LambdaQueryWrapper<Room> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Room::getRoomNo, room.getRoomNo());
        if (roomMapper.selectCount(wrapper) > 0) {
            throw BusinessException.badRequest("房间号已存在");
        }

        // 检查房型是否存在
        RoomType roomType = roomTypeMapper.selectById(room.getRoomTypeId());
        if (roomType == null) {
            throw BusinessException.badRequest("房型不存在");
        }

        room.setCreatedAt(LocalDateTime.now());
        room.setUpdatedAt(LocalDateTime.now());

        if (room.getStatus() == null) {
            room.setStatus("available");
        }

        // 如果未设置价格，使用房型默认价格
        if (room.getPrice() == null) {
            room.setPrice(roomType.getDefaultPrice());
        }

        // 如果未设置容量，使用房型容量
        if (room.getCapacity() == null) {
            room.setCapacity(roomType.getCapacity());
        }

        roomMapper.insert(room);
        log.info("创建房间成功: {}", room.getRoomNo());
        return room;
    }

    /**
     * 更新房间
     */
    @Transactional(rollbackFor = Exception.class)
    public Room update(Long id, Room room) {
        Room existing = getById(id);

        // 检查房间号是否与其他房间重复
        if (!existing.getRoomNo().equals(room.getRoomNo())) {
            LambdaQueryWrapper<Room> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(Room::getRoomNo, room.getRoomNo());
            wrapper.ne(Room::getId, id);
            if (roomMapper.selectCount(wrapper) > 0) {
                throw BusinessException.badRequest("房间号已存在");
            }
        }

        // 检查房型是否存在
        RoomType roomType = roomTypeMapper.selectById(room.getRoomTypeId());
        if (roomType == null) {
            throw BusinessException.badRequest("房型不存在");
        }

        room.setId(id);
        room.setCreatedAt(existing.getCreatedAt());
        room.setUpdatedAt(LocalDateTime.now());

        roomMapper.updateById(room);
        log.info("更新房间成功: {}", room.getRoomNo());
        return room;
    }

    /**
     * 删除房间
     */
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        Room room = getById(id);

        // 检查是否有未完成的订单关联该房间
        LambdaQueryWrapper<BookingOrder> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BookingOrder::getRoomId, id);
        wrapper.in(BookingOrder::getStatus, "pending", "reserved", "occupied");
        long count = bookingOrderMapper.selectCount(wrapper);
        if (count > 0) {
            throw BusinessException.badRequest("该房间有 " + count + " 个未完成订单，无法删除");
        }

        roomMapper.deleteById(id);
        log.info("删除房间成功: {}", room.getRoomNo());
    }

    /**
     * 更新房间状态
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateStatus(Long id, String status) {
        Room room = getById(id);
        room.setStatus(status);
        room.setUpdatedAt(LocalDateTime.now());
        roomMapper.updateById(room);
        log.info("更新房间状态: {} -> {}", room.getRoomNo(), status);
    }

    /**
     * 查询所有房间（用于下拉选择）
     */
    public List<Room> listAll() {
        LambdaQueryWrapper<Room> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByAsc(Room::getRoomNo);
        return roomMapper.selectList(wrapper);
    }
}
