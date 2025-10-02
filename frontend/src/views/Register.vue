<template>
  <div class="auth_page">
    <section class="auth_brand">
      <div class="brand_badge">LC</div>
      <h1 class="brand_title">Join the Workspace</h1>
      <p class="brand_subtitle">Invite teammates to collaborate on cases, evidence, and hearings in a shared control centre.</p>
      <ul class="brand_points">
        <li>Role-based access to matters</li>
        <li>Audit trails for every update</li>
        <li>Guided onboarding in minutes</li>
      </ul>
    </section>

    <section class="auth_panel">
      <el-card class="auth_card" shadow="hover">
        <div class="auth_header">
          <h2>Create account</h2>
          <p>Provide your invitation details to get started.</p>
        </div>
        <el-form ref="registerForm" :model="form" :rules="rules" label-position="top" class="auth_form">
          <el-form-item label="Email" prop="email">
            <el-input v-model="form.email" placeholder="name@firm.com" clearable />
          </el-form-item>
          <el-form-item label="Full Name" prop="fullName">
            <el-input v-model="form.fullName" placeholder="Enter your full name" clearable />
          </el-form-item>
          <el-form-item label="Password" prop="password">
            <el-input v-model="form.password" type="password" placeholder="Create a password" show-password />
          </el-form-item>
          <el-form-item label="Confirm Password" prop="confirmPassword">
            <el-input v-model="form.confirmPassword" type="password" placeholder="Repeat password" show-password />
          </el-form-item>
          <div class="auth_actions">
            <el-button type="primary" round @click="onSubmit">Register</el-button>
          </div>
          <div class="auth_footer">
            <span>Already set up?</span>
            <router-link to="/login">Return to sign in</router-link>
          </div>
        </el-form>
      </el-card>
    </section>
  </div>
</template>

<script>
import { register as registerUser } from '@/api/auth'

export default {
  name: 'Register',
  data() {
    return {
      form: {
        email: '',
        fullName: '',
        password: '',
        confirmPassword: ''
      },
      rules: {
        email: [
          { required: true, message: 'Email is required', trigger: 'blur' },
          { type: 'email', message: 'Invalid email', trigger: ['blur', 'change'] }
        ],
        fullName: [{ required: true, message: 'Full name is required', trigger: 'blur' }],
        password: [{ required: true, message: 'Password is required', trigger: 'blur' }],
        confirmPassword: [
          { required: true, message: 'Please confirm your password', trigger: 'blur' },
          {
            validator: (rule, value, callback) => {
              if (value !== this.form.password) {
                callback(new Error('Passwords do not match'))
              } else {
                callback()
              }
            },
            trigger: 'blur'
          }
        ]
      }
    }
  },
  methods: {
    onSubmit() {
      this.$refs.registerForm.validate(async valid => {
        if (!valid) return
        try {
          await registerUser({
            email: this.form.email,
            password: this.form.password,
            fullName: this.form.fullName
          })
          this.$message.success('Registration successful')
          this.$router.push('/login')
        } catch (error) {
          this.$message.error('Registration failed')
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
  background: linear-gradient(180deg, rgba(56, 189, 248, 0.18) 0%, rgba(99, 102, 241, 0.18) 100%);
  color: #1f2937;
}

.brand_badge {
  width: 60px;
  height: 60px;
  border-radius: 18px;
  background: linear-gradient(135deg, #0ea5e9, #6366f1);
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
  color: #0ea5e9;
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
