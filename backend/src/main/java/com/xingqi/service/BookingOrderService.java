package com.xingqi.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xingqi.common.BusinessException;
import com.xingqi.entity.BookingOrder;
import com.xingqi.entity.Customer;
import com.xingqi.entity.Room;
import com.xingqi.mapper.BookingOrderMapper;
import com.xingqi.mapper.CustomerMapper;
import com.xingqi.mapper.RoomMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookingOrderService {

    private final BookingOrderMapper bookingOrderMapper;
    private final RoomMapper roomMapper;
    private final CustomerMapper customerMapper;
    private final CustomerService customerService;

    public Page<BookingOrder> page(Integer pageNum, Integer pageSize, String keyword, String status) {
        Page<BookingOrder> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<BookingOrder> wrapper = new LambdaQueryWrapper<>();

        if (keyword != null && !keyword.trim().isEmpty()) {
            wrapper.and(w -> w.like(BookingOrder::getOrderNo, keyword)
                    .or().like(BookingOrder::getCustomerName, keyword)
                    .or().like(BookingOrder::getCustomerPhone, keyword)
                    .or().like(BookingOrder::getRoomNo, keyword));
        }

        if (status != null && !status.trim().isEmpty()) {
            wrapper.eq(BookingOrder::getStatus, status);
        }

        wrapper.orderByDesc(BookingOrder::getCreatedAt);
        return bookingOrderMapper.selectPage(page, wrapper);
    }

    public BookingOrder getById(Long id) {
        BookingOrder order = bookingOrderMapper.selectById(id);
        if (order == null) {
            throw BusinessException.notFound("订单不存在");
        }
        return order;
    }

    @Transactional(rollbackFor = Exception.class)
    public BookingOrder create(BookingOrder order) {
        Room room = roomMapper.selectById(order.getRoomId());
        if (room == null) {
            throw BusinessException.badRequest("房间不存在");
        }

        if (!isRoomAvailable(order.getRoomId(), order.getCheckInDate(), order.getCheckOutDate(), null)) {
            throw BusinessException.badRequest("该房间在选定日期已被预订");
        }

        Customer customer;
        if (order.getCustomerId() != null) {
            customer = customerMapper.selectById(order.getCustomerId());
            if (customer == null) {
                throw BusinessException.badRequest("客户不存在");
            }
        } else {
            customer = customerService.getOrCreateByPhone(order.getCustomerPhone(), order.getCustomerName(), order.getCustomerIdNumber());
            order.setCustomerId(customer.getId());
        }

        long nights = ChronoUnit.DAYS.between(order.getCheckInDate(), order.getCheckOutDate());
        if (nights <= 0) {
            throw BusinessException.badRequest("退房日期必须晚于入住日期");
        }
        order.setNights((int) nights);

        BigDecimal totalAmount = room.getPrice().multiply(BigDecimal.valueOf(nights));
        order.setTotalAmount(totalAmount);
        order.setOrderNo(generateOrderNo());
        order.setRoomNo(room.getRoomNo());

        if (order.getStatus() == null) order.setStatus("pending");
        if (order.getPaymentStatus() == null) order.setPaymentStatus("unpaid");
        if (order.getPaidAmount() == null) order.setPaidAmount(BigDecimal.ZERO);
        if (order.getSource() == null) order.setSource("front_desk");  // 默认为前台

        order.setCreatedAt(LocalDateTime.now());
        order.setUpdatedAt(LocalDateTime.now());
        bookingOrderMapper.insert(order);

        log.info("创建订单: {}", order.getOrderNo());
        return order;
    }

    @Transactional(rollbackFor = Exception.class)
    public void confirm(Long id) {
        BookingOrder order = getById(id);
        if (!"pending".equals(order.getStatus())) {
            throw BusinessException.badRequest("只有待确认状态的订单可以确认");
        }
        order.setStatus("reserved");
        order.setUpdatedAt(LocalDateTime.now());
        bookingOrderMapper.updateById(order);
        log.info("确认订单: {}", order.getOrderNo());
    }

    @Transactional(rollbackFor = Exception.class)
    public void checkIn(Long id) {
        BookingOrder order = getById(id);
        if (!"reserved".equals(order.getStatus())) {
            throw BusinessException.badRequest("只有已预订状态的订单可以办理入住");
        }

        // 检查支付状态，未支付不能办理入住
        if (!"paid".equals(order.getPaymentStatus())) {
            throw BusinessException.badRequest("订单尚未支付完成，无法办理入住");
        }

        order.setStatus("occupied");
        order.setUpdatedAt(LocalDateTime.now());
        bookingOrderMapper.updateById(order);

        Room room = roomMapper.selectById(order.getRoomId());
        room.setStatus("occupied");
        room.setUpdatedAt(LocalDateTime.now());
        roomMapper.updateById(room);
        log.info("办理入住: {}", order.getOrderNo());
    }

    @Transactional(rollbackFor = Exception.class)
    public void checkOut(Long id) {
        BookingOrder order = getById(id);
        if (!"occupied".equals(order.getStatus())) {
            throw BusinessException.badRequest("只有已入住状态的订单可以办理退房");
        }
        order.setStatus("checked_out");
        order.setUpdatedAt(LocalDateTime.now());
        bookingOrderMapper.updateById(order);

        Room room = roomMapper.selectById(order.getRoomId());
        room.setStatus("cleaning");
        room.setUpdatedAt(LocalDateTime.now());
        roomMapper.updateById(room);
        log.info("办理退房: {}", order.getOrderNo());
    }

    @Transactional(rollbackFor = Exception.class)
    public void cancel(Long id) {
        BookingOrder order = getById(id);
        if ("checked_out".equals(order.getStatus()) || "cancelled".equals(order.getStatus())) {
            throw BusinessException.badRequest("已退房或已取消的订单无法取消");
        }
        if ("occupied".equals(order.getStatus())) {
            Room room = roomMapper.selectById(order.getRoomId());
            room.setStatus("cleaning");
            room.setUpdatedAt(LocalDateTime.now());
            roomMapper.updateById(room);
        }
        order.setStatus("cancelled");
        order.setUpdatedAt(LocalDateTime.now());
        bookingOrderMapper.updateById(order);
        log.info("取消订单: {}", order.getOrderNo());
    }

    /**
     * 收款
     */
    @Transactional(rollbackFor = Exception.class)
    public void payment(Long id, BigDecimal amount, String paymentMethod) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw BusinessException.badRequest("收款金额必须大于0");
        }

        BookingOrder order = getById(id);

        if ("cancelled".equals(order.getStatus()) || "checked_out".equals(order.getStatus())) {
            throw BusinessException.badRequest("已取消或已退房的订单无法收款");
        }

        // 计算新的已付金额
        BigDecimal newPaidAmount = order.getPaidAmount().add(amount);

        // 检查是否超过订单总额
        if (newPaidAmount.compareTo(order.getTotalAmount()) > 0) {
            throw BusinessException.badRequest("收款金额不能超过订单应付金额");
        }

        // 更新已付金额
        order.setPaidAmount(newPaidAmount);

        // 更新支付状态
        if (newPaidAmount.compareTo(BigDecimal.ZERO) > 0 && newPaidAmount.compareTo(order.getTotalAmount()) < 0) {
            order.setPaymentStatus("partial");  // 部分支付
        } else if (newPaidAmount.compareTo(order.getTotalAmount()) == 0) {
            order.setPaymentStatus("paid");  // 已支付
        }

        order.setUpdatedAt(LocalDateTime.now());
        bookingOrderMapper.updateById(order);

        log.info("订单收款: {} - 收款金额: {} - 支付方式: {} - 已付总额: {}",
                order.getOrderNo(), amount, paymentMethod, newPaidAmount);
    }

    private boolean isRoomAvailable(Long roomId, LocalDate checkIn, LocalDate checkOut, Long excludeOrderId) {
        LambdaQueryWrapper<BookingOrder> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BookingOrder::getRoomId, roomId);
        wrapper.in(BookingOrder::getStatus, "pending", "reserved", "occupied");
        if (excludeOrderId != null) wrapper.ne(BookingOrder::getId, excludeOrderId);

        List<BookingOrder> orders = bookingOrderMapper.selectList(wrapper);
        for (BookingOrder order : orders) {
            if (!(checkOut.isBefore(order.getCheckInDate()) || checkOut.isEqual(order.getCheckInDate())
                    || checkIn.isAfter(order.getCheckOutDate()) || checkIn.isEqual(order.getCheckOutDate()))) {
                return false;
            }
        }
        return true;
    }

    private String generateOrderNo() {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        int random = (int) (Math.random() * 9000) + 1000;
        return "ORD" + timestamp + random;
    }
}
