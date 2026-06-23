package com.xingqi.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 创建/更新房间请求
 */
@Data
public class RoomRequest {

    /**
     * 房间号
     */
    @NotBlank(message = "房间号不能为空")
    private String roomNo;

    /**
     * 房间名称
     */
    @NotBlank(message = "房间名称不能为空")
    private String name;

    /**
     * 房型ID
     */
    @NotNull(message = "房型ID不能为空")
    private Long roomTypeId;

    /**
     * 楼层
     */
    private String floor;

    /**
     * 面积
     */
    private BigDecimal area;

    /**
     * 可住人数
     */
    @NotNull(message = "可住人数不能为空")
    @Positive(message = "可住人数必须大于0")
    private Integer capacity;

    /**
     * 价格
     */
    @NotNull(message = "价格不能为空")
    @Positive(message = "价格必须大于0")
    private BigDecimal price;

    /**
     * 房间状态
     */
    private String status;

    /**
     * 房间图片
     */
    private String imageUrl;

    /**
     * 配套设施
     */
    private String facilities;

    /**
     * 备注
     */
    private String remark;
}
