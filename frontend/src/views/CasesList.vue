<template>
  <div class="list_container">
    <!-- ====== Filters ====== -->
    <div class="toolbar">
      <div class="toolbar_item">
        <label class="toolbar_label">By Case Title:</label>
        <el-input v-model="filters.title" placeholder="Enter case title" clearable maxlength="50" />
      </div>

      <div class="toolbar_item">
        <label class="toolbar_label">By Category:</label>
        <el-select v-model="filters.category" clearable placeholder="Select category" style="min-width:200px">
          <el-option v-for="c in categoryOptions" :key="c.value" :label="c.label" :value="c.value" />
        </el-select>
      </div>

      <div class="toolbar_actions">
        <el-button type="success" round @click="fetchCases">Search</el-button>
        <el-button round @click="resetFilters">Reset</el-button>
      </div>
    </div>

    <div class="divider" />

    <!-- ====== Page Actions ====== -->
    <div class="actions">
      <el-button type="primary" round icon="el-icon-plus" @click="openCreate">Add Case</el-button>
    </div>

    <!-- ====== Table ====== -->
    <el-table
      :data="tableData"
      v-loading="loading"
      tooltip-effect="dark"
      class="table"
      height="calc(100vh - 360px)"
    >
      <el-table-column type="index" label="#" width="60" align="center" fixed="left" />

      <el-table-column label="Case No." prop="caseNumber" width="160" />
      <el-table-column label="Title" prop="title" min-width="200" fixed="left">
        <template slot-scope="scope">
          <span class="link" @click="goDetail(scope.row)">{{ scope.row.title }}</span>
        </template>
      </el-table-column>

      <el-table-column label="Category" prop="category" min-width="200" />
      <el-table-column label="Status" prop="status" width="120" :formatter="statusFormat" />
      <el-table-column label="Hearing" prop="hearingAt" width="160" />
      <el-table-column label="Created At" prop="createdAt" width="160" />

      <el-table-column label="Actions" min-width="200" fixed="right">
        <template slot-scope="scope">
          <el-button size="mini" type="success" round @click="openEdit(scope.row)">Edit</el-button>
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

    <!-- ====== Create Dialog ====== -->
    <el-dialog title="Add Case" :modal="false" :visible.sync="createDlg.visible" width="560px" @close="resetCreateForm">
      <el-form :model="createDlg.form" :rules="rules" ref="createForm" label-position="top">
        <el-form-item label="Title" prop="title">
          <el-input v-model="createDlg.form.title" maxlength="255" />
        </el-form-item>
        <el-form-item label="Case No." prop="caseNum">
          <el-input v-model="createDlg.form.caseNum" maxlength="100" />
        </el-form-item>
        <el-form-item label="Description">
          <el-input v-model="createDlg.form.description" type="textarea" :rows="3" />
        </el-form-item>
        <el-form-item label="Status" prop="status">
          <el-select v-model="createDlg.form.status">
            <el-option label="OPEN" value="OPEN" />
            <el-option label="CLOSED" value="CLOSED" />
          </el-select>
        </el-form-item>
        <el-form-item label="Hearing At">
          <el-date-picker v-model="createDlg.form.hearingAt" type="datetime" placeholder="Select date & time" style="width:100%" />
        </el-form-item>
        <el-form-item label="Categories">
          <el-select v-model="createDlg.form.categoryIds" multiple placeholder="Select categories" style="width:100%">
            <el-option v-for="c in categoryOptions" :key="c.value" :label="c.label" :value="c.value" />
          </el-select>
        </el-form-item>
      </el-form>
      <span slot="footer">
        <el-button @click="createDlg.visible=false">Cancel</el-button>
        <el-button type="primary" @click="submitCreate">Create</el-button>
      </span>
    </el-dialog>

    <!-- ====== Edit Dialog ====== -->
    <el-dialog title="Edit Case" :modal="false" :visible.sync="editDlg.visible" width="560px" @close="resetEditForm">
      <el-form :model="editDlg.form" :rules="rules" ref="editForm" label-position="top">
        <el-form-item label="Title" prop="title">
          <el-input v-model="editDlg.form.title" maxlength="255" />
        </el-form-item>
        <el-form-item label="Case No." prop="caseNum">
          <el-input v-model="editDlg.form.caseNum" maxlength="100" />
        </el-form-item>
        <el-form-item label="Description">
          <el-input v-model="editDlg.form.description" type="textarea" :rows="3" />
        </el-form-item>
        <el-form-item label="Status" prop="status">
          <el-select v-model="editDlg.form.status">
            <el-option label="OPEN" value="OPEN" />
            <el-option label="CLOSED" value="CLOSED" />
          </el-select>
        </el-form-item>
        <el-form-item label="Hearing At">
          <el-date-picker v-model="editDlg.form.hearingAt" type="datetime" placeholder="Select date & time" style="width:100%" />
        </el-form-item>
        <el-form-item label="Categories">
          <el-select v-model="editDlg.form.categoryIds" multiple placeholder="Select categories" style="width:100%">
            <el-option v-for="c in categoryOptions" :key="c.value" :label="c.label" :value="c.value" />
          </el-select>
        </el-form-item>
      </el-form>
      <span slot="footer">
        <el-button @click="editDlg.visible=false">Cancel</el-button>
        <el-button type="primary" @click="submitEdit">Save</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import { listCases, getCase, createCase, updateCase } from '@/api/cases'
import categoryApi from '../api/categories'

export default {
  name: 'CaseList',
  data() {
    return {
      loading: false,
      filters: { title: '', category: '' },
      categoryOptions: [],
      tableData: [],
      pagination: { page: 1, pageSize: 10, total: 0 },

      createDlg: {
        visible: false,
        form: { title: '', caseNum: '', description: '', status: 'OPEN', hearingAt: null, categoryIds: [] }
      },
      editDlg: {
        visible: false,
        form: { id: null, title: '', caseNum: '', description: '', status: 'OPEN', hearingAt: null, categoryIds: [] }
      },

      rules: {
        title:   [{ required: true, message: 'Title required', trigger: 'blur' }],
        caseNum: [{ required: true, message: 'Case No. required', trigger: 'blur' }],
        status:  [{ required: true, message: 'Status required', trigger: 'change' }]
      }
    }
  },
  created() {
    this.init()
  },
  methods: {
    async init() {
      await this.fetchCategories()
      await this.fetchCases()
    },

    async fetchCategories() {
  try {
    const data= await categoryApi.list()
    this.categoryOptions = data.map(function(c) {
      return { label: c.name, value: c.id }
    })
  } catch (e) {
    this.$message.error('Failed to load categories')
  }
},

    async fetchCases() {
  this.loading = true
  try {
    const params = {
      pageNo: this.pagination.page,
      pageSize: this.pagination.pageSize
    }
    if (this.filters.category) params.categoryId = this.filters.category
    if (this.filters.title) params.title = this.filters.title

    const  data= await listCases(params)
    let list = []
    if (Array.isArray(data)) {
      list = data
    } else if (data && Array.isArray(data.data)) {
      list = data.data
    } else if (data && Array.isArray(data.records)) {
      list = data.records
    }

    var rows = list.map(this.mapRow)
    this.tableData = rows

    var count = rows.length
    var base = (this.pagination.page - 1) * this.pagination.pageSize
    this.pagination.total = base + count
  } catch (e) {
    this.$message.error('Failed to load cases')
  } finally {
    this.loading = false
  }
},

   mapRow: function(row) {
  var category = '—'
  if (row.categories && row.categories.length) {
    category = row.categories.join(', ')
  }

  var status = 'closed'
  if (row.status === 'OPEN') {
    status = 'active'
  }

  var hearingAt = '—'
  if (row.hearingAt) {
    hearingAt = row.hearingAt.replace('T', ' ').slice(0, 16)
  }

  var createdAt = '—'
  if (row.createdAt) {
    createdAt = row.createdAt.replace('T', ' ').slice(0, 16)
  }

  return {
    id: row.id,
    caseNumber: row.caseNum,
    title: row.title,
    category: category,
    status: status,
    hearingAt: hearingAt,
    createdAt: createdAt,
  }
},


    resetFilters() {
      this.filters = { title: '', category: '' }
      this.pagination.page = 1
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
      const map = { active: 'Active', closed: 'Closed' }
      return map[row.status] || row.status
    },

    goDetail(row) { this.$router.push(`/cases/${row.id}`) },

    // ===== Create =====
    openCreate() {
      this.createDlg.visible = true
    },
    resetCreateForm() {
      this.$refs.createForm && this.$refs.createForm.resetFields()
      this.createDlg.form = { title: '', caseNum: '', description: '', status: 'OPEN', hearingAt: null, categoryIds: [] }
    },
    submitCreate() {
      this.$refs.createForm.validate(async valid => {
        if (!valid) return
        try {
          const body = {
            title: this.createDlg.form.title,
            caseNum: this.createDlg.form.caseNum,
            description: this.createDlg.form.description,
            status: this.createDlg.form.status,
            hearingAt: this.createDlg.form.hearingAt
              ? new Date(this.createDlg.form.hearingAt).toISOString()
              : null
          }
          const ids = this.createDlg.form.categoryIds
          await createCase(body, ids)
          this.$message.success('Created')
          this.createDlg.visible = false
          this.resetCreateForm()
          this.fetchCases()
        } catch {
          this.$message.error('Create failed')
        }
      })
    },

    // ===== Edit dialog=====
    async openEdit(row) {
  try {
    const d = await getCase(row.id)
    if (!d) {
      this.$message.error('Empty response')
      return
    }

    var categoryIds = []
    if (d.categories && d.categories.length) {
      for (var i = 0; i < d.categories.length; i++) {
        var item = d.categories[i]
        if (item && item.id != null) {
          categoryIds.push(item.id)
        }
      }
    }

    var title = ''
    if (d.title) title = d.title

    var caseNum = ''
    if (d.caseNum) caseNum = d.caseNum
    if (!caseNum && d.caseNumber) caseNum = d.caseNumber   // 兜底

    var description = ''
    if (d.description) description = d.description

    var status = 'OPEN'
    if (d.status) status = d.status   // OPEN/CLOSED

    var hearingAt = null
    if (d.hearingAt) {
      var iso = String(d.hearingAt).replace(' ', 'T')
      hearingAt = new Date(iso)
    }

    this.editDlg.form = {
      id: d.id || null,
      title: title,
      caseNum: caseNum,
      description: description,
      status: status,
      hearingAt: hearingAt,
      categoryIds: categoryIds
    }

    this.editDlg.visible = true
  } catch (e) {
    this.$message.error('Load detail failed')
  }
},
    resetEditForm() {
      this.$refs.editForm && this.$refs.editForm.resetFields()
      this.editDlg.form = { id: null, title: '', caseNum: '', description: '', status: 'OPEN', hearingAt: null, categoryIds: [] }
    },
    submitEdit() {
      this.$refs.editForm.validate(async valid => {
        if (!valid) return
        try {
          const id = this.editDlg.form.id
          const body = {
            title: this.editDlg.form.title,
            caseNum: this.editDlg.form.caseNum,
            description: this.editDlg.form.description,
            status: this.editDlg.form.status,
            hearingAt: this.editDlg.form.hearingAt
              ? new Date(this.editDlg.form.hearingAt).toISOString()
              : null
          }
          const ids = this.editDlg.form.categoryIds
          await updateCase(id, body, ids)
          this.$message.success('Updated')
          this.editDlg.visible = false
          this.resetEditForm()
          this.fetchCases()
        } catch {
          this.$message.error('Update failed')
        }
      })
    },

    namesToIds(names) {
      if (!names || !names.length) return []
      const dict = new Map(this.categoryOptions.map(o => [String(o.label).toLowerCase(), o.value]))
      return names.map(n => dict.get(String(n).toLowerCase())).filter(Boolean)
    }
  }
}
</script>

<style scoped>
.list_container { padding: 16px 2vw; }
.toolbar { display:flex; gap:16px; align-items:flex-end; flex-wrap: wrap; }
.toolbar_item { display:flex; flex-direction:column; gap:6px; }
.toolbar_label { font-size:12px; color:#64748b; }
.toolbar_actions { display:flex; gap:8px; align-items:center; }
.divider { height:1px; background:#eee; margin:12px 0; }
.actions { margin-bottom:12px; }
.table { width:100%; }
.link { color:#3b82f6; cursor:pointer; text-decoration: underline; }
.link:hover { opacity:.85; }
.pagination { display:flex; justify-content:flex-end; margin-top:12px; }
</style>
