<script setup>
import { computed } from 'vue'

/** Medidor em arco do score (0 a 1000), como na tela Resultado da análise. */
const props = defineProps({ score: { type: Number, default: 0 } })

const RAIO = 105
const comprimento = Math.PI * RAIO
const progresso = computed(() => (comprimento * Math.min(1000, Math.max(0, props.score))) / 1000)
const ponto = computed(() => {
  const ang = (Math.PI * props.score) / 1000
  return { x: 125 - RAIO * Math.cos(ang), y: 125 - RAIO * Math.sin(ang) }
})
</script>

<template>
  <svg width="250" height="140" viewBox="0 0 250 140">
    <defs>
      <linearGradient id="gauge-grad" x1="0" x2="1">
        <stop offset="0" stop-color="#F04438" />
        <stop offset=".45" stop-color="#F79009" />
        <stop offset="1" stop-color="#12B76A" />
      </linearGradient>
    </defs>
    <path d="M20 125 A105 105 0 0 1 230 125" fill="none" stroke="rgba(255,255,255,.1)" stroke-width="16" stroke-linecap="round" />
    <path d="M20 125 A105 105 0 0 1 230 125" fill="none" stroke="url(#gauge-grad)" stroke-width="16" stroke-linecap="round" :stroke-dasharray="`${progresso} 999`" />
    <circle :cx="ponto.x" :cy="ponto.y" r="11" fill="#fff" stroke="#12B76A" stroke-width="4" />
  </svg>
</template>
