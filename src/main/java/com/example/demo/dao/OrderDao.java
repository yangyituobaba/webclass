package com.example.demo.dao;

import com.example.demo.bean.Order;
import com.example.demo.bean.OrderItem;
import com.example.demo.bean.Product;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Map;

@Mapper
public interface OrderDao {


    @Insert("INSERT INTO orders(user_id, delivery_id, status, total_price, address, contact_phone, remark) " +
            "VALUES(#{userId}, #{deliveryId}, #{status}, #{totalPrice}, #{address}, #{contactPhone}, #{remark})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insertOrder(Order order);


    //通过order_id筛选订单，用在获取订单详细信息那里
    @Select("SELECT * FROM orders WHERE id = #{id}")
    @Results({
            @Result(property = "userId", column = "user_id"),
            @Result(property = "deliveryId", column = "delivery_id"),
            @Result(property = "status", column = "status"),
            @Result(property = "totalPrice", column = "total_price"),
            @Result(property = "createTime", column = "create_time"),
            @Result(property = "updateTime", column = "update_time"),
            @Result(property = "items", column = "id",
                    many = @Many(select = "com.example.demo.dao.OrderItemDao.selectItemsByOrderId"))
    })
    Order selectById(Integer id);

    @Select("SELECT o.id, o.user_id, o.delivery_id, o.status, o.total_price, o.create_time, o.update_time, " +
            "u.id as user_id, u.username as user_username, u.name as user_name, " +
            "d.id as delivery_id, d.username as delivery_username, d.name as delivery_name " +
            "FROM orders o " +
            "LEFT JOIN user u ON o.user_id = u.id " +
            "LEFT JOIN user d ON o.delivery_id = d.id " +
            "WHERE u.username = #{username}")
    @Results({
            @Result(property = "id", column = "id"),
            @Result(property = "userId", column = "user_id"),
            @Result(property = "deliveryId", column = "delivery_id"),
            @Result(property = "status", column = "status"),
            @Result(property = "totalPrice", column = "total_price"),
            @Result(property = "createTime", column = "create_time"),
            @Result(property = "updateTime", column = "update_time"),
            @Result(property = "user.id", column = "user_id"),
            @Result(property = "user.username", column = "user_username"),
            @Result(property = "user.name", column = "user_name"),
            @Result(property = "deliveryUser.id", column = "delivery_id"),
            @Result(property = "deliveryUser.username", column = "delivery_username"),
            @Result(property = "deliveryUser.name", column = "delivery_name"),
            // 订单项用另外的查询单独处理，不在这里查询
    })
    List<Order> selectListByUsername(@Param("username") String username);

    //获取全部订单
    @Select("SELECT * FROM orders ")
    @Results({
            @Result(property = "userId", column = "user_id"),
            @Result(property = "deliveryId", column = "delivery_id"),
            @Result(property = "status", column = "status"),
            @Result(property = "totalPrice", column = "total_price"),
            @Result(property = "createTime", column = "create_time"),
            @Result(property = "updateTime", column = "update_time"),
            @Result(property = "items", column = "id",
                    many = @Many(select = "com.example.demo.dao.OrderItemDao.selectItemsByOrderId"))
    })
    List<Order> ListAllOrders();

    //订单模糊查询
    @Select({
            "<script>",
            "SELECT * FROM orders",
            "WHERE id IN",
            "<foreach collection='ids' item='id' open='(' separator=',' close=')'>",
            "#{id}",
            "</foreach>",
            "</script>"
    })
    @Results({
            @Result(property = "userId", column = "user_id"),
            @Result(property = "deliveryId", column = "delivery_id"),
            @Result(property = "status", column = "status"),
            @Result(property = "totalPrice", column = "total_price"),
            @Result(property = "createTime", column = "create_time"),
            @Result(property = "updateTime", column = "update_time"),
            @Result(property = "items", column = "id",
                    many = @Many(select = "com.example.demo.dao.OrderItemDao.selectItemsByOrderId"))
    })
    List<Order> findOrdersByIds(@Param("ids") List<Integer> ids);


    //更新订单状态
    @Update("UPDATE orders SET status = #{status}, delivery_id = #{deliveryId} WHERE id = #{id}")
    int updateStatus(Order order);

    // 订单总数
    @Select("SELECT COUNT(*) FROM orders")
    Integer countTotalOrders();

    @Select("SELECT " +
            "SUM(CASE WHEN status = 'pending' THEN 1 ELSE 0 END) AS pending, " +
            "SUM(CASE WHEN status = 'delivering' THEN 1 ELSE 0 END) AS delivering, " +
            "SUM(CASE WHEN status = 'completed' THEN 1 ELSE 0 END) AS completed " +
            "FROM orders")
    Map<String, Object> countThreeOrderStatuses();


    // 已完成订单的总成交金额
    @Select("SELECT SUM(total_price) FROM orders WHERE status = 'completed'")
    Double sumCompletedOrderRevenue();

}
