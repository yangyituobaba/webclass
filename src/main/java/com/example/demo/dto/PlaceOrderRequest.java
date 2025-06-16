package com.example.demo.dto;

import lombok.Data;

import java.util.List;
@Data
public class PlaceOrderRequest {
    private Integer user_id;
    private List<OrderItemDTO> items;
}
