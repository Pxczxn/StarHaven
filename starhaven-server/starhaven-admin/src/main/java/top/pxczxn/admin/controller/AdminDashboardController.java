package top.pxczxn.admin.controller;

import cn.dev33.satoken.annotation.SaCheckRole;
import cn.dev33.satoken.annotation.SaMode;
import top.pxczxn.business.service.DashboardService;
import top.pxczxn.business.vo.DashboardVO;
import top.pxczxn.common.auth.StaffAuth;
import top.pxczxn.common.result.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "管理首页")
@SaCheckRole(value = {"ADMIN", "HOST"}, mode = SaMode.OR)
@RestController
@RequestMapping("/admin/dashboard")
@RequiredArgsConstructor
public class AdminDashboardController {

    private final DashboardService dashboardService;

    @Operation(summary = "运营概览")
    @GetMapping
    public Result<DashboardVO> dashboard() {
        return Result.ok(dashboardService.overview(StaffAuth.merchantScopeId()));
    }
}
