package cn.edu.jlu.user.entity;

import java.util.Date;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

@Data  // 自动生成 getter、setter、toString 等方法（需要安装 Lombok 插件）
public class User {
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;
    private String username;
    private String password;
    private String phone;
    private String avatar;
    private Integer status;
    private Date createTime;
    private Date updateTime;
}
