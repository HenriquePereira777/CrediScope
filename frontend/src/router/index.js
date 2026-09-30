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

// Bloqueia as telas do sistema para quem não está logado
router.beforeEach((to) => {
  const auth = useAuthStore()
  if (!to.meta.publica && !auth.estaLogado) {
    return { name: 'login', query: { voltar: to.fullPath } }
  }
})

export default router
