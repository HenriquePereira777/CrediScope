<script setup>
import { computed } from 'vue'
import { CORES_AVATAR } from '@/utils/rotulos'
import { iniciais } from '@/utils/formatadores'

const props = defineProps({ nome: { type: String, default: '' }, redondo: Boolean })
const cor = computed(() => CORES_AVATAR[[...props.nome].reduce((s, c) => s + c.charCodeAt(0), 0) % CORES_AVATAR.length])
</script>

<template>
  <div class="flex h-9 w-9 shrink-0 items-center justify-center text-xs font-extrabold" :class="[cor, redondo ? 'rounded-full' : 'rounded-[11px]']">
    {{ iniciais(nome) || '?' }}
  </div>
</template>
