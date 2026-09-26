package top.pxczxn.business.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class HouseDetailVO {
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
    private BigDecimal avgScore;
    private Integer commentCount;
    private List<String> images;
    private List<String> facilities;
    private String hostNickname;
    private String hostAvatar;
    private Boolean hostCertified;
    private Boolean favorited;
}
