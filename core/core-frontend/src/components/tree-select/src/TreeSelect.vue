<!--
/**
 * 树形选择器组件 (TreeSelect)
 *
 * ==================== 组件概述 ====================
 * 支持懒加载和搜索的树形选择器组件，提供：
 * 1. 异步懒加载树节点
 * 2. 节点搜索过滤功能
 * 3. 可自定义宽度
 * 4. 支持组织结构等层级数据展示
 *
 * ==================== 主要功能 ====================
 * 1. 懒加载
 *    - 按需加载子节点数据
 *    - 支持异步数据获取
 *    - 自动识别叶子节点
 *
 * 2. 搜索过滤
 *    - 实时搜索节点
 *    - 支持模糊匹配
 *
 * 3. 数据结构
 *    - 基于 Element Plus Tree 组件
 *    - 支持 hasChildren 判断是否有子节点
 *
 * ==================== Props 参数 ====================
 * @param {String} width - 组件宽度，默认 '200px'
 *
 * ==================== 数据结构 ====================
 * interface Tree {
 *   deptId: number          // 部门 ID
 *   pid: number             // 父级 ID
 *   value?: number          // 值
 *   subCount: number        // 子节点数量
 *   name: string            // 显示名称
 *   deptSort: number        // 排序
 *   createTime: number      // 创建时间
 *   updateTime: number      // 更新时间
 *   hasChildren: boolean    // 是否有子节点
 *   leaf: boolean           // 是否为叶子节点
 *   top: boolean            // 是否为顶级节点
 * }
 *
 * ==================== 使用示例 ====================
 * <template>
 *   <TreeSelect width="300px" />
 * </template>
 *
 * <script setup>
 * import TreeSelect from '@/components/tree-select/src/TreeSelect.vue'
 * </script>
 *
 * ==================== 注意事项 ====================
 * - 当前组件包含模拟数据，实际使用时需要替换为真实 API
 * - 懒加载逻辑在 loadNode 方法中实现
 * - 过滤逻辑在 filterNodeMethod 方法中实现
 */
-->
<script lang="ts" setup>
import { ref } from 'vue'
import type Node from 'element-plus-secondary/es/components/tree/src/model/node'
import { propTypes } from '@/utils/propTypes'

/**
 * 树节点数据结构接口
 */
interface Tree {
  deptId: number // 部门 ID
  pid: number // 父级 ID
  value?: number // 值
  subCount: number // 子节点数量
  name: string // 显示名称
  deptSort: number // 排序
  createBy?: string // 创建人
  updateBy?: string // 更新人
  createTime: number // 创建时间
  updateTime: number // 更新时间
  hasChildren: boolean // 是否有子节点
  leaf: boolean // 是否为叶子节点
  top: boolean // 是否为顶级节点
}

const tree = ref()
const currentSelect = ref()

defineProps({
  width: propTypes.string.def('200px')
})

const filterNodeMethod = (value, data) => data.name.includes(value)

const treeDefaultProps = {
  children: 'children',
  label: 'name',
  isLeaf: data => !data.hasChildren
}

const loadNode = (node: Node, resolve: (data: Tree[]) => void) => {
  if (node.level === 0) {
    return setTimeout(() => {
      const data: Tree[] = [
        {
          deptId: 2,
          pid: 0,
          subCount: 2,
          value: 2,
          name: 'wei的组织',
          deptSort: null,
          createBy: null,
          updateBy: null,
          createTime: 1667202467619,
          updateTime: 1667202467619,
          hasChildren: true,
          leaf: false,
          top: true
        },
        {
          deptId: 5,
          pid: 0,
          value: 5,
          subCount: 0,
          name: 'jinlong',
          deptSort: null,
          createBy: null,
          updateBy: null,
          createTime: 1669645057174,
          updateTime: 1669645057174,
          hasChildren: false,
          leaf: true,
          top: true
        },
        {
          deptId: 1,
          pid: 0,
          subCount: 0,
          name: '默认组织1121',
          value: 1,
          deptSort: 0,
          createBy: null,
          updateBy: null,
          createTime: 1622533297817,
          updateTime: 1679037885732,
          hasChildren: false,
          leaf: true,
          top: true
        }
      ]

      resolve(data)
    }, 500)
  }
  if (node.level > 1) return resolve([])

  setTimeout(() => {
    const data: Tree[] = [
      {
        deptId: 3,
        pid: 2,
        subCount: 1,
        value: 3,
        name: 'wei的二级组织',
        deptSort: null,
        createBy: null,
        updateBy: null,
        createTime: 1667202481457,
        updateTime: 1667202481457,
        hasChildren: true,
        leaf: false,
        top: false
      },
      {
        deptId: 4,
        pid: 2,
        subCount: 0,
        value: 4,
        name: 'yyp',
        deptSort: null,
        createBy: null,
        updateBy: null,
        createTime: 1667977447506,
        updateTime: 1667977447506,
        hasChildren: false,
        leaf: true,
        top: false
      }
    ]

    resolve(data)
  }, 500)
}
</script>

<template>
  <el-tree-select
    :load="loadNode"
    lazy
    v-model="currentSelect"
    filterable
    check-strictly
    :filter-node-method="filterNodeMethod"
    clearable
    ref="tree"
    :expand-on-click-node="false"
    check-on-click-node
    :props="treeDefaultProps"
  />
</template>
