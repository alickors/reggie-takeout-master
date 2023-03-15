package com.yzh.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yzh.reggie.common.R;
import com.yzh.reggie.entity.Category;
import com.yzh.reggie.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Select;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author 杨振华
 * @since 2023/1/12
 */
@RestController
@Slf4j
@RequestMapping("/category")
public class CategoryController {

    @Resource
    private CategoryService categoryService;

    @PostMapping()
    public R<String> save(@RequestBody Category category){
        categoryService.save(category);
        return R.success("新增分类成功");
    }

    @GetMapping("/page")
    public R<Page> page(int page,int pageSize){
        Page<Category> pageInfo = new Page<>(page,pageSize);
        LambdaQueryWrapper<Category> categoryLambdaQueryWrapper = new LambdaQueryWrapper<>();
        categoryLambdaQueryWrapper.orderByAsc(Category::getSort);

        categoryService.page(pageInfo,categoryLambdaQueryWrapper);
        return R.success(pageInfo);

    }

    /**
     * 根据id删除分类
     *
     * @param id id
     * @return {@link R}<{@link String}>
     */
    @DeleteMapping()
    public R<String> delete(Long id){
        log.info("删除分类，id为：{}",id);
        categoryService.remove(id);
        return R.success("分类信息删除成功！");
    }

    /**
     * 更新
     *
     * @param category 类别
     * @return {@link R}<{@link String}>
     */
    @PutMapping()
    public R<String> update(@RequestBody Category category){
        log.info("修改分类信息：{}",category);
        categoryService.updateById(category);
        return R.success("分类信息修改成功");
    }

    /**
     * 根据条件查询分类数据
     *
     * @param category 类别
     * @return {@link R}<{@link Category}>
     */
    @GetMapping("/list")
    public R<List<Category>> list(Category category){
        LambdaQueryWrapper<Category> categoryLambdaQueryWrapper = new LambdaQueryWrapper<>();

        List<Category> list = categoryService.list(categoryLambdaQueryWrapper
                .eq(category.getType() != null, Category::getType, category.getType())
                .orderByAsc(Category::getSort)
                .orderByDesc(Category::getUpdateTime));

        return R.success(list);
    }
}
