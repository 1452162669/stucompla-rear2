package com.mrxu.stucomplarear2.controller;

import com.mrxu.stucomplarear2.utils.response.Result;
import com.mrxu.stucomplarear2.service.UserService;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class UserTestController {

    @Autowired
    private UserService userService;

    @ApiOperation("test")
    @RequiresAuthentication
    @GetMapping("/test")
    public Result test(){
        return Result.succ("test");
    }

    @ApiOperation("admin")
    @RequiresRoles("admin")
    @GetMapping("/admin")
    public Result admin(){
        return Result.succ("admin");
    }

    @ApiOperation("vip")
    @RequiresRoles("vip")
    @PostMapping("/vip")
    public Result vip(){
        return Result.succ("vip");
    }

    @ApiOperation("update")
    @RequiresPermissions("update")
    @PutMapping("/update")
    public Result update(){
        return Result.succ("update");
    }

    @ApiOperation("delete")
    @RequiresPermissions("delete")
    @DeleteMapping("/delete")
    public Result delete(){
        return Result.succ("delete");
    }

    @ApiOperation("quest")
    @GetMapping("/guest")
    public Result guest(){
        return Result.succ("guest");
    }
}
