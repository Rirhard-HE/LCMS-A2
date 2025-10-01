<template>
  <div class="welcome-container">
    <el-card class="welcome-card">
      <h1 class="title">Welcome to use Law Case Management System</h1>
      <el-button type="primary" size="large" @click="$router.push('/login')">
        Go to Login
      </el-button>
    </el-card>
  </div>
</template>

<script>
export default {
  name: 'Welcome'
}
</script>

<style scoped>
.welcome-container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
  background: linear-gradient(135deg, #6366f1, #10b981);
}

.welcome-card {
  width: 600px;
  padding: 40px;
  border-radius: 15px;
  text-align: center;
  background: rgba(255, 255, 255, 0.3);
  backdrop-filter: blur(5px);
}

.title {
  font-size: 26px;
  font-weight: bold;
  margin-bottom: 30px;
}
</style>
