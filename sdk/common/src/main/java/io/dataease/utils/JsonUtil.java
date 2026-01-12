package io.dataease.utils;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.StreamReadConstraints;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.ObjectUtils;

import java.util.Collections;
import java.util.List;

/**
 * JSON工具类
 * <p>
 * 基于Jackson提供JSON序列化和反序列化功能，统一全局JSON处理行为
 * </p>
 *
 * <p><b>核心特性：</b></p>
 * <ul>
 *   <li>支持超大字符串处理（最大50MB）- 适用于大数据集、复杂图表配置等场景</li>
 *   <li>忽略未知属性 - 提高接口兼容性，避免版本升级时的反序列化错误</li>
 *   <li>统一异常处理 - 所有JSON处理异常统一记录日志并返回null</li>
 * </ul>
 *
 * <p><b>使用位置：</b></p>
 * <ul>
 *   <li>数据集配置的序列化/反序列化</li>
 *   <li>图表配置的JSON存储和读取</li>
 *   <li>仪表板布局信息的持久化</li>
 *   <li>API请求/响应的JSON处理</li>
 *   <li>缓存数据的序列化</li>
 *   <li>消息队列数据的格式化</li>
 * </ul>
 *
 * @author DataEase
 * @since 1.0.0
 */
public class JsonUtil {

    /**
     * Jackson ObjectMapper实例，全局单例
     * 预配置了适合DataEase业务场景的序列化行为
     */
    private static final ObjectMapper objectMapper;

    /**
     * 静态初始化块，配置ObjectMapper的全局行为
     */
    static {
        objectMapper = new ObjectMapper();

        // 配置更大的StreamReadConstraints限制
        // 默认限制为5MB，此处扩大到50MB以支持超大JSON处理
        // 使用场景：大数据集配置、复杂仪表板布局、批量数据导入等
        objectMapper.getFactory().setStreamReadConstraints(
                StreamReadConstraints.builder()
                        .maxStringLength(50000000) // 50MB最大字符串长度
                        .build()
        );

        // 忽略JSON中存在但Java对象中不存在的属性
        // 好处：提高接口兼容性，避免因字段增减导致的反序列化失败
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    /**
     * 解析JSON字符串为泛型对象（使用TypeReference）
     * <p>
     * 注意：此方法使用了空的TypeReference，可能无法正确解析，建议使用parseObject方法
     * </p>
     *
     * @param json     JSON字符串
     * @param classOfT 目标类型的Class对象（实际未使用）
     * @param <T>      目标类型
     * @return 解析后的对象，解析失败返回null
     */
    public static <T> T parse(String json, Class<T> classOfT) {
        T t = null;
        try {
            // 使用空TypeReference，泛型信息会丢失
            t = objectMapper.readValue(json, new TypeReference<T>() {
            });
        } catch (JsonProcessingException e) {
            // 记录错误日志，不抛出异常，返回null
            LogUtil.error(e.getMessage(), e);
        }
        return t;
    }

    /**
     * 解析JSON字符串为指定类型的对象
     * <p>
     * 最常用的JSON反序列化方法，适用于简单对象和POJO
     * </p>
     *
     * <p><b>使用示例：</b></p>
     * <pre>
     * // 解析用户对象
     * User user = JsonUtil.parseObject(jsonString, User.class);
     *
     * // 解析图表配置
     * ChartConfig config = JsonUtil.parseObject(configJson, ChartConfig.class);
     * </pre>
     *
     * @param json     JSON字符串，null则返回null
     * @param classOfT 目标类型的Class对象
     * @param <T>      目标类型
     * @return 解析后的对象，解析失败或json为null时返回null
     */
    public static <T> T parseObject(String json, Class<T> classOfT) {
        // 参数校验，null直接返回
        if (json == null) return null;
        T t = null;
        try {
            // 使用Class对象进行反序列化
            t = objectMapper.readValue(json, classOfT);
        } catch (JsonProcessingException e) {
            // 记录错误日志，不抛出异常，返回null
            LogUtil.error(e.getMessage(), e);
        }
        return t;
    }

    /**
     * 解析JSON字符串为复杂类型对象（使用TypeReference）
     * <p>
     * 适用于泛型集合、Map等复杂类型的反序列化
     * TypeReference可以保留完整的泛型信息
     * </p>
     *
     * <p><b>使用示例：</b></p>
     * <pre>
     * // 解析Map
     * Map&lt;String, Object&gt; map = JsonUtil.parseObject(
     *     jsonString,
     *     new TypeReference&lt;Map&lt;String, Object&gt;&gt;() {}
     * );
     *
     * // 解析嵌套对象
     * Response&lt;User&gt; response = JsonUtil.parseObject(
     *     jsonString,
     *     new TypeReference&lt;Response&lt;User&gt;&gt;() {}
     * );
     * </pre>
     *
     * @param json          JSON字符串，null则返回null
     * @param typeReference 类型引用，包含完整的泛型信息
     * @param <T>           目标类型
     * @return 解析后的对象，解析失败或json为null时返回null
     */
    public static <T> T parseObject(String json, TypeReference<T> typeReference) {
        // 参数校验，null直接返回
        if (json == null) return null;
        T t = null;
        try {
            // 使用TypeReference进行反序列化，保留泛型信息
            t = objectMapper.readValue(json, typeReference);
        } catch (JsonProcessingException e) {
            // 记录错误日志，不抛出异常，返回null
            LogUtil.error(e.getMessage(), e);
        }
        return t;
    }

    /**
     * 解析JSON字符串为List集合
     * <p>
     * 专门用于解析JSON数组为Java List
     * </p>
     *
     * <p><b>使用示例：</b></p>
     * <pre>
     * // 解析用户列表
     * List&lt;User&gt; users = JsonUtil.parseList(
     *     jsonString,
     *     new TypeReference&lt;List&lt;User&gt;&gt;() {}
     * );
     *
     * // 解析数据集列表
     * List&lt;Dataset&gt; datasets = JsonUtil.parseList(
     *     jsonString,
     *     new TypeReference&lt;List&lt;Dataset&gt;&gt;() {}
     * );
     * </pre>
     *
     * @param json     JSON数组字符串，为空则返回空列表
     * @param classOfT 类型引用，必须是List类型
     * @param <T>      List元素的类型
     * @return 解析后的List，解析失败或json为空时返回空列表
     */
    public static <T> List<T> parseList(String json, TypeReference<List<T>> classOfT) {
        // 参数校验，空值返回空列表（而非null，避免NPE）
        if (ObjectUtils.isEmpty(json)) return Collections.emptyList();
        List<T> t = null;
        try {
            // 使用TypeReference进行List反序列化
            t = objectMapper.readValue(json, classOfT);
        } catch (JsonProcessingException e) {
            // 记录错误日志，不抛出异常，返回null
            LogUtil.error(e.getMessage(), e);
        }
        return t;
    }

    /**
     * 将Java对象序列化为JSON字符串
     * <p>
     * 支持所有可序列化的Java对象，包括POJO、集合、Map等
     * </p>
     *
     * <p><b>使用示例：</b></p>
     * <pre>
     * // 序列化用户对象
     * String json = (String) JsonUtil.toJSONString(user);
     *
     * // 序列化图表配置
     * String configJson = (String) JsonUtil.toJSONString(chartConfig);
     *
     * // 序列化集合
     * String listJson = (String) JsonUtil.toJSONString(userList);
     * </pre>
     *
     * @param o 待序列化的Java对象
     * @return JSON字符串，序列化失败返回null
     */
    public static Object toJSONString(Object o) {
        try {
            // 将对象转换为JSON字符串
            return objectMapper.writeValueAsString(o);
        } catch (JsonProcessingException e) {
            // 记录错误日志，不抛出异常，返回null
            LogUtil.error(e.getMessage(), e);
            return null;
        }
    }

}
