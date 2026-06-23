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
     * 主键 ID
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
     * 最大入住人数
     */
    private Integer capacity;

    /**
     * 床型（如：1.8m 大床、1.2m 双床）
     */
    private String bedType;

    /**
     * 早餐（如：双早、单早、无）
     */
    private String breakfast;

    /**
     * 房型描述
     */
    private String description;

    /**
     * 房型图片 URL
     */
    private String imageUrl;

    /**
     * 状态（enabled: 启用, disabled: 停用）
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
