package io.dataease.utils;

import org.apache.commons.lang3.StringUtils;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * JavaBean工具类
 * <p>
 * 提供JavaBean对象的属性拷贝、反射操作、Map转Bean等常用功能。
 * 该工具类基于Spring BeanUtils和Java反射API，简化了Bean对象的操作流程。
 * </p>
 *
 * <p><b>核心功能：</b></p>
 * <ul>
 *   <li>Bean属性拷贝 - 支持完整拷贝和忽略指定属性的拷贝</li>
 *   <li>反射属性操作 - 通过字段名动态获取和设置属性值</li>
 *   <li>字段名获取 - 获取类的所有字段名列表</li>
 *   <li>Map转Bean - 支持驼峰和下划线命名自动转换，支持类型自动转换</li>
 *   <li>方法获取 - 通过字段名获取Setter方法</li>
 * </ul>
 *
 * <p><b>使用位置：</b></p>
 * <ul>
 *   <li>{@link io.dataease.i18n.Translator#translateObject} - 国际化翻译时设置字段值</li>
 *   <li>{@link io.dataease.utils.TreeUtils#convertTree} - 树形结构转换时拷贝Bean属性</li>
 *   <li>DTO与Entity转换 - 不同层级对象之间的属性复制</li>
 *   <li>数据库查询结果映射 - Map结构数据转换为JavaBean对象</li>
 *   <li>API响应对象构建 - 快速构建返回对象</li>
 * </ul>
 *
 * <p><b>使用示例：</b></p>
 * <pre>
 * // 示例1：基本Bean属性拷贝
 * UserEntity entity = new UserEntity();
 * entity.setId(1L);
 * entity.setName("张三");
 * entity.setPassword("secret");
 *
 * UserDTO dto = new UserDTO();
 * BeanUtils.copyBean(dto, entity);
 * // dto现在包含entity的所有属性值
 *
 * // 示例2：拷贝时忽略敏感属性
 * UserDTO safeDto = new UserDTO();
 * BeanUtils.copyBean(safeDto, entity, "password", "salt");
 * // safeDto中password和salt字段不会被拷贝
 *
 * // 示例3：通过字段名获取属性值
 * String userName = (String) BeanUtils.getFieldValueByName("name", entity);
 * System.out.println("用户名: " + userName);  // 输出: 用户名: 张三
 *
 * // 示例4：通过字段名设置属性值
 * BeanUtils.setFieldValueByName(entity, "status", 1, Integer.class);
 * // entity.status现在被设置为1
 *
 * // 示例5：获取类的所有字段名
 * List&lt;String&gt; fieldNames = BeanUtils.getFieldNames(UserEntity.class);
 * System.out.println("字段列表: " + fieldNames);
 * // 输出: 字段列表: [id, name, password, status, createTime]
 *
 * // 示例6：Map转Bean（支持驼峰和下划线自动转换）
 * Map&lt;String, Object&gt; userMap = new HashMap&lt;&gt;();
 * userMap.put("user_id", 1L);        // 下划线命名
 * userMap.put("userName", "李四");    // 驼峰命名
 * userMap.put("create_time", "2024-01-01");
 *
 * UserEntity user = BeanUtils.mapToBean(userMap, UserEntity.class);
 * // user_id自动映射到userId
 * // userName保持原样映射
 * // create_time自动映射到createTime
 *
 * // 示例7：Map转Bean时的类型自动转换
 * Map&lt;String, Object&gt; dataMap = new HashMap&lt;&gt;();
 * dataMap.put("age", "25");          // String转Integer
 * dataMap.put("height", 175.5);      // Double保持不变
 * dataMap.put("isActive", "true");   // String转Boolean
 *
 * PersonEntity person = BeanUtils.mapToBean(dataMap, PersonEntity.class);
 * // 类型会自动转换为目标Bean的字段类型
 *
 * // 示例8：在国际化场景中的使用（实际使用案例）
 * // 参考 Translator.java:144
 * String fieldName = "description";
 * String translatedValue = translateRawString(fieldName, originalValue);
 * BeanUtils.setFieldValueByName(javaObject, fieldName, translatedValue, String.class);
 *
 * // 示例9：获取Setter方法
 * Method setNameMethod = BeanUtils.getMethod(entity, "name", String.class);
 * if (setNameMethod != null) {
 *     setNameMethod.invoke(entity, "新名称");
 * }
 * </pre>
 *
 * <p><b>注意事项：</b></p>
 * <ul>
 *   <li>copyBean方法要求源对象和目标对象有相同名称和类型的属性</li>
 *   <li>getFieldValueByName和setFieldValueByName依赖标准的getter/setter方法</li>
 *   <li>属性名必须遵循JavaBean命名规范（首字母小写的驼峰命名）</li>
 *   <li>反射操作失败时，get方法返回null，set方法静默失败</li>
 *   <li>mapToBean支持驼峰和下划线两种命名风格的自动转换</li>
 *   <li>mapToBean进行类型转换时，转换失败会跳过该字段而不抛出异常</li>
 *   <li>getFieldNames只返回当前类声明的字段，不包括父类字段</li>
 *   <li>性能考虑：反射操作相对较慢，高频调用场景建议缓存Method对象</li>
 * </ul>
 *
 * <p><b>最佳实践：</b></p>
 * <ul>
 *   <li>DTO转换：使用copyBean方法快速转换层级对象，避免手动逐个字段赋值</li>
 *   <li>敏感数据：使用带ignoreProperties参数的copyBean方法过滤敏感字段</li>
 *   <li>动态属性操作：通过配置文件或数据库配置字段名，动态设置属性值</li>
 *   <li>数据库映射：使用mapToBean处理原生SQL查询返回的Map结果集</li>
 *   <li>错误处理：调用get/set方法后检查返回值，确保操作成功</li>
 *   <li>类型转换：mapToBean已内置常用类型转换，特殊类型需要预处理Map值</li>
 * </ul>
 *
 * @author DataEase
 * @since 1.0.0
 * @see org.springframework.beans.BeanUtils
 * @see io.dataease.i18n.Translator
 * @see io.dataease.utils.TreeUtils
 */
public class BeanUtils {

    /**
     * 拷贝Bean对象属性
     * <p>
     * 将源对象的所有属性值拷贝到目标对象中。只拷贝属性名相同且类型兼容的字段。
     * 基于Spring的BeanUtils.copyProperties实现，使用浅拷贝方式。
     * </p>
     *
     * @param <T> 目标对象类型
     * @param target 目标对象，属性值将被设置到此对象
     * @param source 源对象，属性值从此对象读取
     * @return 返回目标对象（便于链式调用）
     * @throws RuntimeException 当拷贝过程发生异常时抛出
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
     * 拷贝Bean对象属性（忽略指定字段）
     * <p>
     * 将源对象的属性值拷贝到目标对象中，但忽略指定的字段。
     * 常用于过滤敏感字段（如密码）或不需要拷贝的字段（如主键ID）。
     * </p>
     *
     * @param <T> 目标对象类型
     * @param target 目标对象，属性值将被设置到此对象
     * @param source 源对象，属性值从此对象读取
     * @param ignoreProperties 需要忽略的属性名数组，这些字段不会被拷贝
     * @return 返回目标对象（便于链式调用）
     * @throws RuntimeException 当拷贝过程发生异常时抛出
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
     * 通过字段名获取Bean属性值
     * <p>
     * 使用反射机制，通过字段名动态获取Bean对象的属性值。
     * 该方法会自动根据字段名构造getter方法名（如：name -> getName）并调用。
     * </p>
     *
     * @param fieldName 字段名，必须遵循JavaBean命名规范（首字母小写的驼峰命名）
     * @param bean Bean对象实例
     * @return 字段的值；如果字段名为空、getter方法不存在或调用失败则返回null
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
     * 通过字段名设置Bean属性值
     * <p>
     * 使用反射机制，通过字段名动态设置Bean对象的属性值。
     * 该方法会自动根据字段名构造setter方法名（如：name -> setName）并调用。
     * </p>
     *
     * @param bean Bean对象实例
     * @param fieldName 字段名，必须遵循JavaBean命名规范（首字母小写的驼峰命名）
     * @param value 要设置的值
     * @param type 字段的类型（Class对象），用于定位正确的setter方法
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
     * 获取Bean的Setter方法
     * <p>
     * 通过字段名获取对应的setter方法对象，可用于后续的反射调用。
     * 该方法会自动根据字段名构造setter方法名（如：name -> setName）。
     * </p>
     *
     * @param bean Bean对象实例
     * @param fieldName 字段名，必须遵循JavaBean命名规范（首字母小写的驼峰命名）
     * @param type 字段的类型（Class对象），用于定位正确的setter方法
     * @return Method对象；如果字段名为空或方法不存在则返回null
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
     * 获取类的所有字段名列表
     * <p>
     * 使用反射获取指定类声明的所有字段名称。
     * 注意：只返回当前类声明的字段，不包括继承自父类的字段。
     * </p>
     *
     * @param clazz 目标类的Class对象
     * @return 字段名列表，顺序与类中声明顺序一致
     */
    public static List<String> getFieldNames(Class<?> clazz) {
        List<String> fieldNames = new ArrayList<>();
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            fieldNames.add(field.getName());
        }
        return fieldNames;
    }

    /**
     * 下划线命名转驼峰命名
     * <p>
     * 将下划线分隔的字符串转换为驼峰命名格式。
     * 例如：user_name -> userName, create_time -> createTime
     * </p>
     *
     * @param underscore 下划线命名的字符串
     * @return 驼峰命名格式的字符串
     */
    private static String underscoreToCamel(String underscore) {
        StringBuilder result = new StringBuilder();
        String[] parts = underscore.split("_");
        for (int i = 0; i < parts.length; i++) {
            String part = parts[i];
            if (part.isEmpty()) continue;
            if (i == 0) {
                result.append(part);
            } else {
                result.append(Character.toUpperCase(part.charAt(0)))
                        .append(part.substring(1).toLowerCase());
            }
        }
        return result.toString();
    }


    /**
     * Map转JavaBean对象
     * <p>
     * 将Map结构的数据转换为指定类型的JavaBean对象。
     * 支持驼峰和下划线两种命名风格的自动转换，支持常见数据类型的自动转换。
     * </p>
     *
     * <p><b>特性：</b></p>
     * <ul>
     *   <li>自动命名转换：user_id、userId都能映射到Bean的userId属性</li>
     *   <li>类型转换：String转Integer/Long/Double/Boolean等基本类型</li>
     *   <li>容错处理：类型转换失败或字段不存在时跳过，不影响其他字段</li>
     *   <li>只设置有值的字段：Map中value为null的字段不会覆盖Bean的默认值</li>
     * </ul>
     *
     * @param <T> 目标Bean类型
     * @param map 源Map数据，key为字段名（支持驼峰或下划线），value为字段值
     * @param clazz 目标Bean的Class对象，必须有无参构造函数
     * @return 转换后的Bean对象实例
     * @throws RuntimeException 当Bean实例化失败或转换过程发生严重错误时抛出
     */
    public static <T> T mapToBean(Map<String, Object> map, Class<T> clazz) {
        try {
            T bean = clazz.getDeclaredConstructor().newInstance();
            PropertyDescriptor[] descriptors = org.springframework.beans.BeanUtils.getPropertyDescriptors(clazz);

            for (PropertyDescriptor descriptor : descriptors) {
                String propertyName = descriptor.getName();
                if ("class".equals(propertyName)) continue;

                // 查找Map中对应的key（支持驼峰和下划线）
                Object value = findValueInMap(map, propertyName);

                // 只有当值存在且有写入方法时才设置属性
                if (value != null && descriptor.getWriteMethod() != null) {
                    try {
                        // 可选：添加类型转换逻辑来处理类型不匹配的情况
                        Object convertedValue = convertTypeIfNeeded(value, descriptor.getPropertyType());
                        descriptor.getWriteMethod().invoke(bean, convertedValue);
                    } catch (IllegalArgumentException e) {
                        // 类型不匹配时跳过该属性，而不是抛出异常
                        System.err.println("类型不匹配跳过属性: " + propertyName + ", 期望类型: " +
                                descriptor.getPropertyType() + ", 实际类型: " + value.getClass());
                    }
                }
            }
            return bean;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Map转Bean失败", e);
        }
    }

    /**
     * 类型转换（如需要）
     * <p>
     * 尝试将值转换为目标类型。支持常见的基本类型转换。
     * 如果类型已匹配或无法转换，则返回原值。
     * </p>
     *
     * @param value 原始值
     * @param targetType 目标类型
     * @return 转换后的值，转换失败时返回原值
     */
    private static Object convertTypeIfNeeded(Object value, Class<?> targetType) {
        if (value == null) {
            return null;
        }

        // 如果类型匹配，直接返回
        if (targetType.isInstance(value)) {
            return value;
        }

        // 添加常见的类型转换逻辑
        try {
            if (targetType == String.class) {
                return value.toString();
            } else if (targetType == Integer.class || targetType == int.class) {
                if (value instanceof Number) {
                    return ((Number) value).intValue();
                } else {
                    return Integer.parseInt(value.toString());
                }
            } else if (targetType == Long.class || targetType == long.class) {
                if (value instanceof Number) {
                    return ((Number) value).longValue();
                } else {
                    return Long.parseLong(value.toString());
                }
            } else if (targetType == Double.class || targetType == double.class) {
                if (value instanceof Number) {
                    return ((Number) value).doubleValue();
                } else {
                    return Double.parseDouble(value.toString());
                }
            } else if (targetType == Boolean.class || targetType == boolean.class) {
                if (value instanceof Boolean) {
                    return value;
                } else {
                    return Boolean.parseBoolean(value.toString());
                }
            }
            // 可以继续添加其他类型的转换...
        } catch (Exception e) {
            // 转换失败时返回原值，让后续逻辑处理
            System.err.println("类型转换失败: " + value + " to " + targetType);
        }

        return value; // 无法转换时返回原值
    }

    /**
     * 在Map中查找属性值（支持命名风格转换）
     * <p>
     * 根据属性名在Map中查找对应的值，支持驼峰和下划线命名的自动转换。
     * 查找顺序：1.直接匹配 2.驼峰转下划线匹配 3.遍历Map将key转驼峰后匹配
     * </p>
     *
     * @param map 源Map数据
     * @param propertyName Bean属性名（驼峰格式）
     * @return 找到的值，未找到则返回null
     */
    private static Object findValueInMap(Map<String, Object> map, String propertyName) {
        Object value = map.get(propertyName);
        if (value != null) return value;

        String underscore = camelToUnderscore(propertyName);
        value = map.get(underscore);
        if (value != null) return value;

        for (Map.Entry<String, Object> entry : map.entrySet()) {
            String camelKey = underscoreToCamel(entry.getKey());
            if (propertyName.equals(camelKey)) {
                return entry.getValue();
            }
        }

        return null;
    }

    /**
     * 驼峰命名转下划线命名
     * <p>
     * 将驼峰命名格式的字符串转换为下划线分隔的格式。
     * 例如：userName -> user_name, createTime -> create_time
     * </p>
     *
     * @param camel 驼峰命名的字符串
     * @return 下划线命名格式的字符串
     */
    private static String camelToUnderscore(String camel) {
        return camel.replaceAll("([a-z])([A-Z])", "$1_$2").toLowerCase();
    }
}
