package io.dataease.model;

/**
 * DataEase部署模式枚举
 * 定义DataEase系统支持的三种部署模式
 */
public enum DeModel {

    /**
     * 桌面版模式
     * 轻量级单机部署，适用于个人用户或小型团队
     */
    DESKTOP,

    /**
     * 单机版模式
     * 完整功能的单机部署，适用于中小型企业
     */
    STANDALONE,

    /**
     * 分布式模式
     * 企业级分布式部署，支持集群和高可用
     */
    DISTRIBUTED
}
