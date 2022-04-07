package com.mrxu.stucomplarear2.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mrxu.stucomplarear2.dto.RegisterDto;
import com.mrxu.stucomplarear2.dto.WallApplyDto;
import com.mrxu.stucomplarear2.dto.WallAuditDto;
import com.mrxu.stucomplarear2.dto.WallFindDto;
import com.mrxu.stucomplarear2.entity.User;
import com.mrxu.stucomplarear2.entity.Wall;
import com.mrxu.stucomplarear2.mapper.WallMapper;
import com.mrxu.stucomplarear2.service.WallService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author Mr.Xu
 * @since 2021-12-15
 */
@Service
public class WallServiceImpl extends ServiceImpl<WallMapper, Wall> implements WallService {

    @Autowired
    private WallMapper wallMapper;

    @Override
    public String apply(WallApplyDto wallApplyDto) {
        if(wallApplyDto.getUserId()==null){
            return "用户ID不能为空";
        }
        if(wallApplyDto.getWallContent()==null){
            return "内容不能为空";
        }
        Wall wall = new Wall();
        wall.setUserId(wallApplyDto.getUserId());
        wall.setWallContent(wallApplyDto.getWallContent());
        wall.setAuditState(0);
        wallMapper.insert(wall);
        return "申请成功";
    }

    @Override
    public String audit(WallAuditDto wallAuditDto) {
        if(wallAuditDto.getWallId()==null){
            return "墙ID不能为空";
        }
        if(wallAuditDto.getAdminId()==null){
            return "审核员ID不能为空";
        }
        if(wallAuditDto.getAuditState()!=1&&wallAuditDto.getAuditState()!=2){
            return "审核状态参数错误";
        }
        QueryWrapper<Wall> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("wall_id",wallAuditDto.getWallId());
        Wall findWall = wallMapper.selectOne(queryWrapper);
        if(findWall==null){
            return "该信息不存在";
        }
        Wall wall = new Wall();
        wall.setWallId(wallAuditDto.getWallId());
        wall.setAdminId(wallAuditDto.getAdminId());
        wall.setAuditState(wallAuditDto.getAuditState());
        if(wallAuditDto.getAuditState()==2){
            wall.setAuditFailedCause(wallAuditDto.getAuditFailedCause());
        }
        wallMapper.update(wall, queryWrapper);
        return "审核成功";
    }

    @Override
    public Map<String, Object> findWallList(Integer pageNum, Integer pageSize) {
        int first = pageNum == null ? 1 : pageNum;
        int second = pageSize == null ? 4 : pageSize;
        //当前页 页面大小
        IPage<Wall> page = new Page<Wall>(first,second);
        QueryWrapper<Wall> queryWrapper =new QueryWrapper<>();
        queryWrapper.eq("audit_state",1);   //已审的内容
        queryWrapper.orderByDesc("audit_time"); //根据审核时间降序排列
        IPage<Wall> wallIPage = wallMapper.selectPage(page,queryWrapper);
        Map<String, Object> map = new HashMap<>();
        map.put("current",wallIPage.getCurrent());//当前页
        map.put("total",wallIPage.getTotal());//总记录数
        map.put("pages",wallIPage.getPages());//总页数
        map.put("pageSize",wallIPage.getSize());//页面大小
        map.put("walls",wallIPage.getRecords());//数据
        return map;
    }

    @Override
    public Map<String, Object> findWall(WallFindDto wallFindDto) {
        int pageNum = wallFindDto.getPageNum() == null ? 1 : wallFindDto.getPageNum();
        int pageSize = wallFindDto.getPageSize() == null ? 4 : wallFindDto.getPageSize();
        QueryWrapper<Wall> queryWrapper =new QueryWrapper<>();
        if(wallFindDto.getWallId()!=null){
            queryWrapper.eq("wall_id",wallFindDto.getWallId());
        }
        if(wallFindDto.getAdminId()!=null){
            queryWrapper.eq("admin_id",wallFindDto.getAdminId());
        }
        if(wallFindDto.getUserId()!=null){
            queryWrapper.eq("user_id",wallFindDto.getUserId());
        }
        if(wallFindDto.getAuditState()!=null){
            queryWrapper.eq("audit_state",wallFindDto.getAuditState());
        }
        //当前页 页面大小
        IPage<Wall> page = new Page<Wall>(pageNum,pageSize);

        queryWrapper.orderByDesc("audit_time"); //根据审核时间降序排列

        IPage<Wall> wallIPage = wallMapper.selectPage(page,queryWrapper);
        Map<String, Object> map = new HashMap<>();
        map.put("current",wallIPage.getCurrent());//当前页
        map.put("total",wallIPage.getTotal());//总记录数
        map.put("pages",wallIPage.getPages());//总页数
        map.put("pageSize",wallIPage.getSize());//页面大小
        map.put("walls",wallIPage.getRecords());//数据
        return map;
    }
}
