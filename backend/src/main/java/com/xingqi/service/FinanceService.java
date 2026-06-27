package com.xingqi.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xingqi.entity.BookingOrder;
import com.xingqi.mapper.BookingOrderMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class FinanceService {

    private final BookingOrderMapper bookingOrderMapper;

    public Map<String, Object> summary(LocalDate startDate, LocalDate endDate) {
        List<BookingOrder> orders = listOrders(startDate, endDate);
        BigDecimal totalRevenue = orders.stream()
                .filter(this::isIncomeOrder)
                .map(this::safePaidAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal receivable = orders.stream()
                .filter(order -> !"cancelled".equals(order.getStatus()))
                .map(this::safeTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal unpaidAmount = receivable.subtract(totalRevenue);
        if (unpaidAmount.compareTo(BigDecimal.ZERO) < 0) {
            unpaidAmount = BigDecimal.ZERO;
        }

        long paidOrders = orders.stream().filter(order -> "paid".equals(order.getPaymentStatus())).count();
        long unpaidOrders = orders.stream().filter(this::isUnpaidOrder).count();
        long refundOrders = orders.stream().filter(this::isRefundOrder).count();

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("totalRevenue", totalRevenue);
        result.put("receivable", receivable);
        result.put("unpaidAmount", unpaidAmount);
        result.put("orderCount", orders.size());
        result.put("paidOrders", paidOrders);
        result.put("unpaidOrders", unpaidOrders);
        result.put("refundOrders", refundOrders);
        return result;
    }

    public List<Map<String, Object>> revenue(LocalDate startDate, LocalDate endDate) {
        LocalDate start = startDate != null ? startDate : LocalDate.now().minusDays(6);
        LocalDate end = endDate != null ? endDate : LocalDate.now();
        List<BookingOrder> orders = listOrders(start, end);

        List<Map<String, Object>> result = new ArrayList<>();
        for (LocalDate date = start; !date.isAfter(end); date = date.plusDays(1)) {
            LocalDate current = date;
            BigDecimal amount = orders.stream()
                    .filter(order -> order.getCreatedAt() != null && current.equals(order.getCreatedAt().toLocalDate()))
                    .filter(this::isIncomeOrder)
                    .map(this::safePaidAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("date", current.toString());
            item.put("amount", amount);
            result.add(item);
        }
        return result;
    }

    public List<Map<String, Object>> paymentMethods(LocalDate startDate, LocalDate endDate) {
        Map<String, BigDecimal> amountMap = new LinkedHashMap<>();
        Map<String, Long> countMap = new LinkedHashMap<>();

        for (BookingOrder order : listOrders(startDate, endDate)) {
            if (!isIncomeOrder(order)) {
                continue;
            }
            String method = order.getPaymentMethod() == null || order.getPaymentMethod().isBlank()
                    ? "unknown"
                    : order.getPaymentMethod();
            amountMap.put(method, amountMap.getOrDefault(method, BigDecimal.ZERO).add(safePaidAmount(order)));
            countMap.put(method, countMap.getOrDefault(method, 0L) + 1);
        }

        return amountMap.entrySet().stream().map(entry -> {
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("method", entry.getKey());
            item.put("amount", entry.getValue());
            item.put("count", countMap.getOrDefault(entry.getKey(), 0L));
            return item;
        }).toList();
    }

    public List<BookingOrder> unpaidOrders() {
        LambdaQueryWrapper<BookingOrder> wrapper = new LambdaQueryWrapper<>();
        wrapper.ne(BookingOrder::getStatus, "cancelled")
                .and(w -> w.ne(BookingOrder::getPaymentStatus, "paid")
                        .or().isNull(BookingOrder::getPaymentStatus))
                .orderByDesc(BookingOrder::getCreatedAt);
        return bookingOrderMapper.selectList(wrapper);
    }

    public List<BookingOrder> refundOrders() {
        LambdaQueryWrapper<BookingOrder> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BookingOrder::getPaymentStatus, "refunded")
                .or().eq(BookingOrder::getStatus, "refunded")
                .orderByDesc(BookingOrder::getCreatedAt);
        return bookingOrderMapper.selectList(wrapper);
    }

    public List<BookingOrder> exportData(LocalDate startDate, LocalDate endDate) {
        return listOrders(startDate, endDate);
    }

    private List<BookingOrder> listOrders(LocalDate startDate, LocalDate endDate) {
        LambdaQueryWrapper<BookingOrder> wrapper = new LambdaQueryWrapper<>();
        if (startDate != null) {
            wrapper.ge(BookingOrder::getCreatedAt, startDate.atStartOfDay());
        }
        if (endDate != null) {
            wrapper.lt(BookingOrder::getCreatedAt, endDate.plusDays(1).atStartOfDay());
        }
        wrapper.orderByDesc(BookingOrder::getCreatedAt);
        return bookingOrderMapper.selectList(wrapper);
    }

    private boolean isIncomeOrder(BookingOrder order) {
        return !"cancelled".equals(order.getStatus()) && !"refunded".equals(order.getPaymentStatus());
    }

    private boolean isUnpaidOrder(BookingOrder order) {
        return !"cancelled".equals(order.getStatus()) && !"paid".equals(order.getPaymentStatus());
    }

    private boolean isRefundOrder(BookingOrder order) {
        return "refunded".equals(order.getPaymentStatus()) || "refunded".equals(order.getStatus());
    }

    private BigDecimal safePaidAmount(BookingOrder order) {
        return order.getPaidAmount() == null ? BigDecimal.ZERO : order.getPaidAmount();
    }

    private BigDecimal safeTotalAmount(BookingOrder order) {
        return order.getTotalAmount() == null ? BigDecimal.ZERO : order.getTotalAmount();
    }
}
