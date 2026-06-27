package com.xingqi.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xingqi.entity.BookingOrder;
import com.xingqi.entity.Room;
import com.xingqi.mapper.BookingOrderMapper;
import com.xingqi.mapper.RoomMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final BookingOrderMapper bookingOrderMapper;
    private final RoomMapper roomMapper;

    public Map<String, Object> summary() {
        LocalDate today = LocalDate.now();
        LocalDate monthStart = today.withDayOfMonth(1);
        List<BookingOrder> orders = bookingOrderMapper.selectList(null);
        List<Room> rooms = roomMapper.selectList(null);

        long todayCheckIns = orders.stream()
                .filter(order -> today.equals(order.getCheckInDate()))
                .filter(this::isActiveOrder)
                .count();
        long todayCheckOuts = orders.stream()
                .filter(order -> today.equals(order.getCheckOutDate()))
                .filter(order -> !"cancelled".equals(order.getStatus()))
                .count();
        long todayNewOrders = orders.stream()
                .filter(order -> order.getCreatedAt() != null && today.equals(order.getCreatedAt().toLocalDate()))
                .count();

        BigDecimal todayRevenue = orders.stream()
                .filter(order -> today.equals(order.getCheckInDate()))
                .filter(order -> !"cancelled".equals(order.getStatus()))
                .map(this::safePaidAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal monthRevenue = orders.stream()
                .filter(order -> order.getCreatedAt() != null && !order.getCreatedAt().toLocalDate().isBefore(monthStart))
                .filter(order -> !"cancelled".equals(order.getStatus()))
                .map(this::safePaidAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        long availableRooms = rooms.stream().filter(room -> "available".equals(room.getStatus())).count();
        long occupiedRooms = rooms.stream().filter(room -> "occupied".equals(room.getStatus())).count();
        long totalRooms = rooms.size();
        BigDecimal occupancyRate = totalRooms == 0
                ? BigDecimal.ZERO
                : BigDecimal.valueOf(occupiedRooms * 100.0 / totalRooms).setScale(1, java.math.RoundingMode.HALF_UP);

        Map<String, Object> result = new HashMap<>();
        result.put("todayCheckIns", todayCheckIns);
        result.put("todayCheckOuts", todayCheckOuts);
        result.put("availableRooms", availableRooms);
        result.put("occupiedRooms", occupiedRooms);
        result.put("todayNewOrders", todayNewOrders);
        result.put("todayRevenue", todayRevenue);
        result.put("monthRevenue", monthRevenue);
        result.put("occupancyRate", occupancyRate);
        result.put("totalRooms", totalRooms);
        return result;
    }

    public List<Map<String, Object>> orderTrend() {
        LocalDate end = LocalDate.now();
        LocalDate start = end.minusDays(6);
        List<BookingOrder> orders = bookingOrderMapper.selectList(new LambdaQueryWrapper<BookingOrder>()
                .ge(BookingOrder::getCreatedAt, start.atStartOfDay()));

        List<Map<String, Object>> trend = new ArrayList<>();
        for (LocalDate date = start; !date.isAfter(end); date = date.plusDays(1)) {
            LocalDate current = date;
            long count = orders.stream()
                    .filter(order -> order.getCreatedAt() != null && current.equals(order.getCreatedAt().toLocalDate()))
                    .count();
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("date", current.toString());
            item.put("count", count);
            trend.add(item);
        }
        return trend;
    }

    public List<Map<String, Object>> revenueTrend() {
        LocalDate end = LocalDate.now();
        LocalDate start = end.minusDays(6);
        List<BookingOrder> orders = bookingOrderMapper.selectList(new LambdaQueryWrapper<BookingOrder>()
                .ge(BookingOrder::getCreatedAt, start.atStartOfDay()));

        List<Map<String, Object>> trend = new ArrayList<>();
        for (LocalDate date = start; !date.isAfter(end); date = date.plusDays(1)) {
            LocalDate current = date;
            BigDecimal amount = orders.stream()
                    .filter(order -> order.getCreatedAt() != null && current.equals(order.getCreatedAt().toLocalDate()))
                    .filter(order -> !"cancelled".equals(order.getStatus()))
                    .map(this::safePaidAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("date", current.toString());
            item.put("amount", amount);
            trend.add(item);
        }
        return trend;
    }

    public List<Map<String, Object>> roomStatus() {
        List<Room> rooms = roomMapper.selectList(null);
        Map<String, Long> counts = new LinkedHashMap<>();
        counts.put("available", 0L);
        counts.put("occupied", 0L);
        counts.put("cleaning", 0L);
        counts.put("maintenance", 0L);
        counts.put("disabled", 0L);

        for (Room room : rooms) {
            String status = room.getStatus() == null ? "available" : room.getStatus();
            counts.put(status, counts.getOrDefault(status, 0L) + 1);
        }

        return counts.entrySet().stream().map(entry -> {
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("status", entry.getKey());
            item.put("count", entry.getValue());
            return item;
        }).toList();
    }

    private boolean isActiveOrder(BookingOrder order) {
        return "pending".equals(order.getStatus())
                || "reserved".equals(order.getStatus())
                || "occupied".equals(order.getStatus());
    }

    private BigDecimal safePaidAmount(BookingOrder order) {
        return order.getPaidAmount() == null ? BigDecimal.ZERO : order.getPaidAmount();
    }
}
