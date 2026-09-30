<script setup>
defineProps({
  titulo: { type: String, required: true },
  subtitulo: { type: String, default: '' },
  trilha: { type: String, default: '' },
})
</script>

<template>
  <div class="mb-4 flex items-end justify-between">
    <div>
      <p v-if="trilha" class="mb-1.5 text-[12.5px] font-semibold text-slate-400">{{ trilha }}</p>
      <h1 class="text-[28px] font-extrabold tracking-tight">{{ titulo }}</h1>
      <p v-if="subtitulo" class="mt-1 text-sm text-slate-500">{{ subtitulo }}</p>
    </div>
    <div class="flex gap-2.5"><slot name="acoes" /></div>
  </div>
</template>
