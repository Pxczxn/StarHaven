package com.xingqi.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 房间实体
 */
@Data
@TableName("room")
public class Room {

    /**
     * 房间ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 房间号
     */
    private String roomNo;

    /**
     * 房间名称
     */
    private String name;

    /**
     * 房型ID
     */
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
    private Integer capacity;

    /**
     * 价格
     */
    private BigDecimal price;

    /**
     * 房间状态：available-空闲, reserved-已预订, occupied-已入住, cleaning-清洁中, maintenance-维修中, disabled-停用
     */
    private String status;

    /**
     * 房间图片
     */
    private String imageUrl;

    /**
     * 配套设施（逗号分隔）
     */
    private String facilities;

    /**
     * 备注
     */
    private String remark;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;
}
