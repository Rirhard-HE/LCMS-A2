<template>
  <aside :class="['sidebar-shell', { collapsed }]">
    <div :class="['sidebar', { collapsed }]">
      <header class="sidebar-header">
        <button class="profile" type="button" @click="goDashboard">
          <span class="profile-avatar">{{ user.initials }}</span>
          <span v-if="!collapsed" class="profile-copy">
            <span class="profile-name">{{ user.name }}</span>
            <span class="profile-role">{{ user.role }}</span>
          </span>
        </button>
        <button class="collapse-toggle" type="button" @click="toggle">
          <i :class="collapsed ? 'el-icon-arrow-right' : 'el-icon-arrow-left'"></i>
        </button>
      </header>

      <nav class="nav">
        <el-menu
          :collapse="collapsed"
          :collapse-transition="false"
          :default-active="activePath"
          background-color="transparent"
          class="nav-menu"
          text-color="#67728c"
          active-text-color="#ffffff"
          @select="handleSelect"
        >
          <el-menu-item
            v-for="item in navItems"
            :key="item.id"
            :index="item.path"
            class="nav-item"
            :title="collapsed ? item.label : null"
          >
            <i :class="item.icon"></i>
            <span slot="title" class="nav-label">{{ item.label }}</span>
          </el-menu-item>
        </el-menu>
      </nav>

      <footer class="sidebar-footer">
        <button class="logout" type="button" @click="logout">
          <i class="el-icon-switch-button"></i>
          <span v-if="!collapsed">Logout</span>
        </button>
      </footer>
    </div>
  </aside>
</template>

<script>
export default {
  name: 'Sidebar',
  props: { value: { type: Boolean, default: false } },
  data() {
    return {
      collapsed: this.value,
      user: {
        initials: 'AR',
        name: 'Alex Rivera',
        role: 'Managing Partner'
      },
      navItems: [
        { id: 'dashboard', path: '/dashboard', label: 'Dashboard', icon: 'el-icon-data-analysis' },
        { id: 'categories', path: '/categories', label: 'Categories', icon: 'el-icon-collection' },
        { id: 'cases', path: '/cases', label: 'Cases', icon: 'el-icon-s-order' },
        { id: 'evidence', path: '/evidence', label: 'Evidence', icon: 'el-icon-document' },
        { id: 'hearings', path: '/hearings', label: 'Hearings', icon: 'el-icon-reading' }
      ]
    }
  },
  watch: {
    value(v) { this.collapsed = v },
    collapsed(v) { this.$emit('input', v) }
  },
  computed: {
    activePath() {
      const current = this.$route.path
      const match = this.navItems.find(item => current.startsWith(item.path))
      return match ? match.path : current
    }
  },
  methods: {
    toggle() {
      this.collapsed = !this.collapsed
    },
    handleSelect(path) {
      if (path) {
        this.$router.push(path)
      }
    },
    logout() {
      this.$emit('logout')
      this.$router.push('/logout')
    },
    goDashboard() {
      this.$router.push('/dashboard')
    }
  }
}
</script>

<style scoped>
.sidebar-shell {
  position: fixed;
  top: 24px;
  left: 24px;
  z-index: 1000;
  width: 264px;
  transition: width 0.25s ease;
}

.sidebar-shell.collapsed {
  width: 72px;
}

.sidebar {
  width: 264px;
  max-height: calc(100vh - 48px);
  display: flex;
  flex-direction: column;
  background: #f6f7fb;
  border-radius: 24px;
  padding: 24px 20px;
  box-shadow: 0 16px 40px rgba(15, 23, 42, 0.12);
  transition: width 0.25s ease, padding 0.25s ease;
  overflow: visible;
}

.sidebar.collapsed {
  width: 72px;
  padding: 24px 12px;
  align-items: center;
}

.sidebar-header {
  position: relative;
  width: 100%;
  display: flex;
  align-items: center;
  justify-content: flex-start;
  gap: 12px;
}

.profile {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 10px 12px;
  border-radius: 16px;
  background: #ffffff;
  border: none;
  cursor: pointer;
  color: inherit;
  box-shadow: 0 8px 20px rgba(59, 130, 246, 0.12);
  flex: 1;
  min-width: 0;
}

.sidebar.collapsed .profile {
  padding: 10px;
  justify-content: center;
  flex: 0 0 auto;
  margin-right: 28px;
}

.profile-avatar {
  width: 34px;
  height: 34px;
  border-radius: 11px;
  background: linear-gradient(135deg, #6366f1, #8b5cf6);
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 700;
  color: #ffffff;
}

.profile-copy {
  display: flex;
  flex-direction: column;
  line-height: 1.2;
}

.profile-name {
  font-size: 15px;
  font-weight: 600;
  color: #1f2937;
}

.profile-role {
  font-size: 12px;
  color: #64748b;
}

.collapse-toggle {
  position: absolute;
  top: 50%;
  right: -18px;
  transform: translateY(-50%);
  width: 34px;
  height: 34px;
  border-radius: 50%;
  border: none;
  background: #6366f1;
  color: #ffffff;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 12px 24px rgba(99, 102, 241, 0.25);
  transition: background 0.2s ease, transform 0.2s ease;
}

.sidebar.collapsed .collapse-toggle {
  right: -18px;
}

.collapse-toggle:hover {
  background: #4f46e5;
  box-shadow: 0 16px 30px rgba(79, 70, 229, 0.35);
}

.nav {
  flex: 1;
  margin-top: 28px;
  width: 100%;
}

.nav-menu {
  border-right: none !important;
  background: transparent;
}

.nav-menu ::v-deep(.el-menu-item) {
  border-radius: 14px;
  margin: 6px 0;
  height: 46px;
  line-height: 46px;
  padding: 0 18px;
  font-weight: 500;
}

.nav-menu ::v-deep(.el-menu-item:hover),
.nav-menu ::v-deep(.el-menu-item.is-active) {
  background: linear-gradient(135deg, #6366f1, #8b5cf6) !important;
  color: #ffffff !important;
}

.nav-menu ::v-deep(.el-menu-item:hover i),
.nav-menu ::v-deep(.el-menu-item.is-active i) {
  color: #ffffff;
}

.nav-item i {
  font-size: 18px;
}

.nav-label {
  display: inline-flex;
  align-items: center;
  gap: 12px;
}

.sidebar-footer {
  margin-top: auto;
  width: 100%;
  padding-top: 16px;
}

.logout {
  width: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 10px;
  padding: 10px 12px;
  border-radius: 14px;
  border: none;
  background: #ffffff;
  cursor: pointer;
  color: #1f2937;
  box-shadow: 0 8px 20px rgba(15, 23, 42, 0.08);
}

.sidebar.collapsed .logout {
  justify-content: center;
}

@media (max-width: 768px) {
  .sidebar-shell {
    top: 16px;
    left: 16px;
  }
}
</style>










