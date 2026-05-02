<template>
  <div>
    <h2>拍品管理</h2>
    <el-card style="margin-bottom: 12px">
      <template #header>发布拍品</template>
      <div style="display: flex; gap: 8px; flex-wrap: wrap">
        <el-input v-model="publishForm.title" placeholder="标题" style="max-width: 220px" />
        <el-input v-model="publishForm.description" placeholder="描述" style="max-width: 240px" />
        <el-select v-model="publishForm.categoryId" placeholder="分类" style="width: 140px">
          <el-option v-for="c in categories" :key="c.id" :value="c.id" :label="c.name" />
        </el-select>
        <el-input-number v-model="publishForm.startPrice" :min="1" />
        <el-input-number v-model="publishForm.bidIncrement" :min="1" />
        <el-button type="primary" @click="handlePublish">发布</el-button>
      </div>
    </el-card>

    <el-card>
      <template #header>拍品搜索与审核</template>
      <div style="display: flex; gap: 8px; margin-bottom: 12px">
        <el-input v-model="search.keyword" placeholder="关键词" style="max-width: 220px" />
        <el-select v-model="search.status" clearable placeholder="状态" style="width: 140px">
          <el-option :value="0" label="待审核" />
          <el-option :value="1" label="审核通过" />
          <el-option :value="2" label="审核拒绝" />
          <el-option :value="3" label="拍卖中" />
        </el-select>
        <el-button @click="loadItems">搜索</el-button>
      </div>
      <el-table :data="items" v-loading="loading">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="title" label="标题" />
        <el-table-column label="状态" width="120">
          <template #default="{ row }">
            {{ statusText(row.status) }}
          </template>
        </el-table-column>
        <el-table-column label="审核" width="220">
          <template #default="{ row }">
            <el-button :disabled="row.status !== 0" type="success" link @click="handleReview(row.id, true)">通过</el-button>
            <el-button :disabled="row.status !== 0" type="danger" link @click="handleReview(row.id, false)">拒绝</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { onMounted, ref } from "vue";
import { ElMessage, ElMessageBox } from "element-plus";
import { getCategoryList, publishAuction, reviewAuction, searchAuctions } from "@/api/auction";

const categories = ref<any[]>([]);
const items = ref<any[]>([]);
const loading = ref(false);
const publishForm = ref({ title: "", description: "", categoryId: undefined as number | undefined, startPrice: 1000, bidIncrement: 100 });
const search = ref({ keyword: "", status: undefined as number | undefined });

const loadCategories = async () => {
  const res = await getCategoryList();
  categories.value = res.data || [];
};
const loadItems = async () => {
  loading.value = true;
  try {
    const res = await searchAuctions({ keyword: search.value.keyword, status: search.value.status });
    items.value = res.data || [];
  } finally {
    loading.value = false;
  }
};
const handlePublish = async () => {
  if (!publishForm.value.title) return ElMessage.warning("请输入标题");
  await publishAuction({ ...publishForm.value });
  ElMessage.success("发布成功");
  publishForm.value.title = "";
  publishForm.value.description = "";
  await loadItems();
};
const handleReview = async (itemId: number, approved: boolean) => {
  if (approved) {
    await reviewAuction(itemId, { approved: true });
  } else {
    const { value } = await ElMessageBox.prompt("请输入驳回原因", "审核驳回");
    await reviewAuction(itemId, { approved: false, rejectReason: value });
  }
  ElMessage.success("审核完成");
  await loadItems();
};

const statusText = (status: number) => {
  switch (status) {
    case 0:
      return "待审核";
    case 1:
      return "审核通过";
    case 2:
      return "审核拒绝";
    case 3:
      return "拍卖中";
    case 4:
      return "已成交";
    case 5:
      return "流拍";
    default:
      return String(status);
  }
};

onMounted(async () => {
  await Promise.all([loadCategories(), loadItems()]);
});
</script>
