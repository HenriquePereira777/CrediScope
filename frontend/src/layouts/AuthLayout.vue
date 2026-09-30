<template>
  <!-- Fundo escuro com degradê, usado no login e na verificação em 2 fatores -->
  <div class="relative min-h-screen overflow-hidden bg-[#070B18] text-white">
    <div
      class="pointer-events-none absolute inset-0"
      style="background:
        radial-gradient(600px 500px at 12% 10%, rgba(109,93,252,.55), transparent 60%),
        radial-gradient(520px 520px at 55% 95%, rgba(18,181,166,.35), transparent 60%),
        radial-gradient(500px 400px at 95% 20%, rgba(79,140,255,.35), transparent 60%)"
    />
    <div class="relative">
      <slot />
    </div>
  </div>
</template>
