<template>
  <div class="organization-container">
    <!-- 页面标题 -->
    <p class="router-title">{{ t('organization.title') }}</p>

    <!-- 内容区域 -->
    <div class="organization-content">
      <!-- 左侧组织树 -->
      <div class="org-tree-section">
        <div class="tree-header">
          <span class="tree-title">{{ t('organization.org_tree') }}</span>
          <el-dropdown @command="handleRootCommand">
            <el-button type="primary" link>
              <el-icon><Plus /></el-icon>
            </el-button>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="addDept">{{
                  t('organization.add_dept')
                }}</el-dropdown-item>
                <el-dropdown-item command="addUser">{{
                  t('organization.add_user')
                }}</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
        <div class="tree-content">
          <el-tree
            ref="treeRef"
            :data="orgTreeData"
            :props="treeProps"
            :highlight-current="true"
            node-key="id"
            @node-click="handleNodeClick"
          >
            <template #default="{ node, data }">
              <div class="custom-tree-node">
                <span class="node-label">
                  <el-icon v-if="data.type === 'dept'"><OfficeBuilding /></el-icon>
                  <el-icon v-else><User /></el-icon>
                  {{ node.label }}
                </span>
                <el-dropdown trigger="click" @command="cmd => handleNodeCommand(cmd, data)">
                  <el-icon class="more-icon" @click.stop>
                    <MoreFilled />
                  </el-icon>
                  <template #dropdown>
                    <el-dropdown-menu>
                      <!-- 部门操作 -->
                      <template v-if="data.type === 'dept'">
                        <el-dropdown-item command="addSubDept">
                          {{ t('organization.add_sub_dept') }}
                        </el-dropdown-item>
                        <el-dropdown-item command="addUser">{{
                          t('organization.add_user')
                        }}</el-dropdown-item>
                        <el-dropdown-item command="rename">{{
                          t('organization.rename')
                        }}</el-dropdown-item>
                        <el-dropdown-item command="delete" divided>{{
                          t('organization.delete')
                        }}</el-dropdown-item>
                      </template>
                      <!-- 用户操作 -->
                      <template v-else>
                        <el-dropdown-item command="edit">{{
                          t('organization.edit_user')
                        }}</el-dropdown-item>
                        <el-dropdown-item command="delete" divided>{{
                          t('organization.delete_user')
                        }}</el-dropdown-item>
                      </template>
                    </el-dropdown-menu>
                  </template>
                </el-dropdown>
              </div>
            </template>
          </el-tree>
        </div>
      </div>

      <!-- 右侧详情区域 -->
      <div class="org-detail-section">
        <div v-if="currentNode" class="detail-content">
          <!-- 部门详情 -->
          <div v-if="currentNode.type === 'dept'" class="dept-detail">
            <div class="detail-header">
              <el-icon class="dept-icon"><OfficeBuilding /></el-icon>
              <span class="dept-name">{{ currentNode.label }}</span>
              <el-tag size="small">{{ t('organization.department') }}</el-tag>
            </div>
            <div class="detail-info">
              <div class="info-item">
                <span class="info-label">{{ t('organization.dept_id') }}:</span>
                <span class="info-value">{{ currentNode.id }}</span>
              </div>
              <div class="info-item">
                <span class="info-label">{{ t('organization.sub_dept_count') }}:</span>
                <span class="info-value">{{ currentNode.subDeptCount || 0 }}</span>
              </div>
              <div class="info-item">
                <span class="info-label">{{ t('organization.user_count') }}:</span>
                <span class="info-value">{{ currentNode.userCount || 0 }}</span>
              </div>
            </div>
          </div>

          <!-- 用户详情 -->
          <div v-else class="user-detail">
            <div class="detail-header">
              <el-avatar :size="60" :src="currentNode.avatar" />
              <div class="user-info">
                <div class="user-name">
                  {{ currentNode.label }}
                  <el-tag v-if="currentNode.isAdmin" size="small" type="danger">
                    {{ t('organization.admin') }}
                  </el-tag>
                </div>
                <div class="user-email">{{ currentNode.email }}</div>
              </div>
            </div>
            <div class="detail-info">
              <div class="info-item">
                <span class="info-label">{{ t('organization.username') }}:</span>
                <span class="info-value">{{ currentNode.username }}</span>
              </div>
              <div class="info-item">
                <span class="info-label">{{ t('organization.phone') }}:</span>
                <span class="info-value">{{ currentNode.phone || '-' }}</span>
              </div>
              <div class="info-item">
                <span class="info-label">{{ t('organization.department') }}:</span>
                <span class="info-value">{{ currentNode.deptName }}</span>
              </div>
              <div class="info-item">
                <span class="info-label">{{ t('organization.status') }}:</span>
                <el-tag :type="currentNode.status === 'active' ? 'success' : 'danger'" size="small">
                  {{
                    currentNode.status === 'active'
                      ? t('organization.active')
                      : t('organization.inactive')
                  }}
                </el-tag>
              </div>
            </div>
          </div>
        </div>

        <!-- 空状态 -->
        <div v-else class="empty-state">
          <el-empty :description="t('organization.select_node_tip')" />
        </div>
      </div>
    </div>
  </div>

  <!-- 添加部门弹窗 -->
  <el-dialog v-model="deptDialogVisible" :title="dialogTitle" width="500px" @close="resetDeptForm">
    <el-form ref="deptFormRef" :model="deptForm" :rules="deptRules" label-width="100px">
      <el-form-item :label="t('organization.dept_name')" prop="name">
        <el-input v-model="deptForm.name" :placeholder="t('organization.input_dept_name')" />
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="deptDialogVisible = false">{{ t('commons.cancel') }}</el-button>
      <el-button type="primary" @click="submitDeptForm">{{ t('commons.confirm') }}</el-button>
    </template>
  </el-dialog>

  <!-- 添加/编辑用户弹窗 -->
  <el-dialog v-model="userDialogVisible" :title="dialogTitle" width="600px" @close="resetUserForm">
    <el-form ref="userFormRef" :model="userForm" :rules="userRules" label-width="100px">
      <el-form-item :label="t('organization.username')" prop="username">
        <el-input v-model="userForm.username" :placeholder="t('organization.input_username')" />
      </el-form-item>
      <el-form-item :label="t('organization.real_name')" prop="realName">
        <el-input v-model="userForm.realName" :placeholder="t('organization.input_real_name')" />
      </el-form-item>
      <el-form-item :label="t('organization.email')" prop="email">
        <el-input v-model="userForm.email" :placeholder="t('organization.input_email')" />
      </el-form-item>
      <el-form-item :label="t('organization.phone')" prop="phone">
        <el-input v-model="userForm.phone" :placeholder="t('organization.input_phone')" />
      </el-form-item>
      <el-form-item :label="t('organization.password')" prop="password" v-if="!isEditUser">
        <el-input
          v-model="userForm.password"
          type="password"
          :placeholder="t('organization.input_password')"
          show-password
        />
      </el-form-item>
      <el-form-item :label="t('organization.department')" prop="deptId">
        <el-cascader
          v-model="userForm.deptId"
          :options="deptOptions"
          :props="{ checkStrictly: true, value: 'id', label: 'label' }"
          :placeholder="t('organization.select_dept')"
          clearable
        />
      </el-form-item>
      <el-form-item :label="t('organization.isAdmin')" prop="isAdmin">
        <el-switch v-model="userForm.isAdmin" />
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="userDialogVisible = false">{{ t('commons.cancel') }}</el-button>
      <el-button type="primary" @click="submitUserForm">{{ t('commons.confirm') }}</el-button>
    </template>
  </el-dialog>
</template>

<script setup lang="ts" name="SystemOrganization">
import { ref, reactive, computed } from 'vue'
import { useI18n } from '@/hooks/web/useI18n'
import { Plus, OfficeBuilding, User, MoreFilled } from '@element-plus/icons-vue'

const { t } = useI18n()

// ==================== 类型定义 ====================

interface TreeNode {
  id: string | number
  label: string
  type: 'dept' | 'user'
  children?: TreeNode[]
  // 部门特有属性
  subDeptCount?: number
  userCount?: number
  // 用户特有属性
  username?: string
  email?: string
  phone?: string
  deptName?: string
  avatar?: string
  isAdmin?: boolean
  status?: 'active' | 'inactive'
}

interface DeptForm {
  name: string
  parentId?: string | number
}

interface UserForm {
  username: string
  realName: string
  email: string
  phone?: string
  password?: string
  deptId?: string | number | (string | number)[]
  isAdmin: boolean
}

// ==================== 树形数据 ====================

const treeRef = ref()
const treeProps = {
  children: 'children',
  label: 'label'
}

// 模拟组织架构数据
const orgTreeData = ref<TreeNode[]>([
  {
    id: '1',
    label: 'DataEase',
    type: 'dept',
    subDeptCount: 2,
    userCount: 3,
    children: [
      {
        id: '1-1',
        label: '研发部',
        type: 'dept',
        subDeptCount: 1,
        userCount: 2,
        children: [
          {
            id: '1-1-1',
            label: '前端组',
            type: 'dept',
            subDeptCount: 0,
            userCount: 1
          }
        ]
      },
      {
        id: '1-2',
        label: '产品部',
        type: 'dept',
        subDeptCount: 0,
        userCount: 1
      },
      {
        id: 'u-1',
        label: '张三',
        type: 'user',
        username: 'zhangsan',
        email: 'zhangsan@dataease.com',
        phone: '13800138000',
        deptName: '研发部',
        isAdmin: true,
        status: 'active'
      },
      {
        id: 'u-2',
        label: '李四',
        type: 'user',
        username: 'lisi',
        email: 'lisi@dataease.com',
        deptName: '研发部',
        isAdmin: false,
        status: 'active'
      }
    ]
  }
])

// ==================== 当前选中节点 ====================

const currentNode = ref<TreeNode | null>(null)

const handleNodeClick = (data: TreeNode) => {
  currentNode.value = data
}

// ==================== 树节点操作 ====================

const handleRootCommand = (command: string) => {
  switch (command) {
    case 'addDept':
      openDeptDialog()
      break
    case 'addUser':
      openUserDialog()
      break
  }
}

const handleNodeCommand = (command: string, data: TreeNode) => {
  switch (command) {
    case 'addSubDept':
      openDeptDialog(data.id)
      break
    case 'addUser':
      openUserDialog(data.id)
      break
    case 'rename':
      openDeptDialog(undefined, data)
      break
    case 'edit':
      openUserDialog(undefined, data)
      break
    case 'delete':
      if (data.type === 'dept') {
        deleteDept(data)
      } else {
        deleteUser(data)
      }
      break
  }
}

// ==================== 部门表单 ====================

const deptDialogVisible = ref(false)
const deptFormRef = ref<FormInstance>()
const deptForm = reactive<DeptForm>({
  name: ''
})
const editingDeptNode = ref<TreeNode | null>(null)

const deptRules: FormRules = {
  name: [{ required: true, message: t('organization.dept_name_required'), trigger: 'blur' }]
}

const dialogTitle = computed(() => {
  if (editingDeptNode.value) {
    return t('organization.edit_dept')
  }
  return t('organization.add_dept')
})

const openDeptDialog = (parentId?: string | number, node?: TreeNode) => {
  if (node) {
    editingDeptNode.value = node
    deptForm.name = node.label
  } else {
    editingDeptNode.value = null
    deptForm.name = ''
  }
  deptForm.parentId = parentId
  deptDialogVisible.value = true
}

const submitDeptForm = async () => {
  if (!deptFormRef.value) return

  await deptFormRef.value.validate(valid => {
    if (valid) {
      // TODO: 调用后端 API 创建/更新部门
      ElMessage.success(
        editingDeptNode.value
          ? t('organization.update_dept_success')
          : t('organization.create_dept_success')
      )
      deptDialogVisible.value = false
    }
  })
}

const resetDeptForm = () => {
  deptFormRef.value?.resetFields()
  editingDeptNode.value = null
}

const deleteDept = (data: TreeNode) => {
  ElMessageBox.confirm(t('organization.delete_dept_confirm'), t('commons.tip'), {
    confirmButtonText: t('commons.confirm'),
    cancelButtonText: t('commons.cancel'),
    type: 'warning'
  }).then(() => {
    // TODO: 调用后端 API 删除部门
    ElMessage.success(t('organization.delete_dept_success'))
  })
}

// ==================== 用户表单 ====================

const userDialogVisible = ref(false)
const userFormRef = ref<FormInstance>()
const userForm = reactive<UserForm>({
  username: '',
  realName: '',
  email: '',
  phone: '',
  password: '',
  isAdmin: false
})
const editingUserNode = ref<TreeNode | null>(null)
const isEditUser = computed(() => !!editingUserNode.value)

const userRules: FormRules = {
  username: [{ required: true, message: t('organization.username_required'), trigger: 'blur' }],
  realName: [{ required: true, message: t('organization.real_name_required'), trigger: 'blur' }],
  email: [
    { required: true, message: t('organization.email_required'), trigger: 'blur' },
    { type: 'email', message: t('organization.email_format_error'), trigger: 'blur' }
  ],
  password: [{ required: true, message: t('organization.password_required'), trigger: 'blur' }]
}

// 部门选项（级联选择器）
const deptOptions = computed(() => {
  // 过滤出只有部门的数据
  const filterDepts = (nodes: TreeNode[]): TreeNode[] => {
    return nodes
      .filter(node => node.type === 'dept')
      .map(node => ({
        ...node,
        children: node.children ? filterDepts(node.children) : undefined
      }))
  }
  return filterDepts(orgTreeData.value)
})

const openUserDialog = (deptId?: string | number, node?: TreeNode) => {
  if (node) {
    editingUserNode.value = node
    userForm.username = node.username || ''
    userForm.realName = node.label
    userForm.email = node.email || ''
    userForm.phone = node.phone || ''
    userForm.isAdmin = node.isAdmin || false
  } else {
    editingUserNode.value = null
    userForm.username = ''
    userForm.realName = ''
    userForm.email = ''
    userForm.phone = ''
    userForm.password = ''
    userForm.isAdmin = false
  }
  userForm.deptId = deptId
  userDialogVisible.value = true
}

const submitUserForm = async () => {
  if (!userFormRef.value) return

  await userFormRef.value.validate(valid => {
    if (valid) {
      // TODO: 调用后端 API 创建/更新用户
      ElMessage.success(
        isEditUser.value
          ? t('organization.update_user_success')
          : t('organization.create_user_success')
      )
      userDialogVisible.value = false
    }
  })
}

const resetUserForm = () => {
  userFormRef.value?.resetFields()
  editingUserNode.value = null
}

const deleteUser = (data: TreeNode) => {
  ElMessageBox.confirm(t('organization.delete_user_confirm'), t('commons.tip'), {
    confirmButtonText: t('commons.confirm'),
    cancelButtonText: t('commons.cancel'),
    type: 'warning'
  }).then(() => {
    // TODO: 调用后端 API 删除用户
    ElMessage.success(t('organization.delete_user_success'))
  })
}
</script>

<style scoped lang="less">
.organization-container {
  padding: 24px;
  background-color: var(--ContentBG, #ffffff);
}

.router-title {
  color: #1f2329;
  font-feature-settings: 'clig' off, 'liga' off;
  font-family: var(--de-custom_font, 'PingFang');
  font-size: 20px;
  font-style: normal;
  font-weight: 500;
  line-height: 28px;
  margin-bottom: 16px;
}

.organization-content {
  display: flex;
  gap: 16px;
  height: calc(100vh - 200px);
}

// 左侧组织树
.org-tree-section {
  width: 320px;
  background: #ffffff;
  border: 1px solid #dee0e3;
  border-radius: 4px;
  display: flex;
  flex-direction: column;

  .tree-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 16px;
    border-bottom: 1px solid #dee0e3;

    .tree-title {
      font-size: 14px;
      font-weight: 500;
      color: #1f2329;
    }
  }

  .tree-content {
    flex: 1;
    overflow-y: auto;
    padding: 12px;
  }

  .custom-tree-node {
    display: flex;
    align-items: center;
    justify-content: space-between;
    width: 100%;
    padding-right: 8px;

    .node-label {
      display: flex;
      align-items: center;
      gap: 6px;
      font-size: 14px;
      color: #1f2329;

      .el-icon {
        color: #646a73;
      }
    }

    .more-icon {
      color: #646a73;
      cursor: pointer;
      font-size: 16px;

      &:hover {
        color: #3370ff;
      }
    }
  }

  :deep(.el-tree-node__content) {
    height: 36px;
    border-radius: 4px;

    &:hover {
      background-color: #f5f6f7;
    }
  }

  :deep(.el-tree-node.is-current > .el-tree-node__content) {
    background-color: #e5e9ff;
    color: #3370ff;
  }
}

// 右侧详情区域
.org-detail-section {
  flex: 1;
  background: #ffffff;
  border: 1px solid #dee0e3;
  border-radius: 4px;
  padding: 24px;
  overflow-y: auto;

  .detail-content {
    .dept-detail,
    .user-detail {
      .detail-header {
        display: flex;
        align-items: center;
        gap: 12px;
        padding-bottom: 24px;
        border-bottom: 1px solid #dee0e3;
        margin-bottom: 24px;

        .dept-icon {
          font-size: 32px;
          color: #3370ff;
        }

        .dept-name {
          font-size: 18px;
          font-weight: 500;
          color: #1f2329;
        }

        .user-info {
          .user-name {
            font-size: 16px;
            font-weight: 500;
            color: #1f2329;
            display: flex;
            align-items: center;
            gap: 8px;
          }

          .user-email {
            font-size: 14px;
            color: #646a73;
            margin-top: 4px;
          }
        }
      }

      .detail-info {
        display: flex;
        flex-direction: column;
        gap: 16px;

        .info-item {
          display: flex;
          align-items: center;
          font-size: 14px;

          .info-label {
            width: 100px;
            color: #646a73;
          }

          .info-value {
            color: #1f2329;
          }
        }
      }
    }
  }

  .empty-state {
    display: flex;
    align-items: center;
    justify-content: center;
    height: 100%;
  }
}

// 弹窗样式调整
:deep(.el-dialog__body) {
  padding: 20px;
}
</style>
