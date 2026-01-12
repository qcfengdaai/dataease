package io.dataease.constant;

import java.util.Arrays;

/**
 * DataEase消息通知类型枚举
 * <p>
 * 定义系统中支持的各种消息通知方式及其标识码，用于消息推送、通知配置和
 * 用户通信等功能。每种消息类型都有唯一的标识码，便于系统识别和处理。
 * </p>
 *
 * <p><b>支持的通知类型：</b></p>
 * <ul>
 *   <li>INNER (0) - 系统内部消息通知</li>
 *   <li>EMAIL (1) - 电子邮件通知</li>
 *   <li>WECOM (2) - 企业微信通知</li>
 *   <li>DINGTALK (3) - 钉钉通知</li>
 *   <li>LARK (4) - 飞书个人通知</li>
 *   <li>LARKSUITE (5) - 飞书套件通知</li>
 *   <li>LARKGROUP (6) - 飞书群组通知</li>
 *   <li>WEBHOOK (7) - 自定义Webhook通知</li>
 *   <li>LARKSUITEGROUP (8) - 飞书套件群组通知</li>
 * </ul>
 *
 * @author fit2cloud
 * @since 1.0
 */
public enum MessageEnum {

    /** 系统内部消息 - 应用内通知，标识码0 */
    INNER(0),

    /** 电子邮件通知 - 通过SMTP发送邮件，标识码1 */
    EMAIL(1),

    /** 企业微信通知 - 通过企业微信API发送消息，标识码2 */
    WECOM(2),

    /** 钉钉通知 - 通过钉钉机器人或API发送消息，标识码3 */
    DINGTALK(3),

    /** 飞书个人通知 - 通过飞书API发送个人消息，标识码4 */
    LARK(4),

    /** 飞书套件通知 - 通过飞书套件API发送消息，标识码5 */
    LARKSUITE(5),

    /** 飞书群组通知 - 向飞书群组发送消息，标识码6 */
    LARKGROUP(6),

    /** 自定义Webhook通知 - 通过HTTP POST请求发送消息，标识码7 */
    WEBHOOK(7),

    /** 飞书套件群组通知 - 向飞书套件群组发送消息，标识码8 */
    LARKSUITEGROUP(8);
    /** 消息类型标识码，用于唯一识别不同的通知方式 */
    private Integer flag;

    /**
     * 获取消息类型标识码
     * <p>
     * 返回当前消息类型对应的唯一标识码，用于系统内部识别和
     * 数据库存储。标识码为整数类型，便于比较和索引。
     * </p>
     *
     * @return 消息类型标识码
     */
    public Integer getFlag() {
        return flag;
    }

    /**
     * 设置消息类型标识码
     *
     * @param flag 消息类型标识码
     */
    public void setFlag(Integer flag) {
        this.flag = flag;
    }

    /**
     * 消息枚举构造函数
     * <p>
     * 使用指定的标识码创建消息类型枚举实例。
     * 标识码用于唯一标识不同的通知方式。
     * </p>
     *
     * @param flag 消息类型标识码
     */
    MessageEnum(Integer flag) {
        this.flag = flag;
    }

    /**
     * 默认构造函数
     * <p>
     * 创建没有标识码的消息枚举实例，
     * 标识码需要后续通过setFlag方法设置。
     * </p>
     */
    MessageEnum() {
    }

    /**
     * 根据标识码获取对应的消息类型枚举
     * <p>
     * 通过标识码查找并返回对应的消息类型枚举实例。
     * 如果找不到对应的枚举，将抛出NoSuchElementException异常。
     * </p>
     *
     * <p><b>使用示例：</b></p>
     * <pre><code>
     * MessageEnum emailType = MessageEnum.fromValue(1);  // 返回 EMAIL
     * MessageEnum innerType = MessageEnum.fromValue(0);  // 返回 INNER
     * MessageEnum webhookType = MessageEnum.fromValue(7); // 返回 WEBHOOK
     * </code></pre>
     *
     * @param flag 消息类型标识码
     * @return 对应的消息类型枚举实例
     * @throws java.util.NoSuchElementException 如果找不到对应标识码的枚举
     */
    public static MessageEnum fromValue(Integer flag) {
        return Arrays.stream(values()).filter(v -> v.flag.equals(flag)).findFirst().get();
    }
}
