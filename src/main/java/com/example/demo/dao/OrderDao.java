package com.example.demo.dao;

import com.example.demo.bean.Order;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface OrderDao {
    @Insert("INSERT INTO orders(user_id, delivery_id, status, total_price) VALUES(#{userId}, #{deliveryId}, #{status}, #{totalPrice})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insertOrder(Order order);

    @Select("SELECT * FROM orders WHERE id = #{id}")
    @Results({
            @Result(property = "items", column = "id",
                    many = @Many(select = "com.example.demo.dao.OrderItemMapper.selectByOrderId"))
    })
    Order selectById(Integer id);

    @Select("<script>" +
            "SELECT * FROM orders WHERE 1=1 " +
            "<if test='role == \"admin\"'> </if>" +
            "<if test='role == \"client\"'> AND user_id = #{userId} </if>" +
            "<if test='role == \"delivery\"'> AND delivery_id = #{userId} </if>" +
            "</script>")
    List<Order> selectList(@Param("userId") Integer userId, @Param("role") String role);

    @Update("UPDATE orders SET status = #{status}, delivery_id = #{deliveryId} WHERE id = #{id}")
    int updateStatus(Order order);
}
