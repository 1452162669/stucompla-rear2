package com.mrxu.stucomplarear2.service;

import com.mrxu.stucomplarear2.dto.WallApplyDto;
import com.mrxu.stucomplarear2.dto.WallAuditDto;
import com.mrxu.stucomplarear2.dto.WallFindDto;
import com.mrxu.stucomplarear2.entity.Wall;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Map;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author Mr.Xu
 * @since 2021-12-15
 */
public interface WallService extends IService<Wall> {

    String apply(WallApplyDto wallDto);

    String audit(WallAuditDto wallAuditDto);

    Map<String, Object> findWallList(Integer pageNum, Integer pageSize);

    Map<String, Object> findWall(WallFindDto wallFindDto);
}
