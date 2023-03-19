package com.yzh.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yzh.reggie.entity.User;
import com.yzh.reggie.mapper.UserMapper;
import com.yzh.reggie.service.UserService;
import org.springframework.stereotype.Service;

/**
 * @author alick
 * @since 2023/1/15
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
}
