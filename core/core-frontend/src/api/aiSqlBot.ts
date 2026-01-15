import request from '@/config/axios'

/**
 * AI SQL 机器人相关 API
 * 用于智能 SQL 生成和数据集查询
 */

/**
 * 根据 DV 信息查找 SQL 机器人对应的数据集
 * @param dvInfo 数据可视化 ID 或相关信息
 * @returns 数据集信息
 */
export const findDvSqlBotDataset = dvInfo => request.get({ url: '/sqlbot/dataset/' + dvInfo })
