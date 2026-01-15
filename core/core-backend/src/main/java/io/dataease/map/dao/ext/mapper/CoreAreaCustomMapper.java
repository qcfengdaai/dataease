package io.dataease.map.dao.ext.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.dataease.map.dao.ext.entity.CoreAreaCustom;
import org.apache.ibatis.annotations.Mapper;

/**
 * 自定义区域扩展Mapper接口
 *
 * 功能描述：
 * 提供通过上传GeoJSON文件创建的自定义区域的数据访问操作
 * 继承MyBatis Plus的BaseMapper，自动拥有基础的CRUD功能
 *
 * 数据表：core_area_custom（扩展表）
 *
 * @author DataEase
 */
@Mapper
public interface CoreAreaCustomMapper extends BaseMapper<CoreAreaCustom> {
}
