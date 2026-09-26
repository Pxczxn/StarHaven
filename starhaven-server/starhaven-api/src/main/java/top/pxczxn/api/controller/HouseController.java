package top.pxczxn.api.controller;

import top.pxczxn.business.dto.HouseQueryDTO;
import top.pxczxn.business.service.HouseService;
import top.pxczxn.business.vo.HouseCardVO;
import top.pxczxn.business.vo.HouseDetailVO;
import top.pxczxn.common.result.PageData;
import top.pxczxn.common.result.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "房源")
@RestController
@RequestMapping("/api/v1/house")
@RequiredArgsConstructor
public class HouseController {

    private final HouseService houseService;

    @Operation(summary = "房源分页")
    @GetMapping("/page")
    public Result<PageData<HouseCardVO>> page(HouseQueryDTO query) {
        return Result.ok(houseService.page(query));
    }

    @Operation(summary = "推荐房源")
    @GetMapping("/recommend")
    public Result<List<HouseCardVO>> recommend() {
        return Result.ok(houseService.recommend());
    }

    @Operation(summary = "房源详情")
    @GetMapping("/{id}")
    public Result<HouseDetailVO> detail(@PathVariable Long id) {
        return Result.ok(houseService.detail(id));
    }
}
