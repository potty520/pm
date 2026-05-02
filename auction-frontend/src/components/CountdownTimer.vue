<template>
  <div class="countdown-timer">
    <span>{{ days }}</span>天
    <span>{{ hours }}</span>:<span>{{ minutes }}</span>:<span>{{ seconds }}</span>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, onUnmounted, ref, watch } from "vue";

const props = defineProps<{ endTime?: string }>();
const emit = defineEmits<{ timeout: [] }>();

const remaining = ref(0);
let timer: ReturnType<typeof setInterval> | null = null;

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
  if (timer) clearInterval(timer);
});
</script>
