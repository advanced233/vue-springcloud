package cn.edu.jlu.order.entity;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;

@Data
@TableName("orders")  // 指定数据库表名为 orders
public class Order {
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;
    @JsonSerialize(using = ToStringSerializer.class)
    private Long userId;
    @JsonSerialize(using = ToStringSerializer.class)
    private Long merchantId;
    private Double totalAmount;
    private Integer status; // 0: 待处理, 1: 已接单, 2: 制作中, 3: 已完成, 4: 已取消
    private Date createTime;
    private Date updateTime;

    @TableField(exist = false)
    private List<OrderItem> orderItems;

    private String comment;
}
