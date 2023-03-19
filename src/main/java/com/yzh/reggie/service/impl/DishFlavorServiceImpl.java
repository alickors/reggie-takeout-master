package com.yzh.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yzh.reggie.entity.DishFlavor;
import com.yzh.reggie.mapper.DishFlavorMapper;
import com.yzh.reggie.service.DishFlavorService;
import com.yzh.reggie.service.DishService;
import org.springframework.stereotype.Service;

/**
 * @author alick
 * @since 2023/1/13
 */
@Service
public class DishFlavorServiceImpl extends ServiceImpl<DishFlavorMapper, DishFlavor> implements DishFlavorService {
}
