package io.dataease.auth.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 多因子认证(MFA)项视图对象
 * 用于返回多因子认证的配置信息和状态
 */
@Data
public class MfaItem implements Serializable {
    @Serial
    private static final long serialVersionUID = 6647534143991435022L;

    /**
     * MFA是否已启用
     * true表示该用户已启用多因子认证，登录时需要额外验证步骤
     */
    private boolean enabled;

    /**
     * MFA是否已准备就绪
     * true表示MFA配置完成并可以正常使用，false表示配置中或配置异常
     */
    private boolean ready;

    /**
     * 用户唯一标识
     * 关联的用户ID，JSON序列化时转换为字符串防止精度丢失
     */
    @JsonSerialize(using= ToStringSerializer.class)
    private Long uid;
}
