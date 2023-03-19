package com.yzh.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yzh.reggie.common.CustomException;
import com.yzh.reggie.entity.Category;
import com.yzh.reggie.entity.Dish;
import com.yzh.reggie.entity.Employee;
import com.yzh.reggie.entity.Setmeal;
import com.yzh.reggie.mapper.CategoryMapper;
import com.yzh.reggie.mapper.EmployeeMapper;
import com.yzh.reggie.service.CategoryService;
import com.yzh.reggie.service.DishService;
import com.yzh.reggie.service.EmployeeService;
import com.yzh.reggie.service.SetmealService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * Employee接口
 *
 * @author alick
 * @since 2023/1/9
 */
@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {

    @Resource
    private DishService dishService;

    @Resource
    private SetmealService setmealService;

    /**
     * 根据id删除分类，删除前需要进行判断
     *
     * @param id id
     */
    @Override
    public void remove(Long id) {
        LambdaQueryWrapper<Dish> dishLambdaQueryWrapper = new LambdaQueryWrapper<>();
        LambdaQueryWrapper<Setmeal> setmealLambdaQueryWrapper = new LambdaQueryWrapper<>();
        // 添加查询条件
        dishLambdaQueryWrapper.eq(Dish::getCategoryId,id);
        setmealLambdaQueryWrapper.eq(Setmeal::getCategoryId,id);

        // 查询当前分类是否关联了菜品
        if ((dishService.count(dishLambdaQueryWrapper))> 0){
            throw new CustomException("当前分类下关联了菜品，不能删除");
        }

        // 查询当前分类是否关联了套餐
        if ((setmealService.count(setmealLambdaQueryWrapper)) > 0){
            throw new CustomException("当前分类下关联了套餐，不能删除");
        }

        super.removeById(id);

    }
}
