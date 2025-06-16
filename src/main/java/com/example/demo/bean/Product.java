package com.example.demo.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Product {
    //产品实体类
    private Integer id;
    private String name;
    private Double price;
    private String description;
    private String image;   //商品图片地址，对应image_url
    private Integer amount;  //商品余量
}
