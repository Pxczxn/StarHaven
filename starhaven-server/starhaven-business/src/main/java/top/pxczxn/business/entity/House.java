package top.pxczxn.business.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("house")
public class House {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long hostId;
    private String title;
    private String coverImage;
    private String description;
    private String address;
    private String city;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private BigDecimal price;
    private Integer guestNumber;
    private Integer roomNumber;
    private Integer bathroomNumber;
    private Integer bedNumber;
    private String houseType;
    private Integer status;
    private Integer auditStatus;
    private BigDecimal avgScore;
    private Integer commentCount;
    private Integer heat;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
