package cn.edu.jlu.merchant.entity;

import java.util.Date;
import lombok.Data;

@Data
public class Merchant {
    private Long id;
    private String account;    // 商家账号
    private String password;   // 登录密码
    private String name;       // 店铺名称
    private String logo;       // 店铺Logo
    private Integer status;    // 1 正常，0 禁用
    private Date createTime;
    private Date updateTime;
}
