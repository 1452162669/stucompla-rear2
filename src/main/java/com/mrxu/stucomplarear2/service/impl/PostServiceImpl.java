package com.mrxu.stucomplarear2.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mrxu.stucomplarear2.dto.PostFindDto;
import com.mrxu.stucomplarear2.dto.PostPublishDto;
import com.mrxu.stucomplarear2.dto.PostVo;
import com.mrxu.stucomplarear2.entity.Category;
import com.mrxu.stucomplarear2.entity.Post;
import com.mrxu.stucomplarear2.entity.User;
import com.mrxu.stucomplarear2.mapper.CategoryMapper;
import com.mrxu.stucomplarear2.mapper.PostMapper;
import com.mrxu.stucomplarear2.mapper.UserMapper;
import com.mrxu.stucomplarear2.service.PostService;
import com.mrxu.stucomplarear2.utils.jwt.JWTUtil;
import com.mrxu.stucomplarear2.utils.response.Result;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author Mr.Xu
 * @since 2021-12-27
 */
@Service
public class PostServiceImpl extends ServiceImpl<PostMapper, Post> implements PostService {

    @Autowired
    private PostMapper postMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private CategoryMapper categoryMapper;

    @Override
    public Result publishPost(HttpServletRequest request, PostPublishDto postDto) {
        Post post = new Post();
        post.setTitle(postDto.getTitle());
        post.setCategoryId(postDto.getCategoryId());
        post.setDetail(postDto.getDetail());
        post.setImages(postDto.getImages());
        String accessToken = request.getHeader("Authorization");
        //获取token里面的用户ID
        String userId = JWTUtil.getUserId(accessToken);
        post.setUserId(Integer.valueOf(userId));

        try {
            postMapper.insert(post);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.fail(e.getMessage());
        }
        return Result.succ("发布成功");
    }

    @Override
    public Post updateViewNum(Post post) {
        //更新浏览量
        post.setViewNum(post.getViewNum() + 1);
        postMapper.updateById(post);
        return post;
    }

    @Override
    public Map<String, Object> findPostList(PostFindDto postFindDto) {
        int pageNum = postFindDto.getPageNum() == null ? 1 : postFindDto.getPageNum();
        int pageSize = postFindDto.getPageSize() == null ? 4 : postFindDto.getPageSize();
        QueryWrapper<Post> queryWrapper = new QueryWrapper<>();
        if (postFindDto.getPostId() != null) {
            queryWrapper.eq("post_id", postFindDto.getPostId());
        }
        if (StringUtils.isNotBlank(postFindDto.getTitle())) {
            queryWrapper.like("title", postFindDto.getTitle()); //模糊搜索
        }
        if (StringUtils.isNotBlank(postFindDto.getDetail())) {
            queryWrapper.like("detail", postFindDto.getDetail()); //模糊搜索
        }
        if (postFindDto.getUserId() != null) {
            queryWrapper.eq("user_id", postFindDto.getUserId());
        }
        if (postFindDto.getCategoryId() != null) {
            queryWrapper.eq("category_id", postFindDto.getCategoryId());
        }
        String sort = postFindDto.getSort();
        if ("+post_id".equals(sort)) {
            queryWrapper.orderByAsc("post_id");//根据post_id升序排列
        }
        if ("-post_id".equals(sort)) {
            queryWrapper.orderByDesc("post_id");
        }
        if ("+view_num".equals(sort)) {
            queryWrapper.orderByAsc("view_num");
        }
        if ("-view_num".equals(sort)) {
            queryWrapper.orderByDesc("view_num");
        }
        if ("+comment_num".equals(sort)) {
            queryWrapper.orderByAsc("comment_num");
        }
        if ("-comment_num".equals(sort)) {
            queryWrapper.orderByDesc("comment_num");
        }
        if (postFindDto.getBestPost() != null) {
            System.out.println(postFindDto.getBestPost());
            if (postFindDto.getBestPost()) {
                queryWrapper.eq("best_post", postFindDto.getBestPost());
            } else {
                queryWrapper.eq("best_post", postFindDto.getBestPost());
            }
        }

        //当前页 页面大小
        IPage<Post> page = new Page<Post>(pageNum, pageSize);

        IPage<Post> postIPage = postMapper.selectPage(page, queryWrapper);
        Map<String, Object> map = new HashMap<>();
        map.put("current", postIPage.getCurrent());//当前页
        map.put("total", postIPage.getTotal());//总记录数
        map.put("pages", postIPage.getPages());//总页数

        List<PostVo> postVoList = new ArrayList<>();
        map.put("pageSize", postIPage.getSize());//页面大小

        for (Post post : postIPage.getRecords()) {
            PostVo postVo = new PostVo();
            BeanUtils.copyProperties(post, postVo);
            //查对应的发布人信息
            User user = userMapper.selectById(post.getUserId());
            postVo.setUser(user);
            //查对应的帖子类型信息
            Category category = categoryMapper.selectById(post.getCategoryId());
            postVo.setCategory(category);
//            System.out.println(postVo);
            postVoList.add(postVo);
//            System.out.println(postVoList);
        }

        map.put("postList", postVoList);//数据
//        System.out.println(postVoList);
        return map;
    }
}
