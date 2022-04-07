package com.mrxu.stucomplarear2.controller.admin;


import com.mrxu.stucomplarear2.dto.AdminFindDto;
import com.mrxu.stucomplarear2.dto.LoginDto;
import com.mrxu.stucomplarear2.entity.Admin;
import com.mrxu.stucomplarear2.service.AdminService;
import com.mrxu.stucomplarear2.utils.jwt.JWTUtil;
import com.mrxu.stucomplarear2.utils.redis.RedisUtil;
import com.mrxu.stucomplarear2.utils.response.Result;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author Mr.Xu
 * @since 2022-04-02
 */
@RestController
@RequestMapping("/admin/info")
public class AdminController {

    @Resource
    private AdminService adminService;
    @Autowired
    private RedisUtil redisUtil;

    @ApiOperation("获取当前管理员信息")
    @RequiresRoles(value = {"admin", "super"}, logical = Logical.OR)
    @GetMapping
    public Result getInfo(HttpServletRequest request) {
        String accessToken = request.getHeader("Authorization");
        //获取token里面的用户ID
        String adminId = JWTUtil.getUserId(accessToken);
        Admin admin = adminService.getAdminByAdminId(adminId);
        return Result.succ(admin);
    }

    @ApiOperation("获取管理员列表")
    @RequiresRoles("super")
    @GetMapping("/list")
    public Result listAdmin(AdminFindDto adminFindDto) {
        Map<String, Object> map = adminService.findAdminList(adminFindDto);
        return Result.succ(map);
    }

    @ApiOperation("添加管理员")
    @RequiresRoles("super")
    @PostMapping("/add")
    public Result addAdmin(@RequestParam String username, @RequestParam String password, @RequestParam int roleId) {
        return adminService.addAdmin(username, password, roleId);
    }

    @ApiOperation("管理员登录")
    @PostMapping("/login")
    public Result login(@RequestBody LoginDto loginDto, HttpServletResponse response) {
        String username = loginDto.getUsername();
        String password = loginDto.getPassword();
        return adminService.login(username, password, response);
    }

    @ApiOperation("管理员登出")
    @DeleteMapping("/logout")
    @RequiresAuthentication
    public Result logout(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        String adminId = JWTUtil.getUserId(token);
        redisUtil.del("Admin" + adminId);
        return Result.succ("退出成功");
    }
}
