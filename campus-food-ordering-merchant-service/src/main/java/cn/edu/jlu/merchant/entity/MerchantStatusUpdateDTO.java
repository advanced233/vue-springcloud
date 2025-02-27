package cn.edu.jlu.merchant.entity;

import lombok.Data;

@Data
public class MerchantStatusUpdateDTO {
    private Long merchantId;
    private Integer status;  // 0 表示封禁，1 表示正常
}
