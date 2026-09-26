package top.pxczxn.business.service;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import top.pxczxn.business.dto.CreateOrderDTO;
import top.pxczxn.business.entity.BookingOrder;
import top.pxczxn.business.entity.House;
import top.pxczxn.business.entity.Message;
import top.pxczxn.business.mapper.BookingOrderMapper;
import top.pxczxn.business.mapper.HouseMapper;
import top.pxczxn.business.mapper.MessageMapper;
import top.pxczxn.business.vo.OrderVO;
import top.pxczxn.common.constant.RedisKeys;
import top.pxczxn.common.exception.BusinessException;
import top.pxczxn.common.redis.RedisFacade;
import top.pxczxn.common.result.PageData;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {

    private final BookingOrderMapper orderMapper;
    private final HouseMapper houseMapper;
    private final MessageMapper messageMapper;
    private final RedisFacade redisFacade;

    @Transactional
    public OrderVO create(CreateOrderDTO dto) {
        long userId = StpUtil.getLoginIdAsLong();
        if (!dto.getCheckOutDate().isAfter(dto.getCheckInDate())) {
            throw new BusinessException("离店日期必须晚于入住日期");
        }
        String lockKey = RedisKeys.ORDER_LOCK + dto.getHouseId();
        if (!redisFacade.tryLock(lockKey, Duration.ofSeconds(8))) {
            throw new BusinessException("下单繁忙，请稍后重试");
        }
        try {
            House house = houseMapper.selectById(dto.getHouseId());
            if (house == null || house.getStatus() != 1 || house.getAuditStatus() != 1) {
                throw new BusinessException("房源不可预订");
            }
            if (dto.getGuestCount() > house.getGuestNumber()) {
                throw new BusinessException("入住人数超出房源上限");
            }
            long overlap = orderMapper.selectCount(Wrappers.<BookingOrder>lambdaQuery()
                    .eq(BookingOrder::getHouseId, dto.getHouseId())
                    .notIn(BookingOrder::getOrderStatus, List.of("CANCEL"))
                    .lt(BookingOrder::getCheckInDate, dto.getCheckOutDate())
                    .gt(BookingOrder::getCheckOutDate, dto.getCheckInDate()));
            if (overlap > 0) {
                throw new BusinessException("所选日期已被预订");
            }
            long nights = Duration.between(dto.getCheckInDate().atStartOfDay(), dto.getCheckOutDate().atStartOfDay()).toDays();
            BigDecimal housePrice = house.getPrice().multiply(BigDecimal.valueOf(nights))
                    .multiply(BigDecimal.valueOf(dto.getRoomCount() == null ? 1 : dto.getRoomCount()));
            BigDecimal serviceFee = housePrice.multiply(new BigDecimal("0.06")).setScale(2, RoundingMode.HALF_UP);
            BookingOrder order = new BookingOrder();
            order.setOrderNo(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"))
                    + ThreadLocalRandom.current().nextInt(1000, 9999));
            order.setUserId(userId);
            order.setHouseId(house.getId());
            order.setCheckInDate(dto.getCheckInDate());
            order.setCheckOutDate(dto.getCheckOutDate());
            order.setGuestCount(dto.getGuestCount());
            order.setRoomCount(dto.getRoomCount() == null ? 1 : dto.getRoomCount());
            order.setContactName(dto.getContactName());
            order.setContactPhone(dto.getContactPhone());
            order.setHousePrice(housePrice);
            order.setServiceFee(serviceFee);
            order.setTotalAmount(housePrice.add(serviceFee));
            order.setOrderStatus("WAIT_PAY");
            order.setPaymentStatus(0);
            orderMapper.insert(order);
            pushMessage(userId, "ORDER", "订单已创建", "请在 30 分钟内完成支付，订单号 " + order.getOrderNo());
            log.info("创建订单 orderNo={} userId={}", order.getOrderNo(), userId);
            return toVo(order);
        } finally {
            redisFacade.unlock(lockKey);
        }
    }

    public PageData<OrderVO> myOrders(String status, long page, long size) {
        long userId = StpUtil.getLoginIdAsLong();
        Page<BookingOrder> data = orderMapper.selectPage(Page.of(page, size), Wrappers.<BookingOrder>lambdaQuery()
                .eq(BookingOrder::getUserId, userId)
                .eq(StringUtils.hasText(status), BookingOrder::getOrderStatus, status)
                .orderByDesc(BookingOrder::getId));
        return new PageData<>(data.getTotal(), data.getRecords().stream().map(this::toVo).toList(), page, size);
    }

    public OrderVO detail(Long id) {
        return toVo(requireOwner(id));
    }

    @Transactional
    public void cancel(Long id) {
        BookingOrder order = requireOwner(id);
        if (!"WAIT_PAY".equals(order.getOrderStatus())) {
            throw new BusinessException("当前状态不可取消");
        }
        order.setOrderStatus("CANCEL");
        orderMapper.updateById(order);
        pushMessage(order.getUserId(), "ORDER", "订单已取消", "订单号 " + order.getOrderNo());
    }

    public PageData<OrderVO> adminPage(long page, long size, String status, String keyword) {
        Page<BookingOrder> data = orderMapper.selectPage(Page.of(page, size), Wrappers.<BookingOrder>lambdaQuery()
                .eq(StringUtils.hasText(status), BookingOrder::getOrderStatus, status)
                .like(StringUtils.hasText(keyword), BookingOrder::getOrderNo, keyword)
                .orderByDesc(BookingOrder::getId));
        return new PageData<>(data.getTotal(), data.getRecords().stream().map(this::toVo).toList(), page, size);
    }

    public OrderVO adminDetail(Long id) {
        BookingOrder order = orderMapper.selectById(id);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }
        return toVo(order);
    }

    public void updateStatus(Long id, String status) {
        BookingOrder order = orderMapper.selectById(id);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }
        order.setOrderStatus(status);
        orderMapper.updateById(order);
    }

    public void refund(Long id) {
        BookingOrder order = orderMapper.selectById(id);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }
        order.setOrderStatus("CANCEL");
        order.setPaymentStatus(0);
        orderMapper.updateById(order);
        pushMessage(order.getUserId(), "ORDER", "退款已处理", "订单号 " + order.getOrderNo());
    }

    public void cleanExpired() {
        List<BookingOrder> expired = orderMapper.selectList(Wrappers.<BookingOrder>lambdaQuery()
                .eq(BookingOrder::getOrderStatus, "WAIT_PAY")
                .lt(BookingOrder::getCreateTime, LocalDateTime.now().minusMinutes(30)));
        for (BookingOrder order : expired) {
            order.setOrderStatus("CANCEL");
            orderMapper.updateById(order);
        }
        List<BookingOrder> checkIn = orderMapper.selectList(Wrappers.<BookingOrder>lambdaQuery()
                .eq(BookingOrder::getOrderStatus, "PAID")
                .le(BookingOrder::getCheckInDate, LocalDate.now()));
        for (BookingOrder order : checkIn) {
            order.setOrderStatus("CHECK_IN");
            orderMapper.updateById(order);
        }
        List<BookingOrder> finished = orderMapper.selectList(Wrappers.<BookingOrder>lambdaQuery()
                .eq(BookingOrder::getOrderStatus, "CHECK_IN")
                .le(BookingOrder::getCheckOutDate, LocalDate.now()));
        for (BookingOrder order : finished) {
            order.setOrderStatus("FINISHED");
            orderMapper.updateById(order);
        }
    }

    public BookingOrder requireOwner(Long id) {
        BookingOrder order = orderMapper.selectById(id);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }
        if (StpUtil.isLogin() && !order.getUserId().equals(StpUtil.getLoginIdAsLong())
                && !StpUtil.hasRole("ADMIN")) {
            throw new BusinessException(403, "无权查看该订单");
        }
        return order;
    }

    public OrderVO toVo(BookingOrder order) {
        OrderVO vo = new OrderVO();
        vo.setId(order.getId());
        vo.setOrderNo(order.getOrderNo());
        vo.setHouseId(order.getHouseId());
        vo.setCheckInDate(order.getCheckInDate());
        vo.setCheckOutDate(order.getCheckOutDate());
        vo.setGuestCount(order.getGuestCount());
        vo.setRoomCount(order.getRoomCount());
        vo.setContactName(order.getContactName());
        vo.setContactPhone(order.getContactPhone());
        vo.setHousePrice(order.getHousePrice());
        vo.setServiceFee(order.getServiceFee());
        vo.setTotalAmount(order.getTotalAmount());
        vo.setOrderStatus(order.getOrderStatus());
        vo.setPaymentStatus(order.getPaymentStatus());
        vo.setCreateTime(order.getCreateTime());
        House house = houseMapper.selectById(order.getHouseId());
        if (house != null) {
            vo.setHouseTitle(house.getTitle());
            vo.setHouseCover(house.getCoverImage());
        }
        return vo;
    }

    private void pushMessage(Long userId, String type, String title, String content) {
        Message message = new Message();
        message.setUserId(userId);
        message.setType(type);
        message.setTitle(title);
        message.setContent(content);
        message.setReadStatus(0);
        messageMapper.insert(message);
    }
}
