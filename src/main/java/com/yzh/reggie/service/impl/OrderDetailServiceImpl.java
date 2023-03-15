package com.yzh.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.yzh.reggie.entity.OrderDetail;
import com.yzh.reggie.mapper.OrderDetailMapper;
import com.yzh.reggie.service.OrderDetailService;
import org.springframework.stereotype.Service;

@Service
public class OrderDetailServiceImpl extends ServiceImpl<OrderDetailMapper, OrderDetail> implements OrderDetailService {

}