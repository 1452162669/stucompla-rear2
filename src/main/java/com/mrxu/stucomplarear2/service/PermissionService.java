package com.mrxu.stucomplarear2.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.mrxu.stucomplarear2.entity.Permission;

import java.util.Set;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author Mr.Xu
 * @since 2021-12-01
 */
public interface PermissionService extends IService<Permission> {

    Set<String> getPermissionsByUserId(Integer userId);

    Set<String> getPermissionsByAdminId(Integer adminId);
}
