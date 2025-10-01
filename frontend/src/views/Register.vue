<template>
  <div class="auth-container">
    <el-card class="auth-card">
      <h2 class="title">Register</h2>
      <el-form :model="form" :rules="rules" ref="registerForm" label-position="top">
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

        <el-button type="primary" block @click="onSubmit">Register</el-button>
        <div class="link">
          <router-link to="/login">Already have an account? Login</router-link>
        </div>
      </el-form>
    </el-card>
  </div>
</template>

<script>
export default {
  name: 'Register',
  data() {
    return {
      form: {
        email: '',
        regCode: '',
        password: '',
        confirmPassword: ''
      },
      rules: {
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
            trigger: 'blur'
          }
        ]
      }
    }
  },
  methods: {
    onSubmit() {
      this.$refs.registerForm.validate(valid => {
        if (valid) {
          this.$message.success('Registration successful')
          // TODO: 调用后端 API
          this.$router.push('/login')
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
