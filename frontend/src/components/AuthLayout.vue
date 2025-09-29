<template>
  <div class="auth-wrap">
    <router-view />
  </div>
</template>

<script>
export default { name: 'AuthLayout' }
</script>

<style scoped>
.auth-wrap{
  min-height:100vh;
  display:flex; align-items:center; justify-content:center;
  background: linear-gradient(135deg, #6366f1, #10b981);
}
</style>
