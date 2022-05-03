package com.mrxu.stucomplarear2.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mrxu.stucomplarear2.dto.CommentDto;
import com.mrxu.stucomplarear2.dto.CommentVo;
import com.mrxu.stucomplarear2.dto.PostVo;
import com.mrxu.stucomplarear2.entity.Category;
import com.mrxu.stucomplarear2.entity.Comment;
import com.mrxu.stucomplarear2.entity.Post;
import com.mrxu.stucomplarear2.entity.User;
import com.mrxu.stucomplarear2.mapper.CategoryMapper;
import com.mrxu.stucomplarear2.mapper.CommentMapper;
import com.mrxu.stucomplarear2.mapper.PostMapper;
import com.mrxu.stucomplarear2.mapper.UserMapper;
import com.mrxu.stucomplarear2.service.CommentService;
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
public class CommentServiceImpl extends ServiceImpl<CommentMapper, Comment> implements CommentService {

    @Autowired
    private PostMapper postMapper;
    @Autowired
    private CommentMapper commentMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private CategoryMapper categoryMapper;

    @Override
    public Result createComment(HttpServletRequest request, CommentDto commentDto) {
        try {
            Post post = postMapper.selectById(commentDto.getPostId());
            if (post == null) {
                return Result.fail("帖子不存在");
            }
            if (commentDto.getParentId() != null && commentMapper.selectById(commentDto.getParentId()) == null) {
                return Result.fail("父评论不存在");
            }
            if (commentDto.getText() == null || commentDto.getText() == "") {
                return Result.fail("内容不能为空");
            }
            Comment comment = new Comment();
            comment.setPostId(commentDto.getPostId());
            comment.setParentId(commentDto.getParentId());
            comment.setText(commentDto.getText());
            if (StringUtils.isNotBlank(commentDto.getImages())) {
                comment.setImages(commentDto.getImages());
            }
            String accessToken = request.getHeader("Authorization");
            //获取token里面的用户ID
            String userId = JWTUtil.getUserId(accessToken);
            comment.setUserId(Integer.valueOf(userId));
            commentMapper.insert(comment);

            // 更新评论数
            post.setCommentNum(post.getCommentNum() + 1);
            postMapper.updateById(post);

            return Result.succ("发布成功");
        } catch (Exception e) {
            e.printStackTrace();
            return Result.fail("异常");
        }
    }

    @Override
    public Result listCommentFromPost(Integer postId, Integer page, Integer size) {
        try {
            if (postMapper.selectById(postId) == null) {
                return Result.fail("帖子不存在");
            }
            //处理page和size 参数检查
            int pageNum = page == null ? 1 : page;
            int pageSize = size == null ? 4 : size;
            //创建分页条件
            QueryWrapper<Comment> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("post_id", postId);
            //当前页 页面大小
            IPage<Comment> iPage = new Page<Comment>(pageNum, pageSize);
            queryWrapper.orderByDesc("create_time"); //根据上传时间降序排列

            IPage<Comment> commentIPage = commentMapper.selectPage(iPage, queryWrapper);
            Map<String, Object> map = new HashMap<>();
            map.put("current", commentIPage.getCurrent());//当前页
            map.put("total", commentIPage.getTotal());//总记录数
            map.put("pages", commentIPage.getPages());//总页数
            map.put("pageSize", commentIPage.getSize());//页面大小
            List<CommentVo> commentVoList = new ArrayList<>();
            for (Comment comment : commentIPage.getRecords()) {
                CommentVo commentVo = new CommentVo();
                BeanUtils.copyProperties(comment, commentVo);
                //查对应的发布人信息
                User user = userMapper.selectById(comment.getUserId());
                commentVo.setUser(user);

                if(comment.getParentId()!=null){
                    //查对应的父评论信息
                    Comment parentComment = commentMapper.selectById(comment.getParentId());
                    CommentVo parentCommentVo = new CommentVo();
                    BeanUtils.copyProperties(parentComment, parentCommentVo);
                    User parentCommentUser = userMapper.selectById(parentComment.getUserId());
                    parentCommentVo.setUser(parentCommentUser);
                    commentVo.setParentCommentVo(parentCommentVo);
                }
                commentVoList.add(commentVo);
            }
            map.put("comments", commentVoList);//数据
            return Result.succ(map);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.fail("异常");
        }
    }

    @Override
    public Result getMyList(Integer page, Integer size,HttpServletRequest request) {
        try {
            String accessToken = request.getHeader("Authorization");
            //获取token里面的用户ID
            String userId = JWTUtil.getUserId(accessToken);
            if (userMapper.selectById(userId) == null) {
                return Result.fail("用户不存在");
            }

            int pageNum = page == null ? 1 : page;
            int pageSize = size == null ? 4 : size;


            //创建分页条件
            QueryWrapper<Comment> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("user_id", userId);
            //当前页 页面大小
            IPage<Comment> iPage = new Page<Comment>(pageNum, pageSize);
            queryWrapper.orderByDesc("create_time"); //根据评论时间降序排列

            IPage<Comment> commentIPage = commentMapper.selectPage(iPage, queryWrapper);
            Map<String, Object> map = new HashMap<>();
            map.put("current", commentIPage.getCurrent());//当前页
            map.put("total", commentIPage.getTotal());//总记录数
            map.put("pages", commentIPage.getPages());//总页数
            map.put("pageSize", commentIPage.getSize());//页面大小
            List<CommentVo> commentVoList = new ArrayList<>();
            for (Comment comment : commentIPage.getRecords()) {
                CommentVo commentVo = new CommentVo();
                BeanUtils.copyProperties(comment, commentVo);
                //查对应的发布人信息
                User user = userMapper.selectById(comment.getUserId());
                commentVo.setUser(user);
                //查帖子信息
                Post post = postMapper.selectById(comment.getPostId());

                Category category =categoryMapper.selectById(post.getCategoryId());
                PostVo postVo = new PostVo();
                BeanUtils.copyProperties(post,postVo);
                postVo.setCategory(category);
                commentVo.setPostVo(postVo);

                if(comment.getParentId()!=null){
                    //查对应的父评论信息
                    Comment parentComment = commentMapper.selectById(comment.getParentId());
                    CommentVo parentCommentVo = new CommentVo();
                    BeanUtils.copyProperties(parentComment, parentCommentVo);
                    User parentCommentUser = userMapper.selectById(parentComment.getUserId());
                    parentCommentVo.setUser(parentCommentUser);
                    commentVo.setParentCommentVo(parentCommentVo);
                }
                commentVoList.add(commentVo);
            }
            map.put("comments", commentVoList);//数据
            return Result.succ(map);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.fail("异常");
        }
    }

    @Override
    public Result deleteCommentByUser(Integer commentId, HttpServletRequest request) {
        try {
            String accessToken = request.getHeader("Authorization");
            //获取token里面的用户ID
            String userId = JWTUtil.getUserId(accessToken);

            if (commentId == null) {
                return Result.fail("评论ID为空");
            }
            Comment comment = commentMapper.selectById(commentId);
            if (comment==null){
                return Result.fail("评论不存在");
            }
            Post post = postMapper.selectById(comment.getPostId());

            if (post.getUserId()==Integer.valueOf(userId)||comment.getUserId()==Integer.valueOf(userId)) {
                // 发帖人是当前用户 || 评论人是当前用户
                commentMapper.deleteById(commentId);
                return Result.succ("删除成功");
            }else {
                return Result.fail("无权操作");
            }
        } catch (Exception e) {
            return Result.fail(e.toString());
        }
    }

    @Override
    public Result getCommentTotal() {
        try {
            QueryWrapper<Comment> queryWrapper = new QueryWrapper<>();
            Integer selectCount = commentMapper.selectCount(queryWrapper);
            return Result.succ(selectCount);
        } catch (Exception e) {
            return Result.fail(e.toString());
        }
    }
}
