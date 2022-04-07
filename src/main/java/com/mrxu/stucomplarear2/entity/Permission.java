package com.mrxu.stucomplarear2.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * <p>
 * Permission实体类
 * </p>
 *
 * @author Mr.Xu
 * @since 2021-12-01
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("permission")
public class Permission implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 权限ID
     */
    @TableId(value = "permission_id", type = IdType.AUTO)
    private Integer permissionId;

    /**
     * 权限
     */
    private String permissionCode;

    /**
     * 权限名
     */
    private String permissionName;

    /**
     * 父权限
     */
    private Integer fatherId;

    /**
     * 路径
     */
    private String path;

    private Integer isMenu;


}
