package com.yzh.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.databind.util.BeanUtil;
import com.yzh.reggie.common.CustomException;
import com.yzh.reggie.common.R;
import com.yzh.reggie.dto.DishDto;
import com.yzh.reggie.dto.SetmealDto;
import com.yzh.reggie.entity.Category;
import com.yzh.reggie.entity.Dish;
import com.yzh.reggie.entity.Setmeal;
import com.yzh.reggie.entity.SetmealDish;
import com.yzh.reggie.service.CategoryService;
import com.yzh.reggie.service.DishService;
import com.yzh.reggie.service.SetmealDishService;
import com.yzh.reggie.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.websocket.server.PathParam;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author alick
 * @since 2023/1/14
 */
@RestController
@Slf4j
@RequestMapping("/setmeal")
public class SetmealController {
    @Resource
    private SetmealService setmealService;

    @Resource
    private DishService dishService;

    @Resource
    private CategoryService categoryService;

    @Resource
    private SetmealDishService setmealDishService;

    @Resource
    private RedisTemplate redisTemplate;

    /**
     * 新增套餐
     * @param setmealDto
     * @return
     */
    @PostMapping()
    @CacheEvict(value = "setmealCache",allEntries = true)
    public R<String> save(@RequestBody SetmealDto setmealDto ){
        log.info("套餐信息：{}",setmealDto);
        setmealService.saveDish(setmealDto);
        //deleteRedisCache(setmealDto);
        return R.success("套餐新增成功！");
    }

    /**
     * 后台  套餐分页查询
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(int page,int pageSize,String name){
        // 分页
        Page<Setmeal> pageInfo = new Page<Setmeal>(page,pageSize);
        Page<SetmealDto> DtoPage = new Page<>();

        LambdaQueryWrapper<Setmeal> setmealLambdaQueryWrapper = new LambdaQueryWrapper<>();

        setmealLambdaQueryWrapper
                //.eq(Setmeal::getStatus,1)// 只查启售
                .like(name != null,Setmeal::getName,name)
                .orderByDesc(Setmeal::getUpdateTime);

        setmealService.page(pageInfo,setmealLambdaQueryWrapper);

        // 对象拷贝
        BeanUtils.copyProperties(pageInfo,DtoPage,"records");
        List<Setmeal> records = pageInfo.getRecords();

        List<SetmealDto> list =
                records.stream().map((item) -> {
                    SetmealDto setmealDto = new SetmealDto();

                    BeanUtils.copyProperties(item,setmealDto);

                    Long categoryId = item.getCategoryId();
                    Category category = categoryService.getById(categoryId);
                    if (category!=null){
                        setmealDto.setCategoryName(category.getName());
                    }
                    return setmealDto;
                }).collect(Collectors.toList());
        DtoPage.setRecords(list);
        return R.success(DtoPage);
    }

    @DeleteMapping()
    @CacheEvict(value = "setmealCache",allEntries = true)
    public R<String> delete(@RequestParam List<Long> ids){
        setmealService.removeWithDish(ids);
        return R.success("套餐删除成功！");
    }

    /**
     * 前台 根据条件查询套餐数据
     * @param setmeal
     * @return
     */
    @GetMapping("/list")
    @Cacheable(value = "setmealCache",key = "#setmeal.categoryId + '_' + #setmeal.status")
    public R<List<Setmeal>> list(Setmeal setmeal){
        //region redis缓存
        /*List<Setmeal> SetmealList = null;
        String key = "dish_" + setmeal.getCategoryId() + "_" + setmeal.getStatus();

        // 先从redis中获取缓存数据
        // key根据分类id,商品状态来确定
        SetmealList = (List<Setmeal>) redisTemplate.opsForValue().get(key);// 动态构造
        if (SetmealList != null){
            // 如果存在，直接返回，无需查询数据库
            return R.success(SetmealList);
        }*/
        //endregion

        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper
                .eq(setmeal.getCategoryId() != null,Setmeal::getCategoryId,setmeal.getCategoryId())
                .eq(setmeal.getStatus() != null,Setmeal::getStatus,setmeal.getStatus())
                .orderByDesc(Setmeal::getUpdateTime);

        List<Setmeal> SetmealList = setmealService.list(queryWrapper);

        //redisTemplate.opsForValue().set(key,SetmealList,60, TimeUnit.MINUTES);

        return R.success(SetmealList);
    }

    /**
     * 修改页面展示信息
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<SetmealDto> upadte(@PathVariable Long id){
        SetmealDto setmealByDish = setmealService.getSetmealByDish(id);
        return R.success(setmealByDish);
    }

    /**
     * 保存更新
     *
     * @param setmealDto setmeal dto
     * @return {@link R}<{@link String}>
     */
    @PutMapping()
    public R<String> saveUpdate(@RequestBody SetmealDto setmealDto){
        setmealService.updateWithDish(setmealDto);
        //deleteRedisCache(setmealDto);
        return R.success("菜品修改成功！");
    }

    /**
     * 商品停售
     * @param ids
     * @return
     */
    @PostMapping("/status/0")
    public R<String> statusStop(@RequestParam List<Long> ids){
        // 根据输入的ids，进行停售
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper();
        queryWrapper
                .in(Setmeal::getId,ids)
                .eq(Setmeal::getStatus,1);

        int count = setmealService.count(queryWrapper);
        if(count > 0) {
            for (Long id : ids) {
                Setmeal setmeal = setmealService.getById(id);
                setmeal.setStatus(0);
                setmealService.updateById(setmeal);
            }
        }
        return R.success("套餐已经停售！");
    }

    /**
     * 商品启售
     *
     * @param ids id
     * @return {@link R}<{@link String}>
     */
    @PostMapping("/status/1")
    public R<String> statusStart(@RequestParam List<Long> ids){
        // 根据输入的ids，进行停售
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper();
        queryWrapper
                .in(Setmeal::getId,ids)
                .eq(Setmeal::getStatus,0);

        int count = setmealService.count(queryWrapper);
        if(count > 0) {
            for (Long id : ids) {
                Setmeal setmeal = setmealService.getById(id);
                setmeal.setStatus(1);
                setmealService.updateById(setmeal);
            }
        }
        return R.success("套餐已经启售！");
    }

    /**
     * 点击套餐图片查看套餐具体内容
     * 前端主要要展示的信息是:套餐中菜品的基本信息，图片，菜品描述，以及菜品的份数
     * @param id
     * @return
     */
    // todo 该页面未解决，无法展示
    @GetMapping("/dish/{id}")
    public R<List<DishDto>> showSetmealDish(@PathVariable Long id) {
        //条件构造器
        LambdaQueryWrapper<SetmealDish> dishLambdaQueryWrapper = new LambdaQueryWrapper<>();
        //手里的数据只有setmealId
        dishLambdaQueryWrapper.eq(SetmealDish::getSetmealId, id);
        //查询数据
        List<SetmealDish> records = setmealDishService.list(dishLambdaQueryWrapper);
        List<DishDto> dtoList = records.stream().map((item) -> {
            DishDto dishDto = new DishDto();
            //copy数据
            BeanUtils.copyProperties(item,dishDto);
            //查询对应菜品id
            Long dishId = item.getDishId();
            //根据菜品id获取具体菜品数据，这里要自动装配 dishService
            Dish dish = dishService.getById(dishId);
            //其实主要数据是要那个图片，不过我们这里多copy一点也没事
            BeanUtils.copyProperties(dish,dishDto);
            return dishDto;
        }).collect(Collectors.toList());
        return R.success(dtoList);
    }

    /**
     * 删除redis缓存
     *
     * @param dishDto 菜dto
     */
    private void deleteRedisCache(SetmealDto setmealDto) {
        // 清理所有菜品的缓存数据
        // Set keys = redisTemplate.keys("dish_*");
        // redisTemplate.delete(keys);

        // 精确的清理,清理某个分类下面的数据
        String key = "dish_" + setmealDto.getCategoryId() + "_" + setmealDto.getStatus();
        redisTemplate.delete(key);
    }
}
