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

    public DashboardVO overview(Long hostId) {
        DashboardVO vo = new DashboardVO();
        java.util.List<Long> houseIds = hostId == null ? null : houseMapper.selectList(Wrappers.<House>lambdaQuery()
                        .eq(House::getHostId, hostId))
                .stream()
                .map(House::getId)
                .toList();
        if (hostId != null) {
            vo.setUserCount(0);
            vo.setHouseCount(houseIds.size());
            if (houseIds.isEmpty()) {
                vo.setOrderCount(0);
                vo.setTradeAmount(BigDecimal.ZERO);
                vo.setTodayOrderCount(0);
                vo.setHotHouses(java.util.List.of());
                return vo;
            }
        } else {
            vo.setUserCount(userMapper.selectCount(null));
            vo.setHouseCount(houseMapper.selectCount(null));
        }
        var orderQuery = Wrappers.<BookingOrder>lambdaQuery();
        if (houseIds != null) {
            orderQuery.in(BookingOrder::getHouseId, houseIds);
        }
        vo.setOrderCount(orderMapper.selectCount(orderQuery));
        var paidQuery = Wrappers.<BookingOrder>lambdaQuery()
                .in(BookingOrder::getOrderStatus, java.util.List.of("PAID", "CHECK_IN", "FINISHED"));
        if (houseIds != null) {
            paidQuery.in(BookingOrder::getHouseId, houseIds);
        }
        vo.setTradeAmount(orderMapper.selectList(paidQuery)
                .stream()
                .map(BookingOrder::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add));
        var todayQuery = Wrappers.<BookingOrder>lambdaQuery()
                .ge(BookingOrder::getCreateTime, LocalDate.now().atStartOfDay());
        if (houseIds != null) {
            todayQuery.in(BookingOrder::getHouseId, houseIds);
        }
        vo.setTodayOrderCount(orderMapper.selectCount(todayQuery));
        var hotQuery = Wrappers.<House>lambdaQuery()
                .eq(House::getStatus, 1)
                .eq(hostId != null, House::getHostId, hostId)
                .orderByDesc(House::getHeat)
                .last("LIMIT 5");
        vo.setHotHouses(houseMapper.selectList(hotQuery)
                .stream()
                .map(houseService::toCard)
                .toList());
        return vo;
    }
}
