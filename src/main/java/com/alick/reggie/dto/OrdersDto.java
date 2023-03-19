package com.alick.reggie.dto;

import com.alick.reggie.entity.OrderDetail;
import com.alick.reggie.entity.Orders;
import lombok.Data;

import java.util.List;

/**
 * @author alick
 * @since 2023/1/16
 */
@Data
public class OrdersDto extends Orders {
    private List<OrderDetail> orderDetails;

    private int sumNum;
}
