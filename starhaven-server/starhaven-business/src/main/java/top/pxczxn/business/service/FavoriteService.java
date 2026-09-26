package top.pxczxn.business.service;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import top.pxczxn.business.entity.Favorite;
import top.pxczxn.business.entity.House;
import top.pxczxn.business.mapper.FavoriteMapper;
import top.pxczxn.business.mapper.HouseMapper;
import top.pxczxn.business.vo.HouseCardVO;
import top.pxczxn.common.result.PageData;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class FavoriteService {

    private final FavoriteMapper favoriteMapper;
    private final HouseMapper houseMapper;
    private final HouseService houseService;

    public Map<String, Object> toggle(Long houseId) {
        long userId = StpUtil.getLoginIdAsLong();
        Favorite exists = favoriteMapper.selectOne(Wrappers.<Favorite>lambdaQuery()
                .eq(Favorite::getUserId, userId)
                .eq(Favorite::getHouseId, houseId));
        if (exists == null) {
            Favorite favorite = new Favorite();
            favorite.setUserId(userId);
            favorite.setHouseId(houseId);
            favoriteMapper.insert(favorite);
            return Map.of("favorited", true);
        }
        favoriteMapper.deleteById(exists.getId());
        return Map.of("favorited", false);
    }

    public PageData<HouseCardVO> list(long page, long size) {
        long userId = StpUtil.getLoginIdAsLong();
        Page<Favorite> data = favoriteMapper.selectPage(Page.of(page, size), Wrappers.<Favorite>lambdaQuery()
                .eq(Favorite::getUserId, userId)
                .orderByDesc(Favorite::getId));
        var list = data.getRecords().stream().map(item -> {
            House house = houseMapper.selectById(item.getHouseId());
            return house == null ? null : houseService.toCard(house);
        }).filter(item -> item != null).toList();
        return new PageData<>(data.getTotal(), list, page, size);
    }

    public long countByUser(Long userId) {
        return favoriteMapper.selectCount(Wrappers.<Favorite>lambdaQuery().eq(Favorite::getUserId, userId));
    }
}
