<script setup>
import { Bell, CircleHelp, Search } from 'lucide-vue-next'
</script>

<template>
  <header class="mb-4 flex h-12 items-center gap-2.5">
    <div class="flex h-[46px] w-[420px] items-center gap-2.5 rounded-2xl border border-line bg-white px-3.5 text-sm text-slate-400">
      <Search class="h-[18px] w-[18px]" />
      <input class="flex-1 bg-transparent text-ink outline-none placeholder:text-slate-400" placeholder="Buscar cliente, CPF, CNPJ ou relatório..." />
      <kbd class="rounded-md border border-line bg-slate-50 px-1.5 py-0.5 text-[11px] font-bold text-slate-500">Ctrl K</kbd>
    </div>
    <div class="flex-1" />
    <button class="flex h-[46px] w-[46px] items-center justify-center rounded-2xl border border-line bg-white text-slate-600"><CircleHelp class="h-5 w-5" /></button>
    <button class="relative flex h-[46px] w-[46px] items-center justify-center rounded-2xl border border-line bg-white text-slate-600">
      <Bell class="h-5 w-5" />
      <span class="absolute right-2.5 top-2 h-2.5 w-2.5 rounded-full border-2 border-white bg-red-500" />
    </button>
  </header>
</template>
