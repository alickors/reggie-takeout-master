package com.yzh.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sun.org.apache.xpath.internal.operations.Or;
import com.yzh.reggie.common.BaseContext;
import com.yzh.reggie.common.R;
import com.yzh.reggie.dto.OrdersDto;
import com.yzh.reggie.entity.OrderDetail;
import com.yzh.reggie.entity.Orders;
import com.yzh.reggie.entity.ShoppingCart;
import com.yzh.reggie.service.OrderDetailService;
import com.yzh.reggie.service.OrdersService;
import com.yzh.reggie.service.ShoppingCartService;
import com.yzh.reggie.service.UserService;
import com.yzh.reggie.service.impl.OrderDetailServiceImpl;
import com.yzh.reggie.service.impl.ShoppingCartServiceImpl;
import com.yzh.reggie.service.impl.UserServiceImpl;
import lombok.extern.slf4j.Slf4j;
import net.sf.jsqlparser.expression.DateTimeLiteralExpression;
import org.springframework.beans.BeanUtils;
import org.springframework.core.annotation.Order;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author 杨振华
 * @since 2023/1/15
 */
@Slf4j
@RestController
@RequestMapping("/order")
public class OrderController {

    @Resource
    private OrdersService ordersService;

    @Resource
    private OrderDetailService orderDetailService;

    @Resource
    private UserService userService;

    @Resource
    private ShoppingCartService shoppingCartService;

    /**
     * 提交
     *
     * @param orders 订单
     * @return {@link R}<{@link String}>
     */
    @PostMapping("/submit")
    public R<String> submit(@RequestBody Orders orders){
        ordersService.submit(orders);
        return R.success("下单成功！");
    }

    /**
     * 页面显示
     * @param page
     * @param pageSize
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(int page, int pageSize , Long number, String beginTime,String endTime){
        //页面构造器
        Page<Orders> pageInfo = new Page<>(page, pageSize);

        //查询所有orders表信息
        LambdaQueryWrapper<Orders> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper
                .orderByDesc(Orders::getOrderTime);

        if (number != null){
            queryWrapper.like(Orders::getNumber, number);
        }
        if (beginTime != null && endTime != null){
            queryWrapper.between(Orders::getOrderTime,beginTime,endTime);
        };

        ordersService.page(pageInfo, queryWrapper);

        return R.success(pageInfo);
    }
    //region 订单查询
    /*
    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, String number, String beginTime,String endTime){

        log.info("开始时间和结束时间为：{}",beginTime+endTime);

        Page<Orders> ordersPage = new Page<>(page,pageSize);
        Page<OrdersDto> ordersDtoPage = new Page<>();

        BeanUtils.copyProperties(ordersPage,ordersDtoPage,"records");

        LambdaQueryWrapper<Orders> queryWrapper = new LambdaQueryWrapper<>();

        queryWrapper.eq(number != null,Orders::getNumber,number);
        queryWrapper.between(beginTime != null && endTime != null,Orders::getOrderTime,beginTime,endTime);

        //订单列表
        List<Orders> ordersList = ordersService.list(queryWrapper);

        List<OrdersDto> ordersDtoList = ordersList.stream().map(orders -> {

            OrdersDto ordersDto = new OrdersDto();

            BeanUtils.copyProperties(orders, ordersDto);

            Long userId = orders.getUserId();

            User user = userService.getById(userId);

            ordersDto.setUserName(user.getName());

            return ordersDto;
        }).collect(Collectors.toList());

        ordersDtoPage.setRecords(ordersDtoList);

        return R.success(ordersDtoPage);
    }

     */
    //endregion

    @PutMapping
    public R<Orders> update(@RequestBody Orders orders){
        LambdaQueryWrapper<Orders> ordersLambdaQueryWrapper = new LambdaQueryWrapper<>();
        ordersLambdaQueryWrapper
                .eq(Orders::getId,orders.getId());

        ordersService.updateById(orders);
        return R.success(orders);
    }

    /**
     * 查看订单
     * @param page
     * @param pageSize
     * @return
     */
    @GetMapping("/userPage")
    public R<Page> page(int page,int pageSize){
        // 分页构造器
        Page<Orders> pageInfo = new Page<>(page,pageSize);
        Page<OrdersDto> ordersDtoPage = new Page<>();

        // 用户ID
        Long currentId = BaseContext.getCurrentId();

        // 原条件写入
        LambdaQueryWrapper<Orders> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Orders::getUserId,currentId);
        queryWrapper.orderByDesc(Orders::getOrderTime);

        ordersService.page(pageInfo,queryWrapper);

        // 普通赋值
        BeanUtils.copyProperties(pageInfo,ordersDtoPage,"records");

        // 订单赋值
        List<Orders> records = pageInfo.getRecords();

        List<OrdersDto> ordersDtoList = records.stream().map((item) -> {

            // 新创内部元素
            OrdersDto ordersDto = new OrdersDto();

            // 普通值赋值
            BeanUtils.copyProperties(item,ordersDto);

            // 菜单详情赋值
            Long itemId = item.getId();

            LambdaQueryWrapper<OrderDetail> orderDetailLambdaQueryWrapper = new LambdaQueryWrapper<>();
            orderDetailLambdaQueryWrapper.eq(OrderDetail::getOrderId,itemId);

            int count = orderDetailService.count(orderDetailLambdaQueryWrapper);

            List<OrderDetail> orderDetailList = orderDetailService.list(orderDetailLambdaQueryWrapper);

            ordersDto.setSumNum(count);

            ordersDto.setOrderDetails(orderDetailList);

            return ordersDto;
        }).collect(Collectors.toList());

        // 完成dishDtoPage的results的内容封装
        ordersDtoPage.setRecords(ordersDtoList);

        return R.success(ordersDtoPage);
    }
//客户端点击再来一单
    /**
     * 我们需要将订单内的菜品重新加入购物车，所以在此之前我们需要将购物车清空（业务层实现方法）
     *
     *
     */
    @PostMapping("/again")
    public R<String> againSubmit(@RequestBody Map<String,String> map){
        // 获得ID
        String ids = map.get("id");

        long id = Long.parseLong(ids);

        // 制作判断条件
        LambdaQueryWrapper<OrderDetail> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(OrderDetail::getOrderId,id);

        //获取该订单对应的所有的订单明细表
        List<OrderDetail> orderDetailList = orderDetailService.list(queryWrapper);

        //通过用户id把原来的购物车给清空
        shoppingCartService.clean();

        //获取用户id
        Long userId = BaseContext.getCurrentId();

        // 整体赋值
        List<ShoppingCart> shoppingCartList = orderDetailList.stream().map((item) -> {

            // 以下均为赋值操作

            ShoppingCart shoppingCart = new ShoppingCart();
            shoppingCart.setUserId(userId);
            shoppingCart.setImage(item.getImage());

            Long dishId = item.getDishId();
            Long setmealId = item.getSetmealId();

            if (dishId != null) {
                // 如果是菜品那就添加菜品的查询条件
                shoppingCart.setDishId(dishId);
            } else {
                // 添加到购物车的是套餐
                shoppingCart.setSetmealId(setmealId);
            }
            shoppingCart.setName(item.getName());
            shoppingCart.setDishFlavor(item.getDishFlavor());
            shoppingCart.setNumber(item.getNumber());
            shoppingCart.setAmount(item.getAmount());
            shoppingCart.setCreateTime(LocalDateTime.now());
            return shoppingCart;
        }).collect(Collectors.toList());

        // 将携带数据的购物车批量插入购物车表
        shoppingCartService.saveBatch(shoppingCartList);

        return R.success("操作成功");
    }
}
