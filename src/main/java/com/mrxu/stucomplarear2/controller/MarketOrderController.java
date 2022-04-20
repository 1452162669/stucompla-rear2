package com.mrxu.stucomplarear2.controller;


import com.mrxu.stucomplarear2.dto.OrderAddDto;
import com.mrxu.stucomplarear2.service.MarketOrderService;
import com.mrxu.stucomplarear2.utils.response.Result;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author Mr.Xu
 * @since 2022-04-20
 */
@RestController
@RequestMapping("/market-order")
public class MarketOrderController {
    @Resource
    private MarketOrderService marketOrderService;

    @ApiOperation("添加订单")
    @RequiresRoles("user")
    @PostMapping("/addOrder")
    public Result addOrder(@RequestBody OrderAddDto orderAddDto, HttpServletRequest request){
        Result result = marketOrderService.addOrder(orderAddDto,request);
        return result;
    }

    @ApiOperation("订单支付")
    @RequiresRoles("user")
    @PostMapping("/payOrder/{orderId}")
    public Result payOrder(@PathVariable("orderId") Integer orderId){
        Result result = marketOrderService.payOrder(orderId);
        return result;
    }

}
