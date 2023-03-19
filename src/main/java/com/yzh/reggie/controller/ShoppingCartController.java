package com.yzh.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.yzh.reggie.common.BaseContext;
import com.yzh.reggie.common.R;
import com.yzh.reggie.entity.ShoppingCart;
import com.yzh.reggie.mapper.ShoppingCartMapper;
import com.yzh.reggie.service.ShoppingCartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @author alick
 * @since 2023/1/15
 */
@Slf4j
@RestController
@RequestMapping("/shoppingCart")
public class ShoppingCartController {

    @Resource
    private ShoppingCartService shoppingCartService;

    /**
     * 添加
     *
     * @param shoppingCart 购物车
     * @return {@link R}<{@link ShoppingCart}>
     */
    @PostMapping("/add")
    public R<ShoppingCart> add(@RequestBody ShoppingCart shoppingCart){
        log.info("购物车数据：{}",shoppingCart.toString());
        // 设置用户id，指定当前是哪一个用户的购物车数据
        Long currentId = BaseContext.getCurrentId();
        shoppingCart.setUserId(currentId);

        // 查询所选菜品或套餐是否在购物车中
        LambdaQueryWrapper<ShoppingCart> shoppingCartLambdaQueryWrapper = new LambdaQueryWrapper<>();
        shoppingCartLambdaQueryWrapper.eq(ShoppingCart::getUserId,currentId);

        if (shoppingCart.getDishId() != null){
            // 添加的是菜品
            shoppingCartLambdaQueryWrapper.eq(ShoppingCart::getDishId,shoppingCart.getDishId());
        }else {
            // 添加的是套餐
            shoppingCartLambdaQueryWrapper.eq(ShoppingCart::getSetmealId,shoppingCart.getSetmealId());
        }
        // SQL：select * from shopping_cart where user_id = ? and dish_id/setmeal_id = ?
        ShoppingCart shoppingCartOne = shoppingCartService.getOne(shoppingCartLambdaQueryWrapper);

        // 存在，number增加
        if (shoppingCartOne != null){
            shoppingCartOne.setNumber(shoppingCartOne.getNumber() + 1);
            shoppingCartService.updateById(shoppingCartOne);
        }else {
            shoppingCart.setNumber(1);
            shoppingCart.setCreateTime(LocalDateTime.now());
            shoppingCartService.save(shoppingCart);
            shoppingCartOne = shoppingCart;
        }
        return R.success(shoppingCartOne);
    }

    /**
     * 列表
     *
     * @return {@link R}<{@link List}<{@link ShoppingCart}>>
     */
    @GetMapping("/list")
    public R<List<ShoppingCart>> list(){
        log.info("查看购物车..");
        LambdaQueryWrapper<ShoppingCart> shoppingCartLambdaQueryWrapper = new LambdaQueryWrapper<>();
        shoppingCartLambdaQueryWrapper
                .eq(ShoppingCart::getUserId,BaseContext.getCurrentId())
                .orderByAsc(ShoppingCart::getCreateTime);

        List<ShoppingCart> lists = shoppingCartService.list(shoppingCartLambdaQueryWrapper);
        return R.success(lists);
    }

    @DeleteMapping("/clean")
    public R<String> clean(){
        LambdaQueryWrapper<ShoppingCart> shoppingCartLambdaQueryWrapper = new LambdaQueryWrapper<>();
        shoppingCartLambdaQueryWrapper
                .eq(ShoppingCart::getUserId,BaseContext.getCurrentId());
        shoppingCartService.remove(shoppingCartLambdaQueryWrapper);

        return R.success("购物车已清空！");
    }

    /**
     * 减少购物车
     *
     * @param shoppingCart 购物车
     * @return {@link R}<{@link ShoppingCart}>
     */
    @PostMapping("/sub")
    public R<ShoppingCart> sub(@RequestBody ShoppingCart shoppingCart){
        // 设置用户
        Long currentId = BaseContext.getCurrentId();
        shoppingCart.setUserId(currentId);

        // 基本查找条件
        // 根据当前用户查找购物车内容
        LambdaQueryWrapper<ShoppingCart> shoppingCartLambdaQueryWrapper = new LambdaQueryWrapper<>();
        shoppingCartLambdaQueryWrapper
                .eq(ShoppingCart::getUserId,currentId);
        Long dishId = shoppingCart.getDishId();

        // 判断菜品或套餐
        if (dishId!=null){
            shoppingCartLambdaQueryWrapper
                    .eq(ShoppingCart::getDishId,shoppingCart.getDishId());
        }else {
            shoppingCartLambdaQueryWrapper
                    .eq(ShoppingCart::getSetmealId,shoppingCart.getSetmealId());
        }
        ShoppingCart one = shoppingCartService.getOne(shoppingCartLambdaQueryWrapper);
        Integer number = one.getNumber();

        if (number != 0){
            one.setNumber(number - 1);
            shoppingCartService.updateById(one);
        }else {
            shoppingCartService.removeById(one);
        }
        return R.success(one);
    }
}