package com.example.demo.dao;

import com.example.demo.bean.OrderItem;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface OrderItemDao {

    @Insert("INSERT INTO order_item(order_id, product_id, quantity, price) " +
            "VALUES(#{orderId}, #{productId}, #{quantity}, #{price})")
    void insertOrderItem(OrderItem item);

    @Select("SELECT * FROM order_item WHERE order_id = #{orderId}")
    List<OrderItem> findByOrderId(Integer orderId);
}
