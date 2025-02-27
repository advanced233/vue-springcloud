package cn.edu.jlu.admin.entity;

import lombok.Data;
import java.util.Date;

/**
 * 管理员实体类
 */
@Data
public class Admin {
    private Long id;
    private String username;
    private String password;
    private String phone;
    private String avatar;
    private Integer status;
    private Date createTime;
    private Date updateTime;
}
