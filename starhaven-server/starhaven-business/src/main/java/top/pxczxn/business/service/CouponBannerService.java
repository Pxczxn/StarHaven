package top.pxczxn.business.service;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import top.pxczxn.business.entity.Banner;
import top.pxczxn.business.entity.Coupon;
import top.pxczxn.business.entity.CouponUser;
import top.pxczxn.business.mapper.BannerMapper;
import top.pxczxn.business.mapper.CouponMapper;
import top.pxczxn.business.mapper.CouponUserMapper;
import top.pxczxn.business.vo.BannerVO;
import top.pxczxn.business.vo.CouponVO;
import top.pxczxn.common.web.MediaUrlResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CouponBannerService {

    private final CouponMapper couponMapper;
    private final CouponUserMapper couponUserMapper;
    private final BannerMapper bannerMapper;
    private final MediaUrlResolver mediaUrlResolver;

    public List<BannerVO> banners() {
        return bannerMapper.selectList(Wrappers.<Banner>lambdaQuery()
                        .eq(Banner::getStatus, 1)
                        .orderByAsc(Banner::getSort))
                .stream()
                .map(item -> {
                    BannerVO vo = new BannerVO();
                    BeanUtils.copyProperties(item, vo);
                    vo.setImageUrl(mediaUrlResolver.resolve(item.getImageUrl()));
                    return vo;
                })
                .toList();
    }

    public List<CouponVO> myCoupons() {
        long userId = StpUtil.getLoginIdAsLong();
        return couponUserMapper.selectList(Wrappers.<CouponUser>lambdaQuery().eq(CouponUser::getUserId, userId))
                .stream()
                .map(item -> {
                    Coupon coupon = couponMapper.selectById(item.getCouponId());
                    CouponVO vo = new CouponVO();
                    if (coupon != null) {
                        BeanUtils.copyProperties(coupon, vo);
                    }
                    vo.setCouponUserId(item.getId());
                    vo.setUsed(item.getUsed());
                    return vo;
                })
                .toList();
    }

    public long couponCount(Long userId) {
        return couponUserMapper.selectCount(Wrappers.<CouponUser>lambdaQuery()
                .eq(CouponUser::getUserId, userId)
                .eq(CouponUser::getUsed, 0));
    }
}
