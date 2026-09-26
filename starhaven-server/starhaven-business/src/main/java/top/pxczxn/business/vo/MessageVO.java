package top.pxczxn.business.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class MessageVO {
    private Long id;
    private String type;
    private String title;
    private String content;
    private Integer readStatus;
    private LocalDateTime createTime;
}
