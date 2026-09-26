package top.pxczxn.admin.controller;

import cn.dev33.satoken.annotation.SaCheckRole;
import top.pxczxn.common.result.PageData;
import top.pxczxn.common.result.Result;
import top.pxczxn.system.entity.User;
import top.pxczxn.system.service.UserAdminService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "后台用户管理")
@SaCheckRole("ADMIN")
@RestController
@RequestMapping("/admin/user")
@RequiredArgsConstructor
public class AdminUserController {

    private final UserAdminService userAdminService;

    @Operation(summary = "用户分页")
    @GetMapping("/page")
    public Result<PageData<User>> page(
            @RequestParam(defaultValue = "1") long page,
            @RequestParam(defaultValue = "10") long size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String role) {
        var data = userAdminService.page(page, size, keyword, role);
        data.getRecords().forEach(item -> item.setPassword(null));
        return Result.ok(new PageData<>(data.getTotal(), data.getRecords(), page, size));
    }

    @Operation(summary = "冻结 / 解冻")
    @PutMapping("/status")
    public Result<Void> status(@RequestParam Long id, @RequestParam Integer status) {
        userAdminService.updateStatus(id, status);
        return Result.ok();
    }

    @Operation(summary = "修改角色")
    @PutMapping("/role")
    public Result<Void> role(@RequestParam Long id, @RequestParam String role) {
        userAdminService.updateRole(id, role);
        return Result.ok();
    }

    @Operation(summary = "删除用户")
    @DeleteMapping
    public Result<Void> delete(@RequestParam Long id) {
        userAdminService.delete(id);
        return Result.ok();
    }
}
