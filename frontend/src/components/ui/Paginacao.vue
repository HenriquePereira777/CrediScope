<script setup>
defineProps({ pagina: Object })
defineEmits(['ir'])
</script>

<template>
  <div v-if="pagina && pagina.total > 0" class="flex items-center justify-between px-4 pb-1 pt-3.5 text-sm text-slate-500">
    <span>Mostrando <b class="text-ink">{{ pagina.pagina * pagina.tamanho + 1 }}–{{ pagina.pagina * pagina.tamanho + pagina.itens.length }}</b> de {{ pagina.total }}</span>
    <div class="flex gap-1.5">
      <button class="h-8 rounded-lg border border-line bg-white px-3 font-bold text-slate-700 disabled:opacity-40" :disabled="pagina.pagina === 0" @click="$emit('ir', pagina.pagina - 1)">‹ Anterior</button>
      <button class="h-8 rounded-lg border border-line bg-white px-3 font-bold text-slate-700 disabled:opacity-40" :disabled="pagina.pagina + 1 >= pagina.totalPaginas" @click="$emit('ir', pagina.pagina + 1)">Próxima ›</button>
    </div>
  </div>
</template>
