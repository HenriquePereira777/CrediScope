<script setup>
import { computed, nextTick, onMounted, reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { AlertCircle, ArrowRight, CheckCircle2, Clock, Eye, EyeOff, Info, Loader2, Lock, Mail, ShieldCheck } from 'lucide-vue-next'
import { useAuthStore } from '@/stores/auth'
import authService from '@/services/authService'
import AppLogo from '@/components/layout/AppLogo.vue'
import LoginVitrine from '@/components/auth/LoginVitrine.vue'

const auth = useAuthStore()
const router = useRouter()
const route = useRoute()
const modoDev = import.meta.env.DEV

const REGEX_EMAIL = /^[^\s@]+@[^\s@]+\.[^\s@]+$/

// ── Estado do formulário ────────────────────────────────
// Nada é guardado no navegador. O preenchimento automático fica por conta do próprio navegador.
const form = reactive({
  email: modoDev ? 'admin@crediscope.local' : '',
  senha: '',
})
const erros = reactive({ email: '', senha: '' })
const erroGeral = ref('')
const carregando = ref(false)
const mostrarSenha = ref(false)
const capsLigado = ref(false)
const mostrarAjudaSenha = ref(false)

const campoEmail = ref(null)
const campoSenha = ref(null)

// ── Aviso vindo de outra tela (sessão expirada / saiu) ──
const AVISOS = {
  expirada: { texto: 'Sua sessão expirou. Entre novamente para continuar.', icone: Clock, classe: 'bg-amber-50 text-amber-800 ring-amber-200' },
  saiu: { texto: 'Você saiu com segurança.', icone: CheckCircle2, classe: 'bg-emerald-50 text-emerald-800 ring-emerald-200' },
}
const aviso = computed(() => (erroGeral.value ? null : AVISOS[route.query.motivo]))

onMounted(() => {
  // Se o e-mail já está preenchido, o cursor vai direto para a senha
  ;(form.email ? campoSenha : campoEmail).value?.focus()
})

// ── Validação ───────────────────────────────────────────
function validarEmail() {
  const email = form.email.trim()
  if (!email) erros.email = 'Informe seu e-mail.'
  else if (!REGEX_EMAIL.test(email)) erros.email = 'E-mail inválido. Confira se digitou corretamente.'
  else erros.email = ''
}

function validarSenha() {
  erros.senha = form.senha ? '' : 'Informe sua senha.'
}

function validar() {
  validarEmail()
  validarSenha()
  return !erros.email && !erros.senha
}

function verificarCaps(evento) {
  if (typeof evento.getModifierState === 'function') capsLigado.value = evento.getModifierState('CapsLock')
}

// ── Envio ───────────────────────────────────────────────
async function entrar() {
  if (carregando.value) return
  erroGeral.value = ''

  if (!validar()) {
    await nextTick()
    ;(erros.email ? campoEmail : campoSenha).value?.focus()
    return
  }

  carregando.value = true
  try {
    const email = form.email.trim().toLowerCase()
    // O backend grava o token no cookie HttpOnly; aqui chegam só { usuario, expiraEm }
    auth.definirSessao(await authService.login(email, form.senha))

    router.replace(destinoSeguro(route.query.voltar))
  } catch (e) {
    erroGeral.value = traduzirErro(e)
    form.senha = ''
    carregando.value = false // libera o campo antes de devolver o cursor para a senha
    await nextTick()
    campoSenha.value?.focus()
  } finally {
    carregando.value = false
  }
}

/** Só aceita voltar para telas do próprio sistema (evita redirecionar para sites de fora) */
function destinoSeguro(voltar) {
  const valido = typeof voltar === 'string' && voltar.startsWith('/') && !voltar.startsWith('//')
  return valido ? voltar : '/painel'
}

function traduzirErro(e) {
  const status = e?.response?.status
  if (!e?.response) return 'Não foi possível conectar ao servidor. Verifique sua conexão e tente novamente.'
  if (status === 401) return 'E-mail ou senha incorretos.'
  if (status === 423 || status === 429) return e.response.data?.mensagem || 'Muitas tentativas. Aguarde alguns minutos e tente novamente.'
  if (status >= 500) return 'O servidor está com instabilidade. Tente novamente em instantes.'
  return e.response.data?.mensagem || 'Não foi possível entrar. Tente novamente.'
}

const classeCampo = (erro) => [
  'group flex h-[52px] items-center gap-3 rounded-2xl border bg-white px-4 transition focus-within:ring-4',
  erro ? 'border-red-400 focus-within:border-red-500 focus-within:ring-red-500/15' : 'border-slate-300 focus-within:border-primary-500 focus-within:ring-primary-500/15',
  carregando.value ? 'opacity-70' : '',
]
</script>

<template>
  <div class="flex min-h-screen">
    <!-- Lado esquerdo (só em telas grandes) -->
    <section class="hidden flex-1 px-[72px] py-12 lg:block">
      <LoginVitrine />
    </section>

    <!-- Formulário -->
    <section class="flex w-full items-center justify-center px-4 py-10 sm:px-8 lg:w-auto lg:pr-[72px]">
      <div class="w-full max-w-[468px]">
        <AppLogo class="mb-8 justify-center lg:hidden" />

        <form
          class="rounded-[28px] bg-white px-6 py-9 text-ink shadow-[0_40px_100px_rgba(0,0,0,.45)] sm:px-[42px] sm:py-11"
          novalidate
          aria-labelledby="titulo-login"
          @submit.prevent="entrar"
        >
          <h2 id="titulo-login" class="text-[28px] font-extrabold tracking-tight sm:text-3xl">Entrar na sua conta</h2>
          <p class="mb-6 mt-2 text-[15px] text-slate-500">Bem-vindo de volta! Use seu acesso corporativo.</p>

          <!-- Aviso (sessão expirada / saiu) -->
          <p v-if="aviso" role="status" class="mb-5 flex items-start gap-2.5 rounded-xl px-4 py-3 text-sm font-semibold ring-1" :class="aviso.classe">
            <component :is="aviso.icone" class="mt-0.5 h-4 w-4 shrink-0" />{{ aviso.texto }}
          </p>

          <!-- Erro do login -->
          <p v-if="erroGeral" role="alert" class="mb-5 flex items-start gap-2.5 rounded-xl bg-red-50 px-4 py-3 text-sm font-semibold text-red-700 ring-1 ring-red-200">
            <AlertCircle class="mt-0.5 h-4 w-4 shrink-0" />{{ erroGeral }}
          </p>

          <!-- E-mail -->
          <label for="email" class="mb-2 block text-[13px] font-bold text-slate-700">E-mail corporativo</label>
          <div :class="classeCampo(erros.email)">
            <Mail class="h-5 w-5 shrink-0" :class="erros.email ? 'text-red-500' : 'text-slate-400 group-focus-within:text-primary-500'" />
            <input
              id="email"
              ref="campoEmail"
              v-model="form.email"
              type="email"
              inputmode="email"
              autocomplete="username"
              autocapitalize="none"
              spellcheck="false"
              placeholder="voce@suaempresa.com.br"
              :disabled="carregando"
              :aria-invalid="!!erros.email"
              aria-describedby="erro-email"
              class="min-w-0 flex-1 bg-transparent text-[15px] outline-none placeholder:text-slate-400"
              @blur="form.email && validarEmail()"
              @input="erros.email && validarEmail()"
            />
          </div>
          <p id="erro-email" class="mb-4 mt-1.5 min-h-[18px] text-[12.5px] font-semibold text-red-600">{{ erros.email }}</p>

          <!-- Senha -->
          <div class="mb-2 flex items-center justify-between text-[13px] font-bold text-slate-700">
            <label for="senha">Senha</label>
            <button type="button" class="font-bold text-primary-500 hover:text-primary-600" @click="mostrarAjudaSenha = !mostrarAjudaSenha">
              Esqueceu a senha?
            </button>
          </div>
          <p v-if="mostrarAjudaSenha" class="mb-3 flex items-start gap-2 rounded-xl bg-indigo-50 px-3.5 py-2.5 text-[13px] text-indigo-800">
            <Info class="mt-0.5 h-4 w-4 shrink-0" />Peça ao administrador do sistema para redefinir sua senha.
          </p>
          <div :class="classeCampo(erros.senha)">
            <Lock class="h-5 w-5 shrink-0" :class="erros.senha ? 'text-red-500' : 'text-slate-400 group-focus-within:text-primary-500'" />
            <input
              id="senha"
              ref="campoSenha"
              v-model="form.senha"
              :type="mostrarSenha ? 'text' : 'password'"
              autocomplete="current-password"
              placeholder="Sua senha"
              :disabled="carregando"
              :aria-invalid="!!erros.senha"
              aria-describedby="erro-senha aviso-caps"
              class="min-w-0 flex-1 bg-transparent text-[15px] outline-none placeholder:text-slate-400"
              @keydown="verificarCaps"
              @keyup="verificarCaps"
              @input="erros.senha && validarSenha()"
            />
            <button
              type="button"
              class="rounded-lg p-1 text-slate-400 hover:text-slate-600"
              :aria-label="mostrarSenha ? 'Ocultar senha' : 'Mostrar senha'"
              :aria-pressed="mostrarSenha"
              @click="mostrarSenha = !mostrarSenha"
            >
              <EyeOff v-if="mostrarSenha" class="h-5 w-5" /><Eye v-else class="h-5 w-5" />
            </button>
          </div>
          <p id="erro-senha" class="mt-1.5 min-h-[18px] text-[12.5px] font-semibold text-red-600">{{ erros.senha }}</p>
          <p v-if="capsLigado" id="aviso-caps" class="-mt-1 mb-1 text-[12.5px] font-semibold text-amber-600">⇪ Caps Lock está ativado</p>

          <div class="mb-5" />

          <!-- Botão -->
          <button
            type="submit"
            :disabled="carregando"
            class="flex h-[54px] w-full items-center justify-center gap-2.5 rounded-2xl bg-gradient-to-br from-primary-500 to-accent-500 text-base font-bold text-white shadow-[0_12px_28px_rgba(109,93,252,.4)] transition hover:brightness-105 focus-visible:outline-none focus-visible:ring-4 focus-visible:ring-primary-500/30 disabled:cursor-wait disabled:opacity-80"
          >
            <template v-if="carregando"><Loader2 class="h-5 w-5 animate-spin" />Entrando...</template>
            <template v-else>Entrar<ArrowRight class="h-5 w-5" /></template>
          </button>

          <p class="mt-5 text-center text-sm text-slate-500">
            Ainda não tem acesso? <b class="text-primary-500">Solicite ao administrador</b>
          </p>

          <div class="mt-6 flex flex-wrap justify-center gap-x-5 gap-y-1 border-t border-line pt-5 text-xs font-semibold text-slate-500">
            <span class="flex items-center gap-1.5"><Lock class="h-3.5 w-3.5 text-teal-brand" />Conexão segura</span>
            <span class="flex items-center gap-1.5"><ShieldCheck class="h-3.5 w-3.5 text-teal-brand" />Conforme LGPD</span>
          </div>

          <p v-if="modoDev" class="mt-4 rounded-xl bg-slate-50 px-3 py-2 text-center text-xs text-slate-500">
            Desenvolvimento · <b>admin@crediscope.local</b> / <b>admin123</b>
          </p>
        </form>
      </div>
    </section>
  </div>
</template>
