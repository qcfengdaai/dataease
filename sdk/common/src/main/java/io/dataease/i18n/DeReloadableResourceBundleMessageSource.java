package io.dataease.i18n;

import org.springframework.context.support.ReloadableResourceBundleMessageSource;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * DataEase自定义的可重载资源包消息源
 * 继承Spring的ReloadableResourceBundleMessageSource，提供额外的功能定制
 *
 * 主要特性：
 * 1. 对资源基础名称集合进行排序
 * 2. 确保资源文件的加载顺序一致性
 * 3. 优化资源文件的查找性能
 *
 * 排序规则：
 * - 按照基础名称的首字符进行排序
 * - 保证每次获取的资源名称顺序一致
 */
public class DeReloadableResourceBundleMessageSource extends ReloadableResourceBundleMessageSource {

    /**
     * 获取经过排序的资源基础名称集合
     * 重写父类方法，对基础名称进行排序处理
     *
     * @return 排序后的资源基础名称集合
     */
    @Override
    public Set<String> getBasenameSet() {
        // 获取父类的基础名称集合，进行排序后返回
        return super.getBasenameSet()
                .stream()
                .sorted(this::compare)
                .collect(Collectors.toSet());
    }

    /**
     * 比较两个基础名称字符串的排序顺序
     * 根据字符串的第一个字符进行比较
     *
     * @param o1 第一个基础名称
     * @param o2 第二个基础名称
     * @return 比较结果：负数表示o1在前，正数表示o2在前，0表示相等
     */
    private int compare(String o1, String o2) {
        // 比较两个字符串的首字符，用于确定排序顺序
        return o1.substring(0, 1).compareTo(o2.substring(0, 1));
    }

}
