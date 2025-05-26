package com.example.demo.controller;


import com.example.demo.bean.Order;
import com.example.demo.bean.Result;
import com.example.demo.service.OrderService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private OrderService orderService;

    //创建订单
    @PostMapping("/create")
    public Result create(@RequestBody Order order) {
        orderService.createOrder(order);
        return Result.success("订单创建成功");
    }
    // 订单详情
    @GetMapping("/detail")
    public Result getOrderDetail(@RequestParam Integer id) {
        Order order = orderService.getOrderDetail(id);
        return Result.success(order);
    }

    // 订单列表（根据角色过滤）
    @GetMapping("/list")
    public Result getOrderList(@RequestParam Integer userId, @RequestParam String role) {
        List<Order> orders = orderService.getOrdersByUser(userId, role);
        return Result.success(orders);
    }

    // 客户更新订单状态（示例：取消订单）
    @PostMapping("/update/client")
    public Result updateStatusByClient(@RequestParam Integer orderId, @RequestParam String status) {
        boolean success = orderService.updateOrderStatus(orderId, status, null);
        return success ? Result.success("订单状态更新成功") : Result.fail("订单状态更新失败");
    }

    // 配送员更新订单状态（示例：已取货，配送中）
    @PostMapping("/update/delivery")
    public Result updateStatusByDelivery(@RequestParam Integer orderId, @RequestParam String status, @RequestParam Integer deliveryId) {
        boolean success = orderService.updateOrderStatus(orderId, status, deliveryId);
        return success ? Result.success("订单状态更新成功") : Result.fail("订单状态更新失败");
    }

    // 管理员更新订单状态（示例：后台操作）
    @PostMapping("/update/admin")
    public Result updateStatusByAdmin(@RequestParam Integer orderId, @RequestParam String status, @RequestParam Integer deliveryId) {
        boolean success = orderService.updateOrderStatus(orderId, status, deliveryId);
        return success ? Result.success("订单状态更新成功") : Result.fail("订单状态更新失败");
    }
}
