package top.pxczxn.api.controller;

import top.pxczxn.business.service.BrowseHistoryService;
import top.pxczxn.business.vo.HouseCardVO;
import top.pxczxn.common.result.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "浏览记录")
@RestController
@RequestMapping("/api/v1/browse")
@RequiredArgsConstructor
public class BrowseController {

    private final BrowseHistoryService browseHistoryService;

    @Operation(summary = "记录浏览房源")
    @PostMapping("/record/{houseId}")
    public Result<Void> record(@PathVariable Long houseId) {
        browseHistoryService.record(houseId);
        return Result.ok();
    }

    @Operation(summary = "浏览记录列表")
    @GetMapping("/history")
    public Result<List<HouseCardVO>> history() {
        return Result.ok(browseHistoryService.list());
    }

    @Operation(summary = "浏览记录条数")
    @GetMapping("/count")
    public Result<Long> count() {
        return Result.ok(browseHistoryService.count());
    }

    @Operation(summary = "清空浏览记录")
    @DeleteMapping("/history")
    public Result<Void> clearHistory() {
        browseHistoryService.clear();
        return Result.ok();
    }
}
