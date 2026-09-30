<script setup>
import { onMounted, ref } from 'vue'
import { Plus } from 'lucide-vue-next'
import PageHeader from '@/components/ui/PageHeader.vue'
import StatusTag from '@/components/ui/StatusTag.vue'
import ScoreBar from '@/components/ui/ScoreBar.vue'
import Avatar from '@/components/ui/Avatar.vue'
import Paginacao from '@/components/ui/Paginacao.vue'
import EstadoVazio from '@/components/ui/EstadoVazio.vue'
import clienteService from '@/services/clienteService'
import { mensagemDeErro } from '@/services/http'
import { RISCO } from '@/utils/rotulos'
import { formatarDataHora, formatarDocumento, formatarMoeda } from '@/utils/formatadores'

const pagina = ref(null)
const faixa = ref(null)
const erro = ref('')

const FILTROS = [
  { valor: null, rotulo: 'Todos' },
  { valor: 'BAIXO', rotulo: 'Baixo risco' },
  { valor: 'MEDIO', rotulo: 'Médio risco' },
  { valor: 'ALTO', rotulo: 'Alto risco' },
]

async function carregar(numero = 0) {
  try {
    pagina.value = await clienteService.carteira(faixa.value, numero, 15)
  } catch (e) {
    erro.value = mensagemDeErro(e)
  }
}
function filtrar(valor) {
  faixa.value = valor
  carregar()
}
onMounted(() => carregar())
</script>

<template>
  <PageHeader trilha="Clientes › Carteira" titulo="Carteira de clientes" subtitulo="Clientes monitorados. Adicione clientes marcando “monitoramento” na consulta.">
    <template #acoes>
      <RouterLink :to="{ name: 'nova-consulta' }" class="btn-primary"><Plus class="h-[18px] w-[18px]" />Adicionar cliente</RouterLink>
    </template>
  </PageHeader>

  <div class="mb-3.5 flex gap-2.5">
    <button v-for="f in FILTROS" :key="f.rotulo" class="flex h-10 items-center rounded-xl border px-3.5 text-[13px] font-semibold"
            :class="faixa === f.valor ? 'border-indigo-300 bg-primary-50 text-primary-600' : 'border-line bg-white text-slate-700'" @click="filtrar(f.valor)">
      {{ f.rotulo }}
    </button>
  </div>

  <p v-if="erro" class="card mb-4 text-sm font-semibold text-red-700">{{ erro }}</p>

  <div v-if="pagina" class="card p-2 pb-3">
    <EstadoVazio v-if="!pagina.itens.length" titulo="Nenhum cliente monitorado" texto="Faça uma consulta com a opção “Adicionar ao monitoramento” marcada." />
    <div v-else class="overflow-x-auto">
      <table class="tabela">
      <thead><tr><th>Cliente</th><th>Score</th><th>Risco</th><th>Cidade</th><th>Limite concedido</th><th>Última atualização</th></tr></thead>
      <tbody>
        <tr v-for="c in pagina.itens" :key="c.id">
          <td><div class="flex items-center gap-3"><Avatar :nome="c.nome" /><div><b class="block">{{ c.nome }}</b><small class="text-xs text-slate-400">{{ formatarDocumento(c.documento) }}</small></div></div></td>
          <td><ScoreBar :score="c.scoreAtual" :faixa="c.faixaRisco" /></td>
          <td><StatusTag v-if="c.faixaRisco" :rotulo="RISCO[c.faixaRisco].rotulo" :classe="RISCO[c.faixaRisco].classe" /></td>
          <td class="text-slate-600">{{ c.cidade }} / {{ c.uf }}</td>
          <td class="font-bold">{{ formatarMoeda(c.limiteConcedido) }}</td>
          <td class="text-slate-500">{{ formatarDataHora(c.atualizadoScoreEm) }}</td>
        </tr>
      </tbody>
    </table>
    </div>
    <Paginacao :pagina="pagina" @ir="carregar" />
  </div>
</template>
