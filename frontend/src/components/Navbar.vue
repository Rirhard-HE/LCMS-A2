<template>
  <header class="lcms-navbar">
    <div class="navbar-left">
      <div class="brand-badge">LC</div>
      <div class="brand-copy">
        <span class="brand-title">LCMS</span>
        <span class="brand-subtitle">Law Case Management System</span>
      </div>
    </div>
    <div class="navbar-right">
      <el-button type="primary" icon="el-icon-plus" size="small" class="nav-cta" @click="$router.push('/cases/new')">
        New Case
      </el-button>
      <el-badge v-if="hasNotifications" is-dot class="nav-bell">
        <el-button circle class="nav-icon" icon="el-icon-bell"></el-button>
      </el-badge>
      <el-button v-else circle class="nav-icon" icon="el-icon-bell"></el-button>
    </div>
  </header>
</template>

<script>
export default {
  name: 'Navbar',
  data() {
    return {
      hasNotifications: false
    }
  },
  created() {
    const stored = localStorage.getItem('notifications')
    if (!stored) return
    try {
      const notifications = JSON.parse(stored)
      this.hasNotifications = Array.isArray(notifications) && notifications.some(item => item && !item.read)
    } catch (err) {
      this.hasNotifications = false
    }
  }
}
</script>

<style scoped>
.lcms-navbar {
  height: 64px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 32px;
  background: rgba(255, 255, 255, 0.85);
  backdrop-filter: blur(18px);
  border-radius: 20px;
  box-shadow: 0 16px 40px rgba(15, 23, 42, 0.08);
}

.navbar-left {
  display: flex;
  align-items: center;
  gap: 14px;
}

.brand-badge {
  width: 42px;
  height: 42px;
  border-radius: 14px;
  background: linear-gradient(135deg, #6366f1, #8b5cf6);
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 700;
  color: #ffffff;
}

.brand-copy {
  display: flex;
  flex-direction: column;
  line-height: 1.2;
}

.brand-title {
  font-size: 16px;
  font-weight: 600;
  color: #1f2937;
}

.brand-subtitle {
  font-size: 12px;
  color: #6b7280;
}

.navbar-right {
  display: flex;
  align-items: center;
  gap: 16px;
}

.nav-cta {
  border-radius: 999px;
  box-shadow: 0 10px 24px rgba(79, 70, 229, 0.25);
}

.nav-icon {
  color: #1f2937;
  background: rgba(79, 70, 229, 0.15);
  border: none;
}

.nav-icon:hover {
  background: rgba(79, 70, 229, 0.3);
  color: #1f2937;
}

.nav-bell ::v-deep(.el-badge__content) {
  background: #ef4444;
}
</style>
