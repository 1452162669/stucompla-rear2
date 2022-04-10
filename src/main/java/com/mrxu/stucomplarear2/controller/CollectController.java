package com.mrxu.stucomplarear2.controller;


import com.mrxu.stucomplarear2.service.CollectService;
import com.mrxu.stucomplarear2.utils.response.Result;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author Mr.Xu
 * @since 2022-04-09
 */
@RestController
@RequestMapping("/collect")
public class CollectController {

    @Resource
    private CollectService collectService;

    @ApiOperation("查看是否收藏")
    @RequiresRoles("user")
    @GetMapping("/check/{postId}")
    public Result check(@PathVariable("postId") Integer postId, HttpServletRequest request) {
        Result result = collectService.checkCollect(postId, request);
        return result;
    }

    @ApiOperation("收藏帖子")
    @RequiresRoles("user")
    @PostMapping("/add/{postId}")
    public Result add(@PathVariable("postId") Integer postId, HttpServletRequest request) {
        Result result = collectService.add(postId, request);
        return result;
    }

    @ApiOperation("取消收藏")
    @RequiresRoles("user")
    @DeleteMapping("/delete/{postId}")
    public Result delete(@PathVariable("postId") Integer postId, HttpServletRequest request) {
        Result result = collectService.deleteCollect(postId, request);
        return result;
    }

}
