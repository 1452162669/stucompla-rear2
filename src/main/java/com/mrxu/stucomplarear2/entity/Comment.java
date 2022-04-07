package com.mrxu.stucomplarear2.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;
import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 
 * </p>
 *
 * @author Mr.Xu
 * @since 2021-12-27
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class Comment implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 评论ID
     */
    @TableId(value = "comment_id", type = IdType.AUTO)
    private Integer commentId;

    /**
     * 内容
     */
    private String text;

    /**
     * 图片
     */
    private String images;

    /**
     * 帖子ID
     */
    private Integer postId;

    /**
     * 父评论ID
     */
    private Integer parentId;

    /**
     * 评论人ID
     */
    private Integer userId;

    /**
     * 评论时间
     */
    @TableField(fill = FieldFill.INSERT)    //用于mybatisplus自动填充时间
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss", timezone="GMT+8")
    private Date createTime;


}
