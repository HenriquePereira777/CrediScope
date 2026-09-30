<script setup>
defineProps({
  rotulo: { type: String, required: true },
  classe: { type: String, default: 'bg-slate-100 text-slate-600' },
  ponto: { type: Boolean, default: true },
})
</script>

<template>
  <span class="inline-flex items-center gap-1.5 whitespace-nowrap rounded-full px-2.5 py-1 text-xs font-bold" :class="classe">
    <span v-if="ponto" class="h-1.5 w-1.5 rounded-full bg-current" />
    {{ rotulo }}
  </span>
</template>
