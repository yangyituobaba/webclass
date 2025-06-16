package com.example.demo.dao;

import com.example.demo.bean.OrderItem;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface OrderItemDao {


    //插入订单列表
    @Insert("INSERT INTO order_item(order_id, product_id, quantity, price) " +
            "VALUES(#{orderId}, #{productId}, #{quantity}, #{price})")
    void insertOrderItem(OrderItem item);

    @Select("SELECT oi.id, oi.order_id, oi.product_id, oi.quantity, oi.price, p.name as productName " +
            "FROM order_item oi " +
            "JOIN product p ON oi.product_id = p.id " +
            "WHERE oi.order_id = #{orderId}")
    @Results({
            @Result(property = "id", column = "id"),
            @Result(property = "orderId", column = "order_id"),
            @Result(property = "productId", column = "product_id"),
            @Result(property = "quantity", column = "quantity"),
            @Result(property = "price", column = "price"),
            @Result(property = "productName", column = "productName")
    })
    List<OrderItem> selectItemsByOrderId(@Param("orderId") Integer orderId);


    @Select("SELECT DISTINCT oi.order_id " +
            "FROM order_item oi " +
            "JOIN product p ON oi.product_id = p.id " +
            "WHERE p.name LIKE CONCAT('%', #{name}, '%')")
    List<Integer> findOrderIdsByItemName(@Param("name") String name);


}
