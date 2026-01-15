package io.dataease.utils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 集合操作增强工具类
 * <p>
 * 提供集合操作的便捷方法，基于Java 8 Stream API实现，简化常见的集合处理操作。
 * 主要用于数据分组、转换等场景，减少样板代码，提高代码可读性。
 * </p>
 *
 * <p><b>核心功能：</b></p>
 * <ul>
 *   <li>集合分组 - 按指定属性将List分组为Map</li>
 *   <li>空值安全 - 自动过滤null键和空集合</li>
 *   <li>函数式操作 - 支持Lambda表达式和方法引用</li>
 * </ul>
 *
 * <p><b>应用场景：</b></p>
 * <ul>
 *   <li>数据分组 - 将列表数据按某个属性分组</li>
 *   <li>关联查询 - 数据库查询结果的内存分组</li>
 *   <li>数据聚合 - 按维度聚合数据</li>
 *   <li>报表统计 - 多维度数据统计</li>
 * </ul>
 *
 * <p><b>使用位置：</b></p>
 * <ul>
 *   <li>{@link io.dataease.visualization.server.DataVisualizationServer} - 可视化数据处理，按数据集分组数据表和字段</li>
 * </ul>
 *
 * <p><b>使用示例：</b></p>
 * <pre>
 * // 示例1：按部门分组员工（基本用法）
 * List<Employee> employees = Arrays.asList(
 *     new Employee(1L, "张三", "研发部"),
 *     new Employee(2L, "李四", "研发部"),
 *     new Employee(3L, "王五", "销售部")
 * );
 *
 * Map<String, List<Employee>> groupByDept =
 *     DeCollectionUtils.groupBy(employees, Employee::getDepartment);
 * // 结果: {"研发部": [张三, 李四], "销售部": [王五]}
 *
 * // 示例2：按ID分组数据（使用方法引用）
 * List<User> users = userService.findAll();
 * Map<Long, List<User>> groupById =
 *     DeCollectionUtils.groupBy(users, User::getId);
 *
 * // 示例3：按多级属性分组
 * List<Order> orders = orderService.findAll();
 * Map<Long, List<Order>> groupByUserId =
 *     DeCollectionUtils.groupBy(orders, order -> order.getUser().getId());
 *
 * // 示例4：处理空值和空集合
 * List<Product> products = null;
 * Map<Long, List<Product>> result =
 *     DeCollectionUtils.groupBy(products, Product::getCategoryId);
 * // 返回空Map: {}
 *
 * List<Product> emptyList = new ArrayList<>();
 * result = DeCollectionUtils.groupBy(emptyList, Product::getCategoryId);
 * // 返回空Map: {}
 *
 * // 示例5：实际使用场景 - 数据可视化（参考 DataVisualizationServer.java:247-253）
 * // 将数据表按数据集分组
 * Map<Long, List<AppCoreDatasetTableVO>> sourceDatasetTableMap =
 *     DeCollectionUtils.groupBy(sourceDatasetTableList,
 *                               AppCoreDatasetTableVO::getDatasetGroupId);
 *
 * // 将字段按数据表分组
 * Map<Long, List<AppCoreDatasetTableFieldVO>> sourceDatasetTableFieldMap =
 *     DeCollectionUtils.groupBy(sourceDatasetTableFieldList,
 *                               AppCoreDatasetTableFieldVO::getDatasetTableId);
 *
 * // 将字段按数据集分组
 * Map<Long, List<AppCoreDatasetTableFieldVO>> sourceDatasetTableFieldMapGroup =
 *     DeCollectionUtils.groupBy(sourceDatasetTableFieldList,
 *                               AppCoreDatasetTableFieldVO::getDatasetGroupId);
 *
 * // 示例6：组合使用 - 多级分组
 * // 先按部门分组，再统计每个部门的人数
 * Map<String, List<Employee>> groupByDept2 =
 *     DeCollectionUtils.groupBy(employees, Employee::getDepartment);
 *
 * Map<String, Long> countByDept = groupByDept2.entrySet().stream()
 *     .collect(Collectors.toMap(
 *         Map.Entry::getKey,
 *         e -> (long) e.getValue().size()
 *     ));
 *
 * // 示例7：过滤后分组
 * // 只分组在职员工
 * Map<String, List<Employee>> activeByDept =
 *     DeCollectionUtils.groupBy(
 *         employees.stream()
 *             .filter(Employee::isActive)
 *             .collect(Collectors.toList()),
 *         Employee::getDepartment
 *     );
 * </pre>
 *
 * <p><b>注意事项：</b></p>
 * <ul>
 *   <li>输入List为null或空时返回空HashMap，不会抛出异常</li>
 *   <li>自动过滤掉键为null的元素（keyExtractor返回null的元素）</li>
 *   <li>keyExtractor函数不应抛出异常，否则整个分组失败</li>
 *   <li>返回的Map是可变的HashMap，可以继续修改</li>
 *   <li>不会修改原始List，返回新的Map</li>
 *   <li>对于大数据量，注意内存占用</li>
 * </ul>
 *
 * <p><b>最佳实践：</b></p>
 * <ul>
 *   <li>优先使用方法引用而非Lambda表达式，代码更简洁</li>
 *   <li>keyExtractor应该是幂等的（同一对象多次调用返回相同结果）</li>
 *   <li>对于复杂的分组键，考虑在对象中添加专门的getter方法</li>
 *   <li>如果需要保持插入顺序，可修改为返回LinkedHashMap</li>
 *   <li>分组后如果需要进一步处理，可以链式调用Stream API</li>
 * </ul>
 *
 * <p><b>性能考虑：</b></p>
 * <ul>
 *   <li>时间复杂度：O(n)，n为列表大小</li>
 *   <li>空间复杂度：O(n)，需要创建新的Map和List</li>
 *   <li>适用于中小规模数据集（万级以下）</li>
 *   <li>大数据量建议使用数据库分组或分批处理</li>
 * </ul>
 *
 * @author DataEase
 * @since 1.0.0
 * @see java.util.stream.Collectors#groupingBy(Function)
 * @see io.dataease.visualization.server.DataVisualizationServer
 */
public class DeCollectionUtils {
    /**
     * 将List按指定属性分组为Map
     * <p>
     * 使用Stream API的groupingBy收集器将List转换为Map，Map的键是通过keyExtractor提取的属性值，
     * 值是具有相同键的元素列表。自动过滤掉键为null的元素。
     * </p>
     *
     * <p><b>参数说明：</b></p>
     * <ul>
     *   <li>K - 分组键的类型，应该实现equals和hashCode方法</li>
     *   <li>V - 列表元素的类型</li>
     * </ul>
     *
     * <p><b>处理逻辑：</b></p>
     * <ol>
     *   <li>如果输入列表为null或空，直接返回空HashMap</li>
     *   <li>过滤掉keyExtractor返回null的元素</li>
     *   <li>按keyExtractor提取的键进行分组</li>
     *   <li>返回分组后的Map</li>
     * </ol>
     *
     * <p><b>使用示例：</b></p>
     * <pre>
     * // 按用户ID分组订单
     * List<Order> orders = orderMapper.selectAll();
     * Map<Long, List<Order>> ordersByUser =
     *     DeCollectionUtils.groupBy(orders, Order::getUserId);
     *
     * // 获取某个用户的所有订单
     * List<Order> userOrders = ordersByUser.get(userId);
     * if (userOrders != null) {
     *     // 处理订单
     * }
     *
     * // 按状态分组
     * Map<String, List<Order>> ordersByStatus =
     *     DeCollectionUtils.groupBy(orders, Order::getStatus);
     * </pre>
     *
     * @param <K> 分组键的类型
     * @param <V> 列表元素的类型
     * @param list 要分组的列表，可以为null或空
     * @param keyExtractor 键提取函数，从元素中提取分组键，不能为null
     * @return 分组后的Map，键为提取的属性值，值为元素列表；
     *         如果输入列表为null或空，返回空HashMap
     * @throws NullPointerException 如果keyExtractor为null
     */
    public static <K, V> Map<K, List<V>> groupBy(List<V> list,
                                                 Function<V, K> keyExtractor) {
        if (list == null || list.isEmpty()) {
            return new HashMap<>();
        }
        return list.stream()
                .filter(item -> keyExtractor.apply(item) != null)
                .collect(Collectors.groupingBy(keyExtractor));
    }
}
