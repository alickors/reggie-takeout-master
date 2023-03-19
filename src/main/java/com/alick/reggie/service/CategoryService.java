package com.alick.reggie.service;

import com.alick.reggie.entity.Category;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * @author alick
 * @since 2023/1/12
 */
public interface CategoryService extends IService<Category> {
        public void remove(Long id);
}
