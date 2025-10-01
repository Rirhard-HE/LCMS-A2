<template>
  <header class="lcms-navbar">
    <div class="nav-left">
      <span class="app-title">Law Case Management System</span>
    </div>
    <div class="nav-right">
      <el-button type="text" icon="el-icon-bell"></el-button>
       <div class="nav-right">
      <el-button type="danger" round @click="logout">
        Logout
      </el-button>
    </div>
    </div>
  </header>
</template>

<script>
export default { name: 'Navbar',
  methods: {
    logout() {
      localStorage.removeItem('jwt')
      this.$message.success('Logged out')
      this.$router.push('/login')
    }
  }
}
</script>

<style scoped>

.lcms-navbar {
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 16px;

  background: rgba(90, 171, 247, 0.9);
  box-shadow: 0 2px 4px rgba(0,0,0,0.06);
}
.font-size {
  color: #fff;
  font-weight: bold;
  font-family: Arial, sans-serif;
}
.el-dropdown-link {
  color: #fff !important;
  font-weight: bold;  
  font-family: Arial, sans-serif; 
  display: flex;
  align-items: center;
  gap: 4px;  
}

.el-dropdown-link i {
  color: #fff !important;
  font-weight: bold;
}
.app-title { font-size:larger; font-weight: bold; color: #374151;   font-family: Arial, sans-serif;
}
.nav-right { display: flex; align-items: center; gap: 12px; }
</style>
