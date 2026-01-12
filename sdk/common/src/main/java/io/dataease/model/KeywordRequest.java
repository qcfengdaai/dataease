package io.dataease.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 关键字搜索请求模型
 * 用于封装基于关键字的搜索过滤条件
 */
@Schema(description = "关键字过滤器")
@Data
public class KeywordRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = -3038086304525253475L;

    /**
     * 搜索关键字
     * 用户输入的搜索关键词，支持模糊匹配
     */
    @Schema(description = "关键字")
    private String keyword;
}
