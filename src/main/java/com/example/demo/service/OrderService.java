package com.example.demo.service;

import com.example.demo.bean.Order;
import com.example.demo.bean.OrderItem;
import com.example.demo.dao.OrderDao;
import com.example.demo.dao.OrderItemDao;
import com.example.demo.dao.ProductDao;
import com.example.demo.dao.UserDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service
public class OrderService {

    @Autowired
    private ProductDao productDao;

    @Autowired
    private OrderDao orderDao;
    
    @Autowired
    private OrderItemDao orderItemDao;

    @Autowired
    private UserDao userDao;


    //创建订单
    @Transactional
    public void createOrder(Order order,String username) {

        // 1. 查找用户 ID
        Integer userId = userDao.selectIdByUsername(username);
        if (userId == null) {
            throw new RuntimeException("用户名不存在：" + username);
        }
        order.setUserId(userId);

        // 2. 计算总价，并减少库存
        double total = 0.0;
        for (OrderItem item : order.getItems()) {
            total += item.getPrice() * item.getQuantity();
            // 减少库存
            productDao.reduceStock(item.getProductId(), item.getQuantity());
        }
        order.setTotalPrice(total);
        order.setStatus("pending");


        // 3. 插入订单
        orderDao.insertOrder(order);

        // 4. 插入订单项
        for (OrderItem item : order.getItems()) {
            item.setOrderId(order.getId());
            orderItemDao.insertOrderItem(item);
        }
    }

    //获取订单信息
    public Order getOrderDetail(Integer id) {
        return orderDao.selectById(id);
    }

    // 通过用户名和角色获取订单列表
    public List<Order> getOrdersByUsername(String username) {
        List<Order> orders = orderDao.selectListByUsername(username);
        for (Order order : orders) {
            List<OrderItem> items = orderItemDao.selectItemsByOrderId(order.getId());
            order.setItems(items);
        }
        return orders;
    }

    //更新订单状态
    public boolean updateOrderStatus(Integer orderId, String status, Integer deliveryId) {
        Order order = new Order();
        order.setId(orderId);
        order.setStatus(status);
        order.setDeliveryId(deliveryId);
        return orderDao.updateStatus(order) > 0;
    }

    public List<Order> searchOrdersByItemName(String itemName) {
        List<Integer> orderIds = orderItemDao.findOrderIdsByItemName(itemName);
        if (orderIds == null || orderIds.isEmpty()) {
            return Collections.emptyList();
        }

        List<Order> orders = orderDao.findOrdersByIds(orderIds);

        for (Order order : orders) {
            List<OrderItem> items = orderItemDao.selectItemsByOrderId(order.getId());
            order.setItems(items);
        }

        return orders;
    }


    public List<Order> listAllOrders() {
        return orderDao.ListAllOrders();
    }

    public Map<String, Object> getAdminStatistics() {
        Map<String, Object> stats = new HashMap<>();
        // 订单总数
        Integer totalOrders = orderDao.countTotalOrders();
        stats.put("totalOrders", totalOrders);

        // 三种状态统计：pending, delivering, completed
        Map<String, Object> statusCountsRaw = orderDao.countThreeOrderStatuses();
        Map<String, Integer> statusCounts = new HashMap<>();
        for (Map.Entry<String, Object> entry : statusCountsRaw.entrySet()) {
            statusCounts.put(entry.getKey(), ((Number) entry.getValue()).intValue());
        }
        stats.put("statusCounts", statusCounts);

        // 总成交金额（仅 completed）
        Double totalRevenue = orderDao.sumCompletedOrderRevenue();
        stats.put("totalRevenue", totalRevenue != null ? totalRevenue : 0.0);

        return stats;
    }




}
