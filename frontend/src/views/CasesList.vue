<template>
  <div class="page">
    <header class="page_header">
      <div class="page_headline">
        <h1>Cases</h1>
        <p class="page_subtitle">Monitor every matter and its upcoming deadlines.</p>
      </div>
      <div class="metrics_grid">
        <div class="metric_card">
          <span class="metric_label">Total</span>
          <span class="metric_value">{{ summary.total }}</span>
          <span class="metric_hint">records</span>
        </div>
        <div class="metric_card">
          <span class="metric_label">Active</span>
          <span class="metric_value">{{ summary.active }}</span>
          <span class="metric_hint">in progress</span>
        </div>
        <div class="metric_card">
          <span class="metric_label">Upcoming</span>
          <span class="metric_value">{{ summary.upcoming }}</span>
          <span class="metric_hint">hearings scheduled</span>
        </div>
      </div>
    </header>

    <section class="card filters_card">
      <div class="card_header">
        <h2>Filters</h2>
        <span class="card_hint">Search by title, category or status</span>
      </div>
      <div class="filter_grid">
        <div class="toolbar_item">
          <label class="toolbar_label">Case Title</label>
          <el-input v-model="filters.title" placeholder="Enter case title" clearable maxlength="80" @keyup.enter.native="fetchCases" />
        </div>
        <div class="toolbar_item">
          <label class="toolbar_label">Category</label>
          <el-select v-model="filters.category" clearable placeholder="Select category">
            <el-option v-for="option in categoryOptions" :key="option.value" :label="option.label" :value="option.value" />
          </el-select>
        </div>
        <div class="toolbar_item">
          <label class="toolbar_label">Status</label>
          <el-select v-model="filters.status" clearable placeholder="Select status">
            <el-option label="Active" value="active" />
            <el-option label="Closed" value="closed" />
            <el-option label="Draft" value="draft" />
          </el-select>
        </div>
      </div>
      <div class="filter_actions">
        <el-button type="primary" round icon="el-icon-search" @click="fetchCases">Search</el-button>
        <el-button round @click="resetFilters">Clear</el-button>
      </div>
    </section>

    <section class="card action_card">
      <div class="card_header">
        <h2>Quick Actions</h2>
        <span class="card_hint">Use the case register to stay on top of upcoming hearings</span>
      </div>
      <div class="action_buttons">
        <el-button type="primary" round icon="el-icon-plus" @click="goCreate">Add Case</el-button>
      </div>
    </section>

    <section class="card table_card">
      <div class="card_header">
        <h2>Case Register</h2>
        <span class="card_hint">Showing {{ tableData.length }} of {{ pagination.total }} cases</span>
      </div>
      <el-table
        v-loading="loading"
        class="data_table"
        :data="tableData"
        tooltip-effect="dark"
        @sort-change="onSortChange"
      >
        <el-table-column prop="caseNumber" label="Case No." width="140" sortable="custom" />
        <el-table-column prop="title" label="Title" min-width="200" sortable="custom">
          <template slot-scope="scope">
            <span class="link" @click="goDetail(scope.row)">{{ scope.row.title }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="categoryLabel" label="Categories" min-width="160" sortable="custom" />
        <el-table-column prop="status" label="Status" width="120" sortable="custom">
          <template slot-scope="scope">
            <el-tag :type="statusType(scope.row.status)" effect="plain">{{ statusFormat(scope.row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="hearingAt" label="Hearing" width="180" sortable="custom">
          <template slot-scope="scope">
            {{ formatDateTime(scope.row.hearingAt) }}
          </template>
        </el-table-column>
        <el-table-column prop="updatedAt" label="Updated" width="180" sortable="custom">
          <template slot-scope="scope">
            {{ formatDateTime(scope.row.updatedAt) }}
          </template>
        </el-table-column>
        <el-table-column prop="createdAt" label="Created" width="180" sortable="custom">
          <template slot-scope="scope">
            {{ formatDateTime(scope.row.createdAt) }}
          </template>
        </el-table-column>
        <el-table-column prop="owner" label="Owner" width="150" />
        <el-table-column label="Actions" width="120" fixed="right">
          <template slot-scope="scope">
            <el-button size="mini" type="primary" round @click="goDetail(scope.row)">Details</el-button>
          </template>
        </el-table-column>
      </el-table>
      <div class="table_footer">
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
    </section>
  </div>
</template>

<script>
import { listCases } from '@/api/cases'
import categoriesApi from '@/api/categories'

const dateTimeFormatter = new Intl.DateTimeFormat('en-AU', {
  year: 'numeric',
  month: 'short',
  day: 'numeric',
  hour: '2-digit',
  minute: '2-digit'
})

export default {
  name: 'CaseList',
  data() {
    return {
      loading: false,
      filters: {
        title: '',
        category: null,
        status: ''
      },
      categoryOptions: [],
      summary: {
        total: 0,
        active: 0,
        upcoming: 0
      },
      rawData: [],
      tableData: [],
      pagination: {
        page: 1,
        pageSize: 10,
        total: 0
      },
      sortState: {
        prop: '',
        order: ''
      }
    }
  },
  created() {
    this.initialise()
  },
  methods: {
    async initialise() {
      await this.loadCategories()
      this.fetchCases()
    },
    async loadCategories() {
      try {
        const list = await categoriesApi.list()
        const records = list && Array.isArray(list.records) ? list.records : (Array.isArray(list) ? list : [])
        this.categoryOptions = records.map(item => ({ label: item.name, value: item.id }))
      } catch (error) {
        this.categoryOptions = []
      }
    },
    async fetchCases() {
      this.loading = true
      try {
        const params = {
          pageNo: this.pagination.page,
          pageSize: this.pagination.pageSize
        }
        if (this.filters.title) params.keyword = this.filters.title.trim()
        if (this.filters.category) params.categoryId = this.filters.category
        if (this.filters.status) params.status = this.filters.status

        const response = await listCases(params)
        const summaryOrEmpty = value => (value && typeof value === 'object' ? value : {})
        const records = response && Array.isArray(response.records) ? response.records : []
        const summary = summaryOrEmpty(response && response.summary)
        this.summary = {
          total: summary.total != null ? summary.total : (response && typeof response.total === 'number' ? response.total : records.length),
          active: summary.active != null ? summary.active : 0,
          upcoming: summary.upcoming != null ? summary.upcoming : 0
        }
        this.pagination.total = response && typeof response.total === 'number' ? response.total : records.length
        this.rawData = records.map(this.normaliseCase)
        this.applySorting()
      } catch (error) {
        this.$message.error('Failed to load cases')
        this.rawData = []
        this.tableData = []
        this.pagination.total = 0
        this.summary = { total: 0, active: 0, upcoming: 0 }
      } finally {
        this.loading = false
      }
    },
    normaliseCase(row) {
      const categories = Array.isArray(row.categories) ? row.categories : []
      return {
        id: row.id,
        caseNumber: row.caseNum || '—',
        title: row.title || 'Untitled case',
        categoryLabel: categories.length ? categories.join(', ') : 'Uncategorised',
        status: (row.status || '').toLowerCase(),
        hearingAt: row.hearingAt || null,
        createdAt: row.createdAt || null,
        updatedAt: row.updatedAt || null,
        owner: row.lawyerId ? `Lawyer #${row.lawyerId}` : 'Unassigned'
      }
    },
    applySorting() {
      const { prop, order } = this.sortState
      let data = [...this.rawData]
      if (prop && order) {
        const direction = order === 'ascending' ? 1 : -1
        data.sort((a, b) => {
          const left = a[prop]
          const right = b[prop]
          if (left === right) return 0
          if (!left) return -direction
          if (!right) return direction
          if (left > right) return direction
          if (left < right) return -direction
          return 0
        })
      }
      this.tableData = data
    },
    onSortChange({ prop, order }) {
      if (!order) {
        this.sortState = { prop: '', order: '' }
      } else {
        this.sortState = { prop, order }
      }
      this.applySorting()
    },
    onPageSizeChange(size) {
      this.pagination.pageSize = size
      this.pagination.page = 1
      this.fetchCases()
    },
    onPageChange(page) {
      this.pagination.page = page
      this.fetchCases()
    },
    resetFilters() {
      this.filters = { title: '', category: null, status: '' }
      this.pagination.page = 1
      this.fetchCases()
    },
    statusFormat(status) {
      if (!status) return ''
      return status.charAt(0).toUpperCase() + status.slice(1)
    },
    statusType(status) {
      const map = {
        active: 'success',
        closed: 'info',
        draft: 'warning'
      }
      return map[status] || 'info'
    },
    formatDateTime(value) {
      if (!value) return '?'
      const date = new Date(value)
      if (Number.isNaN(date.getTime())) return '?'
      return dateTimeFormatter.format(date)
    },
    goDetail(row) {
      this.$router.push(`/cases/${row.id}`)
    },
    goCreate() {
      this.$router.push('/cases/new')
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

.metric_hint {
  font-size: 12px;
  color: #6b7280;
}

.filter_grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(220px, 1fr));
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

.table_footer {
  display: flex;
  justify-content: flex-end;
}

.link {
  color: #4f46e5;
  font-weight: 600;
  cursor: pointer;
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



