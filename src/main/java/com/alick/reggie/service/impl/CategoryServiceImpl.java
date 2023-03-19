package com.alick.reggie.service.impl;

import com.alick.reggie.entity.Category;
import com.alick.reggie.entity.Dish;
import com.alick.reggie.entity.Setmeal;
import com.alick.reggie.mapper.CategoryMapper;
import com.alick.reggie.service.CategoryService;
import com.alick.reggie.service.DishService;
import com.alick.reggie.service.SetmealService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.alick.reggie.common.CustomException;
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
