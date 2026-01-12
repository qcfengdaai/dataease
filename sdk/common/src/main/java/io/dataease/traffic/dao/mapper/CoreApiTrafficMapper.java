package io.dataease.traffic.dao.mapper;

import io.dataease.traffic.dao.entity.CoreApiTraffic;
import org.apache.ibatis.annotations.*;

/**
 * API流量控制数据访问层
 * 负责对 core_api_traffic 表的增删改查操作
 * 提供API并发控制所需的数据库操作方法
 */
@Mapper
public interface CoreApiTrafficMapper {

    /**
     * 获取指定API当前的活跃并发数
     * @param api API标识
     * @return 当前活跃的并发数量
     */
    @Select("select `alive` from `core_api_traffic` where `api` = #{api}")
    int getAlive(@Param("api") String api);

    /**
     * 增加指定API的活跃并发数
     * 当有新的请求进入时调用此方法，将alive字段加1
     * @param api API标识
     */
    @Update("update `core_api_traffic` set alive = alive + 1 where `api` = #{api}")
    void upgrade(@Param("api") String api);

    /**
     * 插入新的API流量控制记录
     * @param traffic API流量控制实体对象，包含id、api、threshold等信息
     */
    @Insert("insert into core_api_traffic values(#{id}, #{api}, #{threshold}, 0)")
    void insert(CoreApiTraffic traffic);

    /**
     * 检查指定API是否已存在流量控制记录
     * @param api API标识
     * @return 记录数量，0表示不存在，大于0表示已存在
     */
    @Select("select count(*) from core_api_traffic where api = #{api}")
    Integer apiCount(@Param("api") String api);

    /**
     * 释放指定API的活跃并发数
     * 当请求执行完成时调用此方法，将alive字段减1
     * 使用CASE语句确保alive不会小于0
     * @param api API标识
     */
    @Update("""
        update `core_api_traffic` set alive =
        CASE WHEN alive > 0 THEN alive - 1
        ELSE alive END
        where `api` = #{api}
    """)
    void releaseAlive(@Param("api") String api);

    /**
     * 清空所有API流量控制数据
     * 通常在应用启动时调用，用于清理上次运行时遗留的数据
     */
    @Delete("delete from core_api_traffic")
    void cleanTraffic();
}
