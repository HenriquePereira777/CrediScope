import { defineStore } from 'pinia'
import { computed, ref } from 'vue'

const CHAVE_TOKEN = 'crediscope.token'
const CHAVE_USUARIO = 'crediscope.usuario'

function lerUsuario() {
  try {
    return JSON.parse(localStorage.getItem(CHAVE_USUARIO))
  } catch {
    return null
  }
}

export const useAuthStore = defineStore('auth', () => {
  const token = ref(localStorage.getItem(CHAVE_TOKEN))
  const usuario = ref(lerUsuario())

  const estaLogado = computed(() => !!token.value)
  const iniciais = computed(() =>
    (usuario.value?.nome || '?')
      .split(' ')
      .filter(Boolean)
      .slice(0, 2)
      .map((p) => p[0])
      .join('')
      .toUpperCase(),
  )

  function definirSessao(novoToken, dadosUsuario) {
    token.value = novoToken
    usuario.value = dadosUsuario
    localStorage.setItem(CHAVE_TOKEN, novoToken)
    localStorage.setItem(CHAVE_USUARIO, JSON.stringify(dadosUsuario))
  }

  function sair() {
    token.value = null
    usuario.value = null
    localStorage.removeItem(CHAVE_TOKEN)
    localStorage.removeItem(CHAVE_USUARIO)
  }

  return { token, usuario, estaLogado, iniciais, definirSessao, sair }
})
