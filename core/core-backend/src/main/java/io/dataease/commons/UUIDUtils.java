package io.dataease.commons;

import java.util.UUID;

/**
 * UUID工具类
 * 提供UUID生成的工具方法，用于生成全局唯一标识符
 *
 * <p>主要用途：</p>
 * <ul>
 *   <li>生成唯一的业务标识ID</li>
 *   <li>生成临时文件名或目录名</li>
 *   <li>创建唯一的会话标识</li>
 *   <li>其他需要唯一标识的场景</li>
 * </ul>
 *
 * <p>使用示例：</p>
 * <pre>{@code
 * // 生成唯一标识
 * String uniqueId = UUIDUtils.getUUID();
 *
 * // 用于文件名
 * String fileName = "temp_" + UUIDUtils.getUUID() + ".xlsx";
 *
 * // 用于业务ID
 * entity.setId(UUIDUtils.getUUID());
 * }</pre>
 */
public class UUIDUtils {

    /**
     * 生成UUID
     * 生成一个随机的UUID字符串，格式为标准的UUID格式（包含连字符）
     *
     * @return UUID字符串，例如："123e4567-e89b-12d3-a456-426614174000"
     */
    public static String getUUID() {
        return UUID.randomUUID().toString();
    }
}
