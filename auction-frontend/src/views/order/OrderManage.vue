<template>
  <div>
    <h2>订单管理</h2>
    <div style="margin-bottom: 12px">
      <el-button :loading="loading" @click="loadOrders">刷新订单</el-button>
    </div>
    <el-table :data="orders" v-loading="loading">
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="orderNo" label="订单号" />
      <el-table-column prop="itemId" label="拍品ID" width="90" />
      <el-table-column label="状态" width="120">
        <template #default="{ row }">
          {{ statusText(row.status) }}
        </template>
      </el-table-column>
      <el-table-column label="操作" width="320">
        <template #default="{ row }">
          <el-button link :disabled="!canPay(row)" @click="handlePay(row.id)">支付</el-button>
          <el-button link :disabled="!canShip(row)" @click="handleShip(row.id)">发货</el-button>
          <el-button link :disabled="!canComplete(row)" @click="handleComplete(row.id)">完成</el-button>
          <el-button link type="danger" :disabled="!canCancel(row)" @click="handleCancel(row.id)">取消</el-button>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<script setup lang="ts">
import { onMounted, ref } from "vue";
import { ElMessage } from "element-plus";
import { cancelOrder, completeOrder, getMyOrders, payOrder, shipOrder } from "@/api/order";
import { useUserStore } from "@/stores/user";

const orders = ref<any[]>([]);
const loading = ref(false);
const userStore = useUserStore();

const loadOrders = async () => {
  loading.value = true;
  try {
    const res = await getMyOrders();
    orders.value = res.data || [];
  } finally {
    loading.value = false;
  }
};

const handlePay = async (id: number) => {
  await payOrder(id, "alipay");
  ElMessage.success("支付成功");
  await loadOrders();
};
const handleShip = async (id: number) => {
  await shipOrder(id);
  ElMessage.success("发货成功");
  await loadOrders();
};
const handleComplete = async (id: number) => {
  await completeOrder(id);
  ElMessage.success("订单完成");
  await loadOrders();
};
const handleCancel = async (id: number) => {
  await cancelOrder(id);
  ElMessage.success("订单取消");
  await loadOrders();
};

const statusText = (status: number) => {
  switch (status) {
    case 0:
      return "待支付";
    case 1:
      return "已支付";
    case 2:
      return "已发货";
    case 3:
      return "已完成";
    case 4:
      return "已取消";
    default:
      return String(status);
  }
};

const canPay = (row: any) => row.status === 0 && row.buyerId === userStore.userId;
const canShip = (row: any) => row.status === 1 && row.sellerId === userStore.userId;
const canComplete = (row: any) => row.status === 2 && row.buyerId === userStore.userId;
const canCancel = (row: any) => row.status === 0 && (row.buyerId === userStore.userId || row.sellerId === userStore.userId);

onMounted(loadOrders);
</script>
