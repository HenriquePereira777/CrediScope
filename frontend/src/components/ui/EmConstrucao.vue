<script setup>
import { Hammer } from 'lucide-vue-next'

defineProps({ tela: { type: String, required: true } })
</script>

<template>
  <!-- Placeholder: remover quando a tela for desenvolvida -->
  <div class="card flex flex-col items-center justify-center gap-3 py-16 text-center">
    <div class="flex h-14 w-14 items-center justify-center rounded-2xl bg-primary-100 text-primary-500"><Hammer class="h-7 w-7" /></div>
    <b class="text-lg">Tela em desenvolvimento</b>
    <p class="max-w-md text-sm text-slate-500">
      Referência visual: <code class="rounded bg-slate-100 px-1.5 py-0.5">docs/telas/{{ tela }}</code>
    </p>
  </div>
</template>
