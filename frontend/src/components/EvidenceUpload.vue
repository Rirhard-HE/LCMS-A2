<template>
  <el-dialog title="Upload Evidence" :modal=false :visible.sync="visible" width="480px">
    <el-form :model="form" label-width="100px">
      <el-form-item label="Case">
        <el-select v-model="form.caseId" filterable placeholder="Select Case No." style="width:100%">
          <el-option
            v-for="c in cases"
            :key="c.id"
            :label="c.caseNum"
            :value="c.id"
          />
        </el-select>
      </el-form-item>

      <el-form-item label="Title">
        <el-input v-model="form.title" maxlength="100" placeholder="Evidence title"/>
      </el-form-item>

      <el-form-item label="Description">
        <el-input type="textarea" v-model="form.description" maxlength="255" placeholder="Evidence description"/>
      </el-form-item>

      <el-form-item label="File">
        <el-upload
          drag
          action=""
          :auto-upload="false"
          :file-list="fileList"
          :on-change="onFileChange"
        >
          <i class="el-icon-upload"></i>
          <div class="el-upload__text">Drop file here or <em>click to upload</em></div>
          <div class="el-upload__tip">Only PDF / DOC / DOCX</div>
        </el-upload>
      </el-form-item>
    </el-form>

    <div slot="footer" class="dialog-footer">
      <el-button @click="visible=false">Cancel</el-button>
      <el-button type="primary" :loading="loading" @click="submit">Upload</el-button>
    </div>
  </el-dialog>
</template>

<script>
import evidenceApi from '@/api/evidences'
import { listCases } from '@/api/cases'

export default {
  name: 'EvidenceUpload',
  data() {
    return {
      visible: false,
      loading: false,
      cases: [],
      form: { caseId: null, title: '', description: '' },
      fileList: [],
      file: null
    }
  },
  methods: {
    async open() {
      this.visible = true
      this.form = { caseId: null, title: '', description: '' }
      this.fileList = []
      this.file = null
      await this.fetchCases()
    },
    async fetchCases() {
      try {
        const res = await listCases({ pageNo: 1, pageSize: 50 })
        let arr = res
        if (arr && arr.records) arr = arr.records 
        this.cases = arr
      } catch (e) {
        this.$message.error('Failed to load cases')
      }
    },
    onFileChange(file, fileList) {
      this.file = file.raw
      this.fileList = fileList.slice(-1)
    },
    async submit() {
      if (!this.form.caseId) {
        this.$message.warning('Please select a case')
        return
      }
      if (!this.file) {
        this.$message.warning('Please select a file')
        return
      }
      this.loading = true
      try {
        await evidenceApi.upload(this.form.caseId, this.file, this.form.title, this.form.description)
        this.$message.success('Uploaded successfully')
        this.visible = false
        this.$emit('uploaded')
      } catch (e) {
        this.$message.error('Upload failed')
      } finally {
        this.loading = false
      }
    }
  }
}
</script>
