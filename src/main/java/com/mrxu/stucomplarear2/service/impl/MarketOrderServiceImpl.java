package com.mrxu.stucomplarear2.service.impl;

import com.mrxu.stucomplarear2.dto.MarketOrderVo;
import com.mrxu.stucomplarear2.dto.OrderAddDto;
import com.mrxu.stucomplarear2.entity.Goods;
import com.mrxu.stucomplarear2.entity.MarketOrder;
import com.mrxu.stucomplarear2.entity.User;
import com.mrxu.stucomplarear2.mapper.GoodsMapper;
import com.mrxu.stucomplarear2.mapper.MarketOrderMapper;
import com.mrxu.stucomplarear2.mapper.UserMapper;
import com.mrxu.stucomplarear2.service.MarketOrderService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mrxu.stucomplarear2.utils.jwt.JWTUtil;
import com.mrxu.stucomplarear2.utils.response.Result;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.concurrent.RecursiveTask;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author Mr.Xu
 * @since 2022-04-20
 */
@Service
public class MarketOrderServiceImpl extends ServiceImpl<MarketOrderMapper, MarketOrder> implements MarketOrderService {

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private GoodsMapper goodsMapper;
    @Autowired
    private MarketOrderMapper marketOrderMapper;

    @Override
    public Result addOrder(OrderAddDto orderAddDto, HttpServletRequest request) {
        MarketOrderVo marketOrderVo = new MarketOrderVo();
        try {
            String accessToken = request.getHeader("Authorization");
            //获取token里面的用户ID
            String userId = JWTUtil.getUserId(accessToken);
            if (orderAddDto.getGoodsId() == null || orderAddDto.getBuyCount() == null || userId == null) {
                return Result.fail("参数错误");
            }

            //检查商品是否存在
            Goods goods = goodsMapper.selectById(orderAddDto.getGoodsId());
            if (goods == null) {
                return Result.fail("商品不存在或已被下架");
            }
            if (goods.getGoodsCount() < 1) {
                return Result.fail("库存不足");
            }
            MarketOrder marketOrder = new MarketOrder();
            marketOrder.setGoodsId(orderAddDto.getGoodsId());
            marketOrder.setBuyerId(Integer.valueOf(userId));
            marketOrder.setSellerId(goods.getUserId());
            marketOrder.setBuyCount(orderAddDto.getBuyCount());
            marketOrder.setTotalPrice(goods.getGoodsPrice() * orderAddDto.getBuyCount());
            marketOrder.setOrderStatus(0);
            marketOrderMapper.insert(marketOrder);
//            System.out.println("id:"+marketOrder.getOrderId());
            //实体类中@TableName @TableId(value = "order_id", type = IdType.AUTO)
            //一一对应后，inster()之后，实例中可以获取到id

            MarketOrder marketOrder1 = marketOrderMapper.selectById(marketOrder.getOrderId());
            BeanUtils.copyProperties(marketOrder1, marketOrderVo);

            //查对应的用户信息
            User buyer = userMapper.selectById(marketOrder1.getBuyerId());
            User seller = userMapper.selectById(marketOrder1.getSellerId());

            marketOrderVo.setBuyer(buyer);
            marketOrderVo.setSeller(seller);
            marketOrderVo.setGoods(goods);
        } catch (Exception e) {
            e.printStackTrace();
            Result.fail(e.toString());
        }
        return Result.succ(marketOrderVo);
    }

    @Override
    public Result payOrder(Integer orderId) {
        try {
            if (orderId == null) {
                return Result.fail("参数错误");
            }
            MarketOrder marketOrder = marketOrderMapper.selectById(orderId);
            if (marketOrder == null) {
                return Result.fail("订单不存在");
            }
            Goods goods = goodsMapper.selectById(marketOrder.getGoodsId());
            if (goods.getGoodsCount() < 1) {
                return Result.fail("库存不足");
            }
            //库存减一
            goods.setGoodsCount(goods.getGoodsCount() - 1);
            goodsMapper.updateById(goods);

            //更改订单状态为“已支付”
            marketOrder.setOrderStatus(1);
            marketOrderMapper.updateById(marketOrder);
        } catch (Exception e) {
            return Result.fail(e.toString());
        }
        return Result.succ("支付成功");
    }
}
