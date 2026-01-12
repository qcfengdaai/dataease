package io.dataease.utils;

import org.apache.commons.lang3.ArrayUtils;

import java.lang.reflect.Method;

/**
 * 反射操作工具类
 * <p>
 * 提供简化的Java反射API封装，用于方法查找等反射操作。
 * 相比原生反射API，提供了更友好的错误处理和空值安全。
 * </p>
 *
 * <p><b>核心功能：</b></p>
 * <ul>
 *   <li>按名称查找方法 - 从类中查找指定名称的方法</li>
 *   <li>空值安全 - 不抛出异常，查找失败返回null</li>
 *   <li>简化API - 封装复杂的反射操作</li>
 * </ul>
 *
 * <p><b>应用场景：</b></p>
 * <ul>
 *   <li>动态方法调用 - 根据方法名称动态调用对象方法</li>
 *   <li>框架开发 - 插件系统、扩展机制中的动态调用</li>
 *   <li>配置驱动 - 根据配置文件中的方法名调用</li>
 *   <li>通用处理器 - 处理不同类型对象的通用逻辑</li>
 * </ul>
 *
 * <p><b>使用位置：</b></p>
 * <ul>
 *   <li>{@link io.dataease.auth.filter.CommunityTokenFilter#doFilter} - 动态获取缓存对象的方法并调用</li>
 * </ul>
 *
 * <p><b>使用示例：</b></p>
 * <pre>
 * // 示例1：基本方法查找
 * Class<?> userClass = User.class;
 * Method getName = DeReflectUtil.findMethod(userClass, "getName");
 * if (getName != null) {
 *     Object result = getName.invoke(userInstance);
 *     System.out.println("Name: " + result);
 * }
 *
 * // 示例2：查找不存在的方法
 * Method notExist = DeReflectUtil.findMethod(userClass, "nonExistMethod");
 * System.out.println(notExist);  // 输出: null
 *
 * // 示例3：动态调用方法
 * public Object invokeMethodByName(Object obj, String methodName) {
 *     Method method = DeReflectUtil.findMethod(obj.getClass(), methodName);
 *     if (method != null) {
 *         try {
 *             return method.invoke(obj);
 *         } catch (Exception e) {
 *             // 处理异常
 *         }
 *     }
 *     return null;
 * }
 *
 * // 示例4：实际使用场景 - 动态获取用户密钥（参考 CommunityTokenFilter.java:96-100）
 * // 从缓存管理器中动态获取用户密钥
 * Object apisixCacheManage = CommonBeanFactory.getBean("apisixCacheManage");
 *
 * // 查找并调用userCacheBO方法
 * Method method = DeReflectUtil.findMethod(apisixCacheManage.getClass(), "userCacheBO");
 * Object cacheBO = ReflectionUtils.invokeMethod(method, apisixCacheManage, userId);
 *
 * // 查找并调用getSecret方法
 * Method pwdMethod = DeReflectUtil.findMethod(cacheBO.getClass(), "getSecret");
 * Object pwdObj = ReflectionUtils.invokeMethod(pwdMethod, cacheBO);
 * String secret = pwdObj.toString();
 *
 * // 示例5：配置驱动的方法调用
 * public void processConfig(Object processor, Map<String, String> config) {
 *     String methodName = config.get("processMethod");  // 从配置获取方法名
 *     Method method = DeReflectUtil.findMethod(processor.getClass(), methodName);
 *     if (method != null) {
 *         try {
 *             method.invoke(processor);
 *         } catch (Exception e) {
 *             LogUtil.getLogger().error("Failed to invoke method: " + methodName, e);
 *         }
 *     }
 * }
 *
 * // 示例6：查找getter方法
 * public Object getPropertyValue(Object obj, String propertyName) {
 *     // 构造getter方法名
 *     String getterName = "get" + propertyName.substring(0, 1).toUpperCase()
 *                       + propertyName.substring(1);
 *     Method getter = DeReflectUtil.findMethod(obj.getClass(), getterName);
 *     if (getter != null) {
 *         try {
 *             return getter.invoke(obj);
 *         } catch (Exception e) {
 *             // 处理异常
 *         }
 *     }
 *     return null;
 * }
 *
 * // 示例7：检查方法是否存在
 * public boolean hasMethod(Object obj, String methodName) {
 *     return DeReflectUtil.findMethod(obj.getClass(), methodName) != null;
 * }
 * </pre>
 *
 * <p><b>注意事项：</b></p>
 * <ul>
 *   <li>只能查找public方法（使用getMethods()）</li>
 *   <li>不支持按参数类型查找，只按方法名匹配</li>
 *   <li>如果存在重载方法，返回第一个匹配的方法（不确定顺序）</li>
 *   <li>查找失败返回null，不抛出异常</li>
 *   <li>返回的Method对象需要配合invoke使用，注意异常处理</li>
 *   <li>频繁调用时建议缓存Method对象，避免重复查找</li>
 * </ul>
 *
 * <p><b>最佳实践：</b></p>
 * <ul>
 *   <li>对于已知类型，优先使用直接调用而非反射</li>
 *   <li>查找到Method后应判空再使用</li>
 *   <li>调用Method.invoke时需要try-catch处理异常</li>
 *   <li>对于频繁调用的方法，缓存Method对象提高性能</li>
 *   <li>使用反射时注意性能开销，避免在循环中频繁使用</li>
 *   <li>如果需要查找私有方法，使用getDeclaredMethods()并setAccessible(true)</li>
 * </ul>
 *
 * <p><b>性能考虑：</b></p>
 * <ul>
 *   <li>反射比直接调用慢10-100倍</li>
 *   <li>Method查找有一定开销，建议缓存</li>
 *   <li>不适合在高频路径上使用</li>
 *   <li>考虑使用MethodHandle或LambdaMetafactory替代</li>
 * </ul>
 *
 * <p><b>限制说明：</b></p>
 * <ul>
 *   <li>只支持无参或单参数方法的查找（按名称）</li>
 *   <li>对于重载方法，无法精确匹配参数类型</li>
 *   <li>如需更复杂的反射操作，建议使用Spring的ReflectionUtils</li>
 * </ul>
 *
 * @author DataEase
 * @since 1.0.0
 * @see java.lang.reflect.Method
 * @see org.springframework.util.ReflectionUtils
 * @see io.dataease.auth.filter.CommunityTokenFilter
 */
public class DeReflectUtil {

    /**
     * 根据方法名查找方法
     * <p>
     * 从指定类的所有public方法中查找与给定名称匹配的方法。
     * 使用Class.getMethods()获取所有public方法（包括继承的方法），
     * 然后遍历查找名称匹配的第一个方法。
     * </p>
     *
     * <p><b>查找范围：</b></p>
     * <ul>
     *   <li>仅查找public方法</li>
     *   <li>包括从父类继承的方法</li>
     *   <li>包括从接口实现的方法</li>
     *   <li>不包括private、protected、package-private方法</li>
     * </ul>
     *
     * <p><b>匹配规则：</b></p>
     * <ul>
     *   <li>只按方法名精确匹配，不考虑参数类型</li>
     *   <li>如果存在重载方法，返回遍历到的第一个（顺序不确定）</li>
     *   <li>方法名区分大小写</li>
     * </ul>
     *
     * <p><b>使用示例：</b></p>
     * <pre>
     * // 查找getter方法
     * Method getId = DeReflectUtil.findMethod(User.class, "getId");
     * if (getId != null) {
     *     Long id = (Long) getId.invoke(userInstance);
     * }
     *
     * // 查找自定义方法
     * Method process = DeReflectUtil.findMethod(processor.getClass(), "process");
     * if (process != null) {
     *     process.invoke(processor);
     * }
     *
     * // 处理查找失败
     * Method method = DeReflectUtil.findMethod(MyClass.class, "methodName");
     * if (method == null) {
     *     // 方法不存在，使用默认处理
     *     handleDefault();
     * }
     * </pre>
     *
     * @param cla 要查找方法的类，不能为null
     * @param methodName 方法名称，不能为null或空
     * @return 找到的Method对象；如果类没有方法、方法名不存在或参数为null，返回null
     * @throws NullPointerException 如果cla为null（在getMethods()时抛出）
     */
    public static Method findMethod(Class<?> cla, String methodName) {
        Method[] methods = cla.getMethods();
        if (ArrayUtils.isEmpty(methods)) return null;
        for (Method method : methods) {
            if (method.getName().equals(methodName)){
                return method;
            }
        }
        return null;
    }
}
