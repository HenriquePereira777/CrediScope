<script setup>
defineProps({ titulo: String, texto: String })
</script>

<template>
  <div class="flex flex-col items-center gap-2 py-12 text-center">
    <b class="text-base">{{ titulo }}</b>
    <p class="max-w-sm text-sm text-slate-500">{{ texto }}</p>
    <slot />
  </div>
</template>
