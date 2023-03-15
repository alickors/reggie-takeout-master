package com.yzh.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yzh.reggie.entity.AddressBook;
import com.yzh.reggie.mapper.AddressBookMapper;
import com.yzh.reggie.service.AddressBookService;
import org.springframework.stereotype.Service;

/**
 * @author alick
 * @since 2023/1/15
 */
@Service
public class AddressBookServiceImpl extends ServiceImpl<AddressBookMapper, AddressBook> implements AddressBookService {

}
