<script setup>
import AppSidebar from '@/components/layout/AppSidebar.vue'
import AppTopbar from '@/components/layout/AppTopbar.vue'
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
