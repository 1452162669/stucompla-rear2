package com.mrxu.stucomplarear2.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mrxu.stucomplarear2.dto.RegisterDto;
import com.mrxu.stucomplarear2.dto.UserEditDto;
import com.mrxu.stucomplarear2.dto.UserFindDto;
import com.mrxu.stucomplarear2.entity.User;
import com.mrxu.stucomplarear2.mapper.UserMapper;
import com.mrxu.stucomplarear2.service.UserService;
import com.mrxu.stucomplarear2.utils.jwt.JWTUtil;
import com.mrxu.stucomplarear2.utils.response.Result;
import org.apache.commons.lang.StringUtils;
import org.apache.shiro.crypto.hash.SimpleHash;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author Mr.Xu
 * @since 2021-12-01
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public String register(RegisterDto registerDto) {
        String ans = isTrue(registerDto);
        if (ans.equals("OK")) {
            User user = new User();
            user.setUsername(registerDto.getUsername());
            user.setPassword(String.valueOf(new SimpleHash("SHA-1",
                    registerDto.getPassword(), //原始密码
                    registerDto.getUsername(),//用用户名当盐值
                    16)));//加密次数
            user.setSex(registerDto.getSex());
            user.setRoleId(1);
            userMapper.insert(user);
            return "注册成功";
        }
        return ans;
    }

    @Override
    public User getUserByUsername(String username) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", username);
        User user = userMapper.selectOne(queryWrapper);
        return user;
    }

    @Override
    public User getUserByUserId(String userId) {
        User user = userMapper.selectById(userId);
        return user;
    }

    /**
     * 修改密码
     *
     * @param request
     * @param oldPassword
     * @param inPassword
     * @param secondPassword
     * @return
     */
    @Override
    public String changePassword(ServletRequest request, String oldPassword, String inPassword, String secondPassword) {
        if (inPassword == null || inPassword == "") {
            return "密码不能为空";
        }
        if (!inPassword.equals(secondPassword)) {
            return "重复密码不匹配";
        }
        if (inPassword.length() > 16 || inPassword.length() < 6) {
            return "密码长度只能在6~16位";
        }
        HttpServletRequest req = (HttpServletRequest) request;
        //获取传递过来的accessToken
        String accessToken = req.getHeader("Authorization");
        //获取token里面的用户ID
        String userId = JWTUtil.getUserId(accessToken);
        User user = userMapper.selectById(userId);

        oldPassword = String.valueOf(new SimpleHash("SHA-1",
                oldPassword, //原始密码
                user.getUsername(),//用用户名当盐值
                16));
        inPassword = String.valueOf(new SimpleHash("SHA-1",
                inPassword, //原始密码
                user.getUsername(),//用用户名当盐值
                16));
        if (!user.getPassword().equals(oldPassword)) {
            return "旧密码不正确";
        }
        user.setPassword(inPassword);
        userMapper.updateById(user);
        return "密码修改成功";
    }

    @Override
    public Map<String, Object> findUserList(UserFindDto userFindDto) {
        int pageNum = userFindDto.getPageNum() == null ? 1 : userFindDto.getPageNum();
        int pageSize = userFindDto.getPageSize() == null ? 4 : userFindDto.getPageSize();
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        if (userFindDto.getUserId() != null) {
            queryWrapper.eq("user_id", userFindDto.getUserId());
        }
        if (StringUtils.isNotBlank(userFindDto.getUsername())) {
            queryWrapper.eq("username", userFindDto.getUsername());
        }
        if (StringUtils.isNotBlank(userFindDto.getSex())) {
            queryWrapper.eq("sex", userFindDto.getSex());
        }
        if (userFindDto.getStatus() != null) {
            if (userFindDto.getStatus().equals(1)) {
                queryWrapper.eq("locked", 1);
            } else if (userFindDto.getStatus().equals(0)) {
                queryWrapper.eq("locked", 0);
            }
        }

        if ("+id".equals(userFindDto.getSort())) {
            queryWrapper.orderByAsc("user_id"); //根据admin_id升序排列
        } else {
            queryWrapper.orderByDesc("user_id"); //根据admin_id降序排列
        }
        //当前页 页面大小
        IPage<User> page = new Page<User>(pageNum, pageSize);

        IPage<User> userIPage = userMapper.selectPage(page, queryWrapper);
        Map<String, Object> map = new HashMap<>();
        map.put("current", userIPage.getCurrent());//当前页
        map.put("total", userIPage.getTotal());//总记录数
        map.put("pages", userIPage.getPages());//总页数
        map.put("pageSize", userIPage.getSize());//页面大小
        map.put("userList", userIPage.getRecords());//数据
        return map;
    }

    @Override
    public Result editUserInfo(UserEditDto userEditDto, HttpServletRequest request) {
        try {
            String accessToken = request.getHeader("Authorization");
            //获取token里面的用户ID
            String userId = JWTUtil.getUserId(accessToken);

            if (userEditDto.getUserId() == null) {
                return Result.fail("用户ID为空");
            }
            User user = userMapper.selectById(userEditDto.getUserId());
            if (user.getUserId() != Integer.valueOf(userId)) {
                return Result.fail("不可编辑别人的信息");
            }
            user.setUsername(userEditDto.getUsername());
            user.setSex(userEditDto.getSex());
            if(StringUtils.isNotBlank(userEditDto.getAvatar())){
                user.setAvatar(userEditDto.getAvatar());
            }
            user.setSignature(userEditDto.getSignature());
            userMapper.updateById(user);
            return Result.succ("修改成功");
        } catch (Exception e) {
            return Result.fail(e.toString());
        }
    }

    /**
     * 验证注册信息是否合规
     *
     * @param registerDto
     * @return
     */
    public String isTrue(RegisterDto registerDto) {
        if (registerDto.getUsername() == null || registerDto.getUsername() == "") {
            return "账号不能为空";
        }
        if (userMapper.selectUserIdByUsername(registerDto.getUsername()) != null) {
            return "该账号已经存在";
        }
        if (registerDto.getPassword() == null || registerDto.getPassword() == "") {
            return "密码不能为空";
        }
        if (!registerDto.getPassword().equals(registerDto.getSecondPassword())) {
            return "重复密码不匹配";
        }
        if (registerDto.getPassword().length() > 16 || registerDto.getPassword().length() < 6) {
            return "密码长度只能在6~16位";
        }
        if (!registerDto.getSex().equals("男") && !registerDto.getSex().equals("女")) {
            return "性别参数错误";
        }
        return "OK";
    }
}
