package com.example.demo.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Order {
    //订单实体类
    private Integer id;
    private Integer userId;
    private Integer deliveryId;
    private String status;
    private Double totalPrice;
    private Date createTime;
    private Date updateTime;

    private String remark;
    private String address;
    private String contactPhone;  // 必须加

    private User user;
    private User deliveryUser;
    private List<OrderItem> items;
}
