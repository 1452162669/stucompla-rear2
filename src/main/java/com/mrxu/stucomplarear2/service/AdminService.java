package com.mrxu.stucomplarear2.service;

import com.mrxu.stucomplarear2.dto.AdminFindDto;
import com.mrxu.stucomplarear2.entity.Admin;
import com.baomidou.mybatisplus.extension.service.IService;
import com.mrxu.stucomplarear2.utils.response.Result;

import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author Mr.Xu
 * @since 2022-04-02
 */
public interface AdminService extends IService<Admin> {

    Result addAdmin(String username, String password, int roleId);

    Result login(String username, String password, HttpServletResponse response);

    Admin getAdminByAdminId(String adminId);

    Map<String, Object> findAdminList(AdminFindDto adminFindDto);
}
