package com.xingqi.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 房型实体
 */
@Data
@TableName("room_type")
public class RoomType {

    /**
     * 房型ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 房型名称
     */
    private String name;

    /**
     * 默认价格
     */
    private BigDecimal defaultPrice;

    /**
     * 可住人数
     */
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

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;
}
