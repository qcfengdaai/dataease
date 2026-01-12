package io.dataease.exception;

import io.dataease.i18n.Translator;
import io.dataease.result.ResultCode;
import io.dataease.result.ResultMessage;
import io.dataease.utils.LogUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理器
 * 使用Spring的@RestControllerAdvice注解，统一处理整个应用的异常
 *
 * 主要功能：
 * 1. 捕获和处理各种类型的异常
 * 2. 将异常转换为统一的错误响应格式
 * 3. 记录异常日志便于问题排查
 * 4. 支持国际化错误消息
 * 5. 避免向客户端暴露敏感信息
 *
 * 处理的异常类型：
 * - 参数校验异常
 * - 业务逻辑异常
 * - 空指针异常（特别处理用户登录状态）
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 处理参数校验异常
     * 当使用@Valid注解进行参数校验失败时触发
     *
     * 处理逻辑：
     * 1. 获取第一个校验错误信息
     * 2. 通过国际化工具翻译错误消息
     * 3. 记录错误日志
     * 4. 返回参数无效的错误响应
     *
     * @param e 参数校验异常
     * @return 统一格式的错误响应
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResultMessage MethodArgumentNotValidExceptionHandler(MethodArgumentNotValidException e) {
        // 获取第一个校验错误
        ObjectError objectError = e.getBindingResult().getAllErrors().get(0);
        String msg = objectError.getDefaultMessage();

        // 国际化翻译错误消息
        msg = Translator.get(msg);

        // 记录错误日志
        LogUtil.error(msg);

        // 返回参数无效错误
        return new ResultMessage(ResultCode.PARAM_IS_INVALID.code(), msg);
    }

    /**
     * 处理DataEase业务异常
     * 捕获系统中主动抛出的业务异常
     *
     * 处理逻辑：
     * 1. 记录完整的异常信息和堆栈
     * 2. 直接使用异常中的错误码和消息
     * 3. 保持原有的错误分类
     *
     * @param e DataEase业务异常
     * @return 包含具体错误码和消息的响应
     */
    @ExceptionHandler(DEException.class)
    public ResultMessage deExceptionHandler(DEException e) {
        // 记录业务异常的详细信息
        LogUtil.error(e.getMessage(), e);

        // 直接返回异常中定义的错误码和消息
        return new ResultMessage(e.getCode(), e.getMessage());
    }

    /**
     * 处理空指针异常
     * 特别处理用户未登录导致的空指针异常
     *
     * 处理逻辑：
     * 1. 检查异常消息是否包含用户未登录的特征
     * 2. 如果是用户未登录，返回专门的错误码
     * 3. 其他空指针异常作为参数错误处理
     * 4. 记录完整的异常信息用于调试
     *
     * @param e 空指针异常
     * @return 根据异常类型返回相应的错误响应
     */
    @ExceptionHandler(NullPointerException.class)
    public ResultMessage noUserExceptionHandler(Exception e) {
        String message = e.getMessage();

        // 记录异常详细信息
        LogUtil.error(message, e);

        // 特殊处理：检查是否为用户未登录导致的空指针
        if (StringUtils.contains(message, "Cannot invoke \"io.dataease.auth.bo.TokenUserBO.getUserId()\" because \"user\" is null")) {
            // 返回用户未登录错误
            return new ResultMessage(ResultCode.USER_NOT_LOGGED_IN.code(), ResultCode.USER_NOT_LOGGED_IN.message());
        }

        // 其他空指针异常作为参数错误处理
        return new ResultMessage(ResultCode.PARAM_IS_BLANK.code(), message);
    }

}
