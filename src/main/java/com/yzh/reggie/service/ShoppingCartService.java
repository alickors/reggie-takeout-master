package com.yzh.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yzh.reggie.common.R;
import com.yzh.reggie.entity.ShoppingCart;

public interface ShoppingCartService extends IService<ShoppingCart> {

    public R<String> clean();
}
