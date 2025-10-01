<template>
    <div class="dashboard_container">
        <div class="left">
            <el-card class="recent_cases" shadow="hover">
                <h3 class="card_title">Recent Cases</h3>
                <div class="case_list">
                    <el-card v-for="c in recentCases" :key="c.id" class="case_item" shadow="never">
                        <div class="case_title">
                            <span class="link" @click="goDetail(c)">{{ c.title }}</span>
                        </div>
                        <div class="case_info">
                            <span class="case_no">No. {{ c.caseNumber }}</span>
                            <span class="case_category">{{ c.category }}</span>
                            <span :class="['status', c.status]">{{ c.status }}</span>
                        </div>
                    </el-card>
                </div>
            </el-card>
        </div>

        <div class="right">
            <el-card class="hearing_calendar" shadow="hover">
                <h3 class="card_title">Hearing Calendar</h3>
                <el-calendar v-model="calendarDate">
                    <template slot="dateCell" slot-scope="{ date, data }">
                        <div class="calendar-cell" :class="{ hearing: hasHearing(data.day) }">
                            {{ data.day.split('-').slice(2).join('') }}
                        </div>
                    </template>
                </el-calendar>
            </el-card>
        </div>
    </div>
</template>

<script>
import { listCases } from '@/api/cases'

export default {
  name: 'Dashboard',
  data() {
    return {
      recentCases: [],
      hearings: [],
      calendarDate: new Date(),
      loading: false
    }
  },
  created() {
    this.fetchRecentCases()
  },
  methods: {
    async fetchRecentCases() {
      this.loading = true
      try {
        var res = await listCases({ pageNo: 1, pageSize: 3 })
        var arr = res
        if (!Array.isArray(arr)) {
          if (arr && Array.isArray(arr.data)) arr = arr.data
          else if (arr && Array.isArray(arr.records)) arr = arr.records
          else arr = []
        }

        var mapped = []
        var hearingDays = {}
        for (var i = 0; i < arr.length; i++) {
          var row = arr[i]
          var item = this.mapRow(row)
          mapped.push(item)
          console.log(mapped)
          if (row.hearingAt) {
            var day = this.onlyDate(row.hearingAt)
            if (day) hearingDays[day] = true
          }
        }
        this.recentCases = mapped

        var hs = []
        for (var d in hearingDays) {
          if (hearingDays.hasOwnProperty(d)) hs.push({ date: d })
        }
        this.hearings = hs
      } catch (e) {
        this.$message.error('Failed to load recent cases')
      } finally {
        this.loading = false
      }
    },

    mapRow: function(row) {
      var categoryText = '—'
      if (row.categories && row.categories.length) {
        categoryText = row.categories.join(', ')
      }

      var statusText = 'draft'
      if (row.status === 'OPEN') statusText = 'active'
      else if (row.status === 'CLOSED') statusText = 'closed'

      var caseNo = ''
      if (row.caseNum) caseNo = row.caseNum
      if (!caseNo && row.caseNumber) caseNo = row.caseNumber

      var hearingText = '—'
      if (row.hearingAt) {
        hearingText = row.hearingAt.replace('T', ' ').slice(0, 16)
      }

      var titleText = ''
      if (row.title) titleText = row.title

      return {
        id: row.id,
        caseNumber: caseNo,
        title: titleText,
        category: categoryText,
        status: statusText,
        hearingAt: hearingText
      }
    },

    onlyDate: function(iso) {
      if (!iso) return ''
      var s = String(iso)
      var t = s.indexOf('T')
      if (t > 0) return s.substring(0, t)
      if (s.length >= 10) return s.substring(0, 10)
      return ''
    },

    goDetail(c) {
      this.$router.push(`/cases/${c.id}`)
    },

    hasHearing(day) {
      for (var i = 0; i < this.hearings.length; i++) {
        if (this.hearings[i].date === day) return true
      }
      return false
    }
  }
}
</script>



<style scoped>
.dashboard_container {
    display: flex;
    gap: 20px;
    padding: 16px;
}

.left {
    flex: 1;
}

.right {
    flex: 1;
}

.card_title {
    font-weight: 700;
    margin-bottom: 12px;
}

.case_list {
    display: flex;
    flex-direction: column;
    gap: 12px;
}

.case_item {
    border-radius: 10px;
    padding: 12px;
    background: rgba(255, 255, 255, 0.8);
}

.case_title {
    font-weight: 600;
    font-size: 16px;
    margin-bottom: 6px;
}

.case_info {
    display: flex;
    gap: 12px;
    font-size: 13px;
    color: #475569;
}

.status.active {
    color: #16a34a;
    font-weight: 600;
}

.status.closed {
    color: #ef4444;
    font-weight: 600;
}

.status.draft {
    color: #f59e0b;
    font-weight: 600;
}

.link {
    color: #2563eb;
    cursor: pointer;
}

.link:hover {
    text-decoration: underline;
}

.hearing_calendar {
    border-radius: 12px;
}

.calendar-cell {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 6px;
  transition: background 0.3s;
}

.calendar-cell.hearing {
  background: rgba(16, 185, 129, 0.3); 
  font-weight: bold;
  color: #065f46;
}

.calendar-cell.hearing:hover {
  background: rgba(16, 185, 129, 0.6);
  cursor: pointer;
}

.dot {
    width: 6px;
    height: 6px;
    background: #f43f5e;
    border-radius: 50%;
    position: absolute;
    bottom: 4px;
}
</style>
