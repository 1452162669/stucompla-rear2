package com.mrxu.stucomplarear2.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mrxu.stucomplarear2.dto.AdminFindDto;
import com.mrxu.stucomplarear2.entity.Admin;
import com.mrxu.stucomplarear2.entity.Wall;
import com.mrxu.stucomplarear2.mapper.AdminMapper;
import com.mrxu.stucomplarear2.service.AdminService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mrxu.stucomplarear2.utils.jwt.JWTUtil;
import com.mrxu.stucomplarear2.utils.redis.RedisUtil;
import com.mrxu.stucomplarear2.utils.response.Result;
import org.apache.commons.lang.StringUtils;
import org.apache.shiro.crypto.hash.SimpleHash;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author Mr.Xu
 * @since 2022-04-02
 */
@Service
public class AdminServiceImpl extends ServiceImpl<AdminMapper, Admin> implements AdminService {

    @Autowired
    private AdminMapper adminMapper;
    @Autowired
    private RedisUtil redisUtil;

    @Override
    public Result addAdmin(String username, String password, int roleId) {
        //查询是否有重名
        QueryWrapper<Admin> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", username);
        if (adminMapper.selectOne(queryWrapper) != null) {
            return Result.fail("该管理员已存在");
        }
        //没有重名则添加
        Admin admin = new Admin();
        admin.setUsername(username);
        admin.setPassword(String.valueOf(new SimpleHash("SHA-1",
                password, //原始密码
                username,//用用户名当盐值
                16))); //加密次数
        admin.setRoleId(roleId);
        adminMapper.insert(admin);
        return Result.succ("添加成功");
    }

    @Override
    public Result login(String username, String password, HttpServletResponse response) {
        //查询管理员是否存在
        QueryWrapper<Admin> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", username);
        queryWrapper.eq("password", String.valueOf(new SimpleHash("SHA-1",
                password, //输入的原始密码
                username,//用户名当盐值
                16)));
        Admin admin = adminMapper.selectOne(queryWrapper);
        if (admin == null) {
            return Result.fail("用户名或密码错误！");
        }
        long currentTimeMillis = System.currentTimeMillis();
        String token = JWTUtil.createToken(String.valueOf(admin.getAdminId()), currentTimeMillis, "Admin");
        redisUtil.set("Admin" + admin.getAdminId(), currentTimeMillis, 60 * 30);//redis里存30分钟
        (response).setHeader("Authorization", token);
        (response).setHeader("Access-Control-Expose-Headers", "Authorization");//前端可以拿到这个响应头
        return Result.succ(200, "登陆成功", token);
    }

    @Override
    public Admin getAdminByAdminId(String adminId) {
        return adminMapper.selectById(adminId);
    }

    @Override
    public Map<String, Object> findAdminList(AdminFindDto adminFindDto) {
        int pageNum = adminFindDto.getPageNum() == null ? 1 : adminFindDto.getPageNum();
        int pageSize = adminFindDto.getPageSize() == null ? 4 : adminFindDto.getPageSize();
        QueryWrapper<Admin> queryWrapper = new QueryWrapper<>();
        if (adminFindDto.getAdminId() != null) {
            queryWrapper.eq("admin_id", adminFindDto.getAdminId());
        }
        if (StringUtils.isNotBlank(adminFindDto.getUsername())) {
            queryWrapper.eq("username", adminFindDto.getUsername());
        }
        if (adminFindDto.getRoleId() != null) {
            queryWrapper.eq("role_id", adminFindDto.getRoleId());
        }
        if ("+id".equals(adminFindDto.getSort())) {
            queryWrapper.orderByAsc("admin_id"); //根据admin_id升序排列
        } else {
            queryWrapper.orderByDesc("admin_id"); //根据admin_id降序排列
        }
        //当前页 页面大小
        IPage<Admin> page = new Page<Admin>(pageNum, pageSize);


        IPage<Admin> adminIPage = adminMapper.selectPage(page, queryWrapper);
        Map<String, Object> map = new HashMap<>();
        map.put("current", adminIPage.getCurrent());//当前页
        map.put("total", adminIPage.getTotal());//总记录数
        map.put("pages", adminIPage.getPages());//总页数
        map.put("pageSize", adminIPage.getSize());//页面大小
        map.put("adminList", adminIPage.getRecords());//数据
        return map;
    }


}
