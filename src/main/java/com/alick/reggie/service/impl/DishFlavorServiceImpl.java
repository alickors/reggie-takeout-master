package com.alick.reggie.service.impl;

import com.alick.reggie.entity.DishFlavor;
import com.alick.reggie.mapper.DishFlavorMapper;
import com.alick.reggie.service.DishFlavorService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * @author alick
 * @since 2023/1/13
 */
@Service
public class DishFlavorServiceImpl extends ServiceImpl<DishFlavorMapper, DishFlavor> implements DishFlavorService {
}
