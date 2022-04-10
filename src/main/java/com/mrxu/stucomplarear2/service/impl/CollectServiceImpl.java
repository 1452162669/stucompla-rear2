package com.mrxu.stucomplarear2.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mrxu.stucomplarear2.entity.Collect;
import com.mrxu.stucomplarear2.mapper.CollectMapper;
import com.mrxu.stucomplarear2.mapper.PostMapper;
import com.mrxu.stucomplarear2.service.CollectService;
import com.mrxu.stucomplarear2.utils.jwt.JWTUtil;
import com.mrxu.stucomplarear2.utils.response.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author Mr.Xu
 * @since 2022-04-09
 */
@Service
public class CollectServiceImpl extends ServiceImpl<CollectMapper, Collect> implements CollectService {

    @Autowired
    private PostMapper postMapper;
    @Autowired
    private CollectMapper collectMapper;

    @Override
    public Result add(Integer postId, HttpServletRequest request) {
        try {
            String accessToken = request.getHeader("Authorization");
            //获取token里面的用户ID
            String userId = JWTUtil.getUserId(accessToken);
            if (userId == null || postId == null) {
                Result.fail("参数错误");
            }
            if (postMapper.selectById(postId) == null) {
                Result.fail("帖子不存在");
            }
            Collect collect = new Collect();
            collect.setUserId(Integer.valueOf(userId));
            collect.setPostId(postId);
            collectMapper.insert(collect);
        } catch (Exception e) {
            e.printStackTrace();
            Result.fail(e.toString());
        }
        return Result.succ("收藏成功");
    }

    @Override
    public Result deleteCollect(Integer postId, HttpServletRequest request) {
        try {
            String accessToken = request.getHeader("Authorization");
            //获取token里面的用户ID
            String userId = JWTUtil.getUserId(accessToken);
            if (userId == null || postId == null) {
                Result.fail("参数错误");
            }
            if (postMapper.selectById(postId) == null) {
                Result.fail("帖子不存在");
            }
            QueryWrapper<Collect> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("post_id", postId);
            queryWrapper.eq("user_id", Integer.valueOf(userId));
            if (collectMapper.selectOne(queryWrapper) == null) {
                return Result.fail("您还没收藏，取消收藏失败");
            }
            collectMapper.delete(queryWrapper);
        } catch (Exception e) {
            e.printStackTrace();
            Result.fail(e.toString());
        }
        return Result.succ("取消收藏成功");
    }

    @Override
    public Result checkCollect(Integer postId, HttpServletRequest request) {
        try {
            String accessToken = request.getHeader("Authorization");
            //获取token里面的用户ID
            String userId = JWTUtil.getUserId(accessToken);
            if (userId == null || postId == null) {
                Result.fail("参数错误");
            }
            if (postMapper.selectById(postId) == null) {
                Result.fail("帖子不存在");
            }
            QueryWrapper<Collect> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("post_id", postId);
            queryWrapper.eq("user_id", Integer.valueOf(userId));
            if (collectMapper.selectOne(queryWrapper) == null) {
                return Result.succ(false);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Result.fail(e.toString());
        }
        return Result.succ(true);
    }
}
