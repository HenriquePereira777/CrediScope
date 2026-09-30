<script setup>
import { computed, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ArrowRight, Building2, Check, FileText, Layers, Search, User, Zap } from 'lucide-vue-next'
import PageHeader from '@/components/ui/PageHeader.vue'
import consultaService from '@/services/consultaService'
import { mensagemDeErro } from '@/services/http'
import { mascararDocumento, somenteNumeros } from '@/utils/formatadores'

const router = useRouter()

const tipoPessoa = ref('PJ')
const documento = ref('')
const tipo = ref('COMPLETO')
const finalidade = ref('Venda a prazo')
const monitorar = ref(true)
const carregando = ref(false)
const erro = ref('')

const FINALIDADES = ['Venda a prazo', 'Crediário', 'Renovação de limite', 'Cadastro de cliente']
const RELATORIOS = [
  { valor: 'COMPLETO', titulo: 'Relatório completo', texto: 'Score, restrições, protestos, ações judiciais e limite sugerido.', preco: 'R$ 8,90', icone: FileText, cor: 'bg-indigo-50 text-primary-500' },
  { valor: 'SCORE', titulo: 'Score rápido', texto: 'Só o score e a classificação de risco. Ideal para o balcão.', preco: 'R$ 2,50', icone: Zap, cor: 'bg-amber-50 text-amber-500' },
  { valor: 'CADASTRAL', titulo: 'Cadastral', texto: 'Razão social, endereço e situação cadastral.', preco: 'R$ 1,80', icone: Building2, cor: 'bg-emerald-50 text-emerald-500' },
]

const tamanhoEsperado = computed(() => (tipoPessoa.value === 'PF' ? 11 : 14))
const completo = computed(() => somenteNumeros(documento.value).length === tamanhoEsperado.value)

function trocarTipo(novo) {
  tipoPessoa.value = novo
  documento.value = ''
  erro.value = ''
}

function aoDigitar(e) {
  documento.value = mascararDocumento(e.target.value, tipoPessoa.value)
}

async function consultar() {
  if (!completo.value) {
    erro.value = `Digite os ${tamanhoEsperado.value} números do ${tipoPessoa.value === 'PF' ? 'CPF' : 'CNPJ'}.`
    return
  }
  erro.value = ''
  carregando.value = true
  try {
    const resultado = await consultaService.consultar({
      documento: documento.value,
      tipo: tipo.value,
      finalidade: finalidade.value,
      monitorar: monitorar.value,
    })
    router.push({ name: 'resultado', params: { id: resultado.id } })
  } catch (e) {
    erro.value = mensagemDeErro(e)
  } finally {
    carregando.value = false
  }
}
</script>

<template>
  <PageHeader trilha="Consultas › Nova" titulo="Nova consulta" subtitulo="Consulte a situação de crédito de pessoas físicas e jurídicas em tempo real." />

  <form class="relative mb-4 overflow-hidden rounded-[22px] bg-ink px-8 py-7 text-white" @submit.prevent="consultar">
    <div class="pointer-events-none absolute inset-0" style="background: radial-gradient(500px 300px at 15% 0%, rgba(109,93,252,.55), transparent 60%), radial-gradient(400px 300px at 90% 100%, rgba(18,181,166,.35), transparent 60%)" />
    <div class="relative">
      <h2 class="text-[26px] font-extrabold tracking-tight">Quem você quer analisar hoje?</h2>
      <p class="mb-4 mt-1 text-[14.5px] text-slate-400">Os dados são buscados na base da Serasa em poucos segundos.</p>

      <div class="mb-3.5 flex w-max gap-1.5 rounded-xl border border-white/10 bg-white/10 p-1">
        <button type="button" class="flex items-center gap-2 rounded-lg px-3.5 py-2 text-[13px] font-bold" :class="tipoPessoa === 'PJ' ? 'bg-white text-ink' : 'text-slate-300'" @click="trocarTipo('PJ')"><Building2 class="h-4 w-4" />CNPJ · Empresa</button>
        <button type="button" class="flex items-center gap-2 rounded-lg px-3.5 py-2 text-[13px] font-bold" :class="tipoPessoa === 'PF' ? 'bg-white text-ink' : 'text-slate-300'" @click="trocarTipo('PF')"><User class="h-4 w-4" />CPF · Pessoa física</button>
        <RouterLink :to="{ name: 'consulta-lote' }" class="flex items-center gap-2 rounded-lg px-3.5 py-2 text-[13px] font-bold text-slate-300"><Layers class="h-4 w-4" />Lote (planilha)</RouterLink>
      </div>

      <div class="flex gap-2.5 rounded-2xl bg-white p-2 shadow-2xl">
        <div class="flex flex-1 items-center gap-3 px-3">
          <Search class="h-[22px] w-[22px] text-primary-500" />
          <input :value="documento" inputmode="numeric" autofocus class="flex-1 bg-transparent text-lg font-bold tracking-wide text-ink outline-none placeholder:font-medium placeholder:text-slate-400"
                 :placeholder="tipoPessoa === 'PF' ? '000.000.000-00' : '00.000.000/0000-00'" @input="aoDigitar" />
          <span v-if="completo" class="flex items-center gap-1 rounded-full bg-emerald-50 px-2.5 py-1 text-xs font-bold text-emerald-700"><Check class="h-3.5 w-3.5" />Completo</span>
        </div>
        <button :disabled="carregando" class="btn-primary h-[52px] px-6 text-[15px] disabled:opacity-60">
          {{ carregando ? 'Consultando...' : 'Consultar agora' }}<ArrowRight v-if="!carregando" class="h-[18px] w-[18px]" />
        </button>
      </div>

      <p v-if="erro" class="mt-3 rounded-xl bg-red-500/15 px-4 py-2.5 text-sm font-semibold text-red-200">{{ erro }}</p>

      <div class="mt-4 flex gap-2.5 text-[13px] font-semibold text-indigo-100">
        <label class="flex cursor-pointer items-center gap-2 rounded-xl border border-white/10 bg-white/10 px-3 py-2">
          <input v-model="monitorar" type="checkbox" class="accent-teal-500" />Adicionar ao monitoramento
        </label>
        <label class="flex items-center gap-2 rounded-xl border border-white/10 bg-white/10 px-3 py-2">
          Finalidade:
          <select v-model="finalidade" class="bg-transparent font-bold text-white outline-none">
            <option v-for="f in FINALIDADES" :key="f" :value="f" class="text-ink">{{ f }}</option>
          </select>
        </label>
      </div>
    </div>
  </form>

  <b class="mb-3 block text-base font-extrabold">Tipo de relatório</b>
  <div class="grid grid-cols-3 gap-4">
    <button v-for="r in RELATORIOS" :key="r.valor" type="button" class="card relative text-left transition"
            :class="tipo === r.valor ? 'border-2 border-primary-500 ring-4 ring-primary-500/10' : 'hover:border-slate-300'" @click="tipo = r.valor">
      <span class="absolute right-5 top-5 rounded-full px-2.5 py-1 text-xs font-extrabold" :class="tipo === r.valor ? 'bg-primary-500 text-white' : 'bg-slate-100 text-slate-700'">{{ r.preco }}</span>
      <div class="mb-3.5 flex h-11 w-11 items-center justify-center rounded-xl" :class="r.cor"><component :is="r.icone" class="h-5 w-5" /></div>
      <b class="mb-1.5 block text-[15px]">{{ r.titulo }}</b>
      <p class="text-[13px] leading-relaxed text-slate-500">{{ r.texto }}</p>
    </button>
  </div>
  <p class="mt-4 text-xs text-slate-400">Em desenvolvimento a Serasa está em modo simulado (dados fictícios). Teste com CNPJ 11.222.333/0001-81 ou CPF 529.982.247-25.</p>
</template>
