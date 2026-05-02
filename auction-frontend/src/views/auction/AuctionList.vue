<template>
  <div style="padding: 24px">
    <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 12px">
      <h2 style="margin: 0">拍卖列表</h2>
      <el-button @click="goAdmin">进入管理页</el-button>
    </div>
    <el-table :data="items" style="width: 100%">
      <el-table-column prop="title" label="标题" />
      <el-table-column prop="currentPrice" label="当前价" />
      <el-table-column label="操作">
        <template #default="{ row }">
          <el-button type="primary" @click="goLive(row.id)">进入竞拍</el-button>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<script setup lang="ts">
import { onMounted, ref } from "vue";
import { useRouter } from "vue-router";
import { ElMessage } from "element-plus";
import { getAuctionList } from "@/api/auction";
import type { AuctionItem } from "@/types/auction";

const router = useRouter();
const items = ref<AuctionItem[]>([]);

const loadList = async () => {
  try {
    const res = await getAuctionList();
    items.value = res.data || [];
  } catch (error) {
    ElMessage.error("加载拍卖列表失败，请检查后端服务");
    items.value = [];
  }
};

const goLive = (id: number) => {
  router.push(`/auction/live/${id}`);
};

const goAdmin = () => {
  router.push("/admin/manage");
};

onMounted(loadList);
</script>
