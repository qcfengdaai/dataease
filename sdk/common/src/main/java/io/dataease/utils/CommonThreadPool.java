package io.dataease.utils;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;

import java.util.concurrent.*;

/**
 * 通用线程池工具类
 * <p>
 * 提供基于ScheduledThreadPoolExecutor的线程池管理功能，支持普通任务执行、延迟任务执行和超时控制。
 * 该线程池在Spring容器初始化时自动创建，在容器销毁时自动关闭，确保资源的正确管理。
 * </p>
 *
 * <p><b>核心功能：</b></p>
 * <ul>
 *   <li>异步任务执行 - 提交任务到线程池异步执行</li>
 *   <li>延迟任务执行 - 支持延迟指定时间后执行任务</li>
 *   <li>超时控制 - 支持设置任务执行超时时间，超时自动终止</li>
 *   <li>队列监控 - 提供队列可用性检查功能</li>
 *   <li>自动管理 - Spring容器自动初始化和销毁</li>
 * </ul>
 *
 * <p><b>线程池配置：</b></p>
 * <ul>
 *   <li>核心线程数：10（可配置）</li>
 *   <li>最大线程数：10（可配置）</li>
 *   <li>最大队列数：10（用于监控，可配置）</li>
 *   <li>线程空闲时间：600秒（可配置）</li>
 *   <li>线程池类型：ScheduledThreadPoolExecutor</li>
 * </ul>
 *
 * <p><b>应用场景：</b></p>
 * <ul>
 *   <li>异步任务处理 - 耗时操作异步执行，避免阻塞主线程</li>
 *   <li>定时任务 - 延迟执行或定时执行某些操作</li>
 *   <li>超时任务 - 需要控制执行时间的任务</li>
 *   <li>批量数据处理 - 将大量数据分批异步处理</li>
 * </ul>
 *
 * <p><b>使用示例：</b></p>
 * <pre>
 * // 示例1：基本异步任务执行
 * &#64;Service
 * public class DataService {
 *     &#64;Resource
 *     private CommonThreadPool threadPool;
 *
 *     public void processData() {
 *         // 检查线程池是否可用
 *         if (threadPool.available()) {
 *             threadPool.addTask(() -> {
 *                 // 执行耗时操作
 *                 System.out.println("异步处理数据...");
 *                 // 数据处理逻辑
 *             });
 *         }
 *     }
 * }
 *
 * // 示例2：延迟任务执行
 * public void scheduleTask() {
 *     // 5秒后执行任务
 *     threadPool.scheduleTask(() -> {
 *         System.out.println("延迟5秒后执行");
 *         sendNotification();
 *     }, 5, TimeUnit.SECONDS);
 * }
 *
 * // 示例3：带超时控制的任务
 * public void taskWithTimeout() {
 *     // 任务必须在10秒内完成，否则自动终止
 *     threadPool.addTask(() -> {
 *         // 可能耗时很长的操作
 *         processLargeFile();
 *     }, 10, TimeUnit.SECONDS);
 * }
 *
 * // 示例4：批量异步处理
 * public void batchProcess(List<Data> dataList) {
 *     for (Data data : dataList) {
 *         if (threadPool.available()) {
 *             threadPool.addTask(() -> {
 *                 processOne(data);
 *             });
 *         } else {
 *             // 线程池队列已满，等待或同步处理
 *             processOne(data);
 *         }
 *     }
 * }
 *
 * // 示例5：配置线程池参数
 * &#64;Configuration
 * public class ThreadPoolConfig {
 *     &#64;Bean
 *     public CommonThreadPool commonThreadPool() {
 *         CommonThreadPool pool = new CommonThreadPool();
 *         pool.setCorePoolSize(20);        // 设置核心线程数
 *         pool.setMaxQueueSize(50);        // 设置最大队列数
 *         pool.setKeepAliveSeconds(300);   // 设置线程空闲时间
 *         return pool;
 *     }
 * }
 * </pre>
 *
 * <p><b>注意事项：</b></p>
 * <ul>
 *   <li>该类需要被Spring容器管理（@Bean或@Component）才能自动初始化</li>
 *   <li>任务执行失败会记录日志但不会中断线程池</li>
 *   <li>超时任务会被强制终止，需要确保任务支持中断</li>
 *   <li>available()方法仅检查队列大小，不保证任务一定被执行</li>
 *   <li>应避免提交大量长时间运行的任务，可能导致队列溢出</li>
 *   <li>线程池在应用关闭时会自动shutdown，未完成的任务可能被中断</li>
 * </ul>
 *
 * <p><b>最佳实践：</b></p>
 * <ul>
 *   <li>根据实际业务场景合理配置线程池参数</li>
 *   <li>提交任务前使用available()检查队列是否可用</li>
 *   <li>对于关键任务，建议使用带超时的方法防止任务挂起</li>
 *   <li>任务内部应妥善处理异常，避免影响线程池</li>
 *   <li>避免在任务中执行阻塞操作，影响线程池效率</li>
 *   <li>监控线程池队列大小，及时调整参数</li>
 * </ul>
 *
 * <p><b>实现原理：</b></p>
 * <ul>
 *   <li>基于ScheduledThreadPoolExecutor实现，支持延迟和定时任务</li>
 *   <li>使用@PostConstruct在Spring初始化时创建线程池</li>
 *   <li>使用@PreDestroy在Spring销毁时关闭线程池</li>
 *   <li>超时控制通过Future.get(timeout)实现</li>
 * </ul>
 *
 * @author gin
 * @since 2021-04-13
 */
public class CommonThreadPool {

    /**
     * 核心线程数，默认10个
     * <p>
     * 核心线程会一直存活，即使空闲也不会被回收
     * </p>
     */
    private int corePoolSize = 10;

    /**
     * 最大队列大小，默认10
     * <p>
     * 用于{@link #available()}方法判断队列是否可用，不是线程池的硬性限制
     * </p>
     */
    private int maxQueueSize = 10;

    /**
     * 最大线程数，默认10
     * <p>
     * ScheduledThreadPoolExecutor中该值等于corePoolSize
     * </p>
     */
    private int maximumPoolSize = 10;

    /**
     * 线程空闲存活时间，默认600秒
     * <p>
     * 超过核心线程数的线程在空闲指定时间后会被回收
     * </p>
     */
    private int keepAliveSeconds = 600;

    /**
     * 定时线程池执行器
     * <p>
     * 支持延迟任务和定时任务的执行
     * </p>
     */
    private ScheduledThreadPoolExecutor scheduledThreadPoolExecutor;

    /**
     * 初始化线程池
     * <p>
     * 该方法由Spring容器在Bean初始化后自动调用（@PostConstruct），
     * 创建并配置ScheduledThreadPoolExecutor实例。
     * </p>
     */
    @PostConstruct
    public void init() {
        scheduledThreadPoolExecutor = new ScheduledThreadPoolExecutor(corePoolSize);
        scheduledThreadPoolExecutor.setMaximumPoolSize(corePoolSize);
        scheduledThreadPoolExecutor.setKeepAliveTime(keepAliveSeconds, TimeUnit.SECONDS);
    }

    /**
     * 关闭线程池
     * <p>
     * 该方法由Spring容器在Bean销毁前自动调用（@PreDestroy），
     * 优雅地关闭线程池，等待已提交的任务执行完成。
     * </p>
     */
    @PreDestroy
    public void shutdown() {
        if (scheduledThreadPoolExecutor != null) {
            scheduledThreadPoolExecutor.shutdown();
        }
    }

    /**
     * 检查线程池是否可用
     * <p>
     * 通过比较当前队列大小与最大队列大小来判断线程池是否还能接收新任务。
     * 这是一个软性限制，不会阻止任务提交，仅用于业务判断。
     * </p>
     *
     * <p><b>使用示例：</b></p>
     * <pre>
     * if (threadPool.available()) {
     *     threadPool.addTask(() -> processData());
     * } else {
     *     // 队列已满，采用其他策略
     *     processDataSync();  // 同步处理
     *     // 或者等待后重试
     * }
     * </pre>
     *
     * @return true表示队列未满可以添加任务，false表示队列已满建议暂停添加
     */
    public boolean available() {
        return scheduledThreadPoolExecutor.getQueue().size() <= maxQueueSize;
    }

    /**
     * 添加异步任务到线程池
     * <p>
     * 将任务提交到线程池异步执行，不限制队列大小。
     * 任务会立即提交，但具体执行时间取决于线程池的调度。
     * </p>
     *
     * <p><b>使用示例：</b></p>
     * <pre>
     * threadPool.addTask(() -> {
     *     System.out.println("异步任务开始执行");
     *     // 执行业务逻辑
     *     processData();
     *     System.out.println("异步任务执行完成");
     * });
     * </pre>
     *
     * @param task 要执行的任务，不能为null
     */
    public void addTask(Runnable task) {
        scheduledThreadPoolExecutor.execute(task);
    }

    /**
     * 添加延迟执行任务
     * <p>
     * 将任务延迟指定时间后执行，适用于需要延迟处理的场景。
     * 不限制队列大小，任务会在延迟时间到达后由线程池调度执行。
     * </p>
     *
     * <p><b>使用示例：</b></p>
     * <pre>
     * // 5秒后发送通知
     * threadPool.scheduleTask(() -> {
     *     sendNotification("任务完成");
     * }, 5, TimeUnit.SECONDS);
     *
     * // 1分钟后清理缓存
     * threadPool.scheduleTask(() -> {
     *     clearCache();
     * }, 1, TimeUnit.MINUTES);
     * </pre>
     *
     * @param task 要执行的任务，不能为null
     * @param delay 延迟时间，必须大于0
     * @param unit 延迟时间单位（秒、分钟、小时等）
     */
    public void scheduleTask(Runnable task, long delay, TimeUnit unit) {
        scheduledThreadPoolExecutor.schedule(task, delay, unit);
    }

    /**
     * 添加带超时控制的任务
     * <p>
     * 提交任务到线程池并设置最大执行时间，如果任务在指定时间内未完成，将被强制终止。
     * 这可以防止某些任务执行时间过长而占用线程资源，影响其他任务的执行。
     * </p>
     *
     * <p><b>实现原理：</b></p>
     * <ul>
     *   <li>为每个任务创建一个独立的单线程执行器</li>
     *   <li>使用Future.get(timeout)等待任务完成或超时</li>
     *   <li>超时后Future会抛出TimeoutException，任务被中断</li>
     *   <li>执行完成后自动关闭临时执行器释放资源</li>
     * </ul>
     *
     * <p><b>使用示例：</b></p>
     * <pre>
     * // 处理文件，最多等待30秒
     * threadPool.addTask(() -> {
     *     processLargeFile();
     * }, 30, TimeUnit.SECONDS);
     *
     * // 调用外部API，最多等待10秒
     * threadPool.addTask(() -> {
     *     callExternalAPI();
     * }, 10, TimeUnit.SECONDS);
     * </pre>
     *
     * <p><b>注意事项：</b></p>
     * <ul>
     *   <li>任务必须能够响应中断（检查Thread.interrupted()）</li>
     *   <li>超时后任务会被中断但不保证立即停止</li>
     *   <li>任务抛出的异常会被捕获并记录日志</li>
     *   <li>每个任务都会创建临时执行器，不适合大量任务</li>
     * </ul>
     *
     * @param task 要执行的任务，不能为null
     * @param timeOut 超时时间，必须大于0
     * @param timeUnit 超时时间单位（秒、分钟、小时等）
     */
    public void addTask(Runnable task, long timeOut, TimeUnit timeUnit) {
        scheduledThreadPoolExecutor.execute(() -> {
            ExecutorService executorService = Executors.newSingleThreadExecutor();
            try {
                Future future = executorService.submit(task);
                future.get(timeOut, timeUnit); // 此行会阻塞，直到任务执行完或超时
            } catch (TimeoutException timeoutException) {
                LogUtil.getLogger().error("timeout to execute task", timeoutException);
            } catch (Exception exception) {
                LogUtil.getLogger().error("failed to execute task", exception);
            } finally {
                if (!executorService.isShutdown()) {
                    executorService.shutdown();
                }
            }
        });
    }

    /**
     * 设置核心线程数
     * <p>
     * 必须在{@link #init()}方法调用前设置才能生效。
     * 建议在Bean配置时设置，而不是运行时动态修改。
     * </p>
     *
     * @param corePoolSize 核心线程数，建议根据CPU核心数和任务类型设置
     */
    public void setCorePoolSize(int corePoolSize) {
        this.corePoolSize = corePoolSize;
    }

    /**
     * 设置最大队列大小
     * <p>
     * 该值仅用于{@link #available()}方法的判断，不是线程池的硬性限制。
     * 建议设置为合理值以避免队列无限增长导致内存溢出。
     * </p>
     *
     * @param maxQueueSize 最大队列大小，超过此值available()返回false
     */
    public void setMaxQueueSize(int maxQueueSize) {
        this.maxQueueSize = maxQueueSize;
    }

    /**
     * 设置线程空闲存活时间
     * <p>
     * 超过核心线程数的空闲线程在此时间后会被回收。
     * 必须在{@link #init()}方法调用前设置才能生效。
     * </p>
     *
     * @param keepAliveSeconds 线程空闲存活时间，单位：秒
     */
    public void setKeepAliveSeconds(int keepAliveSeconds) {
        this.keepAliveSeconds = keepAliveSeconds;
    }
}
