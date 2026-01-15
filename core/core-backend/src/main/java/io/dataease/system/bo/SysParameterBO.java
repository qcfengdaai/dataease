package io.dataease.system.bo;

import lombok.Data;

import java.io.Serializable;

/**
 * 系统参数业务对象
 * <p>
 * 用于封装系统参数的键值对信息，支持参数的传输和展示
 * </p>
 */
@Data
public class SysParameterBO implements Serializable {

    /**
     * 参数键
     */
    private String key;

    /**
     * 参数值
     */
    private String val;

    /**
     * 参数类型
     */
    private String type;

    /**
     * 排序顺序
     */
    private String sort;
}
