<template>
  <div class="list_container">
    <!-- Filters -->
    <div class="toolbar">
      <div class="toolbar_item">
        <label class="toolbar_label">By Category Name:</label>
        <el-input v-model="filters.name" placeholder="Enter category name" clearable maxlength="50"/>
      </div>
      <div class="toolbar_item">
        <label class="toolbar_label">Status:</label>
        <el-select v-model="filters.status" clearable placeholder="Select status">
          <el-option label="Active" value="active"/>
          <el-option label="Disabled" value="disabled"/>
        </el-select>
      </div>
      <div class="toolbar_actions">
        <el-button type="success" round @click="fetchData">Search</el-button>
        <el-button round @click="resetFilters">Reset</el-button>
      </div>
    </div>

    <div class="divider"/>

    <!-- Page actions -->
    <div class="actions">
      <el-button type="primary" round icon="el-icon-plus" @click="$router.push('/categories/new')">
        Add Category
      </el-button>
      <el-button type="danger" round icon="el-icon-delete" :disabled="!multipleSelection.length" @click="batchDelete">
        Bulk Delete
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

      <el-table-column label="Name" prop="name" min-width="220" fixed="left">
      </el-table-column>

      <el-table-column label="Description" prop="description" min-width="280"/>
      <el-table-column label="Status" prop="status" width="120" :formatter="statusFormat"/>
      <el-table-column label="Created At" prop="createdAt" width="180" sortable="custom"/>
      <el-table-column label="Owner" prop="owner" width="120"/>

      <el-table-column label="Actions" min-width="200" fixed="right">
        <template slot-scope="scope">
          <el-button size="mini" type="success" round @click="editRow(scope.row)">Edit</el-button>
          <el-button size="mini" type="danger" round @click="deleteRow(scope.row)">Delete</el-button>
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
  </div>
</template>

<script>
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
    }
  },
  created(){ this.fetchData() },
  methods: {
    fetchData(){
      const all = [
        { id:1, name:'Criminal', description:'Criminal cases', status:'active',   createdAt:'2025-09-01 10:00', owner:'Alice' },
        { id:2, name:'Civil',     description:'Civil litigation', status:'active', createdAt:'2025-08-10 14:30', owner:'Bob'   },
        { id:3, name:'Family',    description:'Family disputes', status:'disabled', createdAt:'2025-07-21 09:20', owner:'Eve'  }
      ]
      let rows = all.filter(r =>
        (!this.filters.name || r.name.toLowerCase().includes(this.filters.name.toLowerCase())) &&
        (!this.filters.status || r.status === this.filters.status)
      )
      const dir = this.orderType === 'ASC' ? 1 : -1
      rows = rows.sort((a,b) => (a[this.orderBy] > b[this.orderBy] ? dir : -dir))
      this.pagination.total = rows.length
      const start = (this.pagination.page-1) * this.pagination.pageSize
      this.tableData = rows.slice(start, start + this.pagination.pageSize)
    },
    resetFilters(){ this.filters = { name:'', status:'' }; this.pagination.page = 1; this.fetchData() },
    statusFormat(row){ return row.status==='active' ? 'Active' : 'Disabled' },
    onSelectionChange(v){ this.multipleSelection = v },
    onSortChange({prop, order}) {
      if (!order) { this.orderBy='createdAt'; this.orderType='DESC' }
      else { this.orderBy = prop; this.orderType = order==='ascending'?'ASC':'DESC' }
      this.fetchData()
    },
    onPageSizeChange(s){ this.pagination.pageSize = s; this.pagination.page=1; this.fetchData() },
    onPageChange(p){ this.pagination.page = p; this.fetchData() },
    goDetail(row){ this.$router.push(`/categories/${row.id}`) },
    editRow(row){ this.$router.push(`/categories/${row.id}/edit`) },
    deleteRow(row){ this.$message.success(`Deleted category "${row.name}" (mock)`) },
    batchDelete(){ this.$message.success(`Deleted ${this.multipleSelection.length} categories (mock)`) }
  }
}
</script>
