package com.mrxu.stucomplarear2.service;

import com.mrxu.stucomplarear2.dto.MarketOrderFindDto;
import com.mrxu.stucomplarear2.dto.OrderAddDto;
import com.mrxu.stucomplarear2.entity.MarketOrder;
import com.baomidou.mybatisplus.extension.service.IService;
import com.mrxu.stucomplarear2.utils.response.Result;

import javax.servlet.http.HttpServletRequest;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author Mr.Xu
 * @since 2022-04-20
 */
public interface MarketOrderService extends IService<MarketOrder> {

    Result addOrder(OrderAddDto orderAddDto, HttpServletRequest request);

    Result payOrder(Integer orderId);

    Result getMyOrder(MarketOrderFindDto marketOrderFindDto, HttpServletRequest request);

    Result deleteOrder(Integer orderId, HttpServletRequest request);

    Result receipt(Integer orderId, HttpServletRequest request);

    Result getMySalesOrders(MarketOrderFindDto marketOrderFindDto, HttpServletRequest request);

    Result sendGoods(Integer orderId, HttpServletRequest request);

    Result getOrderTotal();
}
