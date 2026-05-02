import { computed, onMounted, onUnmounted, ref } from "vue";
import { useRoute } from "vue-router";
import { ElMessage } from "element-plus";
import CountdownTimer from "@/components/CountdownTimer.vue";
import { getAuctionDetail, getBidRecords, sendBid } from "@/api/auction";
import { useUserStore } from "@/stores/user";
import wsService from "@/utils/websocket";
const route = useRoute();
const userStore = useUserStore();
const item = ref(null);
const records = ref([]);
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
    }
    catch (error) {
        ElMessage.error("出价失败");
    }
    finally {
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
debugger; /* PartiallyEnd: #3632/scriptSetup.vue */
const __VLS_ctx = {};
let __VLS_components;
let __VLS_directives;
__VLS_asFunctionalElement(__VLS_intrinsicElements.div, __VLS_intrinsicElements.div)({
    ...{ style: {} },
});
__VLS_asFunctionalElement(__VLS_intrinsicElements.h2, __VLS_intrinsicElements.h2)({});
(__VLS_ctx.item?.title || "实时竞价");
/** @type {[typeof CountdownTimer, ]} */ ;
// @ts-ignore
const __VLS_0 = __VLS_asFunctionalComponent(CountdownTimer, new CountdownTimer({
    ...{ 'onTimeout': {} },
    endTime: (__VLS_ctx.item?.endTime),
}));
const __VLS_1 = __VLS_0({
    ...{ 'onTimeout': {} },
    endTime: (__VLS_ctx.item?.endTime),
}, ...__VLS_functionalComponentArgsRest(__VLS_0));
let __VLS_3;
let __VLS_4;
let __VLS_5;
const __VLS_6 = {
    onTimeout: (__VLS_ctx.loadDetail)
};
var __VLS_2;
__VLS_asFunctionalElement(__VLS_intrinsicElements.div, __VLS_intrinsicElements.div)({
    ...{ style: {} },
});
(__VLS_ctx.currentPrice);
const __VLS_7 = {}.ElInputNumber;
/** @type {[typeof __VLS_components.ElInputNumber, typeof __VLS_components.elInputNumber, ]} */ ;
// @ts-ignore
const __VLS_8 = __VLS_asFunctionalComponent(__VLS_7, new __VLS_7({
    modelValue: (__VLS_ctx.bidAmount),
    min: (__VLS_ctx.minBidAmount),
    step: (__VLS_ctx.item?.bidIncrement || 1),
}));
const __VLS_9 = __VLS_8({
    modelValue: (__VLS_ctx.bidAmount),
    min: (__VLS_ctx.minBidAmount),
    step: (__VLS_ctx.item?.bidIncrement || 1),
}, ...__VLS_functionalComponentArgsRest(__VLS_8));
const __VLS_11 = {}.ElButton;
/** @type {[typeof __VLS_components.ElButton, typeof __VLS_components.elButton, typeof __VLS_components.ElButton, typeof __VLS_components.elButton, ]} */ ;
// @ts-ignore
const __VLS_12 = __VLS_asFunctionalComponent(__VLS_11, new __VLS_11({
    ...{ 'onClick': {} },
    loading: (__VLS_ctx.bidding),
    type: "primary",
    ...{ style: {} },
}));
const __VLS_13 = __VLS_12({
    ...{ 'onClick': {} },
    loading: (__VLS_ctx.bidding),
    type: "primary",
    ...{ style: {} },
}, ...__VLS_functionalComponentArgsRest(__VLS_12));
let __VLS_15;
let __VLS_16;
let __VLS_17;
const __VLS_18 = {
    onClick: (__VLS_ctx.handleBid)
};
__VLS_14.slots.default;
var __VLS_14;
__VLS_asFunctionalElement(__VLS_intrinsicElements.h3, __VLS_intrinsicElements.h3)({
    ...{ style: {} },
});
const __VLS_19 = {}.ElTable;
/** @type {[typeof __VLS_components.ElTable, typeof __VLS_components.elTable, typeof __VLS_components.ElTable, typeof __VLS_components.elTable, ]} */ ;
// @ts-ignore
const __VLS_20 = __VLS_asFunctionalComponent(__VLS_19, new __VLS_19({
    data: (__VLS_ctx.records),
    ...{ style: {} },
}));
const __VLS_21 = __VLS_20({
    data: (__VLS_ctx.records),
    ...{ style: {} },
}, ...__VLS_functionalComponentArgsRest(__VLS_20));
__VLS_22.slots.default;
const __VLS_23 = {}.ElTableColumn;
/** @type {[typeof __VLS_components.ElTableColumn, typeof __VLS_components.elTableColumn, ]} */ ;
// @ts-ignore
const __VLS_24 = __VLS_asFunctionalComponent(__VLS_23, new __VLS_23({
    prop: "userId",
    label: "用户ID",
}));
const __VLS_25 = __VLS_24({
    prop: "userId",
    label: "用户ID",
}, ...__VLS_functionalComponentArgsRest(__VLS_24));
const __VLS_27 = {}.ElTableColumn;
/** @type {[typeof __VLS_components.ElTableColumn, typeof __VLS_components.elTableColumn, ]} */ ;
// @ts-ignore
const __VLS_28 = __VLS_asFunctionalComponent(__VLS_27, new __VLS_27({
    prop: "bidAmount",
    label: "出价金额",
}));
const __VLS_29 = __VLS_28({
    prop: "bidAmount",
    label: "出价金额",
}, ...__VLS_functionalComponentArgsRest(__VLS_28));
const __VLS_31 = {}.ElTableColumn;
/** @type {[typeof __VLS_components.ElTableColumn, typeof __VLS_components.elTableColumn, ]} */ ;
// @ts-ignore
const __VLS_32 = __VLS_asFunctionalComponent(__VLS_31, new __VLS_31({
    prop: "bidTime",
    label: "出价时间",
}));
const __VLS_33 = __VLS_32({
    prop: "bidTime",
    label: "出价时间",
}, ...__VLS_functionalComponentArgsRest(__VLS_32));
var __VLS_22;
var __VLS_dollars;
const __VLS_self = (await import('vue')).defineComponent({
    setup() {
        return {
            CountdownTimer: CountdownTimer,
            item: item,
            records: records,
            currentPrice: currentPrice,
            bidAmount: bidAmount,
            bidding: bidding,
            minBidAmount: minBidAmount,
            loadDetail: loadDetail,
            handleBid: handleBid,
        };
    },
});
export default (await import('vue')).defineComponent({
    setup() {
        return {};
    },
});
; /* PartiallyEnd: #4569/main.vue */
