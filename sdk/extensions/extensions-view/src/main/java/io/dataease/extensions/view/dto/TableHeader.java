package io.dataease.extensions.view.dto;

import lombok.Data;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

/**
 * 表格头部配置
 * 用于定义表格的头部分组和列配置
 */
@Data
public class TableHeader {
    /**
     * 头部分组配置
     */
    private HeaderGroupConfig headerGroupConfig;

    /**
     * 是否启用头部分组
     */
    private boolean headerGroup;

    /**
     * 头部分组配置信息
     */
    @Data
    static
    public class HeaderGroupConfig {
        /**
         * 元数据信息列表
         */
        private List<MetaInfo> meta = new ArrayList<>();

        /**
         * 列配置信息列表
         */
        private List<ColumnInfo> columns = new ArrayList<>();
    }

    /**
     * 列配置信息
     */
    @Data
    static
    public class ColumnInfo {
        /**
         * 列键值
         */
        @Getter
        private String key;

        /**
         * 子列配置列表
         */
        private List<ColumnInfo> children = new ArrayList<>();

        /**
         * 列宽度
         */
        private Integer width;
    }

    /**
     * 元数据信息
     */
    @Getter
    @Data
    static
    public class MetaInfo {
        /**
         * 字段名
         */
        private String field;

        /**
         * 显示名称
         */
        private String name;
    }
}
