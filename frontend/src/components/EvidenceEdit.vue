<template>
  <el-dialog title="Edit Evidence" :modal=false :visible.sync="visible" width="480px" @close="reset">
    <el-form :model="form" label-width="100px">
      <el-form-item label="Title">
        <el-input v-model="form.title" maxlength="100" />
      </el-form-item>

      <el-form-item label="Description">
        <el-input type="textarea" v-model="form.description" maxlength="255" />
      </el-form-item>

      <el-form-item label="Replace File">
        <el-upload
          drag
          action=""
          :auto-upload="false"
          :file-list="fileList"
          :on-change="onFileChange"
          :on-remove="onFileRemove"
          accept=".pdf,.doc,.docx"
        >
          <i class="el-icon-upload"></i>
          <div class="el-upload__text">Drop file here or <em>click to select</em></div>
          <div class="el-upload__tip">Only PDF / DOC / DOCX</div>
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
import evidenceApi from '@/api/evidences'

export default {
  name: 'EvidenceEdit',
  data() {
    return {
      visible: false,
      loading: false,
      form: { id: null, title: '', description: '' },
      fileList: [],
      file: null
    }
  },
  methods: {
    open(payload) {
      this.reset()
      this.form.id = payload.id
      this.form.title = payload.title || ''
      this.form.description = payload.description || ''
      this.visible = true
    },
    onFileChange(file, fileList) {
      this.file = file.raw
      this.fileList = fileList.slice(-1)
    },
    onFileRemove() {
      this.file = null
      this.fileList = []
    },
    async submit() {
      if (!this.form.id) {
        this.$message.warning('Invalid evidence id')
        return
      }
      this.loading = true
      try {
        await evidenceApi.updateFile(this.form.id, this.file, this.form.title, this.form.description)
        this.$message.success('Updated')
        this.visible = false
        this.$emit('updated')
      } catch (e) {
        this.$message.error('Update failed')
      } finally {
        this.loading = false
      }
    },
    reset() {
      this.loading = false
      this.form = { id: null, title: '', description: '' }
      this.fileList = []
      this.file = null
    }
  }
}
</script>

<style scoped>
.el-upload__text em { color: #2563eb; font-style: normal; }
</style>
