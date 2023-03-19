package com.alick.reggie.mapper;

import com.alick.reggie.entity.Category;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 分类mapper
 *
 * @author alick
 * @since 2023/01/12
 */
@Mapper
public interface CategoryMapper extends BaseMapper<Category> {

}
