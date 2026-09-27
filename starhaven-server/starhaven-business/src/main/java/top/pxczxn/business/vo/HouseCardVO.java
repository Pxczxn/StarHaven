package top.pxczxn.business.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class HouseCardVO {
    private Long id;
    private String title;
    private String coverImage;
    private String city;
    private String address;
    private BigDecimal price;
    private BigDecimal avgScore;
    private Integer commentCount;
    private String houseType;
    private List<String> facilities;
    @Schema(description = "当前用户是否已收藏")
    private Boolean favorited;
    private Integer status;
    private Integer auditStatus;
}
