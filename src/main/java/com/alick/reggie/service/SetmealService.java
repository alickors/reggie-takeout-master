package com.alick.reggie.service;

import com.alick.reggie.entity.Setmeal;
import com.baomidou.mybatisplus.extension.service.IService;
import com.alick.reggie.dto.SetmealDto;

import java.util.List;


/**
 * setmeal服务
 *
 * @author alick
 * @date 2023/01/13
 */
public interface SetmealService extends IService<Setmeal> {
    public void saveDish(SetmealDto setmealDto);

    public void removeWithDish(List<Long> ids);

    public void updateWithDish(SetmealDto setmealDto);

    public SetmealDto getSetmealByDish(Long id);
}
