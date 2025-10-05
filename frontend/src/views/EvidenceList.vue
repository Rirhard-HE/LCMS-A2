<template>
  <div class="list_container">
    <!-- Cancel Filters -->
    <div class="toolbar">
      <!-- <div class="toolbar_item">
        <label class="toolbar_label">Keyword:</label>
        <el-input v-model="filters.keyword" placeholder="Search title/file/desc" clearable maxlength="50"/>
      </div> -->
      <div class="toolbar_item">
        <label class="toolbar_label">Case No.:</label>
        <el-input v-model="filters.caseNo" placeholder="Enter case no." clearable maxlength="50"/>
      </div>
      <div class="toolbar_actions">
        <el-button type="success" round :loading="loading" @click="fetchData">Search</el-button>
        <el-button round @click="resetFilters">Reset</el-button>
      </div>
    </div>

    <div class="divider"/>

    <!-- Page actions -->
    <div class="actions">
      <el-button type="primary" round icon="el-icon-upload2" @click="openUpload">
        Upload Evidence
      </el-button>
    </div>

    <!-- Table -->
    <el-table :data="pagedRows" class="table" height="calc(100vh - 360px)" v-loading="loading">
      <el-table-column type="index" label="#" width="60" align="center" fixed="left"/>
      <el-table-column label="File Name" prop="fileName" min-width="260" fixed="left">
        <template slot-scope="scope">
          <a class="link" :href="scope.row.uri" target="_blank">{{ scope.row.fileName }}</a>
        </template>
      </el-table-column>
      <el-table-column label="Case No." prop="caseNo" width="160"/>      
      <el-table-column label="Description." prop="description" width="160"/>
      <el-table-column label="Uploaded At" prop="uploadedAt" width="180"/>
      <!-- <el-table-column label="Uploader" prop="uploader" width="120"/> -->
      <el-table-column label="Actions" min-width="200" fixed="right">
        <template slot-scope="scope">
          <el-button size="mini" round @click="openEdit(scope.row)">Edit</el-button>
          <el-button size="mini" type="primary" round @click="download(scope.row)">Download</el-button>
          <el-button size="mini" type="danger" round @click="remove(scope.row)">Delete</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- Pagination -->
    <div class="pagination">
      <el-pagination
        :current-page="pagination.page"
        :page-sizes="[10, 20, 50, 100]"
        :page-size="pagination.pageSize"
        layout="total, sizes, prev, pager, next, jumper"
        :total="pagination.total"
        @size-change="onPageSizeChange"
        @current-change="onPageChange"
      />
    </div>

    <!-- Dialogs -->
    <EvidenceUpload ref="uploader" @uploaded="fetchData"/>
    <EvidenceEdit ref="editor" @updated="fetchData"/>
  </div>
</template>

<script>
import evidenceApi from '@/api/evidences'
import EvidenceUpload from '@/components/EvidenceUpload.vue'
import EvidenceEdit from '@/components/EvidenceEdit.vue'

export default {
  name: 'EvidenceList',
  components: { EvidenceUpload, EvidenceEdit },
  data() {
    return {
      loading: false,
      filters: { keyword: '', caseNo: '' },
      rawRows: [],
      pagination: { page: 1, pageSize: 10, total: 0 }
    }
  },
  computed: {
    filteredRows() {
      let rows = this.rawRows
      if (this.filters.caseNo) {
        const cn = this.filters.caseNo.toLowerCase()
        rows = rows.filter(r => (r.caseNo || '').toLowerCase().includes(cn))
      }
      return rows
    },
    pagedRows() {
      const start = (this.pagination.page - 1) * this.pagination.pageSize
      return this.filteredRows.slice(start, start + this.pagination.pageSize)
    }
  },
  created() { this.fetchData() },
  methods: {
    async fetchData() {
      this.loading = true
      try {
        const params = {}
        if (this.filters.keyword) params.keyword = this.filters.keyword
        const res = await evidenceApi.list(params)
        const list = res
        if(list){
        this.rawRows = list.map(this.mapRow)
        this.pagination.total = this.rawRows.length
      }else{
                this.$message.error('No evidences to show, you need to create' )

      }
      } catch (e) {
        this.$message.error('Failed to load evidence')
      } finally {
        this.loading = false
      }
    },
    mapRow(e) {
      const fileName = e.fileUrl ? e.fileUrl.split('/').pop() : (e.title || '')
      const caseNo = e.caseNum || (e.caseId ? String(e.caseId) : '—')
      return {
        id: e.id,
        description:e.description,
        fileName,
        uri: "http://localhost:8081"+e.fileUrl,
        caseNo,
        uploadedAt: e.createdAt ? e.createdAt.replace('T', ' ').slice(0, 16) : '—',
        uploader: e.uploadedBy || '—',
        _raw: { id: e.id, title: e.title || '', description: e.description || '' }
      }
    },
    resetFilters() {
      this.filters = { keyword: '', caseNo: '' }
      this.pagination.page = 1
      this.fetchData()
    },
    onPageSizeChange(s) { this.pagination.pageSize = s; this.pagination.page = 1 },
    onPageChange(p) { this.pagination.page = p },
    download(r) { r.uri ? window.open(r.uri, '_blank') : this.$message.warning('No file URL') },
    async remove(r) {
      
        await this.$confirm(`Delete "${r.fileName}" ?`, 'Confirm', { type: 'warning' })
        await evidenceApi.remove(r.id)
        this.$message.success('Deleted')
        this.fetchData()
    },
    openUpload() { this.$refs.uploader.open() },
    openEdit(r) { this.$refs.editor.open(r._raw) }
  }
}
</script>

<style scoped>
.list_container { padding: 16px 2vw; }
.toolbar { display:flex; gap:16px; flex-wrap: wrap; align-items:flex-end; }
.toolbar_item { display:flex; flex-direction:column; gap:6px; }
.toolbar_label { font-size:12px; color:#64748b; }
.toolbar_actions { display:flex; gap:8px; }
.divider { height:1px; background:#eee; margin:12px 0; }
.actions { margin-bottom:12px; }
.table { width:100%; }
.link { color:#3b82f6; cursor:pointer; text-decoration: underline; }
.pagination { display:flex; justify-content:flex-end; margin-top:12px; }
</style>
