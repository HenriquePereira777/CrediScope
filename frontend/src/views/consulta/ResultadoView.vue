<script setup>
import { computed, onMounted, ref } from 'vue'
import { Calendar, Check, Clock, CreditCard, FileText, MapPin, Scale, TriangleAlert, User, X } from 'lucide-vue-next'
import ScoreGauge from '@/components/ui/ScoreGauge.vue'
import StatusTag from '@/components/ui/StatusTag.vue'
import consultaService from '@/services/consultaService'
import { mensagemDeErro } from '@/services/http'
import { DECISAO, PENDENCIA, RECOMENDACAO, RISCO, TIPO_CONSULTA } from '@/utils/rotulos'
import { formatarDataHora, formatarDocumento, formatarMoeda, iniciais } from '@/utils/formatadores'

const props = defineProps({ id: { type: String, required: true } })

const consulta = ref(null)
const erro = ref('')
const salvando = ref(false)

onMounted(carregar)

async function carregar() {
  try {
    consulta.value = await consultaService.buscar(props.id)
  } catch (e) {
    erro.value = mensagemDeErro(e)
  }
}

async function decidir(decisao) {
  salvando.value = true
  erro.value = ''
  try {
    consulta.value = await consultaService.decidir(props.id, decisao)
  } catch (e) {
    erro.value = mensagemDeErro(e)
  } finally {
    salvando.value = false
  }
}

// Lista fixa de verificações: mostra "Nada consta" quando não há pendência daquele tipo
const ICONES = { RESTRICAO: CreditCard, PROTESTO: FileText, CHEQUE: X, ACAO_JUDICIAL: Scale, FALENCIA: TriangleAlert }
const pendencias = computed(() =>
  Object.keys(PENDENCIA).map((tipo) => {
    const achadas = (consulta.value?.pendencias || []).filter((p) => p.tipo === tipo)
    const qtd = achadas.reduce((s, p) => s + p.quantidade, 0)
    const valor = achadas.reduce((s, p) => s + Number(p.valorTotal || 0), 0)
    return { tipo, rotulo: PENDENCIA[tipo], icone: ICONES[tipo], qtd, valor }
  }),
)
const limpas = computed(() => pendencias.value.filter((p) => p.qtd === 0).length)

const recomendacaoCor = computed(() => ({
  APROVAR: 'from-emerald-500/25 to-teal-500/15 border-emerald-400/40 text-emerald-100',
  ANALISE_MANUAL: 'from-amber-500/25 to-amber-500/10 border-amber-400/40 text-amber-100',
  RECUSAR: 'from-red-500/25 to-red-500/10 border-red-400/40 text-red-100',
}[consulta.value?.recomendacao]))
</script>

<template>
  <p class="mb-3 text-[12.5px] font-semibold text-slate-400">
    Consultas › <RouterLink :to="{ name: 'historico' }" class="hover:text-slate-600">Histórico</RouterLink> › <span class="text-ink">Resultado</span>
  </p>

  <p v-if="erro" class="card mb-4 text-sm font-semibold text-red-700">{{ erro }}</p>

  <template v-if="consulta">
    <!-- Cabeçalho -->
    <div class="card flex items-center justify-between px-[22px]">
      <div class="flex items-center gap-4">
        <div class="flex h-[60px] w-[60px] items-center justify-center rounded-[18px] bg-gradient-to-br from-primary-500 to-accent-500 text-xl font-extrabold text-white">{{ iniciais(consulta.nome) }}</div>
        <div>
          <h1 class="flex items-center gap-2.5 text-[22px] font-extrabold tracking-tight">
            {{ consulta.nome }}
            <StatusTag v-bind="DECISAO[consulta.decisao || 'PENDENTE']" />
          </h1>
          <div class="mt-1.5 flex gap-4 text-[13px] text-slate-500">
            <span class="flex items-center gap-1.5"><FileText class="h-[15px] w-[15px] text-slate-400" />{{ consulta.tipoPessoa === 'PF' ? 'CPF' : 'CNPJ' }} {{ formatarDocumento(consulta.documento) }}</span>
            <span class="flex items-center gap-1.5"><MapPin class="h-[15px] w-[15px] text-slate-400" />{{ consulta.cidade }}, {{ consulta.uf }}</span>
            <span class="flex items-center gap-1.5"><Clock class="h-[15px] w-[15px] text-slate-400" />{{ formatarDataHora(consulta.criadoEm) }}</span>
            <span class="flex items-center gap-1.5"><User class="h-[15px] w-[15px] text-slate-400" />{{ consulta.consultadoPor }}</span>
          </div>
        </div>
      </div>
      <div class="flex gap-2.5">
        <button :disabled="salvando" class="btn border border-red-200 bg-white text-red-600 disabled:opacity-50" @click="decidir('RECUSADO')"><X class="h-[18px] w-[18px]" />Recusar</button>
        <button :disabled="salvando" class="btn bg-emerald-500 text-white shadow-lg shadow-emerald-500/30 disabled:opacity-50" @click="decidir('APROVADO')"><Check class="h-[18px] w-[18px]" />Aprovar crédito</button>
      </div>
    </div>

    <div class="mt-4 grid grid-cols-[330px_1fr_1fr] gap-4">
      <!-- Score -->
      <div class="relative overflow-hidden rounded-[20px] bg-ink p-[22px] text-white">
        <div class="pointer-events-none absolute inset-0" style="background: radial-gradient(300px 250px at 10% 0%, rgba(109,93,252,.5), transparent 60%)" />
        <div class="relative">
          <p class="text-xs font-bold tracking-[1.2px] text-slate-400">SCORE SERASA</p>
          <div class="relative mx-auto mt-3.5 h-[140px] w-[250px]">
            <ScoreGauge :score="consulta.score" />
            <div class="absolute inset-x-0 top-[60px] text-center text-[52px] font-extrabold tracking-[-2px]">
              {{ consulta.score }}
              <small class="-mt-1 block text-[13px] font-semibold tracking-normal text-slate-400">de 1000 · risco {{ RISCO[consulta.faixaRisco].rotulo.toLowerCase() }}</small>
            </div>
          </div>
          <div class="mt-6 grid grid-cols-2 gap-2.5">
            <div class="rounded-xl border border-white/10 bg-white/5 px-3 py-2.5 text-[11.5px] font-semibold text-slate-400">Limite sugerido<b class="mt-0.5 block text-[15px] text-white">{{ formatarMoeda(consulta.limiteSugerido) }}</b></div>
            <div class="rounded-xl border border-white/10 bg-white/5 px-3 py-2.5 text-[11.5px] font-semibold text-slate-400">Relatório<b class="mt-0.5 block text-[15px] text-white">{{ TIPO_CONSULTA[consulta.tipo] }}</b></div>
          </div>
          <div class="mt-3.5 rounded-2xl border bg-gradient-to-br p-3.5 text-[13px]" :class="recomendacaoCor">
            <b class="block text-[14.5px] text-white">Recomendação: {{ RECOMENDACAO[consulta.recomendacao].rotulo.toLowerCase() }}</b>
            Conforme a política de crédito da empresa.
          </div>
        </div>
      </div>

      <!-- Pendências -->
      <div class="card">
        <div class="mb-3.5 flex items-center justify-between">
          <b class="text-base font-extrabold">Pendências financeiras</b>
          <StatusTag :rotulo="`${limpas} de ${pendencias.length} limpas`" :classe="limpas === pendencias.length ? 'bg-emerald-50 text-emerald-700' : 'bg-amber-50 text-amber-700'" />
        </div>
        <div v-for="p in pendencias" :key="p.tipo" class="flex items-center justify-between border-b border-slate-100 py-3 last:border-0">
          <div class="flex items-center gap-3">
            <div class="flex h-9 w-9 items-center justify-center rounded-xl border border-line bg-slate-50 text-slate-500"><component :is="p.icone" class="h-[17px] w-[17px]" /></div>
            <b class="text-[13.5px]">{{ p.rotulo }}</b>
          </div>
          <StatusTag v-if="p.qtd === 0" rotulo="Nada consta" classe="bg-emerald-50 text-emerald-700" />
          <StatusTag v-else :rotulo="`${p.qtd} · ${formatarMoeda(p.valor)}`" :classe="p.tipo === 'ACAO_JUDICIAL' ? 'bg-amber-50 text-amber-700' : 'bg-red-50 text-red-700'" />
        </div>
      </div>

      <!-- Dados -->
      <div class="card">
        <b class="mb-3.5 block text-base font-extrabold">Dados da consulta</b>
        <div class="grid grid-cols-2 gap-x-4 gap-y-3.5 text-[13px]">
          <div><small class="mb-0.5 block text-xs font-semibold text-slate-400">Nome</small><b>{{ consulta.nome }}</b></div>
          <div><small class="mb-0.5 block text-xs font-semibold text-slate-400">Tipo de pessoa</small><b>{{ consulta.tipoPessoa === 'PF' ? 'Pessoa física' : 'Pessoa jurídica' }}</b></div>
          <div><small class="mb-0.5 block text-xs font-semibold text-slate-400">Cidade</small><b>{{ consulta.cidade }} / {{ consulta.uf }}</b></div>
          <div><small class="mb-0.5 block text-xs font-semibold text-slate-400">Finalidade</small><b>{{ consulta.finalidade }}</b></div>
          <div><small class="mb-0.5 block text-xs font-semibold text-slate-400">Custo da consulta</small><b>{{ formatarMoeda(consulta.custo) }}</b></div>
          <div><small class="mb-0.5 block text-xs font-semibold text-slate-400">Protocolo</small><b>#{{ String(consulta.id).padStart(6, '0') }}</b></div>
        </div>
        <div class="mt-5 rounded-xl bg-slate-50 p-3.5 text-xs leading-relaxed text-slate-500">
          <Calendar class="mr-1 inline h-3.5 w-3.5" />
          Consulta registrada no histórico com usuário, data, finalidade e IP (LGPD).
          <span v-if="consulta.tipoPessoa === 'PJ'"> Os dados de sócios e faturamento entram com a integração real da Serasa.</span>
        </div>
      </div>
    </div>
  </template>
</template>
