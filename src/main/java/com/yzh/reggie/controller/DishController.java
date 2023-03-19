package com.yzh.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.databind.util.BeanUtil;
import com.yzh.reggie.common.R;
import com.yzh.reggie.dto.DishDto;
import com.yzh.reggie.entity.Category;
import com.yzh.reggie.entity.Dish;
import com.yzh.reggie.entity.DishFlavor;
import com.yzh.reggie.entity.Setmeal;
import com.yzh.reggie.service.CategoryService;
import com.yzh.reggie.service.DishFlavorService;
import com.yzh.reggie.service.DishService;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 菜品控制器
 *
 * @author alick
 * @since 2023/1/13
 */

//region redis实现菜品缓存
/**
 * 前面我们已经实现了移动端菜品查看功能，
 * 对应的服务端方法为DishController的list方法，
 * 此方法会根据前端提交的查询条件进行数据库查询操作。
 * 在高并发的情况下，频繁查询数据库会导致系统性能下降，服务端响应时间增长。
 * 现在需要对此方法进行缓存优化，提高系统的性能。具体的实现思路如下:
 * 1、政造DishController的list方法，先从Redis中获取菜品数据，如果有则直接返回，
 * 无需查询数据库;如果没有则查询数据库，并将查询到的菜品数据放入Redis。
 * 2、改造DishController的save和update方法，加入清理缓存的逻辑
 * 注意事项
 * 在使用缓存过程中，要注意保证数据库中的数据和缓存中的数据一致，
 * 如果数据库中的数据发生变化，需要及时清理缓存数据。
 */
//endregion
@RestController
@Slf4j
@RequestMapping("/dish")
public class DishController {

    @Resource
    private DishService dishService;
    @Resource
    private DishFlavorService dishFlavorService;
    @Resource
    private CategoryService categoryService;

    @Resource
    private RedisTemplate redisTemplate;

    /**
     *  新增菜品
     * @param dishDto
     * @return
     */
    @PostMapping()
    public R<String> save(@RequestBody DishDto dishDto){
        log.info(dishDto.toString());
        dishService.saveWithFlavor(dishDto);
        deleteRedisCache(dishDto);
        return R.success("新增菜品成功！");
    }

    /**
     * 分页查询
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(int page,int pageSize,String name){
        // 分页构造器
        Page<Dish> pageInfo = new Page<>(page,pageSize);
        // 这里因为需要有categoryName这一属性，
        // 所以需要引入DishDto，所以需要进行copy操作
        Page<DishDto> dishDtoPage = new Page<>(page,pageSize);


        // 条件构造器
        LambdaQueryWrapper<Dish> dishLambdaQueryWrapper = new LambdaQueryWrapper<>();
        dishLambdaQueryWrapper
                .like(name != null,Dish::getName,name)
                .orderByDesc(Dish::getUpdateTime);


        dishService.page(pageInfo,dishLambdaQueryWrapper);
        // 对象拷贝
        BeanUtils.copyProperties(pageInfo,dishDtoPage,"records");
        List<Dish> records = pageInfo.getRecords();

        List<DishDto> list = records.stream().map((item) -> {
            DishDto dishDto = new DishDto();
            // 拷贝忽略records
            BeanUtils.copyProperties(item,dishDto);

            // 得到分类id
            Long categoryId = item.getCategoryId();
            // 得到分类名称
            Category category = categoryService.getById(categoryId);
            if (category != null){
                dishDto.setCategoryName(category.getName());
            }

            return dishDto;
        }).collect(Collectors.toList());

        dishDtoPage.setRecords(list);

        return R.success(dishDtoPage);
    }

    /**
     * 根据id修改菜品信息和口味信息
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<DishDto> update(@PathVariable Long id){
        DishDto dishDto = dishService.getByIdWithFlavor(id);

        deleteRedisCache(dishDto);

        return R.success(dishDto);
    }


    /**
     * 保存更新
     *
     * @param dishDto 菜dto
     * @return {@link R}<{@link String}>
     */
    @PutMapping()
    public R<String> saveUpdate(@RequestBody DishDto dishDto){
        dishService.updateWithFlavor(dishDto);

        return R.success("菜品修改成功！");
    }

    /**
     * 根据条件查询菜品数据
     * @param dish
     * @return
     */
    /*@GetMapping("/list")
    public R<List<Dish>> list(Dish dish){
        LambdaQueryWrapper<Dish> dishLambdaQueryWrapper = new LambdaQueryWrapper<>();

        dishLambdaQueryWrapper
                .eq(dish.getCategoryId()!=null,Dish::getCategoryId,dish.getCategoryId())
                .eq(Dish::getStatus,1)// 只查启售
                .orderByAsc(Dish::getSort)
                .orderByDesc(Dish::getUpdateTime);

        List<Dish> list = dishService.list(dishLambdaQueryWrapper);

        return R.success(list);

    }*/
    @GetMapping("/list")
    public R<List<DishDto>> list(Dish dish){
        List<DishDto> dishDtoList = null;
        String key = "dish_" + dish.getCategoryId() + "_" + dish.getStatus();

        // 先从redis中获取缓存数据
        // key根据分类id,商品状态来确定
        dishDtoList = (List<DishDto>) redisTemplate.opsForValue().get(key);// 动态构造
        if (dishDtoList != null){
            // 如果存在，直接返回，无需查询数据库
            return R.success(dishDtoList);
        }
        // 如果不存在，需要查询数据库，将查询到的菜品数据缓存到redis
        LambdaQueryWrapper<Dish> dishLambdaQueryWrapper = new LambdaQueryWrapper<>();

        dishLambdaQueryWrapper
                .eq(dish.getCategoryId()!=null,Dish::getCategoryId,dish.getCategoryId())
                .eq(Dish::getStatus,1)// 只查启售
                .orderByAsc(Dish::getSort)
                .orderByDesc(Dish::getUpdateTime);

        List<Dish> list = dishService.list(dishLambdaQueryWrapper);

        dishDtoList = list.stream().map((item) -> {
            DishDto dishDto = new DishDto();
            // 拷贝忽略records
            BeanUtils.copyProperties(item,dishDto);

            // 得到分类id
            Long categoryId = item.getCategoryId();
            // 得到分类名称
            Category category = categoryService.getById(categoryId);
            if (category != null){
                dishDto.setCategoryName(category.getName());
            }
            // 菜品id
            Long itemId = item.getId();
            LambdaQueryWrapper<DishFlavor> dishFlavorLambdaQueryWrapper = new LambdaQueryWrapper<>();
            dishFlavorLambdaQueryWrapper.eq(DishFlavor::getDishId,itemId);
            // SQL：select * from dish_flavor where dish_id = ?

            dishDto.setFlavors(dishFlavorService.list(dishFlavorLambdaQueryWrapper));

            return dishDto;
        }).collect(Collectors.toList());

        // 缓存到redis
        redisTemplate.opsForValue().set(key,dishDtoList,60, TimeUnit.MINUTES);

        return R.success(dishDtoList);

    }

    @DeleteMapping()
    public R<String> delete(@RequestParam List<Long> ids){
        dishService.deleteWithFlavor(ids);
        return R.success("菜品删除成功！");
    }


    /**
     * 菜品停售
     * @param ids
     * @return
     */
    @PostMapping("/status/0")
    public R<String> statusStop(@RequestParam List<Long> ids){
        // 根据输入的ids，进行停售
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper();
        queryWrapper
                .in(Dish::getId,ids)
                .eq(Dish::getStatus,1);

        int count = dishService.count(queryWrapper);
        if(count > 0) {
            for (Long id : ids) {
                Dish dish = dishService.getById(id);
                dish.setStatus(0);
                dishService.updateById(dish);
            }
        }
        return R.success("菜品已经停售！");
    }

    /**
     * 菜品启售
     *
     * @param ids id
     * @return {@link R}<{@link String}>
     */
    @PostMapping("/status/1")
    public R<String> statusStart(@RequestParam List<Long> ids){
        // 根据输入的ids，进行停售
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper();
        queryWrapper
                .in(Dish::getId,ids)
                .eq(Dish::getStatus,0);

        int count = dishService.count(queryWrapper);
        if(count > 0) {
            for (Long id : ids) {
                Dish dish = dishService.getById(id);
                dish.setStatus(1);
                dishService.updateById(dish);
            }
        }
        return R.success("菜品已经启售！");
    }

    /**
     * 删除redis缓存
     *
     * @param dishDto 菜dto
     */
    private void deleteRedisCache(DishDto dishDto) {
        // 清理所有菜品的缓存数据
        // Set keys = redisTemplate.keys("dish_*");
        // redisTemplate.delete(keys);

        // 精确的清理,清理某个分类下面的数据
        String key = "dish_" + dishDto.getCategoryId() + "_" + dishDto.getStatus();
        redisTemplate.delete(key);
    }
}
