<template>
  <div class="auth-container">
    <el-card class="auth-card">
      <h2 class="title">Register</h2>

      <el-form :model="form" :rules="rules" ref="registerForm" label-position="top">
        <el-form-item label="Full Name" prop="fullName">
          <el-input v-model="form.fullName" placeholder="Enter your full name"></el-input>
        </el-form-item>

        <el-form-item label="Email" prop="email">
          <el-input v-model="form.email" placeholder="Enter your email"></el-input>
        </el-form-item>

        <el-form-item label="Registration Code" prop="regCode">
          <el-input v-model="form.regCode" placeholder="Enter registration code"></el-input>
        </el-form-item>

        <el-form-item label="Password" prop="password">
          <el-input v-model="form.password" type="password" placeholder="Enter password"></el-input>
        </el-form-item>

        <el-form-item label="Confirm Password" prop="confirmPassword">
          <el-input v-model="form.confirmPassword" type="password" placeholder="Confirm password"></el-input>
        </el-form-item>

        <el-button type="primary" :loading="submitting" style="width:100%" @click="onSubmit">
          Register
        </el-button>
        <div class="link">
          <router-link to="/login">Already have an account? Login</router-link>
        </div>
      </el-form>
    </el-card>
  </div>
</template>

<script>
import { register } from '@/api/auth'

const REG_CODE = 'IfN636Te5tC0de'

export default {
  name: 'Register',
  data() {
    return {
      submitting: false,
      form: {
        fullName: '',
        email: '',
        regCode: '',
        password: '',
        confirmPassword: ''
      },
      rules: {
        fullName: [{ required: true, message: 'Full name is required', trigger: 'blur' }],
        email: [
          { required: true, message: 'Email is required', trigger: 'blur' },
          { type: 'email', message: 'Invalid email', trigger: ['blur', 'change'] }
        ],
        regCode: [{ required: true, message: 'Registration code is required', trigger: 'blur' }],
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
            trigger: ['blur', 'change']
          }
        ]
      }
    }
  },
  methods: {
    async onSubmit() {
      this.$refs.registerForm.validate(async (valid) => {
        if (!valid) return
        if (this.form.regCode !== REG_CODE) {
          this.$message.error('Invalid registration code')
          return
        }
        this.submitting = true
        try {
          const res = await register({
            fullName: this.form.fullName,
            email: this.form.email,
            password: this.form.password
          })
          if (res && (res.status === 200 || res.code === 200)) {
            this.$message.success('Registration successful')
            this.$router.push('/login')
          } else {
            this.$message.error('Registration failed')
          }
        } catch (e) {
          this.$message.error('Network or server error')
        } finally {
          this.submitting = false
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
