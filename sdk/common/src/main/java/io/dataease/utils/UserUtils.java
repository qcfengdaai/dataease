package io.dataease.utils;

import io.dataease.auth.bo.TokenUserBO;

/**
 * 用户上下文工具类
 * <p>
 * 提供用户信息的上下文管理功能，用于在当前线程中设置、获取和清除用户信息。
 * 本工具类是对{@link AuthUtils}的封装，简化了用户上下文的操作。
 * 主要用于HTTP请求过滤器中设置当前登录用户信息，以及在业务代码中获取当前用户。
 * </p>
 *
 * <p><b>核心功能：</b></p>
 * <ul>
 *   <li>用户信息设置 - 将用户信息设置到当前线程上下文</li>
 *   <li>桌面用户设置 - 为桌面版应用设置默认用户</li>
 *   <li>用户信息清除 - 清除当前线程的用户信息，防止内存泄漏</li>
 * </ul>
 *
 * <p><b>工作原理：</b></p>
 * <ul>
 *   <li>使用ThreadLocal存储用户信息，每个线程独立</li>
 *   <li>在HTTP请求开始时设置用户信息</li>
 *   <li>在HTTP请求结束时清除用户信息</li>
 *   <li>业务代码通过AuthUtils.getUser()获取当前用户</li>
 * </ul>
 *
 * <p><b>使用位置：</b></p>
 * <ul>
 *   <li>{@link io.dataease.auth.filter.TokenFilter#doFilter} - HTTP请求过滤器中设置和清除用户信息</li>
 *   <li>{@link io.dataease.utils.TokenUtils} - Token验证工具中设置用户信息</li>
 *   <li>各业务模块 - 通过AuthUtils.getUser()获取当前登录用户信息</li>
 * </ul>
 *
 * <p><b>使用示例：</b></p>
 * <pre>
 * // 示例1：在过滤器中设置用户信息（实际使用场景）
 * // 参考 TokenFilter.java:82
 * public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) {
 *     try {
 *         String token = ServletUtils.getToken();
 *         TokenUserBO userBO = TokenUtils.validate(token);
 *         UserUtils.setUserInfo(userBO);  // 设置用户信息到上下文
 *         chain.doFilter(request, response);
 *     } finally {
 *         UserUtils.removeUser();  // 请求结束后清除用户信息
 *     }
 * }
 *
 * // 示例2：在业务代码中获取当前用户
 * public void updateUserProfile(UserDTO userDTO) {
 *     // 获取当前登录用户
 *     Long currentUserId = AuthUtils.getUser().getUserId();
 *     Long currentOrgId = AuthUtils.getUser().getDefaultOid();
 *
 *     // 验证权限
 *     if (!currentUserId.equals(userDTO.getId())) {
 *         throw new RuntimeException("无权修改其他用户信息");
 *     }
 *
 *     // 执行业务逻辑
 *     userService.update(userDTO);
 * }
 *
 * // 示例3：桌面版应用设置默认用户
 * if (ModelUtils.isDesktop()) {
 *     UserUtils.setDesktopUser();  // 设置默认桌面用户（ID=1）
 *     // 执行业务逻辑...
 * }
 *
 * // 示例4：手动设置用户信息（测试场景）
 * TokenUserBO testUser = new TokenUserBO();
 * testUser.setUserId(100L);
 * testUser.setDefaultOid(1L);
 * UserUtils.setUserInfo(testUser);
 * try {
 *     // 执行需要用户上下文的测试代码...
 *     Long userId = AuthUtils.getUser().getUserId();
 *     System.out.println("当前用户ID: " + userId);  // 输出: 当前用户ID: 100
 * } finally {
 *     UserUtils.removeUser();  // 测试后清理
 * }
 *
 * // 示例5：分享链接中设置用户信息
 * String linkToken = ServletUtils.getHead("LINK-TOKEN");
 * if (StringUtils.isNotBlank(linkToken)) {
 *     TokenUserBO tokenUserBO = TokenUtils.validateLinkToken(linkToken);
 *     UserUtils.setUserInfo(tokenUserBO);  // 设置分享创建者的用户信息
 *     // 允许访问分享的资源...
 * }
 * </pre>
 *
 * <p><b>注意事项：</b></p>
 * <ul>
 *   <li>线程隔离 - 用户信息存储在ThreadLocal中，不同线程之间互不影响</li>
 *   <li>必须清除 - 使用后必须调用removeUser()清除用户信息，防止ThreadLocal内存泄漏</li>
 *   <li>Filter中使用 - 通常在Filter的finally块中调用removeUser()，确保一定会执行</li>
 *   <li>桌面版默认用户 - 桌面版使用固定的用户ID（1）和组织ID（1）</li>
 *   <li>异步任务 - 如果在异步任务中需要用户信息，需要在任务执行前手动传递</li>
 * </ul>
 *
 * <p><b>最佳实践：</b></p>
 * <ul>
 *   <li>统一在Filter中管理 - 所有HTTP请求的用户信息设置和清除应在Filter中统一处理</li>
 *   <li>使用try-finally - 确保removeUser()一定会被调用，避免内存泄漏</li>
 *   <li>避免手动设置 - 业务代码中不应手动设置用户信息，应由Filter统一管理</li>
 *   <li>异步任务传递 - 异步任务中需要用户信息时，应在任务参数中显式传递</li>
 *   <li>单元测试 - 单元测试中需要用户上下文时，在@Before中设置，在@After中清除</li>
 * </ul>
 *
 * <p><b>典型的请求处理流程：</b></p>
 * <ol>
 *   <li>客户端发起HTTP请求，携带JWT Token</li>
 *   <li>TokenFilter拦截请求，验证Token</li>
 *   <li>TokenUtils解析Token，提取用户ID和组织ID</li>
 *   <li>UserUtils.setUserInfo()将用户信息设置到ThreadLocal</li>
 *   <li>请求进入Controller和Service层</li>
 *   <li>业务代码通过AuthUtils.getUser()获取当前用户</li>
 *   <li>请求处理完成，Filter的finally块调用UserUtils.removeUser()</li>
 *   <li>ThreadLocal被清理，防止内存泄漏</li>
 * </ol>
 *
 * @author DataEase
 * @since 1.0.0
 * @see io.dataease.utils.AuthUtils
 * @see io.dataease.auth.bo.TokenUserBO
 * @see io.dataease.auth.filter.TokenFilter
 * @see io.dataease.utils.TokenUtils
 */
public class UserUtils {

    /**
     * 设置当前线程的用户信息
     * <p>
     * 将用户信息设置到当前线程的上下文中，后续业务代码可以通过AuthUtils.getUser()获取。
     * 通常在HTTP请求过滤器中调用，在Token验证成功后设置用户信息。
     * </p>
     *
     * <p><b>使用场景：</b></p>
     * <ul>
     *   <li>Token验证成功后，设置用户信息</li>
     *   <li>分享链接验证成功后，设置分享创建者的用户信息</li>
     *   <li>单元测试中，模拟用户登录状态</li>
     * </ul>
     *
     * @param userBO 用户信息对象，包含用户ID和组织ID
     * @see io.dataease.auth.filter.TokenFilter#doFilter
     */
    public static void setUserInfo(TokenUserBO userBO) {
        AuthUtils.setUser(userBO);
    }

    /**
     * 设置桌面版默认用户
     * <p>
     * 为DataEase桌面版应用设置默认的用户信息。
     * 桌面版不需要用户登录，使用固定的用户ID（1）和组织ID（1）。
     * 这是桌面版应用的特殊处理，Web版不应使用此方法。
     * </p>
     *
     * <p><b>默认用户信息：</b></p>
     * <ul>
     *   <li>用户ID：1</li>
     *   <li>组织ID：1</li>
     * </ul>
     *
     * <p><b>使用场景：</b></p>
     * <ul>
     *   <li>桌面版应用启动时设置默认用户</li>
     *   <li>桌面版应用处理请求时设置用户上下文</li>
     * </ul>
     *
     * @see io.dataease.auth.filter.TokenFilter#doFilter
     * @see io.dataease.utils.ModelUtils#isDesktop()
     */
    public static void setDesktopUser() {
        TokenUserBO bo = new TokenUserBO();
        bo.setUserId(1L);
        bo.setDefaultOid(1L);
        AuthUtils.setUser(bo);
    }

    /**
     * 清除当前线程的用户信息
     * <p>
     * 从当前线程的ThreadLocal中移除用户信息，防止内存泄漏。
     * <b>重要：</b>在HTTP请求处理完成后必须调用此方法，通常在Filter的finally块中调用。
     * </p>
     *
     * <p><b>为什么必须清除：</b></p>
     * <ul>
     *   <li>Web容器使用线程池，线程会被复用</li>
     *   <li>如果不清除ThreadLocal，下次复用线程时会读取到旧的用户信息</li>
     *   <li>长期不清除会导致ThreadLocal内存泄漏</li>
     * </ul>
     *
     * <p><b>使用场景：</b></p>
     * <ul>
     *   <li>HTTP请求处理完成后清除用户信息（Filter的finally块）</li>
     *   <li>单元测试结束后清除测试用户信息（@After方法）</li>
     *   <li>异步任务执行完成后清除传递的用户信息</li>
     * </ul>
     *
     * @see io.dataease.auth.filter.TokenFilter#doFilter
     */
    public static void removeUser() {
        AuthUtils.remove();
    }
}
