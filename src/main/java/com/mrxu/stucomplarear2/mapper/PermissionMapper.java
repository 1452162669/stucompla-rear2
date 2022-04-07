package com.mrxu.stucomplarear2.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mrxu.stucomplarear2.entity.Permission;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Component;

import java.util.Set;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author Mr.Xu
 * @since 2021-12-01
 */
@Component
public interface PermissionMapper extends BaseMapper<Permission> {

    @Select("select permission_code from user natural join role_permission natural join permission where user_id=#{userId} and father_id!=0")
    Set<String> selectPermissionsByUserId(Integer userId);

    @Select("select permission_code from admin natural join role_permission natural join permission where admin_id=#{adminId} and father_id!=0")
    Set<String> selectPermissionsByAdminId(Integer adminId);
}
