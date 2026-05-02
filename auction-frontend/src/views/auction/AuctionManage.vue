<template>
  <div>
    <h2>拍卖管理</h2>
    <el-card style="margin-bottom: 12px">
      <template #header>专场列表</template>
      <el-table :data="sessions">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="name" label="名称" />
        <el-table-column prop="description" label="描述" />
      </el-table>
    </el-card>
    <el-card>
      <template #header>实时竞价入口</template>
      <div style="display: flex; gap: 8px; margin-bottom: 12px">
        <el-input-number v-model="liveItemId" :min="1" />
        <el-button type="primary" @click="goLive">进入实时竞价</el-button>
      </div>
      <el-table :data="auctionList" v-loading="loading">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="title" label="标题" />
        <el-table-column prop="currentPrice" label="当前价" />
        <el-table-column label="操作" width="200">
          <template #default="{ row }">
            <el-button link type="primary" @click="router.push(`/auction/live/${row.id}`)">竞价</el-button>
            <el-button link type="danger" :disabled="row.status !== 3" @click="handleEnd(row.id)">结束拍卖</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { onMounted, ref } from "vue";
import { useRouter } from "vue-router";
import { ElMessage } from "element-plus";
import { endAuction, getAuctionList, getSessionList } from "@/api/auction";

const router = useRouter();
const liveItemId = ref(1);
const sessions = ref<any[]>([]);
const auctionList = ref<any[]>([]);
const loading = ref(false);

const loadData = async () => {
  loading.value = true;
  try {
    const [sessionRes, listRes] = await Promise.all([getSessionList(), getAuctionList()]);
    sessions.value = sessionRes.data || [];
    auctionList.value = listRes.data || [];
  } finally {
    loading.value = false;
  }
};

const goLive = () => {
  router.push(`/auction/live/${liveItemId.value}`);
};
const handleEnd = async (itemId: number) => {
  await endAuction(itemId);
  ElMessage.success("已结束拍卖");
  await loadData();
};

onMounted(loadData);
</script>
