package com.yzh.reggie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yzh.reggie.entity.Employee;
import org.apache.ibatis.annotations.Mapper;

/**
 * 员工mapper
 *
 * @author 杨振华
 * @since 2023/1/9
 */
@Mapper
public interface EmployeeMapper extends BaseMapper<Employee> {

}
