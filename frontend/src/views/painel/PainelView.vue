<script setup>
import { computed, onMounted, ref } from 'vue'
import { AlertTriangle, Check, Plus, Search, Users } from 'lucide-vue-next'
import PageHeader from '@/components/ui/PageHeader.vue'
import StatusTag from '@/components/ui/StatusTag.vue'
import ScoreBar from '@/components/ui/ScoreBar.vue'
import Avatar from '@/components/ui/Avatar.vue'
import EstadoVazio from '@/components/ui/EstadoVazio.vue'
import painelService from '@/services/painelService'
import { mensagemDeErro } from '@/services/http'
import { useAuthStore } from '@/stores/auth'
import { DECISAO, RISCO } from '@/utils/rotulos'
import { formatarDiaMes, formatarDocumento } from '@/utils/formatadores'

const auth = useAuthStore()
const resumo = ref(null)
const erro = ref('')

onMounted(async () => {
  try {
    resumo.value = await painelService.resumo()
  } catch (e) {
    erro.value = mensagemDeErro(e)
  }
})

const saudacao = computed(() => {
  const h = new Date().getHours()
  return h < 12 ? 'Bom dia' : h < 18 ? 'Boa tarde' : 'Boa noite'
})
const primeiroNome = computed(() => (auth.usuario?.nome || '').split(' ')[0])

const kpis = computed(() => {
  const r = resumo.value
  if (!r) return []
  return [
    { rotulo: 'Consultas no mês', valor: r.consultasNoMes, icone: Search, cor: 'bg-indigo-50 text-primary-500' },
    { rotulo: 'Taxa de aprovação', valor: `${r.taxaAprovacao}%`, icone: Check, cor: 'bg-emerald-50 text-emerald-500' },
    { rotulo: 'Clientes monitorados', valor: r.clientesMonitorados, icone: Users, cor: 'bg-sky-50 text-sky-500' },
    { rotulo: 'Alto risco na carteira', valor: r.riscoCarteira.alto, icone: AlertTriangle, cor: 'bg-red-50 text-red-500' },
  ]
})

const maxDia = computed(() => Math.max(1, ...(resumo.value?.consultasPorDia || []).map((d) => d.total)))

// Donut do risco da carteira
const donut = computed(() => {
  const r = resumo.value?.riscoCarteira
  if (!r) return { total: 0, fatias: [] }
  const total = r.baixo + r.medio + r.alto
  let acumulado = 0
  const fatias = [
    { chave: 'BAIXO', valor: r.baixo },
    { chave: 'MEDIO', valor: r.medio },
    { chave: 'ALTO', valor: r.alto },
  ].map((f) => {
    const pct = total ? (f.valor / total) * 100 : 0
    const fatia = { ...f, pct, inicio: acumulado }
    acumulado += pct
    return fatia
  })
  return { total, fatias }
})
</script>

<template>
  <PageHeader trilha="Início" :titulo="`${saudacao}, ${primeiroNome} 👋`" subtitulo="Aqui está o resumo da análise de crédito da sua empresa.">
    <template #acoes>
      <RouterLink :to="{ name: 'nova-consulta' }" class="btn-primary"><Plus class="h-[18px] w-[18px]" />Nova consulta</RouterLink>
    </template>
  </PageHeader>

  <p v-if="erro" class="card mb-4 text-sm font-semibold text-red-700">{{ erro }}</p>

  <template v-if="resumo">
    <!-- Indicadores -->
    <div class="mb-4 grid grid-cols-4 gap-4">
      <div v-for="k in kpis" :key="k.rotulo" class="card">
        <div class="flex h-10 w-10 items-center justify-center rounded-xl" :class="k.cor"><component :is="k.icone" class="h-5 w-5" /></div>
        <p class="mt-2.5 text-[13px] font-semibold text-slate-500">{{ k.rotulo }}</p>
        <p class="text-[28px] font-extrabold tracking-tight">{{ k.valor }}</p>
      </div>
    </div>

    <div class="mb-4 grid grid-cols-[1.65fr_1fr] gap-4">
      <!-- Gráfico de barras -->
      <div class="card">
        <div class="mb-4 flex items-center justify-between">
          <b class="text-base font-extrabold">Consultas por dia</b>
          <div class="flex gap-4 text-xs font-semibold text-slate-500">
            <span class="flex items-center gap-1.5"><i class="h-2.5 w-2.5 rounded-sm bg-indigo-100" />Consultas</span>
            <span class="flex items-center gap-1.5"><i class="h-2.5 w-2.5 rounded-sm bg-primary-500" />Aprovação recomendada</span>
          </div>
        </div>
        <div class="flex h-40 items-end gap-3">
          <div v-for="d in resumo.consultasPorDia" :key="d.dia" class="group relative flex h-full flex-1 flex-col justify-end" :title="`${d.total} consultas · ${d.aprovadas} aprovadas`">
            <div class="relative w-full rounded-t-lg bg-indigo-100" :style="{ height: `${(d.total / maxDia) * 100}%`, minHeight: d.total ? '4px' : '0' }">
              <div class="absolute inset-x-0 bottom-0 rounded-t-lg bg-gradient-to-b from-indigo-400 to-primary-500" :style="{ height: d.total ? `${(d.aprovadas / d.total) * 100}%` : 0 }" />
            </div>
          </div>
        </div>
        <div class="mt-2 flex gap-3">
          <span v-for="d in resumo.consultasPorDia" :key="d.dia" class="flex-1 text-center text-[11px] font-semibold text-slate-400">{{ formatarDiaMes(d.dia) }}</span>
        </div>
      </div>

      <!-- Risco da carteira -->
      <div class="card">
        <div class="mb-3.5 flex items-center justify-between">
          <b class="text-base font-extrabold">Risco da carteira</b>
          <RouterLink :to="{ name: 'carteira' }" class="text-[12.5px] font-bold text-primary-500">Ver carteira →</RouterLink>
        </div>
        <div class="flex items-center gap-5">
          <svg width="140" height="140" viewBox="0 0 42 42" class="shrink-0">
            <circle cx="21" cy="21" r="15.9" fill="none" stroke="#F2F4F7" stroke-width="5" />
            <circle v-for="f in donut.fatias" :key="f.chave" cx="21" cy="21" r="15.9" fill="none" :stroke="RISCO[f.chave].cor" stroke-width="5"
                    :stroke-dasharray="`${f.pct} ${100 - f.pct}`" :stroke-dashoffset="25 - f.inicio" />
            <text x="21" y="21.5" text-anchor="middle" font-size="7" font-weight="800" fill="#0B1020">{{ donut.total }}</text>
            <text x="21" y="27" text-anchor="middle" font-size="2.8" font-weight="600" fill="#98A2B3">clientes</text>
          </svg>
          <div class="flex flex-1 flex-col gap-3 text-[13px]">
            <div v-for="f in donut.fatias" :key="f.chave" class="flex items-center justify-between">
              <span class="flex items-center gap-2 font-semibold text-slate-600"><i class="h-2.5 w-2.5 rounded-sm" :style="{ background: RISCO[f.chave].cor }" />{{ RISCO[f.chave].rotulo }} risco</span>
              <b>{{ f.valor }} <em class="ml-1 text-xs font-semibold not-italic text-slate-400">{{ Math.round(f.pct) }}%</em></b>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Últimas consultas -->
    <div class="card pb-2">
      <div class="mb-3 flex items-center justify-between">
        <b class="text-base font-extrabold">Últimas consultas</b>
        <RouterLink :to="{ name: 'historico' }" class="text-[12.5px] font-bold text-primary-500">Ver histórico →</RouterLink>
      </div>
      <EstadoVazio v-if="!resumo.ultimasConsultas.length" titulo="Nenhuma consulta ainda" texto="Faça a primeira consulta para ver os dados aparecerem aqui.">
        <RouterLink :to="{ name: 'nova-consulta' }" class="btn-primary mt-2">Fazer primeira consulta</RouterLink>
      </EstadoVazio>
      <div v-else class="overflow-x-auto">
      <table class="tabela">
        <thead><tr><th>Cliente</th><th>Score</th><th>Risco</th><th>Decisão</th><th>Consultado por</th></tr></thead>
        <tbody>
          <tr v-for="c in resumo.ultimasConsultas" :key="c.id" class="cursor-pointer hover:bg-slate-50" @click="$router.push({ name: 'resultado', params: { id: c.id } })">
            <td><div class="flex items-center gap-3"><Avatar :nome="c.nome" /><div><b class="block">{{ c.nome }}</b><small class="text-xs text-slate-400">{{ formatarDocumento(c.documento) }}</small></div></div></td>
            <td><ScoreBar :score="c.score" :faixa="c.faixaRisco" /></td>
            <td><StatusTag :rotulo="RISCO[c.faixaRisco].rotulo" :classe="RISCO[c.faixaRisco].classe" /></td>
            <td><StatusTag v-bind="DECISAO[c.decisao || 'PENDENTE']" /></td>
            <td class="text-slate-500">{{ c.consultadoPor }}</td>
          </tr>
        </tbody>
      </table>
    </div>
    </div>
  </template>
</template>
