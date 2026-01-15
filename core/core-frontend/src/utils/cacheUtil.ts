/**
 * 缓存工具函数
 * 提供本地缓存的清理功能
 */

/**
 * 清除本地缓存
 *
 * 清除所有与 DataEase 相关的本地存储数据，包括：
 * - 用户信息（令牌、用户ID、用户名等）
 * - 界面状态（树排序、展开状态等）
 * - 权限和权重信息
 * - 语言设置
 *
 * 使用场景：
 * - 用户登出
 * - 需要重置应用状态
 * - 清除过期数据
 */
export const clearCache = () => {
  // 需要清除的缓存键名列表
  const keys = [
    'DataEaseKey', // 应用主键
    'TreeSort-backend', // 后端树排序状态
    'app.desktop', // 桌面应用配置
    'de-global-refresh', // 全局刷新标识
    'open-backend', // 后端开启状态
    'panel-weight', // 仪表板权重
    'screen-weight', // 大屏权重
    'user.exp', // 用户经验值
    'user.language', // 用户语言设置
    'user.name', // 用户名
    'user.oid', // 组织ID
    'user.time', // 用户时间
    'user.token', // 用户令牌
    'user.uid' // 用户ID
  ]

  // 遍历并移除所有缓存的键值对
  keys.forEach(key => {
    localStorage.removeItem(key)
  })
}
