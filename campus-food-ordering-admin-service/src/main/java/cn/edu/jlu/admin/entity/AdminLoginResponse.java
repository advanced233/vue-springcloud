package cn.edu.jlu.admin.entity;

import lombok.Data;

/**
 * 管理员登录响应对象
 */
@Data
public class AdminLoginResponse {
    private Long adminId;
    private String message;
}
