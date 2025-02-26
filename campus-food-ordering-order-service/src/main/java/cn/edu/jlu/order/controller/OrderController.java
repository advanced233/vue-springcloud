package cn.edu.jlu.order.controller;

import cn.edu.jlu.order.entity.Order;
import cn.edu.jlu.order.entity.OrderItem;
import cn.edu.jlu.order.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/order")
@CrossOrigin(origins = "*") // 允许所有域名跨域访问
public class OrderController {

    @Autowired
    private OrderService orderService;

    /**
     * 用户下单接口：POST /order/create
     * 请求体中包含 userId、merchantId 及订单项列表（orderItems）
     */
    @PostMapping("/create")
    public String createOrder(@RequestBody Order order) {
        return orderService.createOrder(order);
    }

    /**
     * 商家更新订单状态接口：PUT /order/{orderId}/status
     * 例如：将订单状态由 0 更新为 1（已接单）
     */
    @PutMapping("/{orderId}/status")
    public String updateOrderStatus(@PathVariable("orderId") Long orderId,
                                    @RequestParam Integer status) {
        return orderService.updateOrderStatus(orderId, status);
    }

    /**
     * 用户查看订单历史：GET /order/user/{userId}
     */
    @GetMapping("/user/{userId}")
    public List<Order> getOrdersByUser(@PathVariable("userId") Long userId) {
        return orderService.getOrdersByUserId(userId);
    }

    /**
     * 商家查看订单历史：GET /order/merchant/{merchantId}
     */
    @GetMapping("/merchant/{merchantId}")
    public List<Order> getOrdersByMerchant(@PathVariable("merchantId") Long merchantId) {
        return orderService.getOrdersByMerchantId(merchantId);
    }

    @GetMapping("/{orderId}/items")
    public List<OrderItem> getOrderItems(@PathVariable("orderId") Long orderId) {
        return orderService.getOrderItemsByOrderId(orderId);
    }
}
