import { defineStore } from 'pinia'
import { computed, ref } from 'vue'
import authService from '@/services/authService'

/**
 * Sessão do usuário, guardada SÓ na memória.
 * Nada vai para o localStorage: o token fica no cookie HttpOnly (o JavaScript não lê)
 * e os dados do usuário são buscados de novo no backend (/auth/me) ao abrir o sistema.
 */
export const useAuthStore = defineStore('auth', () => {
  const usuario = ref(null)
  /** Momento (em milissegundos) em que a sessão expira */
  const expiraEm = ref(0)
  /** Já perguntamos ao backend se existe sessão? (evita perguntar a cada troca de tela) */
  const verificado = ref(false)

  const estaLogado = computed(() => !!usuario.value && Date.now() < expiraEm.value)

  const iniciais = computed(() =>
    (usuario.value?.nome || '?')
      .split(' ')
      .filter(Boolean)
      .slice(0, 2)
      .map((p) => p[0])
      .join('')
      .toUpperCase(),
  )

  /** Havia usuário na memória, mas o prazo da sessão passou */
  function sessaoExpirada() {
    return !!usuario.value && Date.now() >= expiraEm.value
  }

  /** Recebe a resposta do login ou do /me: { usuario, expiraEm } */
  function definirSessao(sessao) {
    usuario.value = sessao.usuario
    expiraEm.value = new Date(sessao.expiraEm).getTime()
    verificado.value = true
  }

  /** Limpa a sessão só na memória (sem chamar o backend) */
  function limpar() {
    usuario.value = null
    expiraEm.value = 0
  }

  /** Ao abrir o sistema: pergunta ao backend se o cookie ainda vale */
  async function verificarSessao() {
    if (verificado.value) return
    try {
      definirSessao(await authService.me())
    } catch {
      limpar()
    } finally {
      verificado.value = true
    }
  }

  /** Sair: backend apaga o cookie e a memória é limpa */
  async function sair() {
    try {
      await authService.logout()
    } catch {
      /* mesmo sem resposta do backend, limpa a sessão local */
    }
    limpar()
  }

  return { usuario, expiraEm, estaLogado, iniciais, sessaoExpirada, definirSessao, limpar, verificarSessao, sair }
})
