package io.dataease.chart.dao.auto.mapper;

import io.dataease.chart.dao.auto.entity.CoreChartView;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 图表视图数据访问层接口
 * 提供对 core_chart_view 表的基础 CRUD 操作
 * 继承 MyBatis Plus 的 BaseMapper，获得通用的数据库操作方法
 *
 * <p>主要功能：</p>
 * <ul>
 *   <li>图表视图的增删改查操作</li>
 *   <li>支持条件查询和分页查询</li>
 *   <li>图表与场景的关联查询</li>
 * </ul>
 *
 * @author fit2cloud
 * @since 2024-12-12
 */
@Mapper
public interface CoreChartViewMapper extends BaseMapper<CoreChartView> {

}
