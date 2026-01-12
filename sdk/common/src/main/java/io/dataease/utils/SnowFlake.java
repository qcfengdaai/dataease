package io.dataease.utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * 雪花算法ID生成器
 * <p>
 * 基于Twitter的Snowflake算法实现的分布式唯一ID生成器。
 * 雪花算法生成的ID是一个64位的Long类型整数，具有全局唯一性、趋势递增、高性能等特点。
 * 适用于分布式系统中需要生成全局唯一ID的场景，特别是数据库主键生成。
 * </p>
 *
 * <p><b>核心功能：</b></p>
 * <ul>
 *   <li>分布式唯一ID生成 - 在分布式环境下生成全局唯一的64位Long类型ID</li>
 *   <li>趋势递增 - ID按时间顺序递增，对数据库索引友好</li>
 *   <li>高性能 - 本地生成，无需远程调用，单机每秒可生成400万ID</li>
 *   <li>时钟回拨检测 - 检测系统时间回拨，防止ID重复</li>
 * </ul>
 *
 * <p><b>ID结构（64位）：</b></p>
 * <pre>
 * 0 - 00000000 00000000 00000000 00000000 00000000 0 - 00000 - 00000 - 000000000000
 * |   |------------------时间戳部分(41位)----------|   |--数据中心(5位)--|   |-机器ID(5位)-|   |--序列号(12位)--|
 * |
 * 符号位(1位，恒为0)
 *
 * 各部分说明：
 * - 1位符号位：固定为0，保证生成的ID为正数
 * - 41位时间戳：精确到毫秒级，相对于起始时间戳的差值，可使用69年
 * - 5位数据中心ID：支持32个数据中心（0-31）
 * - 5位机器ID：每个数据中心支持32台机器（0-31）
 * - 12位序列号：同一毫秒内支持4096个序列号（0-4095）
 *
 * 理论性能：
 * - 单机每毫秒最多生成4096个ID
 * - 单机每秒最多生成409.6万个ID
 * - 支持1024个节点（32个数据中心 × 32台机器）
 * </pre>
 *
 * <p><b>使用位置：</b></p>
 * <ul>
 *   <li>{@link io.dataease.utils.IDUtils#snowID()} - ID工具类中生成雪花算法ID</li>
 *   <li>数据库主键生成 - 各业务模块的实体主键ID生成</li>
 *   <li>分布式系统 - 订单号、流水号等业务ID生成</li>
 * </ul>
 *
 * <p><b>使用示例：</b></p>
 * <pre>
 * // 示例1：通过Spring注入使用（推荐）
 * {@code @Autowired}
 * private SnowFlake snowFlake;
 *
 * public void createUser() {
 *     Long userId = snowFlake.nextId();
 *     User user = new User();
 *     user.setId(userId);
 *     userMapper.insert(user);
 * }
 *
 * // 示例2：使用IDUtils工具类（推荐）
 * Long id = IDUtils.snowID();
 * System.out.println("生成的ID: " + id);
 * // 输出示例: 生成的ID: 1234567890123456789
 *
 * // 示例3：手动创建实例（不推荐，推荐使用Spring注入）
 * SnowFlake snowFlake = new SnowFlake(1L, 1L);  // 数据中心ID=1, 机器ID=1
 * Long id1 = snowFlake.nextId();
 * Long id2 = snowFlake.nextId();
 * // id2 > id1，保证递增
 *
 * // 示例4：批量生成ID
 * List&lt;Long&gt; idList = new ArrayList&lt;&gt;();
 * for (int i = 0; i &lt; 1000; i++) {
 *     idList.add(snowFlake.nextId());
 * }
 * // 生成1000个唯一ID，耗时约1毫秒
 *
 * // 示例5：在实体类中使用
 * {@code @TableName("sys_user")}
 * public class User {
 *     {@code @TableId(type = IdType.ASSIGN_ID)}  // MyBatis-Plus会自动使用雪花算法
 *     private Long id;
 *     private String username;
 *     // ...
 * }
 *
 * // 示例6：配置机器ID（application.yml）
 * // dataease:
 * //   machine-id: 1  # 配置当前服务器的机器ID，分布式部署时每台服务器配置不同的值
 * </pre>
 *
 * <p><b>注意事项：</b></p>
 * <ul>
 *   <li>时钟依赖 - 依赖系统时间，时间回拨会导致异常，需确保系统时间准确</li>
 *   <li>机器ID配置 - 分布式部署时，每个节点的datacenterId和machineId必须唯一</li>
 *   <li>起始时间戳 - START_STMP是固定的起始时间，不可随意修改</li>
 *   <li>线程安全 - nextId()方法使用synchronized保证线程安全</li>
 *   <li>ID长度 - 生成的ID是Long类型（64位），前端JavaScript需要注意精度问题</li>
 *   <li>时间回拨 - 检测到时钟回拨会抛出RuntimeException，建议配置NTP时间同步</li>
 * </ul>
 *
 * <p><b>最佳实践：</b></p>
 * <ul>
 *   <li>通过配置文件设置机器ID - 使用dataease.machine-id配置项，避免硬编码</li>
 *   <li>合理规划机器ID - 提前规划好数据中心ID和机器ID的分配方案</li>
 *   <li>监控时钟回拨 - 配置NTP时间同步，监控系统时间跳变</li>
 *   <li>前端处理 - ID传给前端时建议转为字符串，避免JavaScript精度丢失</li>
 *   <li>数据库字段类型 - 使用BIGINT类型存储雪花算法生成的ID</li>
 *   <li>单例模式 - 使用Spring的@Component注解，确保全局单例</li>
 * </ul>
 *
 * <p><b>与UUID对比：</b></p>
 * <table border="1">
 *   <tr><th>特性</th><th>SnowFlake</th><th>UUID</th></tr>
 *   <tr><td>长度</td><td>64位Long（8字节）</td><td>128位字符串（36字符）</td></tr>
 *   <tr><td>有序性</td><td>趋势递增，索引友好</td><td>无序，索引性能差</td></tr>
 *   <tr><td>存储空间</td><td>小（BIGINT）</td><td>大（VARCHAR）</td></tr>
 *   <tr><td>性能</td><td>极高</td><td>较高</td></tr>
 *   <tr><td>可读性</td><td>纯数字</td><td>含字母和数字</td></tr>
 *   <tr><td>时钟依赖</td><td>依赖系统时间</td><td>不依赖</td></tr>
 * </table>
 *
 * <p><b>配置说明：</b></p>
 * <pre>
 * # application.yml配置示例
 * dataease:
 *   machine-id: 1  # 机器ID，取值范围0-31，分布式部署时每台服务器配置不同的值
 *
 * # 多数据中心部署建议：
 * # 数据中心1：
 * #   服务器1: datacenterId=1, machineId=1
 * #   服务器2: datacenterId=1, machineId=2
 * # 数据中心2：
 * #   服务器1: datacenterId=2, machineId=1
 * #   服务器2: datacenterId=2, machineId=2
 * </pre>
 *
 * @author DataEase
 * @since 1.0.0
 * @see io.dataease.utils.IDUtils
 */
@Component
public class SnowFlake {

    /**
     * 设置机器ID（从配置文件注入）
     * <p>
     * 通过Spring的@Value注解从配置文件读取机器ID。
     * 如果配置文件中未配置dataease.machine-id，则使用默认值1。
     * </p>
     *
     * <p><b>注意：</b></p>
     * <ul>
     *   <li>分布式部署时，每台服务器必须配置不同的machine-id</li>
     *   <li>machine-id取值范围：0-31（超出范围会在构造函数中抛出异常）</li>
     *   <li>建议在application.yml中明确配置，不要依赖默认值</li>
     * </ul>
     *
     * @param machineId 机器ID，取值范围0-31
     */
    @Value("${dataease.machine-id:1}")
    public void setMachineId(long machineId) {
        this.machineId = machineId;
    }

    /**
     * 起始时间戳（2016-11-26 15:14:25）
     * <p>
     * 雪花算法的时间基准点，所有生成的ID中的时间戳都是相对于这个起始时间的偏移量。
     * 这个值一旦确定就不能修改，否则会导致ID冲突或时间计算错误。
     * 起始时间戳越晚，可用的时间范围越长（41位时间戳可用约69年）。
     * </p>
     */
    private final static long START_STMP = 1480166465631L;

    /**
     * 每一部分占用的位数
     */
    private final static long SEQUENCE_BIT = 12; // 序列号占用12位，支持同一毫秒内生成4096个ID
    private final static long MACHINE_BIT = 5;   // 机器标识占用5位，支持32台机器
    private final static long DATACENTER_BIT = 5; // 数据中心占用5位，支持32个数据中心

    /**
     * 每一部分的最大值
     */
    private final static long MAX_DATACENTER_NUM = -1L ^ (-1L << DATACENTER_BIT); // 数据中心最大值：31
    private final static long MAX_MACHINE_NUM = -1L ^ (-1L << MACHINE_BIT);       // 机器ID最大值：31
    private final static long MAX_SEQUENCE = -1L ^ (-1L << SEQUENCE_BIT);         // 序列号最大值：4095

    /**
     * 每一部分向左的位移量
     */
    private final static long MACHINE_LEFT = SEQUENCE_BIT;                        // 机器ID左移12位
    private final static long DATACENTER_LEFT = SEQUENCE_BIT + MACHINE_BIT;       // 数据中心ID左移17位
    private final static long TIMESTAMP_LEFT = DATACENTER_LEFT + DATACENTER_BIT;  // 时间戳左移22位

    /**
     * 数据中心ID（0-31）
     */
    private long datacenterId;

    /**
     * 机器ID（0-31）
     */
    private long machineId;

    /**
     * 当前毫秒内的序列号（0-4095）
     * <p>
     * 同一毫秒内每生成一个ID，序列号自增1。
     * 如果同一毫秒内序列号用完（达到4096），则等待下一毫秒。
     * </p>
     */
    private long sequence = 0L;

    /**
     * 上一次生成ID的时间戳（毫秒）
     * <p>
     * 用于检测时钟回拨和判断是否需要重置序列号。
     * </p>
     */
    private long lastStmp = -1L;

    /**
     * 双参数构造函数
     * <p>
     * 创建一个指定数据中心ID和机器ID的雪花算法生成器。
     * 这是手动创建实例时使用的构造函数。
     * </p>
     *
     * @param datacenterId 数据中心ID，取值范围0-31
     * @param machineId 机器ID，取值范围0-31
     * @throws IllegalArgumentException 当datacenterId或machineId超出有效范围时抛出
     */
    public SnowFlake(long datacenterId, long machineId) {
        if (datacenterId > MAX_DATACENTER_NUM || datacenterId < 0) {
            throw new IllegalArgumentException("datacenterId can't be greater than MAX_DATACENTER_NUM or less than 0");
        }
        if (machineId > MAX_MACHINE_NUM || machineId < 0) {
            throw new IllegalArgumentException("machineId can't be greater than MAX_MACHINE_NUM or less than 0");
        }
        this.datacenterId = datacenterId;
        this.machineId = machineId;
    }

    /**
     * 无参构造函数
     * <p>
     * 创建一个使用默认数据中心ID（1）的雪花算法生成器。
     * 机器ID通过@Value注解从配置文件注入。
     * 这是Spring容器中使用的构造函数。
     * </p>
     */
    public SnowFlake() {
        this.datacenterId = 1;
    }

    /**
     * 生成下一个唯一ID
     * <p>
     * 这是雪花算法的核心方法，生成一个64位的全局唯一ID。
     * 方法使用synchronized关键字保证线程安全。
     * </p>
     *
     * <p><b>生成逻辑：</b></p>
     * <ol>
     *   <li>获取当前时间戳</li>
     *   <li>检测时钟回拨（当前时间小于上次时间），如果回拨则抛出异常</li>
     *   <li>如果在同一毫秒内：
     *     <ul>
     *       <li>序列号自增</li>
     *       <li>如果序列号溢出（超过4095），等待下一毫秒</li>
     *     </ul>
     *   </li>
     *   <li>如果是新的毫秒：重置序列号为0</li>
     *   <li>组装ID：时间戳 | 数据中心ID | 机器ID | 序列号</li>
     * </ol>
     *
     * <p><b>ID组成示例：</b></p>
     * <pre>
     * 假设：
     * - 当前时间戳相对值：123456789
     * - 数据中心ID：1
     * - 机器ID：1
     * - 序列号：0
     *
     * 生成的ID二进制表示（64位）：
     * 0 | 0000000000000000000000000000000000000111010110111100110100010101 | 00001 | 00001 | 000000000000
     * |   |----------------------41位时间戳--------------------------|   |5位DC|   |5位MC|   |--12位序列--|
     *
     * 最终的Long类型ID：516818079039389696
     * </pre>
     *
     * @return 64位Long类型的全局唯一ID
     * @throws RuntimeException 当检测到时钟回拨时抛出，异常信息：Clock moved backwards. Refusing to generate id
     */
    public synchronized long nextId() {
        long currStmp = getNewstmp();
        // 时钟回拨检测
        if (currStmp < lastStmp) {
            throw new RuntimeException("Clock moved backwards.  Refusing to generate id");
        }

        if (currStmp == lastStmp) {
            // 相同毫秒内，序列号自增
            sequence = (sequence + 1) & MAX_SEQUENCE;
            // 同一毫秒的序列数已经达到最大（4096），等待下一毫秒
            if (sequence == 0L) {
                currStmp = getNextMill();
            }
        } else {
            // 不同毫秒内，序列号置为0
            sequence = 0L;
        }

        lastStmp = currStmp;

        // 组装64位ID：时间戳部分(41位) | 数据中心部分(5位) | 机器标识部分(5位) | 序列号部分(12位)
        return (currStmp - START_STMP) << TIMESTAMP_LEFT // 时间戳部分左移22位
                | datacenterId << DATACENTER_LEFT        // 数据中心部分左移17位
                | machineId << MACHINE_LEFT              // 机器标识部分左移12位
                | sequence;                              // 序列号部分（低12位）
    }

    /**
     * 获取下一毫秒的时间戳
     * <p>
     * 当同一毫秒内序列号用完时，循环等待直到下一毫秒。
     * 这是一个自旋等待，通常只需要等待不到1毫秒。
     * </p>
     *
     * @return 大于lastStmp的时间戳（毫秒）
     */
    private long getNextMill() {
        long mill = getNewstmp();
        while (mill <= lastStmp) {
            mill = getNewstmp();
        }
        return mill;
    }

    /**
     * 获取当前时间戳
     * <p>
     * 获取系统当前时间的毫秒数。
     * 这个方法依赖于系统时间，因此需要确保系统时间准确。
     * </p>
     *
     * @return 当前时间戳（毫秒）
     */
    private long getNewstmp() {
        return System.currentTimeMillis();
    }
}
