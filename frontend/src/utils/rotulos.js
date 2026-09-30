/** Textos e cores dos valores que vêm da API (enums do backend). */

export const RISCO = {
  BAIXO: { rotulo: 'Baixo', classe: 'bg-emerald-50 text-emerald-700', cor: '#12B76A' },
  MEDIO: { rotulo: 'Médio', classe: 'bg-amber-50 text-amber-700', cor: '#F79009' },
  ALTO: { rotulo: 'Alto', classe: 'bg-red-50 text-red-700', cor: '#F04438' },
}

export const RECOMENDACAO = {
  APROVAR: { rotulo: 'Aprovar', classe: 'bg-emerald-50 text-emerald-700' },
  ANALISE_MANUAL: { rotulo: 'Análise manual', classe: 'bg-amber-50 text-amber-700' },
  RECUSAR: { rotulo: 'Recusar', classe: 'bg-red-50 text-red-700' },
}

export const DECISAO = {
  APROVADO: { rotulo: 'Aprovado', classe: 'bg-emerald-50 text-emerald-700' },
  RECUSADO: { rotulo: 'Recusado', classe: 'bg-red-50 text-red-700' },
  EM_ANALISE: { rotulo: 'Em análise', classe: 'bg-amber-50 text-amber-700' },
  PENDENTE: { rotulo: 'Pendente', classe: 'bg-slate-100 text-slate-600' },
}

export const TIPO_CONSULTA = {
  COMPLETO: 'Completo',
  SCORE: 'Score rápido',
  CADASTRAL: 'Cadastral',
  MONITORAMENTO: 'Monitoramento',
}

export const PENDENCIA = {
  RESTRICAO: 'Restrições (Pefin/Refin)',
  PROTESTO: 'Protestos',
  CHEQUE: 'Cheques sem fundo',
  ACAO_JUDICIAL: 'Ações judiciais',
  FALENCIA: 'Falência / Recuperação',
}

export const PERFIL = {
  ADMINISTRADOR: 'Administrador',
  GERENTE: 'Gerente',
  ANALISTA: 'Analista',
  VENDEDOR: 'Vendedor',
}

export const CORES_AVATAR = [
  'bg-indigo-50 text-indigo-700',
  'bg-orange-50 text-orange-700',
  'bg-emerald-50 text-emerald-700',
  'bg-sky-50 text-sky-700',
  'bg-pink-50 text-pink-700',
  'bg-violet-50 text-violet-700',
]
