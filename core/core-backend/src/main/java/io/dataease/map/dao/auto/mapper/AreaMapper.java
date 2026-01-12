package io.dataease.map.dao.auto.mapper;

import io.dataease.map.dao.auto.entity.Area;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 地理区域Mapper接口
 *
 * 功能描述：
 * 提供系统内置地理区域的数据访问操作
 * 继承MyBatis Plus的BaseMapper，自动拥有基础的CRUD功能
 *
 * 数据表：area
 *
 * @author fit2cloud
 * @since 2023-07-09
 */
@Mapper
public interface AreaMapper extends BaseMapper<Area> {

}
