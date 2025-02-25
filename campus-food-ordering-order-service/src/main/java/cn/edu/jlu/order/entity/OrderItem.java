package cn.edu.jlu.order.entity;

import java.util.Date;
import lombok.Data;

@Data
public class OrderItem {
    private Long id;
    private Long orderId;
    private Long dishId;
    private Integer quantity;
    private Double price;
    private Date createTime;
    private Date updateTime;
}
