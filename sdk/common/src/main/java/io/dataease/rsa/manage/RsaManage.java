package io.dataease.rsa.manage;

import io.dataease.model.RSAModel;
import io.dataease.rsa.dao.entity.CoreRsa;
import io.dataease.rsa.dao.mapper.CoreRsaMapper;
import io.dataease.utils.CommonBeanFactory;
import io.dataease.utils.RsaUtils;
import jakarta.annotation.Resource;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import static io.dataease.constant.CacheConstant.CommonCacheConstant.RSA_CACHE;

/**
 * RSA密钥管理服务类
 * <p>
 * 提供RSA密钥对和AES密钥的生成、存储、查询等管理功能
 * 支持缓存机制，提高密钥查询性能
 * </p>
 */
@Component
public class RsaManage {

    @Resource
    private CoreRsaMapper coreRsaMapper;


    /**
     * 检查RSA密钥是否存在
     * <p>
     * 如果系统中不存在RSA密钥，则自动生成并保存
     * 这个方法通常在系统启动时调用，确保密钥的可用性
     * </p>
     */
    public void check() {
        // 获取代理对象以确保缓存注解生效
        RsaManage proxy = proxy();
        // 查询现有密钥，如果不存在则生成新的
        if (ObjectUtils.isEmpty(proxy.query())) {
            proxy.save();
        }
    }

    /**
     * 生成并保存RSA密钥对
     * <p>
     * 生成新的RSA公钥、私钥和AES密钥，并保存到数据库中
     * 使用固定ID=1确保系统中只有一套密钥
     * 保存时会清除缓存
     * </p>
     */
    @CacheEvict(value = RSA_CACHE, key = "'-de-'")
    public void save() {
        // 使用RsaUtils生成RSA密钥对和AES密钥
        RSAModel model = RsaUtils.generate();

        // 构建CoreRsa实体对象
        CoreRsa coreRsa = new CoreRsa();
        coreRsa.setId(1); // 固定ID确保唯一性
        coreRsa.setCreateTime(System.currentTimeMillis());
        coreRsa.setPrivateKey(model.getPrivateKey());
        coreRsa.setPublicKey(model.getPublicKey());
        coreRsa.setAesKey(model.getAesKey());

        // 插入到数据库
        coreRsaMapper.insert(coreRsa);
    }

    /**
     * 查询RSA密钥信息
     * <p>
     * 从数据库中查询ID为1的RSA密钥信息
     * 结果会被缓存，提高查询性能
     * </p>
     *
     * @return RSA密钥信息，如果不存在则返回null
     */
    @Cacheable(value = RSA_CACHE, key = "'-de-'", unless = "#result == null")
    public CoreRsa query() {
        return coreRsaMapper.selectById(1);
    }

    /**
     * 获取当前类的Spring代理对象
     * <p>
     * 通过代理对象调用方法可以确保Spring的AOP功能（如缓存注解）正常工作
     * 这是因为内部方法调用不会触发Spring的代理机制
     * </p>
     *
     * @return RsaManage的Spring代理实例
     */
    private RsaManage proxy() {
        return CommonBeanFactory.getBean(RsaManage.class);
    }
}
