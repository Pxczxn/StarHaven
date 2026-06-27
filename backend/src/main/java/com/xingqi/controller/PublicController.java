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
import com.xingqi.service.SiteSettingService;
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
    private final SiteSettingService siteSettingService;

    /**
     * 获取站点信息
     */
    @GetMapping("/site-info")
    public ApiResponse<Map<String, String>> getSiteInfo() {
        Map<String, String> info = new HashMap<>();
        var setting = siteSettingService.getSiteSetting();
        info.put("brandName", setting.getBrandName());
        info.put("slogan", setting.getSlogan());
        info.put("phone", setting.getPhone());
        info.put("address", setting.getAddress());
        info.put("description", setting.getDescription());
        info.put("heroImageUrl", setting.getHeroImageUrl());
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
     * 订单查询（订单尾号/完整订单号/手机号，至少提供一个）
     */
    @GetMapping("/orders/query")
    public ApiResponse<List<BookingOrder>> queryOrder(
            @RequestParam(required = false) String orderNo,
            @RequestParam(required = false) String phone) {

        List<BookingOrder> orders = findOrdersByNoOrTail(orderNo, phone);
        if (orders.isEmpty()) {
            throw BusinessException.notFound("订单不存在");
        }

        return ApiResponse.success(orders);
    }

    /**
     * 获取订单详情（需验证手机号）
     */
    @GetMapping("/orders/{orderNo}")
    public ApiResponse<BookingOrder> getOrderByNo(
            @PathVariable String orderNo,
            @RequestParam String phone) {

        BookingOrder order = findOrderByNoOrTail(orderNo, phone);
        if (order == null) {
            throw BusinessException.notFound("订单不存在");
        }

        return ApiResponse.success(order);
    }

    private BookingOrder findOrderByNoOrTail(String orderNo, String phone) {
        List<BookingOrder> orders = findOrdersByNoOrTail(orderNo, phone);
        return orders.isEmpty() ? null : orders.get(0);
    }

    private List<BookingOrder> findOrdersByNoOrTail(String orderNo, String phone) {
        String trimmedOrderNo = orderNo == null ? "" : orderNo.trim();
        String trimmedPhone = phone == null ? "" : phone.trim();

        if (trimmedOrderNo.isEmpty() && trimmedPhone.isEmpty()) {
            return List.of();
        }

        LambdaQueryWrapper<BookingOrder> wrapper = new LambdaQueryWrapper<>();
        if (!trimmedPhone.isEmpty()) {
            wrapper.eq(BookingOrder::getCustomerPhone, trimmedPhone);
        }

        if (trimmedOrderNo.length() == 6 && trimmedOrderNo.chars().allMatch(Character::isDigit)) {
            wrapper.likeRight(BookingOrder::getOrderNo, "ORD");
            wrapper.like(BookingOrder::getOrderNo, trimmedOrderNo);
            wrapper.orderByDesc(BookingOrder::getCreatedAt);
            List<BookingOrder> orders = bookingOrderMapper.selectList(wrapper);
            return orders.stream()
                    .filter(order -> order.getOrderNo() != null && order.getOrderNo().endsWith(trimmedOrderNo))
                    .toList();
        }

        if (!trimmedOrderNo.isEmpty()) {
            wrapper.eq(BookingOrder::getOrderNo, trimmedOrderNo);
        }
        wrapper.orderByDesc(BookingOrder::getCreatedAt);
        return bookingOrderMapper.selectList(wrapper);
    }
}
