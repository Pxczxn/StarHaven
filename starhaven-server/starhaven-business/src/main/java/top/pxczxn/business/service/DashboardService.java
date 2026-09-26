package top.pxczxn.business.service;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import top.pxczxn.business.entity.BookingOrder;
import top.pxczxn.business.entity.House;
import top.pxczxn.business.mapper.BookingOrderMapper;
import top.pxczxn.business.mapper.HouseMapper;
import top.pxczxn.business.vo.DashboardVO;
import top.pxczxn.system.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final UserMapper userMapper;
    private final HouseMapper houseMapper;
    private final BookingOrderMapper orderMapper;
    private final HouseService houseService;

    public DashboardVO overview() {
        DashboardVO vo = new DashboardVO();
        vo.setUserCount(userMapper.selectCount(null));
        vo.setHouseCount(houseMapper.selectCount(null));
        vo.setOrderCount(orderMapper.selectCount(null));
        vo.setTradeAmount(orderMapper.selectList(Wrappers.<BookingOrder>lambdaQuery()
                        .in(BookingOrder::getOrderStatus, java.util.List.of("PAID", "CHECK_IN", "FINISHED")))
                .stream()
                .map(BookingOrder::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add));
        vo.setTodayOrderCount(orderMapper.selectCount(Wrappers.<BookingOrder>lambdaQuery()
                .ge(BookingOrder::getCreateTime, LocalDate.now().atStartOfDay())));
        vo.setHotHouses(houseMapper.selectList(Wrappers.<House>lambdaQuery()
                        .eq(House::getStatus, 1)
                        .orderByDesc(House::getHeat)
                        .last("LIMIT 5"))
                .stream()
                .map(houseService::toCard)
                .toList());
        return vo;
    }
}
