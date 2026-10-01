<script setup>
/**
 * Lado esquerdo da tela de login: título e cartões ilustrativos.
 * É só decorativo (dados fictícios) e some em telas pequenas.
 */
import { Bell, Check, KeyRound, Lock, ShieldCheck } from 'lucide-vue-next'
import AppLogo from '@/components/layout/AppLogo.vue'
import ScoreGauge from '@/components/ui/ScoreGauge.vue'
</script>

<template>
  <div class="relative flex h-full flex-col">
    <AppLogo />

    <div class="mt-14">
      <span class="inline-flex items-center gap-2 rounded-full border border-white/15 bg-white/10 px-3.5 py-2 text-[13px] font-semibold text-indigo-200">
        <span class="h-2 w-2 rounded-full bg-emerald-400 shadow-[0_0_0_4px_rgba(50,213,131,.2)]" />
        Integrado em tempo real à API Serasa
      </span>
      <h1 class="mb-4 mt-5 text-[48px] font-extrabold leading-[1.06] tracking-[-2px] xl:text-[54px]">
        Decisões de crédito<br />com
        <span class="bg-gradient-to-r from-violet-300 via-sky-300 to-teal-300 bg-clip-text text-transparent">dados</span>, não<br />com achismo.
      </h1>
      <p class="max-w-md text-[17px] leading-relaxed text-slate-400">
        Consulte CPF e CNPJ, monitore sua carteira e aprove com segurança, tudo num só lugar.
      </p>
    </div>

    <!-- Cartões ilustrativos -->
    <div class="relative mt-10 h-[300px] max-w-[640px]" aria-hidden="true">
      <div class="absolute left-0 top-8 w-[290px] rounded-[20px] border border-white/15 bg-white/[.07] p-5 shadow-2xl backdrop-blur-md">
        <p class="text-[11px] font-bold tracking-[1.2px] text-slate-400">SCORE DE CRÉDITO</p>
        <div class="relative mt-1 h-[118px] overflow-hidden">
          <ScoreGauge :score="842" class="origin-top-left scale-[.92]" />
          <span class="absolute inset-x-0 top-[62px] w-[230px] text-center text-[40px] font-extrabold tracking-tight">842</span>
        </div>
        <div class="mt-2 flex justify-between text-xs text-slate-300">
          <span>Comercial Sul Grãos</span><span class="font-bold text-teal-300">Baixo risco</span>
        </div>
      </div>

      <div class="absolute left-[240px] top-0 flex w-[270px] -rotate-2 items-center gap-3 rounded-[20px] bg-white px-4 py-3.5 text-ink shadow-2xl">
        <span class="flex h-10 w-10 items-center justify-center rounded-xl bg-emerald-500"><Check class="h-5 w-5 stroke-[2.8] text-white" /></span>
        <span><b class="block text-sm">Crédito aprovado</b><span class="text-[12.5px] text-slate-500">Limite de R$ 25.000 · 60 dias</span></span>
      </div>

      <div class="absolute left-[330px] top-[120px] w-[290px] rounded-[20px] border border-white/15 bg-white/[.07] px-5 py-4 shadow-2xl backdrop-blur-md">
        <div class="flex items-center justify-between">
          <span class="text-xs font-semibold text-slate-300">Inadimplência da carteira</span>
          <span class="rounded-full bg-teal-300/15 px-2 py-0.5 text-[11px] font-extrabold text-teal-300">-38%</span>
        </div>
        <p class="mb-2 mt-1 text-[28px] font-extrabold">1,8%</p>
        <svg width="250" height="46" viewBox="0 0 250 46">
          <defs>
            <linearGradient id="vitrine-area" x1="0" x2="0" y1="0" y2="1">
              <stop offset="0" stop-color="#5EEAD4" stop-opacity=".45" /><stop offset="1" stop-color="#5EEAD4" stop-opacity="0" />
            </linearGradient>
          </defs>
          <path d="M0,6 C30,8 45,18 70,16 S110,28 140,26 S190,38 220,36 S245,41 250,40 L250,46 L0,46Z" fill="url(#vitrine-area)" />
          <path d="M0,6 C30,8 45,18 70,16 S110,28 140,26 S190,38 220,36 S245,41 250,40" fill="none" stroke="#5EEAD4" stroke-width="2.5" />
        </svg>
      </div>

      <div class="absolute left-4 top-[232px] flex w-[260px] items-center gap-2.5 rounded-2xl border border-white/15 bg-white/[.07] px-3.5 py-3 shadow-2xl backdrop-blur-md">
        <span class="flex h-[34px] w-[34px] items-center justify-center rounded-[10px] bg-red-500/20 text-red-300"><Bell class="h-[17px] w-[17px]" /></span>
        <span class="text-[12.5px] text-indigo-100"><b class="block text-[13px] text-white">Nova restrição detectada</b>Mercado Bom Preço · agora</span>
      </div>
    </div>

    <div class="mt-auto flex flex-wrap gap-x-6 gap-y-2 pt-8 text-[13px] font-semibold text-slate-400">
      <span class="flex items-center gap-2"><ShieldCheck class="h-4 w-4 text-sky-300" />Dados protegidos (LGPD)</span>
      <span class="flex items-center gap-2"><Lock class="h-4 w-4 text-sky-300" />Conexão segura</span>
      <span class="flex items-center gap-2"><KeyRound class="h-4 w-4 text-sky-300" />Acesso individual</span>
    </div>
  </div>
</template>
