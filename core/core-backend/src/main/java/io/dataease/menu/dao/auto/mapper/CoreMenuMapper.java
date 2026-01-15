package io.dataease.menu.dao.auto.mapper;

import io.dataease.menu.dao.auto.entity.CoreMenu;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 系统菜单Mapper接口
 *
 * 功能描述：
 * 提供系统菜单的数据访问操作
 * 继承MyBatis Plus的BaseMapper，自动拥有基础的CRUD功能
 *
 * 数据表：core_menu
 *
 * @author fit2cloud
 * @since 2023-06-02
 */
@Mapper
public interface CoreMenuMapper extends BaseMapper<CoreMenu> {

}
