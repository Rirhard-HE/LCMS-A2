<template>
  <div class="list_container">
    <!-- Filters -->
    <div class="toolbar">
      <div class="toolbar_item">
        <label class="toolbar_label">By File Name:</label>
        <el-input v-model="filters.keyword" placeholder="Enter file name" clearable maxlength="50"/>
      </div>
      <div class="toolbar_item">
        <label class="toolbar_label">By Case No.:</label>
        <el-input v-model="filters.caseNo" placeholder="Enter case no." clearable maxlength="50"/>
      </div>
      <div class="toolbar_actions">
        <el-button type="success" round @click="fetchData">Search</el-button>
        <el-button round @click="resetFilters">Reset</el-button>
      </div>
    </div>

    <div class="divider"/>

    <!-- Page actions -->
    <div class="actions">
      <el-button type="primary" round icon="el-icon-upload2" @click="$message.info('Upload (mock)')">
        Upload Evidence
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

      <el-table-column label="File Name" prop="fileName" min-width="260" fixed="left">
        <template slot-scope="scope">
          <a class="link" :href="scope.row.uri" target="_blank" rel="noopener">
            {{ scope.row.fileName }}
          </a>
        </template>
      </el-table-column>

      <el-table-column label="Case No." prop="caseNo" width="160"/>
      <el-table-column label="Uploaded At" prop="uploadedAt" width="180" sortable="custom"/>
      <el-table-column label="Uploader" prop="uploader" width="120"/>

      <el-table-column label="Actions" min-width="220" fixed="right">
        <template slot-scope="scope">
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
  </div>
</template>

<script>
export default {
  name: 'EvidenceList',
  data() {
    return {
      filters: { keyword:'', caseNo:'' },
      tableData: [],
      multipleSelection: [],
      orderBy: 'uploadedAt',
      orderType: 'DESC',
      pagination: { page: 1, pageSize: 10, total: 0 }
    }
  },
  created(){ this.fetchData() },
  methods: {
    fetchData(){
      const all = [
        { id:1, fileName:'contract.pdf',  uri:'#', caseNo:'LC-2025-0004', uploadedAt:'2025-09-22 11:00', uploader:'Alice' },
        { id:2, fileName:'photo.jpg',     uri:'#', caseNo:'LC-2025-0001', uploadedAt:'2025-09-10 12:15', uploader:'Alice' },
        { id:3, fileName:'recording.mp3', uri:'#', caseNo:'LC-2025-0003', uploadedAt:'2025-09-02 16:40', uploader:'Eve'   },
        { id:4, fileName:'invoice.docx',  uri:'#', caseNo:'LC-2025-0002', uploadedAt:'2025-07-23 09:10', uploader:'Bob'   }
      ]
      let rows = all.filter(r =>
        (!this.filters.keyword || r.fileName.toLowerCase().includes(this.filters.keyword.toLowerCase())) &&
        (!this.filters.caseNo   || r.caseNo.toLowerCase().includes(this.filters.caseNo.toLowerCase()))
      )
      const dir = this.orderType === 'ASC' ? 1 : -1
      rows = rows.sort((a,b) => (a[this.orderBy] > b[this.orderBy] ? dir : -dir))
      this.pagination.total = rows.length
      const start = (this.pagination.page-1) * this.pagination.pageSize
      this.tableData = rows.slice(start, start + this.pagination.pageSize)
    },
    resetFilters(){ this.filters = { keyword:'', caseNo:'' }; this.pagination.page = 1; this.fetchData() },
    onSelectionChange(v){ this.multipleSelection = v },
    onSortChange({prop, order}){
      if (!order) { this.orderBy='uploadedAt'; this.orderType='DESC' }
      else { this.orderBy = prop; this.orderType = order==='ascending'?'ASC':'DESC' }
      this.fetchData()
    },
    onPageSizeChange(s){ this.pagination.pageSize = s; this.pagination.page=1; this.fetchData() },
    onPageChange(p){ this.pagination.page = p; this.fetchData() },
    download(row){ this.$message.success(`Downloading ${row.fileName} (mock)`) },
    remove(row){ this.$message.success(`Deleted ${row.fileName} (mock)`) },
    batchDelete(){ this.$message.success(`Deleted ${this.multipleSelection.length} evidences (mock)`) }
  }
}
</script>
