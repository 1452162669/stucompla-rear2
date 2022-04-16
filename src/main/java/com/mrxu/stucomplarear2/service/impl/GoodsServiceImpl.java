package com.mrxu.stucomplarear2.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mrxu.stucomplarear2.dto.GoodsAddDto;
import com.mrxu.stucomplarear2.dto.GoodsFindDto;
import com.mrxu.stucomplarear2.entity.Goods;
import com.mrxu.stucomplarear2.mapper.GoodsMapper;
import com.mrxu.stucomplarear2.service.GoodsService;
import com.mrxu.stucomplarear2.utils.jwt.JWTUtil;
import com.mrxu.stucomplarear2.utils.response.Result;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author Mr.Xu
 * @since 2022-04-15
 */
@Service
public class GoodsServiceImpl extends ServiceImpl<GoodsMapper, Goods> implements GoodsService {

    @Autowired
    private GoodsMapper goodsMapper;

    @Override
    public Result add(GoodsAddDto goodsDto, HttpServletRequest request) {
        // 要做非空判断
        try {
            Goods goods = new Goods();
            BeanUtils.copyProperties(goodsDto, goods);

            System.out.println(goodsDto);
            String accessToken = request.getHeader("Authorization");
            //获取token里面的用户ID
            String userId = JWTUtil.getUserId(accessToken);
            goods.setUserId(Integer.valueOf(userId));

            goodsMapper.insert(goods);
        } catch (Exception e) {
            e.printStackTrace();
            Result.fail(e.toString());
        }
        return Result.succ("发布成功");
    }

    @Override
    public Result findGoods(GoodsFindDto goodsFindDto) {
        int pageNum = goodsFindDto.getPageNum() == null ? 1 : goodsFindDto.getPageNum();
        int pageSize = goodsFindDto.getPageSize() == null ? 10 : goodsFindDto.getPageSize();
        QueryWrapper<Goods> queryWrapper = new QueryWrapper<>();
        if (goodsFindDto.getGoodsId() != null) {
            queryWrapper.eq("goods_id", goodsFindDto.getGoodsId());
        }
        if (goodsFindDto.getGoodsCategoryId() != null) {
            queryWrapper.eq("goods_category_id", goodsFindDto.getGoodsCategoryId());
        }
        if (goodsFindDto.getUserId() != null) {
            queryWrapper.eq("user_id", goodsFindDto.getUserId());
        }
        if (goodsFindDto.getKeyName() != null) {
            queryWrapper.like("goods_name", goodsFindDto.getKeyName());
            queryWrapper.like("goods_detail", goodsFindDto.getKeyName());
        }
        if (goodsFindDto.getGoodsStatus() != null) {
            queryWrapper.eq("goods_status", goodsFindDto.getGoodsStatus());
        }
        String sort = goodsFindDto.getSort();
        if ("+goods_id".equals(sort)) {
            queryWrapper.orderByAsc("goods_id");
        } else if ("-goods_id".equals(sort)) {
            queryWrapper.orderByDesc("goods_id");
        } else if ("+goods_price".equals(sort)) {
            queryWrapper.orderByAsc("goods_price");
        } else if ("-goods_price".equals(sort)) {
            queryWrapper.orderByDesc("goods_price");
        } else {
            queryWrapper.orderByDesc("create_time"); //默认发布时间降序
        }
        IPage<Goods> page = new Page<>(pageNum, pageSize);
        IPage<Goods> goodsIPage = goodsMapper.selectPage(page, queryWrapper);
        Map<String, Object> map = new HashMap<>();
        map.put("current", goodsIPage.getCurrent());//当前页
        map.put("total", goodsIPage.getTotal());//总记录数
        map.put("pages", goodsIPage.getPages());//总页数

        map.put("pageSize", goodsIPage.getSize());//页面大小
        map.put("goodsList", goodsIPage.getRecords());//数据

        return Result.succ(map);
    }

}
