package io.dataease.utils;

import io.dataease.auth.bo.TokenUserBO;
import org.apache.commons.lang3.ObjectUtils;

/**
 * 认证工具类
 * <p>
 * 基于ThreadLocal管理当前请求线程的用户信息，提供用户身份识别和权限判断
 * </p>
 *
 * <p><b>核心功能：</b></p>
 * <ul>
 *   <li>线程级用户信息存储 - 保证多线程环境下的用户信息隔离</li>
 *   <li>系统管理员识别 - 用于权限控制和特殊业务逻辑</li>
 *   <li>用户上下文管理 - 提供统一的用户信息获取接口</li>
 * </ul>
 *
 * <p><b>使用位置：</b></p>
 * <ul>
 *   <li>TokenFilter - 请求开始时设置用户信息</li>
 *   <li>业务Service层 - 获取当前登录用户</li>
 *   <li>权限控制 - 判断用户权限</li>
 *   <li>审计日志 - 记录操作用户</li>
 *   <li>数据过滤 - 根据用户权限过滤数据</li>
 * </ul>
 *
 * <p><b>注意事项：</b></p>
 * <ul>
 *   <li>必须在请求结束时调用remove()清理ThreadLocal，避免内存泄漏</li>
 *   <li>异步任务中无法直接获取用户信息，需要手动传递</li>
 *   <li>系统管理员UID固定为1L，请勿修改</li>
 * </ul>
 *
 * @author DataEase
 * @since 1.0.0
 */
public class AuthUtils {

    /**
     * 系统管理员的用户ID
     * 固定为1L，系统初始化时创建的第一个用户
     */
    private static final Long SYS_ADMIN_UID = 1L;

    /**
     * 线程本地变量，存储当前请求线程的用户信息
     * 每个HTTP请求对应一个线程，保证用户信息隔离
     */
    private static final ThreadLocal<TokenUserBO> USER_INFO = new ThreadLocal<TokenUserBO>();

    /**
     * 获取当前线程的用户信息
     * <p>
     * 从ThreadLocal中获取当前请求用户的完整信息
     * </p>
     *
     * <p><b>使用示例：</b></p>
     * <pre>
     * // 在Service层获取当前用户
     * TokenUserBO user = AuthUtils.getUser();
     * if (user != null) {
     *     Long userId = user.getUserId();
     *     String username = user.getUsername();
     *     // 进行业务处理
     * }
     * </pre>
     *
     * @return 当前用户信息，未登录或未设置时返回null
     */
    public static TokenUserBO getUser() {
        // 检查ThreadLocal中是否有用户信息
        if (ObjectUtils.isNotEmpty(USER_INFO.get()))
            return USER_INFO.get();
        return null;
    }

    /**
     * 设置当前线程的用户信息
     * <p>
     * 通常在请求过滤器（TokenFilter）中调用，将认证后的用户信息绑定到当前线程
     * </p>
     *
     * <p><b>调用位置：</b></p>
     * <ul>
     *   <li>TokenFilter - Token验证通过后设置用户信息</li>
     *   <li>CommunityTokenFilter - 社区版Token过滤器</li>
     *   <li>单元测试 - 模拟用户登录</li>
     * </ul>
     *
     * @param userBO 用户业务对象，包含userId、username等信息
     */
    public static void setUser(TokenUserBO userBO) {
        // 将用户信息存入ThreadLocal
        USER_INFO.set(userBO);
    }

    /**
     * 清除当前线程的用户信息
     * <p>
     * 必须在请求结束时调用，避免ThreadLocal内存泄漏
     * 通常在过滤器的finally块中调用
     * </p>
     *
     * <p><b>重要性：</b></p>
     * <ul>
     *   <li>避免内存泄漏 - Tomcat线程池复用，不清理会导致旧数据残留</li>
     *   <li>安全性 - 防止用户信息泄露到下一个请求</li>
     *   <li>数据准确性 - 确保每个请求的用户信息独立</li>
     * </ul>
     *
     * <p><b>调用位置：</b></p>
     * <ul>
     *   <li>TokenFilter的finally块</li>
     *   <li>异常处理器</li>
     *   <li>拦截器的afterCompletion方法</li>
     * </ul>
     */
    public static void remove() {
        // 清除ThreadLocal中的用户信息
        USER_INFO.remove();
    }

    /**
     * 判断当前用户是否为系统管理员
     * <p>
     * 从当前线程获取用户信息，判断是否为系统管理员（UID=1）
     * </p>
     *
     * <p><b>使用示例：</b></p>
     * <pre>
     * // 系统设置修改需要管理员权限
     * if (AuthUtils.isSysAdmin()) {
     *     // 执行管理员操作
     *     updateSystemSettings(settings);
     * } else {
     *     throw new DEException("无权限操作");
     * }
     * </pre>
     *
     * @return true-是系统管理员，false-不是或未登录
     */
    public static boolean isSysAdmin() {
        TokenUserBO user = null;
        // 获取当前用户，为空则不是管理员
        if (ObjectUtils.isEmpty(user = getUser())) {
            return false;
        }
        // 提取用户ID并判断
        Long userId = user.getUserId();
        return isSysAdmin(userId);
    }

    /**
     * 判断指定用户ID是否为系统管理员
     * <p>
     * 根据用户ID判断是否为系统管理员，不依赖当前线程上下文
     * </p>
     *
     * <p><b>使用场景：</b></p>
     * <ul>
     *   <li>数据权限过滤 - 判断数据归属用户是否为管理员</li>
     *   <li>批量操作 - 判断多个用户的管理员身份</li>
     *   <li>异步任务 - 传递用户ID进行权限判断</li>
     * </ul>
     *
     * @param userId 用户ID
     * @return true-是系统管理员（UID=1），false-不是
     */
    public static boolean isSysAdmin(Long userId) {
        // 系统管理员的UID固定为1L
        return userId.equals(SYS_ADMIN_UID);
    }


}
