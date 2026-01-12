package io.dataease.model;

import io.dataease.constant.CommonConstants;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 业务节点请求模型
 * 用于封装业务资源节点的查询条件和参数
 */
@Data
public class BusiNodeRequest implements Serializable {
    @Serial
    private static final long serialVersionUID = 3859379188035689316L;

    /**
     * 业务标识
     * 用于区分不同的业务类型
     */
    private String busiFlag;

    /**
     * 节点ID
     * 节点的唯一标识
     */
    private String id;

    /**
     * 是否叶子节点
     * true表示叶子节点，false表示非叶子节点
     */
    private Boolean leaf;

    /**
     * 权重
     * 用于排序和优先级判定
     */
    private Integer weight;

    /**
     * 排序类型
     * 指定节点列表的排序方式
     */
    private String sortType;

    /**
     * 资源表
     * 数据库表名，用于指定查询的数据源
     */
    private String resourceTable;
}
