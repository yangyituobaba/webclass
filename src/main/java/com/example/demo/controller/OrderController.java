package com.example.demo.controller;


import com.example.demo.annotation.AccessVerification;
import com.example.demo.annotation.VerificationLevel;
import com.example.demo.bean.Order;
import com.example.demo.bean.Result;
import com.example.demo.bean.User;
import com.example.demo.service.OrderService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.List;


@RestController
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private OrderService orderService;


    @Autowired
    private StringRedisTemplate redisTemplate;

    private static final String REDIS_ORDER_CONFIRM_PREFIX = "order:confirm:";

    // ✅ 1️⃣ 缓存订单确认信息（前端勾选的订单项 + 表单）
    @AccessVerification(VerificationLevel.PRIMARY)
    @PostMapping("/confirm/cache")
    public Result cacheOrderConfirm(@RequestBody Order confirmOrder, HttpServletRequest request) {
        User currentUser = (User) request.getAttribute("currentUser");
        if (currentUser == null) {
            return Result.fail("用户未登录");
        }

        try {
            String key = REDIS_ORDER_CONFIRM_PREFIX + currentUser.getUsername();
            ObjectMapper mapper = new ObjectMapper();
            String json = mapper.writeValueAsString(confirmOrder);
            redisTemplate.opsForValue().set(key, json);
            return Result.success("确认信息已缓存");
        } catch (Exception e) {
            return Result.fail("缓存失败：" + e.getMessage());
        }
    }

    // ✅ 2️⃣ 获取 Redis 中缓存的确认订单信息
    @AccessVerification(VerificationLevel.PRIMARY)
    @GetMapping("/confirm/get")
    public Result getOrderConfirm(HttpServletRequest request) {
        User currentUser = (User) request.getAttribute("currentUser");
        if (currentUser == null) {
            return Result.fail("用户未登录");
        }

        String key = REDIS_ORDER_CONFIRM_PREFIX + currentUser.getUsername();
        String json = redisTemplate.opsForValue().get(key);
        if (json == null) {
            return Result.fail("没有缓存的订单确认信息");
        }

        try {
            ObjectMapper mapper = new ObjectMapper();
            Order order = mapper.readValue(json, Order.class);
            return Result.success(order);
        } catch (Exception e) {
            return Result.fail("读取失败：" + e.getMessage());
        }
    }

    // ✅ 3️⃣ 创建订单（从缓存中读取订单信息再创建）
    @AccessVerification(VerificationLevel.PRIMARY)
    @PostMapping("/create")
    public Result createOrder(HttpServletRequest request) {
        User currentUser = (User) request.getAttribute("currentUser");
        System.out.println("currentUser = " + currentUser);
        if (currentUser == null) {
            return Result.fail("用户未登录");
        }

        String key = REDIS_ORDER_CONFIRM_PREFIX + currentUser.getUsername();
        String json = redisTemplate.opsForValue().get(key);

        if (json == null) {
            return Result.fail("未找到订单确认信息，请先确认订单");
        }

        try {
            ObjectMapper mapper = new ObjectMapper();
            Order order = mapper.readValue(json, Order.class);
            orderService.createOrder(order, currentUser.getUsername());
            redisTemplate.delete(key); // 删除缓存
            return Result.success("订单创建成功");
        } catch (Exception e) {
            return Result.fail("订单创建失败：" + e.getMessage());
        }
    }
//    //创建订单(需要初级认证）    测试通过
//    @AccessVerification(VerificationLevel.PRIMARY)
//    @PostMapping("/create")
//    public Result createOrder(@RequestBody Order order,HttpServletRequest request) {
//        // 从请求属性中获取当前用户
//        User currentUser = (User) request.getAttribute("currentUser");
//        if (currentUser == null) {
//            return Result.fail("用户未登录");
//        }
//
//        try {
//            // 使用当前登录用户的用户名创建订单
//            orderService.createOrder(order, currentUser.getUsername());
//            return Result.success("订单创建成功");
//        } catch (Exception e) {
//            return Result.fail("创建失败: " + e.getMessage());
//        }
//    }


    // 订单详情（需要初级认证）   测试通过
    @AccessVerification(VerificationLevel.PRIMARY)
    @PostMapping("/detail")
    public Result getOrderDetail(@RequestBody Map<String, Object> params,HttpServletRequest request) {
        // 从请求属性中获取当前用户,这一步只是为了确认用户是否登录emmmm
        User currentUser = (User) request.getAttribute("currentUser");
        if (currentUser == null) {
            return Result.fail("用户未登录");
        }
        Integer id = (Integer) params.get("id");
        // 根据订单ID和当前用户名获取订单详情
        Order order = orderService.getOrderDetail(id);

        if (order == null) {
            return Result.fail("订单不存在或无权访问");
        }
        return Result.success(order);
    }


    @PostMapping("/list")
    @AccessVerification(VerificationLevel.PRIMARY)
    public Result getOrderList( HttpServletRequest request) {
        // 从请求属性中取当前用户
        User currentUser;
        currentUser = (User) request.getAttribute("currentUser");
        if (currentUser == null) {
            return Result.fail("用户未登录");
        }
        String username = currentUser.getUsername();

        List<Order> orders = orderService.getOrdersByUsername(username);
        return Result.success(orders);
    }

    //模糊查询
    @AccessVerification(VerificationLevel.NONE)
    @GetMapping("/search")
    public Result searchOrdersByItem(@RequestParam("keyword") String keyword) {
        List<Order> orders = orderService.searchOrdersByItemName(keyword);
        return Result.success(orders);
    }

    // 客户更新订单状态（示例：取消订单）
    @AccessVerification(VerificationLevel.PRIMARY)
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



    // 获取所有订单（仅限管理员）
    @AccessVerification(VerificationLevel.PRIMARY)
    @GetMapping("/all")
    public Result listAllOrders() {
        List<Order> orders = orderService.listAllOrders();
        return Result.success(orders);
    }



    // 管理员统计信息接口
    @AccessVerification(VerificationLevel.NONE)
    @GetMapping("/admin/statistics")
    public Result getAdminStatistics() {
        try {
            Map<String, Object> stats = orderService.getAdminStatistics();
            return Result.success(stats);
        } catch (Exception e) {
            return Result.fail("获取统计信息失败：" + e.getMessage());
        }
    }

}
