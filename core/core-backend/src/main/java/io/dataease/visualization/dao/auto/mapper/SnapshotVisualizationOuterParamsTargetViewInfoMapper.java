package io.dataease.visualization.dao.auto.mapper;

import io.dataease.visualization.dao.auto.entity.SnapshotVisualizationOuterParamsTargetViewInfo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 外部参数联动视图字段信息快照表 Mapper接口
 *
 * <p>提供对snapshot_visualization_outer_params_target_view_info表的数据库操作</p>
 *
 * <p>继承MyBatis Plus的BaseMapper，包含基础CRUD方法：
 * <ul>
 *   <li>insert - 插入单条记录</li>
 *   <li>deleteById - 根据ID删除</li>
 *   <li>updateById - 根据ID更新</li>
 *   <li>selectById - 根据ID查询</li>
 *   <li>selectList - 条件查询列表</li>
 *   <li>selectPage - 分页查询</li>
 * </ul>
 * </p>
 *
 * @author DataEase
 * @since 2024-01-12
 */
@Mapper
public interface SnapshotVisualizationOuterParamsTargetViewInfoMapper extends BaseMapper<SnapshotVisualizationOuterParamsTargetViewInfo> {

}
