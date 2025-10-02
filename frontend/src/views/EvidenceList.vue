<template>
  <div class="page">
    <header class="page_header">
      <div class="page_headline">
        <h1>Evidence Library</h1>
        <p class="page_subtitle">Find supporting files by case and stay audit-ready.</p>
      </div>
      <div class="metrics_grid">
        <div class="metric_card">
          <span class="metric_label">Total Files</span>
          <span class="metric_value">{{ summary.total }}</span>
          <span class="metric_hint">records</span>
        </div>
        <div class="metric_card">
          <span class="metric_label">Case Coverage</span>
          <span class="metric_value">{{ summary.caseCount }}</span>
          <span class="metric_hint">unique matters</span>
        </div>
        <div class="metric_card">
          <span class="metric_label">Last Upload</span>
          <span class="metric_value metric_value--text">{{ summary.recent }}</span>
          <span class="metric_hint">most recent file</span>
        </div>
      </div>
    </header>

    <section class="card filters_card">
      <div class="card_header">
        <h2>Filters</h2>
        <span class="card_hint">Search by file name or case number</span>
      </div>
      <div class="filter_grid">
        <div class="toolbar_item">
          <label class="toolbar_label">File Name</label>
          <el-input v-model="filters.keyword" placeholder="Enter file name" clearable @keyup.enter.native="fetchData" />
        </div>
        <div class="toolbar_item">
          <label class="toolbar_label">Case Number</label>
          <el-input v-model="filters.caseNumber" placeholder="e.g. LC-2025-0001" clearable @keyup.enter.native="fetchData" />
        </div>
      </div>
      <div class="filter_actions">
        <el-button type="primary" round icon="el-icon-search" @click="fetchData">Search</el-button>
        <el-button round @click="resetFilters">Clear</el-button>
      </div>
    </section>

    <section class="card action_card">
      <div class="card_header">
        <h2>Quick Actions</h2>
        <span class="card_hint">Upload or manage evidence items</span>
      </div>
      <div class="action_buttons">
        <el-button type="primary" round icon="el-icon-upload2" @click="notifyUpload">Upload Evidence</el-button>
      </div>
    </section>

    <section class="card table_card">
      <div class="card_header">
        <h2>Evidence Register</h2>
        <span class="card_hint">Showing {{ tableData.length }} of {{ tableData.length }} items</span>
      </div>
      <el-table v-loading="loading" class="data_table" :data="tableData" tooltip-effect="dark">
        <el-table-column prop="fileName" label="File Name" min-width="240">
          <template slot-scope="scope">
            <a class="link" :href="scope.row.fileUrl" target="_blank" rel="noopener">{{ scope.row.fileName }}</a>
          </template>
        </el-table-column>
        <el-table-column prop="caseNumber" label="Case" width="160">
          <template slot-scope="scope">
            <span class="link" @click="openCase(scope.row.caseId)">{{ scope.row.caseNumber }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="uploadedByLabel" label="Uploader" width="160" />
        <el-table-column prop="createdAt" label="Uploaded" width="180">
          <template slot-scope="scope">
            {{ formatDateTime(scope.row.createdAt) }}
          </template>
        </el-table-column>
        <el-table-column prop="sizeLabel" label="Size" width="120" />
      </el-table>
    </section>
  </div>
</template>

<script>
import evidenceApi from '@/api/evidences'
import { listCases } from '@/api/cases'

const dateTimeFormatter = new Intl.DateTimeFormat('en-AU', {
  year: 'numeric',
  month: 'short',
  day: 'numeric',
  hour: '2-digit',
  minute: '2-digit'
})

export default {
  name: 'EvidenceList',
  data() {
    return {
      loading: false,
      filters: { keyword: '', caseNumber: '' },
      tableData: [],
      summary: {
        total: 0,
        caseCount: 0,
        recent: '—'
      },
      caseLookupById: new Map(),
      caseLookupByNumber: new Map()
    }
  },
  created() {
    this.initialise()
  },
  methods: {
    async initialise() {
      await this.loadCasesLookup()
      this.fetchData()
    },
    async loadCasesLookup() {
      try {
        const response = await listCases({ pageNo: 1, pageSize: 500 })
        const records = response && Array.isArray(response.records) ? response.records : []
        this.caseLookupById = new Map()
        this.caseLookupByNumber = new Map()
        records.forEach(item => {
          if (item && item.id) {
            const number = item.caseNum || `Case #${item.id}`
            this.caseLookupById.set(item.id, number)
            this.caseLookupByNumber.set(number.toLowerCase(), item.id)
          }
        })
      } catch (error) {
        this.caseLookupById = new Map()
        this.caseLookupByNumber = new Map()
      }
    },
    async fetchData() {
      this.loading = true
      try {
        const result = await evidenceApi.list({ keyword: this.filters.keyword || undefined })
        const records = result && Array.isArray(result.records) ? result.records : Array.isArray(result) ? result : []
        const filtered = this.applyCaseFilter(records)
        this.tableData = filtered.map(item => this.normaliseEvidence(item))
        this.computeSummary(filtered)
      } catch (error) {
        this.$message.error('Failed to load evidence')
        this.tableData = []
        this.summary = { total: 0, caseCount: 0, recent: '—' }
      } finally {
        this.loading = false
      }
    },
    applyCaseFilter(records) {
      const input = (this.filters.caseNumber ? this.filters.caseNumber.trim() : '')
      if (!input) return records
      const caseId = this.caseLookupByNumber.get(input.toLowerCase())
      if (!caseId) {
        return records.filter(item => (item.caseId || '').toString().includes(input))
      }
      return records.filter(item => item.caseId === caseId)
    },
    normaliseEvidence(item) {
      const fileName = item.title || item.description || 'Untitled file'
      const caseNumber = this.caseLookupById.get(item.caseId) || (item.caseId ? `Case #${item.caseId}` : '?')
      const uploader = item.uploadedBy ? `Lawyer #${item.uploadedBy}` : 'Unknown'
      return {
        id: item.id,
        fileName,
        fileUrl: item.fileUrl || '#',
        caseId: item.caseId,
        caseNumber,
        uploadedByLabel: uploader,
        createdAt: item.createdAt,
        sizeLabel: this.formatSize(item.sizeBytes)
      }
    },
    computeSummary(records) {
      const total = records.length
      const caseSet = new Set(records.map(item => item.caseId))
      const recent = records.reduce((acc, item) => {
        if (!item.createdAt) return acc
        if (!acc) return item.createdAt
        return item.createdAt > acc ? item.createdAt : acc
      }, null)
      this.summary = {
        total,
        caseCount: caseSet.size,
        recent: recent ? this.formatDateTime(recent) : '?'
      }
    },
    formatDateTime(value) {
      if (!value) return '?'
      const date = new Date(value)
      if (Number.isNaN(date.getTime())) return '?'
      return dateTimeFormatter.format(date)
    },
    formatSize(size) {
      if (!size) return '?'
      const num = Number(size)
      if (!Number.isFinite(num)) return '?'
      if (num < 1024) return `${num} B`
      if (num < 1024 * 1024) return `${(num / 1024).toFixed(1)} KB`
      return `${(num / (1024 * 1024)).toFixed(1)} MB`
    },
    resetFilters() {
      this.filters = { keyword: '', caseNumber: '' }
      this.fetchData()
    },
    notifyUpload() {
      this.$message.info('Upload functionality is coming soon.')
    },
    openCase(caseId) {
      if (!caseId) return
      this.$router.push(`/cases/${caseId}`)
    }
  }
}
</script>

<style scoped>
.page {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  gap: 24px;
  padding: 32px 36px 48px;
  background: linear-gradient(180deg, #eef2ff 0%, #f8fafc 100%);
}

.page_header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 24px;
}

.page_headline h1 {
  margin: 0;
  font-size: 28px;
  font-weight: 700;
  color: #111827;
}

.page_subtitle {
  margin: 8px 0 0;
  color: #6b7280;
  font-size: 14px;
}

.card {
  background: #ffffff;
  border-radius: 24px;
  padding: 24px 28px;
  box-shadow: 0 20px 40px rgba(15, 23, 42, 0.08);
  display: flex;
  flex-direction: column;
  gap: 18px;
}

.card_header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 12px;
}

.card_header h2 {
  margin: 0;
  font-size: 18px;
  font-weight: 600;
  color: #111827;
}

.card_hint {
  font-size: 12px;
  color: #9ca3af;
}

.metrics_grid { display: flex; gap: 16px; flex-wrap: wrap; align-items: stretch; }

.metric_card { flex: 1 1 220px; min-width: 220px; background: rgba(79, 70, 229, 0.06); border: 1px solid rgba(79, 70, 229, 0.18); border-radius: 18px; padding: 16px 18px; display: flex; flex-direction: column; gap: 6px; }

.metric_label {
  font-size: 12px;
  color: #6366f1;
  text-transform: uppercase;
  letter-spacing: 0.08em;
}

.metric_value {
  font-size: 28px;
  font-weight: 700;
  color: #1f2937;
}

.metric_value--text {
  font-size: 20px;
  font-weight: 600;
}

.metric_hint {
  font-size: 12px;
  color: #6b7280;
}

.filter_grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(240px, 1fr));
  gap: 16px;
}

.toolbar_item {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.toolbar_label {
  font-size: 13px;
  font-weight: 600;
  color: #4b5563;
}

.filter_actions {
  display: flex;
  gap: 12px;
}

.action_buttons {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
}

.table_card {
  gap: 24px;
}

.data_table {
  width: 100%;
  border-radius: 18px;
  overflow: hidden;
}

::v-deep(.data_table .el-table__header-wrapper th) {
  background: rgba(99, 102, 241, 0.08);
  color: #1f2937;
  font-weight: 600;
}

::v-deep(.data_table .el-table__row:hover > td) {
  background: rgba(79, 70, 229, 0.08) !important;
}

.link {
  color: #4f46e5;
  font-weight: 600;
}

.link:hover {
  color: #312e81;
}

@media (max-width: 1024px) {
  .page {
    padding: 24px;
  }
  .page_header {
    flex-direction: column;
  }
  .filter_actions {
    flex-wrap: wrap;
  }
}
</style>


