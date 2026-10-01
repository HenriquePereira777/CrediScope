import { createRouter, createWebHistory } from 'vue-router'
import { useAuthStore } from '@/stores/auth'

const routes = [
  // Autenticação (sem menu lateral)
  { path: '/login', name: 'login', component: () => import('@/views/auth/LoginView.vue'), meta: { layout: 'auth', publica: true } },
  { path: '/verificacao', name: 'verificacao', component: () => import('@/views/auth/VerificacaoView.vue'), meta: { layout: 'auth', publica: true } },

  // Sistema
  { path: '/', redirect: '/painel' },
  { path: '/painel', name: 'painel', component: () => import('@/views/painel/PainelView.vue') },
  { path: '/consultas/nova', name: 'nova-consulta', component: () => import('@/views/consulta/NovaConsultaView.vue') },
  { path: '/consultas/lote', name: 'consulta-lote', component: () => import('@/views/consulta/ConsultaLoteView.vue') },
  { path: '/consultas/:id', name: 'resultado', component: () => import('@/views/consulta/ResultadoView.vue'), props: true },
  { path: '/carteira', name: 'carteira', component: () => import('@/views/carteira/CarteiraView.vue') },
  { path: '/alertas', name: 'alertas', component: () => import('@/views/alertas/AlertasView.vue') },
  { path: '/historico', name: 'historico', component: () => import('@/views/historico/HistoricoView.vue') },
  { path: '/relatorios', name: 'relatorios', component: () => import('@/views/relatorios/RelatoriosView.vue') },
  { path: '/politica', name: 'politica', component: () => import('@/views/politica/PoliticaView.vue') },
  { path: '/usuarios', name: 'usuarios', component: () => import('@/views/usuarios/UsuariosView.vue') },
  { path: '/integracoes', name: 'integracoes', component: () => import('@/views/integracoes/IntegracoesView.vue') },
  { path: '/configuracoes', name: 'configuracoes', component: () => import('@/views/configuracoes/ConfiguracoesView.vue') },

  { path: '/:pathMatch(.*)*', redirect: '/painel' },
]

const router = createRouter({
  history: createWebHistory(),
  routes,
})

// Controle de acesso às telas
router.beforeEach(async (to) => {
  const auth = useAuthStore()

  // Na primeira navegação, pergunta ao backend se já existe sessão (cookie válido)
  await auth.verificarSessao()

  // Telas públicas (login, verificação). Quem já está logado não precisa ver o login.
  if (to.meta.publica) {
    if (to.name === 'login' && auth.estaLogado) return { name: 'painel' }
    return true
  }

  // Telas do sistema: exige sessão válida
  if (!auth.estaLogado) {
    const expirou = auth.sessaoExpirada()
    auth.limpar()
    return {
      name: 'login',
      query: { voltar: to.fullPath, ...(expirou ? { motivo: 'expirada' } : {}) },
    }
  }
  return true
})

export default router
