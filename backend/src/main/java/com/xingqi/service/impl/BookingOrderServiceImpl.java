package com.xingqi.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xingqi.common.BusinessException;
import com.xingqi.dto.request.OrderCancelRequest;
import com.xingqi.dto.request.OrderPaymentRequest;
import com.xingqi.dto.request.OrderRequest;
import com.xingqi.entity.BookingOrder;
import com.xingqi.entity.Customer;
import com.xingqi.entity.Room;
import com.xingqi.mapper.BookingOrderMapper;
import com.xingqi.mapper.CustomerMapper;
import com.xingqi.mapper.RoomMapper;
import com.xingqi.service.BookingOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;

/**
 * 订单服务实现
 */
@Service
@RequiredArgsConstructor
public class BookingOrderServiceImpl implements BookingOrderService {

    private final BookingOrderMapper orderMapper;
    private final RoomMapper roomMapper;
    private final CustomerMapper customerMapper;

    @Override
    public Page<BookingOrder> page(Integer page, Integer pageSize, String keyword, String status, String startDate, String endDate) {
        Page<BookingOrder> pageRequest = new Page<>(page, pageSize);
        LambdaQueryWrapper<BookingOrder> wrapper = new LambdaQueryWrapper<>();

        if (StringUtils.hasText(keyword)) {
            wrapper.and(w -> w.like(BookingOrder::getOrderNo, keyword)
                    .or().like(BookingOrder::getCustomerName, keyword)
                    .or().like(BookingOrder::getCustomerPhone, keyword));
        }

        if (StringUtils.hasText(status)) {
            wrapper.eq(BookingOrder::getStatus, status);
        }

        if (StringUtils.hasText(startDate)) {
            wrapper.ge(BookingOrder::getCheckInDate, startDate);
        }

        if (StringUtils.hasText(endDate)) {
            wrapper.le(BookingOrder::getCheckOutDate, endDate);
        }

        wrapper.orderByDesc(BookingOrder::getCreatedAt);

        return orderMapper.selectPage(pageRequest, wrapper);
    }

    @Override
    public BookingOrder getById(Long id) {
        BookingOrder order = orderMapper.selectById(id);
        if (order == null) {
            throw new BusinessException(404, "订单不存在");
        }
        return order;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public BookingOrder create(OrderRequest request) {
        // 1. 校验房间是否存在
        Room room = roomMapper.selectById(request.getRoomId());
        if (room == null) {
            throw new BusinessException(404, "房间不存在");
        }

        // 2. 校验日期
        LocalDate checkInDate = LocalDate.parse(request.getCheckInDate());
        LocalDate checkOutDate = LocalDate.parse(request.getCheckOutDate());

        if (!checkOutDate.isAfter(checkInDate)) {
            throw new BusinessException(400, "离店日期必须晚于入住日期");
        }

        // 3. 检查日期冲突
        checkDateConflict(request.getRoomId(), request.getCheckInDate(), request.getCheckOutDate(), null);

        // 4. 计算入住晚数和金额
        long nights = ChronoUnit.DAYS.between(checkInDate, checkOutDate);
        BigDecimal totalAmount = room.getPrice().multiply(BigDecimal.valueOf(nights));

        // 5. 查找或创建客户
        Long customerId = findOrCreateCustomer(request.getCustomerName(), request.getCustomerPhone());

        // 6. 生成订单编号
        String orderNo = generateOrderNo();

        // 7. 创建订单
        BookingOrder order = new BookingOrder();
        order.setOrderNo(orderNo);
        order.setCustomerId(customerId);
        order.setCustomerName(request.getCustomerName());
        order.setCustomerPhone(request.getCustomerPhone());
        order.setRoomId(room.getId());
        order.setRoomNo(room.getRoomNo());
        order.setCheckInDate(request.getCheckInDate());
        order.setCheckOutDate(request.getCheckOutDate());
        order.setNights((int) nights);
        order.setTotalAmount(totalAmount);
        order.setPaidAmount(request.getPaidAmount() != null ? request.getPaidAmount() : BigDecimal.ZERO);
        order.setPaymentMethod(request.getPaymentMethod());
        order.setSource(request.getSource() != null ? request.getSource() : "front_desk");
        order.setStatus("pending");
        order.setRemark(request.getRemark());
        order.setCreatedAt(LocalDateTime.now());
        order.setUpdatedAt(LocalDateTime.now());

        orderMapper.insert(order);

        return order;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public BookingOrder update(Long id, OrderRequest request) {
        BookingOrder order = getById(id);

        if (!"pending".equals(order.getStatus()) && !"reserved".equals(order.getStatus())) {
            throw new BusinessException(400, "当前状态不允许修改订单");
        }

        // 校验房间
        Room room = roomMapper.selectById(request.getRoomId());
        if (room == null) {
            throw new BusinessException(404, "房间不存在");
        }

        // 校验日期
        LocalDate checkInDate = LocalDate.parse(request.getCheckInDate());
        LocalDate checkOutDate = LocalDate.parse(request.getCheckOutDate());

        if (!checkOutDate.isAfter(checkInDate)) {
            throw new BusinessException(400, "离店日期必须晚于入住日期");
        }

        // 检查日期冲突（排除当前订单）
        checkDateConflict(request.getRoomId(), request.getCheckInDate(), request.getCheckOutDate(), id);

        // 计算入住晚数和金额
        long nights = ChronoUnit.DAYS.between(checkInDate, checkOutDate);
        BigDecimal totalAmount = room.getPrice().multiply(BigDecimal.valueOf(nights));

        // 更新客户
        Long customerId = findOrCreateCustomer(request.getCustomerName(), request.getCustomerPhone());

        order.setCustomerId(customerId);
        order.setCustomerName(request.getCustomerName());
        order.setCustomerPhone(request.getCustomerPhone());
        order.setRoomId(room.getId());
        order.setRoomNo(room.getRoomNo());
        order.setCheckInDate(request.getCheckInDate());
        order.setCheckOutDate(request.getCheckOutDate());
        order.setNights((int) nights);
        order.setTotalAmount(totalAmount);
        order.setPaymentMethod(request.getPaymentMethod());
        order.setSource(request.getSource());
        order.setRemark(request.getRemark());
        order.setUpdatedAt(LocalDateTime.now());

        orderMapper.updateById(order);

        return order;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public BookingOrder confirm(Long id) {
        BookingOrder order = getById(id);

        if (!"pending".equals(order.getStatus())) {
            throw new BusinessException(400, "只有待确认订单可以确认");
        }

        order.setStatus("reserved");
        order.setUpdatedAt(LocalDateTime.now());
        orderMapper.updateById(order);

        // 更新房间状态
        Room room = roomMapper.selectById(order.getRoomId());
        if (room != null && "available".equals(room.getStatus())) {
            room.setStatus("reserved");
            room.setUpdatedAt(LocalDateTime.now());
            roomMapper.updateById(room);
        }

        return order;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public BookingOrder checkIn(Long id) {
        BookingOrder order = getById(id);

        if (!"reserved".equals(order.getStatus()) && !"pending".equals(order.getStatus())) {
            throw new BusinessException(400, "只有已预订或待确认订单可以办理入住");
        }

        order.setStatus("occupied");
        order.setUpdatedAt(LocalDateTime.now());
        orderMapper.updateById(order);

        // 更新房间状态
        Room room = roomMapper.selectById(order.getRoomId());
        if (room != null) {
            room.setStatus("occupied");
            room.setUpdatedAt(LocalDateTime.now());
            roomMapper.updateById(room);
        }

        return order;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public BookingOrder checkOut(Long id) {
        BookingOrder order = getById(id);

        if (!"occupied".equals(order.getStatus())) {
            throw new BusinessException(400, "只有已入住订单可以办理退房");
        }

        order.setStatus("completed");
        order.setUpdatedAt(LocalDateTime.now());
        orderMapper.updateById(order);

        // 更新房间状态为清洁中
        Room room = roomMapper.selectById(order.getRoomId());
        if (room != null) {
            room.setStatus("cleaning");
            room.setUpdatedAt(LocalDateTime.now());
            roomMapper.updateById(room);
        }

        return order;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public BookingOrder cancel(Long id, OrderCancelRequest request) {
        BookingOrder order = getById(id);

        if (!"pending".equals(order.getStatus()) && !"reserved".equals(order.getStatus())) {
            throw new BusinessException(400, "当前状态不允许取消订单");
        }

        order.setStatus("cancelled");
        order.setCancelReason(request.getCancelReason());
        order.setUpdatedAt(LocalDateTime.now());
        orderMapper.updateById(order);

        // 恢复房间状态
        Room room = roomMapper.selectById(order.getRoomId());
        if (room != null && ("reserved".equals(room.getStatus()) || "occupied".equals(room.getStatus()))) {
            room.setStatus("available");
            room.setUpdatedAt(LocalDateTime.now());
            roomMapper.updateById(room);
        }

        return order;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public BookingOrder recordPayment(Long id, OrderPaymentRequest request) {
        BookingOrder order = getById(id);

        BigDecimal newPaidAmount = order.getPaidAmount().add(request.getAmount());

        if (newPaidAmount.compareTo(order.getTotalAmount()) > 0) {
            throw new BusinessException(400, "已付金额不能超过订单总额");
        }

        order.setPaidAmount(newPaidAmount);
        order.setPaymentMethod(request.getPaymentMethod());
        order.setUpdatedAt(LocalDateTime.now());
        orderMapper.updateById(order);

        return order;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        BookingOrder order = getById(id);

        if ("occupied".equals(order.getStatus())) {
            throw new BusinessException(400, "已入住订单不能删除");
        }

        orderMapper.deleteById(id);
    }

    /**
     * 检查日期冲突
     */
    private void checkDateConflict(Long roomId, String checkInDate, String checkOutDate, Long excludeOrderId) {
        LambdaQueryWrapper<BookingOrder> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BookingOrder::getRoomId, roomId);
        wrapper.in(BookingOrder::getStatus, List.of("pending", "reserved", "occupied"));

        // 排除当前订单
        if (excludeOrderId != null) {
            wrapper.ne(BookingOrder::getId, excludeOrderId);
        }

        // 日期冲突判断：新订单入住日期 < 已有订单离店日期 且 新订单离店日期 > 已有订单入住日期
        wrapper.and(w -> w.lt(BookingOrder::getCheckOutDate, checkOutDate)
                .gt(BookingOrder::getCheckInDate, checkInDate));

        List<BookingOrder> conflictOrders = orderMapper.selectList(wrapper);

        if (!conflictOrders.isEmpty()) {
            throw new BusinessException(409, "该房间在此日期范围内已有订单，无法预订");
        }
    }

    /**
     * 查找或创建客户
     */
    private Long findOrCreateCustomer(String name, String phone) {
        LambdaQueryWrapper<Customer> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Customer::getPhone, phone);
        Customer customer = customerMapper.selectOne(wrapper);

        if (customer == null) {
            customer = new Customer();
            customer.setName(name);
            customer.setPhone(phone);
            customer.setCreatedAt(LocalDateTime.now());
            customer.setUpdatedAt(LocalDateTime.now());
            customerMapper.insert(customer);
        } else {
            // 更新客户姓名
            customer.setName(name);
            customer.setUpdatedAt(LocalDateTime.now());
            customerMapper.updateById(customer);
        }

        return customer.getId();
    }

    /**
     * 生成订单编号
     */
    private String generateOrderNo() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        String timestamp = LocalDateTime.now().format(formatter);
        int random = (int) (Math.random() * 10000);
        return "XQ" + timestamp + String.format("%04d", random);
    }
}
