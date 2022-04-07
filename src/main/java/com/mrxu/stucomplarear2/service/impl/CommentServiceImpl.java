package com.mrxu.stucomplarear2.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mrxu.stucomplarear2.dto.CommentDto;
import com.mrxu.stucomplarear2.entity.Comment;
import com.mrxu.stucomplarear2.mapper.CommentMapper;
import com.mrxu.stucomplarear2.mapper.PostMapper;
import com.mrxu.stucomplarear2.service.CommentService;
import com.mrxu.stucomplarear2.utils.Constants;
import com.mrxu.stucomplarear2.utils.jwt.JWTUtil;
import com.mrxu.stucomplarear2.utils.response.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
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

    @Override
    public Result createComment(HttpServletRequest request, CommentDto commentDto) {
        try {
            if (postMapper.selectById(commentDto.getPostId()) == null) {
                return Result.fail("帖子不存在");
            }
            if (commentDto.getParentId() != null && commentMapper.selectById(commentDto.getParentId()) == null) {
                return Result.fail("父评论不存在");
            }
            if(commentDto.getText()==null||commentDto.getText()==""){
                return Result.fail("内容不能为空");
            }
            Comment comment = new Comment();
            comment.setPostId(commentDto.getPostId());
            comment.setParentId(commentDto.getParentId());
            comment.setText(commentDto.getText());
            comment.setImages(commentDto.getImages());
            String accessToken = request.getHeader("Authorization");
            //获取token里面的用户ID
            String userId = JWTUtil.getUserId(accessToken);
            comment.setUserId(Integer.valueOf(userId));
            commentMapper.insert(comment);
            return Result.succ("发布成功");
        } catch (Exception e) {
            e.printStackTrace();
            return Result.fail("异常");
        }
    }

    @Override
    public Result listCommentFromPost(Integer postId, int page, int size) {
        try {
            if (postMapper.selectById(postId) == null) {
                return Result.fail("帖子不存在");
            }
            //处理page和size 参数检查
            if (page < Constants.Page.DEFAULT_PAGE) {
                page = Constants.Page.DEFAULT_PAGE;
            }
            if (size < Constants.Page.MIN_SIZE) {
                size = Constants.Page.MIN_SIZE;
            }
            //创建分页条件
            QueryWrapper<Comment> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("post_id",postId);
            //当前页 页面大小
            IPage<Comment> iPage = new Page<Comment>(page, size);
            queryWrapper.orderByDesc("create_time"); //根据上传时间降序排列

            IPage<Comment> commentIPage = commentMapper.selectPage(iPage,queryWrapper);
            Map<String, Object> map = new HashMap<>();
            map.put("current", commentIPage.getCurrent());//当前页
            map.put("total", commentIPage.getTotal());//总记录数
            map.put("pages", commentIPage.getPages());//总页数
            map.put("pageSize", commentIPage.getSize());//页面大小
            map.put("comments", commentIPage.getRecords());//数据
            return Result.succ(map);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.fail("异常");
        }
    }
}
