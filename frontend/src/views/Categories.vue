<template>
  <div class="page">
    <header class="page_header">
      <div class="page_headline">
        <h1>Categories</h1>
        <p class="page_subtitle">Organise matters by practice area and keep descriptions clear.</p>
      </div>
      <div class="metrics_grid">
        <div class="metric_card">
          <span class="metric_label">Total</span>
          <span class="metric_value">{{ summary.total }}</span>
          <span class="metric_hint">categories</span>
        </div>
        <div class="metric_card">
          <span class="metric_label">Documented</span>
          <span class="metric_value">{{ summary.described }}</span>
          <span class="metric_hint">with description</span>
        </div>
        <div class="metric_card">
          <span class="metric_label">Last Updated</span>
          <span class="metric_value metric_value--text">{{ summary.recent }}</span>
          <span class="metric_hint">most recent change</span>
        </div>
      </div>
    </header>

    <section class="card filters_card">
      <div class="card_header">
        <h2>Filters</h2>
        <span class="card_hint">Search by category name</span>
      </div>
      <div class="filter_grid">
        <div class="toolbar_item">
          <label class="toolbar_label">Category Name</label>
          <el-input v-model="filters.name" placeholder="Enter category name" clearable maxlength="50" />
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
        <span class="card_hint">Create or update categories in one place</span>
      </div>
      <div class="action_buttons">
        <el-button type="primary" round icon="el-icon-plus" @click="openAddDialog">Add Category</el-button>
      </div>
    </section>

    <section class="card table_card">
      <div class="card_header">
        <h2>Category Directory</h2>
        <span class="card_hint">Showing {{ tableData.length }} of {{ summary.total }}</span>
      </div>
      <el-table
        class="data_table"
        :data="tableData"
        @selection-change="onSelectionChange"
        @sort-change="onSortChange"
      >
        <el-table-column type="selection" width="55" fixed="left" />
        <el-table-column type="index" label="#" width="60" align="center" fixed="left" />
        <el-table-column label="Name" prop="name" min-width="220" fixed="left" />
        <el-table-column label="Description" prop="description" min-width="280" />
        <el-table-column label="Created At" prop="createdAt" width="180" sortable="custom" />
        <el-table-column label="Owner" prop="owner" width="140" />
        <el-table-column label="Actions" min-width="220" fixed="right">
          <template slot-scope="scope">
            <el-button size="mini" type="success" round @click="openEditDialog(scope.row)">Edit</el-button>
            <el-button size="mini" type="danger" round @click="deleteRow(scope.row)">Delete</el-button>
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

    <el-dialog
      title="Add Category"
      :visible.sync="addDialogVisible"
      width="520px"
      :modal="false"
      @close="onAddDialogClosed"
    >
      <el-form ref="addFormRef" :model="addForm" :rules="formRules" label-width="110px">
        <el-form-item label="Name" prop="name">
          <el-input v-model="addForm.name" maxlength="100" show-word-limit placeholder="Category name" />
        </el-form-item>
        <el-form-item label="Description">
          <el-input v-model="addForm.description" type="textarea" :rows="3" maxlength="255" show-word-limit placeholder="Description (optional)" />
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button @click="addDialogVisible = false">Cancel</el-button>
        <el-button type="primary" @click="submitAdd">Create</el-button>
      </span>
    </el-dialog>

    <el-dialog
      title="Edit Category"
      :visible.sync="editDialogVisible"
      width="520px"
      :modal="false"
      @close="onEditDialogClosed"
    >
      <el-form ref="editFormRef" :model="editForm" :rules="formRules" label-width="110px">
        <el-form-item label="Name" prop="name">
          <el-input v-model="editForm.name" maxlength="100" show-word-limit placeholder="Category name" />
        </el-form-item>
        <el-form-item label="Description">
          <el-input v-model="editForm.description" type="textarea" :rows="3" maxlength="255" show-word-limit placeholder="Description (optional)" />
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button @click="editDialogVisible = false">Cancel</el-button>
        <el-button type="primary" @click="submitEdit">Save</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import categoryApi from '../api/categories'

const emptyAddForm = () => ({
  name: '',
  description: ''
})

export default {
  name: 'CategoryList',
  data() {
    return {
      filters: { name: '', status: '' },
      tableData: [],
      multipleSelection: [],
      orderBy: 'createdAt',
      orderType: 'DESC',
      pagination: { page: 1, pageSize: 10, total: 0 },

      addDialogVisible: false,
      editDialogVisible: false,

      addForm: emptyAddForm(),
      editForm: { id: null, name: '', description: '' },

      formRules: {
        name: [
          { required: true, message: 'Name is required', trigger: 'blur' },
          { min: 2, max: 100, message: 'Name length 2-100', trigger: 'blur' }
        ]
      },

      summary: {
        total: 0,
        described: 0,
        recent: '?'
      }
    }
  },
  created() { this.fetchData() },
  methods: {
    async fetchData() {
      try {
        const data = await categoryApi.list({
          name: this.filters.name,
          status: this.filters.status,
          page: this.pagination.page,
          size: this.pagination.pageSize,
          orderBy: this.orderBy,
          orderType: this.orderType
        })
        const rows = Array.isArray(data) ? data : (data.records || [])
        const total = Array.isArray(data) ? rows.length : (data.total || 0)

        this.tableData = rows
        this.pagination.total = total

        const described = rows.filter(item => item.description && item.description.trim().length).length
        const latest = rows.reduce((acc, item) => {
          const candidate = item.updatedAt || item.createdAt
          if (!candidate) return acc
          if (!acc) return candidate
          return candidate > acc ? candidate : acc
        }, null)

        this.summary = {
          total: total || rows.length,
          described,
          recent: latest || '?'
        }
      } catch (e) {
        this.$message.error('Loading category failed')
      }
    },
    resetFilters() {
      this.filters = { name: '', status: '' }
      this.pagination.page = 1
      this.fetchData()
    },
    onSelectionChange(v) { this.multipleSelection = v },
    onSortChange({ prop, order }) {
      if (!order) { this.orderBy = 'createdAt'; this.orderType = 'DESC' }
      else { this.orderBy = prop; this.orderType = order === 'ascending' ? 'ASC' : 'DESC' }
      this.fetchData()
    },
    onPageSizeChange(s) { this.pagination.pageSize = s; this.pagination.page = 1; this.fetchData() },
    onPageChange(p) { this.pagination.page = p; this.fetchData() },

    openAddDialog() {
      this.addDialogVisible = true
      this.$nextTick(() => {
        if (this.$refs.addFormRef) this.$refs.addFormRef.resetFields()
        this.addForm = emptyAddForm()
      })
    },
    onAddDialogClosed() {
      if (this.$refs.addFormRef) this.$refs.addFormRef.resetFields()
      this.addForm = emptyAddForm()
    },
    submitAdd() {
      this.$refs.addFormRef.validate(async valid => {
        if (!valid) return
        try {
          await categoryApi.create({
            name: this.addForm.name,
            description: this.addForm.description
          })
          this.$message.success('Created')
          this.addDialogVisible = false
          this.fetchData()
        } catch (e) {
          this.$message.error('Create failed')
        }
      })
    },

    openEditDialog(row) {
      this.editForm = {
        id: row.id,
        name: row.name,
        description: row.description || ''
      }
      this.editDialogVisible = true
      this.$nextTick(() => {
        if (this.$refs.editFormRef) this.$refs.editFormRef.clearValidate()
      })
    },
    onEditDialogClosed() {
      if (this.$refs.editFormRef) this.$refs.editFormRef.resetFields()
      this.editForm = { id: null, name: '', description: '' }
    },
    submitEdit() {
      this.$refs.editFormRef.validate(async valid => {
        if (!valid) return
        try {
          await categoryApi.update(this.editForm.id, {
            name: this.editForm.name,
            description: this.editForm.description
          })
          this.$message.success('Saved')
          this.editDialogVisible = false
          this.fetchData()
        } catch (e) {
          this.$message.error('Save failed')
        }
      })
    },

    async deleteRow(row) {
      try {
        await this.$confirm(`Delete category "${row.name}"?`, 'Confirm', { type: 'warning' })
        await categoryApi.remove(row.id)
        this.$message.success('Deleted')
        this.fetchData()
      } catch (e) {
        return
      }
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

.table_footer {
  display: flex;
  justify-content: flex-end;
}

.dialog-footer {
  display: inline-flex;
  gap: 8px;
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

