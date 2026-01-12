package io.dataease.i18n;

import com.baomidou.mybatisplus.core.metadata.IPage;
import io.dataease.exception.DEException;
import io.dataease.utils.BeanUtils;
import io.dataease.utils.JsonUtil;
import io.dataease.utils.LogUtil;
import jakarta.annotation.Resource;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;

/**
 * 国际化翻译器
 * 提供统一的国际化翻译功能，支持多种数据类型的自动翻译
 *
 * 主要功能：
 * 1. 单个key的国际化翻译
 * 2. 带占位符的消息翻译
 * 3. 复杂对象的递归翻译
 * 4. JSON字符串的自动解析和翻译
 *
 * 支持的数据类型：
 * - String（包括JSON格式）
 * - Map
 * - Collection
 * - Array
 * - 自定义对象（io.dataease包下）
 * - MyBatis Plus分页对象
 */
@Component
public class Translator {

    /**
     * JSON识别符号
     * 用于判断字符串是否为JSON格式
     */
    private static final String JSON_SYMBOL = "\":";

    /**
     * 忽略翻译的字段键名集合
     * 包含敏感信息或不需要翻译的字段名
     */
    private static final HashSet<String> IGNORE_KEYS = new HashSet<>(Arrays.asList("id", "password", "passwd"));

    /**
     * Spring消息源，用于获取国际化消息
     */
    private static MessageSource messageSource;

    /**
     * 设置消息源
     * 通过Spring依赖注入设置消息源实例
     *
     * @param messageSource Spring消息源
     */
    @Resource
    public void setMessageSource(MessageSource messageSource) {
        Translator.messageSource = messageSource;
    }

    /**
     * 单Key翻译
     * 根据国际化key获取对应语言的消息文本
     *
     * @param key 国际化key
     * @return 翻译后的消息，如果找不到则返回原key
     */
    public static String get(String key) {
        return messageSource.getMessage(key, null, key, LocaleContextHolder.getLocale());
    }

    /**
     * 获取国际化消息并替换占位符
     * 支持参数化的国际化消息翻译
     *
     * @param key          国际化键 如：确定删除名为{0}的{1}吗？
     * @param placeholders 占位值数组
     * @return 替换后的消息
     */
    public static String get(String key, Object... placeholders) {
        return messageSource.getMessage(key, placeholders, key, LocaleContextHolder.getLocale());
    }

    /**
     * 翻译原始字符串
     * 对单个字符串进行国际化翻译处理
     *
     * @param key       字段键名，用于判断是否需要忽略翻译
     * @param rawString 原始字符串
     * @return 翻译后的字符串，如果不需要翻译则返回原字符串
     */
    private static Object translateRawString(String key, String rawString) {
        // 空字符串直接返回
        if (StringUtils.isBlank(rawString)) {
            return rawString;
        }

        // 检查是否为忽略翻译的字段
        for (String ignoreKey : IGNORE_KEYS) {
            if (StringUtils.containsIgnoreCase(key, ignoreKey)) {
                return rawString;
            }
        }

        // 尝试翻译
        if (key != null) {
            String desc = get(rawString);
            if (StringUtils.isNotBlank(desc)) {
                return desc;
            }
        }
        return rawString;
    }

    /**
     * 翻译复杂对象
     * 递归遍历对象的所有属性，对字符串类型的属性进行国际化翻译
     * 支持多种数据类型的自动识别和处理
     *
     * @param javaObject 需要翻译的对象
     * @return 翻译后的对象
     */
    public static Object translateObject(Object javaObject) {
        if (javaObject == null) {
            return null;
        }
        try {
            // 处理字符串类型
            if (javaObject instanceof String) {
                String rawString = javaObject.toString();
                // 判断是否为JSON格式的字符串
                if (StringUtils.contains(rawString, JSON_SYMBOL)) {
                    try {
                        // 解析JSON并递归翻译
                        Object jsonObject = JsonUtil.parse(rawString, Object.class);
                        return JsonUtil.toJSONString(translateObject(jsonObject));
                    } catch (Exception e) {
                        LogUtil.error("Failed to translate object: " + rawString, e);
                        e.printStackTrace();
                        LogUtil.warn("Failed to translate object " + rawString + ". Error: " + ExceptionUtils.getStackTrace(e));
                        // JSON解析失败，按普通字符串处理
                        return translateRawString(null, rawString);
                    }
                } else {
                    // 普通字符串直接翻译
                    return translateRawString(null, rawString);
                }
            }

            // 处理Map类型
            if (javaObject instanceof Map) {
                Map<Object, Object> map = (Map<Object, Object>) javaObject;
                for (Map.Entry<Object, Object> entry : map.entrySet()) {
                    if (entry.getValue() != null) {
                        if (entry.getValue() instanceof String) {
                            // Map中的字符串值需要翻译
                            if (StringUtils.contains(entry.getValue().toString(), JSON_SYMBOL)) {
                                map.put(entry.getKey(), translateObject(entry.getValue()));
                            } else {
                                map.put(entry.getKey(), translateRawString(entry.getKey().toString(), entry.getValue().toString()));
                            }
                        } else {
                            // 递归处理非字符串值
                            translateObject(entry.getValue());
                        }
                    }
                }
            }

            // 处理集合类型
            if (javaObject instanceof Collection) {
                Collection<Object> collection = (Collection<Object>) javaObject;
                for (Object item : collection) {
                    // 递归翻译集合中的每个元素
                    translateObject(item);
                }
            }

            // 处理MyBatis Plus分页对象
            if (javaObject instanceof IPage) {
                IPage iPage = (IPage) javaObject;
                // 翻译分页数据中的记录列表
                translateObject(iPage.getRecords());
            }

            // 处理数组类型
            if (javaObject.getClass().isArray()) {
                for (int i = 0; i < Array.getLength(javaObject); ++i) {
                    Object item = Array.get(javaObject, i);
                    // 递归翻译数组元素并更新
                    Array.set(javaObject, i, translateObject(item));
                }
            }

            // 处理自定义对象（io.dataease包下的类）
            Class<?> objectClass = javaObject.getClass();
            String packageName = objectClass.getPackageName();
            if (StringUtils.startsWith(packageName, "io.dataease")) {
                try {
                    // 获取所有声明的字段
                    Field[] declaredFields = objectClass.getDeclaredFields();
                    for (Field field : declaredFields) {
                        field.setAccessible(true);
                        Object v = field.get(javaObject);
                        if (ObjectUtils.isEmpty(v)) continue;

                        if (field.getType() == String.class) {
                            // 处理字符串字段
                            String fieldName = field.getName();
                            if (StringUtils.contains(v.toString(), JSON_SYMBOL)) {
                                // JSON格式的字符串字段
                                BeanUtils.setFieldValueByName(javaObject, fieldName, translateObject(v), String.class);
                            } else {
                                // 普通字符串字段
                                BeanUtils.setFieldValueByName(javaObject, fieldName, translateRawString(fieldName, v.toString()), String.class);
                            }
                        } else {
                            // 递归处理非字符串字段
                            translateObject(v);
                        }
                    }
                } catch (Exception e) {
                    LogUtil.error(e.getMessage());
                    DEException.throwException(e);
                }
            }
            return javaObject;
        } catch (StackOverflowError stackOverflowError) {
            // 防止递归栈溢出，直接返回原对象
            return javaObject;
        }
    }
}
