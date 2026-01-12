/**
 * 数据填报工具类包
 * <p>
 * 本包提供数据填报模块所需的通用工具类，目前主要包含Bean操作工具。
 * </p>
 *
 * <p><b>核心工具类：</b></p>
 * <ul>
 *   <li>{@link io.dataease.extensions.datafilling.utils.BeanUtils} - Bean属性拷贝和反射操作工具类</li>
 * </ul>
 *
 * <p><b>使用场景：</b></p>
 * <ul>
 *   <li><b>对象转换</b> - DTO与Entity之间的属性拷贝</li>
 *   <li><b>反射操作</b> - 动态获取和设置对象属性值</li>
 *   <li><b>字段操作</b> - 获取类的所有字段名称</li>
 * </ul>
 *
 * <p><b>使用示例：</b></p>
 * <pre>
 * // 1. 复制对象属性
 * ExtTableField source = ...; // 源对象
 * ExtTableField target = new ExtTableField();
 * BeanUtils.copyBean(target, source);
 *
 * // 2. 复制对象属性（忽略某些字段）
 * BeanUtils.copyBean(target, source, "removed", "id");
 *
 * // 3. 通过字段名获取属性值
 * Object value = BeanUtils.getFieldValueByName("columnName", mapping);
 * System.out.println("列名: " + value);
 *
 * // 4. 通过字段名设置属性值
 * BeanUtils.setFieldValueByName(mapping, "columnName", "user_name", String.class);
 *
 * // 5. 获取类的所有字段名
 * List&lt;String&gt; fieldNames = BeanUtils.getFieldNames(ExtTableField.class);
 * fieldNames.forEach(System.out::println);
 * </pre>
 *
 * <p><b>与其他模块的关系：</b></p>
 * <ul>
 *   <li><b>dto包</b> - BeanUtils用于在DTO对象之间拷贝属性</li>
 *   <li><b>provider包</b> - DDL生成过程中可能需要动态获取对象属性</li>
 *   <li><b>plugin包</b> - 插件实现中使用BeanUtils进行对象转换</li>
 * </ul>
 *
 * <p><b>注意事项：</b></p>
 * <ul>
 *   <li>BeanUtils的方法都是静态的，无需实例化</li>
 *   <li>反射操作有性能开销，避免在循环中频繁调用</li>
 *   <li>属性拷贝要求源对象和目标对象有相同名称和类型的属性</li>
 *   <li>反射方法可能抛出异常，已在内部处理</li>
 * </ul>
 *
 * @author DataEase
 * @since 2.0.0
 * @see io.dataease.extensions.datafilling.dto
 */
package io.dataease.extensions.datafilling.utils;
