package io.dataease.commons.utils;

import io.dataease.utils.BeanUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 加密工具类
 * 提供系统中常用的加密和解密功能，包括AES对称加密和MD5散列算法
 *
 * <p>继承自CodingUtil，复用基础的编码解码方法</p>
 *
 * <p>支持的加密算法：</p>
 * <ul>
 *   <li>AES对称加密 - 用于可逆的数据加密</li>
 *   <li>MD5散列算法 - 用于不可逆的数据摘要</li>
 * </ul>
 *
 * <p>主要用途：</p>
 * <ul>
 *   <li>敏感数据的存储加密（如密码、密钥等）</li>
 *   <li>数据传输过程中的加密保护</li>
 *   <li>配置信息的安全存储</li>
 *   <li>数据完整性验证</li>
 * </ul>
 *
 * <p>安全说明：</p>
 * <ul>
 *   <li>使用固定的密钥和初始化向量，适用于内部系统</li>
 *   <li>生产环境建议使用动态密钥管理</li>
 * </ul>
 */
public class EncryptUtils extends CodingUtil {

    /**
     * AES加密密钥
     * 固定密钥，用于系统内部数据的加密解密
     */
    private static final String secretKey = "www.fit2cloud.co";

    /**
     * AES加密初始化向量
     * 固定IV，与密钥配合使用
     */
    private static final String iv = "1234567890123456";

    /**
     * AES加密
     * 对输入对象进行AES加密，返回加密后的字符串
     *
     * @param o 待加密的对象，会自动转换为字符串
     * @return 加密后的字符串，如果输入为null则返回null
     */
    public static Object aesEncrypt(Object o) {
        if (o == null) {
            return null;
        }
        return aesEncrypt(o.toString(), secretKey, iv);
    }

    /**
     * AES解密
     * 对输入对象进行AES解密，返回解密后的原始字符串
     *
     * @param o 待解密的对象，应为之前加密的字符串
     * @return 解密后的原始字符串，如果输入为null则返回null
     */
    public static Object aesDecrypt(Object o) {
        if (o == null) {
            return null;
        }
        return aesDecrypt(o.toString(), secretKey, iv);
    }

    /**
     * 批量AES解密
     * 对列表中对象的指定属性进行批量AES解密，适用于查询结果的批量解密处理
     *
     * @param <T> 列表元素类型
     * @param o 待处理的对象列表
     * @param attrName 需要解密的属性名称
     * @return 解密后的对象列表，原列表中指定属性的值会被解密后的值替换
     */
    public static <T> Object aesDecrypt(List<T> o, String attrName) {
        if (o == null) {
            return null;
        }
        return o.stream()
                // 过滤出指定属性不为null的元素
                .filter(element -> BeanUtils.getFieldValueByName(attrName, element) != null)
                // 对指定属性进行解密处理
                .peek(element -> BeanUtils.setFieldValueByName(element, attrName,
                    aesDecrypt(BeanUtils.getFieldValueByName(attrName, element).toString(), secretKey, iv),
                    String.class))
                .collect(Collectors.toList());
    }

    /**
     * MD5加密
     * 对输入对象进行MD5散列计算，生成不可逆的摘要字符串
     *
     * @param o 待加密的对象，会自动转换为字符串
     * @return MD5散列值，如果输入为null则返回null
     */
    public static Object md5Encrypt(Object o) {
        if (o == null) {
            return null;
        }
        return md5(o.toString());
    }
}
