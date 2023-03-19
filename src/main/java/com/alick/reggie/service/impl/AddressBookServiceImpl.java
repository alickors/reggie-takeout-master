package com.alick.reggie.service.impl;

import com.alick.reggie.entity.AddressBook;
import com.alick.reggie.mapper.AddressBookMapper;
import com.alick.reggie.service.AddressBookService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * @author alick
 * @since 2023/1/15
 */
@Service
public class AddressBookServiceImpl extends ServiceImpl<AddressBookMapper, AddressBook> implements AddressBookService {

}
