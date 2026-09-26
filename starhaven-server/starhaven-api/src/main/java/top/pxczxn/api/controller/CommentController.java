package top.pxczxn.api.controller;

import top.pxczxn.business.dto.CreateCommentDTO;
import top.pxczxn.business.service.CommentService;
import top.pxczxn.business.vo.CommentVO;
import top.pxczxn.common.result.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "评论")
@RestController
@RequestMapping("/api/v1/comment")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @Operation(summary = "发表评论")
    @PostMapping("/create")
    public Result<Void> create(@Valid @RequestBody CreateCommentDTO dto) {
        commentService.create(dto);
        return Result.ok();
    }

    @Operation(summary = "房源评论列表")
    @GetMapping("/list/{houseId}")
    public Result<List<CommentVO>> list(@PathVariable Long houseId) {
        return Result.ok(commentService.listByHouse(houseId));
    }
}
