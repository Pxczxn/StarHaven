package top.pxczxn.business.service;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import top.pxczxn.business.dto.HouseQueryDTO;
import top.pxczxn.business.entity.SearchHistory;
import top.pxczxn.business.mapper.SearchHistoryMapper;
import top.pxczxn.business.vo.HouseCardVO;
import top.pxczxn.common.constant.RedisKeys;
import top.pxczxn.common.redis.RedisFacade;
import top.pxczxn.common.result.PageData;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class SearchService {

    private final HouseService houseService;
    private final SearchHistoryMapper searchHistoryMapper;
    private final RedisFacade redisFacade;

    public PageData<HouseCardVO> search(HouseQueryDTO query) {
        if (StringUtils.hasText(query.getKeyword())) {
            redisFacade.incrementZSet(RedisKeys.SEARCH_HOT, query.getKeyword(), 1);
            if (StpUtil.isLogin()) {
                SearchHistory history = new SearchHistory();
                history.setUserId(StpUtil.getLoginIdAsLong());
                history.setKeyword(query.getKeyword());
                searchHistoryMapper.insert(history);
            }
        }
        return houseService.page(query);
    }

    public List<String> hot() {
        Set<String> cached = redisFacade.topZSet(RedisKeys.SEARCH_HOT, 8);
        if (cached != null && !cached.isEmpty()) {
            return List.copyOf(cached);
        }
        return List.of("东京", "京都", "北海道", "海景房", "温泉", "民宿");
    }

    public List<String> history() {
        if (!StpUtil.isLogin()) {
            return List.of();
        }
        return searchHistoryMapper.selectList(Wrappers.<SearchHistory>lambdaQuery()
                        .eq(SearchHistory::getUserId, StpUtil.getLoginIdAsLong())
                        .orderByDesc(SearchHistory::getId)
                        .last("LIMIT 10"))
                .stream()
                .map(SearchHistory::getKeyword)
                .distinct()
                .toList();
    }

    public void clearHistory() {
        if (!StpUtil.isLogin()) {
            return;
        }
        searchHistoryMapper.delete(Wrappers.<SearchHistory>lambdaQuery()
                .eq(SearchHistory::getUserId, StpUtil.getLoginIdAsLong()));
    }
}
