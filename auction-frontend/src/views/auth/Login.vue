<template>
  <div style="height: 100vh; display: grid; place-items: center; background: #f6f8fa">
    <el-card style="width: 420px">
      <template #header>登录</template>
      <el-form label-width="90px">
        <el-form-item label="用户名">
          <el-input v-model="form.username" />
        </el-form-item>
        <el-form-item label="密码">
          <el-input v-model="form.password" type="password" show-password />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :loading="loading" @click="handleLogin">登录</el-button>
        </el-form-item>
      </el-form>
      <div style="color: #666; font-size: 12px">
        后端当前是最小 token 机制，登录成功会拿到 `uid:{id}` 并写入本地存储。
      </div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref } from "vue";
import { useRouter } from "vue-router";
import { ElMessage } from "element-plus";
import request from "@/utils/request";
import { useUserStore } from "@/stores/user";

const router = useRouter();
const userStore = useUserStore();
const loading = ref(false);
const form = ref({ username: "", password: "" });

const handleLogin = async () => {
  if (!form.value.username || !form.value.password) return ElMessage.warning("请输入用户名和密码");
  loading.value = true;
  try {
    const res: any = await request({
      url: "/user/login",
      method: "post",
      data: form.value
    });
    userStore.setToken(res.data.token);
    ElMessage.success("登录成功");
    router.push("/dashboard/user");
  } finally {
    loading.value = false;
  }
};
</script>
