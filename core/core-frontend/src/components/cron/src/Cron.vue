<!--
/**
 * Cron 表达式生成器组件
 *
 * ==================== 组件概述 ====================
 * 用于生成和编辑 Cron 表达式的可视化组件，支持：
 * 1. 通过图形界面生成 Cron 表达式
 * 2. 支持秒、分、时、日、月、周、年的配置
 * 3. 实时预览生成的 Cron 表达式
 * 4. 表达式解析和回显
 *
 * ==================== Cron 表达式格式 ====================
 * 标准格式：秒 分 时 日 月 周 年
 * 示例：0 0 12 * * ? - 每天中午12点执行
 *
 * 各字段说明：
 * - 秒: 0-59 , - * /
 * - 分: 0-59 , - * /
 * - 时: 0-23 , - * /
 * - 日: 1-31 , - * ? / L W
 * - 月: 1-12 , - * /
 * - 周: 1-7 , - * ? / L #
 * - 年: 1970-2099 , - * /
 *
 * ==================== Props 参数 ====================
 * @param {String} modelValue - Cron 表达式值，支持 v-model
 * @param {Boolean} isRate - 是否实时响应外部变化，默认 false
 *
 * ==================== Events 事件 ====================
 * @event update:modelValue - Cron 表达式变化时触发
 *
 * ==================== 使用示例 ====================
 * <template>
 *   <Cron v-model="cronExpression" />
 *
 *   <div>当前 Cron: {{ cronExpression }}</div>
 * </template>
 *
 * <script setup>
 * import { ref } from 'vue'
 * import Cron from '@/components/cron/src/Cron.vue'
 *
 * const cronExpression = ref('0 0 12 * * ?')
 * </script>
 *
 * ==================== 常用 Cron 表达式示例 ====================
 * - 0 0 12 * * ?     - 每天中午12点
 * - 0 0 0 * * ?      - 每天凌晨
 * - 0 0/5 * * * ?    - 每5分钟
 * - 0 0 9-17 * * ?    - 每天9点到17点
 * - 0 0 0 1 * ?       - 每月1号凌晨
 * - 0 0 0 ? * MON     - 每周一凌晨
 * - 0 0,30 12,15 * * ? - 每天12点和15点的整点和30分
 */
-->
<script lang="ts" setup>
import { reactive, computed, watch, onBeforeMount } from 'vue'
import { propTypes } from '@/utils/propTypes'
import { useI18n } from '@/hooks/web/useI18n'
import { ElMessage } from 'element-plus-secondary'
import SecondAndMinute from './SecondAndMinute.vue'
import hour from './Hour.vue'
import day from './Day.vue'
import month from './Month.vue'
import week from './Week.vue'
import year from './Year.vue'
const props = defineProps({
  modelValue: propTypes.string.def('?'),
  isRate: propTypes.bool.def(false)
})

const { t } = useI18n()

const state = reactive({
  activeName: 's',
  sVal: '',
  mVal: '',
  hVal: '',
  dVal: '',
  monthVal: '',
  weekVal: '',
  yearVal: ''
})

const tableData = computed(() => {
  return [
    {
      sVal: state.sVal,
      mVal: state.mVal,
      hVal: state.hVal,
      dVal: state.dVal,
      monthVal: state.monthVal,
      weekVal: state.weekVal,
      yearVal: state.yearVal
    }
  ]
})

const resultValue = computed(() => {
  if (!state.dVal && !state.weekVal) {
    return ''
  }
  if (state.dVal === '?' && state.weekVal === '?') {
    ElMessage.error(t('cron.d_w_cant_not_set'))
  }
  if (state.dVal !== '?' && state.weekVal !== '?') {
    ElMessage.error(t('cron.d_w_must_one_set'))
  }
  const v = `${state.sVal} ${state.mVal} ${state.hVal} ${state.dVal} ${state.monthVal} ${state.weekVal} ${state.yearVal}`
  return v
})

onBeforeMount(() => {
  updateVal()
})

watch(
  () => props.modelValue,
  () => {
    if (!props.isRate) return
    updateVal()
  },
  {
    immediate: true
  }
)

watch(
  () => resultValue.value,
  () => {
    emits('update:modelValue', resultValue.value)
  }
)

const updateVal = () => {
  if (!props.modelValue) {
    return
  }
  const arrays = props.modelValue.split(' ')
  state.sVal = arrays[0]
  state.mVal = arrays[1]
  state.hVal = arrays[2]
  state.dVal = arrays[3]
  state.monthVal = arrays[4]
  state.weekVal = arrays[5]
  state.yearVal = arrays[6]
}

const emits = defineEmits(['update:modelValue'])
</script>

<template>
  <div>
    <el-tabs v-model="state.activeName">
      <el-tab-pane :label="t('cron.second')" name="s">
        <second-and-minute v-model="state.sVal" :label="t('cron.second')" />
      </el-tab-pane>
      <el-tab-pane :label="t('cron.minute')" name="m">
        <second-and-minute v-model="state.mVal" :label="t('cron.minute')" />
      </el-tab-pane>
      <el-tab-pane :label="t('cron.hour')" name="h">
        <hour v-model="state.hVal" :label="t('cron.hour')" />
      </el-tab-pane>
      <el-tab-pane :label="t('cron.day')" name="d">
        <day v-model="state.dVal" :label="t('cron.day')" />
      </el-tab-pane>
      <el-tab-pane :label="t('cron.month')" name="month">
        <month v-model="state.monthVal" :label="t('cron.month')" />
      </el-tab-pane>
      <el-tab-pane :label="t('cron.week')" name="week">
        <week v-model="state.weekVal" :label="t('cron.week')" />
      </el-tab-pane>
      <el-tab-pane :label="t('cron.year')" name="year">
        <year v-model="state.yearVal" :label="t('cron.year')" />
      </el-tab-pane>
    </el-tabs>
    <!-- table -->
    <el-table :data="tableData" size="mini" border style="width: 100%">
      <el-table-column prop="sVal" :label="t('cron.second')" width="70" />
      <el-table-column prop="mVal" :label="t('cron.minute')" width="70" />
      <el-table-column prop="hVal" :label="t('cron.hour')" width="70" />
      <el-table-column prop="dVal" :label="t('cron.day')" width="70" />
      <el-table-column prop="monthVal" :label="t('cron.month')" width="70" />
      <el-table-column prop="weekVal" :label="t('cron.week')" width="70" />
      <el-table-column prop="yearVal" :label="t('cron.year')" />
    </el-table>
  </div>
</template>
