package io.dataease.utils;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Long类型数组转字符串数组的JSON序列化器
 * <p>
 * 自定义Jackson序列化器，用于在JSON序列化时将Long类型的列表转换为String类型的列表。
 * 主要解决前端JavaScript处理Long类型数据时精度丢失的问题。
 * </p>
 *
 * <p><b>核心功能：</b></p>
 * <ul>
 *   <li>类型转换 - 将List&lt;Long&gt;转换为List&lt;String&gt;</li>
 *   <li>空值过滤 - 自动过滤null值，避免序列化异常</li>
 *   <li>精度保护 - 防止JavaScript中Long类型精度丢失</li>
 *   <li>透明序列化 - 通过注解自动应用，无需手动调用</li>
 * </ul>
 *
 * <p><b>问题背景：</b></p>
 * <ul>
 *   <li>JavaScript的Number类型最大安全整数为2^53-1（9007199254740991）</li>
 *   <li>Java的Long类型范围为-2^63到2^63-1，远大于JavaScript的安全范围</li>
 *   <li>当Long值超过JavaScript安全范围时，前端会出现精度丢失</li>
 *   <li>将Long转为String可以保证数据在前后端传输时不丢失精度</li>
 * </ul>
 *
 * <p><b>使用位置：</b></p>
 * <ul>
 *   <li>{@link io.dataease.api.xpack.dataFilling.dto.TaskInfoGridVO} - 任务信息网格视图对象</li>
 *   <li>{@link io.dataease.api.xpack.dataFilling.dto.DfUserTaskData} - 数据填报用户任务数据</li>
 *   <li>{@link io.dataease.api.xpack.dataFilling.dto.DfUserTaskVo} - 数据填报用户任务视图对象</li>
 *   <li>{@link io.dataease.api.xpack.dataFilling.dto.TaskInfoVO} - 任务信息视图对象</li>
 * </ul>
 *
 * <p><b>使用示例：</b></p>
 * <pre>
 * // 示例1：在实体类中使用（通过注解应用）
 * public class TaskInfoVO {
 *     // 将Long列表序列化为String列表，防止前端精度丢失
 *     {@literal @}JsonSerialize(using = LongArray2StringSerialize.class)
 *     private List&lt;Long&gt; userIds;
 *
 *     // getter和setter...
 * }
 *
 * // 示例2：实际使用场景
 * TaskInfoVO task = new TaskInfoVO();
 * List&lt;Long&gt; userIds = Arrays.asList(
 *     123456789012345678L,  // 超过JavaScript安全整数范围
 *     987654321098765432L,
 *     null,                  // 空值会被自动过滤
 *     555555555555555555L
 * );
 * task.setUserIds(userIds);
 *
 * // 使用ObjectMapper序列化
 * ObjectMapper mapper = new ObjectMapper();
 * String json = mapper.writeValueAsString(task);
 * // 输出JSON: {"userIds":["123456789012345678","987654321098765432","555555555555555555"]}
 * // 注意：Long值已转为String，null值已被过滤
 *
 * // 示例3：前端接收（JavaScript）
 * // 前端可以安全地处理这些字符串形式的Long值
 * const response = await fetch('/api/task/info');
 * const data = await response.json();
 * console.log(data.userIds);  // ["123456789012345678", "987654321098765432", "555555555555555555"]
 * // 可以直接用于显示或作为字符串参数传递
 *
 * // 示例4：数据填报场景中的实际应用
 * // 在数据填报任务中，任务ID、用户ID等都是Long类型
 * DfUserTaskVo userTask = new DfUserTaskVo();
 * userTask.setId(123456789012345678L);
 * userTask.setTaskId(987654321098765432L);
 * userTask.setAssignBy(555555555555555555L);
 * // 序列化后，这些ID会自动转为字符串格式传给前端
 * </pre>
 *
 * <p><b>注意事项：</b></p>
 * <ul>
 *   <li>仅用于序列化List&lt;Long&gt;类型，不适用于单个Long值或其他类型</li>
 *   <li>序列化后的字符串在前端需要保持字符串类型，不应转回数字</li>
 *   <li>null值会被自动过滤，不会出现在序列化结果中</li>
 *   <li>此序列化器是单向的，仅处理Java到JSON的序列化</li>
 *   <li>如需反序列化（JSON到Java），需要单独配置反序列化器</li>
 *   <li>空列表会序列化为空JSON数组：[]</li>
 * </ul>
 *
 * <p><b>最佳实践：</b></p>
 * <ul>
 *   <li>对于所有可能超过JavaScript安全整数范围的Long列表字段，都应使用此序列化器</li>
 *   <li>与{@literal @}JsonSerialize注解配合使用，在字段级别声明</li>
 *   <li>前端应将这些字段视为字符串处理，不要转换为数字类型</li>
 *   <li>如果前端需要进行数值比较，应使用BigInt或专门的大数处理库</li>
 *   <li>建议同时使用ToStringSerializer处理单个Long字段，保持一致性</li>
 * </ul>
 *
 * <p><b>性能考虑：</b></p>
 * <ul>
 *   <li>序列化过程会创建新的ArrayList，有一定的内存开销</li>
 *   <li>对于大量数据的列表，可能会有性能影响</li>
 *   <li>过滤null值的操作是O(n)时间复杂度</li>
 * </ul>
 *
 * @author DataEase
 * @since 1.0.0
 * @see com.fasterxml.jackson.databind.JsonSerializer
 * @see com.fasterxml.jackson.databind.annotation.JsonSerialize
 * @see com.fasterxml.jackson.databind.ser.std.ToStringSerializer
 */
public class LongArray2StringSerialize extends JsonSerializer<List<Long>> {

    /**
     * 将Long类型列表序列化为String类型列表
     * <p>
     * 遍历输入的Long列表，将每个非null的Long值转换为String，
     * 然后使用Jackson的JsonGenerator输出转换后的列表。
     * </p>
     *
     * <p><b>处理流程：</b></p>
     * <ol>
     *   <li>创建新的String类型列表</li>
     *   <li>遍历输入的Long列表</li>
     *   <li>跳过null值</li>
     *   <li>将非null的Long值转换为String并添加到新列表</li>
     *   <li>使用JsonGenerator输出转换后的列表</li>
     * </ol>
     *
     * @param longs Long类型的列表，可能包含null值
     * @param jsonGenerator JSON生成器，用于输出序列化结果
     * @param serializerProvider 序列化提供者，提供序列化上下文
     * @throws IOException 当写入JSON数据时发生IO异常
     */
    @Override
    public void serialize(List<Long> longs, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        // 创建字符串列表用于存储转换后的值
        List<String> list = new ArrayList<>();

        // 遍历Long列表，将非null值转换为String
        for (Long str : longs) {
            if (str != null) {
                list.add(str.toString());
            }
        }

        // 将转换后的列表序列化为JSON
        jsonGenerator.writeObject(list);
    }
}
