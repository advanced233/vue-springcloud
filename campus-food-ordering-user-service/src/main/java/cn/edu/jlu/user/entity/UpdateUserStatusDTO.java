package cn.edu.jlu.user.entity;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

@Data
public class UpdateUserStatusDTO {
    @JsonSerialize(using = ToStringSerializer.class)
    private Long userId;
    private Integer status;  // 0 表示封禁，1 表示正常
}
