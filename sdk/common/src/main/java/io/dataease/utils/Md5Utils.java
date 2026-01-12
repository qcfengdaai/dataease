package io.dataease.utils;

import java.security.MessageDigest;

/**
 * MD5加密工具类
 * <p>
 * 提供基于MD5算法的单向加密功能，用于密码加密、数据完整性校验等场景。
 * MD5（Message-Digest Algorithm 5）是一种广泛使用的哈希算法，可将任意长度的数据转换为固定长度（128位/16字节）的散列值。
 * </p>
 *
 * <p><b>核心功能：</b></p>
 * <ul>
 *   <li>MD5加密 - 将字符串转换为32位十六进制MD5值</li>
 *   <li>字符编码支持 - 支持自定义字符编码（默认UTF-8）</li>
 *   <li>单向加密 - MD5是不可逆的哈希算法</li>
 * </ul>
 *
 * <p><b>算法特点：</b></p>
 * <ul>
 *   <li>输出长度：固定128位（32个十六进制字符）</li>
 *   <li>单向性：不可逆，无法从MD5值还原原文</li>
 *   <li>唯一性：相同输入产生相同输出，不同输入几乎不可能产生相同输出</li>
 *   <li>雪崩效应：输入微小变化会导致输出完全不同</li>
 * </ul>
 *
 * <p><b>使用位置：</b></p>
 * <ul>
 *   <li>{@link io.dataease.auth.filter.CommunityTokenFilter#doFilter} - JWT Token密钥生成</li>
 *   <li>密码加密 - 用户密码的单向加密存储</li>
 *   <li>数据校验 - 文件完整性校验、数据防篡改</li>
 *   <li>签名生成 - API签名、Token生成等</li>
 * </ul>
 *
 * <p><b>使用示例：</b></p>
 * <pre>
 * // 示例1：用户密码加密
 * String password = "myPassword123";
 * String md5Password = Md5Utils.md5(password);
 * System.out.println("MD5加密后: " + md5Password);
 * // 输出: MD5加密后: 6f2b0a5e3e0c4d5a7b8c9d0e1f2a3b4c（示例）
 *
 * // 示例2：指定字符编码
 * String text = "中文字符串";
 * String md5WithUtf8 = Md5Utils.md5(text, "UTF-8");
 * String md5WithGbk = Md5Utils.md5(text, "GBK");
 * // 不同编码会产生不同的MD5值
 *
 * // 示例3：文件内容校验
 * String fileContent = readFileContent("file.txt");
 * String fileMd5 = Md5Utils.md5(fileContent);
 * // 保存或比对MD5值以验证文件完整性
 *
 * // 示例4：JWT Token密钥生成（实际使用场景）
 * // 参考 CommunityTokenFilter.java:41
 * String pwd = getUserPassword();
 * String secret = Md5Utils.md5(pwd);  // 将密码MD5作为JWT密钥
 *
 * // 示例5：API签名生成
 * String apiKey = "myApiKey";
 * String timestamp = String.valueOf(System.currentTimeMillis());
 * String signData = apiKey + timestamp;
 * String signature = Md5Utils.md5(signData);
 * </pre>
 *
 * <p><b>注意事项：</b></p>
 * <ul>
 *   <li>MD5已被证明存在碰撞漏洞，不建议用于高安全性场景</li>
 *   <li>密码加密应使用更安全的算法（如BCrypt、PBKDF2）+ 加盐</li>
 *   <li>MD5是单向加密，无法解密还原原文</li>
 *   <li>相同的输入必然产生相同的MD5值</li>
 *   <li>字符编码不同会导致MD5值不同</li>
 * </ul>
 *
 * <p><b>安全建议：</b></p>
 * <ul>
 *   <li>密码加密应使用BCrypt等专用密码哈希算法</li>
 *   <li>如果必须使用MD5加密密码，应加盐（salt）并多次迭代</li>
 *   <li>对于数据完整性校验，可以使用SHA-256等更安全的算法</li>
 *   <li>不要将MD5用于数字签名等高安全性场景</li>
 * </ul>
 *
 * @author DataEase
 * @since 1.0.0
 * @see io.dataease.auth.filter.CommunityTokenFilter
 */
public class Md5Utils {
    /**
     * 十六进制数字字符数组，用于将字节转换为十六进制字符串
     */
    private static final char[] HEX_DIGITS = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

    /**
     * 默认字符编码：UTF-8
     */
    private static final String UTF_8 = "UTF-8";

    /**
     * MD5加密（使用UTF-8编码）
     * <p>
     * 使用UTF-8编码对字符串进行MD5加密，这是最常用的便捷方法。
     * </p>
     *
     * @param src 待加密的原始字符串
     * @return 32位小写十六进制MD5值
     * @throws RuntimeException 当加密过程发生错误时抛出
     */
    public static String md5(String src) {
        return md5(src, UTF_8);
    }

    /**
     * MD5加密（自定义字符编码）
     * <p>
     * 使用指定的字符编码对字符串进行MD5加密。
     * 不同的字符编码会产生不同的MD5值，因此加密和验证时必须使用相同的编码。
     * </p>
     *
     * @param src 待加密的原始字符串
     * @param charset 字符编码（如UTF-8、GBK等），为空或null时使用系统默认编码
     * @return 32位小写十六进制MD5值
     * @throws RuntimeException 当加密过程发生错误时抛出
     */
    public static String md5(String src, String charset) {
        try {
            byte[] strTemp = charset == null || charset.equals("") ? src.getBytes() : src.getBytes(charset);
            MessageDigest mdTemp = MessageDigest.getInstance("MD5");
            mdTemp.update(strTemp);

            byte[] md = mdTemp.digest();
            int j = md.length;
            char[] str = new char[j * 2];
            int k = 0;

            for (byte byte0 : md) {
                str[k++] = HEX_DIGITS[byte0 >>> 4 & 0xf];
                str[k++] = HEX_DIGITS[byte0 & 0xf];
            }

            return new String(str);
        } catch (Exception e) {
            throw new RuntimeException("MD5 encrypt error:", e);
        }
    }
}
