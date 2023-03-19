package com.alick.reggie.service.impl;

import com.alick.reggie.entity.User;
import com.alick.reggie.mapper.UserMapper;
import com.alick.reggie.service.UserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * @author alick
 * @since 2023/1/15
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
}
