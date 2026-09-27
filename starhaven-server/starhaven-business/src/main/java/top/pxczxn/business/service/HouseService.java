package top.pxczxn.business.service;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import top.pxczxn.business.dto.HouseQueryDTO;
import top.pxczxn.business.entity.Favorite;
import top.pxczxn.business.entity.House;
import top.pxczxn.business.entity.HouseFacility;
import top.pxczxn.business.entity.HouseImage;
import top.pxczxn.business.mapper.FavoriteMapper;
import top.pxczxn.business.mapper.HouseFacilityMapper;
import top.pxczxn.business.mapper.HouseImageMapper;
import top.pxczxn.business.mapper.HouseMapper;
import top.pxczxn.business.vo.HouseCardVO;
import top.pxczxn.business.vo.HouseDetailVO;
import top.pxczxn.common.constant.RedisKeys;
import top.pxczxn.common.exception.BusinessException;
import top.pxczxn.common.redis.RedisFacade;
import top.pxczxn.common.result.PageData;
import top.pxczxn.common.web.MediaUrlResolver;
import top.pxczxn.system.entity.User;
import top.pxczxn.system.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.Duration;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class HouseService {

    private final HouseMapper houseMapper;
    private final HouseImageMapper houseImageMapper;
    private final HouseFacilityMapper houseFacilityMapper;
    private final FavoriteMapper favoriteMapper;
    private final UserMapper userMapper;
    private final RedisFacade redisFacade;
    private final ObjectMapper objectMapper;
    private final MediaUrlResolver mediaUrlResolver;

    public PageData<HouseCardVO> page(HouseQueryDTO query) {
        LambdaQueryWrapper<House> wrapper = Wrappers.<House>lambdaQuery()
                .eq(House::getStatus, 1)
                .eq(House::getAuditStatus, 1)
                .like(StringUtils.hasText(query.getKeyword()), House::getTitle, query.getKeyword())
                .eq(StringUtils.hasText(query.getCity()), House::getCity, query.getCity())
                .eq(StringUtils.hasText(query.getHouseType()), House::getHouseType, query.getHouseType())
                .ge(query.getMinPrice() != null, House::getPrice, query.getMinPrice())
                .le(query.getMaxPrice() != null, House::getPrice, query.getMaxPrice())
                .ge(query.getGuestCount() != null, House::getGuestNumber, query.getGuestCount());
        if ("PRICE_ASC".equals(query.getSort())) {
            wrapper.orderByAsc(House::getPrice);
        } else if ("SCORE_DESC".equals(query.getSort())) {
            wrapper.orderByDesc(House::getAvgScore);
        } else {
            wrapper.orderByDesc(House::getHeat).orderByDesc(House::getAvgScore);
        }
        Page<House> page = houseMapper.selectPage(Page.of(query.getPage(), query.getSize()), wrapper);
        List<HouseCardVO> list = page.getRecords().stream()
                .filter(house -> matchFacility(house.getId(), query.getFacility()))
                .map(this::toCard)
                .toList();
        return new PageData<>(page.getTotal(), list, query.getPage(), query.getSize());
    }

    public List<HouseCardVO> recommend() {
        String cached = redisFacade.get(RedisKeys.HOUSE_HOT);
        if (StringUtils.hasText(cached)) {
            try {
                return objectMapper.readValue(cached, new TypeReference<>() {
                });
            } catch (Exception ignored) {
                // 缓存损坏时回源数据库
            }
        }
        List<HouseCardVO> list = houseMapper.selectList(Wrappers.<House>lambdaQuery()
                        .eq(House::getStatus, 1)
                        .eq(House::getAuditStatus, 1)
                        .orderByDesc(House::getHeat)
                        .last("LIMIT 8"))
                .stream()
                .map(this::toCard)
                .toList();
        try {
            redisFacade.set(RedisKeys.HOUSE_HOT, objectMapper.writeValueAsString(list), Duration.ofMinutes(10));
        } catch (Exception ex) {
            log.warn("热门房源缓存写入失败", ex);
        }
        return list;
    }

    public HouseDetailVO detail(Long id) {
        String cacheKey = RedisKeys.HOUSE_DETAIL + id;
        String cached = redisFacade.get(cacheKey);
        if (StringUtils.hasText(cached)) {
            try {
                HouseDetailVO vo = objectMapper.readValue(cached, HouseDetailVO.class);
                vo.setFavorited(isFavorited(id));
                return vo;
            } catch (Exception ignored) {
            }
        }
        House house = houseMapper.selectById(id);
        if (house == null) {
            throw new BusinessException("房源不存在");
        }
        HouseDetailVO vo = new HouseDetailVO();
        vo.setId(house.getId());
        vo.setHostId(house.getHostId());
        vo.setTitle(house.getTitle());
        vo.setCoverImage(mediaUrlResolver.resolve(house.getCoverImage()));
        vo.setDescription(house.getDescription());
        vo.setAddress(house.getAddress());
        vo.setCity(house.getCity());
        vo.setLatitude(house.getLatitude());
        vo.setLongitude(house.getLongitude());
        vo.setPrice(house.getPrice());
        vo.setGuestNumber(house.getGuestNumber());
        vo.setRoomNumber(house.getRoomNumber());
        vo.setBathroomNumber(house.getBathroomNumber());
        vo.setBedNumber(house.getBedNumber());
        vo.setHouseType(house.getHouseType());
        vo.setAvgScore(house.getAvgScore());
        vo.setCommentCount(house.getCommentCount());
        vo.setImages(houseImageMapper.selectList(Wrappers.<HouseImage>lambdaQuery()
                        .eq(HouseImage::getHouseId, id)
                        .orderByAsc(HouseImage::getSort))
                .stream()
                .map(image -> mediaUrlResolver.resolve(image.getImageUrl()))
                .toList());
        vo.setFacilities(facilities(id));
        User host = userMapper.selectById(house.getHostId());
        if (host != null) {
            vo.setHostNickname(host.getNickname());
            vo.setHostAvatar(mediaUrlResolver.resolve(host.getAvatar()));
            vo.setHostCertified("HOST".equals(host.getRole()) || "ADMIN".equals(host.getRole()));
        }
        try {
            redisFacade.set(cacheKey, objectMapper.writeValueAsString(vo), Duration.ofMinutes(30));
        } catch (Exception ex) {
            log.warn("房源详情缓存写入失败", ex);
        }
        vo.setFavorited(isFavorited(id));
        return vo;
    }

    public java.util.List<Long> idsOfHost(Long hostId) {
        return houseMapper.selectList(Wrappers.<House>lambdaQuery().eq(House::getHostId, hostId))
                .stream()
                .map(House::getId)
                .toList();
    }

    public PageData<HouseCardVO> adminPage(long page, long size, String keyword, Integer auditStatus, Long hostId) {
        Page<House> data = houseMapper.selectPage(Page.of(page, size), Wrappers.<House>lambdaQuery()
                .eq(hostId != null, House::getHostId, hostId)
                .like(StringUtils.hasText(keyword), House::getTitle, keyword)
                .eq(auditStatus != null, House::getAuditStatus, auditStatus)
                .orderByDesc(House::getId));
        return new PageData<>(data.getTotal(), data.getRecords().stream().map(this::toCard).toList(), page, size);
    }

    public void audit(Long id, Integer auditStatus) {
        House house = requireManageable(id);
        house.setAuditStatus(auditStatus);
        if (auditStatus != null && auditStatus == 1) {
            house.setStatus(1);
        }
        houseMapper.updateById(house);
        redisFacade.delete(RedisKeys.HOUSE_DETAIL + id);
    }

    public void updateStatus(Long id, Integer status) {
        House house = requireManageable(id);
        house.setStatus(status);
        houseMapper.updateById(house);
        redisFacade.delete(RedisKeys.HOUSE_DETAIL + id);
    }

    public void delete(Long id) {
        requireManageable(id);
        houseMapper.deleteById(id);
        redisFacade.delete(RedisKeys.HOUSE_DETAIL + id);
    }

    public void refreshHeat() {
        List<House> houses = houseMapper.selectList(null);
        for (House house : houses) {
            int heat = (house.getCommentCount() == null ? 0 : house.getCommentCount()) * 3
                    + (house.getAvgScore() == null ? 0 : house.getAvgScore().intValue()) * 10;
            house.setHeat(heat);
            houseMapper.updateById(house);
        }
        redisFacade.delete(RedisKeys.HOUSE_HOT);
    }

    public HouseCardVO toCard(House house) {
        HouseCardVO vo = new HouseCardVO();
        vo.setId(house.getId());
        vo.setTitle(house.getTitle());
        vo.setCoverImage(mediaUrlResolver.resolve(house.getCoverImage()));
        vo.setCity(house.getCity());
        vo.setAddress(house.getAddress());
        vo.setPrice(house.getPrice());
        vo.setHouseType(house.getHouseType());
        vo.setAvgScore(house.getAvgScore());
        vo.setCommentCount(house.getCommentCount());
        vo.setFacilities(facilities(house.getId()));
        vo.setFavorited(isFavorited(house.getId()));
        vo.setStatus(house.getStatus());
        vo.setAuditStatus(house.getAuditStatus());
        return vo;
    }

    private List<String> facilities(Long houseId) {
        return houseFacilityMapper.selectList(Wrappers.<HouseFacility>lambdaQuery()
                        .eq(HouseFacility::getHouseId, houseId))
                .stream()
                .map(HouseFacility::getFacilityName)
                .toList();
    }

    private boolean matchFacility(Long houseId, String facility) {
        if (!StringUtils.hasText(facility)) {
            return true;
        }
        Set<String> names = facilities(houseId).stream().collect(Collectors.toSet());
        for (String item : facility.split(",")) {
            if (!names.contains(item.trim())) {
                return false;
            }
        }
        return true;
    }

    private boolean isFavorited(Long houseId) {
        if (!StpUtil.isLogin()) {
            return false;
        }
        return favoriteMapper.selectCount(Wrappers.<Favorite>lambdaQuery()
                .eq(Favorite::getUserId, StpUtil.getLoginIdAsLong())
                .eq(Favorite::getHouseId, houseId)) > 0;
    }

    private House requireManageable(Long id) {
        House house = requireHouse(id);
        if (!StpUtil.hasRole("ADMIN") && !house.getHostId().equals(StpUtil.getLoginIdAsLong())) {
            throw new BusinessException(403, "无权操作该房源");
        }
        return house;
    }

    private House requireHouse(Long id) {
        House house = houseMapper.selectById(id);
        if (house == null) {
            throw new BusinessException("房源不存在");
        }
        return house;
    }
}
