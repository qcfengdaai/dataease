package io.dataease.license.manage;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * 核心许可证管理类
 * 负责管理系统的版本信息
 */
@Component
public class CoreLicManage {

    /** 系统版本号 */
    @Value("${dataease.version}")
    private String version;

    /**
     * 获取系统版本号
     * @return 版本号字符串
     */
    public String getVersion() {
        return version;
    }

}
