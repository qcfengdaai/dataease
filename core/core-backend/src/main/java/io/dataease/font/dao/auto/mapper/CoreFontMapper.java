package io.dataease.font.dao.auto.mapper;

import io.dataease.font.dao.auto.entity.CoreFont;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 字体信息Mapper接口
 * 提供字体实体的数据库访问功能，继承MyBatis Plus的基础CRUD操作
 *
 * <p>功能说明：</p>
 * <ul>
 *   <li>继承自BaseMapper，提供基础的增删改查操作</li>
 *   <li>支持字体信息的条件查询和分页查询</li>
 *   <li>提供批量操作和事务支持</li>
 * </ul>
 *
 * <p>主要支持的操作：</p>
 * <ul>
 *   <li>insert - 插入新字体记录</li>
 *   <li>selectById - 根据ID查询字体信息</li>
 *   <li>selectList - 根据条件查询字体列表</li>
 *   <li>updateById - 根据ID更新字体信息</li>
 *   <li>deleteById - 根据ID删除字体记录</li>
 * </ul>
 *
 * @author fit2cloud
 * @since 2024-08-28
 */
@Mapper
public interface CoreFontMapper extends BaseMapper<CoreFont> {

}
