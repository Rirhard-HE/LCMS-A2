<template>
  <div class="auth_page">
    <section class="auth_brand">
      <div class="brand_badge">LC</div>
      <h1 class="brand_title">Law Case Management</h1>
      <p class="brand_subtitle">Deliver every filing on time, coordinate teams, and stay court-ready with a single workspace.</p>
      <ul class="brand_points">
        <li>Realtime dashboards for matters and tasks</li>
        <li>Rich evidence library with secure links</li>
        <li>Calendar views for every hearing window</li>
      </ul>
    </section>

    <section class="auth_panel">
      <el-card class="auth_card" shadow="hover">
        <div class="auth_header">
          <h2>Sign in</h2>
          <p>Use your firm email to access the LCMS console.</p>
        </div>
        <el-form ref="loginForm" :model="form" :rules="rules" label-position="top" class="auth_form">
          <el-form-item label="Email" prop="email">
            <el-input v-model="form.email" placeholder="name@firm.com" clearable />
          </el-form-item>
          <el-form-item label="Password" prop="password">
            <el-input v-model="form.password" type="password" placeholder="Enter your password" show-password />
          </el-form-item>
          <div class="auth_actions">
            <el-button type="primary" round @click="onSubmit">Sign in</el-button>
          </div>
          <div class="auth_footer">
            <span>Need an account?</span>
            <router-link to="/register">Request access</router-link>
          </div>
        </el-form>
      </el-card>
    </section>
  </div>
</template>

<script>
import { login } from '../api/auth'

export default {
  name: 'Login',
  data() {
    return {
      form: { email: '', password: '' },
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
      this.$refs.loginForm.validate(async valid => {
        if (!valid) return
        try {
          const { data } = await login(this.form)
          localStorage.setItem('jwt', data.token)
          this.$message.success('Login successful')
          this.$router.push('/dashboard')
        } catch {
          this.$message.error('Login failed')
        }
      })
    }
  }
}
</script>

<style scoped>
.auth_page {
  min-height: 100vh;
  display: grid;
  grid-template-columns: minmax(320px, 1fr) minmax(360px, 480px);
  background: linear-gradient(135deg, #eef2ff 0%, #e0f2fe 100%);
}

.auth_brand {
  padding: 64px 72px;
  display: flex;
  flex-direction: column;
  justify-content: center;
  gap: 24px;
  background: linear-gradient(180deg, rgba(79, 70, 229, 0.18) 0%, rgba(14, 165, 233, 0.18) 100%);
  color: #1f2937;
}

.brand_badge {
  width: 60px;
  height: 60px;
  border-radius: 18px;
  background: linear-gradient(135deg, #6366f1, #8b5cf6);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  font-weight: 700;
  color: #fff;
}

.brand_title {
  margin: 0;
  font-size: 32px;
  font-weight: 700;
}

.brand_subtitle {
  margin: 0;
  font-size: 16px;
  line-height: 1.5;
  color: #374151;
}

.brand_points {
  margin: 0;
  padding-left: 20px;
  display: flex;
  flex-direction: column;
  gap: 8px;
  color: #1f2937;
  font-size: 14px;
}

.auth_panel {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 64px 40px;
}

.auth_card {
  width: 100%;
  border-radius: 24px;
  padding: 32px 36px;
  box-shadow: 0 28px 60px rgba(15, 23, 42, 0.16);
  border: none;
}

.auth_header h2 {
  margin: 0;
  font-size: 26px;
  font-weight: 700;
  color: #111827;
}

.auth_header p {
  margin: 6px 0 0;
  color: #6b7280;
  font-size: 14px;
}

.auth_form {
  margin-top: 24px;
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.auth_actions {
  margin-top: 8px;
}

.auth_actions .el-button {
  width: 100%;
  font-weight: 600;
}

.auth_footer {
  display: flex;
  justify-content: center;
  gap: 6px;
  font-size: 13px;
  color: #6b7280;
}

.auth_footer a {
  color: #4f46e5;
  font-weight: 600;
}

@media (max-width: 960px) {
  .auth_page {
    grid-template-columns: 1fr;
  }
  .auth_brand {
    padding: 48px 32px;
    text-align: center;
    align-items: center;
  }
  .brand_points {
    align-items: flex-start;
  }
  .auth_panel {
    padding: 48px 24px 64px;
  }
}
</style>
