package cn.edu.jlu.merchant.entity;

import java.util.Date;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

@Data
public class Dish {
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;
    @JsonSerialize(using = ToStringSerializer.class)
    private Long merchantId;
    private String name;
    private Double price;
    private String description;
    private String image;
    private Integer status;    // 1: 上架, 0: 下架
    private Date createTime;
    private Date updateTime;
}
