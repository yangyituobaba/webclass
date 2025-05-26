package com.example.demo.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Order {
    private Integer id;
    private Integer userId;     //下单用户id
    private Integer deliveryId;     //配送员id
    private String status;      //订单状态
    private Double totalPrice;      //订单总价
    private Data createTime;
    private Data updateTime;

    private User user;
    private User deliveryUser;
    private List<OrderItem> items;
}
