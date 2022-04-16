package com.mrxu.stucomplarear2.controller;


import com.mrxu.stucomplarear2.dto.GoodsAddDto;
import com.mrxu.stucomplarear2.dto.GoodsFindDto;
import com.mrxu.stucomplarear2.service.GoodsService;
import com.mrxu.stucomplarear2.utils.response.Result;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author Mr.Xu
 * @since 2022-04-15
 */
@RestController
@RequestMapping("/goods")
public class GoodsController {

    @Resource
    private GoodsService goodsService;

    @ApiOperation("发布二手商品")
    @RequiresRoles("user")
    @PostMapping("/add")
    public Result add(@RequestBody GoodsAddDto goodsDto, HttpServletRequest request) {
        // 这个@RequestBody有两个包，别导错了！！
        Result result = goodsService.add(goodsDto, request);
        return result;
    }

    @ApiOperation("获取二手商品列表")
    @GetMapping("/getList")
    public Result getList(GoodsFindDto goodsFindDto) {
        Result result = goodsService.findGoods(goodsFindDto);
        return result;
    }
}
