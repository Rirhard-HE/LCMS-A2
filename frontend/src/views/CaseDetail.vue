<template>
  <div class="caseDetail_container">
    <!-- ===== Header ===== -->
    <div class="header">
      <div class="titleblock">
        <div class="case_title">{{ caseData.title }}</div>
        <div class="case_no">Case No. {{ caseData.caseNumber }}</div>
      </div>
      <div class="actions">
        <el-button :type="caseData.status==='active' ? 'info' : 'success'" round @click="toggleStatus">
          {{ caseData.status==='active' ? 'Close' : 'Activate' }}
        </el-button>
      </div>
    </div>

    <!-- ===== Info Card ===== -->
    <el-card class="info_card" shadow="hover">
      <div class="info_grid">
        <div class="info_item">
          <div class="label">Category</div>
          <div class="value">{{ caseData.category }}</div>
        </div>
        <div class="info_item">
          <div class="label">Status</div>
          <div class="value status" :class="caseData.status">{{ caseData.status | ucFirst }}</div>
        </div>
        <div class="info_item">
          <div class="label">Owner</div>
          <div class="value">{{ caseData.owner }}</div>
        </div>
        <div class="info_item">
          <div class="label">Created At</div>
          <div class="value">{{ caseData.createdAt }}</div>
        </div>
        <div class="info_item">
          <div class="label">Last Updated</div>
          <div class="value">{{ caseData.updatedAt }}</div>
        </div>
      </div>
    </el-card>

    <!-- ===== Hearings Section (moved out of tabs) ===== -->
    <el-card class="hearing_section" shadow="hover">
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
            <span class="link" @click="goCase(caseData.caseNumber)">{{ caseData.title }}</span>
            <span class="case_no_inline"> ({{ caseData.caseNumber }})</span>
          </div>
        </el-card>
      </div>
    </el-card>

    <!-- ===== Evidence Section ===== -->
    <el-card class="tabs_card" shadow="never">
      <div class="tab_header">
        <div class="tab_title">Evidence List</div>
        <el-button type="primary" round icon="el-icon-folder-add" @click="goEvidence">Go to evidence</el-button>
      </div>
      <el-table
        :data="caseData.evidence"
        class="table"
        height="380"
        border
      >
        <el-table-column type="index" width="60" align="center" />
        <el-table-column label="File Name" prop="filename" min-width="240">
          <template slot-scope="scope">
            <span>{{ scope.row.filename }}</span>
          </template>
        </el-table-column>
        <el-table-column label="Uploaded By" prop="uploadedBy" width="160" />
        <el-table-column label="Uploaded At" prop="uploadedAt" width="180" />
        <!-- 删除 Actions 列 -->
      </el-table>
    </el-card>
  </div>
</template>

<script>
export default {
  name: 'CaseDetail',
  data() {
    return {
      caseData: {
        id: 1,
        caseNumber: 'LC-2025-0001',
        title: 'Johnson v. State',
        category: 'Criminal',
        status: 'active',
        owner: 'Alice',
        createdAt: '2025-09-10 12:00',
        updatedAt: '2025-09-25 16:40',
        hearings: [
          { id: 1, date: '2025-10-01', time: '10:00' },
        ],
        evidence: [
          { id: 11, filename: 'witness_statement.pdf', uploadedBy: 'Alice', uploadedAt: '2025-09-12 09:30' },
          { id: 12, filename: 'photo_scene.jpg',       uploadedBy: 'Bob',   uploadedAt: '2025-09-15 11:00' }
        ]
      }
    }
  },
  filters: {
    ucFirst(v) { return v ? v.charAt(0).toUpperCase() + v.slice(1) : v }
  },
  methods: {
    toggleStatus() {
      this.caseData.status = this.caseData.status === 'active' ? 'closed' : 'active'
      this.$message.success('Status updated (mock)')
    },
    addHearing() {
      this.$message.info('Add hearing clicked (mock)')
    },
    goEvidence() {
      this.$router.push('/evidence')
    },
    goCase(caseNo) { this.$message.info(`Go to case ${caseNo} (mock)`) }
  }
}
</script>

<style scoped>
.caseDetail_container {
  display: flex;
  flex-direction: column;
  margin: 0;
  padding: 16px 2vw;
  border: 2px solid #f1f1f1;
  border-radius: 12px;
  background: rgba(255,255,255,0.6);
  backdrop-filter: blur(6px);
  min-height: calc(100vh - 120px);
}

/* Header */
.header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 16px;
  margin-bottom: 14px;
}
.titleblock .case_title {
  font-size: 22px;
  font-weight: 800;
  color: #0f172a;
}
.titleblock .case_no {
  margin-top: 4px;
  color: #475569;
  font-weight: 600;
}
.actions {
  display: flex;
  gap: 10px;
}

/* Info Card */
.info_card {
  border-radius: 16px;
  margin-bottom: 16px;
  background: rgba(255,255,255,0.66);
}
.info_grid {
  display: grid;
  grid-template-columns: repeat(5, minmax(140px, 1fr));
  gap: 12px 18px;
}
.info_item .label {
  font-size: 12px;
  color: #64748b;
}
.info_item .value {
  font-size: 14px;
  font-weight: 700;
  color: #0f172a;
}
.value.status.active { color: #16a34a; }
.value.status.closed { color: #ef4444; }
.value.status.draft  { color: #f59e0b; }

/* Tabs */
.tabs_card {
  border-radius: 16px;
  background: transparent;
}

/* Hearings */
.hearing_list {
  display: grid;
  grid-template-columns: 1fr;
  gap: 16px;
}
.hearing_card {
  border-radius: 16px;
  color: #fff;
  background: linear-gradient(135deg, rgba(99,102,241,0.6), rgba(16,185,129,0.6));
  backdrop-filter: blur(12px);
  border: 1px solid rgba(255,255,255,0.25);
}
.hearing_row {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.hearing_time {
  font-size: 18px;
  font-weight: 800;
}
.time_value { color: #ffeb3b; }
.hearing_case {
  margin-top: 8px;
  font-size: 14px;
}
.case_no_inline { color: #e5e7eb; }

.link { color: #fff; text-decoration: underline; cursor: pointer; }
.link:hover { opacity: 0.9; }

/* Evidence table */
.table { width: 100%; }

/* History */
.history_card { border-radius: 12px; }
.history_title { font-weight: 700; margin-bottom: 6px; }
.history_desc  { color: #475569; }

/* Tab header */
.tab_header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}
.tab_title { font-weight: 800; color: #0f172a; }
</style>
