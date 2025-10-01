<template>
  <div class="hearing_container">
    <el-row :gutter="20" v-loading="loading">
      <el-col
        v-for="item in showData"
        :key="item.id"
        :xs="24"
        :sm="12"
        :md="8"
      >
        <el-card class="hearing_card" shadow="hover">
          <div class="hearing_time">
            Hearing Time:
            <span class="time_value">{{ item.date }} {{ item.time }}</span>
          </div>

          <div class="case_title">
            <a href="javascript:void(0)" @click.prevent="goCase(item)">
              {{ item.title }}
            </a>
          </div>

          <div class="case_no">Case No. {{ item.caseNo }}</div>

          <div class="actions">
            <el-button type="danger" round size="medium" @click="notify(item)">
              Notify Me
            </el-button>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <div class="load_more" v-if="showData.length < allData.length">
      <el-button type="primary" round @click="loadMore">Load More</el-button>
    </div>
  </div>
</template>

<script>
import { listCases } from '@/api/cases'

export default {
  name: 'HearingListCards',
  data() {
    return {
      loading: false,
      allData: [],
      showData: [],
      step: 6
    }
  },
  created() {
    this.fetchHearings()
  },
  methods: {
  async fetchHearings() {
    this.loading = true
    try {
      var params = { pageNo: 1, pageSize: 200 }
      var res = await listCases(params)
      var arr = res
      if (!Array.isArray(arr)) {
        if (arr && Array.isArray(arr.data)) arr = arr.data
        else if (arr && Array.isArray(arr.records)) arr = arr.records
        else arr = []
      }

      var mapped = []
      for (var i = 0; i < arr.length; i++) {
        var r = arr[i]
        if (!r || !r.hearingAt) continue
        var s = String(r.hearingAt)
        var tIndex = s.indexOf('T')
        var date = s.length >= 10 ? s.slice(0, 10) : s
        var time = ''
        if (tIndex > 0) time = s.slice(tIndex + 1, tIndex + 6)
        else if (s.length >= 16) time = s.slice(11, 16)

        mapped.push({
          id: r.id,
          caseNo: r.caseNum || r.caseNumber || '',
          title: r.title || '',
          hearingAt: s,
          date: date,
          time: time
        })
      }

      mapped.sort(function (a, b) {
        return a.hearingAt < b.hearingAt ? -1 : (a.hearingAt > b.hearingAt ? 1 : 0)
      })

      this.allData = mapped
      this.showData = mapped.slice(0, this.step)
    } catch (e) {
      this.$message.error('Failed to load hearings')
    } finally {
      this.loading = false
    }
  },

  loadMore() {
    var next = this.showData.length + this.step
    this.showData = this.allData.slice(0, next)
  },

  goCase(item) {
    this.$router.push('/cases/' + item.id)
  },

  notify(item) {
    this.$message.success('You will be notified for hearing of ' + item.caseNo)
  }
}
}
</script>

<style scoped>
.hearing_container { padding: 30px; }
.hearing_card {
  border-radius: 20px;
  padding: 24px;
  margin-bottom: 20px;
  color: #fff;
  background: linear-gradient(135deg, rgba(99, 102, 241, 0.6), rgba(16, 185, 129, 0.6));
  backdrop-filter: blur(12px);
  -webkit-backdrop-filter: blur(12px);
  border: 1px solid rgba(255, 255, 255, 0.2);
  opacity: 0.9;
}
.hearing_time { font-size: 20px; font-weight: 700; margin-bottom: 15px; }
.time_value { color: #ffeb3b; }
.case_title { font-size: 18px; font-weight: 600; margin-bottom: 8px; }
.case_title a { color: #fff; text-decoration: underline; cursor: pointer; }
.case_no { font-size: 15px; margin-bottom: 20px; color: #ddd; }
.actions { text-align: right; }
.load_more { text-align: center; margin-top: 20px; }
</style>
