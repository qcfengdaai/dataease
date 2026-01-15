package io.dataease.extensions.view.filter;

import lombok.Data;

import java.io.Serializable;

/**
 * 动态时间设置配置
 * 用于配置相对时间的过滤条件
 *
 * @Author Junjun
 */
@Data
public class DynamicTimeSetting implements Serializable {
    /**
     * 相对当前时间的预设类型
     * 支持值：thisYear(今年), lastYear(去年), thisMonth(本月), lastMonth(上月),
     * today(今天), yesterday(昨天), monthBeginning(月初), yearBeginning(年初)
     */
    private String relativeToCurrent;

    /**
     * 时间粒度
     * 支持值：year(年), month(月), date(日), datetime(日期时间)
     */
    private String timeGranularity;

    /**
     * 时间数值
     */
    private Integer timeNum;

    /**
     * 相对当前时间的类型
     * 支持值：year(年), month(月), date(日)
     */
    private String relativeToCurrentType;

    /**
     * 时间方向
     * 支持值：f(前), b(后)
     */
    private String around;

    /**
     * 任意时间设置
     * 当timeGranularity为datetime时，用于设置具体的时分秒
     */
    private String arbitraryTime;
}
