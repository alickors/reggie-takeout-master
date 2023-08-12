package com.alick.reggie.mapper;

import com.alick.reggie.entity.Employee;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 员工mapper
 *
 * @author alick
 * @since 2023/1/9
 */
@Mapper
public interface EmployeeMapper extends BaseMapper<Employee> {


}
