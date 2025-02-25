package cn.edu.jlu.merchant.entity;

import java.util.Date;
import lombok.Data;

@Data
public class Dish {
    private Long id;
    private Long merchantId;
    private String name;
    private Double price;
    private String description;
    private String image;
    private Integer status;    // 1: 上架, 0: 下架
    private Date createTime;
    private Date updateTime;
}
