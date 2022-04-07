package com.mrxu.stucomplarear2.service;

import com.mrxu.stucomplarear2.dto.CommentDto;
import com.mrxu.stucomplarear2.entity.Comment;
import com.baomidou.mybatisplus.extension.service.IService;
import com.mrxu.stucomplarear2.utils.response.Result;

import javax.servlet.http.HttpServletRequest;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author Mr.Xu
 * @since 2021-12-27
 */
public interface CommentService extends IService<Comment> {

    Result createComment(HttpServletRequest request, CommentDto commentDto);

    Result listCommentFromPost(Integer postId,int page,int size);
}
