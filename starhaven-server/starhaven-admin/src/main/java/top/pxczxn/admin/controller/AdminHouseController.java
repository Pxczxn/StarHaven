package top.pxczxn.admin.controller;

import cn.dev33.satoken.annotation.SaCheckRole;
import cn.dev33.satoken.annotation.SaMode;
import top.pxczxn.business.service.HouseService;
import top.pxczxn.business.vo.HouseCardVO;
import top.pxczxn.common.auth.StaffAuth;
import top.pxczxn.common.result.PageData;
import top.pxczxn.common.result.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "后台房源管理")
@SaCheckRole(value = {"ADMIN", "HOST"}, mode = SaMode.OR)
@RestController
@RequestMapping("/admin/house")
@RequiredArgsConstructor
public class AdminHouseController {

    private final HouseService houseService;

    @Operation(summary = "房源分页")
    @GetMapping("/page")
    public Result<PageData<HouseCardVO>> page(
            @RequestParam(defaultValue = "1") long page,
            @RequestParam(defaultValue = "10") long size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer auditStatus) {
        return Result.ok(houseService.adminPage(page, size, keyword, auditStatus, StaffAuth.merchantScopeId()));
    }

    @SaCheckRole("ADMIN")
    @Operation(summary = "审核房源 1通过 2拒绝")
    @PutMapping("/audit")
    public Result<Void> audit(@RequestParam Long id, @RequestParam Integer auditStatus) {
        houseService.audit(id, auditStatus);
        return Result.ok();
    }

    @Operation(summary = "上下架 1上架 0下架")
    @PutMapping("/status")
    public Result<Void> status(@RequestParam Long id, @RequestParam Integer status) {
        houseService.updateStatus(id, status);
        return Result.ok();
    }

    @Operation(summary = "删除房源")
    @DeleteMapping
    public Result<Void> delete(@RequestParam Long id) {
        houseService.delete(id);
        return Result.ok();
    }
}
