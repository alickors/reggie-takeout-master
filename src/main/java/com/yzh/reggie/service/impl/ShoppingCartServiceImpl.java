package com.yzh.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yzh.reggie.common.BaseContext;
import com.yzh.reggie.common.R;
import com.yzh.reggie.entity.ShoppingCart;
import com.yzh.reggie.mapper.ShoppingCartMapper;
import com.yzh.reggie.service.ShoppingCartService;
import org.springframework.stereotype.Service;

import javax.xml.transform.Result;

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
