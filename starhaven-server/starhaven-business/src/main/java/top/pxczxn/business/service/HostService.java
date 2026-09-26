package top.pxczxn.business.service;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import top.pxczxn.business.entity.BookingOrder;
import top.pxczxn.business.entity.HostApply;
import top.pxczxn.business.entity.House;
import top.pxczxn.business.mapper.BookingOrderMapper;
import top.pxczxn.business.mapper.HostApplyMapper;
import top.pxczxn.business.mapper.HouseMapper;
import top.pxczxn.business.vo.HouseCardVO;
import top.pxczxn.business.vo.OrderVO;
import top.pxczxn.common.exception.BusinessException;
import top.pxczxn.system.entity.User;
import top.pxczxn.system.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class HostService {

    private final HostApplyMapper hostApplyMapper;
    private final HouseMapper houseMapper;
    private final BookingOrderMapper orderMapper;
    private final UserMapper userMapper;
    private final HouseService houseService;
    private final OrderService orderService;

    public void apply(String realName, String idCard) {
        long userId = StpUtil.getLoginIdAsLong();
        HostApply apply = new HostApply();
        apply.setUserId(userId);
        apply.setRealName(realName);
        apply.setIdCard(idCard);
        apply.setStatus(0);
        hostApplyMapper.insert(apply);
    }

    public void auditHost(Long applyId, Integer status, String remark) {
        HostApply apply = hostApplyMapper.selectById(applyId);
        if (apply == null) {
            throw new BusinessException("申请不存在");
        }
        apply.setStatus(status);
        apply.setRemark(remark);
        hostApplyMapper.updateById(apply);
        if (status != null && status == 1) {
            User user = userMapper.selectById(apply.getUserId());
            user.setRole("HOST");
            userMapper.updateById(user);
        }
    }

    public List<HouseCardVO> myHouses() {
        long userId = StpUtil.getLoginIdAsLong();
        return houseMapper.selectList(Wrappers.<House>lambdaQuery().eq(House::getHostId, userId))
                .stream()
                .map(houseService::toCard)
                .toList();
    }

    public List<OrderVO> myOrders() {
        long userId = StpUtil.getLoginIdAsLong();
        List<Long> houseIds = houseMapper.selectList(Wrappers.<House>lambdaQuery().eq(House::getHostId, userId))
                .stream()
                .map(House::getId)
                .toList();
        if (houseIds.isEmpty()) {
            return List.of();
        }
        return orderMapper.selectList(Wrappers.<BookingOrder>lambdaQuery().in(BookingOrder::getHouseId, houseIds))
                .stream()
                .map(orderService::toVo)
                .toList();
    }

    public Map<String, Object> income() {
        BigDecimal amount = myOrders().stream()
                .filter(item -> "PAID".equals(item.getOrderStatus())
                        || "CHECK_IN".equals(item.getOrderStatus())
                        || "FINISHED".equals(item.getOrderStatus()))
                .map(OrderVO::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return Map.of("totalAmount", amount, "orderCount", myOrders().size());
    }
}
