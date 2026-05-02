<template>
  <div>
    <h2>用户管理</h2>
    <el-tabs v-model="activeTab">
      <el-tab-pane label="个人中心" name="me">
        <div style="display: grid; grid-template-columns: 1fr 1fr; gap: 16px">
          <el-card>
            <template #header>我的信息</template>
            <el-descriptions :column="1" border>
              <el-descriptions-item label="ID">{{ me?.id }}</el-descriptions-item>
              <el-descriptions-item label="用户名">{{ me?.username }}</el-descriptions-item>
              <el-descriptions-item label="昵称">{{ me?.nickname }}</el-descriptions-item>
              <el-descriptions-item label="实名状态">{{ me?.authStatus }}</el-descriptions-item>
              <el-descriptions-item label="保证金">{{ me?.deposit }}</el-descriptions-item>
            </el-descriptions>
            <el-button style="margin-top: 12px" @click="loadMe">刷新</el-button>
          </el-card>

          <el-card>
            <template #header>实名与保证金</template>
            <div style="display: flex; gap: 8px; margin-bottom: 8px">
              <el-input v-model="verifyForm.realName" placeholder="真实姓名" />
              <el-input v-model="verifyForm.idCard" placeholder="身份证号" />
              <el-button type="primary" @click="handleVerify">提交实名</el-button>
            </div>
            <div style="display: flex; gap: 8px">
              <el-input-number v-model="depositAmount" :min="1" />
              <el-button type="primary" @click="handleDeposit">充值保证金</el-button>
            </div>
          </el-card>
        </div>
      </el-tab-pane>

      <el-tab-pane label="用户列表与权限" name="users">
        <el-card style="margin-bottom: 12px">
          <template #header>新增用户</template>
          <div style="display: flex; gap: 8px; flex-wrap: wrap">
            <el-input v-model="createForm.username" placeholder="用户名" style="max-width: 180px" />
            <el-input v-model="createForm.password" placeholder="密码" style="max-width: 180px" />
            <el-input v-model="createForm.nickname" placeholder="昵称" style="max-width: 180px" />
            <el-button type="primary" @click="handleCreateUser">新增</el-button>
          </div>
        </el-card>

        <el-card>
          <template #header>用户管理（编辑/删除/买卖角色分配）</template>
          <el-table :data="users">
            <el-table-column prop="id" label="ID" width="70" />
            <el-table-column prop="username" label="用户名" />
            <el-table-column prop="nickname" label="昵称" />
            <el-table-column prop="status" label="状态" width="90" />
            <el-table-column prop="authStatus" label="实名" width="90" />
            <el-table-column prop="deposit" label="保证金" width="120" />
            <el-table-column label="角色" width="220">
              <template #default="{ row }">
                <el-tag
                  v-for="r in row._roles"
                  :key="`${row.id}-${r.roleId}`"
                  closable
                  style="margin-right: 6px"
                  @close="handleUnbindRole(row.id, r.roleId)"
                >
                  {{ r.roleName || r.roleCode }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="460">
              <template #default="{ row }">
                <el-button link @click="handleEditUser(row)">编辑</el-button>
                <el-button link type="danger" @click="handleDeleteUser(row.id)">删除</el-button>
                <el-select v-model="row._bindRoleId" placeholder="角色" style="width: 120px">
                  <el-option v-for="r in roles" :key="r.id" :label="r.roleName" :value="r.id" />
                </el-select>
                <el-button link type="primary" @click="handleBindRole(row)">绑定角色</el-button>
                <el-button link @click="handleQuickBind(row, 'buyer')">设为买家</el-button>
                <el-button link @click="handleQuickBind(row, 'seller')">设为卖家</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script setup lang="ts">
import { onMounted, ref } from "vue";
import { ElMessage, ElMessageBox } from "element-plus";
import { depositMe, deleteUserById, getMe, getUserList, registerUser, updateUserById, verifyMe } from "@/api/user";
import { bindRole, getRoles, getUserRoleAssignments, unbindRole } from "@/api/system";

const activeTab = ref("me");
const me = ref<any>(null);
const users = ref<any[]>([]);
const roles = ref<any[]>([]);
const verifyForm = ref({ realName: "", idCard: "" });
const depositAmount = ref(1000);
const createForm = ref({ username: "", password: "", nickname: "" });

const loadMe = async () => {
  const res = await getMe();
  me.value = res.data;
};
const loadUsers = async () => {
  const res = await getUserList();
  users.value = (res.data || []).map((u: any) => ({ ...u, _bindRoleId: undefined, _roles: [] }));
  const ids = users.value.map((u: any) => u.id);
  if (!ids.length) return;
  const roleRes = await getUserRoleAssignments(ids);
  const assignments = roleRes.data || [];
  const roleMap = new Map<number, any[]>();
  for (const a of assignments) {
    const list = roleMap.get(a.userId) || [];
    list.push(a);
    roleMap.set(a.userId, list);
  }
  users.value = users.value.map((u: any) => ({ ...u, _roles: roleMap.get(u.id) || [] }));
};
const loadRoles = async () => {
  const res = await getRoles();
  roles.value = res.data || [];
};
const handleVerify = async () => {
  if (!verifyForm.value.realName || !verifyForm.value.idCard) return ElMessage.warning("请填写实名信息");
  await verifyMe(verifyForm.value);
  ElMessage.success("实名认证成功");
  await loadMe();
};
const handleDeposit = async () => {
  await depositMe({ amount: depositAmount.value });
  ElMessage.success("保证金充值成功");
  await loadMe();
};

const handleCreateUser = async () => {
  if (!createForm.value.username || !createForm.value.password) return ElMessage.warning("请填写用户名和密码");
  await registerUser(createForm.value);
  createForm.value.username = "";
  createForm.value.password = "";
  createForm.value.nickname = "";
  ElMessage.success("新增用户成功");
  await loadUsers();
};

const handleDeleteUser = async (id: number) => {
  await deleteUserById(id);
  ElMessage.success("删除用户成功");
  await loadUsers();
};

const handleEditUser = async (row: any) => {
  const { value } = await ElMessageBox.prompt("请输入新的昵称", "编辑用户", { inputValue: row.nickname || "" });
  await updateUserById(row.id, {
    nickname: value,
    phone: row.phone,
    email: row.email,
    status: row.status,
    authStatus: row.authStatus,
    deposit: row.deposit
  });
  ElMessage.success("编辑用户成功");
  await loadUsers();
};

const handleBindRole = async (row: any) => {
  if (!row._bindRoleId) return ElMessage.warning("请选择角色");
  await bindRole({ userId: row.id, roleId: row._bindRoleId });
  ElMessage.success("角色绑定成功");
  await loadUsers();
};

const handleUnbindRole = async (userId: number, roleId: number) => {
  await unbindRole({ userId, roleId });
  ElMessage.success("角色移除成功");
  await loadUsers();
};

const handleQuickBind = async (row: any, roleCode: "buyer" | "seller") => {
  const role = roles.value.find((r: any) => r.roleCode === roleCode);
  if (!role) return ElMessage.warning(`未找到角色：${roleCode}`);
  await bindRole({ userId: row.id, roleId: role.id });
  ElMessage.success(`已分配${role.roleName}`);
  await loadUsers();
};

onMounted(async () => {
  await Promise.all([loadMe(), loadUsers(), loadRoles()]);
});
</script>
