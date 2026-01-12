package io.dataease.license.server;

import io.dataease.api.license.LicenseApi;
import io.dataease.api.license.dto.LicenseRequest;
import io.dataease.exception.DEException;
import io.dataease.license.bo.F2CLicResult;
import io.dataease.license.manage.CoreLicManage;
import io.dataease.license.manage.F2CLicManage;
import io.dataease.license.utils.LicenseUtil;
import io.dataease.utils.AuthUtils;
import jakarta.annotation.Resource;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 许可证服务控制器
 * 提供许可证相关的REST API接口
 *
 * 主要功能：
 * - 更新许可证
 * - 验证许可证
 * - 查询版本信息
 * - 还原许可证
 */
@RestController
@RequestMapping("/license")
public class LicenseServer implements LicenseApi {

    /** 产品名称 */
    private static final String product = "DataEase v2";
    @Resource
    private CoreLicManage coreLicManage;

    @Resource
    private F2CLicManage f2CLicManage;


    /**
     * 更新许可证
     * @param request 许可证请求，包含许可证内容
     * @return 许可证更新结果
     */
    @Override
    public F2CLicResult update(LicenseRequest request) {
        return f2CLicManage.updateLicense(product, request.getLicense());
    }

    /**
     * 验证许可证
     * @param request 许可证请求，license为空时验证当前许可证
     * @return 许可证验证结果
     */
    @Override
    public F2CLicResult validate(LicenseRequest request) {
        if (StringUtils.isBlank(request.getLicense())) {
            return f2CLicManage.validate();
        }
        return f2CLicManage.validate(product, request.getLicense());
    }

    /**
     * 获取系统版本号
     * @return 版本号字符串
     */
    @Override
    public String version() {
        return coreLicManage.getVersion();
    }

    /**
     * 还原许可证
     * 只有系统管理员且许可证过期时才能还原
     */
    @Override
    public void revert() {
        F2CLicResult f2CLicResult = null;
        if (!AuthUtils.isSysAdmin() || ObjectUtils.isEmpty(f2CLicResult = LicenseUtil.get()) || f2CLicResult.getStatus() != F2CLicResult.Status.expired) {
            DEException.throwException("不能进行还原操作!");
        }
        f2CLicManage.revert();
    }
}
