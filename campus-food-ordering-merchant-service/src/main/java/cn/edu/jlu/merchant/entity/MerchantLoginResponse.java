package cn.edu.jlu.merchant.entity;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

public class MerchantLoginResponse {
    @JsonSerialize(using = ToStringSerializer.class)
    private Long merchantId;
    private String message;   // 登录成功 / 商家不存在 / 密码错误 等
    private Integer status;

    public MerchantLoginResponse() {}

    public MerchantLoginResponse(Long merchantId, String message) {
        this.merchantId = merchantId;
        this.message = message;
    }

    public Long getMerchantId() {
        return merchantId;
    }
    public void setMerchantId(Long merchantId) {
        this.merchantId = merchantId;
    }
    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }
    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }
}
