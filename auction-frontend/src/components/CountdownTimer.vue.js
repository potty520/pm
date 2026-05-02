import { computed, onMounted, onUnmounted, ref, watch } from "vue";
const props = defineProps();
const emit = defineEmits();
const remaining = ref(0);
let timer = null;
const days = computed(() => String(Math.floor(remaining.value / 86400)).padStart(2, "0"));
const hours = computed(() => String(Math.floor((remaining.value % 86400) / 3600)).padStart(2, "0"));
const minutes = computed(() => String(Math.floor((remaining.value % 3600) / 60)).padStart(2, "0"));
const seconds = computed(() => String(remaining.value % 60).padStart(2, "0"));
const tick = () => {
    if (!props.endTime) {
        remaining.value = 0;
        return;
    }
    const end = new Date(props.endTime).getTime();
    remaining.value = Math.max(0, Math.floor((end - Date.now()) / 1000));
    if (remaining.value === 0) {
        emit("timeout");
    }
};
onMounted(() => {
    tick();
    timer = setInterval(tick, 1000);
});
watch(() => props.endTime, tick);
onUnmounted(() => {
    if (timer)
        clearInterval(timer);
});
debugger; /* PartiallyEnd: #3632/scriptSetup.vue */
const __VLS_ctx = {};
let __VLS_components;
let __VLS_directives;
__VLS_asFunctionalElement(__VLS_intrinsicElements.div, __VLS_intrinsicElements.div)({
    ...{ class: "countdown-timer" },
});
__VLS_asFunctionalElement(__VLS_intrinsicElements.span, __VLS_intrinsicElements.span)({});
(__VLS_ctx.days);
__VLS_asFunctionalElement(__VLS_intrinsicElements.span, __VLS_intrinsicElements.span)({});
(__VLS_ctx.hours);
__VLS_asFunctionalElement(__VLS_intrinsicElements.span, __VLS_intrinsicElements.span)({});
(__VLS_ctx.minutes);
__VLS_asFunctionalElement(__VLS_intrinsicElements.span, __VLS_intrinsicElements.span)({});
(__VLS_ctx.seconds);
/** @type {__VLS_StyleScopedClasses['countdown-timer']} */ ;
var __VLS_dollars;
const __VLS_self = (await import('vue')).defineComponent({
    setup() {
        return {
            days: days,
            hours: hours,
            minutes: minutes,
            seconds: seconds,
        };
    },
    __typeEmits: {},
    __typeProps: {},
});
export default (await import('vue')).defineComponent({
    setup() {
        return {};
    },
    __typeEmits: {},
    __typeProps: {},
});
; /* PartiallyEnd: #4569/main.vue */
