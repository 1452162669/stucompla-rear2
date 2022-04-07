package com.mrxu.stucomplarear2.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mrxu.stucomplarear2.entity.Permission;
import com.mrxu.stucomplarear2.mapper.PermissionMapper;
import com.mrxu.stucomplarear2.service.PermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author Mr.Xu
 * @since 2021-12-01
 */
@Service
public class PermissionServiceImpl extends ServiceImpl<PermissionMapper, Permission> implements PermissionService {
    @Autowired
    private PermissionMapper permissionMapper;

    @Override
    public Set<String> getPermissionsByUserId(Integer userId) {
        return permissionMapper.selectPermissionsByUserId(userId);
    }

    @Override
    public Set<String> getPermissionsByAdminId(Integer adminId) {
        return permissionMapper.selectPermissionsByAdminId(adminId);
    }
}
