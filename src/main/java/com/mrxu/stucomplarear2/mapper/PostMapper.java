package com.mrxu.stucomplarear2.mapper;

import com.mrxu.stucomplarear2.dto.PostPublishDto;
import com.mrxu.stucomplarear2.entity.Post;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mrxu.stucomplarear2.utils.response.Result;
import org.springframework.stereotype.Component;

import javax.servlet.ServletRequest;
import java.awt.image.RescaleOp;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author Mr.Xu
 * @since 2021-12-27
 */
@Component
public interface PostMapper extends BaseMapper<Post> {

}
