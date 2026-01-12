package io.dataease.utils;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * AES加密解密工具类
 * <p>
 * 提供基于AES算法的对称加密和解密功能，支持CBC模式和PKCS5Padding填充方式。
 * AES（Advanced Encryption Standard）是一种对称加密算法，加密和解密使用相同的密钥。
 * </p>
 *
 * <p><b>核心功能：</b></p>
 * <ul>
 *   <li>AES加密 - 将明文加密为密文</li>
 *   <li>AES解密 - 将密文解密为明文</li>
 *   <li>Base64编码 - 加密后的二进制数据转为可读字符串</li>
 *   <li>异常容错 - 解密失败时返回原字符串</li>
 * </ul>
 *
 * <p><b>加密模式：</b></p>
 * <ul>
 *   <li>算法：AES</li>
 *   <li>模式：CBC（Cipher Block Chaining）</li>
 *   <li>填充：PKCS5Padding</li>
 *   <li>编码：UTF-8</li>
 *   <li>输出格式：Base64</li>
 * </ul>
 *
 * <p><b>使用位置：</b></p>
 * <ul>
 *   <li>敏感数据加密 - 数据库密码、API密钥等敏感配置的加密存储</li>
 *   <li>数据传输加密 - 前后端敏感数据传输时的加密保护</li>
 *   <li>配置文件加密 - 配置文件中敏感信息的加密保存</li>
 * </ul>
 *
 * <p><b>使用示例：</b></p>
 * <pre>
 * // 示例1：基本加密解密
 * String plainText = "敏感数据";
 * String secretKey = "1234567890123456";  // 16字节密钥
 * String iv = "1234567890123456";         // 16字节初始化向量
 *
 * // 加密
 * String encrypted = AesUtils.aesEncrypt(plainText, secretKey, iv);
 * System.out.println("加密后: " + encrypted);  // 输出Base64格式密文
 *
 * // 解密
 * String decrypted = AesUtils.aesDecrypt(encrypted, secretKey, iv);
 * System.out.println("解密后: " + decrypted);  // 输出: 敏感数据
 *
 * // 示例2：使用默认密钥加密（适用于内部数据）
 * String data = "用户密码";
 * String encrypted2 = (String) AesUtils.aesEncrypt(data);
 * String decrypted2 = (String) AesUtils.aesDecrypt(encrypted2);
 *
 * // 示例3：数据库密码加密
 * String dbPassword = "myDBPassword123";
 * String encryptedPwd = AesUtils.aesEncrypt(dbPassword, secretKey, iv);
 * // 将encryptedPwd保存到配置文件
 *
 * // 使用时解密
 * String originalPwd = AesUtils.aesDecrypt(encryptedPwd, secretKey, iv);
 * // 使用originalPwd连接数据库
 *
 * // 示例4：处理null值
 * String result = (String) AesUtils.aesEncrypt(null);  // 返回 null
 * </pre>
 *
 * <p><b>注意事项：</b></p>
 * <ul>
 *   <li>密钥长度必须是16字节（128位）、24字节（192位）或32字节（256位）</li>
 *   <li>IV（初始化向量）长度必须是16字节</li>
 *   <li>密钥和IV必须妥善保管，泄露将导致加密失效</li>
 *   <li>解密失败时会返回原字符串，避免程序异常</li>
 *   <li>默认密钥仅用于内部数据，不应用于高安全性场景</li>
 *   <li>加密后的数据使用Base64编码，便于存储和传输</li>
 * </ul>
 *
 * <p><b>安全建议：</b></p>
 * <ul>
 *   <li>不要在代码中硬编码密钥，应从配置文件或环境变量读取</li>
 *   <li>不同的业务场景应使用不同的密钥</li>
 *   <li>定期更换密钥以提高安全性</li>
 *   <li>对于高安全性需求，建议使用256位密钥</li>
 *   <li>IV应该是随机生成的，不应重复使用相同的IV</li>
 * </ul>
 *
 * @author DataEase
 * @since 1.0.0
 */
public class AesUtils {

    /**
     * AES解密
     * <p>
     * 使用指定的密钥和初始化向量对Base64编码的密文进行解密。
     * 如果解密失败（如密文格式错误），会返回原字符串而不是抛出异常。
     * </p>
     *
     * @param src 待解密的Base64编码密文
     * @param secretKey AES密钥，长度必须是16/24/32字节
     * @param iv 初始化向量（Initialization Vector），长度必须是16字节
     * @return 解密后的明文；如果解密失败则返回原字符串
     * @throws RuntimeException 当密钥为空或解密过程发生严重错误时抛出
     */
    public static String aesDecrypt(String src, String secretKey, String iv) {
        if (StringUtils.isBlank(secretKey)) {
            throw new RuntimeException("secretKey is empty");
        }
        try {
            byte[] raw = secretKey.getBytes(UTF_8);
            SecretKeySpec secretKeySpec = new SecretKeySpec(raw, "AES");
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            IvParameterSpec iv1 = new IvParameterSpec(iv.getBytes());
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, iv1);
            byte[] encrypted1 = Base64.decodeBase64(src);
            byte[] original = cipher.doFinal(encrypted1);
            return new String(original, UTF_8);
        } catch (BadPaddingException | IllegalBlockSizeException e) {
            // 解密的原字符串为非加密字符串，则直接返回原字符串
            return src;
        } catch (Exception e) {
            throw new RuntimeException("decrypt error，please check parameters", e);
        }
    }

    /**
     * AES加密
     * <p>
     * 使用指定的密钥和初始化向量对明文进行加密，并将结果编码为Base64格式。
     * 加密后的字符串可以安全地存储在数据库或配置文件中。
     * </p>
     *
     * @param src 待加密的明文字符串
     * @param secretKey AES密钥，长度必须是16/24/32字节
     * @param iv 初始化向量（Initialization Vector），长度必须是16字节
     * @return Base64编码的密文字符串
     * @throws RuntimeException 当密钥为空或加密过程发生错误时抛出
     */
    public static String aesEncrypt(String src, String secretKey, String iv) {
        if (StringUtils.isBlank(secretKey)) {
            throw new RuntimeException("secretKey is empty");
        }

        try {
            byte[] raw = secretKey.getBytes(UTF_8);
            SecretKeySpec secretKeySpec = new SecretKeySpec(raw, "AES");
            // "算法/模式/补码方式" ECB
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            IvParameterSpec iv1 = new IvParameterSpec(iv.getBytes());
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, iv1);
            byte[] encrypted = cipher.doFinal(src.getBytes(UTF_8));
            return Base64.encodeBase64String(encrypted);
        } catch (Exception e) {
            throw new RuntimeException("AES encrypt error:", e);
        }

    }

    /**
     * 使用默认密钥进行AES加密
     * <p>
     * 这是一个便捷方法，使用内置的默认密钥和IV进行加密。
     * <b>警告：</b>默认密钥仅适用于内部低敏感度数据，不应用于高安全性场景。
     * </p>
     *
     * <p><b>默认配置：</b></p>
     * <ul>
     *   <li>密钥：www.fit2cloud.co（16字节）</li>
     *   <li>IV：1234567890123456（16字节）</li>
     * </ul>
     *
     * @param o 待加密的对象，会调用toString()方法转换为字符串
     * @return Base64编码的密文字符串；如果输入为null则返回null
     */
    public static Object aesEncrypt(Object o) {

        return o == null ? null : aesEncrypt(o.toString(), "www.fit2cloud.co", "1234567890123456");
    }

    /**
     * 使用默认密钥进行AES解密
     * <p>
     * 这是一个便捷方法，使用内置的默认密钥和IV进行解密。
     * 必须与{@link #aesEncrypt(Object)}方法配对使用。
     * </p>
     *
     * <p><b>默认配置：</b></p>
     * <ul>
     *   <li>密钥：www.fit2cloud.co（16字节）</li>
     *   <li>IV：1234567890123456（16字节）</li>
     * </ul>
     *
     * @param o 待解密的Base64编码密文，会调用toString()方法转换为字符串
     * @return 解密后的明文字符串；如果输入为null则返回null
     */
    public static Object aesDecrypt(Object o) {
        return o == null ? null : aesDecrypt(o.toString(), "www.fit2cloud.co", "1234567890123456");
    }
}
