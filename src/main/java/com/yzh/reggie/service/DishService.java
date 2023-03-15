package com.yzh.reggie.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.yzh.reggie.dto.DishDto;
import com.yzh.reggie.entity.Dish;

import java.util.List;

public interface DishService extends IService<Dish> {
    // 新增菜品，同时插入菜品对于的口味数据，需要操作两张表
    public void saveWithFlavor(DishDto dishDto);

    public DishDto getByIdWithFlavor(Long id);

    /**
     * 更新菜品和口味
     *
     * @param dishDto 菜dto
     */
    public void updateWithFlavor(DishDto dishDto);

    public void deleteWithFlavor(List<Long> ids);
}
