package com.mrxu.stucomplarear2.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.mrxu.stucomplarear2.dto.GoodsAddDto;
import com.mrxu.stucomplarear2.dto.GoodsFindDto;
import com.mrxu.stucomplarear2.entity.Goods;
import com.mrxu.stucomplarear2.utils.response.Result;

import javax.servlet.http.HttpServletRequest;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author Mr.Xu
 * @since 2022-04-15
 */
public interface GoodsService extends IService<Goods> {

    Result add(GoodsAddDto goodsDto, HttpServletRequest request);

    Result findGoods(GoodsFindDto goodsFindDto);
}
