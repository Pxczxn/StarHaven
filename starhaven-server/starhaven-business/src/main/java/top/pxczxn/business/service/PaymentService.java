package top.pxczxn.business.service;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import top.pxczxn.business.dto.CreatePayDTO;
import top.pxczxn.business.entity.BookingOrder;
import top.pxczxn.business.entity.Message;
import top.pxczxn.business.entity.Payment;
import top.pxczxn.business.mapper.BookingOrderMapper;
import top.pxczxn.business.mapper.MessageMapper;
import top.pxczxn.business.mapper.PaymentMapper;
import top.pxczxn.common.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentMapper paymentMapper;
    private final BookingOrderMapper orderMapper;
    private final OrderService orderService;
    private final MessageMapper messageMapper;

    @Transactional
    public Map<String, Object> create(CreatePayDTO dto) {
        BookingOrder order = orderService.requireOwner(dto.getOrderId());
        if (!"WAIT_PAY".equals(order.getOrderStatus())) {
            throw new BusinessException("订单无需支付");
        }
        String payType = dto.getPayType() == null ? "WECHAT" : dto.getPayType();
        Payment payment = new Payment();
        payment.setOrderId(order.getId());
        payment.setPaymentNo("P" + UUID.randomUUID().toString().replace("-", "").substring(0, 18).toUpperCase());
        payment.setPayType(payType);
        payment.setAmount(order.getTotalAmount());
        payment.setStatus(0);
        paymentMapper.insert(payment);
        log.info("创建支付单 paymentNo={} orderId={}", payment.getPaymentNo(), order.getId());
        return Map.of(
                "paymentNo", payment.getPaymentNo(),
                "payType", payType,
                "amount", order.getTotalAmount(),
                "mock", true
        );
    }

    @Transactional
    public void callback(String paymentNo) {
        Payment payment = paymentMapper.selectOne(Wrappers.<Payment>lambdaQuery().eq(Payment::getPaymentNo, paymentNo));
        if (payment == null) {
            throw new BusinessException("支付单不存在");
        }
        markPaid(payment);
    }

    @Transactional
    public void mockSuccess(Long orderId) {
        BookingOrder order = orderService.requireOwner(orderId);
        Payment payment = paymentMapper.selectOne(Wrappers.<Payment>lambdaQuery()
                .eq(Payment::getOrderId, order.getId())
                .orderByDesc(Payment::getId)
                .last("LIMIT 1"));
        if (payment == null) {
            CreatePayDTO dto = new CreatePayDTO();
            dto.setOrderId(orderId);
            dto.setPayType("WECHAT");
            create(dto);
            payment = paymentMapper.selectOne(Wrappers.<Payment>lambdaQuery()
                    .eq(Payment::getOrderId, order.getId())
                    .orderByDesc(Payment::getId)
                    .last("LIMIT 1"));
        }
        markPaid(payment);
    }

    private void markPaid(Payment payment) {
        payment.setStatus(1);
        payment.setPayTime(LocalDateTime.now());
        paymentMapper.updateById(payment);
        BookingOrder order = orderMapper.selectById(payment.getOrderId());
        order.setPaymentStatus(1);
        order.setOrderStatus("PAID");
        orderMapper.updateById(order);
        Message message = new Message();
        message.setUserId(order.getUserId());
        message.setType("ORDER");
        message.setTitle("支付成功");
        message.setContent("订单 " + order.getOrderNo() + " 已支付，期待您的入住");
        message.setReadStatus(0);
        messageMapper.insert(message);
        log.info("支付成功 paymentNo={}", payment.getPaymentNo());
    }
}
