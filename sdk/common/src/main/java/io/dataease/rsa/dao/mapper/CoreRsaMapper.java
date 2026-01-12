package io.dataease.rsa.dao.mapper;

import io.dataease.rsa.dao.entity.CoreRsa;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * RSA密钥对数据访问接口
 * <p>
 * 提供对core_rsa表的基础CRUD操作，继承MyBatis Plus的BaseMapper
 * 用于管理系统中的RSA公钥、私钥以及AES密钥的持久化操作
 * </p>
 *
 * @author fit2cloud
 * @since 2023-04-03
 */
@Mapper
public interface CoreRsaMapper extends BaseMapper<CoreRsa> {

}
