package com.mrxu.stucomplarear2.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.mrxu.stucomplarear2.dto.RegisterDto;
import com.mrxu.stucomplarear2.dto.UserFindDto;
import com.mrxu.stucomplarear2.entity.User;

import javax.servlet.ServletRequest;
import java.util.Map;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author Mr.Xu
 * @since 2021-12-01
 */
public interface UserService extends IService<User> {

    String register(RegisterDto registerDto);

    User getUserByUsername(String username);

    User getUserByUserId(String userId);

    String changePassword(ServletRequest request, String oldPassword, String password, String secondPassword);

    Map<String, Object> findAdminList(UserFindDto userFindDto);
}
