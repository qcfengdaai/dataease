package io.dataease.traffic.dao.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * API流量控制实体类
 * 用于存储API并发控制的配置信息和运行时状态
 * 对应数据库表: core_api_traffic
 */
@TableName("core_api_traffic")
@Data
public class CoreApiTraffic implements Serializable {
    @Serial
    private static final long serialVersionUID = -9130425144350145905L;

    /**
     * 主键ID，使用雪花算法生成
     */
    private Long id;

    /**
     * API标识，用于标识不同的API接口
     * 通常是API的路径或唯一标识符
     */
    private String api;

    /**
     * 并发阈值，定义该API允许的最大并发数
     */
    private Integer threshold;

    /**
     * 当前活跃并发数，记录正在执行中的并发请求数量
     * 初始值为0，每次API调用时递增，调用完成后递减
     */
    private Integer alive;
}
