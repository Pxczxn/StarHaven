package top.pxczxn.business.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class HouseQueryDTO {
    @Schema(description = "页码", example = "1")
    private long page = 1;
    @Schema(description = "每页条数", example = "10")
    private long size = 10;
    private String keyword;
    private String city;
    @Schema(description = "最低价")
    private java.math.BigDecimal minPrice;
    @Schema(description = "最高价")
    private java.math.BigDecimal maxPrice;
    @Schema(description = "设施，逗号分隔")
    private String facility;
    private String houseType;
    private Integer guestCount;
    @Schema(description = "RECOMMEND / PRICE_ASC / SCORE_DESC")
    private String sort;
}
