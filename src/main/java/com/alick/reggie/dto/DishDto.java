package com.alick.reggie.dto;


import com.alick.reggie.entity.Dish;
import com.alick.reggie.entity.DishFlavor;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 菜品数据传输
 *
 * @author alick
 * @date 2023/01/13
 */
@Data
public class DishDto extends Dish {

    private List<DishFlavor> flavors = new ArrayList<>();

    private String categoryName;

    private Integer copies;

}
