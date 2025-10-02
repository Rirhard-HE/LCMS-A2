<template>
  <div class="caseDetail_container">
    <!-- Header -->
    <div class="header">
      <div class="titleblock">
        <el-button type="primary" round="50" @click="gotoCases()" style="margin-bottom: 10px; margin-left: -4px;">back to Cases</el-button>
        <div class="case_title">{{ caseData.title || 'N/A' }}</div>
        <div class="case_no">Case No. {{ caseData.caseNumber || 'N/A' }}</div>
      </div>
      <div class="actions">
        <el-button :type="caseData.status==='active' ? 'info' : 'success'" round @click="toggleStatus">
          {{ caseData.status==='active' ? 'Close' : 'Activate' }}
        </el-button>
      </div>
    </div>

    <div class="detail_cards">
      <!-- Case Summary -->
      <el-card class="info_card" shadow="hover" v-loading="loadingCase">
        <div class="info_grid">
          <div class="info_item">
            <div class="label">Category</div>
            <div class="value">{{ caseData.category || 'N/A' }}</div>
          </div>
          <div class="info_item">
            <div class="label">Status</div>
            <div class="value status" :class="caseData.status">{{ ucFirst(caseData.status || 'N/A') }}</div>
          </div>
          <div class="info_item">
            <div class="label">Owner</div>
            <div class="value">{{ caseData.owner || 'N/A' }}</div>
          </div>
          <div class="info_item">
            <div class="label">Created At</div>
            <div class="value">{{ caseData.createdAt || 'N/A' }}</div>
          </div>
          <div class="info_item">
            <div class="label">Last Updated</div>
            <div class="value">{{ caseData.updatedAt || 'N/A' }}</div>
          </div>
        </div>
      </el-card>

      <!-- Hearings -->
      <el-card class="hearing_section" shadow="hover" v-if="caseData.hearings && caseData.hearings.length">
        <div class="tab_header">
          <div class="tab_title">Hearings</div>
        </div>
        <div class="hearing_list">
          <el-card
            v-for="h in caseData.hearings"
            :key="h.id"
            class="hearing_card"
            shadow="hover"
          >
            <div class="hearing_row">
              <div class="hearing_time">
                Hearing Time:
                <span class="time_value">{{ h.date }} {{ h.time }}</span>
              </div>
            </div>
            <div class="hearing_case">
              <span class="link" @click="goCase()">{{ caseData.title }}</span>
              <span class="case_no_inline"> ({{ caseData.caseNumber }})</span>
            </div>
          </el-card>
        </div>
      </el-card>

      <!-- Evidence -->
      <el-card class="tabs_card" shadow="never">
        <div class="tab_header">
          <div class="tab_title">Evidence List</div>
          <el-button type="primary" round icon="el-icon-folder-add" @click="goEvidence()">Go to evidence</el-button>
        </div>
        <el-table :data="evidenceRows" class="table" height="380" border v-loading="loadingEv">
          <el-table-column type="index" width="60" align="center" />
          <el-table-column label="File Name" prop="filename" min-width="240" />
          <el-table-column label="Uploaded By" prop="uploadedBy" width="160" />
          <el-table-column label="Uploaded At" prop="uploadedAt" width="180" />
        </el-table>
      </el-card>
    </div>
  </div>
</template>

<script>
const sampleCase = {
  id: 101,
  caseNumber: 'CASE-2025-072',
  title: 'Anderson vs City Council',
  category: 'Zoning Dispute',
  categories: ['Zoning', 'Municipal Law'],
  status: 'active',
  owner: 'Alex Morgan',
  createdAt: '2025-07-12 09:30',
  updatedAt: '2025-09-04 14:40',
  hearingAt: '2025-10-03T10:00:00'
}

const sampleEvidence = [
  {
    id: 1,
    fileUrl: 'https://example.com/docs/site-inspection-report.pdf',
    title: 'Site Inspection Report.pdf',
    uploadedBy: 'Alex Morgan',
    createdAt: '2025-08-15T11:20:00'
  },
  {
    id: 2,
    fileUrl: 'https://example.com/docs/traffic-analysis.xlsx',
    title: 'Traffic Analysis.xlsx',
    uploadedBy: 'Jamie Patel',
    createdAt: '2025-08-20T09:05:00'
  },
  {
    id: 3,
    fileUrl: 'https://example.com/docs/public-feedback.pdf',
    title: 'Public Feedback Summary.pdf',
    uploadedBy: 'Case Assistant',
    createdAt: '2025-08-22T16:45:00'
  }
]

export default {
  name: 'CaseDetail',
  data() {
    return {
      loadingCase: false,
      loadingEv: false,
      caseId: this.$route.params.id || 'CASE-2024-072',
      caseData: {
        id: null,
        caseNumber: '',
        title: '',
        category: '',
        status: 'active',
        owner: '',
        createdAt: '',
        updatedAt: '',
        hearings: []
      },
      evidenceRows: []
    }
  },
  created() {
    this.loadCase()
    this.loadEvidence()
  },
  methods: {
    async loadCase() {
      this.loadingCase = true
      try {
        const data = sampleCase
        const status = data.status === 'CLOSED' ? 'closed' : data.status === 'DRAFT' ? 'draft' : 'active'
        const categoryText = Array.isArray(data.categories)
          ? data.categories.map(c => (typeof c === 'string' ? c : c.name)).join(', ')
          : data.category || 'N/A'

        this.caseData = {
          id: data.id,
          caseNumber: data.caseNumber || 'N/A',
          title: data.title || '',
          category: categoryText,
          status,
          owner: data.owner || 'N/A',
          createdAt: data.createdAt || 'N/A',
          updatedAt: data.updatedAt || 'N/A',
          hearings: this.buildHearings(data.hearingAt)
        }
      } finally {
        this.loadingCase = false
      }
    },

    async loadEvidence() {
      this.loadingEv = true
      try {
        const list = sampleEvidence
        this.evidenceRows = list.map(e => ({
          id: e.id,
          filename: this.filenameFromUrl(e.fileUrl) || e.title || 'N/A',
          uploadedBy: e.uploadedBy ? String(e.uploadedBy) : 'N/A',
          uploadedAt: e.createdAt ? this.formatDate(e.createdAt) : 'N/A'
        }))
      } finally {
        this.loadingEv = false
      }
    },

    buildHearings(hearingAt) {
      if (!hearingAt) return []
      const s = String(hearingAt)
      const t = s.indexOf('T')
      const date = s.length >= 10 ? s.slice(0, 10) : s
      let time = ''
      if (t > 0) time = s.slice(t + 1, t + 6)
      else if (s.length >= 16) time = s.slice(11, 16)
      return [{ id: 1, date, time }]
    },

    toggleStatus() {
      this.caseData.status = this.caseData.status === 'active' ? 'closed' : 'active'
      this.$message.success('Status updated (sample)')
    },
    goEvidence() {
      this.$router.push('/evidence')
    },
    goCase() {
      this.$router.push('/cases/' + this.caseId)
    },

    // util
    ucFirst(v) { return v ? v.charAt(0).toUpperCase() + v.slice(1) : v },
    formatDate(v) {
      const s = String(v).replace('T', ' ')
      return s.length >= 16 ? s.slice(0, 16) : s
    },
    filenameFromUrl(u) {
      if (!u) return ''
      const idx = u.lastIndexOf('/')
      return idx >= 0 ? u.substring(idx + 1) : u
    },
    gotoCases() {
      this.$router.push('/cases')
    }
  }
}
</script>

<style scoped>
.caseDetail_container {
  display: flex;
  flex-direction: column;
  padding: 16px 2vw;
  border: 2px solid #f1f1f1;
  border-radius: 12px;
  background: rgba(255,255,255,0.6);
  backdrop-filter: blur(6px);
  min-height: calc(100vh - 120px);
}
.header { display: flex; justify-content: space-between; align-items: flex-start; gap: 16px; margin-bottom: 14px; }
.titleblock .case_title { font-size: 22px; font-weight: 800; color: #0f172a; }
.titleblock .case_no { margin-top: 4px; color: #475569; font-weight: 600; }
.actions { display: flex; gap: 10px; }
.detail_cards { display: grid; grid-template-columns: repeat(3, minmax(0, 1fr)); gap: 20px; margin-top: 16px; }
.detail_cards > .el-card { height: 100%; }
.info_card { border-radius: 16px; background: rgba(255,255,255,0.66); }
.info_grid { display: grid; grid-template-columns: repeat(5, minmax(140px, 1fr)); gap: 12px 18px; }
.info_item .label { font-size: 12px; color: #64748b; }
.info_item .value { font-size: 14px; font-weight: 700; color: #0f172a; }
.value.status.active { color: #16a34a; }
.value.status.closed { color: #ef4444; }
.value.status.draft  { color: #f59e0b; }
.tabs_card { border-radius: 16px; background: transparent; }
.hearing_list { display: grid; grid-template-columns: 1fr; gap: 16px; }
.hearing_card { border-radius: 16px; color: #fff; background: linear-gradient(135deg, rgba(99,102,241,0.6), rgba(16,185,129,0.6)); backdrop-filter: blur(12px); border: 1px solid rgba(255,255,255,0.25); }
.hearing_row { display: flex; justify-content: space-between; align-items: center; }
.hearing_time { font-size: 18px; font-weight: 800; }
.time_value { color: #ffeb3b; }
.hearing_case { margin-top: 8px; font-size: 14px; }
.case_no_inline { color: #e5e7eb; }
.link { color: #fff; text-decoration: underline; cursor: pointer; }
.link:hover { opacity: 0.9; }
.table { width: 100%; }
.tab_header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 12px; }
.tab_title { font-weight: 800; color: #0f172a; }
@media (max-width: 1280px) {
  .detail_cards { grid-template-columns: repeat(auto-fit, minmax(280px, 1fr)); }
}
</style>





