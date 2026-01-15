/**
 * 地图相关 API
 *
 * 功能说明：
 * 1. 获取世界地图层级结构
 * 2. 获取 GeoJSON 地图数据
 * 3. 自定义地理区域管理
 */

import request from '@/config/axios'
import { FeatureCollection } from '@antv/l7plot/dist/esm/plots/choropleth/types'

// ==================== 系统地图 ====================

/**
 * 获取世界地图树形结构
 * @returns 地图层级节点
 */
export const getWorldTree = (): Promise<IResponse<AreaNode>> => {
  return request.get({ url: '/map/worldTree' })
}

/**
 * 获取 GeoJSON 地图数据
 *
 * @param areaId 区域 ID
 * @returns GeoJSON 格式的地图数据
 *
 * 支持两种地图：
 * 1. 系统内置地图（areaId 不以 'geo_' 开头）
 * 2. 自定义地图（areaId 以 'geo_' 开头）
 */
export const getGeoJson = (areaId: string): Promise<IResponse<FeatureCollection>> => {
  let prefix = '/map'
  let areaCode = areaId

  // 判断是否为自定义地图
  if (isCustomGeo(areaId)) {
    prefix = '/geo'
    areaCode = getBusiGeoCode(areaId)
  }

  // 提取国家代码（前3位）
  const realCountry = areaCode.substring(0, 3)
  const url = `${prefix}/${realCountry}/${areaCode}.json`

  return request.get({ url })
}

// ==================== 辅助函数 ====================

/**
 * 判断是否为自定义地图
 * @param id 地图 ID
 * @returns 是否为自定义地图
 */
const isCustomGeo = (id: string) => {
  return id.startsWith('geo_')
}

/**
 * 获取业务地图代码
 * @param id 地图 ID
 * @returns 业务地图代码
 */
const getBusiGeoCode = (id: string) => {
  return id.substring(4)
}

// ==================== 自定义地图管理 ====================

/**
 * 获取自定义地理区域列表
 * @returns 自定义区域列表
 */
export const listCustomGeoArea = (): Promise<IResponse<CustomGeoArea[]>> => {
  return request.get({ url: '/customGeo/geoArea/list' })
}

/**
 * 获取自定义地理区域的子区域
 * @param id 区域 ID
 * @returns 子区域列表
 */
export const getCustomGeoArea = (id: string): Promise<IResponse<CustomGeoSubArea[]>> => {
  return request.get({ url: `/customGeo/geoArea/${id}` })
}

/**
 * 保存自定义地理区域
 * @param area 区域信息
 * @returns 保存结果
 */
export const saveCustomGeoArea = (area: CustomGeoArea) => {
  return request.post({ url: '/customGeo/geoArea/save', data: area })
}

/**
 * 删除自定义地理区域
 * @param id 区域 ID
 * @returns 删除结果
 */
export const deleteCustomGeoArea = (id: string) => {
  return request.delete({ url: `/customGeo/geoArea/${id}` })
}

/**
 * 保存自定义子区域
 * @param area 子区域信息
 * @returns 保存结果
 */
export const saveCustomGeoSubArea = (area: CustomGeoSubArea) => {
  return request.post({ url: '/customGeo/geoSubArea/save', data: area })
}

/**
 * 删除自定义子区域
 * @param id 子区域 ID
 * @returns 删除结果
 */
export const deleteCustomGeoSubArea = (id: string) => {
  return request.delete({ url: `/customGeo/geoSubArea/${id}` })
}

/**
 * 获取子区域选项列表
 * @returns 子区域选项
 */
export const listSubAreaOptions = (): Promise<IResponse<AreaNode[]>> => {
  return request.get({ url: '/customGeo/geoSubArea/options' })
}
