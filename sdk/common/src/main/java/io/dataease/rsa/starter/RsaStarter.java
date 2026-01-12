package io.dataease.rsa.starter;

import io.dataease.rsa.manage.RsaManage;
import io.dataease.utils.LogUtil;
import jakarta.annotation.Resource;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/**
 * RSA密钥系统启动器
 * <p>
 * 实现ApplicationRunner接口，在Spring Boot应用启动完成后自动执行
 * 主要功能是检查系统中RSA密钥的存在性，确保加密功能的正常工作
 * </p>
 */
@Component
public class RsaStarter implements ApplicationRunner {

    @Resource
    private RsaManage rsaManage;

    /**
     * Spring Boot应用启动后的执行入口
     * <p>
     * 该方法在Spring Boot应用完全启动后被自动调用
     * 主要用于检查和初始化RSA密钥
     * </p>
     *
     * @param args 命令行参数
     * @throws Exception 异常情况
     */
    @Override
    public void run(ApplicationArguments args) throws Exception {
        // 启动时检查RSA密钥
        checkRsa();
    }

    /**
     * 检查RSA密钥的存在性
     * <p>
     * 调用RsaManage的check方法来检查RSA密钥是否存在
     * 如果不存在则会自动生成，确保系统加密功能的可用性
     * 对异常情况进行捕获并记录日志，不影响应用启动
     * </p>
     */
    private void checkRsa() {
        try {
            // 调用RSA管理器检查密钥
            rsaManage.check();
        } catch (Exception e) {
            // 记录错误日志，但不中断应用启动
            LogUtil.error(e.getMessage());
        }
    }
}
