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
     * 主键 ID
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
     * 房型 ID
     */
    private Long roomTypeId;

    /**
     * 楼层
     */
    private String floor;

    /**
     * 面积（平方米）
     */
    private BigDecimal area;

    /**
     * 最大入住人数
     */
    private Integer capacity;

    /**
     * 房间价格（可能与房型默认价格不同）
     */
    private BigDecimal price;

    /**
     * 房间状态（available: 空闲, occupied: 已入住, cleaning: 清洁中, maintenance: 维修中）
     */
    private String status;

    /**
     * 房间图片 URL
     */
    private String imageUrl;

    /**
     * 设施（JSON 字符串，如：["WiFi", "空调", "电视"]）
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
