package io.dataease.utils;

import org.apache.commons.lang3.StringUtils;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

/**
 * 日期时间格式化工具类
 * <p>
 * 提供时间戳与字符串之间的转换功能，基于Java 8时间API实现。
 * 本工具类专注于将长整型时间戳（毫秒）格式化为指定格式的日期时间字符串。
 * 使用系统默认时区进行时间转换，确保时间显示符合本地时区。
 * </p>
 *
 * <p><b>核心功能：</b></p>
 * <ul>
 *   <li>时间戳转字符串 - 将毫秒级时间戳转换为可读的日期时间字符串</li>
 *   <li>自定义格式 - 支持自定义日期时间格式模式</li>
 *   <li>默认格式化 - 提供常用的默认格式（yyyy-MM-dd HH:mm:ss）</li>
 *   <li>时区感知 - 自动使用系统默认时区进行转换</li>
 * </ul>
 *
 * <p><b>技术特点：</b></p>
 * <ul>
 *   <li>基于Java 8时间API（java.time包）</li>
 *   <li>线程安全 - 每次调用都创建新的DateTimeFormatter实例</li>
 *   <li>时区自动适配 - 使用ZoneId.systemDefault()获取系统时区</li>
 *   <li>毫秒精度 - 支持毫秒级时间戳转换</li>
 * </ul>
 *
 * <p><b>使用位置：</b></p>
 * <ul>
 *   <li>日志记录 - 格式化日志中的时间戳，使日志更易读</li>
 *   <li>报表导出 - 导出Excel、PDF时格式化时间字段</li>
 *   <li>前端展示 - 将数据库存储的时间戳转换为前端展示格式</li>
 *   <li>数据填报 - 参考 {@link io.dataease.extensions.datafilling.provider.ExtDDLProvider}</li>
 *   <li>审计日志 - 格式化用户操作时间</li>
 *   <li>定时任务 - 记录任务执行时间</li>
 * </ul>
 *
 * <p><b>使用示例：</b></p>
 * <pre>
 * // 示例1：使用默认格式（yyyy-MM-dd HH:mm:ss）
 * Long timestamp = System.currentTimeMillis();  // 1705046400000
 * String dateStr = DateUtils.time2String(timestamp);
 * System.out.println(dateStr);  // 输出: 2024-01-12 12:00:00
 *
 * // 示例2：自定义日期格式
 * Long timestamp = 1705046400000L;
 * String dateOnly = DateUtils.time2String(timestamp, "yyyy-MM-dd");
 * System.out.println(dateOnly);  // 输出: 2024-01-12
 *
 * String timeOnly = DateUtils.time2String(timestamp, "HH:mm:ss");
 * System.out.println(timeOnly);  // 输出: 12:00:00
 *
 * String fullFormat = DateUtils.time2String(timestamp, "yyyy年MM月dd日 HH时mm分ss秒");
 * System.out.println(fullFormat);  // 输出: 2024年01月12日 12时00分00秒
 *
 * // 示例3：格式化数据库记录的创建时间
 * Dataset dataset = datasetMapper.selectById(datasetId);
 * String createTimeStr = DateUtils.time2String(dataset.getCreateTime());
 * log.info("数据集创建时间: {}", createTimeStr);
 * // 输出: 数据集创建时间: 2024-01-12 12:00:00
 *
 * // 示例4：导出报表时格式化时间列
 * List&lt;Map&lt;String, Object&gt;&gt; exportData = new ArrayList&lt;&gt;();
 * for (Chart chart : charts) {
 *     Map&lt;String, Object&gt; row = new HashMap&lt;&gt;();
 *     row.put("图表名称", chart.getName());
 *     row.put("创建时间", DateUtils.time2String(chart.getCreateTime()));
 *     row.put("更新时间", DateUtils.time2String(chart.getUpdateTime()));
 *     exportData.add(row);
 * }
 * // 导出到Excel...
 *
 * // 示例5：格式化日志时间戳
 * Long operateTime = operateLog.getTime();
 * String formattedTime = DateUtils.time2String(operateTime, "yyyy-MM-dd HH:mm:ss.SSS");
 * System.out.println("操作时间: " + formattedTime);
 * // 输出: 操作时间: 2024-01-12 12:00:00.123
 *
 * // 示例6：API响应中的时间格式化
 * {@literal @}GetMapping("/dataset/{id}")
 * public DatasetVO getDataset(@PathVariable Long id) {
 *     Dataset dataset = datasetService.getById(id);
 *     DatasetVO vo = new DatasetVO();
 *     BeanUtils.copyProperties(dataset, vo);
 *     // 将时间戳转换为字符串返回给前端
 *     vo.setCreateTimeStr(DateUtils.time2String(dataset.getCreateTime()));
 *     vo.setUpdateTimeStr(DateUtils.time2String(dataset.getUpdateTime()));
 *     return vo;
 * }
 *
 * // 示例7：不同格式的应用场景
 * Long now = System.currentTimeMillis();
 * String iso8601 = DateUtils.time2String(now, "yyyy-MM-dd'T'HH:mm:ss");  // ISO 8601格式
 * String compact = DateUtils.time2String(now, "yyyyMMddHHmmss");  // 紧凑格式，用于文件名
 * String readable = DateUtils.time2String(now, "MM月dd日 HH:mm");  // 简洁可读格式
 * </pre>
 *
 * <p><b>常用日期格式模式：</b></p>
 * <table border="1">
 *   <tr>
 *     <th>格式模式</th>
 *     <th>说明</th>
 *     <th>示例输出</th>
 *   </tr>
 *   <tr>
 *     <td>yyyy-MM-dd HH:mm:ss</td>
 *     <td>标准日期时间格式（默认）</td>
 *     <td>2024-01-12 12:00:00</td>
 *   </tr>
 *   <tr>
 *     <td>yyyy-MM-dd</td>
 *     <td>仅日期</td>
 *     <td>2024-01-12</td>
 *   </tr>
 *   <tr>
 *     <td>HH:mm:ss</td>
 *     <td>仅时间</td>
 *     <td>12:00:00</td>
 *   </tr>
 *   <tr>
 *     <td>yyyy-MM-dd HH:mm:ss.SSS</td>
 *     <td>包含毫秒</td>
 *     <td>2024-01-12 12:00:00.123</td>
 *   </tr>
 *   <tr>
 *     <td>yyyyMMddHHmmss</td>
 *     <td>紧凑格式（无分隔符）</td>
 *     <td>20240112120000</td>
 *   </tr>
 *   <tr>
 *     <td>yyyy/MM/dd HH:mm</td>
 *     <td>斜杠分隔（无秒）</td>
 *     <td>2024/01/12 12:00</td>
 *   </tr>
 *   <tr>
 *     <td>yyyy年MM月dd日</td>
 *     <td>中文格式</td>
 *     <td>2024年01月12日</td>
 *   </tr>
 * </table>
 *
 * <p><b>注意事项：</b></p>
 * <ul>
 *   <li>时间戳必须是毫秒级（13位），不支持秒级（10位）时间戳</li>
 *   <li>使用系统默认时区，跨时区应用需要注意时区转换</li>
 *   <li>格式模式为空时自动使用默认格式（yyyy-MM-dd HH:mm:ss）</li>
 *   <li>DateTimeFormatter是线程安全的，但每次调用都会创建新实例</li>
 *   <li>时间戳为null会抛出NullPointerException，调用前需要判空</li>
 *   <li>无效的格式模式会抛出IllegalArgumentException</li>
 *   <li>格式化后的字符串长度取决于格式模式，存储时需预留足够空间</li>
 * </ul>
 *
 * <p><b>最佳实践：</b></p>
 * <ul>
 *   <li>数据库存储时间戳，展示时使用本工具格式化</li>
 *   <li>定义常量保存常用格式模式，避免字符串硬编码</li>
 *   <li>跨时区应用应明确指定时区，而不是依赖系统默认时区</li>
 *   <li>前端展示时考虑国际化，根据用户语言选择日期格式</li>
 *   <li>日志记录建议包含毫秒，便于问题排查</li>
 *   <li>文件名中使用紧凑格式（yyyyMMddHHmmss），避免特殊字符</li>
 *   <li>配合 {@link CalendarUtils} 进行日期计算</li>
 * </ul>
 *
 * <p><b>相关工具类：</b></p>
 * <ul>
 *   <li>{@link CalendarUtils} - 日历和时间范围计算</li>
 *   <li>{@link io.dataease.constant.SQLConstants#DEFAULT_DATE_FORMAT} - SQL日期格式常量</li>
 *   <li>{@link io.dataease.extensions.datafilling.provider.ExtDDLProvider#DEFAULT_DATE_FORMAT_STR} - 数据填报日期格式</li>
 * </ul>
 *
 * @author DataEase
 * @since 1.0.0
 * @see CalendarUtils
 * @see java.time.format.DateTimeFormatter
 * @see java.time.Instant
 */
public class DateUtils {

    /**
     * 默认日期时间格式模式：yyyy-MM-dd HH:mm:ss
     */
    private static final String DEFAULT_PATTERN = "yyyy-MM-dd HH:mm:ss";

    /**
     * 将时间戳转换为指定格式的日期时间字符串
     * <p>
     * 使用自定义的日期时间格式模式将毫秒级时间戳转换为字符串。
     * 转换过程中使用系统默认时区，确保时间显示符合本地时区。
     * 如果格式模式为空或null，将自动使用默认格式（yyyy-MM-dd HH:mm:ss）。
     * </p>
     *
     * <p><b>格式模式说明：</b></p>
     * <ul>
     *   <li>yyyy - 四位年份（如2024）</li>
     *   <li>MM - 两位月份（01-12）</li>
     *   <li>dd - 两位日期（01-31）</li>
     *   <li>HH - 24小时制小时（00-23）</li>
     *   <li>mm - 两位分钟（00-59）</li>
     *   <li>ss - 两位秒数（00-59）</li>
     *   <li>SSS - 三位毫秒（000-999）</li>
     * </ul>
     *
     * <p><b>时区说明：</b></p>
     * <p>
     * 本方法使用 {@code ZoneId.systemDefault()} 获取系统默认时区。
     * 对于跨时区部署的应用，应确保服务器时区设置正确，或考虑使用明确指定时区的转换方法。
     * </p>
     *
     * <p><b>使用示例：</b></p>
     * <pre>
     * // 标准格式
     * String datetime = DateUtils.time2String(1705046400000L, "yyyy-MM-dd HH:mm:ss");
     * // 输出: 2024-01-12 12:00:00
     *
     * // 仅日期
     * String date = DateUtils.time2String(1705046400000L, "yyyy-MM-dd");
     * // 输出: 2024-01-12
     *
     * // 紧凑格式（用于文件名）
     * String compact = DateUtils.time2String(1705046400000L, "yyyyMMddHHmmss");
     * // 输出: 20240112120000
     *
     * // 中文格式
     * String chinese = DateUtils.time2String(1705046400000L, "yyyy年MM月dd日 HH时mm分");
     * // 输出: 2024年01月12日 12时00分
     *
     * // 格式模式为空，使用默认格式
     * String defaultFormat = DateUtils.time2String(1705046400000L, null);
     * // 输出: 2024-01-12 12:00:00
     * </pre>
     *
     * @param time 毫秒级时间戳（13位长整型），表示从1970-01-01 00:00:00 UTC到指定时间的毫秒数
     * @param pattern 日期时间格式模式（如"yyyy-MM-dd HH:mm:ss"），为空或null时使用默认格式
     * @return 格式化后的日期时间字符串
     * @throws NullPointerException 当time参数为null时抛出
     * @throws IllegalArgumentException 当pattern格式不正确时抛出
     * @throws java.time.DateTimeException 当时间戳无法转换为LocalDateTime时抛出
     * @see DateTimeFormatter#ofPattern(String)
     */
    public static String time2String(Long time, String pattern) {
        if (StringUtils.isBlank(pattern)) pattern = DEFAULT_PATTERN;
        DateTimeFormatter format = DateTimeFormatter.ofPattern(pattern);
        ZoneId zoneId = ZoneId.systemDefault();
        LocalDateTime timeByMilli = Instant.ofEpochMilli(time).atZone(zoneId).toLocalDateTime();
        return format.format(timeByMilli);
    }

    /**
     * 将时间戳转换为默认格式的日期时间字符串
     * <p>
     * 这是一个便捷方法，使用默认格式（yyyy-MM-dd HH:mm:ss）将毫秒级时间戳转换为字符串。
     * 等价于调用 {@code time2String(time, "yyyy-MM-dd HH:mm:ss")}。
     * 这是最常用的时间格式化方法，适用于大多数日志记录和展示场景。
     * </p>
     *
     * <p><b>默认格式说明：</b></p>
     * <p>
     * 默认格式 {@code yyyy-MM-dd HH:mm:ss} 是标准的日期时间格式，包含：
     * </p>
     * <ul>
     *   <li>完整的年月日（yyyy-MM-dd）</li>
     *   <li>24小时制的时分秒（HH:mm:ss）</li>
     *   <li>中间用空格分隔</li>
     *   <li>符合国际标准和数据库标准</li>
     * </ul>
     *
     * <p><b>使用场景：</b></p>
     * <ul>
     *   <li>日志记录 - 记录操作时间、错误时间等</li>
     *   <li>数据展示 - 前端展示创建时间、更新时间等</li>
     *   <li>报表导出 - 导出Excel、PDF时显示时间</li>
     *   <li>审计记录 - 记录用户操作时间</li>
     * </ul>
     *
     * <p><b>使用示例：</b></p>
     * <pre>
     * // 示例1：格式化当前时间
     * Long now = System.currentTimeMillis();
     * String nowStr = DateUtils.time2String(now);
     * System.out.println("当前时间: " + nowStr);
     * // 输出: 当前时间: 2024-01-12 12:00:00
     *
     * // 示例2：格式化数据库记录时间
     * User user = userMapper.selectById(userId);
     * String createTime = DateUtils.time2String(user.getCreateTime());
     * String updateTime = DateUtils.time2String(user.getUpdateTime());
     * log.info("用户创建时间: {}, 更新时间: {}", createTime, updateTime);
     *
     * // 示例3：在VO对象中使用
     * {@literal @}Data
     * public class ChartVO {
     *     private Long id;
     *     private String name;
     *     private Long createTime;  // 数据库存储的时间戳
     *
     *     // 转换为字符串供前端展示
     *     public String getCreateTimeStr() {
     *         return DateUtils.time2String(createTime);
     *     }
     * }
     *
     * // 示例4：日志中使用
     * log.info("任务开始执行，时间: {}", DateUtils.time2String(System.currentTimeMillis()));
     * // 输出: 任务开始执行，时间: 2024-01-12 12:00:00
     *
     * // 示例5：批量格式化列表数据
     * List&lt;Dataset&gt; datasets = datasetMapper.selectList(null);
     * datasets.forEach(dataset -&gt; {
     *     String createTimeStr = DateUtils.time2String(dataset.getCreateTime());
     *     System.out.println(dataset.getName() + " 创建于: " + createTimeStr);
     * });
     * </pre>
     *
     * @param time 毫秒级时间戳（13位长整型），表示从1970-01-01 00:00:00 UTC到指定时间的毫秒数
     * @return 格式化后的日期时间字符串，格式为 yyyy-MM-dd HH:mm:ss
     * @throws NullPointerException 当time参数为null时抛出
     * @throws java.time.DateTimeException 当时间戳无法转换为LocalDateTime时抛出
     * @see #time2String(Long, String)
     */
    public static String time2String(Long time) {
        String pattern = DEFAULT_PATTERN;
        return time2String(time, pattern);
    }
}
