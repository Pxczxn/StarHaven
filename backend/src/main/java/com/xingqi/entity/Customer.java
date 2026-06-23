package com.xingqi.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 客户实体
 */
@Data
@TableName("customer")
public class Customer {

    /**
     * 主键 ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 客户姓名
     */
    private String name;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 证件类型（如：身份证、护照）
     */
    private String idType;

    /**
     * 证件号码
     */
    private String idNumber;

    /**
     * 性别（male: 男, female: 女）
     */
    private String gender;

    /**
     * 生日
     */
    private String birthday;

    /**
     * 客户来源
     */
    private String source;

    /**
     * 客户等级
     */
    private String level;

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
