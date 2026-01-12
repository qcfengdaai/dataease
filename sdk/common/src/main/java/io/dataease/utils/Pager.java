package io.dataease.utils;

/**
 * 分页结果封装类
 * <p>
 * 用于封装分页查询的结果数据，包含数据列表、总记录数和总页数。
 * 这是一个通用的分页结果容器，支持泛型，可以封装任何类型的数据列表。
 * </p>
 *
 * <p><b>核心功能：</b></p>
 * <ul>
 *   <li>分页数据封装 - 将查询结果、总记录数、总页数统一封装</li>
 *   <li>泛型支持 - 支持封装任意类型的数据列表</li>
 *   <li>标准分页信息 - 提供页码、记录数等标准分页元数据</li>
 * </ul>
 *
 * <p><b>数据结构：</b></p>
 * <ul>
 *   <li>listObject - 当前页的数据列表（泛型T，通常是List类型）</li>
 *   <li>itemCount - 符合查询条件的总记录数</li>
 *   <li>pageCount - 根据每页大小计算的总页数</li>
 * </ul>
 *
 * <p><b>使用位置：</b></p>
 * <ul>
 *   <li>{@link io.dataease.api.threshold.ThresholdApi#pager} - 阈值告警分页查询接口返回</li>
 *   <li>{@link io.dataease.api.threshold.ThresholdApi#instancePager} - 阈值实例分页查询接口返回</li>
 *   <li>{@link io.dataease.api.report.ReportApi} - 报表管理分页查询接口返回</li>
 *   <li>{@link io.dataease.api.xpack.dataFilling.DataFillingApi} - 数据填报分页查询接口返回</li>
 *   <li>各业务模块的列表查询接口 - 统一的分页返回格式</li>
 * </ul>
 *
 * <p><b>使用示例：</b></p>
 * <pre>
 * // 示例1：基本的分页结果封装
 * List&lt;User&gt; userList = userMapper.selectByPage(pageNum, pageSize);
 * long totalCount = userMapper.countAll();
 * long totalPages = (totalCount + pageSize - 1) / pageSize;
 *
 * Pager&lt;List&lt;User&gt;&gt; pager = new Pager&lt;&gt;(userList, totalCount, totalPages);
 * return pager;
 *
 * // 示例2：使用MyBatis-Plus的IPage转换为Pager
 * IPage&lt;ThresholdGridVO&gt; page = new Page&lt;&gt;(goPage, pageSize);
 * IPage&lt;ThresholdGridVO&gt; result = thresholdMapper.selectPage(page, queryWrapper);
 *
 * Pager&lt;List&lt;ThresholdGridVO&gt;&gt; pager = new Pager&lt;&gt;();
 * pager.setListObject(result.getRecords());
 * pager.setItemCount(result.getTotal());
 * pager.setPageCount(result.getPages());
 * return pager;
 *
 * // 示例3：在Controller中使用
 * {@code @PostMapping("/list/{goPage}/{pageSize}")}
 * public Pager&lt;List&lt;ReportVO&gt;&gt; queryReportList(
 *         {@code @PathVariable} int goPage,
 *         {@code @PathVariable} int pageSize,
 *         {@code @RequestBody} ReportQueryRequest request) {
 *     List&lt;ReportVO&gt; reports = reportService.queryList(goPage, pageSize, request);
 *     long total = reportService.countByCondition(request);
 *     long pages = (total + pageSize - 1) / pageSize;
 *     return new Pager&lt;&gt;(reports, total, pages);
 * }
 *
 * // 示例4：空结果处理
 * if (CollectionUtils.isEmpty(dataList)) {
 *     return new Pager&lt;&gt;(Collections.emptyList(), 0L, 0L);
 * }
 *
 * // 示例5：前端接收分页数据
 * // JavaScript
 * axios.post('/api/threshold/pager/1/10', requestData)
 *   .then(response =&gt; {
 *     const pager = response.data;
 *     console.log('数据列表:', pager.listObject);    // 当前页数据
 *     console.log('总记录数:', pager.itemCount);     // 总记录数
 *     console.log('总页数:', pager.pageCount);       // 总页数
 *   });
 * </pre>
 *
 * <p><b>注意事项：</b></p>
 * <ul>
 *   <li>listObject通常是List类型，但也可以是其他集合类型</li>
 *   <li>itemCount和pageCount使用long类型，支持大数据量场景</li>
 *   <li>计算总页数时注意向上取整：(itemCount + pageSize - 1) / pageSize</li>
 *   <li>空结果时应返回空列表而非null，避免前端空指针异常</li>
 *   <li>与MyBatis-Plus的IPage配合使用时，注意页码从1开始还是从0开始</li>
 *   <li>建议在业务层统一封装分页逻辑，避免重复代码</li>
 * </ul>
 *
 * <p><b>最佳实践：</b></p>
 * <ul>
 *   <li>统一分页格式 - 全系统使用统一的分页返回格式，便于前端统一处理</li>
 *   <li>合理的分页大小 - 建议每页10-100条记录，避免单页数据过多</li>
 *   <li>性能优化 - 大数据量时使用count缓存或估算，避免每次查询都计算总数</li>
 *   <li>参数校验 - 验证pageNum和pageSize的合法性，防止恶意请求</li>
 *   <li>索引优化 - 确保分页查询使用了合适的索引，避免全表扫描</li>
 * </ul>
 *
 * <p><b>分页计算说明：</b></p>
 * <pre>
 * 总页数计算公式：
 * pageCount = (itemCount + pageSize - 1) / pageSize
 *
 * 或者：
 * pageCount = itemCount % pageSize == 0 ? itemCount / pageSize : itemCount / pageSize + 1
 *
 * 示例：
 * - itemCount=100, pageSize=10 → pageCount=10
 * - itemCount=105, pageSize=10 → pageCount=11
 * - itemCount=0, pageSize=10 → pageCount=0
 * </pre>
 *
 * @param <T> 数据列表的类型，通常是List&lt;E&gt;形式
 * @author DataEase
 * @since 1.0.0
 * @see io.dataease.api.threshold.ThresholdApi
 * @see com.baomidou.mybatisplus.core.metadata.IPage
 */
public class Pager<T> {
    /**
     * 当前页的数据列表
     * <p>
     * 通常是List类型，包含当前页的所有记录。
     * 建议使用ArrayList或其他可序列化的List实现类。
     * </p>
     */
    private T listObject;

    /**
     * 符合查询条件的总记录数
     * <p>
     * 所有符合查询条件的记录总数，用于计算总页数和显示给用户。
     * 使用long类型支持大数据量场景。
     * </p>
     */
    private long itemCount;

    /**
     * 总页数
     * <p>
     * 根据总记录数和每页大小计算得出的总页数。
     * 计算公式：(itemCount + pageSize - 1) / pageSize
     * </p>
     */
    private long pageCount;

    /**
     * 无参构造函数
     * <p>
     * 创建一个空的分页对象，所有字段都是默认值。
     * 通常用于需要手动设置各个字段的场景。
     * </p>
     */
    public Pager() {
    }

    /**
     * 全参构造函数
     * <p>
     * 创建一个包含完整分页信息的对象。
     * 这是最常用的构造方法，一次性设置所有分页数据。
     * </p>
     *
     * @param listObject 当前页的数据列表
     * @param itemCount 符合查询条件的总记录数
     * @param pageCount 总页数
     */
    public Pager(T listObject, long itemCount, long pageCount) {
        this.listObject = listObject;
        this.itemCount = itemCount;
        this.pageCount = pageCount;
    }

    /**
     * 获取总页数
     *
     * @return 总页数
     */
    public long getPageCount() {
        return pageCount;
    }

    /**
     * 设置总页数
     *
     * @param pageCount 总页数
     */
    public void setPageCount(long pageCount) {
        this.pageCount = pageCount;
    }

    /**
     * 获取总记录数
     *
     * @return 符合查询条件的总记录数
     */
    public long getItemCount() {
        return itemCount;
    }

    /**
     * 设置总记录数
     *
     * @param itemCount 符合查询条件的总记录数
     */
    public void setItemCount(long itemCount) {
        this.itemCount = itemCount;
    }

    /**
     * 获取当前页数据列表
     *
     * @return 当前页的数据列表
     */
    public T getListObject() {
        return listObject;
    }

    /**
     * 设置当前页数据列表
     *
     * @param listObject 当前页的数据列表
     */
    public void setListObject(T listObject) {
        this.listObject = listObject;
    }
}
