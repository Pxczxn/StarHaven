package com.xingqi.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("operation_task")
public class OperationTask {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long roomId;

    private String roomNo;

    private String taskType;

    private String title;

    private String description;

    private String priority;

    private String status;

    private String assignedTo;

    private LocalDateTime dueDate;

    private String remark;

    private LocalDateTime completedAt;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
