package io.dataease.exception;

import io.dataease.result.ResultCode;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serial;

/**
 * DataEase自定义业务异常类
 * 继承自RuntimeException，用于处理业务逻辑中的异常情况
 *
 * 主要功能：
 * 1. 封装异常错误码和错误信息
 * 2. 提供多种便捷的异常抛出方式
 * 3. 支持链式调用设置属性
 * 4. 自动触发Spring事务回滚
 *
 * 使用场景：
 * - 业务规则验证失败
 * - 资源不存在或访问受限
 * - 数据状态异常
 * - 权限校验失败
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Accessors(chain = true)
public class DEException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 8170873998824378304L;

    /**
     * 错误码
     * 用于标识具体的错误类型，便于前端处理和系统监控
     */
    private int code;

    /**
     * 错误信息
     * 面向用户的错误描述，支持国际化
     */
    private String msg;

    /**
     * 构造函数：指定错误码和错误信息
     * 最常用的构造方式，明确指定错误类型和描述
     *
     * @param code 错误码
     * @param msg  错误信息
     */
    public DEException(int code, String msg) {
        super(msg);
        this.code = code;
        this.msg = msg;
    }

    /**
     * 私有构造函数：只指定错误信息
     * 使用系统内部错误码作为默认错误码
     *
     * @param message 错误信息
     */
    private DEException(String message) {
        // 使用默认的系统内部错误码
        this(ResultCode.SYSTEM_INNER_ERROR.code(), message);
    }

    /**
     * 私有构造函数：从其他异常构造
     * 用于包装其他异常，保留原始异常的堆栈信息
     *
     * @param t 原始异常
     */
    private DEException(Throwable t) {
        super(t);
        this.code = ResultCode.SYSTEM_INNER_ERROR.code();
        this.msg = t.getMessage();
    }

    /**
     * 静态方法：抛出带消息的异常
     * 使用默认错误码，快速抛出业务异常
     *
     * @param message 错误信息
     * @throws DEException 业务异常
     */
    public static void throwException(String message) {
        throw new DEException(message);
    }

    /**
     * 静态方法：抛出带错误码和消息的异常
     * 明确指定错误码，便于错误分类处理
     *
     * @param code    错误码
     * @param message 错误信息
     * @throws DEException 业务异常
     */
    public static void throwException(int code, String message) {
        throw new DEException(code, message);
    }

    /**
     * 静态方法：获取异常实例
     * 注意：此方法实际上也会抛出异常，方法名可能有误导性
     *
     * @param message 错误信息
     * @throws DEException 业务异常
     * @deprecated 方法名具有误导性，建议使用 throwException 方法
     */
    public static DEException getException(String message) {
        throw new DEException(message);
    }

    /**
     * 静态方法：抛出包装其他异常的异常
     * 用于将检查型异常转换为运行时异常
     *
     * @param t 原始异常
     * @throws DEException 包装后的业务异常
     */
    public static void throwException(Throwable t) {
        throw new DEException(t);
    }
}
