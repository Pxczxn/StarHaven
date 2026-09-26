package top.pxczxn.business.vo;

import lombok.Data;

@Data
public class BannerVO {
    private Long id;
    private String title;
    private String subtitle;
    private String imageUrl;
    private String linkUrl;
}
