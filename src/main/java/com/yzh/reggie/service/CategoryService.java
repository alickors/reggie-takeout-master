package com.yzh.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yzh.reggie.entity.Category;
import com.yzh.reggie.entity.Employee;

/**
 * @author alick
 * @since 2023/1/12
 */
public interface CategoryService extends IService<Category> {
        public void remove(Long id);
}
