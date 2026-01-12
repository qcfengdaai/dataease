package io.dataease.map.dao.auto.mapper;

import io.dataease.map.dao.auto.entity.CoreCustomGeoArea;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 自定义地理区域Mapper接口
 *
 * 功能描述：
 * 提供用户自定义地理区域的数据访问操作
 * 继承MyBatis Plus的BaseMapper，自动拥有基础的CRUD功能
 *
 * 数据表：core_custom_geo_area
 *
 * @author fit2cloud
 * @since 2024-11-22
 */
@Mapper
public interface CoreCustomGeoAreaMapper extends BaseMapper<CoreCustomGeoArea> {

}
