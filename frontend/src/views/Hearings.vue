<template>
  <div class="page">
    <header class="page_header">
      <div class="page_headline">
        <h1>Hearing Schedule</h1>
        <p class="page_subtitle">Stay ahead of every courtroom appearance.</p>
      </div>
      <div class="metrics_grid">
        <div class="metric_card">
          <span class="metric_label">Total Hearings</span>
          <span class="metric_value">{{ summary.total }}</span>
          <span class="metric_hint">across all cases</span>
        </div>
        <div class="metric_card">
          <span class="metric_label">This Week</span>
          <span class="metric_value">{{ summary.thisWeek }}</span>
          <span class="metric_hint">within 7 days</span>
        </div>
        <div class="metric_card">
          <span class="metric_label">Next Hearing</span>
          <span class="metric_value metric_value--text">{{ summary.nextLabel }}</span>
          <span class="metric_hint">at a glance</span>
        </div>
      </div>
    </header>

    <section class="card upcoming_card">
      <div class="card_header">
        <h2>Next on the Docket</h2>
        <span class="card_hint">Details for the closest upcoming hearing</span>
      </div>
      <div v-if="nextHearing" class="upcoming_body">
        <div class="upcoming_date">
          <span class="upcoming_weekday">{{ formatWeekday(nextHearing) }}</span>
          <span class="upcoming_day">{{ formatDate(nextHearing) }}</span>
          <span class="upcoming_time">{{ formatTime(nextHearing) }}</span>
        </div>
        <div class="upcoming_meta">
          <h3 class="upcoming_title">{{ nextHearing.title }}</h3>
          <p class="upcoming_case">Case {{ nextHearing.caseNumber }}</p>
          <div class="upcoming_actions">
            <el-button type="text" class="link_button" @click="goCase(nextHearing)">View case</el-button>
            <el-button type="primary" round @click="notify(nextHearing)">Notify me</el-button>
          </div>
        </div>
      </div>
      <div v-else class="empty_state">
        <span>No hearings scheduled yet.</span>
      </div>
    </section>

    <section class="card schedule_card">
      <div class="card_header">
        <h2>All Hearings</h2>
        <span class="card_hint">Showing {{ visibleHearings.length }} of {{ allHearings.length }}</span>
      </div>
      <div class="hearing_grid">
        <article v-for="item in visibleHearings" :key="item.id" class="hearing_card">
          <header class="hearing_header">
            <span class="hearing_weekday">{{ formatWeekday(item) }}</span>
            <span class="hearing_date">{{ formatDate(item) }}</span>
          </header>
          <div class="hearing_body">
            <h3 class="hearing_title">{{ item.title }}</h3>
            <p class="hearing_case">Case {{ item.caseNumber }}</p>
            <p class="hearing_time">{{ formatTime(item) }}</p>
          </div>
          <footer class="hearing_actions">
            <el-button type="text" class="link_button" @click="goCase(item)">Open case</el-button>
            <el-button type="primary" round @click="notify(item)">Notify</el-button>
          </footer>
        </article>
      </div>
      <div class="card_footer" v-if="visibleHearings.length < allHearings.length">
        <el-button type="primary" round icon="el-icon-more" @click="loadMore">Load more</el-button>
      </div>
    </section>
  </div>
</template>

<script>
const sampleHearings = [
  {
    id: 301,
    caseNumber: 'CASE-2025-072',
    title: 'Anderson vs City Council',
    hearingAt: '2025-10-03T10:00:00'
  },
  {
    id: 302,
    caseNumber: 'CASE-2025-108',
    title: 'State vs Porter Manufacturing',
    hearingAt: '2025-10-06T14:30:00'
  },
  {
    id: 303,
    caseNumber: 'CASE-2025-211',
    title: 'Lopez vs Evergreen Homes',
    hearingAt: '2025-10-09T09:15:00'
  },
  {
    id: 304,
    caseNumber: 'CASE-2025-317',
    title: 'In re Estate of Carter',
    hearingAt: '2025-10-12T11:00:00'
  },
  {
    id: 305,
    caseNumber: 'CASE-2025-455',
    title: 'City of Riverton vs Blake Logistics',
    hearingAt: '2025-10-15T13:45:00'
  },
  {
    id: 306,
    caseNumber: 'CASE-2025-512',
    title: 'Hernandez vs Central Medical',
    hearingAt: '2025-10-18T10:30:00'
  }
]

const weekdayFormatter = new Intl.DateTimeFormat('en-US', { weekday: 'short' })
const dateFormatter = new Intl.DateTimeFormat('en-US', { month: 'short', day: 'numeric', year: 'numeric' })
const timeFormatter = new Intl.DateTimeFormat('en-US', { hour: 'numeric', minute: '2-digit' })
const fullFormatter = new Intl.DateTimeFormat('en-US', { month: 'long', day: 'numeric', year: 'numeric', hour: 'numeric', minute: '2-digit' })

export default {
  name: 'HearingListCards',
  data() {
    return {
      loading: false,
      allHearings: [],
      visibleHearings: [],
      step: 6,
      summary: {
        total: 0,
        thisWeek: 0,
        nextLabel: '?'
      },
      nextHearing: null
    }
  },
  created() {
    this.fetchHearings()
  },
  methods: {
    fetchHearings() {
      this.loading = true
      const records = sampleHearings
      const hearings = records
        .map(item => ({
          id: item.id,
          caseNumber: item.caseNumber || `Case #${item.id}`,
          title: item.title || 'Untitled case',
          hearingAt: item.hearingAt,
          date: item.hearingAt,
          time: item.hearingAt
        }))
        .map(entry => ({
          ...entry,
          dateObj: entry.hearingAt ? new Date(entry.hearingAt) : null
        }))
        .filter(entry => entry.dateObj && !Number.isNaN(entry.dateObj.getTime()))
        .sort((a, b) => a.dateObj - b.dateObj)

      this.allHearings = hearings
      this.visibleHearings = hearings.slice(0, this.step)
      this.computeSummary()
      this.loading = false
    },
    computeSummary() {
      const now = new Date()
      const weekAhead = new Date(now)
      weekAhead.setDate(now.getDate() + 7)

      const thisWeek = this.allHearings.filter(item => item.dateObj >= now && item.dateObj <= weekAhead).length
      const next = this.allHearings.find(item => item.dateObj >= now) || null

      this.summary = {
        total: this.allHearings.length,
        thisWeek,
        nextLabel: next ? fullFormatter.format(next.dateObj) : '?'
      }
      this.nextHearing = next
    },
    loadMore() {
      const nextCount = this.visibleHearings.length + this.step
      this.visibleHearings = this.allHearings.slice(0, nextCount)
    },
    goCase(item) {
      this.$router.push(`/cases/${item.id}`)
    },
    notify(item) {
      const when = item.dateObj ? fullFormatter.format(item.dateObj) : 'the scheduled time'
      this.$message.success(`You will be notified for the hearing on ${when}`)
    },
    formatWeekday(item) {
      return item.dateObj ? weekdayFormatter.format(item.dateObj) : '?'
    },
    formatDate(item) {
      return item.dateObj ? dateFormatter.format(item.dateObj) : '?'
    },
    formatTime(item) {
      return item.dateObj ? timeFormatter.format(item.dateObj) : '?'
    }
  }
}
</script>

<style scoped>
/* existing styles preserved */
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

.upcoming_body {
  display: flex;
  flex-wrap: wrap;
  gap: 28px;
  align-items: center;
}

.upcoming_date {
  display: flex;
  flex-direction: column;
  gap: 4px;
  min-width: 180px;
  color: #4f46e5;
  font-weight: 600;
}

.upcoming_weekday {
  text-transform: uppercase;
  font-size: 12px;
  letter-spacing: 0.08em;
}

.upcoming_day {
  font-size: 26px;
}

.upcoming_time {
  font-size: 14px;
  color: #6b7280;
  font-weight: 500;
}

.upcoming_meta {
  display: flex;
  flex-direction: column;
  gap: 12px;
  flex: 1;
}

.upcoming_title {
  margin: 0;
  font-size: 20px;
  font-weight: 700;
  color: #111827;
}

.upcoming_case {
  margin: 0;
  color: #64748b;
  font-size: 14px;
}

.upcoming_actions {
  display: flex;
  align-items: center;
  gap: 12px;
}

.empty_state {
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 24px;
  color: #6b7280;
}

.schedule_card {
  gap: 24px;
}

.hearing_grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(260px, 1fr));
  gap: 20px;
}

.hearing_card {
  border: 1px solid #e5e7eb;
  border-radius: 18px;
  padding: 20px;
  display: flex;
  flex-direction: column;
  gap: 14px;
  background: #fbfcff;
  transition: transform 0.2s ease, box-shadow 0.2s ease;
}

.hearing_card:hover {
  transform: translateY(-4px);
  box-shadow: 0 18px 36px rgba(79, 70, 229, 0.14);
  border-color: rgba(79, 70, 229, 0.35);
}

.hearing_header {
  display: flex;
  justify-content: space-between;
  align-items: baseline;
  font-weight: 600;
  color: #4f46e5;
}

.hearing_weekday {
  text-transform: uppercase;
  font-size: 12px;
  letter-spacing: 0.08em;
}

.hearing_date {
  font-size: 14px;
  color: #6b7280;
}

.hearing_title {
  margin: 0;
  font-size: 16px;
  font-weight: 700;
  color: #111827;
}

.hearing_case {
  margin: 0;
  color: #64748b;
  font-size: 13px;
}

.hearing_time {
  margin: 0;
  color: #1f2937;
  font-weight: 600;
}

.hearing_actions {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.link_button {
  color: #4f46e5;
  font-weight: 600;
}

.card_footer {
  display: flex;
  justify-content: center;
}

@media (max-width: 1024px) {
  .page {
    padding: 24px;
  }
  .page_header {
    flex-direction: column;
  }
  .upcoming_body {
    flex-direction: column;
    align-items: flex-start;
  }
  .hearing_actions {
    flex-direction: column;
    align-items: flex-start;
    gap: 8px;
  }
}
</style>


