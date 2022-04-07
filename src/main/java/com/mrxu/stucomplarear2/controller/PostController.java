package com.mrxu.stucomplarear2.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.mrxu.stucomplarear2.dto.AdminFindDto;
import com.mrxu.stucomplarear2.dto.PostFindDto;
import com.mrxu.stucomplarear2.dto.PostPublishDto;
import com.mrxu.stucomplarear2.entity.Category;
import com.mrxu.stucomplarear2.entity.Post;
import com.mrxu.stucomplarear2.mapper.CategoryMapper;
import com.mrxu.stucomplarear2.service.PostService;
import com.mrxu.stucomplarear2.utils.response.Result;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author Mr.Xu
 * @since 2021-12-27
 */
@CrossOrigin
@RestController
@RequestMapping("/post")
public class PostController {

    @Autowired
    private CategoryMapper categoryMapper;
    @Autowired
    private PostService postService;

    @ApiOperation("发帖")
    @RequiresRoles("user")
    @PostMapping("/publish")
    public Result publish(HttpServletRequest request, @RequestBody PostPublishDto postDto){
        //System.out.println(postDto);
        QueryWrapper<Category> queryWrapper =new QueryWrapper<>();
        queryWrapper.eq("category_id",postDto.getCategoryId());
        if (categoryMapper.selectOne(queryWrapper) == null || postDto.getTitle().isEmpty() || postDto.getDetail().isEmpty()) {
            return Result.fail("种类参数错误");
        }
        postService.publishPost(request,postDto);
        return Result.succ(postDto);
    }

    @ApiOperation("帖子详情")
    @GetMapping("/{postId}")
    public Result getPost(@PathVariable("postId") Integer postId){
        try {
            QueryWrapper<Post> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("post_id",postId);
            Post post = postService.getOne(queryWrapper);

            post = postService.updateViewNum(post);

            return Result.succ(post);
        }catch (Exception e) {
            e.printStackTrace();
            return Result.fail(e.getMessage());
        }
    }

    @ApiOperation("获取帖子列表")
    @RequiresRoles(value = {"admin", "super"}, logical = Logical.OR)
    @GetMapping("/list")
    public Result listPost(PostFindDto postFindDto) {
        Map<String, Object> map = postService.findPostList(postFindDto);
        return Result.succ(map);
    }

}
