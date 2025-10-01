<template>
  <el-dialog
    title="Add Evidence"
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

      <el-form-item label="File" prop="file">
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
      <el-button type="primary" :loading="loading" @click="submit">Upload</el-button>
    </span>
  </el-dialog>
</template>

<script>
import evidenceApi from '@/api/evidence'

export default {
  name: 'EvidenceUpload',
  props: {
    caseId: { type: Number, required: true } // 绑定到具体 Case
  },
  data() {
    return {
      visible: false,
      loading: false,
      form: {
        title: '',
        description: '',
        file: null
      },
      fileList: [],
      rules: {
        title: [{ required: true, message: 'Title required', trigger: 'blur' }],
        file: [{ required: true, message: 'File required', trigger: 'change' }]
      }
    }
  },
  methods: {
    open() {
      this.visible = true
    },
    resetForm() {
      this.$refs.formRef && this.$refs.formRef.resetFields()
      this.fileList = []
      this.form = { title: '', description: '', file: null }
    },
    handleFileChange(file, fileList) {
      this.form.file = file.raw
      this.fileList = fileList
    },
    beforeUpload(file) {
      const ext = file.name.split('.').pop().toLowerCase()
      const ok = ['pdf', 'doc', 'docx'].includes(ext)
      const sizeOk = file.size / 1024 / 1024 < 10
      if (!ok) this.$message.error('Only PDF/DOC/DOCX allowed')
      if (!sizeOk) this.$message.error('File size must be < 10MB')
      return ok && sizeOk
    },
    submit() {
      this.$refs.formRef.validate(async valid => {
        if (!valid) return
        this.loading = true
        try {
          await evidenceApi.upload(
            this.caseId,
            this.form.file,
            this.form.title,
            this.form.description
          )
          this.$message.success('Evidence uploaded successfully')
          this.visible = false
          this.resetForm()
          this.$emit('uploaded') // 通知父组件刷新
        } catch (e) {
          this.$message.error('Upload failed')
        } finally {
          this.loading = false
        }
      })
    }
  }
}
</script>

<style scoped>
.el-upload__text em {
  color: #2563eb;
  font-style: normal;
}
</style>
