package com.mrxu.stucomplarear2.controller;


import com.mrxu.stucomplarear2.dto.CommentDto;
import com.mrxu.stucomplarear2.service.CommentService;
import com.mrxu.stucomplarear2.utils.response.Result;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author Mr.Xu
 * @since 2021-12-27
 */
@RestController
@RequestMapping("/comment")
public class CommentController {

    @Autowired
    private CommentService commentService;

    @ApiOperation("评论")
    @PostMapping("/create")
    public Result createComment(HttpServletRequest request, @RequestBody CommentDto commentDto) {
        return commentService.createComment(request, commentDto);
    }

    @ApiOperation("帖子的评论列表")
    @GetMapping("/list/{postId}/{page}/{size}")
    public Result listComment(@PathVariable("postId") Integer postId, @PathVariable("page") int page, @PathVariable("size") int size) {

        return commentService.listCommentFromPost(postId, page, size);
    }
}
