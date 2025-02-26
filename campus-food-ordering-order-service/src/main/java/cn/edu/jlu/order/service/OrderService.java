package cn.edu.jlu.order.service;

import cn.edu.jlu.order.entity.Order;
import cn.edu.jlu.order.entity.OrderItem;
import cn.edu.jlu.order.mapper.OrderMapper;
import cn.edu.jlu.order.mapper.OrderItemMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import java.util.List;

@Service
public class OrderService {

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private OrderItemMapper orderItemMapper;

    /**
     * 创建订单（用户下单）
     */
    @Transactional
    public String createOrder(Order order) {
        List<OrderItem> items = order.getOrderItems();
        if (items == null || items.isEmpty()) {
            return "订单不能为空";
        }
        double total = 0;
        for (OrderItem item : items) {
            total += item.getPrice() * item.getQuantity();
        }
        order.setTotalAmount(total);
        // 设置初始状态为 0 待处理
        order.setStatus(0);
        int result = orderMapper.insert(order);
        if (result <= 0) {
            return "订单创建失败";
        }
        // 插入订单项
        for (OrderItem item : items) {
            item.setOrderId(order.getId());
            orderItemMapper.insert(item);
        }
        return "订单创建成功";
    }

    /**
     * 商家更新订单状态（例如接单、制作中、已完成等）
     */
    public String updateOrderStatus(Long orderId, Integer status) {
        Order order = orderMapper.selectById(orderId);
        if (order == null) {
            return "订单不存在";
        }
        order.setStatus(status);
        int result = orderMapper.updateById(order);
        return result > 0 ? "订单状态更新成功" : "订单状态更新失败";
    }

    /**
     * 用户查看订单历史
     */
    public List<Order> getOrdersByUserId(Long userId) {
        QueryWrapper<Order> query = new QueryWrapper<>();
        query.eq("user_id", userId);
        return orderMapper.selectList(query);
    }

    /**
     * 商家查看订单历史
     */
    public List<Order> getOrdersByMerchantId(Long merchantId) {
        QueryWrapper<Order> query = new QueryWrapper<>();
        query.eq("merchant_id", merchantId);
        return orderMapper.selectList(query);
    }

    public List<OrderItem> getOrderItemsByOrderId(Long orderId) {
        return orderItemMapper.selectByOrderId(orderId);
    }
}
