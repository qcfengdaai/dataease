/**
 * 数据检查工具函数
 * 提供数组重复项检查功能
 */

/**
 * 检查数组中指定字段是否存在重复值
 * @param arrayData - 待检查的数组
 * @param key - 要检查的字段名
 * @returns 是否存在重复值
 *
 * @example
 * const data = [
 *   { id: 1, name: 'Alice' },
 *   { id: 2, name: 'Bob' },
 *   { id: 3, name: 'Alice' }
 * ]
 * checkArrayRepeat(data, 'name')  // true（'Alice' 重复）
 * checkArrayRepeat(data, 'id')    // false（所有 id 唯一）
 */
export default function checkArrayRepeat(arrayData, key) {
  for (let i = 0; i < arrayData.length; i++) {
    for (let j = i + 1; j < arrayData.length; j++) {
      if (arrayData[i][key] === arrayData[j][key]) {
        return true
      }
    }
  }
  return false
}
