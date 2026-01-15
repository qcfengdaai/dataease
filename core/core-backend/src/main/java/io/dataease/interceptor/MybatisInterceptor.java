package io.dataease.interceptor;

import io.dataease.commons.utils.MybatisInterceptorConfig;
import io.dataease.utils.BeanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.cache.CacheKey;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;

import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * MyBatis拦截器
 * 用于在MyBatis执行SQL时自动进行数据加密和解密处理
 *
 * <p>主要功能：</p>
 * <ul>
 *   <li>在插入/更新数据前，对指定字段进行加密</li>
 *   <li>在查询数据后，对指定字段进行解密</li>
 *   <li>支持反射调用自定义的加密/解密方法</li>
 *   <li>使用缓存提高性能</li>
 * </ul>
 *
 * <p>配置方式：</p>
 * <p>通过Spring注入interceptorConfigList配置，指定哪些类的哪些字段需要加密/解密</p>
 */
@Intercepts({
        @Signature(type = Executor.class, method = "update", args = {MappedStatement.class, Object.class}),
        @Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class}),
        @Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class, CacheKey.class, BoundSql.class}),
})
public class MybatisInterceptor implements Interceptor {

    /** 拦截器配置列表 */
    private List<MybatisInterceptorConfig> interceptorConfigList;

    /** 类名到Class对象的缓存映射 */
    private ConcurrentHashMap<String, Class> classMap = new ConcurrentHashMap<>();
    /** 类名到拦截器配置的缓存映射 */
    private ConcurrentHashMap<String, Map<String, Map<String, MybatisInterceptorConfig>>> interceptorConfigMap = new ConcurrentHashMap<>();

    /**
     * 拦截MyBatis执行方法，进行加密和解密处理
     * @param invocation 方法调用上下文
     * @return 处理后的结果
     * @throws Throwable 处理异常
     */
    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        // 获取方法名称
        String methodName = invocation.getMethod().getName();
        // 获取方法参数
        Object parameter = invocation.getArgs()[1];
        // 如果是update操作，对参数进行加密处理
        if (parameter != null && methodName.equals("update")) {
            invocation.getArgs()[1] = process(parameter);
        }
        // 执行原方法
        Object returnValue = invocation.proceed();
        Object result = returnValue;
        // 如果返回值是列表，对列表中的每个元素进行解密
        if (returnValue instanceof ArrayList<?>) {
            List<Object> list = new ArrayList<>();
            boolean isDecrypted = false;
            for (Object val : (ArrayList<?>) returnValue) {
                Object a = undo(val);
                if (a != val) {
                    isDecrypted = true;
                    list.add(a);
                } else {
                    break;
                }
            }
            if (isDecrypted) {
                result = list;
            }
        } else {
            // 如果返回值不是列表，直接解密
            result = undo(returnValue);
        }
        return result;
    }

    /**
     * 获取对象对应的拦截器配置
     * @param p 要处理的对象
     * @return 拦截器配置映射，key为属性名，value为加密/解密配置
     */
    private Map<String, Map<String, MybatisInterceptorConfig>> getConfig(Object p) {
        Map<String, Map<String, MybatisInterceptorConfig>> result = new HashMap<>();
        if (p == null) {
            return null;
        }
        // 获取对象的类名
        String pClassName = p.getClass().getName();
        // 如果缓存中有配置，直接返回
        if (interceptorConfigMap.get(pClassName) != null) {
            return interceptorConfigMap.get(pClassName);
        }
        // 遍历所有拦截器配置，找到匹配的配置
        Map<String, List<MybatisInterceptorConfig>> m = new HashMap<>();
        for (MybatisInterceptorConfig interceptorConfig : interceptorConfigList) {
            String className = interceptorConfig.getModelName();
            String attrName = interceptorConfig.getAttrName();
            if (StringUtils.isNotBlank(className)) {
                // 从缓存中获取Class对象
                Class c = classMap.get(className);
                if (c == null) {
                    try {
                        c = Class.forName(className);
                        classMap.put(className, c);
                    } catch (ClassNotFoundException e) {
                        continue;
                    }
                }
                // 判断对象是否是该类的实例
                if (c.isInstance(p)) {
                    if (result.get(attrName) == null) {
                        result.put(attrName, new HashMap<>());
                    }
                    // 添加加密配置
                    if (StringUtils.isNotBlank(interceptorConfig.getInterceptorMethod())) {
                        result.get(attrName).put(Methods.encrypt.name(), interceptorConfig);
                    }
                    // 添加解密配置
                    if (StringUtils.isNotBlank(interceptorConfig.getInterceptorMethod())) {
                        result.get(attrName).put(Methods.decrypt.name(), interceptorConfig);
                    }
                }
            }
        }
        // 缓存配置
        interceptorConfigMap.put(pClassName, result);
        return result;
    }

    /**
     * 对对象进行加密处理
     * @param obj 要处理的对象
     * @return 加密后的对象
     * @throws Throwable 处理异常
     */
    private Object process(Object obj) throws Throwable {
        // 如果是Map类型，递归处理Map中的每个值
        if (obj instanceof Map) {
            Map paramMap = (Map) obj;
            for (Object key : paramMap.keySet()) {
                if (paramMap.get(key) != null) {
                    paramMap.put(key, process(paramMap.get(key)));
                }
            }
            return paramMap;
        }
        // 获取对象的拦截器配置
        Map<String, Map<String, MybatisInterceptorConfig>> localInterceptorConfigMap = getConfig(obj);
        if (isEmpty(localInterceptorConfigMap)) {
            return obj;
        }
        // 创建新对象并复制属性
        Object newObject = obj.getClass().newInstance();
        BeanUtils.copyBean(newObject, obj);
        // 遍历需要加密的属性
        for (String attrName : localInterceptorConfigMap.keySet()) {
            if (isEmpty(localInterceptorConfigMap.get(attrName))) {
                continue;
            }
            // 获取加密配置
            MybatisInterceptorConfig interceptorConfig = localInterceptorConfigMap.get(attrName).get(Methods.encrypt.name());
            if (interceptorConfig == null || StringUtils.isBlank(interceptorConfig.getInterceptorClass())
                    || StringUtils.isBlank(interceptorConfig.getInterceptorMethod())) {
                continue;
            }
            // 获取字段值
            Object fieldValue = BeanUtils.getFieldValueByName(interceptorConfig.getAttrName(), newObject);
            if (fieldValue != null) {
                // 通过反射调用加密方法
                Class<?> processClazz = Class.forName(interceptorConfig.getInterceptorClass());
                Method method = processClazz.getMethod(interceptorConfig.getInterceptorMethod(), Object.class);
                Object processedValue = method.invoke(null, fieldValue);
                // 设置加密后的值
                if (processedValue instanceof byte[]) {
                    BeanUtils.setFieldValueByName(newObject, interceptorConfig.getAttrName(), processedValue, byte[].class);
                } else {
                    BeanUtils.setFieldValueByName(newObject, interceptorConfig.getAttrName(), processedValue, fieldValue.getClass());
                }
            }
        }

        return newObject;
    }


    /**
     * 对对象进行解密处理
     * @param obj 要处理的对象
     * @return 解密后的对象
     * @throws Throwable 处理异常
     */
    private Object undo(Object obj) throws Throwable {
        // 获取对象的拦截器配置
        Map<String, Map<String, MybatisInterceptorConfig>> localDecryptConfigMap = getConfig(obj);
        Object result;
        if (isEmpty(localDecryptConfigMap)) {
            return obj;
        }
        // 创建新对象并复制属性
        result = obj.getClass().newInstance();
        BeanUtils.copyBean(result, obj);
        // 遍历需要解密的属性
        for (String attrName : localDecryptConfigMap.keySet()) {
            if (isEmpty(localDecryptConfigMap.get(attrName))) {
                continue;
            }
            // 获取解密配置
            MybatisInterceptorConfig interceptorConfig = localDecryptConfigMap.get(attrName).get(Methods.decrypt.name());
            if (interceptorConfig == null || StringUtils.isBlank(interceptorConfig.getUndoClass())
                    || StringUtils.isBlank(interceptorConfig.getUndoMethod())) {
                continue;
            }
            // 获取字段值
            Object fieldValue = BeanUtils.getFieldValueByName(interceptorConfig.getAttrName(), result);
            if (fieldValue != null) {
                // 通过反射调用解密方法
                Class<?> processClazz = Class.forName(interceptorConfig.getUndoClass());
                Object undoValue;
                if (fieldValue instanceof List) {
                    // 处理List类型的字段
                    Method method = processClazz.getMethod(interceptorConfig.getUndoMethod(), List.class, String.class);
                    //fieldValue获取的是list的引用，所以list类型的属性不需要再调用setFieldValueByName了
                    method.invoke(null, fieldValue, interceptorConfig.getAttrNameForList());
                } else {
                    // 处理普通类型的字段
                    Method method = processClazz.getMethod(interceptorConfig.getUndoMethod(), Object.class);
                    undoValue = method.invoke(null, fieldValue);
                    // 设置解密后的值
                    if (undoValue instanceof byte[]) {
                        BeanUtils.setFieldValueByName(result, interceptorConfig.getAttrName(), undoValue, byte[].class);
                    } else {
                        BeanUtils.setFieldValueByName(result, interceptorConfig.getAttrName(), undoValue, fieldValue.getClass());
                    }
                }
            }
        }
        return result;
    }

    /**
     * 包装目标对象
     * @param target 目标对象
     * @return 代理对象
     */
    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    /**
     * 设置属性
     * @param properties 属性配置
     */
    @Override
    public void setProperties(Properties properties) {
    }

    /**
     * 获取拦截器配置列表
     * @return 拦截器配置列表
     */
    public List<MybatisInterceptorConfig> getInterceptorConfigList() {
        return interceptorConfigList;
    }

    /**
     * 设置拦截器配置列表
     * @param interceptorConfigList 拦截器配置列表
     */
    public void setInterceptorConfigList(List<MybatisInterceptorConfig> interceptorConfigList) {
        this.interceptorConfigList = interceptorConfigList;
    }

    /**
     * 处理方法类型枚举
     */
    private enum Methods {
        /** 加密 */
        encrypt,
        /** 解密 */
        decrypt
    }

    /**
     * 判断Map是否为空
     * @param map 要判断的Map
     * @return true表示为空，false表示不为空
     */
    private boolean isEmpty(Map<?, ?> map) {
        return map == null || map.isEmpty();
    }

}
