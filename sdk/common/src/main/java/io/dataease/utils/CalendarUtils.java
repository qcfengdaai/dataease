package io.dataease.utils;

import org.apache.commons.lang3.ObjectUtils;

import java.util.Calendar;

/**
 * 日历时间计算工具类
 * <p>
 * 提供基于Calendar的日期时间计算功能，主要用于日期的加减运算。
 * 本工具类封装了Java Calendar API，简化日期时间的计算操作，
 * 支持按月、日、小时等时间单位进行时间戳的增减计算。
 * </p>
 *
 * <p><b>核心功能：</b></p>
 * <ul>
 *   <li>月份计算 - 在指定时间戳上增加或减少指定月数</li>
 *   <li>自动处理 - 自动处理大小月、闰年等复杂情况</li>
 *   <li>时区感知 - 使用系统默认时区进行计算</li>
 *   <li>默认值处理 - 时间戳为空时自动使用当前时间</li>
 * </ul>
 *
 * <p><b>技术特点：</b></p>
 * <ul>
 *   <li>基于java.util.Calendar实现</li>
 *   <li>自动处理月份溢出（如1月31日加1个月 = 2月28/29日）</li>
 *   <li>支持负数运算（减少时间）</li>
 *   <li>线程安全 - 每次调用创建新的Calendar实例</li>
 *   <li>毫秒精度 - 输入输出都是毫秒级时间戳</li>
 * </ul>
 *
 * <p><b>使用位置：</b></p>
 * <ul>
 *   <li>许可证管理 - 计算许可证到期时间</li>
 *   <li>订阅服务 - 计算会员到期时间、续费时间</li>
 *   <li>数据保留策略 - 计算数据过期时间、归档时间</li>
 *   <li>定时任务 - 计算下次执行时间</li>
 *   <li>报表周期 - 计算报表生成周期（月报、季报等）</li>
 *   <li>试用期管理 - 计算试用到期时间</li>
 *   <li>账期管理 - 计算财务账期、结算周期</li>
 * </ul>
 *
 * <p><b>使用示例：</b></p>
 * <pre>
 * // 示例1：计算3个月后的时间（许可证场景）
 * Long currentTime = System.currentTimeMillis();  // 2024-01-12 12:00:00
 * Long expireTime = CalendarUtils.getTimeAfterMonth(currentTime, 3);
 * System.out.println("3个月后: " + DateUtils.time2String(expireTime));
 * // 输出: 3个月后: 2024-04-12 12:00:00
 *
 * // 示例2：计算1年后的时间（年度订阅）
 * Long subscribeTime = System.currentTimeMillis();
 * Long renewTime = CalendarUtils.getTimeAfterMonth(subscribeTime, 12);
 * System.out.println("1年后续费时间: " + DateUtils.time2String(renewTime));
 * // 输出: 1年后续费时间: 2025-01-12 12:00:00
 *
 * // 示例3：计算6个月前的时间（数据归档）
 * Long now = System.currentTimeMillis();
 * Long archiveTime = CalendarUtils.getTimeAfterMonth(now, -6);
 * System.out.println("6个月前的数据需要归档: " + DateUtils.time2String(archiveTime));
 * // 输出: 6个月前的数据需要归档: 2023-07-12 12:00:00
 *
 * // 示例4：使用null参数（自动使用当前时间）
 * Long threeMonthsLater = CalendarUtils.getTimeAfterMonth(null, 3);
 * System.out.println("从现在起3个月后: " + DateUtils.time2String(threeMonthsLater));
 * // 输出: 从现在起3个月后: 2024-04-12 12:00:00
 *
 * // 示例5：许可证到期时间计算（实际业务场景）
 * public class LicenseService {
 *     public License createLicense(String userId, int months) {
 *         License license = new License();
 *         license.setUserId(userId);
 *         license.setCreateTime(System.currentTimeMillis());
 *
 *         // 计算到期时间
 *         Long expireTime = CalendarUtils.getTimeAfterMonth(
 *             license.getCreateTime(),
 *             months
 *         );
 *         license.setExpireTime(expireTime);
 *
 *         log.info("创建许可证，用户: {}, 有效期: {}个月, 到期时间: {}",
 *             userId, months, DateUtils.time2String(expireTime));
 *
 *         return licenseMapper.insert(license);
 *     }
 *
 *     public boolean isExpired(License license) {
 *         Long now = System.currentTimeMillis();
 *         return now &gt; license.getExpireTime();
 *     }
 * }
 *
 * // 示例6：会员续费（在现有到期时间基础上延长）
 * public void renewMembership(Long userId, int months) {
 *     User user = userMapper.selectById(userId);
 *     Long currentExpireTime = user.getExpireTime();
 *
 *     // 如果已过期，从当前时间开始计算
 *     if (currentExpireTime &lt; System.currentTimeMillis()) {
 *         currentExpireTime = System.currentTimeMillis();
 *     }
 *
 *     // 在原到期时间基础上延长
 *     Long newExpireTime = CalendarUtils.getTimeAfterMonth(currentExpireTime, months);
 *     user.setExpireTime(newExpireTime);
 *     userMapper.updateById(user);
 *
 *     log.info("用户 {} 续费 {} 个月，新到期时间: {}",
 *         userId, months, DateUtils.time2String(newExpireTime));
 * }
 *
 * // 示例7：数据清理任务（删除6个月前的日志）
 * {@literal @}Scheduled(cron = "0 0 2 * * ?")  // 每天凌晨2点执行
 * public void cleanOldLogs() {
 *     // 计算6个月前的时间戳
 *     Long sixMonthsAgo = CalendarUtils.getTimeAfterMonth(null, -6);
 *
 *     // 删除6个月前的操作日志
 *     int deletedCount = operateLogMapper.deleteByTimeBefore(sixMonthsAgo);
 *     log.info("清理6个月前的日志，删除 {} 条记录", deletedCount);
 * }
 *
 * // 示例8：月报生成（计算上个月的时间范围）
 * public void generateMonthlyReport() {
 *     Long now = System.currentTimeMillis();
 *     Long lastMonthEnd = CalendarUtils.getTimeAfterMonth(now, -1);
 *
 *     // 获取上个月的数据
 *     List&lt;Data&gt; monthlyData = dataMapper.selectByTimeRange(lastMonthEnd, now);
 *     // 生成报表...
 * }
 *
 * // 示例9：试用期管理
 * public User createTrialUser(String username) {
 *     User user = new User();
 *     user.setUsername(username);
 *     user.setCreateTime(System.currentTimeMillis());
 *
 *     // 试用期1个月
 *     Long trialExpireTime = CalendarUtils.getTimeAfterMonth(null, 1);
 *     user.setTrialExpireTime(trialExpireTime);
 *     user.setUserType("TRIAL");
 *
 *     userMapper.insert(user);
 *     log.info("创建试用用户: {}, 试用到期: {}",
 *         username, DateUtils.time2String(trialExpireTime));
 *
 *     return user;
 * }
 *
 * // 示例10：处理特殊日期（月末溢出）
 * // 2024-01-31 加1个月 = 2024-02-29（闰年）或 2024-02-28（平年）
 * Long jan31 = 1706688000000L;  // 2024-01-31 00:00:00
 * Long feb = CalendarUtils.getTimeAfterMonth(jan31, 1);
 * System.out.println("1月31日加1个月: " + DateUtils.time2String(feb));
 * // 输出: 1月31日加1个月: 2024-02-29 00:00:00（2024是闰年）
 * </pre>
 *
 * <p><b>日期计算规则说明：</b></p>
 * <table border="1">
 *   <tr>
 *     <th>原始日期</th>
 *     <th>操作</th>
 *     <th>结果日期</th>
 *     <th>说明</th>
 *   </tr>
 *   <tr>
 *     <td>2024-01-15</td>
 *     <td>+3个月</td>
 *     <td>2024-04-15</td>
 *     <td>普通日期，正常计算</td>
 *   </tr>
 *   <tr>
 *     <td>2024-01-31</td>
 *     <td>+1个月</td>
 *     <td>2024-02-29</td>
 *     <td>月末溢出，自动调整到2月最后一天</td>
 *   </tr>
 *   <tr>
 *     <td>2024-03-31</td>
 *     <td>+1个月</td>
 *     <td>2024-04-30</td>
 *     <td>月末溢出，自动调整到4月最后一天</td>
 *   </tr>
 *   <tr>
 *     <td>2024-05-15</td>
 *     <td>-2个月</td>
 *     <td>2024-03-15</td>
 *     <td>负数表示减少月份</td>
 *   </tr>
 *   <tr>
 *     <td>2024-01-15</td>
 *     <td>+12个月</td>
 *     <td>2025-01-15</td>
 *     <td>跨年计算</td>
 *   </tr>
 * </table>
 *
 * <p><b>注意事项：</b></p>
 * <ul>
 *   <li>时间戳为null或空时，自动使用当前系统时间（System.currentTimeMillis()）</li>
 *   <li>月份参数可以是负数，表示减少月份（如-3表示3个月前）</li>
 *   <li>自动处理大小月、闰年等特殊情况，例如1月31日加1个月会自动调整到2月的最后一天</li>
 *   <li>使用系统默认时区进行计算，跨时区应用需要注意</li>
 *   <li>返回的时间戳保留了原始时间戳的时分秒毫秒部分</li>
 *   <li>Calendar不是线程安全的，但本工具每次调用都创建新实例，因此是线程安全的</li>
 *   <li>对于需要精确到天的计算，建议先将时间戳规整到当天的0点</li>
 * </ul>
 *
 * <p><b>最佳实践：</b></p>
 * <ul>
 *   <li>许可证、订阅等到期时间计算统一使用本工具，保持计算逻辑一致</li>
 *   <li>数据保留策略（如日志清理）建议在配置文件中定义保留月数</li>
 *   <li>计算结果建议配合 {@link DateUtils} 格式化为可读字符串，便于日志记录</li>
 *   <li>对于需要精确控制的场景（如财务结算），建议明确指定计算的起始时间</li>
 *   <li>跨时区场景建议使用Java 8的ZonedDateTime进行计算</li>
 *   <li>大批量日期计算时注意性能，可以考虑批量计算或缓存结果</li>
 *   <li>定时任务中使用时，建议加上异常处理，避免因日期计算异常导致任务中断</li>
 * </ul>
 *
 * <p><b>常见业务场景：</b></p>
 * <ul>
 *   <li><b>3个月试用期：</b>{@code CalendarUtils.getTimeAfterMonth(null, 3)}</li>
 *   <li><b>1年会员：</b>{@code CalendarUtils.getTimeAfterMonth(null, 12)}</li>
 *   <li><b>半年归档：</b>{@code CalendarUtils.getTimeAfterMonth(null, -6)}</li>
 *   <li><b>季度报表：</b>{@code CalendarUtils.getTimeAfterMonth(startTime, 3)}</li>
 *   <li><b>月度续费：</b>{@code CalendarUtils.getTimeAfterMonth(lastExpireTime, 1)}</li>
 * </ul>
 *
 * <p><b>与其他时间工具的配合使用：</b></p>
 * <pre>
 * // 计算3个月后并格式化为字符串
 * Long futureTime = CalendarUtils.getTimeAfterMonth(null, 3);
 * String futureTimeStr = DateUtils.time2String(futureTime);
 * System.out.println("3个月后: " + futureTimeStr);
 *
 * // 结合使用计算时间范围
 * Long endTime = System.currentTimeMillis();
 * Long startTime = CalendarUtils.getTimeAfterMonth(endTime, -1);  // 1个月前
 * System.out.println("查询时间范围: " +
 *     DateUtils.time2String(startTime) + " 到 " +
 *     DateUtils.time2String(endTime));
 * </pre>
 *
 * <p><b>相关工具类：</b></p>
 * <ul>
 *   <li>{@link DateUtils} - 日期时间格式化工具</li>
 *   <li>{@link java.util.Calendar} - Java标准日历类</li>
 *   <li>{@link java.time.LocalDateTime} - Java 8时间API（推荐用于新代码）</li>
 * </ul>
 *
 * @author DataEase
 * @since 1.0.0
 * @see DateUtils
 * @see java.util.Calendar
 */
public class CalendarUtils {

    /**
     * 计算指定时间戳增加或减少若干月后的时间戳
     * <p>
     * 在指定的时间戳基础上增加或减少指定的月数，返回新的时间戳。
     * 如果输入时间戳为null或空，则自动使用当前系统时间作为基准时间。
     * 月份参数支持负数，负数表示减少月份（即计算过去的时间）。
     * </p>
     *
     * <p><b>计算规则：</b></p>
     * <ul>
     *   <li>使用Calendar.add(Calendar.MONTH, months)方法进行计算</li>
     *   <li>保留原时间戳的时分秒毫秒部分，只改变年月日</li>
     *   <li>自动处理月份溢出：
     *     <ul>
     *       <li>1月31日 + 1个月 = 2月28日（平年）或2月29日（闰年）</li>
     *       <li>3月31日 + 1个月 = 4月30日</li>
     *       <li>5月31日 + 1个月 = 6月30日</li>
     *     </ul>
     *   </li>
     *   <li>自动处理跨年：12月 + 1个月 = 次年1月</li>
     *   <li>自动处理闰年：考虑闰年2月有29天</li>
     * </ul>
     *
     * <p><b>参数说明：</b></p>
     * <ul>
     *   <li><b>time为null：</b>自动使用System.currentTimeMillis()作为基准时间</li>
     *   <li><b>months为正数：</b>计算未来时间（如3表示3个月后）</li>
     *   <li><b>months为负数：</b>计算过去时间（如-3表示3个月前）</li>
     *   <li><b>months为0：</b>返回原时间戳（无实际意义，但不会报错）</li>
     * </ul>
     *
     * <p><b>使用示例：</b></p>
     * <pre>
     * // 示例1：计算3个月后
     * Long now = System.currentTimeMillis();
     * Long future = CalendarUtils.getTimeAfterMonth(now, 3);
     * System.out.println(DateUtils.time2String(future));
     * // 如果现在是 2024-01-12，输出: 2024-04-12
     *
     * // 示例2：计算6个月前
     * Long past = CalendarUtils.getTimeAfterMonth(now, -6);
     * System.out.println(DateUtils.time2String(past));
     * // 如果现在是 2024-01-12，输出: 2023-07-12
     *
     * // 示例3：time为null，自动使用当前时间
     * Long oneYearLater = CalendarUtils.getTimeAfterMonth(null, 12);
     * System.out.println("1年后: " + DateUtils.time2String(oneYearLater));
     *
     * // 示例4：许可证到期时间计算
     * Long createTime = System.currentTimeMillis();
     * Long expireTime = CalendarUtils.getTimeAfterMonth(createTime, 3);
     * license.setExpireTime(expireTime);
     * log.info("许可证到期时间: {}", DateUtils.time2String(expireTime));
     *
     * // 示例5：月份溢出处理
     * Long jan31 = 1706688000000L;  // 2024-01-31 00:00:00
     * Long feb = CalendarUtils.getTimeAfterMonth(jan31, 1);
     * System.out.println(DateUtils.time2String(feb));
     * // 输出: 2024-02-29 00:00:00（2024是闰年）
     * // 如果是2023年，输出: 2023-02-28 00:00:00
     *
     * // 示例6：跨年计算
     * Long dec15 = 1702598400000L;  // 2023-12-15 00:00:00
     * Long jan15 = CalendarUtils.getTimeAfterMonth(dec15, 1);
     * System.out.println(DateUtils.time2String(jan15));
     * // 输出: 2024-01-15 00:00:00
     * </pre>
     *
     * <p><b>业务场景示例：</b></p>
     * <pre>
     * // 场景1：创建3个月试用许可证
     * License license = new License();
     * license.setCreateTime(System.currentTimeMillis());
     * license.setExpireTime(CalendarUtils.getTimeAfterMonth(null, 3));
     *
     * // 场景2：会员续费1年
     * User user = userMapper.selectById(userId);
     * Long newExpireTime = CalendarUtils.getTimeAfterMonth(
     *     user.getExpireTime(),
     *     12
     * );
     * user.setExpireTime(newExpireTime);
     *
     * // 场景3：清理3个月前的临时数据
     * Long cleanBeforeTime = CalendarUtils.getTimeAfterMonth(null, -3);
     * tempDataMapper.deleteBeforeTime(cleanBeforeTime);
     *
     * // 场景4：计算季度报表时间范围
     * Long quarterEnd = System.currentTimeMillis();
     * Long quarterStart = CalendarUtils.getTimeAfterMonth(quarterEnd, -3);
     * generateReport(quarterStart, quarterEnd);
     * </pre>
     *
     * <p><b>性能说明：</b></p>
     * <ul>
     *   <li>每次调用都会创建新的Calendar实例，单次调用性能开销很小</li>
     *   <li>适合常规的业务场景使用</li>
     *   <li>如果需要大批量（万级以上）的日期计算，建议考虑缓存或批量处理</li>
     * </ul>
     *
     * @param time 基准时间戳（毫秒级，13位长整型），为null或空时使用当前系统时间
     * @param months 要增加或减少的月数，正数表示未来，负数表示过去，0表示不变
     * @return 计算后的时间戳（毫秒级，13位长整型）
     * @see Calendar#add(int, int)
     * @see Calendar#MONTH
     * @see DateUtils#time2String(Long)
     */
    public static Long getTimeAfterMonth(Long time, int months) {
        if (ObjectUtils.isEmpty(time)) time = System.currentTimeMillis();
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time);
        calendar.add(Calendar.MONTH, months);
        return calendar.getTimeInMillis();
    }
}
