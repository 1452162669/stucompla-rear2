package com.mrxu.stucomplarear2.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.mrxu.stucomplarear2.dto.GoodsAddDto;
import com.mrxu.stucomplarear2.dto.GoodsFindDto;
import com.mrxu.stucomplarear2.dto.GoodsVo;
import com.mrxu.stucomplarear2.dto.PostVo;
import com.mrxu.stucomplarear2.entity.*;
import com.mrxu.stucomplarear2.mapper.GoodsCategoryMapper;
import com.mrxu.stucomplarear2.mapper.UserMapper;
import com.mrxu.stucomplarear2.service.GoodsService;
import com.mrxu.stucomplarear2.utils.response.Result;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
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
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private GoodsCategoryMapper goodsCategoryMapper;

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
        System.out.println(goodsFindDto);
        Result result = goodsService.findGoods(goodsFindDto);
        return result;
    }

    @ApiOperation("商品详情")
    @GetMapping("/{goodsId}")
    public Result getGoodsDetail(@PathVariable("goodsId") Integer goodsId) {
        try {
            QueryWrapper<Goods> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("goods_id", goodsId);
            Goods goods = goodsService.getOne(queryWrapper);

            goods = goodsService.updateViewNum(goods);
            GoodsVo goodsVo = new GoodsVo();
            BeanUtils.copyProperties(goods, goodsVo);
            //查对应的发布人信息
            User user = userMapper.selectById(goods.getUserId());
            goodsVo.setUser(user);
            //查对应的帖子类型信息
            GoodsCategory goodsCategory = goodsCategoryMapper.selectById(goods.getGoodsCategoryId());
            goodsVo.setGoodsCategory(goodsCategory);

            return Result.succ(goodsVo);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.fail(e.getMessage());
        }
    }
}
