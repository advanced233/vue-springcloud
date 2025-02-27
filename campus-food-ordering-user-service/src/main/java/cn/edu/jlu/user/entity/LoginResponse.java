package cn.edu.jlu.user.entity;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

public class LoginResponse {
    @JsonSerialize(using = ToStringSerializer.class)
    private Long userId;   // 用户ID，可为null
    private String message;  // 提示消息，如"用户不存在"、"密码错误"、"登录成功..."

    // 无参构造
    public LoginResponse() {}

    // 有参构造（可选，看习惯）
    public LoginResponse(Long userId, String message) {
        this.userId = userId;
        this.message = message;
    }

    // getter & setter
    public Long getUserId() {
        return userId;
    }
    public void setUserId(Long userId) {
        this.userId = userId;
    }
    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }
}
