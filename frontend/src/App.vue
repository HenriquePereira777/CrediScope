<script setup>
import { computed } from 'vue'
import { useRoute } from 'vue-router'
import AppLayout from '@/layouts/AppLayout.vue'
import AuthLayout from '@/layouts/AuthLayout.vue'

const route = useRoute()
// Cada rota diz qual layout usa (meta.layout). Padrão: layout do sistema.
const layout = computed(() => (route.meta.layout === 'auth' ? AuthLayout : AppLayout))
</script>

<template>
  <component :is="layout">
    <RouterView />
  </component>
</template>
