package io.dataease.auth.bo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 * Token用户业务对象
 * 用于在令牌验证和用户认证过程中传递用户基本信息
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TokenUserBO implements Serializable {

    @Serial
    private static final long serialVersionUID = -9163835853313313361L;

    /**
     * 用户ID
     * 系统中唯一标识用户的主键
     */
    private Long userId;

    /**
     * 默认组织ID
     * 用户登录后默认进入的组织标识，用于多租户环境下的组织隔离
     */
    private Long defaultOid;
}
