package com.alick.reggie.service;

import com.alick.reggie.common.R;
import com.alick.reggie.entity.ShoppingCart;
import com.baomidou.mybatisplus.extension.service.IService;

public interface ShoppingCartService extends IService<ShoppingCart> {

    public R<String> clean();
}
