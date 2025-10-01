<template>
  <div class="list_container">
    <div class="toolbar">
      <div class="toolbar_item">
        <label class="toolbar_label">By Case Title:</label>
        <el-input v-model="filters.title" placeholder="Enter case title" clearable maxlength="50" />
      </div>

      <div class="toolbar_item">
        <label class="toolbar_label">By Category:</label>
        <el-select v-model="filters.category" clearable placeholder="Select category">
          <el-option v-for="c in categoryOptions" :key="c.value" :label="c.label" :value="c.value" />
        </el-select>
      </div>

      <!-- <div class="toolbar_item">
        <label class="toolbar_label">Status:</label>
        <el-select v-model="filters.status" clearable placeholder="Select status">
          <el-option label="Active"  value="active" />
          <el-option label="Closed"  value="closed" />
          <el-option label="Draft"   value="draft" />
        </el-select>
      </div> -->

      <div class="toolbar_actions">
        <el-button type="success" round @click="fetchCases">Search</el-button>
        <el-button round @click="resetFilters">Reset</el-button>
      </div>
    </div>

    <div class="divider" />

    <!-- ====== Page Actions ====== -->
    <div class="actions">
      <el-button type="primary" round icon="el-icon-plus" @click="$router.push('/cases/new')">Add Case</el-button>
      <el-button type="danger"  round icon="el-icon-delete" @click="batchDelete" :disabled="!multipleSelection.length">Bulk Delete</el-button>
    </div>

    <!-- ====== Table ====== -->
    <el-table
      ref="caseTable"
      :data="tableData"
      tooltip-effect="dark"
      class="table"
      height="calc(100vh - 360px)"
      @selection-change="onSelectionChange"
      @sort-change="onSortChange"
    >
      <el-table-column type="selection" width="55" fixed="left" />
      <el-table-column type="index" label="#" width="60" align="center" fixed="left" />

      <el-table-column label="Case No." prop="caseNumber" width="140" sortable="custom" />

      <el-table-column label="Title" prop="title" min-width="160" fixed="left">
        <template slot-scope="scope">
          <span class="link" @click="goDetail(scope.row)">{{ scope.row.title }}</span>
        </template>
      </el-table-column>

      <el-table-column label="Category" prop="category" width="160" />
      <el-table-column label="Status"    prop="status"   width="120" :formatter="statusFormat" />
      <el-table-column label="Hearing"   prop="hearingAt" width="120" sortable="custom" />
      <el-table-column label="Created At" prop="createdAt" width="120" sortable="custom" />
      <el-table-column label="Owner" prop="owner" width="120" />

      <el-table-column label="Actions" min-width="320" fixed="right">
        <template slot-scope="scope">
          <el-button size="mini" type="primary" round @click="goDetail(scope.row)">Details</el-button>
          <el-button size="mini" type="success" round @click="editRow(scope.row)">Edit</el-button>
          <el-button size="mini" :type="scope.row.status==='active' ? 'info' : 'warning'" round @click="toggleStatus(scope.row)">
            {{ scope.row.status==='active' ? 'Close' : 'Activate' }}
          </el-button>
          <el-button size="mini" type="danger" round @click="deleteRow(scope.row)">Delete</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- ====== Pagination ====== -->
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
  </div>
</template>

<script>
export default {
  name: 'CaseList',
  data() {
    return {
      filters: {
        title: '',
        category: '',
        status: ''
      },
      categoryOptions: [
        { label: 'Civil', value: 'civil' },
        { label: 'Criminal', value: 'criminal' },
        { label: 'Family', value: 'family' },
        { label: 'Immigration', value: 'immigration' }
      ],
      tableData: [],
      multipleSelection: [],
      orderBy: 'createdAt',
      orderType: 'DESC',
      pagination: {
        page: 1,
        pageSize: 10,
        total: 0
      }
    }
  },
  created() {
    this.fetchCases()
  },
  methods: {
    fetchCases() {
      const all = [
        { id:1, caseNumber:'LC-2025-0001', title:'Johnson v. State', category:'criminal', status:'active',   hearingAt:'2025-10-15 10:00', createdAt:'2025-09-10 12:00', owner:'Alice' },
        { id:2, caseNumber:'LC-2025-0002', title:'Acme LLC vs. Doe',  category:'civil',     status:'closed',  hearingAt:'2025-08-01 09:00', createdAt:'2025-07-22 15:10', owner:'Bob'   },
        { id:3, caseNumber:'LC-2025-0003', title:'Green Card Appeal', category:'immigration', status:'draft', hearingAt:'—',                 createdAt:'2025-09-01 08:30', owner:'Eve'   },
        { id:4, caseNumber:'LC-2025-0004', title:'Smith Divorce',     category:'family',    status:'active',   hearingAt:'2025-11-02 14:30', createdAt:'2025-09-21 09:45', owner:'Alice' }
      ]

      // filter
      let rows = all.filter(r =>
        (!this.filters.title || r.title.toLowerCase().includes(this.filters.title.toLowerCase())) &&
        (!this.filters.category || r.category === this.filters.category) &&
        (!this.filters.status || r.status === this.filters.status)
      )

      // sort
      const key = this.orderBy
      const dir = this.orderType === 'ASC' ? 1 : -1
      rows = rows.slice().sort((a, b) => (a[key] > b[key] ? dir : -dir))

      // pagination
      this.pagination.total = rows.length
      const start = (this.pagination.page - 1) * this.pagination.pageSize
      const end   = start + this.pagination.pageSize
      this.tableData = rows.slice(start, end)
    },

    resetFilters() {
      this.filters = { title: '', category: '', status: '' }
      this.pagination.page = 1
      this.fetchCases()
    },

    onSelectionChange(val) {
      this.multipleSelection = val
    },

    onSortChange({ prop, order }) {
      if (!order) {
        this.orderBy = 'createdAt'
        this.orderType = 'DESC'
      } else {
        this.orderBy = prop
        this.orderType = order === 'ascending' ? 'ASC' : 'DESC'
      }
      this.fetchCases()
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

    statusFormat(row) {
      const map = { active: 'Active', closed: 'Closed', draft: 'Draft' }
      return map[row.status] || row.status
    },

    // ====== Actions (stubs) ======
    goDetail(row) { this.$router.push(`/cases/${row.id}`) },
    editRow(row)  { this.$router.push(`/cases/${row.id}/edit`) },
    toggleStatus(row) {
      row.status = row.status === 'active' ? 'closed' : 'active'
      this.$message.success('Status updated')
    },
    deleteRow(row) {
      this.$confirm(`Delete case "${row.title}"?`, 'Confirm', { type: 'warning' })
        .then(() => { this.tableData = this.tableData.filter(r => r.id !== row.id); this.$message.success('Deleted') })
        .catch(() => {})
    },
    batchDelete() {
      const count = this.multipleSelection.length
      if (!count) return
      this.$confirm(`Delete ${count} selected case(s)?`, 'Confirm', { type: 'warning' })
        .then(() => { this.$message.success('Deleted'); this.fetchCases() })
        .catch(() => {})
    },
    batchActivate() {
      if (!this.multipleSelection.length) return
      this.$message.success('Activated selected')
    },
    batchDeactivate() {
      if (!this.multipleSelection.length) return
      this.$message.success('Closed selected')
    }
  }
}
</script>


