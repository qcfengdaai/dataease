package io.dataease.visualization.server;

/**
 * 文件类型枚举
 * <p>
 * 定义支持的图片文件类型，通过文件头魔数进行识别
 * <p>
 * 支持的格式：
 * <ul>
 * <li>JPEG - 魔数: FFD8FF</li>
 * <li>PNG - 魔数: 89504E47</li>
 * <li>GIF - 魔数: 47494638</li>
 * </ul>
 *
 * @author DataEase
 * @since 2024-06-21
 */
public enum FileType {

    /**
     * JPEG格式
     * <p>
     * 文件头魔数: FFD8FF
     * 扩展名: jpg
     */
    JPEG("FFD8FF", "jpg"),

    /**
     * PNG格式
     * <p>
     * 文件头魔数: 89504E47
     * 扩展名: png
     */
    PNG("89504E47", "png"),

    /**
     * GIF格式
     * <p>
     * 文件头魔数: 47494638
     * 扩展名: gif
     */
    GIF("47494638", "gif");

    /**
     * 文件头魔数（十六进制字符串）
     */
    private String value = "";

    /**
     * 文件扩展名
     */
    private String ext = "";

    /**
     * 构造函数（仅设置魔数）
     *
     * @param value 文件头魔数
     */
    FileType(String value) {
        this.value = value;
    }

    /**
     * 构造函数（设置魔数和扩展名）
     *
     * @param value 文件头魔数
     * @param ext   文件扩展名
     */
    FileType(String value, String ext) {
        this(value);
        this.ext = ext;
    }

    /**
     * 获取文件扩展名
     *
     * @return 文件扩展名（如"jpg"、"png"、"gif"）
     */
    public String getExt() {
        return ext;
    }

    /**
     * 获取文件头魔数
     *
     * @return 文件头魔数（十六进制字符串）
     */
    public String getValue() {
        return value;
    }

}