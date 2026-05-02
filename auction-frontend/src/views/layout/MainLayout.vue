<template>
  <el-container style="height: 100vh">
    <el-aside width="220px" style="border-right: 1px solid #eee">
      <div style="padding: 16px; font-weight: 700">拍卖系统后台</div>
      <el-menu :default-active="route.path" router>
        <el-menu-item index="/dashboard/user">用户管理</el-menu-item>
        <el-menu-item index="/dashboard/item">拍品管理</el-menu-item>
        <el-menu-item index="/dashboard/auction">拍卖管理</el-menu-item>
        <el-menu-item index="/dashboard/order">订单管理</el-menu-item>
        <el-menu-item index="/dashboard/system">系统管理</el-menu-item>
      </el-menu>
    </el-aside>
    <el-main style="padding-top: 12px">
      <div style="display: flex; justify-content: flex-end; margin-bottom: 12px">
        <el-button size="small" @click="logout">退出登录</el-button>
      </div>
      <router-view />
    </el-main>
  </el-container>
</template>

<script setup lang="ts">
import { useRoute, useRouter } from "vue-router";
import { useUserStore } from "@/stores/user";

const route = useRoute();
const router = useRouter();
const userStore = useUserStore();

const logout = () => {
  userStore.logout();
  router.push("/login");
};
</script>
