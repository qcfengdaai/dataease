package io.dataease.engine.func;

/**
 * SQL函数常量类
 * 定义DataEase数据引擎中支持的各种SQL函数常量，用于数据查询和分析
 *
 * <p>主要用途：</p>
 * <ul>
 *   <li>SQL查询构建时的函数验证</li>
 *   <li>图表数据聚合计算</li>
 *   <li>数据集字段的聚合操作</li>
 *   <li>报表统计分析</li>
 * </ul>
 *
 * @author Junjun
 */
public class FunctionConstant {

    /**
     * 所有聚合函数数组
     * 包含DataEase支持的所有SQL聚合函数，用于数据汇总和统计分析
     *
     * <p>支持的函数类型：</p>
     * <ul>
     *   <li><b>SUM</b> - 求和函数</li>
     *   <li><b>AVG</b> - 平均值函数</li>
     *   <li><b>MAX</b> - 最大值函数</li>
     *   <li><b>MIN</b> - 最小值函数</li>
     *   <li><b>COUNT</b> - 计数函数</li>
     *   <li><b>STDDEV</b> - 标准差函数</li>
     *   <li><b>STDDEV_POP</b> - 总体标准差函数</li>
     *   <li><b>STDDEV_SAMP</b> - 样本标准差函数</li>
     *   <li><b>VAR_POP</b> - 总体方差函数</li>
     *   <li><b>VAR_SAMP</b> - 样本方差函数</li>
     * </ul>
     */
    public static final String[] AGG_FUNC = {"SUM", "AVG", "MAX", "MIN", "COUNT", "STDDEV", "STDDEV_POP", "STDDEV_SAMP", "VAR_POP", "VAR_SAMP"};
}
