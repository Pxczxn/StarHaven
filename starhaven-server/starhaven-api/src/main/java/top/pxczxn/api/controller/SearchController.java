package top.pxczxn.api.controller;

import top.pxczxn.business.dto.HouseQueryDTO;
import top.pxczxn.business.service.SearchService;
import top.pxczxn.business.vo.HouseCardVO;
import top.pxczxn.common.result.PageData;
import top.pxczxn.common.result.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "搜索")
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class SearchController {

    private final SearchService searchService;

    @Operation(summary = "搜索房源")
    @GetMapping("/search")
    public Result<PageData<HouseCardVO>> search(HouseQueryDTO query) {
        return Result.ok(searchService.search(query));
    }

    @Operation(summary = "热门搜索")
    @GetMapping("/search/hot")
    public Result<List<String>> hot() {
        return Result.ok(searchService.hot());
    }

    @Operation(summary = "搜索历史")
    @GetMapping("/search/history")
    public Result<List<String>> history() {
        return Result.ok(searchService.history());
    }

    @Operation(summary = "清空搜索历史")
    @DeleteMapping("/search/history")
    public Result<Void> clearHistory() {
        searchService.clearHistory();
        return Result.ok();
    }
}
