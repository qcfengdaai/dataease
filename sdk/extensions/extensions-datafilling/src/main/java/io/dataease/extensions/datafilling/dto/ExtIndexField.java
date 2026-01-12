package io.dataease.extensions.datafilling.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * 数据填报扩展索引字段DTO
 * 用于配置数据填报表单对应数据库表的索引信息，支持单列索引和组合索引的定义
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ExtIndexField implements Serializable {
    @Serial
    private static final long serialVersionUID = -3169849285437114316L;

    /**
     * 索引名称
     * 用于标识数据库表中的索引，需要符合数据库命名规范
     */
    private String name;

    /**
     * 索引列配置列表
     * 包含组成索引的字段及其排序方式，支持组合索引配置
     */
    private List<ColumnSetting> columns;

    /**
     * 索引是否已删除标记
     * true表示该索引已被标记为删除，在表结构更新时会被移除
     */
    private boolean removed;

    /**
     * 索引列配置
     * 定义索引中单个列的配置信息，包括列名和排序方式
     */
    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ColumnSetting {
        /**
         * 列名
         * 参与索引的数据库表字段名称
         */
        private String column;

        /**
         * 排序方式
         * 指定该列在索引中的排序方式，如ASC（升序）或DESC（降序）
         */
        private String order;
    }
}
