package top.pxczxn.business.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("house_facility")
public class HouseFacility {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long houseId;
    private String facilityName;
}
