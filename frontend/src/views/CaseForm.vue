<template>
  <div class="case_form" v-loading="loading">
    <el-card class="form_card" shadow="hover">
      <div class="form_header">
        <h1>Create Case</h1>
        <p>Provide the core details for the new matter.</p>
      </div>
      <el-form ref="caseForm" :model="form" :rules="rules" label-position="top" class="form_body">
        <el-form-item label="Case Number" prop="caseNum">
          <el-input v-model="form.caseNum" placeholder="e.g. LC-2025-0007" clearable />
        </el-form-item>
        <el-form-item label="Title" prop="title">
          <el-input v-model="form.title" placeholder="Enter case title" clearable />
        </el-form-item>
        <el-form-item label="Description">
          <el-input
            v-model="form.description"
            type="textarea"
            :rows="4"
            placeholder="Background, goals or next steps"
          />
        </el-form-item>
        <el-row :gutter="16">
          <el-col :xs="24" :sm="12">
            <el-form-item label="Status" prop="status">
              <el-select v-model="form.status">
                <el-option label="Active" value="active" />
                <el-option label="Draft" value="draft" />
                <el-option label="Closed" value="closed" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :xs="24" :sm="12">
            <el-form-item label="Hearing Date">
              <el-date-picker
                v-model="form.hearingAt"
                type="datetime"
                value-format="yyyy-MM-dd'T'HH:mm:ss"
                placeholder="Select hearing date"
                format="dd MMM yyyy, HH:mm"
                clearable
              />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="Categories">
          <el-select v-model="form.categories" multiple filterable placeholder="Assign categories">
            <el-option v-for="cat in categoryOptions" :key="cat.value" :label="cat.label" :value="cat.value" />
          </el-select>
        </el-form-item>
        <div class="form_actions">
          <el-button @click="cancel">Cancel</el-button>
          <el-button type="primary" @click="submit">Create Case</el-button>
        </div>
      </el-form>
    </el-card>
  </div>
</template>

<script>
import { createCase } from '@/api/cases'
import categoriesApi from '@/api/categories'

export default {
  name: 'CaseForm',
  data() {
    return {
      loading: false,
      categoryOptions: [],
      form: {
        caseNum: '',
        title: '',
        description: '',
        status: 'active',
        hearingAt: '',
        categories: []
      },
      rules: {
        caseNum: [{ required: true, message: 'Case number is required', trigger: 'blur' }],
        title: [{ required: true, message: 'Title is required', trigger: 'blur' }],
        status: [{ required: true, message: 'Status is required', trigger: 'change' }]
      }
    }
  },
  created() {
    this.loadCategories()
  },
  methods: {
    async loadCategories() {
      try {
        const list = await categoriesApi.list()
        const records = list && Array.isArray(list.records) ? list.records : (Array.isArray(list) ? list : [])
        this.categoryOptions = records.map(item => ({ label: item.name, value: item.id }))
      } catch (error) {
        this.categoryOptions = []
      }
    },
    cancel() {
      this.$router.back()
    },
    submit() {
      this.$refs.caseForm.validate(async valid => {
        if (!valid) return
        this.loading = true
        try {
          const payload = {
            caseNum: this.form.caseNum,
            title: this.form.title,
            description: this.form.description,
            status: this.form.status,
            hearingAt: this.form.hearingAt || null
          }
          const categoryIds = this.form.categories
          await createCase(payload, categoryIds)
          this.$message.success('Case created')
          this.$router.push('/cases')
        } catch (error) {
          this.$message.error('Failed to create case')
        } finally {
          this.loading = false
        }
      })
    }
  }
}
</script>

<style scoped>
.case_form {
  min-height: 100vh;
  display: flex;
  justify-content: center;
  align-items: flex-start;
  padding: 48px 24px 72px;
  background: linear-gradient(135deg, #eef2ff 0%, #e0f2fe 100%);
}

.form_card {
  width: 100%;
  max-width: 720px;
  border-radius: 28px;
  box-shadow: 0 28px 60px rgba(15, 23, 42, 0.16);
  border: none;
  padding: 32px 36px;
}

.form_header h1 {
  margin: 0;
  font-size: 26px;
  font-weight: 700;
  color: #111827;
}

.form_header p {
  margin: 8px 0 0;
  color: #6b7280;
  font-size: 14px;
}

.form_body {
  margin-top: 24px;
}

.form_actions {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
}

@media (max-width: 640px) {
  .form_card {
    padding: 24px;
  }
}
</style>


