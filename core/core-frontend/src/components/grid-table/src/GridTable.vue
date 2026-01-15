<!--
/**
 * 表格组件 (GridTable)
 *
 * ==================== 组件概述 ====================
 * 基于 Element Plus Table 的二次封装表格组件，提供以下增强功能：
 * 1. 跨页选中记忆功能
 * 2. 自定义空状态展示
 * 3. 统一的分页配置
 * 4. 支持搜索空状态
 * 5. 完整的事件透传
 *
 * ==================== 主要功能 ====================
 * 1. 选中记忆
 *    - 跨页面保持选中状态
 *    - 支持自定义标识字段（默认 id）
 *    - 提供 API 操作选中状态
 *
 * 2. 空状态
 *    - 无数据时的空状态展示
 *    - 区分搜索无数据和表格无数据
 *    - 可自定义空状态文案和图片
 *
 * 3. 分页
 *    - 统一的分页样式和配置
 *    - 支持自定义每页显示数量
 *    - 可隐藏分页器
 *
 * 4. 事件透传
 *    - 自动透传 ElTable 的所有事件
 *    - 自动透传 ElTable 的所有属性
 *
 * ==================== Props 参数 ====================
 * @param {Array} columns - 列配置数组
 * @param {Boolean} isSearch - 是否为搜索模式（影响空状态显示）
 * @param {Boolean} showPagination - 是否显示分页，默认 true
 * @param {Array} multipleSelection - 默认选中的行数组
 * @param {Object} pagination - 分页配置对象
 *   - currentPage: 当前页码
 *   - pageSize: 每页显示数量
 *   - pageSizes: 每页显示数量选项
 *   - total: 总条数
 *   - layout: 分页组件布局
 * @param {Boolean} isRememberSelected - 是否跨页记忆选中，默认 false
 * @param {String} selectedFlags - 选中标识字段名，默认 'id'
 * @param {Array} tableData - 表格数据
 * @param {String} emptyDesc - 空状态描述文案
 * @param {String} emptyImg - 空状态图片类型
 * @param {Boolean} border - 是否显示纵向边框，默认 false
 * @param {Boolean} showEmptyImg - 是否显示空状态图片，默认 true
 * @param {Boolean} dataLoading - 数据加载状态，默认 false
 *
 * ==================== 暴露的方法 ====================
 * - toggleRowSelection(row): 切换某一行的选中状态
 * - toggleAllSelection(): 切换所有行的选中状态
 * - clearSelection(): 清除选中状态
 *
 * ==================== Events 事件 ====================
 * 组件自动透传 ElTable 和 ElPagination 的所有事件：
 * @event selection-change - 选中项变化
 * @event size-change - 每页显示数量变化
 * @event current-change - 当前页变化
 * @event sort-change - 排序变化
 * @event filter-change - 筛选变化
 * ... 以及 ElTable 的所有其他事件
 *
 * ==================== Slots 插槽 ====================
 * @slot empty - 自定义空状态内容
 * 其他插槽透传给 ElTable
 *
 * ==================== 使用示例 ====================
 * <template>
 *   <GridTable
 *     v-loading="loading"
 *     :table-data="tableData"
 *     :columns="columns"
 *     :pagination="pagination"
 *     :is-remember-selected="true"
 *     @selection-change="handleSelectionChange"
 *     @current-change="handlePageChange"
 *   >
 *     <el-table-column prop="name" label="名称" />
 *     <el-table-column prop="code" label="编码" />
 *   </GridTable>
 * </template>
 *
 * <script setup>
 * const tableData = ref([
 *   { id: 1, name: '项目A', code: 'A001' },
 *   { id: 2, name: '项目B', code: 'A002' }
 * ])
 *
 * const pagination = reactive({
 *   currentPage: 1,
 *   pageSize: 10,
 *   total: 100
 * })
 *
 * const columns = ['name', 'code']
 * </script>
 */
-->
<script lang="ts" setup>
import { reactive, ref, computed, watch, nextTick, onBeforeMount, useAttrs } from 'vue'
import { ElTable, ElPagination } from 'element-plus-secondary'
import EmptyBackground from '@/components/empty-background/src/EmptyBackground.vue'
import TableBody from './TableBody.vue'
import { propTypes } from '@/utils/propTypes'
import { useI18n } from '@/hooks/web/useI18n'
const { t } = useI18n()
const props = defineProps({
  columns: propTypes.arrayOf(propTypes.string),
  isSearch: propTypes.bool.def(false),
  showPagination: propTypes.bool.def(true),
  multipleSelection: propTypes.array.def(() => []),
  pagination: propTypes.object,
  isRememberSelected: propTypes.bool.def(false),
  selectedFlags: propTypes.string.def('id'),
  tableData: propTypes.array,
  emptyDesc: propTypes.string,
  emptyImg: propTypes.string,
  border: propTypes.bool.def(false),
  showEmptyImg: propTypes.bool.def(true),
  dataLoading: propTypes.bool.def(false)
})

const attrs = useAttrs()

const handleListeners = () => {
  Object.keys(attrs).forEach(key => {
    if (key.startsWith('on')) {
      if (['onSizeChange', 'onCurrentChange'].includes(key)) {
        state.paginationEvent[key.slice(2)] = attrs[key]
      } else {
        state.tableEvent[key.slice(2)] = attrs[key]
      }
    } else {
      state.tableAttrs[key] = attrs[key]
    }
  })
}
const toggleRowSelection = row => {
  table.value.toggleRowSelection(row, true)
}
const toggleAllSelection = () => {
  table.value.toggleAllSelection()
}
const clearSelection = () => {
  table.value.clearSelection()
}
const handlerSelected = multipleSelection => {
  state.multipleSelectionCache = [...state.multipleSelectionCache, ...multipleSelection]
  const flags = state.multipleSelectionCache.map(ele => ele[props.selectedFlags])
  // 当前页的选中项索引
  const notCurrentArr = []
  props.tableData.forEach(ele => {
    const resultIndex = flags.indexOf(ele[props.selectedFlags])
    if (resultIndex !== -1) {
      table.value.toggleRowSelection(ele, true)
      notCurrentArr.push(resultIndex)
    }
  })
  notCurrentArr.sort().reduceRight((_, next) => {
    state.multipleSelectionCache.splice(next, 1)
  }, 0)
}

onBeforeMount(() => {
  handleListeners()
})

const state = reactive({
  paginationEvent: {},
  paginationDefault: {
    currentPage: 1,
    pageSizes: [10, 20, 50, 100],
    pageSize: 10,
    layout: 'total, prev, pager, next, sizes, jumper',
    total: 0
  },
  multipleSelectionCache: [],
  tableEvent: {},
  tableAttrs: {}
})

const imgType = computed(() => {
  return props.emptyImg ? props.emptyImg : props.isSearch ? 'tree' : 'noneWhite'
})
const table = ref(null)

const multipleSelectionAll = computed(() => [
  ...state.multipleSelectionCache,
  ...props.multipleSelection
])
watch(
  props.pagination,
  () => {
    state.paginationDefault = {
      ...state.paginationDefault,
      ...props.pagination
    }
  },
  { deep: true, immediate: true }
)

watch(
  props.tableData,
  () => {
    nextTick(() => {
      table.value.doLayout()
    })
    if (!props.isRememberSelected) return
    // 先拷贝 重新加载数据会触发SelectionChange 导致this.multipleSelection为空
    const multipleSelection = [...props.multipleSelection]
    nextTick(() => {
      handlerSelected(multipleSelection)
    })
  },
  { deep: true }
)
defineExpose({
  toggleRowSelection,
  clearSelection,
  toggleAllSelection,
  multipleSelectionAll
})
</script>

<template>
  <div class="flex-table" :class="!tableData.length && 'no-data'">
    <el-table
      ref="table"
      :border="border"
      v-bind="state.tableAttrs"
      :data="tableData"
      :style="{ width: '100%', height: '100%' }"
      v-on="state.tableEvent"
      v-loading="props.dataLoading"
    >
      <table-body :columns="columns">
        <slot />
      </table-body>
      <template #empty>
        <empty-background
          v-if="props.showEmptyImg"
          :description="props.emptyDesc ? props.emptyDesc : t('data_set.no_data')"
          :img-type="imgType || 'noneWhite'"
        />
        <div v-else :style="{ width: '100%' }" />
      </template>
    </el-table>
    <div v-if="showPagination && !!tableData.length" class="pagination-cont">
      <el-pagination
        v-model:current-page="state.paginationDefault.currentPage"
        v-model:page-size="state.paginationDefault.pageSize"
        background
        v-bind="state.paginationDefault"
        v-on="state.paginationEvent"
      />
    </div>
  </div>
</template>

<style lang="less" scoped>
.flex-table {
  display: flex;
  height: 100%;
  flex-direction: column;
  justify-content: space-between;
  .pagination-cont {
    display: flex;
    justify-content: flex-end;
    margin-top: 10px;
  }

  &.no-data {
    :deep(.ed-table__inner-wrapper::before) {
      display: none;
    }
  }
}
</style>
