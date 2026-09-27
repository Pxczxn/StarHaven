package top.pxczxn.business.service;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import top.pxczxn.business.entity.BrowseHistory;
import top.pxczxn.business.entity.House;
import top.pxczxn.business.mapper.BrowseHistoryMapper;
import top.pxczxn.business.mapper.HouseMapper;
import top.pxczxn.business.vo.HouseCardVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BrowseHistoryService {

    /** 每人最多保留的浏览记录条数 */
    private static final int MAX_KEEP = 50;
    /** 列表最多返回的条数 */
    private static final int MAX_RETURN = 20;

    private final BrowseHistoryMapper browseHistoryMapper;
    private final HouseMapper houseMapper;
    private final HouseService houseService;

    /**
     * 记录一次房源浏览：同一房源重复浏览只刷新时间，不产生重复记录
     */
    public void record(Long houseId) {
        if (houseId == null || !StpUtil.isLogin()) {
            return;
        }
        Long userId = StpUtil.getLoginIdAsLong();
        BrowseHistory existing = browseHistoryMapper.selectOne(Wrappers.<BrowseHistory>lambdaQuery()
                .eq(BrowseHistory::getUserId, userId)
                .eq(BrowseHistory::getHouseId, houseId)
                .last("LIMIT 1"));
        if (existing != null) {
            existing.setCreateTime(LocalDateTime.now());
            browseHistoryMapper.updateById(existing);
            return;
        }
        BrowseHistory history = new BrowseHistory();
        history.setUserId(userId);
        history.setHouseId(houseId);
        history.setCreateTime(LocalDateTime.now());
        browseHistoryMapper.insert(history);
        trim(userId);
    }

    /**
     * 浏览记录列表：按最近浏览倒序，已下架或已删除的房源不展示
     */
    public List<HouseCardVO> list() {
        if (!StpUtil.isLogin()) {
            return List.of();
        }
        List<BrowseHistory> records = browseHistoryMapper.selectList(Wrappers.<BrowseHistory>lambdaQuery()
                .eq(BrowseHistory::getUserId, StpUtil.getLoginIdAsLong())
                .orderByDesc(BrowseHistory::getCreateTime)
                .last("LIMIT " + MAX_RETURN));
        List<HouseCardVO> result = new ArrayList<>();
        for (BrowseHistory record : records) {
            House house = houseMapper.selectById(record.getHouseId());
            if (house == null || house.getStatus() == null || house.getStatus() != 1) {
                continue;
            }
            result.add(houseService.toCard(house));
        }
        return result;
    }

    /**
     * 浏览记录条数（只统计仍然上架的房源，与列表口径一致）
     */
    public long count() {
        if (!StpUtil.isLogin()) {
            return 0;
        }
        List<BrowseHistory> records = browseHistoryMapper.selectList(Wrappers.<BrowseHistory>lambdaQuery()
                .eq(BrowseHistory::getUserId, StpUtil.getLoginIdAsLong())
                .orderByDesc(BrowseHistory::getCreateTime)
                .last("LIMIT " + MAX_RETURN));
        long count = 0;
        for (BrowseHistory record : records) {
            House house = houseMapper.selectById(record.getHouseId());
            if (house != null && house.getStatus() != null && house.getStatus() == 1) {
                count++;
            }
        }
        return count;
    }

    public void clear() {
        if (!StpUtil.isLogin()) {
            return;
        }
        browseHistoryMapper.delete(Wrappers.<BrowseHistory>lambdaQuery()
                .eq(BrowseHistory::getUserId, StpUtil.getLoginIdAsLong()));
    }

    /**
     * 超出上限时淘汰最旧的记录
     */
    private void trim(Long userId) {
        List<BrowseHistory> all = browseHistoryMapper.selectList(Wrappers.<BrowseHistory>lambdaQuery()
                .eq(BrowseHistory::getUserId, userId)
                .orderByDesc(BrowseHistory::getCreateTime));
        if (all.size() <= MAX_KEEP) {
            return;
        }
        for (BrowseHistory outdated : all.subList(MAX_KEEP, all.size())) {
            browseHistoryMapper.deleteById(outdated.getId());
        }
    }
}
