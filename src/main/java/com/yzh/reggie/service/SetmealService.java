package com.yzh.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yzh.reggie.dto.SetmealDto;
import com.yzh.reggie.entity.Setmeal;

import java.util.List;


/**
 * setmeal服务
 *
 * @author 杨振华
 * @date 2023/01/13
 */
public interface SetmealService extends IService<Setmeal> {
    public void saveDish(SetmealDto setmealDto);

    public void removeWithDish(List<Long> ids);

    public void updateWithDish(SetmealDto setmealDto);

    public SetmealDto getSetmealByDish(Long id);
}
