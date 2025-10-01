<template>
  <div class="list_container">
    <!-- Filters -->
    <div class="toolbar">
      <div class="toolbar_item">
        <label class="toolbar_label">By Category Name:</label>
        <el-input v-model="filters.name" placeholder="Enter category name" clearable maxlength="50"/>
      </div>
      <div class="toolbar_actions">
        <el-button type="success" round @click="fetchData">Search</el-button>
        <el-button round @click="resetFilters">Reset</el-button>
      </div>
    </div>

    <div class="divider"/>

    <!-- Page actions -->
    <div class="actions">
      <el-button type="primary" round icon="el-icon-plus" @click="openAddDialog">
        Add Category
      </el-button>
    </div>

    <!-- Table -->
    <el-table
      :data="tableData"
      class="table"
      height="calc(100vh - 360px)"
      @selection-change="onSelectionChange"
      @sort-change="onSortChange"
    >
      <el-table-column type="selection" width="55" fixed="left"/>
      <el-table-column type="index" label="#" width="60" align="center" fixed="left"/>

      <el-table-column label="Name" prop="name" min-width="220" fixed="left"/>

      <el-table-column label="Description" prop="description" min-width="280"/>
      <el-table-column label="Created At" prop="createdAt" width="180" sortable="custom"/>
      <el-table-column label="Owner" prop="owner" width="120"/>

      <el-table-column label="Actions" min-width="220" fixed="right">
        <template slot-scope="scope">
          <el-button size="mini" type="success" round @click="openEditDialog(scope.row)">Edit</el-button>
          <el-button size="mini" type="danger" round @click="deleteRow(scope.row)">Delete</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- Pagination -->
    <div class="pagination">
      <el-pagination
        :current-page="pagination.page"
        :page-sizes="[10,20,50,100]"
        :page-size="pagination.pageSize"
        layout="total, sizes, prev, pager, next, jumper"
        :total="pagination.total"
        @size-change="onPageSizeChange"
        @current-change="onPageChange"
      />
    </div>

    <!-- Add Dialog -->
    <el-dialog
      title="Add Category"
      :visible.sync="addDialogVisible"
      width="520px"
      :modal="false"
      @close="onAddDialogClosed"
    >
      <el-form ref="addFormRef" :model="addForm" :rules="formRules" label-width="110px">
        <el-form-item label="Name" prop="name">
          <el-input v-model="addForm.name" maxlength="100" show-word-limit placeholder="Category name"/>
        </el-form-item>
        <el-form-item label="Description">
          <el-input v-model="addForm.description" type="textarea" :rows="3" maxlength="255" show-word-limit placeholder="Description (optional)"/>
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button @click="addDialogVisible = false">Cancel</el-button>
        <el-button type="primary" @click="submitAdd">Create</el-button>
      </span>
    </el-dialog>

    <!-- Edit Dialog -->
    <el-dialog
      title="Edit Category"
      :visible.sync="editDialogVisible"
      width="520px"
      :modal="false"
      @close="onEditDialogClosed"
    >
      <el-form ref="editFormRef" :model="editForm" :rules="formRules" label-width="110px">
        <el-form-item label="Name" prop="name">
          <el-input v-model="editForm.name" maxlength="100" show-word-limit placeholder="Category name"/>
        </el-form-item>
        <el-form-item label="Description">
          <el-input v-model="editForm.description" type="textarea" :rows="3" maxlength="255" show-word-limit placeholder="Description (optional)"/>
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
      editForm: { id: null, name: '', description: '' /*, status: 'active'*/ },

      formRules: {
        name: [
          { required: true, message: 'Name is required', trigger: 'blur' },
          { min: 2, max: 100, message: 'Name length 2-100', trigger: 'blur' }
        ]
      }
    }
  },
  created(){ this.fetchData() },
  methods: {
    async fetchData(){
      try {
        const data = await categoryApi.list({
          name: this.filters.name,
          status: this.filters.status,
          page: this.pagination.page,
          size: this.pagination.pageSize,
          orderBy: this.orderBy,
          orderType: this.orderType
        })
        const rows  = Array.isArray(data) ? data : (data.records || [])
        const total = Array.isArray(data) ? rows.length : (data.total || 0)

        this.tableData = rows
        this.pagination.total = total
      } catch (e) {
        this.$message.error('Loading category failed')
      }
    },
    resetFilters(){
      this.filters = { name:'', status:'' }
      this.pagination.page = 1
      this.fetchData()
    },
    onSelectionChange(v){ this.multipleSelection = v },
    onSortChange({ prop, order }) {
      if (!order) { this.orderBy='createdAt'; this.orderType='DESC' }
      else { this.orderBy = prop; this.orderType = order==='ascending'?'ASC':'DESC' }
      this.fetchData()
    },
    onPageSizeChange(s){ this.pagination.pageSize = s; this.pagination.page=1; this.fetchData() },
    onPageChange(p){ this.pagination.page = p; this.fetchData() },

    // ===== add dialog =====
    openAddDialog(){
      this.addDialogVisible = true
      this.$nextTick(() => {
        if (this.$refs.addFormRef) this.$refs.addFormRef.resetFields()
        this.addForm = emptyAddForm()
      })
    },
    onAddDialogClosed(){
      if (this.$refs.addFormRef) this.$refs.addFormRef.resetFields()
      this.addForm = emptyAddForm()
    },
    submitAdd(){
      this.$refs.addFormRef.validate(async valid => {
        if (!valid) return
        try {
          await categoryApi.create({
            name: this.addForm.name,
            description: this.addForm.description
            // status: this.addForm.status
          })
          this.$message.success('Created')
          this.addDialogVisible = false
          this.fetchData()
        } catch (e) {
          this.$message.error('Create failed')
        }
      })
    },

    // ===== edit dialog =====
    openEditDialog(row){
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
    onEditDialogClosed(){
      if (this.$refs.editFormRef) this.$refs.editFormRef.resetFields()
      this.editForm = { id: null, name: '', description: '' /*, status: 'active'*/ }
    },
    submitEdit(){
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

    async deleteRow(row){
      try {
        await this.$confirm(`Delete category "${row.name}"?`, 'Confirm', { type: 'warning' })
        await categoryApi.remove(row.id)
        this.$message.success('Deleted')
        this.fetchData()
      } catch (e) {return}
    },
  }
}
</script>

<style scoped>
.dialog-footer {
  display: inline-flex;
  gap: 8px;
}
</style>
