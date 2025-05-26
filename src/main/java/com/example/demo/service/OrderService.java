package com.example.demo.service;

import com.example.demo.bean.Order;
import com.example.demo.bean.OrderItem;
import com.example.demo.dao.OrderDao;
import com.example.demo.dao.OrderItemDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
public class OrderService {
    @Autowired
    private OrderDao orderDao;
    
    @Autowired
    private OrderItemDao orderItemDao;


    //创建订单
    @Transactional
    public void createOrder(Order order) {
        double total = 0.0;
        for(OrderItem item : order.getItems()) {
            total += item.getPrice()*item.getQuantity();
        }
        order.setTotalPrice(total);
        order.setStatus("waitingfororder");
        orderDao.insertOrder(order);

        for(OrderItem item : order.getItems()) {
            item.setOrderId(order.getId());
            orderItemDao.insertOrderItem(item);
        }
    }

    //获取订单信息
    public Order getOrderDetail(Integer id) {
        return orderDao.selectById(id);
    }

    //获取对应用户的订单
    public List<Order> getOrdersByUser(Integer userId, String role) {
        return orderDao.selectList(userId, role);
    }


    //更新订单状态
    public boolean updateOrderStatus(Integer orderId, String status, Integer deliveryId) {
        Order order = new Order();
        order.setId(orderId);
        order.setStatus(status);
        order.setDeliveryId(deliveryId);
        return orderDao.updateStatus(order) > 0;
    }

}
