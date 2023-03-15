package com.yzh.reggie.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.databind.util.BeanUtil;
import com.yzh.reggie.common.CustomException;
import com.yzh.reggie.dto.DishDto;
import com.yzh.reggie.entity.Dish;
import com.yzh.reggie.entity.DishFlavor;
import com.yzh.reggie.mapper.DishMapper;
import com.yzh.reggie.service.DishFlavorService;
import com.yzh.reggie.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {

    @Resource
    private DishFlavorService dishFlavorService;
    /**
     * 新增菜品，保存口味
     * @param dishDto
     */
    @Override
    @Transactional// 多张表操作，需要事务控制，启动类也要加注解
    public void saveWithFlavor(DishDto dishDto) {
        this.save(dishDto);

        // dishId
        Long dishDtoId = dishDto.getId();

        List<DishFlavor> flavors = dishDto.getFlavors();
        flavors = flavors.stream().map((item) -> {
            item.setDishId(dishDtoId);
            return item;
        }).collect(Collectors.toList());

        dishFlavorService.saveBatch(flavors);
    }

    /**
     * 通过id修改信息和口味
     *
     * @param id id
     * @return {@link DishDto}
     */
    @Override
    public DishDto getByIdWithFlavor(Long id) {
        // 查询菜品基本信息
        Dish dish = this.getById(id);

        DishDto dishDto = new DishDto();
        BeanUtils.copyProperties(dish,dishDto);

        // 查询口味
        LambdaQueryWrapper<DishFlavor> dishFlavorLambdaQueryWrapper = new LambdaQueryWrapper<>();
        dishFlavorLambdaQueryWrapper
                .eq(DishFlavor::getDishId,id);

        List<DishFlavor> flavors = dishFlavorService.list(dishFlavorLambdaQueryWrapper);
        dishDto.setFlavors(flavors);

        return dishDto;
    }

    @Override
    @Transactional
    public void updateWithFlavor(DishDto dishDto) {
        // 更新菜品表
        this.updateById(dishDto);

        // 更新口味表
        LambdaQueryWrapper<DishFlavor> dishFlavorLambdaQueryWrapper = new LambdaQueryWrapper<>();

        // 先删除
        dishFlavorService.remove(
                dishFlavorLambdaQueryWrapper
                        .eq(DishFlavor::getDishId,dishDto.getId()
                        )
        );

        // 再封装
        List<DishFlavor> flavors = dishDto.getFlavors();
        flavors = flavors.stream().map((item) -> {
            item.setDishId(dishDto.getId());
            return item;
        }).collect(Collectors.toList());

        // 保存
        dishFlavorService.saveBatch(flavors);
    }

    /**
     * 删除菜品及其口味
     *
     * @param ids id
     */
    @Override
    @Transactional
    public void deleteWithFlavor(List<Long> ids) {
        // 先查询是否存在启售
        LambdaQueryWrapper<Dish> dishLambdaQueryWrapper = new LambdaQueryWrapper<>();
        dishLambdaQueryWrapper
                .in(Dish::getId,ids)
                .eq(Dish::getStatus,1);
        if (this.count(dishLambdaQueryWrapper) > 0){
            throw new CustomException("该菜品正在售卖中，不可删除！");
        }
        // 已停售
        this.removeByIds(ids);

        // 在dish_flavor中
        LambdaQueryWrapper<DishFlavor> dishFlavorLambdaQueryWrapper = new LambdaQueryWrapper<>();
        dishFlavorLambdaQueryWrapper.in(DishFlavor::getDishId,ids);

        dishFlavorService.remove(dishFlavorLambdaQueryWrapper);
    }
}
