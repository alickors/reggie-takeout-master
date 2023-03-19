package com.yzh.reggie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yzh.reggie.entity.Category;
import com.yzh.reggie.entity.Employee;
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
