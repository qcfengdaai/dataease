package io.dataease.chart.constant;

/**
 * 图表常量定义类
 * 定义图表模块中使用的各种常量，包括同比环比计算和查询模式等
 *
 * <p>主要常量类型：</p>
 * <ul>
 *   <li><b>时间比较类型：</b>同比(YoY)、环比(MoM)计算常量</li>
 *   <li><b>查询模式：</b>图表数据查询的不同模式</li>
 * </ul>
 *
 * @author gin
 */
public class ChartConstants {

    /**
     * 年度环比
     * Month-over-Month 计算，按年度维度进行环比分析
     */
    public static final String YEAR_MOM = "year_mom";

    /**
     * 月度环比
     * Month-over-Month 计算，按月度维度进行环比分析
     */
    public static final String MONTH_MOM = "month_mom";

    /**
     * 年度同比
     * Year-over-Year 计算，按年度维度进行同比分析
     */
    public static final String YEAR_YOY = "year_yoy";

    /**
     * 日度环比
     * Day-over-Day 计算，按日度维度进行环比分析
     */
    public static final String DAY_MOM = "day_mom";

    /**
     * 月度同比
     * Month-over-Month Year-over-Year 计算，按月度维度进行同比分析
     */
    public static final String MONTH_YOY = "month_yoy";

    /**
     * 所有同比环比类型数组
     * 包含所有支持的时间比较分析类型
     */
    public static final String[] M_Y = {YEAR_MOM, MONTH_MOM, YEAR_YOY, DAY_MOM, MONTH_YOY};

    /**
     * 图表数据查询模式常量类
     * 定义图表数据查询时的不同模式选择
     */
    public static final class VIEW_RESULT_MODE {

        /**
         * 查询所有数据模式
         * 不限制查询结果数量，返回所有符合条件的数据
         */
        public static final String ALL = "all";

        /**
         * 自定义查询模式
         * 按照用户设定的条件和数量限制查询数据
         */
        public static final String CUSTOM = "custom";
    }
}
