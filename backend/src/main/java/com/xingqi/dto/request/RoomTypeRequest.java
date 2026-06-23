package com.xingqi.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 创建/更新房型请求
 */
@Data
public class RoomTypeRequest {

    /**
     * 房型名称
     */
    @NotBlank(message = "房型名称不能为空")
    private String name;

    /**
     * 默认价格
     */
    @NotNull(message = "默认价格不能为空")
    @Positive(message = "默认价格必须大于0")
    private BigDecimal defaultPrice;

    /**
     * 可住人数
     */
    @NotNull(message = "可住人数不能为空")
    @Positive(message = "可住人数必须大于0")
    private Integer capacity;

    /**
     * 床型
     */
    private String bedType;

    /**
     * 早餐说明
     */
    private String breakfast;

    /**
     * 房型描述
     */
    private String description;

    /**
     * 房型图片
     */
    private String imageUrl;

    /**
     * 状态：enabled-启用, disabled-禁用
     */
    private String status;
}
