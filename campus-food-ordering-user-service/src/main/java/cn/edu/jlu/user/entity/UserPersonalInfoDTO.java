package cn.edu.jlu.user.entity;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

@Data
public class UserPersonalInfoDTO {
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;
    private String username;
    private String password;
    private String phone;
    private String avatar;
    private Integer status;
    private Date createTime;
    private Date updateTime;

    // 用户所有地址
    private List<UserAddress> addresses;
}
