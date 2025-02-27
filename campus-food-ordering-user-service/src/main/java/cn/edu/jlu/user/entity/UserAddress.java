package cn.edu.jlu.user.entity;

import java.util.Date;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

@Data
public class UserAddress {
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;
    @JsonSerialize(using = ToStringSerializer.class)
    private Long userId;
    private String address;
    private String tag;         // 地址标签，如 "家"、"公司" 等
    private Integer isDefault;  // 0：否，1：是
    private Date createTime;
    private Date updateTime;
}
