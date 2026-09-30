<script setup>
import { onMounted, ref } from 'vue'
import PageHeader from '@/components/ui/PageHeader.vue'
import StatusTag from '@/components/ui/StatusTag.vue'
import Paginacao from '@/components/ui/Paginacao.vue'
import EstadoVazio from '@/components/ui/EstadoVazio.vue'
import consultaService from '@/services/consultaService'
import { mensagemDeErro } from '@/services/http'
import { DECISAO, RECOMENDACAO, TIPO_CONSULTA } from '@/utils/rotulos'
import { formatarDataHora, formatarDocumento, formatarMoeda } from '@/utils/formatadores'

const pagina = ref(null)
const erro = ref('')

async function carregar(numero = 0) {
  try {
    pagina.value = await consultaService.historico(numero, 15)
  } catch (e) {
    erro.value = mensagemDeErro(e)
  }
}
onMounted(() => carregar())
</script>

<template>
  <PageHeader trilha="Auditoria › Histórico" titulo="Histórico de consultas" subtitulo="Registro de quem consultou, quando e por quê (exigido pela LGPD)." />
  <p v-if="erro" class="card mb-4 text-sm font-semibold text-red-700">{{ erro }}</p>

  <div v-if="pagina" class="card p-2 pb-3">
    <EstadoVazio v-if="!pagina.itens.length" titulo="Nenhuma consulta registrada" texto="As consultas feitas pela equipe aparecem aqui." />
    <div v-else class="overflow-x-auto">
      <table class="tabela">
      <thead><tr><th>Data / hora</th><th>Usuário</th><th>Consultado</th><th>Tipo</th><th>Finalidade</th><th>Recomendação</th><th>Decisão</th><th class="text-right">Custo</th></tr></thead>
      <tbody>
        <tr v-for="c in pagina.itens" :key="c.id" class="cursor-pointer hover:bg-slate-50" @click="$router.push({ name: 'resultado', params: { id: c.id } })">
          <td class="font-semibold text-slate-600">{{ formatarDataHora(c.criadoEm) }}</td>
          <td class="font-semibold">{{ c.consultadoPor }}</td>
          <td><b class="block">{{ c.nome }}</b><small class="text-xs text-slate-400">{{ formatarDocumento(c.documento) }}</small></td>
          <td><StatusTag :rotulo="TIPO_CONSULTA[c.tipo]" classe="bg-indigo-50 text-primary-600" :ponto="false" /></td>
          <td>{{ c.finalidade }}</td>
          <td><StatusTag v-bind="RECOMENDACAO[c.recomendacao]" /></td>
          <td><StatusTag v-bind="DECISAO[c.decisao || 'PENDENTE']" /></td>
          <td class="text-right font-bold">{{ formatarMoeda(c.custo) }}</td>
        </tr>
      </tbody>
    </table>
    </div>
    <Paginacao :pagina="pagina" @ir="carregar" />
  </div>
</template>
