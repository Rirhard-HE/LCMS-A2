<template>
  <div class="hearing_container">
    <el-row :gutter="20">
      <el-col
        v-for="item in showData"
        :key="item.id"
        :xs="24"
        :sm="12"
        :md="8"
      >
        <el-card class="hearing_card" shadow="hover">
          <!-- Hearing Time -->
          <div class="hearing_time">
            Hearing Time:
            <span class="time_value">{{ item.date }} {{ item.time }}</span>
          </div>

          <!-- Case Title -->
          <div class="case_title">
            <a @click.prevent="goCase(item)" href="javascript:void(0)">
              {{ item.title }}
            </a>
          </div>

          <!-- Case No -->
          <div class="case_no">Case No. {{ item.caseNo }}</div>

          <!-- Notify -->
          <div class="actions">
            <el-button
              type="danger"
              round
              size="medium"
              @click="notify(item)"
            >
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
export default {
  name: 'HearingListCards',
  data() {
    return {
      allData: [
        { id: 1, caseNo: 'LC-2025-0001', title: 'Johnson v. State',  date: '2025-10-01', time: '10:00' },
        { id: 2, caseNo: 'LC-2025-0002', title: 'Acme LLC vs. Doe',  date: '2025-10-03', time: '14:30' },
        { id: 3, caseNo: 'LC-2025-0003', title: 'Green Card Appeal', date: '2025-10-07', time: '09:15' },
        { id: 4, caseNo: 'LC-2025-0004', title: 'Smith Divorce',     date: '2025-10-09', time: '11:00' },
        { id: 5, caseNo: 'LC-2025-0005', title: 'Contract Breach',   date: '2025-10-12', time: '15:30' },
        { id: 6, caseNo: 'LC-2025-0006', title: 'Fraud Hearing',     date: '2025-10-15', time: '13:00' }
      ],
      showData: [],
      step: 3
    }
  },
  created() {
    this.showData = this.allData.slice(0, this.step)
  },
  methods: {
    loadMore() {
      const next = this.showData.length + this.step
      this.showData = this.allData.slice(0, next)
    },
    goCase(item) {
      this.$router.push(`/cases/${item.caseNo}`)
    },
    notify(item) {
      this.$message.success(`You will be notified for hearing of ${item.caseNo}`)
    }
  }
}
</script>

<style scoped>
.hearing_container {
  padding: 30px;
}

/* Card style */
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

.hearing_time {
  font-size: 20px;
  font-weight: 700;
  margin-bottom: 15px;
}
.time_value {
  color: #ffeb3b;
}

.case_title {
  font-size: 18px;
  font-weight: 600;
  margin-bottom: 8px;
}
.case_title a {
  color: #fff;
  text-decoration: underline;
  cursor: pointer;
}

.case_no {
  font-size: 15px;
  margin-bottom: 20px;
  color: #ddd;
}

.actions {
  text-align: right;
}

.load_more {
  text-align: center;
  margin-top: 20px;
}
</style>
