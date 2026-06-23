package com.xingqi.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 修改房间状态请求
 */
@Data
public class RoomStatusRequest {

    /**
     * 房间状态：available-空闲, reserved-已预订, occupied-已入住, cleaning-清洁中, maintenance-维修中, disabled-停用
     */
    @NotBlank(message = "房间状态不能为空")
    private String status;
}
