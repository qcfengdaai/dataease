/**
 * 数据验证工具函数
 *
 * 功能说明：
 * 1. 提供常用的数据格式验证方法
 * 2. 支持外部链接验证
 * 3. 提供正则表达式常量供复用
 *
 * 使用场景：
 * - 表单输入验证
 * - URL 安全性检查
 * - 用户输入数据格式校验
 */

// ==================== 路径验证 ====================

/**
 * 判断路径是否为外部链接
 *
 * 检查给定的路径是否为外部 URL（http/https/mailto/tel）
 * 或插件静态资源路径
 *
 * @param path - 要验证的路径字符串
 * @returns 是否为外部链接
 *
 * @example
 * isExternal('https://example.com')  // true
 * isExternal('/home')                // false
 * isExternal('tel:13800138000')      // true
 * isExternal('/api/pluginCommon/staticInfo/xxx')  // true
 */
export function isExternal(path) {
  return (
    /^(https?:|mailto:|tel:)/.test(path) ||
    /^(http?:|mailto:|tel:)/.test(path) ||
    path.startsWith('/api/pluginCommon/staticInfo')
  )
}

// ==================== 用户名验证 ====================

/**
 * 验证用户名是否有效
 *
 * 检查用户名是否在预定义的有效用户名列表中
 * 注意：此方法仅用于开发/测试环境，生产环境应使用后端验证
 *
 * @param str - 要验证的用户名字符串
 * @returns 用户名是否有效
 *
 * @example
 * validUsername('admin')  // true
 * validUsername('cyw')    // true
 * validUsername('test')   // false
 */
export function validUsername(str) {
  const valid_map = ['admin', 'cyw']
  return valid_map.indexOf(str.trim()) >= 0
}

// ==================== 正则表达式常量 ====================

/**
 * 手机号码正则表达式
 *
 * 中国大陆手机号格式：1开头，第二位为3/4/5/7/8，共11位数字
 *
 * @example
 * // 使用示例
 * const isValid = /^1[3|4|5|7|8][0-9]{9}$/.test('13800138000')
 */
export const PHONE_REGEX = '^1[3|4|5|7|8][0-9]{9}$'

/**
 * 电子邮箱正则表达式
 *
 * 标准邮箱格式：用户名@域名.后缀
 * 支持多级域名和常见特殊字符
 *
 * @example
 * // 使用示例
 * const isValid = /^[a-zA-Z0-9_._-]+@[a-zA-Z0-9_-]+(\.[a-zA-Z0-9_-]+)+$/.test('user@example.com')
 */
export const EMAIL_REGEX = '^[a-zA-Z0-9_._-]+@[a-zA-Z0-9_-]+(\\.[a-zA-Z0-9_-]+)+$'
