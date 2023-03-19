package com.alick.reggie.mapper;

import com.alick.reggie.entity.User;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author alick
 * @since 2023/1/15
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {
}
