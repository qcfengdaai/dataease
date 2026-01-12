/**
 * 系统常量定义包
 * <p>
 * 包含DataEase系统中所有的常量定义，包括认证常量、缓存键、SQL常量、排序常量等。
 * 统一管理常量避免魔法值（Magic Number/String），提高代码可维护性。
 * </p>
 *
 * <h2>核心常量类</h2>
 *
 * <h3>1. 认证相关</h3>
 * <ul>
 *   <li>{@link io.dataease.constant.AuthConstant} - 认证常量（Token键、Header名等）</li>
 *   <li>{@link io.dataease.constant.AuthEnum} - 认证枚举（权限类型、认证方式等）</li>
 *   <li>{@link io.dataease.constant.AuthResourceEnum} - 资源类型枚举</li>
 * </ul>
 *
 * <h3>2. 缓存相关</h3>
 * <ul>
 *   <li>{@link io.dataease.constant.CacheConstant} - 缓存键常量</li>
 * </ul>
 *
 * <h3>3. SQL相关</h3>
 * <ul>
 *   <li>{@link io.dataease.constant.SQLConstants} - SQL常量（关键字、函数等）</li>
 * </ul>
 *
 * <h3>4. 排序相关</h3>
 * <ul>
 *   <li>{@link io.dataease.constant.SortConstants} - 排序字段常量</li>
 * </ul>
 *
 * <h3>5. 日志相关</h3>
 * <ul>
 *   <li>{@link io.dataease.constant.LogOT} - 日志操作类型（Operation Type）</li>
 *   <li>{@link io.dataease.constant.LogST} - 日志来源类型（Source Type）</li>
 * </ul>
 *
 * <h3>6. 消息相关</h3>
 * <ul>
 *   <li>{@link io.dataease.constant.MessageEnum} - 消息类型枚举</li>
 * </ul>
 *
 * <h3>7. 报表任务相关</h3>
 * <ul>
 *   <li>{@link io.dataease.constant.ReportTaskEnum} - 报表任务枚举</li>
 * </ul>
 *
 * <h2>使用示例</h2>
 *
 * <h3>示例1：使用认证常量</h3>
 * <pre>
 * // 从Header中获取Token
 * String token = request.getHeader(AuthConstant.TOKEN_HEADER);
 *
 * // 从Redis获取用户信息
 * String cacheKey = AuthConstant.USER_CACHE_PREFIX + userId;
 * TokenUserBO user = cacheService.get(cacheKey);
 * </pre>
 *
 * <h3>示例2：使用缓存常量</h3>
 * <pre>
 * // 缓存数据集信息
 * String datasetKey = CacheConstant.DATASET_PREFIX + datasetId;
 * cacheService.set(datasetKey, dataset, CacheConstant.DEFAULT_TTL);
 *
 * // 清除缓存
 * cacheService.delete(datasetKey);
 * </pre>
 *
 * <h3>示例3：使用SQL常量</h3>
 * <pre>
 * // 构建SQL查询
 * String sql = "SELECT * FROM table WHERE " +
 *              SQLConstants.COLUMN_CREATE_TIME + " > ?";
 *
 * // 使用SQL函数常量
 * String countSql = "SELECT " + SQLConstants.COUNT_FUNCTION + "(*) FROM table";
 * </pre>
 *
 * <h2>命名规范</h2>
 *
 * <h3>常量命名</h3>
 * <ul>
 *   <li>所有常量使用大写字母和下划线：<code>USER_CACHE_PREFIX</code></li>
 *   <li>相关常量使用相同前缀：<code>CACHE_XXX</code></li>
 *   <li>枚举值使用大写：<code>READ</code>, <code>EDIT</code></li>
 * </ul>
 *
 * <h3>缓存键格式</h3>
 * <ul>
 *   <li>格式：<code>模块:类型:ID</code></li>
 *   <li>示例：<code>dataset:info:123</code></li>
 *   <li>示例：<code>user:auth:456</code></li>
 * </ul>
 *
 * <h2>注意事项</h2>
 *
 * <h3>1. 不要硬编码</h3>
 * <ul>
 *   <li>避免在代码中直接使用字符串或数字</li>
 *   <li>使用常量类中定义的常量</li>
 *   <li>便于统一修改和维护</li>
 * </ul>
 *
 * <h3>2. 枚举优于常量</h3>
 * <ul>
 *   <li>有限的可选值使用枚举而非字符串常量</li>
 *   <li>枚举提供类型安全和编译期检查</li>
 *   <li>枚举可以包含额外的属性和方法</li>
 * </ul>
 *
 * <h3>3. 缓存键规范</h3>
 * <ul>
 *   <li>统一使用冒号分隔</li>
 *   <li>避免过长的键名</li>
 *   <li>便于通配符匹配和批量删除</li>
 * </ul>
 *
 * @author DataEase
 * @since 1.0.0
 */
package io.dataease.constant;
