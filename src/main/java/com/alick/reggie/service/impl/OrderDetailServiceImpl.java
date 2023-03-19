package com.alick.reggie.service.impl;

import com.alick.reggie.entity.OrderDetail;
import com.alick.reggie.mapper.OrderDetailMapper;
import com.alick.reggie.service.OrderDetailService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import org.springframework.stereotype.Service;

@Service
public class OrderDetailServiceImpl extends ServiceImpl<OrderDetailMapper, OrderDetail> implements OrderDetailService {

}