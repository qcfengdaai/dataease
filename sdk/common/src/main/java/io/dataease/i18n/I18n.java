package io.dataease.i18n;

import java.lang.annotation.*;

/**
 * 国际化注解
 * 用于标记需要进行国际化处理的方法
 *
 * 主要功能：
 * - 标识方法需要进行国际化翻译
 * - 可指定自定义的国际化key值
 *
 * 使用场景：
 * - API接口返回值的自动国际化
 * - 业务方法结果的多语言支持
 * - 数据导出时的字段名称翻译
 *
 * 示例：
 * @I18n("custom.message.key")
 * public String getMessage() {
 *     return "Hello World";
 * }
 */
@Documented
@Inherited
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface I18n {

    /**
     * 国际化key值
     * 用于指定特定的国际化资源key，如果为空则使用默认处理逻辑
     *
     * @return 国际化key，默认为空字符串
     */
    String value() default "";
}
