package io.dataease.home;

import io.dataease.home.manage.DeIndexManage;
import io.dataease.utils.ModelUtils;
import io.dataease.utils.RsaUtils;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * 首页REST控制器
 * 提供系统配置相关的API接口，包括密钥获取和版本模式查询
 *
 * 主要功能：
 * - 获取RSA公钥
 * - 生成对称加密密钥
 * - 查询应用模式（桌面版/服务器版）
 * - 查询企业版模式状态
 *
 * 使用场景：
 * - 前端初始化时获取加密密钥
 * - 前端查询当前运行模式
 * - 前端查询是否为企业版
 */
@RestController
@RequestMapping
public class RestIndexController {


    @Resource
    private DeIndexManage deIndexManage;

    /**
     * 获取RSA公钥
     * 用于前端加密数据
     * @return RSA公钥字符串
     */
    @GetMapping("/dekey")
    @ResponseBody
    public String dekey() {
        return RsaUtils.publicKey();
    }

    /**
     * 生成对称加密密钥
     * 用于前端数据加密传输
     * @return 对称密钥字符串
     */
    @GetMapping("/symmetricKey")
    @ResponseBody
    public String symmetricKey() {
        return RsaUtils.generateSymmetricKey();
    }


    /**
     * 查询应用模式
     * @return true表示桌面版，false表示服务器版
     */
    @GetMapping("/model")
    @ResponseBody
    public boolean model() {
        return ModelUtils.isDesktop();
    }


    /**
     * 查询企业版模式状态
     * @return true表示企业版，false或null表示社区版
     */
    @GetMapping("/xpackModel")
    @ResponseBody
    public Boolean xpackModel() {
        return deIndexManage.xpackModel();
    }

}
