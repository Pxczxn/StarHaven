package com.xingqi.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xingqi.common.ApiResponse;
import com.xingqi.common.BusinessException;
import com.xingqi.entity.BookingOrder;
import com.xingqi.entity.Room;
import com.xingqi.entity.RoomType;
import com.xingqi.mapper.BookingOrderMapper;
import com.xingqi.mapper.RoomMapper;
import com.xingqi.service.BookingOrderService;
import com.xingqi.service.RoomTypeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Public 接口控制器（供 UniApp 使用，无需登录）
 */
@Slf4j
@RestController
@RequestMapping("/api/public")
@RequiredArgsConstructor
public class PublicController {

    private final RoomTypeService roomTypeService;
    private final RoomMapper roomMapper;
    private final BookingOrderMapper bookingOrderMapper;
    private final BookingOrderService bookingOrderService;

    /**
     * 获取站点信息
     */
    @GetMapping("/site-info")
    public ApiResponse<Map<String, String>> getSiteInfo() {
        Map<String, String> info = new HashMap<>();
        info.put("brandName", "星栖民宿");
        info.put("slogan", "星辰为引，栖心而居");
        info.put("phone", "400-888-8888");
        info.put("address", "某市某区某街道123号");
        info.put("description", "在星空下，找到心灵的栖息地");
        return ApiResponse.success(info);
    }

    /**
     * 获取启用的房型列表
     */
    @GetMapping("/room-types")
    public ApiResponse<List<RoomType>> listRoomTypes() {
        List<RoomType> roomTypes = roomTypeService.listEnabled();
        return ApiResponse.success(roomTypes);
    }

    /**
     * 获取房型详情
     */
    @GetMapping("/room-types/{id}")
    public ApiResponse<RoomType> getRoomType(@PathVariable Long id) {
        RoomType roomType = roomTypeService.getById(id);

        // 只返回启用的房型
        if (roomType == null || !"enabled".equals(roomType.getStatus())) {
            return ApiResponse.error(404, "房型不存在或已下架");
        }

        return ApiResponse.success(roomType);
    }

    /**
     * 查询可订房间
     */
    @GetMapping("/available-rooms")
    public ApiResponse<List<Room>> getAvailableRooms(
            @RequestParam String checkInDate,
            @RequestParam String checkOutDate) {

        LocalDate checkIn = LocalDate.parse(checkInDate);
        LocalDate checkOut = LocalDate.parse(checkOutDate);

        if (checkOut.isBefore(checkIn) || checkOut.isEqual(checkIn)) {
            throw BusinessException.badRequest("离店日期必须晚于入住日期");
        }

        // 查询所有可用房间
        LambdaQueryWrapper<Room> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Room::getStatus, "available");
        wrapper.orderByAsc(Room::getRoomNo);
        List<Room> allRooms = roomMapper.selectList(wrapper);

        // 过滤出在指定日期可预订的房间
        List<Room> availableRooms = allRooms.stream()
                .filter(room -> bookingOrderService.isRoomAvailable(room.getId(), checkIn, checkOut, null))
                .toList();

        return ApiResponse.success(availableRooms);
    }

    /**
     * 创建订单（无需登录）
     */
    @PostMapping("/orders")
    public ApiResponse<BookingOrder> createOrder(@RequestBody BookingOrder order) {
        // 设置订单来源为 uniapp
        order.setSource("uniapp");
        BookingOrder created = bookingOrderService.create(order);
        log.info("UniApp 创建订单: {}", created.getOrderNo());
        return ApiResponse.success(created);
    }

    /**
     * 订单查询（订单号 + 手机号验证）
     */
    @GetMapping("/orders/query")
    public ApiResponse<BookingOrder> queryOrder(
            @RequestParam String orderNo,
            @RequestParam String phone) {

        LambdaQueryWrapper<BookingOrder> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BookingOrder::getOrderNo, orderNo);
        wrapper.eq(BookingOrder::getCustomerPhone, phone);
        BookingOrder order = bookingOrderMapper.selectOne(wrapper);

        if (order == null) {
            throw BusinessException.notFound("订单不存在或手机号不匹配");
        }

        return ApiResponse.success(order);
    }

    /**
     * 获取订单详情（需验证手机号）
     */
    @GetMapping("/orders/{orderNo}")
    public ApiResponse<BookingOrder> getOrderByNo(
            @PathVariable String orderNo,
            @RequestParam String phone) {

        LambdaQueryWrapper<BookingOrder> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BookingOrder::getOrderNo, orderNo);
        BookingOrder order = bookingOrderMapper.selectOne(wrapper);

        if (order == null) {
            throw BusinessException.notFound("订单不存在");
        }

        if (!order.getCustomerPhone().equals(phone)) {
            throw BusinessException.forbidden("无权查看该订单");
        }

        return ApiResponse.success(order);
    }
}
