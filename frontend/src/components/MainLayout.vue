<template>
  <div :class="['app-shell', { 'is-collapsed': collapsed }]">
    <Sidebar v-model="collapsed" />
    <div class="app-content">
      <Navbar />
      <main class="app-main">
        <router-view />
      </main>
    </div>
  </div>
</template>

<script>
import Navbar from '@/components/Navbar.vue'
import Sidebar from '@/components/Sidebar.vue'

export default {
  name: 'MainLayout',
  components: { Navbar, Sidebar },
  data() {
    return { collapsed: false }
  }
}
</script>

<style scoped>
.app-shell {
  position: relative;
  min-height: 100vh;
  background: linear-gradient(180deg, #f8fafc 0%, #eef2ff 100%);
  padding-left: calc(264px + 48px);
  transition: padding-left 0.25s ease;
}

.app-shell.is-collapsed {
  padding-left: calc(72px + 48px);
}

.app-content {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  padding: 24px 32px 48px 32px;
  transition: padding 0.25s ease;
}

.app-shell.is-collapsed .app-content {
  padding-left: 32px;
}

.app-content > * + * {
  margin-top: 24px;
}

.app-main {
  flex: 1;
  min-height: 0;
}

@media (max-width: 1024px) {
  .app-shell {
    padding-left: calc(72px + 32px);
  }

  .app-content {
    padding: 24px;
  }
}
</style>
