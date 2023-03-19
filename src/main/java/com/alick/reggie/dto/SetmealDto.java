package com.alick.reggie.dto;

import com.alick.reggie.entity.Setmeal;
import com.alick.reggie.entity.SetmealDish;
import lombok.Data;
import java.util.List;

@Data
public class SetmealDto extends Setmeal {

    private List<SetmealDish> setmealDishes;

    private String categoryName;

    private String dishName;

    //图片
    private String image;


    //描述信息
    private String description;

}
