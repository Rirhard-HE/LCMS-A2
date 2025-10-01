<template>
  <el-dialog
    title="Edit Evidence"
    :modal="false"
    :visible.sync="visible"
    width="560px"
    @close="resetForm"
  >
    <el-form :model="form" :rules="rules" ref="formRef" label-position="top">
      <el-form-item label="Title" prop="title">
        <el-input v-model="form.title" maxlength="255" placeholder="Enter evidence title" />
      </el-form-item>

      <el-form-item label="Description">
        <el-input v-model="form.description" type="textarea" :rows="3" maxlength="500" />
      </el-form-item>

      <el-form-item label="Replace File (optional)">
        <el-upload
          drag
          action=""
          :auto-upload="false"
          :file-list="fileList"
          :on-change="handleFileChange"
          :limit="1"
          :before-upload="beforeUpload"
        >
          <i class="el-icon-upload"></i>
          <div class="el-upload__text">Drop file here or <em>click to upload</em></div>
          <div class="el-upload__tip" slot="tip">Only PDF / DOC / DOCX, max 10MB</div>
        </el-upload>
      </el-form-item>
    </el-form>

    <span slot="footer">
      <el-button @click="visible=false">Cancel</el-button>
      <el-button type="primary" :loading="loading" @click="submit">Save</el-button>
    </span>
  </el-dialog>
</template>

<script>
import evidenceApi from '@/api/evidence'

export default {
  name: 'EvidenceEdit',
  data() {
    return {
      visible: false,
      loading: false,
      evidenceId: null,
      form: {
        title: '',
        description: '',
        file: null
      },
      fileList: [],
      rules: {
        title: [{ required: true, message: 'Title required', trigger: 'blur' }]
      }
    }
  },
  methods: {
    open(row) {
      this.visible = true
      this.evidenceId = row.id
      this.form.title = row.title || ''
      this.form.description = row.description || ''
      this.form.file = null
      this.fileList = []
    },
    resetForm() {
      this.$refs.formRef && this.$refs.formRef.resetFields()
      this.evidenceId = null
      this.form = { title: '', description: '', file: null }
      this.fileList = []
    },
    handleFileChange(file, fileList) {
      this.form.file = file.raw
      this.fileList = fileList
    },
    beforeUpload(file) {
      var ext = file.name.split('.').pop().toLowerCase()
      var ok = ['pdf', 'doc', 'docx'].includes(ext)
      var sizeOk = file.size / 1024 / 1024 < 10
      if (!ok) this.$message.error('Only PDF/DOC/DOCX allowed')
      if (!sizeOk) this.$message.error('File size must be < 10MB')
      return ok && sizeOk
    },
    submit() {
      this.$refs.formRef.validate(async valid => {
        if (!valid) return
        this.loading = true
        try {
          await evidenceApi.updateFile(
            this.evidenceId,
            this.form.file,               // 可为 null：只改标题/描述
            this.form.title,
            this.form.description
          )
          this.$message.success('Evidence updated')
          this.visible = false
          this.resetForm()
          this.$emit('updated')          // 通知父组件刷新列表
        } catch (e) {
          this.$message.error('Update failed')
        } finally {
          this.loading = false
        }
      })
    }
  }
}
</script>

<style scoped>
.el-upload__text em { color: #2563eb; font-style: normal; }
</style>
