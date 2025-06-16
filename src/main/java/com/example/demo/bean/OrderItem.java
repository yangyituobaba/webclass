package com.example.demo.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderItem {
    //订单项实体类
    private Integer id;
    private Integer orderId;    //所属订单Id，对应数据库order_id
    private Integer productId;      //商品id,对应数据库product_id
    private Integer quantity;       //商品量
    private Double price;   //商品价格


    // 这个字段数据库没有，前端显示用
    private String productName;
}
