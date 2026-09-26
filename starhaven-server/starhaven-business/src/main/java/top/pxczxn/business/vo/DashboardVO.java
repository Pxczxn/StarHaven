package top.pxczxn.business.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class DashboardVO {
    private long userCount;
    private long houseCount;
    private long orderCount;
    private BigDecimal tradeAmount;
    private long todayOrderCount;
    private java.util.List<HouseCardVO> hotHouses;
}
