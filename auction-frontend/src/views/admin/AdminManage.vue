<template>
  <div style="padding: 24px">
    <h2>系统管理</h2>
    <el-tabs v-model="activeTab">
      <el-tab-pane label="分类管理" name="category">
        <div style="display: flex; gap: 8px; margin-bottom: 12px">
          <el-input v-model="categoryForm.name" placeholder="分类名称" style="max-width: 220px" />
          <el-input-number v-model="categoryForm.sort" :min="0" />
          <el-button type="primary" @click="handleCreateCategory">新增分类</el-button>
        </div>
        <el-table :data="categories" v-loading="categoryLoading">
          <el-table-column prop="id" label="ID" width="80" />
          <el-table-column prop="name" label="名称" />
          <el-table-column prop="sort" label="排序" width="100" />
          <el-table-column label="操作" width="180">
            <template #default="{ row }">
              <el-button link @click="handleEditCategory(row)">编辑</el-button>
              <el-button type="danger" link @click="handleDeleteCategory(row.id)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>

      <el-tab-pane label="专场管理" name="session">
        <div style="display: flex; gap: 8px; margin-bottom: 12px">
          <el-input v-model="sessionForm.name" placeholder="专场名称" style="max-width: 220px" />
          <el-input v-model="sessionForm.description" placeholder="描述" style="max-width: 260px" />
          <el-button type="primary" @click="handleCreateSession">新增专场</el-button>
        </div>
        <el-table :data="sessions" v-loading="sessionLoading">
          <el-table-column prop="id" label="ID" width="80" />
          <el-table-column prop="name" label="名称" />
          <el-table-column prop="description" label="描述" />
          <el-table-column label="操作" width="180">
            <template #default="{ row }">
              <el-button link @click="handleEditSession(row)">编辑</el-button>
              <el-button type="danger" link @click="handleDeleteSession(row.id)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>

      <el-tab-pane label="权限管理" name="acl">
        <div style="display: flex; gap: 8px; margin-bottom: 12px">
          <el-input v-model="roleForm.roleCode" placeholder="角色编码" style="max-width: 200px" />
          <el-input v-model="roleForm.roleName" placeholder="角色名称" style="max-width: 200px" />
          <el-button type="primary" @click="handleCreateRole">新增角色</el-button>
        </div>
        <div style="display: flex; gap: 8px; margin-bottom: 12px">
          <el-input v-model="permForm.permCode" placeholder="权限编码" style="max-width: 220px" />
          <el-input v-model="permForm.permName" placeholder="权限名称" style="max-width: 220px" />
          <el-button type="primary" @click="handleCreatePerm">新增权限</el-button>
        </div>
        <div style="display: flex; gap: 8px; margin-bottom: 12px">
          <el-input-number v-model="bindRoleForm.userId" :min="1" />
          <el-select v-model="bindRoleForm.roleId" placeholder="角色" style="width: 180px">
            <el-option v-for="r in roles" :key="r.id" :label="`${r.roleName}(${r.roleCode})`" :value="r.id" />
          </el-select>
          <el-button @click="handleBindRole">绑定角色到用户</el-button>
        </div>
        <div style="display: flex; gap: 8px; margin-bottom: 12px">
          <el-select v-model="bindPermForm.roleId" placeholder="角色" style="width: 180px">
            <el-option v-for="r in roles" :key="r.id" :label="r.roleName" :value="r.id" />
          </el-select>
          <el-select v-model="bindPermForm.permissionId" placeholder="权限" style="width: 260px">
            <el-option v-for="p in permissions" :key="p.id" :label="`${p.permName}(${p.permCode})`" :value="p.id" />
          </el-select>
          <el-button @click="handleBindPerm">绑定权限到角色</el-button>
        </div>
      </el-tab-pane>

      <el-tab-pane label="操作日志" name="log">
        <div style="margin-bottom: 12px">
          <el-button @click="loadLogs">刷新日志</el-button>
        </div>
        <el-table :data="pagedLogs" v-loading="logLoading">
          <el-table-column prop="id" label="ID" width="80" />
          <el-table-column prop="userId" label="用户ID" width="90" />
          <el-table-column prop="action" label="动作" />
          <el-table-column prop="targetType" label="目标类型" width="120" />
          <el-table-column prop="targetId" label="目标ID" width="90" />
          <el-table-column prop="detail" label="详情" />
        </el-table>
        <div style="display: flex; justify-content: flex-end; margin-top: 12px">
          <el-pagination
            v-model:current-page="logPage"
            v-model:page-size="logPageSize"
            :page-sizes="[10, 20, 50]"
            layout="total, sizes, prev, pager, next"
            :total="logs.length"
          />
        </div>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from "vue";
import { ElMessage, ElMessageBox } from "element-plus";
import {
  createAdminCategory,
  createAdminSession,
  deleteAdminCategory,
  deleteAdminSession,
  getAdminCategories,
  getAdminLogs,
  getAdminSessions,
  updateAdminCategory,
  updateAdminSession
} from "@/api/admin";
import { bindPermission, bindRole, createPermission, createRole, getPermissions, getRoles } from "@/api/system";

const activeTab = ref("category");
const categories = ref<any[]>([]);
const sessions = ref<any[]>([]);
const logs = ref<any[]>([]);
const roles = ref<any[]>([]);
const permissions = ref<any[]>([]);
const categoryLoading = ref(false);
const sessionLoading = ref(false);
const logLoading = ref(false);
const logPage = ref(1);
const logPageSize = ref(10);

const categoryForm = ref({ name: "", sort: 0 });
const sessionForm = ref({ name: "", description: "" });
const roleForm = ref({ roleCode: "", roleName: "", status: 1 });
const permForm = ref({ permCode: "", permName: "" });
const bindRoleForm = ref({ userId: 2, roleId: undefined as number | undefined });
const bindPermForm = ref({ roleId: undefined as number | undefined, permissionId: undefined as number | undefined });
const pagedLogs = computed(() => {
  const start = (logPage.value - 1) * logPageSize.value;
  return logs.value.slice(start, start + logPageSize.value);
});

const loadCategories = async () => {
  categoryLoading.value = true;
  try {
    const res = await getAdminCategories();
    categories.value = res.data || [];
  } finally {
    categoryLoading.value = false;
  }
};
const loadSessions = async () => {
  sessionLoading.value = true;
  try {
    const res = await getAdminSessions();
    sessions.value = res.data || [];
  } finally {
    sessionLoading.value = false;
  }
};
const loadLogs = async () => {
  logLoading.value = true;
  try {
    const res = await getAdminLogs(200);
    logs.value = res.data || [];
    if (logPage.value !== 1) {
      logPage.value = 1;
    }
  } finally {
    logLoading.value = false;
  }
};
const loadAcl = async () => {
  const [roleRes, permRes] = await Promise.all([getRoles(), getPermissions()]);
  roles.value = roleRes.data || [];
  permissions.value = permRes.data || [];
};

const handleCreateCategory = async () => {
  if (!categoryForm.value.name) return ElMessage.warning("请输入分类名称");
  try {
    await createAdminCategory({ ...categoryForm.value, status: 1, parentId: 0 });
    categoryForm.value.name = "";
    await loadCategories();
    ElMessage.success("新增分类成功");
  } catch (error) {
    ElMessage.error("新增分类失败");
  }
};
const handleDeleteCategory = async (id: number) => {
  try {
    await deleteAdminCategory(id);
    await loadCategories();
    ElMessage.success("删除分类成功");
  } catch (error) {
    ElMessage.error("删除分类失败");
  }
};
const handleEditCategory = async (row: any) => {
  try {
    const { value: name } = await ElMessageBox.prompt("请输入新分类名称", "编辑分类", {
      inputValue: row.name
    });
    await updateAdminCategory({ ...row, name });
    await loadCategories();
    ElMessage.success("更新分类成功");
  } catch (error) {
    // cancel or failed request
  }
};

const handleCreateSession = async () => {
  if (!sessionForm.value.name) return ElMessage.warning("请输入专场名称");
  try {
    await createAdminSession({ ...sessionForm.value, status: 1 });
    sessionForm.value.name = "";
    sessionForm.value.description = "";
    await loadSessions();
    ElMessage.success("新增专场成功");
  } catch (error) {
    ElMessage.error("新增专场失败");
  }
};
const handleDeleteSession = async (id: number) => {
  try {
    await deleteAdminSession(id);
    await loadSessions();
    ElMessage.success("删除专场成功");
  } catch (error) {
    ElMessage.error("删除专场失败");
  }
};
const handleEditSession = async (row: any) => {
  try {
    const { value: name } = await ElMessageBox.prompt("请输入新专场名称", "编辑专场", {
      inputValue: row.name
    });
    await updateAdminSession({ ...row, name });
    await loadSessions();
    ElMessage.success("更新专场成功");
  } catch (error) {
    // cancel or failed request
  }
};

const handleCreateRole = async () => {
  if (!roleForm.value.roleCode || !roleForm.value.roleName) return ElMessage.warning("请填写角色信息");
  try {
    await createRole(roleForm.value);
    roleForm.value.roleCode = "";
    roleForm.value.roleName = "";
    await loadAcl();
    ElMessage.success("新增角色成功");
  } catch (error) {
    ElMessage.error("新增角色失败");
  }
};
const handleCreatePerm = async () => {
  if (!permForm.value.permCode || !permForm.value.permName) return ElMessage.warning("请填写权限信息");
  try {
    await createPermission(permForm.value);
    permForm.value.permCode = "";
    permForm.value.permName = "";
    await loadAcl();
    ElMessage.success("新增权限成功");
  } catch (error) {
    ElMessage.error("新增权限失败");
  }
};
const handleBindRole = async () => {
  if (!bindRoleForm.value.roleId) return ElMessage.warning("请选择角色");
  try {
    await bindRole(bindRoleForm.value);
    ElMessage.success("绑定角色成功");
  } catch (error) {
    ElMessage.error("绑定角色失败");
  }
};
const handleBindPerm = async () => {
  if (!bindPermForm.value.roleId || !bindPermForm.value.permissionId) return ElMessage.warning("请选择角色与权限");
  try {
    await bindPermission(bindPermForm.value);
    ElMessage.success("绑定权限成功");
  } catch (error) {
    ElMessage.error("绑定权限失败");
  }
};

onMounted(async () => {
  try {
    await Promise.all([loadCategories(), loadSessions(), loadAcl(), loadLogs()]);
  } catch (error) {
    ElMessage.error("管理页数据加载失败，请确认使用管理员token");
  }
});
</script>
