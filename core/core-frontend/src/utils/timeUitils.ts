/**
 * 时间处理工具函数
 * 提供时间范围计算和时间粒度处理功能
 */

// ==================== 时间范围计算 ====================

/**
 * 根据时间值和时间粒度获取时间范围
 * @param outerTimeValue - 外部时间值
 * @param timeGranularity - 时间粒度
 * @returns 时间范围数组 [开始时间戳, 结束时间戳] 或原始时间值
 *
 * 支持的时间粒度：
 * - 'year' / 'y': 年份
 * - 'month' / 'y_M': 年月
 * - 'date' / 'y_M_d' / 'M_d': 日期
 * - 'hour' / 'y_M_d_H': 小时
 * - 'minute' / 'y_M_d_H_m': 分钟
 * - 'y_M_d_H_m_s': 秒
 * - 'datetime': 精确日期时间
 *
 * @example
 * getRange('2023-01-15', 'y_M_d')     // 返回 2023-01-15 当天的时间范围
 * getRange('2023-01', 'y_M')           // 返回 2023-01 月度的时间范围
 * getRange('2023', 'year')             // 返回 2023 年度的时间范围
 */
export const getRange = (outerTimeValue, timeGranularity) => {
  // 处理小时粒度格式，补全分钟秒
  const selectValue = timeGranularity === 'y_M_d_H' ? outerTimeValue + ':' : outerTimeValue
  // 无效日期直接返回原值
  if (new Date(selectValue).toString() === 'Invalid Date') {
    return selectValue
  }
  switch (timeGranularity) {
    case 'year':
    case 'y':
      return getYearEnd(selectValue)
    case 'month':
    case 'y_M':
      return getMonthEnd(selectValue)
    case 'date':
    case 'y_M_d':
    case 'M_d':
      return getDayEnd(selectValue)
    case 'hour':
    case 'y_M_d_H':
      return getHourEnd(selectValue)
    case 'minute':
    case 'y_M_d_H_m':
      return getMinuteEnd(selectValue)
    case 'y_M_d_H_m_s':
      return getSecondEnd(selectValue)
    case 'datetime':
      return [+new Date(selectValue), +new Date(selectValue)]
    default:
      return selectValue
  }
}

/**
 * 根据时间值和时间粒度获取时间范围的开始时间
 * @param selectValue - 选择的时间值
 * @param timeGranularity - 时间粒度
 * @returns 时间范围数组 [开始时间戳, 结束时间戳] 或原始时间值
 *
 * 用于兼容旧的时间处理逻辑
 */
export const getTimeBegin = (selectValue, timeGranularity) => {
  switch (timeGranularity) {
    case 'year':
      return getYearEnd(selectValue)
    case 'month':
      return getMonthEnd(selectValue)
    case 'date':
      return getDayEnd(selectValue)
    default:
      return selectValue
  }
}

// ==================== 内部辅助函数 ====================

/**
 * 获取年份的时间范围
 * @param timestamp - 时间戳或日期字符串
 * @returns [该年1月1日 00:00:00的时间戳, 该年12月31日 23:59:59的时间戳]
 *
 * @example
 * getYearEnd('2023-01-15')  // [2023-01-01 00:00:00, 2023-12-31 23:59:59]
 */
const getYearEnd = timestamp => {
  const time = new Date(timestamp)
  return [
    +new Date(time.getFullYear(), 0, 1),
    +new Date(time.getFullYear(), 11, 31) + 60 * 1000 * 60 * 24 - 1000
  ]
}

/**
 * 获取月份的时间范围
 * @param timestamp - 时间戳或日期字符串
 * @returns [该月1日 00:00:00的时间戳, 该月最后一天 23:59:59的时间戳]
 *
 * @example
 * getMonthEnd('2023-01-15')  // [2023-01-01 00:00:00, 2023-01-31 23:59:59]
 */
const getMonthEnd = timestamp => {
  const time = new Date(timestamp)
  const date = new Date(time.getFullYear(), time.getMonth(), 1)
  date.setDate(1)
  date.setMonth(date.getMonth() + 1)
  return [+new Date(time.getFullYear(), time.getMonth(), 1), +new Date(date.getTime() - 1000)]
}

/**
 * 获取日期的时间范围
 * @param timestamp - 时间戳或日期字符串
 * @returns [当天 00:00:00 的时间戳, 当天 23:59:59 的时间戳]
 *
 * 注意：使用 UTC 时间进行计算，避免时区影响
 *
 * @example
 * getDayEnd('2023-01-15')  // [2023-01-15 00:00:00, 2023-01-15 23:59:59]
 */
const getDayEnd = timestamp => {
  const utcTime = getUtcTime(timestamp)
  return [+utcTime, +utcTime + 60 * 1000 * 60 * 24 - 1000]
}

/**
 * 获取小时的时间范围
 * @param timestamp - 时间戳或日期字符串
 * @returns [该小时 00分00秒 的时间戳, 该小时 59分59秒 的时间戳]
 *
 * @example
 * getHourEnd('2023-01-15 10:30')  // [2023-01-15 10:00:00, 2023-01-15 10:59:59]
 */
const getHourEnd = timestamp => {
  return [+new Date(timestamp), +new Date(timestamp) + 60 * 1000 * 60 - 1000]
}

/**
 * 获取分钟的时间范围
 * @param timestamp - 时间戳或日期字符串
 * @returns [该分钟 00秒 的时间戳, 该分钟 59秒 的时间戳]
 *
 * @example
 * getMinuteEnd('2023-01-15 10:30:45')  // [2023-01-15 10:30:00, 2023-01-15 10:30:59]
 */
const getMinuteEnd = timestamp => {
  return [+new Date(timestamp), +new Date(timestamp) + 60 * 1000 - 1000]
}

/**
 * 获取秒的时间范围
 * @param timestamp - 时间戳或日期字符串
 * @returns [该秒 0毫秒 的时间戳, 该秒 999毫秒 的时间戳]
 *
 * @example
 * getSecondEnd('2023-01-15 10:30:45.500')  // [2023-01-15 10:30:45.000, 2023-01-15 10:30:45.999]
 */
const getSecondEnd = timestamp => {
  return [+new Date(timestamp), +new Date(timestamp) + 999]
}

/**
 * 转换为 UTC 时间
 * @param timestamp - 时间戳或日期字符串
 * @returns UTC 时间对象或原值
 *
 * 将本地时间转换为 UTC 时间，避免时区差异导致的问题
 */
const getUtcTime = timestamp => {
  if (timestamp) {
    const time = new Date(timestamp)
    const utcDate = new Date(
      time.getUTCFullYear(),
      time.getUTCMonth(),
      time.getUTCDate(),
      time.getUTCHours(),
      time.getUTCMinutes(),
      time.getUTCSeconds()
    )
    return utcDate
  } else {
    return timestamp
  }
}
