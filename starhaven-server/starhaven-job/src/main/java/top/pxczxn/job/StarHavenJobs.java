package top.pxczxn.job;

import top.pxczxn.business.mapper.BookingOrderMapper;
import top.pxczxn.business.service.HouseService;
import top.pxczxn.business.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class StarHavenJobs {

    private final OrderService orderService;
    private final HouseService houseService;
    private final BookingOrderMapper orderMapper;

    @Scheduled(cron = "0 0/10 * * * ?")
    public void cleanExpiredOrders() {
        orderService.cleanExpired();
        log.info("清理过期订单完成");
    }

    @Scheduled(cron = "0 20 1 * * ?")
    public void refreshHouseHeat() {
        houseService.refreshHeat();
        log.info("更新房源热度完成");
    }

    @Scheduled(cron = "0 40 1 * * ?")
    public void statTrade() {
        Long count = orderMapper.selectCount(null);
        log.info("交易数据统计 orderCount={}", count);
    }
}
