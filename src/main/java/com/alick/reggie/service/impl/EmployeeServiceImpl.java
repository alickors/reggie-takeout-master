package com.alick.reggie.service.impl;

import com.alick.reggie.entity.Employee;
import com.alick.reggie.mapper.EmployeeMapper;
import com.alick.reggie.service.EmployeeService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * Employee接口
 *
 * @author alick
 * @since 2023/1/9
 */
@Service
public class EmployeeServiceImpl extends ServiceImpl<EmployeeMapper, Employee> implements EmployeeService {

}
