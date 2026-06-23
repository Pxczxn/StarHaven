package com.xingqi.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 创建/更新客户请求
 */
@Data
public class CustomerRequest {

    /**
     * 客户姓名
     */
    @NotBlank(message = "客户姓名不能为空")
    private String name;

    /**
     * 性别
     */
    private String gender;

    /**
     * 手机号
     */
    @NotBlank(message = "手机号不能为空")
    private String phone;

    /**
     * 证件类型
     */
    private String idType;

    /**
     * 证件号码
     */
    private String idNumber;

    /**
     * 生日
     */
    private String birthday;

    /**
     * 客户来源
     */
    private String source;

    /**
     * 会员等级
     */
    private String level;

    /**
     * 备注
     */
    private String remark;
}
