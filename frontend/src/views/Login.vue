<template>
  <div class="auth-container">
    <el-card class="auth-card">
      <h2 class="title">Login</h2>
      <el-form :model="form" :rules="rules" ref="loginForm" label-position="top">
        <el-form-item label="Email" prop="email">
          <el-input v-model="form.email" placeholder="Enter your email"></el-input>
        </el-form-item>

        <el-form-item label="Password" prop="password">
          <el-input v-model="form.password" type="password" placeholder="Enter your password"></el-input>
        </el-form-item>

        <el-button type="primary" block @click="onSubmit">Login</el-button>
        <div class="link">
          <router-link to="/register">Don’t have an account? Register</router-link>
        </div>
      </el-form>
    </el-card>
  </div>
</template>

<script>
export default {
  name: 'Login',
  data() {
    return {
      form: {
        email: '',
        password: ''
      },
      rules: {
        email: [
          { required: true, message: 'Email is required', trigger: 'blur' },
          { type: 'email', message: 'Invalid email', trigger: ['blur', 'change'] }
        ],
        password: [{ required: true, message: 'Password is required', trigger: 'blur' }]
      }
    }
  },
  methods: {
    onSubmit() {
      this.$refs.loginForm.validate(valid => {
        if (valid) {
          this.$message.success('Login successful')
          // TODO: 调用后端 API
          this.$router.push('/')
        }
      })
    }
  }
}
</script>

<style scoped>
.auth-container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
  background: linear-gradient(135deg, #6366f1, #10b981);
}
.auth-card {
  width: 400px;
  padding: 20px;
  border-radius: 15px;
}
.title {
  text-align: center;
  margin-bottom: 20px;
}
.link {
  margin-top: 10px;
  text-align: center;
}
</style>
