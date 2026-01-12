package io.dataease.auth.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 密码失效信息视图对象
 * 用于返回用户密码的有效性状态和相关提示信息
 */
@Data
public class InvalidPwdVO implements Serializable {
    @Serial
    private static final long serialVersionUID = 3684394012648654165L;

    /**
     * 用户唯一标识
     * 密码失效的用户ID，JSON序列化时转换为字符串防止精度丢失
     */
    @JsonSerialize(using= ToStringSerializer.class)
    private Long uid;

    /**
     * 密码是否失效
     * true表示密码已失效需要强制修改，false表示密码仍然有效
     */
    private boolean invalid;

    /**
     * 密码有效期
     * 密码剩余有效时间，单位为秒；null表示永久有效
     */
    private Long validityPeriod;
}
