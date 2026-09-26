package top.pxczxn.api.controller;

import top.pxczxn.business.service.FavoriteService;
import top.pxczxn.business.vo.HouseCardVO;
import top.pxczxn.common.result.PageData;
import top.pxczxn.common.result.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Tag(name = "收藏")
@RestController
@RequestMapping("/api/v1/favorite")
@RequiredArgsConstructor
public class FavoriteController {

    private final FavoriteService favoriteService;

    @Operation(summary = "收藏 / 取消收藏")
    @PostMapping("/toggle")
    public Result<Map<String, Object>> toggle(@RequestParam Long houseId) {
        return Result.ok(favoriteService.toggle(houseId));
    }

    @Operation(summary = "收藏列表")
    @GetMapping("/list")
    public Result<PageData<HouseCardVO>> list(
            @RequestParam(defaultValue = "1") long page,
            @RequestParam(defaultValue = "10") long size) {
        return Result.ok(favoriteService.list(page, size));
    }
}
