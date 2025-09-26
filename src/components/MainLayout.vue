<template>
  <el-container class="app-layout">
    <el-header class="app-header">
      <Navbar />
    </el-header>

    <el-container class="app-body">
      <el-aside :width="collapsed ? '64px' : '220px'" class="app-aside">
        <Sidebar v-model="collapsed" />
      </el-aside>

      <el-main class="app-main">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
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

.app-layout {
  height: 100vh;
}

.app-header {
  height: var(--nav-h) !important; 
  line-height: var(--nav-h);
  padding: 0;
}

.app-body {
  height: calc(100vh - var(--nav-h));
}

.app-aside {
  overflow: auto;
  border-right: 1px solid rgba(255,255,255,.06);
  transition: width .25s ease;
  background: rgba(41,112,179,0.9);
}

.app-main {
  min-width: 0;
  height: 100%;
  overflow: auto;
  padding: 0;
  background: radial-gradient(1200px 600px at 10% 10%, rgba(99,102,241,0.15), transparent),
              radial-gradient(1200px 600px at 90% 30%, rgba(16,185,129,0.16), transparent),
              linear-gradient(180deg, #F8FAFC 0%, #EEF2FF 100%);
}
</style>
