package com.alick.reggie.service.impl;

import com.alick.reggie.common.BaseContext;
import com.alick.reggie.common.R;
import com.alick.reggie.entity.ShoppingCart;
import com.alick.reggie.mapper.ShoppingCartMapper;
import com.alick.reggie.service.ShoppingCartService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class ShoppingCartServiceImpl extends ServiceImpl<ShoppingCartMapper, ShoppingCart> implements ShoppingCartService {
    public R<String> clean(){

        // 进行用户比对
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId, BaseContext.getCurrentId());

        // 删除即可
        this.remove(queryWrapper);

        return R.success("清空成功");

    }
}
