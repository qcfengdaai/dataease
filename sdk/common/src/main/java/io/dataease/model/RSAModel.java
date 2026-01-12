package io.dataease.model;

import lombok.Data;

/**
 * RSA加密模型
 * 用于存储RSA非对称加密和AES对称加密的密钥信息
 */
@Data
public class RSAModel {

    /**
     * RSA私钥
     * 用于解密数据和数字签名
     */
    private String privateKey;

    /**
     * RSA公钥
     * 用于加密数据和验证数字签名
     */
    private String publicKey;

    /**
     * AES密钥
     * 用于对称加密，通常与RSA结合使用以提高加密效率
     */
    private String aesKey;
}
