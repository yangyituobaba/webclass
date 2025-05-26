package com.example.demo.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderItem {
    private Integer id;
    private Integer orderId;    //所属订单Id
    private Integer productId;      //商品id
    private Integer quantity;
    private Double price;

    private Product product;
}
