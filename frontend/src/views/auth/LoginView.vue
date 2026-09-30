<script setup>
import { ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ArrowRight, Eye, EyeOff, KeyRound, Lock, Mail, ShieldCheck } from 'lucide-vue-next'
import { useAuthStore } from '@/stores/auth'
import authService from '@/services/authService'
import { mensagemDeErro } from '@/services/http'
import AppLogo from '@/components/layout/AppLogo.vue'

const auth = useAuthStore()
const router = useRouter()
const route = useRoute()

const modoDev = import.meta.env.DEV
const email = ref(modoDev ? 'admin@crediscope.local' : '')
const senha = ref('')
const mostrarSenha = ref(false)
const carregando = ref(false)
const erro = ref('')

async function entrar() {
  erro.value = ''
  carregando.value = true
  try {
    const resposta = await authService.login(email.value, senha.value)
    auth.definirSessao(resposta.token, resposta.usuario)
    router.push(route.query.voltar || '/painel')
  } catch (e) {
    erro.value = mensagemDeErro(e, 'Não foi possível entrar.')
  } finally {
    carregando.value = false
  }
}
</script>

<template>
  <div class="flex min-h-screen items-center justify-between gap-16 px-[72px] py-12">
    <!-- Lado esquerdo -->
    <div class="max-w-xl">
      <AppLogo />
      <span class="mt-16 inline-flex items-center gap-2 rounded-full border border-white/15 bg-white/10 px-3.5 py-2 text-[13px] font-semibold text-indigo-200">
        <span class="h-2 w-2 rounded-full bg-emerald-400 shadow-[0_0_0_4px_rgba(50,213,131,.2)]" />
        Integrado em tempo real à API Serasa
      </span>
      <h1 class="mb-4 mt-5 text-[52px] font-extrabold leading-[1.06] tracking-[-2px]">
        Decisões de crédito<br />com
        <span class="bg-gradient-to-r from-violet-300 via-sky-300 to-teal-300 bg-clip-text text-transparent">dados</span>, não<br />com achismo.
      </h1>
      <p class="max-w-md text-[17px] leading-relaxed text-slate-400">
        Consulte CPF e CNPJ, monitore sua carteira e aprove com segurança, tudo num só lugar.
      </p>
      <div class="mt-12 flex gap-6 text-[13px] font-semibold text-slate-400">
        <span class="flex items-center gap-2"><ShieldCheck class="h-4 w-4 text-sky-300" />Dados protegidos (LGPD)</span>
        <span class="flex items-center gap-2"><Lock class="h-4 w-4 text-sky-300" />Conexão segura</span>
        <span class="flex items-center gap-2"><KeyRound class="h-4 w-4 text-sky-300" />Acesso individual</span>
      </div>
    </div>

    <!-- Formulário -->
    <form class="w-[468px] shrink-0 rounded-[28px] bg-white px-[42px] py-11 text-ink shadow-[0_40px_100px_rgba(0,0,0,.45)]" @submit.prevent="entrar">
      <h2 class="text-3xl font-extrabold tracking-tight">Entrar na sua conta</h2>
      <p class="mb-7 mt-2 text-[15px] text-slate-500">Bem-vindo de volta! Use seu acesso corporativo.</p>

      <label class="mb-2 block text-[13px] font-bold text-slate-700" for="email">E-mail corporativo</label>
      <div class="group mb-[18px] flex h-[52px] items-center gap-3 rounded-2xl border border-slate-300 px-4 focus-within:border-primary-500 focus-within:ring-4 focus-within:ring-primary-500/15">
        <Mail class="h-5 w-5 text-slate-400 group-focus-within:text-primary-500" />
        <input id="email" v-model="email" type="email" required autocomplete="username" class="flex-1 bg-transparent text-[15px] outline-none" placeholder="voce@suaempresa.com.br" />
      </div>

      <div class="mb-2 flex justify-between text-[13px] font-bold text-slate-700">
        <label for="senha">Senha</label>
        <a class="cursor-pointer text-primary-500">Esqueceu a senha?</a>
      </div>
      <div class="group mb-5 flex h-[52px] items-center gap-3 rounded-2xl border border-slate-300 px-4 focus-within:border-primary-500 focus-within:ring-4 focus-within:ring-primary-500/15">
        <Lock class="h-5 w-5 text-slate-400 group-focus-within:text-primary-500" />
        <input id="senha" v-model="senha" :type="mostrarSenha ? 'text' : 'password'" required autocomplete="current-password" class="flex-1 bg-transparent text-[15px] outline-none" placeholder="Sua senha" />
        <button type="button" class="text-slate-400 hover:text-slate-600" :title="mostrarSenha ? 'Ocultar' : 'Mostrar'" @click="mostrarSenha = !mostrarSenha">
          <EyeOff v-if="mostrarSenha" class="h-5 w-5" /><Eye v-else class="h-5 w-5" />
        </button>
      </div>

      <p v-if="erro" class="mb-4 rounded-xl bg-red-50 px-4 py-3 text-sm font-semibold text-red-700">{{ erro }}</p>

      <button :disabled="carregando" class="flex h-[54px] w-full items-center justify-center gap-2.5 rounded-2xl bg-gradient-to-br from-primary-500 to-accent-500 text-base font-bold text-white shadow-[0_12px_28px_rgba(109,93,252,.4)] disabled:opacity-60">
        {{ carregando ? 'Entrando...' : 'Entrar' }}
        <ArrowRight v-if="!carregando" class="h-5 w-5" />
      </button>

      <p class="mt-5 text-center text-sm text-slate-500">
        Ainda não tem acesso? <b class="text-primary-500">Solicite ao administrador</b>
      </p>
      <p v-if="modoDev" class="mt-6 border-t border-line pt-5 text-center text-xs text-slate-400">
        Ambiente de desenvolvimento · usuário inicial: <b>admin@crediscope.local</b> / <b>admin123</b>
      </p>
    </form>
  </div>
</template>
