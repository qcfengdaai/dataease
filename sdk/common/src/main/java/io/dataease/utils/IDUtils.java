package io.dataease.utils;


import jakarta.annotation.Resource;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Component;

/**
 * ID生成工具类
 * <p>
 * 提供两种ID生成策略：
 * <ul>
 *   <li>随机字符串ID - 适用于临时标识、token等场景</li>
 *   <li>雪花算法ID - 适用于数据库主键，保证全局唯一且有序</li>
 * </ul>
 * </p>
 *
 * <p><b>使用位置：</b></p>
 * <ul>
 *   <li>core-backend各业务模块的实体主键生成</li>
 *   <li>临时token生成（如分享链接、导出任务等）</li>
 *   <li>文件上传的唯一标识生成</li>
 * </ul>
 *
 * @author DataEase
 * @since 1.0.0
 */
@Component
public class IDUtils {

    /**
     * 雪花算法实例，用于生成分布式唯一ID
     * 静态变量，全局共享，保证ID生成的唯一性
     */
    private static SnowFlake snowFlake;

    /**
     * 注入雪花算法实例
     * <p>
     * 通过Spring依赖注入设置静态变量，确保在Spring容器启动后可用
     * </p>
     *
     * @param snowFlake 雪花算法实例
     */
    @Resource
    public void setSnowFlake(SnowFlake snowFlake) {
        IDUtils.snowFlake = snowFlake;
    }

    /**
     * 生成随机字符串ID
     * <p>
     * 生成指定长度的字母数字混合字符串，默认16位
     * </p>
     *
     * <p><b>使用场景：</b></p>
     * <ul>
     *   <li>临时token生成</li>
     *   <li>分享链接的短码</li>
     *   <li>验证码生成</li>
     *   <li>文件上传的临时标识</li>
     * </ul>
     *
     * @param num 指定长度，为null时默认16位
     * @return 随机字符串，包含大小写字母和数字
     */
    public static String randomID(Integer num) {
        // 参数校验，空值时使用默认长度16
        num = ObjectUtils.isEmpty(num) ? 16 : num;
        // 生成字母数字混合的随机字符串
        return RandomStringUtils.randomAlphanumeric(num);
    }

    /**
     * 生成雪花算法ID（推荐用于数据库主键）
     * <p>
     * 使用雪花算法生成64位Long类型的全局唯一ID，具有以下特点：
     * <ul>
     *   <li>全局唯一 - 分布式环境下保证不重复</li>
     *   <li>趋势递增 - 按时间顺序递增，对数据库索引友好</li>
     *   <li>高性能 - 本地生成，无需网络调用</li>
     *   <li>64位长度 - 适合作为Long类型主键</li>
     * </ul>
     * </p>
     *
     * <p><b>使用场景：</b></p>
     * <ul>
     *   <li>数据库表主键（推荐）</li>
     *   <li>分布式系统中需要全局唯一标识的场景</li>
     *   <li>订单号、流水号等业务ID</li>
     * </ul>
     *
     * <p><b>注意事项：</b></p>
     * <ul>
     *   <li>主键请不要使用字符串，推荐使用雪花算法</li>
     *   <li>确保系统时间准确，时间回拨会影响ID生成</li>
     *   <li>分布式部署时需要配置不同的workerId</li>
     * </ul>
     *
     * @return 雪花算法生成的Long类型ID
     */
    public static Long snowID() {
        return snowFlake.nextId();
    }
}
