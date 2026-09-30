<script setup>
import { computed } from 'vue'
import { RISCO } from '@/utils/rotulos'

const props = defineProps({ score: Number, faixa: String })
const cor = computed(() => RISCO[props.faixa]?.cor || '#98A2B3')
</script>

<template>
  <div class="flex items-center gap-2.5">
    <span class="w-8 text-sm font-extrabold">{{ score ?? '—' }}</span>
    <span class="relative h-1.5 w-16 overflow-hidden rounded-full bg-slate-100">
      <span class="absolute inset-y-0 left-0 rounded-full" :style="{ width: `${(score || 0) / 10}%`, background: cor }" />
    </span>
  </div>
</template>
