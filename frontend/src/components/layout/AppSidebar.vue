<script setup>
import { ChevronsUpDown, LogOut } from 'lucide-vue-next'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import AppLogo from './AppLogo.vue'
import { menu } from './menu'
import { PERFIL } from '@/utils/rotulos'
import { computed, onMounted, ref } from 'vue'
import { EMPRESA } from '@/config'
import painelService from '@/services/painelService'

const consultasMes = ref(0)
onMounted(async () => {
  try {
    consultasMes.value = (await painelService.resumo()).consultasNoMes
  } catch {
    /* sem conexão: mantém 0 */
  }
})
const usoPct = computed(() => Math.min(100, Math.round((consultasMes.value / EMPRESA.limiteConsultasMes) * 100)))

const auth = useAuthStore()
const router = useRouter()

async function sair() {
  await auth.sair()
  router.push({ name: 'login', query: { motivo: 'saiu' } })
}
</script>

<template>
  <aside class="sticky top-4 m-4 mr-0 flex h-[calc(100vh-2rem)] w-[268px] shrink-0 flex-col rounded-3xl border border-line bg-white px-3.5 pb-3.5 pt-5">
    <AppLogo class="px-2" />

    <!-- Empresa / plano -->
    <div class="my-5 flex items-center gap-2.5 rounded-2xl border border-line bg-slate-50/60 p-2.5">
      <div class="flex h-9 w-9 items-center justify-center rounded-[10px] bg-ink text-xs font-extrabold text-white">{{ EMPRESA.sigla }}</div>
      <div class="leading-tight">
        <b class="block text-[13.5px]">{{ EMPRESA.nome }}</b>
        <small class="text-xs text-slate-500">{{ EMPRESA.plano }}</small>
      </div>
      <ChevronsUpDown class="ml-auto h-4 w-4 text-slate-400" />
    </div>

    <!-- Menu -->
    <nav class="flex-1 overflow-y-auto">
      <template v-for="bloco in menu" :key="bloco.grupo">
        <p class="mx-3 mb-2 mt-3.5 text-[11px] font-bold tracking-[1.2px] text-slate-400">{{ bloco.grupo }}</p>
        <RouterLink
          v-for="item in bloco.itens"
          :key="item.rota"
          :to="{ name: item.rota }"
          class="relative mb-0.5 flex h-[42px] whitespace-nowrap items-center gap-3 rounded-xl px-3 text-sm font-semibold text-slate-600 hover:bg-slate-50"
          active-class="!bg-primary-100 !text-primary-600 before:absolute before:-left-3.5 before:top-2 before:bottom-2 before:w-1 before:rounded-r before:bg-primary-500"
        >
          <component :is="item.icone" class="h-5 w-5 shrink-0 opacity-70" />
          {{ item.rotulo }}
          <span
            v-if="item.selo"
            class="ml-auto rounded-full px-2 py-0.5 text-[11px] font-bold"
            :class="item.selo.tipo === 'novo' ? 'bg-emerald-100 text-emerald-700' : 'bg-red-100 text-red-600'"
          >{{ item.selo.texto }}</span>
        </RouterLink>
      </template>
    </nav>

    <!-- Uso do plano -->
    <div class="mt-3 rounded-[18px] bg-gradient-to-br from-primary-500 to-accent-500 p-4 text-white">
      <small class="text-xs opacity-85">Consultas do mês</small>
      <b class="mb-2.5 mt-0.5 block text-[15px]">{{ consultasMes }} de {{ EMPRESA.limiteConsultasMes }} usadas</b>
      <div class="h-2 rounded-full bg-white/25"><div class="h-full rounded-full bg-white" :style="{ width: `${usoPct}%` }" /></div>
    </div>

    <!-- Usuário -->
    <div class="mt-3 flex items-center gap-2.5 border-t border-line px-2 pt-3">
      <div class="flex h-[38px] w-[38px] items-center justify-center rounded-full bg-gradient-to-br from-amber-200 to-pink-300 text-[13px] font-extrabold text-amber-900">{{ auth.iniciais }}</div>
      <div class="leading-tight">
        <b class="block max-w-[140px] truncate text-[13.5px]">{{ auth.usuario?.nome || 'Usuário' }}</b>
        <small class="text-xs text-slate-500">{{ PERFIL[auth.usuario?.perfil] || '' }}</small>
      </div>
      <button class="ml-auto text-slate-400 hover:text-slate-600" title="Sair" @click="sair"><LogOut class="h-5 w-5" /></button>
    </div>
  </aside>
</template>
