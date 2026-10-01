<script setup>
import { onBeforeUnmount, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import AppSidebar from '@/components/layout/AppSidebar.vue'
import AppTopbar from '@/components/layout/AppTopbar.vue'
import { useAuthStore } from '@/stores/auth'

const auth = useAuthStore()
const router = useRouter()

// Confere a cada 30 segundos se a sessão expirou, mesmo que o usuário não clique em nada
let verificador
onMounted(() => {
  verificador = setInterval(() => {
    if (!auth.estaLogado) {
      auth.limpar()
      router.push({ name: 'login', query: { motivo: 'expirada', voltar: router.currentRoute.value.fullPath } })
    }
  }, 30_000)
})
onBeforeUnmount(() => clearInterval(verificador))
</script>

<template>
  <div class="flex min-h-screen bg-page">
    <AppSidebar />
    <main class="flex min-w-0 flex-1 flex-col px-6 py-4">
      <AppTopbar />
      <slot />
    </main>
  </div>
</template>
