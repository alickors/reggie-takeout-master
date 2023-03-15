package com.yzh.reggie.dto;

import com.yzh.reggie.entity.OrderDetail;
import com.yzh.reggie.entity.Orders;
import lombok.Data;

import java.util.List;

/**
 * @author 杨振华
 * @since 2023/1/16
 */
@Data
public class OrdersDto extends Orders {
    private List<OrderDetail> orderDetails;

    private int sumNum;
}
