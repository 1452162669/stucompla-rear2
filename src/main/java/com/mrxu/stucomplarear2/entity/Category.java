package com.mrxu.stucomplarear2.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
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
public class Category implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 种类ID
     */
    @TableId(value = "category_id", type = IdType.AUTO)
    private Integer categoryId;

    /**
     * 种类名
     */
    private String categoryName;

    /**
     * 描述
     */
    private String description;

    /**
     * 帖子数
     */
    private Integer postNum;

    /**
     * 浏览量
     */
    private Integer viewNum;

    /**
     * 图标
     */
    private String icon;


}
