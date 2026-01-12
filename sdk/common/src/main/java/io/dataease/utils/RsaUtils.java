package io.dataease.utils;


import io.dataease.exception.DEException;
import io.dataease.model.RSAModel;
import io.dataease.rsa.dao.entity.CoreRsa;
import io.dataease.rsa.manage.RsaManage;
import jakarta.annotation.Resource;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

/**
 * RSA加密解密工具类
 * <p>
 * 提供基于RSA算法的非对称加密和解密功能，同时集成AES对称加密用于公钥传输保护。
 * RSA（Rivest-Shamir-Adleman）是一种非对称加密算法，使用公钥加密、私钥解密。
 * 本工具类还提供了对称加密功能，用于系统内部数据的快速加密。
 * </p>
 *
 * <p><b>核心功能：</b></p>
 * <ul>
 *   <li>RSA密钥对生成 - 生成2048位的公钥和私钥对</li>
 *   <li>RSA加密 - 使用公钥对数据进行加密（分段加密，支持长文本）</li>
 *   <li>RSA解密 - 使用私钥对密文进行解密（分段解密）</li>
 *   <li>AES密钥生成 - 生成16位随机AES密钥</li>
 *   <li>AES加密 - 用于保护RSA公钥在传输过程中的安全性</li>
 *   <li>对称加密 - 提供基于AES的快速加密解密功能</li>
 * </ul>
 *
 * <p><b>加密模式：</b></p>
 * <ul>
 *   <li><b>RSA加密：</b>
 *     <ul>
 *       <li>算法：RSA 2048位</li>
 *       <li>加密块大小：245字节</li>
 *       <li>解密块大小：256字节</li>
 *       <li>填充方式：PKCS1Padding</li>
 *       <li>输出格式：Base64</li>
 *     </ul>
 *   </li>
 *   <li><b>AES加密（公钥保护）：</b>
 *     <ul>
 *       <li>算法：AES 128位</li>
 *       <li>模式：CBC（Cipher Block Chaining）</li>
 *       <li>填充：PKCS7Padding</li>
 *       <li>IV：固定16字节零向量</li>
 *       <li>密钥：16位随机字符串</li>
 *     </ul>
 *   </li>
 *   <li><b>对称加密（内部数据）：</b>
 *     <ul>
 *       <li>算法：AES 128位</li>
 *       <li>模式：CBC</li>
 *       <li>填充：PKCS5Padding</li>
 *       <li>IV：固定16字节零向量</li>
 *     </ul>
 *   </li>
 * </ul>
 *
 * <p><b>使用位置：</b></p>
 * <ul>
 *   <li>{@link io.dataease.rsa.manage.RsaManage#save()} - RSA密钥对的生成和保存</li>
 *   <li>前后端数据传输加密 - 敏感数据的非对称加密传输</li>
 *   <li>公钥分发 - 将加密后的公钥和AES密钥安全地分发给客户端</li>
 *   <li>系统初始化 - 系统启动时生成和缓存RSA密钥对</li>
 * </ul>
 *
 * <p><b>使用示例：</b></p>
 * <pre>
 * // 示例1：生成RSA密钥对（系统初始化时）
 * RSAModel rsaModel = RsaUtils.generate();
 * String publicKey = rsaModel.getPublicKey();   // 公钥（Base64编码）
 * String privateKey = rsaModel.getPrivateKey(); // 私钥（Base64编码）
 * String aesKey = rsaModel.getAesKey();         // AES密钥（16位随机字符串）
 *
 * // 将密钥保存到数据库
 * CoreRsa coreRsa = new CoreRsa();
 * coreRsa.setPublicKey(publicKey);
 * coreRsa.setPrivateKey(privateKey);
 * coreRsa.setAesKey(aesKey);
 * // ... 保存到数据库
 *
 * // 示例2：RSA加密（客户端使用公钥加密敏感数据）
 * String sensitiveData = "用户密码123456";
 * String encryptedData = RsaUtils.encryptStr(sensitiveData);
 * System.out.println("加密后: " + encryptedData);
 * // 发送encryptedData到服务端
 *
 * // 示例3：RSA解密（服务端使用私钥解密）
 * // 使用系统私钥解密
 * String decryptedData = RsaUtils.decryptStr(encryptedData);
 * System.out.println("解密后: " + decryptedData);  // 输出: 用户密码123456
 *
 * // 使用指定私钥解密
 * String decrypted2 = RsaUtils.decryptStr(encryptedData, privateKey);
 *
 * // 示例4：获取加密后的公钥（用于客户端）
 * String protectedPublicKey = RsaUtils.publicKey();
 * // 返回格式: {加密后的公钥}-pk_separator-{AES密钥}
 * // 客户端收到后，先用AES密钥解密公钥，再用公钥加密数据
 *
 * // 示例5：获取系统私钥
 * String systemPrivateKey = RsaUtils.privateKey();
 * // 从数据库或缓存中获取系统私钥
 *
 * // 示例6：对称加密（用于系统内部快速加密）
 * String data = "内部敏感数据";
 * String encrypted = RsaUtils.symmetricEncrypt(data);
 * String decrypted = RsaUtils.symmetricDecrypt(encrypted);
 * System.out.println("原始数据: " + decrypted);
 *
 * // 示例7：完整的加密传输流程
 * // 服务端：生成并保存密钥对
 * RSAModel model = RsaUtils.generate();
 * // 保存到数据库...
 *
 * // 服务端：提供公钥给客户端
 * String publicKeyForClient = RsaUtils.publicKey();
 * // 返回给前端...
 *
 * // 客户端：使用公钥加密数据
 * String userPassword = "myPassword123";
 * String encryptedPassword = RsaUtils.encryptStr(userPassword);
 * // 发送到服务端...
 *
 * // 服务端：使用私钥解密
 * String originalPassword = RsaUtils.decryptStr(encryptedPassword);
 * // 验证密码...
 * </pre>
 *
 * <p><b>注意事项：</b></p>
 * <ul>
 *   <li>RSA加密性能较慢，不适合加密大量数据，通常用于加密对称密钥或少量敏感信息</li>
 *   <li>本实现使用2048位密钥长度，单次最多加密245字节数据</li>
 *   <li>对于超过245字节的数据，会自动进行分段加密</li>
 *   <li>私钥必须严格保密，泄露将导致所有加密数据不再安全</li>
 *   <li>公钥可以公开分发，但建议通过AES加密后再传输（本类已实现）</li>
 *   <li>IV使用固定值"0000000000000000"，不适用于高安全性场景</li>
 *   <li>对称加密密钥会缓存在内存中，应注意密钥生命周期管理</li>
 *   <li>本类使用BouncyCastle作为加密提供者，确保支持PKCS7Padding</li>
 * </ul>
 *
 * <p><b>安全建议：</b></p>
 * <ul>
 *   <li>建议定期更换RSA密钥对（如每月或每季度）</li>
 *   <li>私钥应存储在安全的存储介质中，并进行访问控制</li>
 *   <li>对于高安全性需求，建议使用更长的密钥（如4096位）</li>
 *   <li>IV应该是随机生成的，不应使用固定值</li>
 *   <li>对于敏感数据，建议在RSA加密前先进行AES加密（混合加密）</li>
 *   <li>建议配合HTTPS使用，防止中间人攻击</li>
 *   <li>对称加密的密钥应定期更换，不应长期使用同一密钥</li>
 * </ul>
 *
 * <p><b>工作原理：</b></p>
 * <ol>
 *   <li>系统启动时，{@link RsaManage#save()} 调用 {@link #generate()} 生成密钥对</li>
 *   <li>密钥对保存到数据库的 core_rsa 表中，并缓存到内存</li>
 *   <li>客户端请求公钥时，调用 {@link #publicKey()} 返回AES加密后的公钥</li>
 *   <li>客户端使用AES密钥解密公钥，然后用公钥加密敏感数据</li>
 *   <li>服务端收到加密数据后，调用 {@link #decryptStr(String)} 使用私钥解密</li>
 * </ol>
 *
 * @author DataEase
 * @since 1.0.0
 * @see io.dataease.rsa.manage.RsaManage
 * @see io.dataease.model.RSAModel
 * @see io.dataease.rsa.dao.entity.CoreRsa
 */
@Component
public class RsaUtils {

    static {
        if (ObjectUtils.isNotEmpty(Security.getProvider("BC"))) {
            Security.removeProvider("BC");
        }
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
    }

    /**
     * RSA加密时的最大明文块大小（字节）
     * <p>
     * 2048位RSA密钥使用PKCS1Padding时，最大可加密数据长度为：密钥长度/8 - 11 = 256 - 11 = 245字节
     * </p>
     */
    private static final int MAX_ENCRYPT_BLOCK = 245;

    /**
     * RSA解密时的最大密文块大小（字节）
     * <p>
     * 2048位RSA密钥的密文块大小固定为：密钥长度/8 = 256字节
     * </p>
     */
    private static final int MAX_DECRYPT_BLOCK = 256;

    /**
     * 公钥和AES密钥的分隔符
     * <p>
     * 用于在 {@link #publicKey()} 方法中，将加密后的公钥和AES密钥拼接在一起时使用
     * </p>
     */
    private static final String PK_SEPARATOR = "-pk_separator-";

    /**
     * RSA管理器实例，用于从数据库或缓存中查询RSA密钥
     */
    private static RsaManage rsaManage;

    /**
     * 设置RSA管理器实例（Spring依赖注入）
     *
     * @param rsaManage RSA管理器实例
     */
    @Resource
    public void setRsaManage(RsaManage rsaManage) {
        RsaUtils.rsaManage = rsaManage;
    }

    /**
     * 生成RSA密钥对
     * <p>
     * 使用RSA算法生成2048位的密钥对（公钥和私钥）。
     * 2048位是目前推荐的最低安全强度，可以抵御当前的计算能力。
     * </p>
     *
     * @return RSA密钥对对象，包含公钥和私钥
     * @throws DEException 当RSA算法不可用时抛出
     */
    private static KeyPair getKeyPair() {
        KeyPairGenerator generator = null;
        try {
            generator = KeyPairGenerator.getInstance("RSA");
        } catch (NoSuchAlgorithmException e) {
            LogUtil.error(e.getMessage(), e);
            DEException.throwException(e);
        }
        generator.initialize(2048);
        return generator.generateKeyPair();
    }

    /**
     * 从Base64编码的字符串构造私钥对象
     * <p>
     * 将Base64编码的私钥字符串解码后，使用PKCS8格式构造私钥对象。
     * PKCS8是私钥的标准编码格式。
     * </p>
     *
     * @param privateKey Base64编码的私钥字符串
     * @return 私钥对象
     * @throws RuntimeException 当私钥格式错误或算法不可用时抛出
     */
    private static PrivateKey getPrivateKey(String privateKey) {
        KeyFactory keyFactory = null;
        try {
            keyFactory = KeyFactory.getInstance("RSA");
            byte[] decodedKey = Base64.getDecoder().decode(privateKey.getBytes());
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(decodedKey);
            return keyFactory.generatePrivate(keySpec);
        } catch (Exception e) {
            LogUtil.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    /**
     * 从Base64编码的字符串构造公钥对象
     * <p>
     * 将Base64编码的公钥字符串解码后，使用X509格式构造公钥对象。
     * X509是公钥的标准编码格式。
     * </p>
     *
     * @param publicKey Base64编码的公钥字符串
     * @return 公钥对象
     * @throws RuntimeException 当公钥格式错误或算法不可用时抛出
     */
    private static PublicKey getPublicKey(String publicKey) {
        KeyFactory keyFactory = null;
        try {
            keyFactory = KeyFactory.getInstance("RSA");
            byte[] decodedKey = Base64.getDecoder().decode(publicKey.getBytes());
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(decodedKey);
            return keyFactory.generatePublic(keySpec);
        } catch (Exception e) {
            LogUtil.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    /**
     * RSA加密（内部方法，支持分段加密）
     * <p>
     * 使用公钥对数据进行RSA加密。由于RSA加密有长度限制（2048位密钥最多加密245字节），
     * 本方法会自动将超长数据分段加密，然后将所有密文块拼接在一起。
     * </p>
     *
     * @param data 待加密的明文字符串
     * @param publicKey 公钥对象
     * @return Base64编码的密文字符串
     * @throws Exception 当加密过程发生错误时抛出
     */
    private static String encrypt(String data, PublicKey publicKey) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        int inputLen = data.getBytes().length;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int offset = 0;
        byte[] cache;
        int i = 0;
        while (inputLen - offset > 0) {
            if (inputLen - offset > MAX_ENCRYPT_BLOCK) {
                cache = cipher.doFinal(data.getBytes(), offset, MAX_ENCRYPT_BLOCK);
            } else {
                cache = cipher.doFinal(data.getBytes(), offset, inputLen - offset);
            }
            out.write(cache, 0, cache.length);
            i++;
            offset = i * MAX_ENCRYPT_BLOCK;
        }
        byte[] encryptedData = out.toByteArray();
        out.close();
        return Base64.getEncoder().encodeToString(encryptedData);
    }

    /**
     * RSA解密（内部方法，支持分段解密）
     * <p>
     * 使用私钥对Base64编码的密文进行RSA解密。会自动将密文分段解密，
     * 然后将所有明文块拼接在一起，还原原始数据。
     * </p>
     *
     * @param data Base64编码的密文字符串
     * @param privateKey 私钥对象
     * @return 解密后的明文字符串（UTF-8编码）
     * @throws Exception 当解密过程发生错误时抛出
     */
    private static String decrypt(String data, PrivateKey privateKey) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        byte[] dataBytes = Base64.getDecoder().decode(data);
        int inputLen = dataBytes.length;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int offset = 0;
        byte[] cache;
        int i = 0;
        while (inputLen - offset > 0) {
            if (inputLen - offset > MAX_DECRYPT_BLOCK) {
                cache = cipher.doFinal(dataBytes, offset, MAX_DECRYPT_BLOCK);
            } else {
                cache = cipher.doFinal(dataBytes, offset, inputLen - offset);
            }
            out.write(cache, 0, cache.length);
            i++;
            offset = i * MAX_DECRYPT_BLOCK;
        }
        out.close();
        return out.toString(StandardCharsets.UTF_8);
    }

    /**
     * 生成RSA密钥对及AES密钥
     * <p>
     * 生成一个完整的RSA加密体系所需的密钥，包括：
     * </p>
     * <ul>
     *   <li>RSA公钥（Base64编码）- 用于加密数据</li>
     *   <li>RSA私钥（Base64编码）- 用于解密数据</li>
     *   <li>AES密钥（16位随机字符串）- 用于加密公钥</li>
     * </ul>
     * <p>
     * 该方法通常在系统初始化时调用，生成的密钥对会保存到数据库中。
     * </p>
     *
     * @return {@link RSAModel} 包含公钥、私钥和AES密钥的模型对象
     * @throws DEException 当密钥生成失败时抛出
     * @see io.dataease.rsa.manage.RsaManage#save()
     */
    public static RSAModel generate() {
        KeyPair keyPair = getKeyPair();
        String privateKey = new String(Base64.getEncoder().encode(keyPair.getPrivate().getEncoded()));
        String publicKey = new String(Base64.getEncoder().encode(keyPair.getPublic().getEncoded()));
        RSAModel rsaModel = new RSAModel();
        rsaModel.setPrivateKey(privateKey);
        rsaModel.setPublicKey(publicKey);
        rsaModel.setAesKey(generateAesKey());
        return rsaModel;
    }

    /**
     * RSA解密（使用指定私钥）
     * <p>
     * 使用指定的私钥对Base64编码的密文进行解密。
     * 适用于需要使用特定私钥进行解密的场景。
     * </p>
     *
     * @param data Base64编码的密文字符串
     * @param privateKey Base64编码的私钥字符串
     * @return 解密后的明文字符串
     * @throws RuntimeException 当解密失败时抛出
     */
    public static String decryptStr(String data, String privateKey) {
        try {
            return decrypt(data, getPrivateKey(privateKey));
        } catch (Exception e) {
            LogUtil.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    /**
     * RSA解密（使用系统私钥）
     * <p>
     * 使用系统私钥对Base64编码的密文进行解密。
     * 系统私钥从数据库或缓存中获取，这是最常用的解密方法。
     * </p>
     *
     * <p><b>使用示例：</b></p>
     * <pre>
     * // 客户端发送过来的加密数据
     * String encryptedPassword = request.getParameter("password");
     * // 服务端使用系统私钥解密
     * String originalPassword = RsaUtils.decryptStr(encryptedPassword);
     * </pre>
     *
     * @param data Base64编码的密文字符串
     * @return 解密后的明文字符串
     * @throws RuntimeException 当解密失败时抛出
     */
    public static String decryptStr(String data) {
        return decryptStr(data, privateKey());
    }

    /**
     * RSA加密（使用系统公钥）
     * <p>
     * 使用系统公钥对明文进行RSA加密，返回Base64编码的密文。
     * 通常在测试或服务端内部需要加密数据时使用。
     * 正常情况下，加密操作应该在客户端进行。
     * </p>
     *
     * @param data 待加密的明文字符串
     * @return Base64编码的密文字符串
     * @throws RuntimeException 当加密失败时抛出
     */
    public static String encryptStr(String data) {
        try {
            return encrypt(data, getPublicKey(publicKey()));
        } catch (Exception e) {
            LogUtil.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    /**
     * 获取系统私钥
     * <p>
     * 从数据库或缓存中查询系统的RSA私钥（Base64编码）。
     * 私钥用于解密客户端使用公钥加密的数据。
     * </p>
     *
     * @return Base64编码的系统私钥字符串
     */
    public static String privateKey() {
        CoreRsa coreRsa = rsaManage.query();
        return coreRsa.getPrivateKey();
    }

    /**
     * 获取加密后的系统公钥
     * <p>
     * 返回经过AES加密的系统公钥，用于安全地分发给客户端。
     * 返回格式为：{加密后的公钥}{分隔符}{AES密钥}
     * </p>
     *
     * <p><b>返回格式：</b></p>
     * <pre>
     * {Base64编码的AES加密公钥}{Base64编码的分隔符}{16位AES密钥}
     * </pre>
     *
     * <p><b>客户端使用流程：</b></p>
     * <ol>
     *   <li>接收服务端返回的加密公钥字符串</li>
     *   <li>根据分隔符拆分，得到加密公钥和AES密钥</li>
     *   <li>使用AES密钥解密公钥</li>
     *   <li>使用解密后的公钥加密敏感数据</li>
     *   <li>将加密数据发送到服务端</li>
     * </ol>
     *
     * @return 加密后的公钥字符串（格式：{加密公钥}{分隔符}{AES密钥}）
     */
    public static String publicKey() {
        CoreRsa coreRsa = rsaManage.query();
        String publicKey = coreRsa.getPublicKey();
        String aesKey = coreRsa.getAesKey();
        String pk = ascEncrypt(publicKey, aesKey).replaceAll("[\\s*\t\n\r]", "");
        String separator = Base64.getUrlEncoder().encodeToString(PK_SEPARATOR.getBytes(StandardCharsets.UTF_8));
        return pk + separator + aesKey;
    }

    /**
     * 初始化向量（Initialization Vector）
     * <p>
     * AES加密使用的固定16字节初始化向量。
     * 注意：使用固定IV不是最佳实践，理想情况下应该使用随机IV。
     * </p>
     */
    public static final String IV_KEY = "0000000000000000";

    /**
     * 生成16位随机AES密钥
     * <p>
     * 生成由字母和数字组成的16字符随机字符串，用作AES加密的密钥。
     * 用于加密RSA公钥，保护公钥在传输过程中的安全性。
     * </p>
     *
     * @return 16位随机字母数字字符串
     */
    private static String generateAesKey() {
        return RandomStringUtils.randomAlphanumeric(16);
    }

    /**
     * AES加密（用于加密公钥）
     * <p>
     * 使用AES/CBC/PKCS7Padding模式对消息进行加密，主要用于加密RSA公钥。
     * 这是一个内部方法，用于实现 {@link #publicKey()} 方法中的公钥保护。
     * </p>
     *
     * @param message 待加密的消息字符串（通常是RSA公钥）
     * @param key AES密钥（16字节）
     * @return Base64编码的密文字符串
     * @throws RuntimeException 当加密过程发生错误时抛出
     */
    private static String ascEncrypt(String message, String key) {
        Cipher cipher = null;
        try {
            byte[] baseKey = key.getBytes(StandardCharsets.UTF_8);
            byte[] ivBytes = IV_KEY.getBytes(StandardCharsets.UTF_8);
            byte[] messageBytes = message.getBytes(StandardCharsets.UTF_8);
            cipher = Cipher.getInstance("AES/CBC/PKCS7Padding");
            SecretKey keySpec = new SecretKeySpec(baseKey, "AES");
            IvParameterSpec ivps = new IvParameterSpec(ivBytes);
            cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivps);
            byte[] data = cipher.doFinal(messageBytes);
            return Base64.getEncoder().encodeToString(data);
        } catch (Exception e) {
            LogUtil.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }

    }

    /**
     * 对称加密算法名称
     */
    private static final String ALGORITHM = "AES";

    /**
     * 对称加密密钥缓存
     * <p>
     * 缓存生成的对称密钥，避免重复生成。
     * 注意：密钥存储在内存中，应用重启后会重新生成。
     * </p>
     */
    public static String symmetricKey = null;

    /**
     * AES密钥长度（位）
     */
    private static final int KEY_SIZE = 128;

    /**
     * 生成或获取对称加密密钥
     * <p>
     * 生成128位的AES密钥并缓存在内存中。
     * 如果密钥已存在，则直接返回缓存的密钥。
     * 该密钥用于系统内部数据的快速对称加密。
     * </p>
     *
     * <p><b>注意事项：</b></p>
     * <ul>
     *   <li>密钥缓存在内存中，应用重启后会重新生成</li>
     *   <li>建议定期更换密钥以提高安全性</li>
     *   <li>不应用于需要持久化的加密数据</li>
     * </ul>
     *
     * @return Base64编码的AES密钥字符串
     * @throws RuntimeException 当密钥生成失败时抛出
     */
    public static String generateSymmetricKey() {
        try {
            if (StringUtils.isEmpty(symmetricKey)) {
                KeyGenerator keyGenerator = KeyGenerator.getInstance(ALGORITHM);
                keyGenerator.init(KEY_SIZE, new SecureRandom());
                SecretKey secretKey = keyGenerator.generateKey();
                symmetricKey = Base64.getEncoder().encodeToString(secretKey.getEncoded());
            }
            return symmetricKey;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 对称加密
     * <p>
     * 使用AES/CBC/PKCS5Padding模式对数据进行对称加密。
     * 使用自动生成的对称密钥，适用于系统内部数据的快速加密。
     * 相比RSA加密，AES加密速度更快，适合加密大量数据。
     * </p>
     *
     * <p><b>使用场景：</b></p>
     * <ul>
     *   <li>临时数据加密 - 会话期间的敏感数据保护</li>
     *   <li>内存数据加密 - 缓存中的敏感信息加密</li>
     *   <li>快速加密 - 需要高性能加密的场景</li>
     * </ul>
     *
     * <p><b>使用示例：</b></p>
     * <pre>
     * String data = "敏感信息";
     * String encrypted = RsaUtils.symmetricEncrypt(data);
     * // 将encrypted存储到缓存或临时存储
     *
     * // 需要使用时解密
     * String decrypted = RsaUtils.symmetricDecrypt(encrypted);
     * </pre>
     *
     * @param data 待加密的明文字符串
     * @return Base64编码的密文字符串
     * @throws RuntimeException 当加密过程发生错误时抛出
     */
    public static String symmetricEncrypt(String data) {
        try {
            byte[] iv = IV_KEY.getBytes(StandardCharsets.UTF_8);
            IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            SecretKeySpec secretKeySpec = new SecretKeySpec(Base64.getDecoder().decode(generateSymmetricKey()), ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, ivParameterSpec);
            byte[] ciphertext = cipher.doFinal(data.getBytes("UTF-8"));
            return Base64.getEncoder().encodeToString(ciphertext);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 对称解密
     * <p>
     * 使用AES/CBC/PKCS5Padding模式对Base64编码的密文进行解密。
     * 必须与 {@link #symmetricEncrypt(String)} 方法配对使用。
     * 使用相同的对称密钥进行解密。
     * </p>
     *
     * <p><b>注意事项：</b></p>
     * <ul>
     *   <li>必须使用加密时相同的密钥才能成功解密</li>
     *   <li>应用重启后密钥会重新生成，之前加密的数据将无法解密</li>
     *   <li>因此不建议用于需要持久化的加密数据</li>
     * </ul>
     *
     * @param data Base64编码的密文字符串
     * @return 解密后的明文字符串
     * @throws RuntimeException 当解密过程发生错误时抛出
     */
    public static String symmetricDecrypt(String data) {
        try {
            byte[] iv = IV_KEY.getBytes(StandardCharsets.UTF_8);
            IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);
            SecretKeySpec secretKeySpec = new SecretKeySpec(Base64.getDecoder().decode(generateSymmetricKey()), ALGORITHM);
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, ivParameterSpec);
            byte[] decodedCiphertext = Base64.getDecoder().decode(data);
            byte[] decryptedText = cipher.doFinal(decodedCiphertext);
            return new String(decryptedText, "UTF-8");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
