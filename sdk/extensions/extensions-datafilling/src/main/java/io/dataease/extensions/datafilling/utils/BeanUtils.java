package io.dataease.extensions.datafilling.utils;

import io.dataease.license.utils.LogUtil;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Bean对象工具类
 * 提供JavaBean对象的属性拷贝、反射访问等便捷操作方法
 *
 * <p>主要功能：</p>
 * <ul>
 *     <li>对象属性拷贝：将源对象的属性值复制到目标对象</li>
 *     <li>反射读取属性：通过属性名动态获取对象的属性值</li>
 *     <li>反射设置属性：通过属性名动态设置对象的属性值</li>
 *     <li>获取类的字段列表：获取类的所有字段名称</li>
 * </ul>
 *
 * <p>在数据填报模块中的作用：</p>
 * <ul>
 *     <li>用于DTO对象之间的数据转换和复制</li>
 *     <li>动态处理表单字段的读写操作</li>
 *     <li>支持数据填报表单的通用字段映射处理</li>
 * </ul>
 *
 * <p>注意事项：</p>
 * <ul>
 *     <li>本工具类基于Spring的BeanUtils实现属性拷贝</li>
 *     <li>属性拷贝采用浅拷贝，引用类型对象不会被深度复制</li>
 *     <li>反射操作会有一定的性能开销，频繁调用时需注意</li>
 *     <li>字段名必须符合JavaBean命名规范（驼峰命名）</li>
 * </ul>
 */
public class BeanUtils {

    /**
     * 拷贝对象属性
     * 将源对象的所有属性值复制到目标对象的同名属性中
     *
     * <p>该方法基于Spring的BeanUtils.copyProperties实现，特点：</p>
     * <ul>
     *     <li>只复制同名且类型兼容的属性</li>
     *     <li>采用浅拷贝，引用类型共享同一对象</li>
     *     <li>源对象的null值会覆盖目标对象的非null值</li>
     * </ul>
     *
     * <p>使用示例：</p>
     * <pre>
     * // 将DTO对象的属性复制到Entity对象
     * FormDataDTO dto = new FormDataDTO();
     * dto.setName("张三");
     * dto.setAge(25);
     *
     * FormDataEntity entity = new FormDataEntity();
     * BeanUtils.copyBean(entity, dto);
     * // entity.getName() 返回 "张三"
     * // entity.getAge() 返回 25
     * </pre>
     *
     * @param <T> 目标对象的类型
     * @param target 目标对象，属性会被源对象的值覆盖
     * @param source 源对象，提供属性值
     * @return 返回目标对象本身（方便链式调用）
     * @throws RuntimeException 如果属性拷贝过程中发生异常
     */
    public static <T> T copyBean(T target, Object source) {
        try {
            org.springframework.beans.BeanUtils.copyProperties(source, target);
            return target;
        } catch (Exception e) {
            throw new RuntimeException("Failed to copy object: ", e);
        }
    }

    /**
     * 拷贝对象属性（忽略指定属性）
     * 将源对象的属性值复制到目标对象，可以指定需要忽略的属性名
     *
     * <p>使用场景：</p>
     * <ul>
     *     <li>更新操作时，不希望覆盖某些字段（如id、createTime等）</li>
     *     <li>复制时排除敏感字段（如密码、token等）</li>
     *     <li>保留目标对象的特定属性值不被覆盖</li>
     * </ul>
     *
     * <p>使用示例：</p>
     * <pre>
     * // 更新用户信息，但保留id和创建时间不变
     * UserDTO dto = new UserDTO();
     * dto.setName("李四");
     * dto.setEmail("lisi@example.com");
     *
     * UserEntity entity = userRepository.findById(1L);
     * // 忽略id和createTime字段，只复制其他属性
     * BeanUtils.copyBean(entity, dto, "id", "createTime");
     * userRepository.save(entity);
     * </pre>
     *
     * @param <T> 目标对象的类型
     * @param target 目标对象
     * @param source 源对象
     * @param ignoreProperties 要忽略的属性名数组，这些属性不会被复制
     * @return 返回目标对象本身
     * @throws RuntimeException 如果属性拷贝过程中发生异常
     */
    public static <T> T copyBean(T target, Object source, String... ignoreProperties) {
        try {
            org.springframework.beans.BeanUtils.copyProperties(source, target, ignoreProperties);
            return target;
        } catch (Exception e) {
            throw new RuntimeException("Failed to copy object: ", e);
        }
    }

    /**
     * 通过字段名获取对象的属性值
     * 使用反射机制，通过getter方法获取对象指定字段的值
     *
     * <p>实现原理：</p>
     * <ol>
     *     <li>将字段名首字母大写</li>
     *     <li>拼接"get"前缀，构造getter方法名</li>
     *     <li>通过反射调用getter方法获取值</li>
     * </ol>
     *
     * <p>使用场景：</p>
     * <ul>
     *     <li>动态读取表单字段值，字段名在运行时确定</li>
     *     <li>通用的数据导出功能，根据配置读取不同字段</li>
     *     <li>数据验证时动态获取字段值进行校验</li>
     * </ul>
     *
     * <p>使用示例：</p>
     * <pre>
     * FormDataEntity entity = new FormDataEntity();
     * entity.setName("王五");
     * entity.setAge(30);
     *
     * // 动态获取字段值
     * Object nameValue = BeanUtils.getFieldValueByName("name", entity);
     * System.out.println(nameValue); // 输出: 王五
     *
     * Object ageValue = BeanUtils.getFieldValueByName("age", entity);
     * System.out.println(ageValue); // 输出: 30
     * </pre>
     *
     * @param fieldName 字段名称（驼峰命名，如"userName"）
     * @param bean 目标对象
     * @return 字段的值，如果字段不存在或读取失败返回null
     */
    public static Object getFieldValueByName(String fieldName, Object bean) {
        try {
            if (StringUtils.isBlank(fieldName)) {
                return null;
            }
            String firstLetter = fieldName.substring(0, 1).toUpperCase();
            String getter = "get" + firstLetter + fieldName.substring(1);
            Method method = bean.getClass().getMethod(getter);
            return method.invoke(bean);
        } catch (Exception e) {
            LogUtil.error("failed to getFieldValueByName. ", e);
            return null;
        }
    }

    /**
     * 通过字段名设置对象的属性值
     * 使用反射机制，通过setter方法设置对象指定字段的值
     *
     * <p>实现原理：</p>
     * <ol>
     *     <li>将字段名首字母大写</li>
     *     <li>拼接"set"前缀，构造setter方法名</li>
     *     <li>通过反射调用setter方法设置值</li>
     * </ol>
     *
     * <p>使用场景：</p>
     * <ul>
     *     <li>动态设置表单字段值，字段名在运行时确定</li>
     *     <li>通用的数据导入功能，根据配置设置不同字段</li>
     *     <li>数据填报时根据表单配置动态填充字段值</li>
     * </ul>
     *
     * <p>使用示例：</p>
     * <pre>
     * FormDataEntity entity = new FormDataEntity();
     *
     * // 动态设置字段值
     * BeanUtils.setFieldValueByName(entity, "name", "赵六", String.class);
     * BeanUtils.setFieldValueByName(entity, "age", 28, Integer.class);
     *
     * System.out.println(entity.getName()); // 输出: 赵六
     * System.out.println(entity.getAge());  // 输出: 28
     * </pre>
     *
     * @param bean 目标对象
     * @param fieldName 字段名称（驼峰命名）
     * @param value 要设置的值
     * @param type 字段的类型（用于查找匹配的setter方法）
     */
    public static void setFieldValueByName(Object bean, String fieldName, Object value, Class<?> type) {
        try {
            if (StringUtils.isBlank(fieldName)) {
                return;
            }
            String firstLetter = fieldName.substring(0, 1).toUpperCase();
            String setter = "set" + firstLetter + fieldName.substring(1);
            Method method = bean.getClass().getMethod(setter, type);
            method.invoke(bean, value);
        } catch (Exception e) {
            LogUtil.error("failed to setFieldValueByName. ", e);
        }
    }

    /**
     * 获取对象的setter方法
     * 根据字段名和字段类型，获取对应的setter方法对象
     *
     * <p>使用场景：</p>
     * <ul>
     *     <li>预先获取Method对象，避免重复反射查找（性能优化）</li>
     *     <li>判断对象是否有某个字段的setter方法</li>
     *     <li>批量设置多个字段时，先获取所有Method对象再统一调用</li>
     * </ul>
     *
     * <p>使用示例：</p>
     * <pre>
     * FormDataEntity entity = new FormDataEntity();
     *
     * // 获取setter方法
     * Method setNameMethod = BeanUtils.getMethod(entity, "name", String.class);
     * if (setNameMethod != null) {
     *     // 批量设置多条记录的同一字段
     *     for (FormDataEntity item : entityList) {
     *         setNameMethod.invoke(item, "批量设置的值");
     *     }
     * }
     * </pre>
     *
     * @param bean 目标对象
     * @param fieldName 字段名称
     * @param type 字段类型
     * @return setter方法对象，如果方法不存在返回null
     */
    public static Method getMethod(Object bean, String fieldName, Class<?> type) {
        try {
            if (StringUtils.isBlank(fieldName)) {
                return null;
            }
            String firstLetter = fieldName.substring(0, 1).toUpperCase();
            String setter = "set" + firstLetter + fieldName.substring(1);
            return bean.getClass().getMethod(setter, type);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 获取类的所有字段名称列表
     * 通过反射获取类声明的所有字段的名称
     *
     * <p>注意事项：</p>
     * <ul>
     *     <li>只返回当前类声明的字段，不包括父类的字段</li>
     *     <li>包括所有访问级别的字段（public、protected、private、package）</li>
     *     <li>包括静态字段和实例字段</li>
     * </ul>
     *
     * <p>使用场景：</p>
     * <ul>
     *     <li>动态生成表单时，列出所有可用的字段</li>
     *     <li>通用的对象比较功能，遍历所有字段进行对比</li>
     *     <li>数据导出时，根据类的字段生成表头</li>
     * </ul>
     *
     * <p>使用示例：</p>
     * <pre>
     * // 获取FormDataEntity类的所有字段名
     * List&lt;String&gt; fieldNames = BeanUtils.getFieldNames(FormDataEntity.class);
     * // 可能返回: ["id", "name", "age", "createTime", "updateTime"]
     *
     * // 动态处理所有字段
     * FormDataEntity entity = getFormData();
     * for (String fieldName : fieldNames) {
     *     Object value = BeanUtils.getFieldValueByName(fieldName, entity);
     *     System.out.println(fieldName + " = " + value);
     * }
     * </pre>
     *
     * @param clazz 目标类的Class对象
     * @return 字段名称列表
     */
    public static List<String> getFieldNames(Class<?> clazz) {
        List<String> fieldNames = new ArrayList<>();
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            fieldNames.add(field.getName());
        }
        return fieldNames;
    }
}
