<template>
  <div style="padding: 24px">
    <h2>{{ item?.title || "实时竞价" }}</h2>
    <CountdownTimer :end-time="item?.endTime" @timeout="loadDetail" />
    <div style="margin: 12px 0">当前价: {{ currentPrice }}</div>
    <el-input-number v-model="bidAmount" :min="minBidAmount" :step="item?.bidIncrement || 1" />
    <el-button :loading="bidding" type="primary" style="margin-left: 8px" @click="handleBid">
      立即出价
    </el-button>

    <h3 style="margin-top: 24px">出价记录</h3>
    <el-table :data="records" style="width: 100%">
      <el-table-column prop="userId" label="用户ID" />
      <el-table-column prop="bidAmount" label="出价金额" />
      <el-table-column prop="bidTime" label="出价时间" />
    </el-table>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, onUnmounted, ref } from "vue";
import { useRoute } from "vue-router";
import { ElMessage } from "element-plus";
import CountdownTimer from "@/components/CountdownTimer.vue";
import { getAuctionDetail, getBidRecords, sendBid } from "@/api/auction";
import type { AuctionItem, BidRecord } from "@/types/auction";
import { useUserStore } from "@/stores/user";
import wsService from "@/utils/websocket";

const route = useRoute();
const userStore = useUserStore();
const item = ref<AuctionItem | null>(null);
const records = ref<BidRecord[]>([]);
const currentPrice = ref(0);
const bidAmount = ref(0);
const bidding = ref(false);

const itemId = computed(() => Number(route.params.id));
const minBidAmount = computed(() => currentPrice.value + (item.value?.bidIncrement || 1));

const loadDetail = async () => {
  const res = await getAuctionDetail(itemId.value);
  item.value = res.data;
  currentPrice.value = res.data.currentPrice || res.data.startPrice;
  bidAmount.value = minBidAmount.value;
};

const loadRecords = async () => {
  const res = await getBidRecords(itemId.value);
  records.value = res.data || [];
};

const handleBid = async () => {
  try {
    bidding.value = true;
    await sendBid(itemId.value, userStore.userId, bidAmount.value);
    ElMessage.success("出价成功");
    await Promise.all([loadDetail(), loadRecords()]);
  } catch (error) {
    ElMessage.error("出价失败");
  } finally {
    bidding.value = false;
  }
};

onMounted(async () => {
  await Promise.all([loadDetail(), loadRecords()]);
  wsService.connect(userStore.token);
  wsService.subscribeBidUpdate(itemId.value, async () => {
    await Promise.all([loadDetail(), loadRecords()]);
  });
});

onUnmounted(() => {
  wsService.disconnect();
});
</script>
