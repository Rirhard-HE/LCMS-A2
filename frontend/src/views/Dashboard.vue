<template>
    <div class="dashboard">
        <section class="overview_grid">
            <el-card
                v-for="stat in overviewStats"
                :key="stat.id"
                class="overview_card"
                shadow="hover"
            >
                <div class="overview_header">
                    <span class="overview_label">{{ stat.label }}</span>
                    <el-tag v-if="stat.badge" :type="stat.badgeType" size="small">{{ stat.badge }}</el-tag>
                </div>
                <div class="overview_value">{{ stat.value }}</div>
                <div class="overview_meta">
                    <span :class="['overview_change', changeClass(stat.change)]">{{ stat.change }}</span>
                    <span class="overview_hint">{{ stat.hint }}</span>
                </div>
            </el-card>
        </section>

        <section class="content_grid">
            <div class="column left">
                <el-card class="panel recent_panel" shadow="hover">
                    <div class="panel_header">
                        <h3>Recent Cases</h3>
                        <el-button type="text" @click="$router.push('/cases')">View all</el-button>
                    </div>
                    <div class="case_list">
                        <div
                            v-for="caseItem in recentCases"
                            :key="caseItem.id"
                            class="case_row"
                        >
                            <div class="case_title" @click="goDetail(caseItem)">{{ caseItem.title }}</div>
                            <div class="case_meta">
                                <span class="case_number">No. {{ caseItem.caseNumber }}</span>
                                <span class="case_category">{{ caseItem.category }}</span>
                                <el-tag :type="statusType(caseItem.status)" size="mini" effect="plain">{{ formatStatus(caseItem.status) }}</el-tag>
                            </div>
                            <div class="case_deadline">
                                <span>{{ caseItem.nextStep }}</span>
                                <span class="case_due">Due {{ caseItem.due }}</span>
                            </div>
                        </div>
                    </div>
                </el-card>

                <el-card class="panel workload_panel" shadow="hover">
                    <div class="panel_header">
                        <h3>Team Workload</h3>
                        <span class="panel_hint">Load vs. band target</span>
                    </div>
                    <div class="workload_list">
                        <div
                            v-for="area in practiceAreas"
                            :key="area.name"
                            class="workload_row"
                        >
                            <div class="workload_meta">
                                <span class="workload_name">{{ area.name }}</span>
                                <span class="workload_trend" :class="{ positive: area.trend.startsWith('+'), negative: area.trend.startsWith('-') }">{{ area.trend }}</span>
                            </div>
                            <el-progress :percentage="area.percentage" :stroke-width="8" :color="area.color" />
                        </div>
                    </div>
                    <div class="task_list">
                        <div
                            v-for="task in priorityTasks"
                            :key="task.id"
                            class="task_row"
                        >
                            <div>
                                <div class="task_title">{{ task.title }}</div>
                                <div class="task_meta">
                                    <span>{{ task.owner }}</span>
                                    <span>Due {{ task.due }}</span>
                                </div>
                            </div>
                            <el-tag :type="task.priority === 'High' ? 'danger' : task.priority === 'Medium' ? 'warning' : 'info'" size="mini">{{ task.priority }}</el-tag>
                        </div>
                    </div>
                </el-card>
            </div>

            <div class="column right">
                <el-card class="panel hearings_panel" shadow="hover">
                    <div class="panel_header">
                        <h3>Upcoming Hearings</h3>
                        <el-button type="text" @click="$router.push('/calendar')">Calendar</el-button>
                    </div>
                    <div class="hearing_list">
                        <div
                            v-for="hearing in upcomingHearings"
                            :key="hearing.id"
                            class="hearing_row"
                        >
                            <div class="hearing_date">
                                <span class="hearing_day">{{ formatDay(hearing.date) }}</span>
                                <span class="hearing_weekday">{{ formatWeekday(hearing.date) }}</span>
                            </div>
                            <div class="hearing_detail">
                                <div class="hearing_title">{{ hearing.title }}</div>
                                <div class="hearing_meta">
                                    <span>{{ hearing.time }}</span>
                                    <span>{{ hearing.location }}</span>
                                    <span>{{ hearing.judge }}</span>
                                </div>
                            </div>
                            <el-tag type="info" effect="dark" size="mini">{{ hearing.stage }}</el-tag>
                        </div>
                    </div>
                </el-card>

                            <el-card class="panel calendar_panel" shadow="hover">
                    <div class="calendar_header">
                        <div class="calendar_title">
                            <h3>Hearing Calendar</h3>
                            <span class="panel_hint">Keep track of upcoming court appearances</span>
                        </div>
                        <div class="calendar_controls">
                            <el-button circle size="mini" icon="el-icon-arrow-left" @click="changeMonth(-1)"></el-button>
                            <span class="calendar_month">{{ calendarMonth }}</span>
                            <el-button circle size="mini" icon="el-icon-arrow-right" @click="changeMonth(1)"></el-button>
                        </div>
                    </div>
                    <el-calendar v-model="calendarDate">
                        <template slot="dateCell" slot-scope="{ data }">
                            <div
                                :class="[
                                    'calendar_cell',
                                    {
                                        hearing: data.type === 'current-month' && hasHearing(data.day),
                                        today: isToday(data.day),
                                        weekend: isWeekend(data.day),
                                        current: data.type === 'current-month',
                                        'other-month': data.type !== 'current-month'
                                    }
                                ]"
                            >
                                <span class="calendar_day">{{ data.day.split('-').slice(2).join('') }}</span>
                                <span v-if="hasHearing(data.day)" class="calendar_dot"></span>
                            </div>
                        </template>
                    </el-calendar>
                </el-card>
            </div>
        </section>
    </div>
</template>

<script>
const formatter = new Intl.DateTimeFormat('en-US', { month: 'short', day: 'numeric' })
const weekdayFormatter = new Intl.DateTimeFormat('en-US', { weekday: 'short' })
const monthFormatter = new Intl.DateTimeFormat('en-US', { month: 'long', year: 'numeric' })
const toLocalDate = value => new Date(`${value}T00:00:00`)

export default {
    name: 'Dashboard',
    data() {
        return {
            overviewStats: [
                { id: 'active', label: 'Active Cases', value: 48, change: '+8.3%', hint: '8 newly assigned this week', badge: 'On track', badgeType: 'success' },
                { id: 'hearings', label: 'Hearings this month', value: 12, change: '+2.0%', hint: '3 rescheduled items pending', badge: 'Busy', badgeType: 'warning' },
                { id: 'tasks', label: 'Tasks due today', value: 5, change: '-1.2%', hint: '2 critical filings completed', badge: '', badgeType: '' },
                { id: 'sla', label: 'SLA compliance', value: '96%', change: '+3.5%', hint: 'Up from 92% last cycle', badge: 'Great', badgeType: 'success' }
            ],
            recentCases: [
                { id: 1, caseNumber: 'LC-2025-0001', title: 'Johnson v. State', category: 'Criminal', status: 'active', nextStep: 'Draft witness outline', due: 'Oct 04' },
                { id: 2, caseNumber: 'LC-2025-0005', title: 'Acme LLC vs. Doe', category: 'Civil', status: 'pending', nextStep: 'Collect discovery docs', due: 'Oct 06' },
                { id: 3, caseNumber: 'LC-2025-0007', title: 'Green Card Appeal', category: 'Immigration', status: 'draft', nextStep: 'Finalize brief', due: 'Oct 09' },
                { id: 4, caseNumber: 'LC-2025-0010', title: 'State v. Patel', category: 'Criminal', status: 'active', nextStep: 'Pre-trial conference', due: 'Oct 12' }
            ],
            practiceAreas: [
                { name: 'Criminal Defense', percentage: 68, trend: '+3.1%', color: '#2563eb' },
                { name: 'Civil Litigation', percentage: 52, trend: '-1.8%', color: '#f59e0b' },
                { name: 'Immigration', percentage: 35, trend: '+0.4%', color: '#10b981' }
            ],
            priorityTasks: [
                { id: 't1', title: 'File motion for expedited discovery', owner: 'A. Rivera', due: 'Oct 03', priority: 'High' },
                { id: 't2', title: 'Confirm expert availability', owner: 'S. Morgan', due: 'Oct 05', priority: 'Medium' },
                { id: 't3', title: 'Send settlement questionnaire', owner: 'J. Chen', due: 'Oct 06', priority: 'Low' }
            ],
            upcomingHearings: [
                { id: 'h1', date: '2025-10-01', title: 'Johnson v. State – Status Conference', time: '09:30 AM', location: 'Courtroom 2A', judge: 'Judge Lee', stage: 'Status' },
                { id: 'h2', date: '2025-10-09', title: 'State v. Patel – Pre-trial', time: '11:00 AM', location: 'Courtroom 4C', judge: 'Judge Singh', stage: 'Pre-trial' },
                { id: 'h3', date: '2025-10-15', title: 'Acme LLC vs. Doe – Mediation', time: '02:00 PM', location: 'Mediation Suite', judge: 'Mediator Brown', stage: 'Mediation' }
            ],
            calendarDate: new Date()
        }
    },
    computed: {
        calendarMonth() {
            return monthFormatter.format(this.calendarDate)
        }
    },
    methods: {
        goDetail(caseItem) {
            this.$router.push(`/cases/${caseItem.id}`)
        },
        navigate(to) {
            if (to) {
                this.$router.push(to)
            }
        },
        hasHearing(day) {
            return this.upcomingHearings.some(h => h.date === day)
        },
        changeMonth(step) {
            const next = new Date(this.calendarDate)
            next.setMonth(next.getMonth() + step)
            this.calendarDate = next
        },
        isToday(day) {
            const value = toLocalDate(day)
            const today = new Date()
            return value.getFullYear() === today.getFullYear() &&
                value.getMonth() === today.getMonth() &&
                value.getDate() === today.getDate()
        },
        isWeekend(day) {
            const value = toLocalDate(day)
            const weekday = value.getDay()
            return weekday === 0 || weekday === 6
        },
        formatDay(date) {
            return formatter.format(toLocalDate(date))
        },
        formatWeekday(date) {
            return weekdayFormatter.format(toLocalDate(date))
        },
        statusType(status) {
            const map = {
                active: 'success',
                pending: 'warning',
                draft: 'info',
                closed: 'info'
            }
            return map[status] || 'info'
        },
        formatStatus(status) {
            return status.charAt(0).toUpperCase() + status.slice(1)
        },
        changeClass(change) {
            return change.startsWith('-') ? 'negative' : 'positive'
        }
    }
}
</script>

<style scoped>
.dashboard {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  gap: 32px;
  padding: 32px 36px 48px;
  background: linear-gradient(180deg, #eef2ff 0%, #f8fafc 100%);
}

.overview_grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 20px;
}

.overview_card {
  border-radius: 20px;
  border: none;
  background: #ffffff;
  box-shadow: 0 16px 32px rgba(79, 70, 229, 0.16);
  padding: 22px 24px;
}

.overview_header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  color: #6b7280;
  font-size: 14px;
}

.overview_label {
  font-weight: 600;
}

.overview_value {
  font-size: 34px;
  font-weight: 700;
  color: #111827;
  letter-spacing: -0.5px;
}

.overview_meta {
  display: flex;
  gap: 12px;
  margin-top: 12px;
  font-size: 13px;
  color: #9ca3af;
}

.overview_change {
  font-weight: 600;
}

.overview_change.positive {
  color: #16a34a;
}

.overview_change.negative {
  color: #dc2626;
}

.content_grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(360px, 1fr));
  gap: 24px;
}

.column {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.panel {
  border-radius: 22px;
  border: none;
  padding: 24px 26px;
  background: #ffffff;
  box-shadow: 0 20px 40px rgba(15, 23, 42, 0.08);
}

.panel_header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 20px;
}

.panel_header h3 {
  margin: 0;
  font-size: 18px;
  font-weight: 600;
  color: #111827;
}

.panel_header .el-button {
  padding: 0;
  color: #6366f1;
  font-weight: 500;
}

.panel_hint {
  font-size: 12px;
  color: #9ca3af;
}

.case_list,
.workload_list,
.task_list,
.hearing_list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.case_row {
  padding: 16px 18px;
  border-radius: 16px;
  border: 1px solid #e5e7eb;
  background: #fbfcff;
  transition: border-color 0.2s ease, box-shadow 0.2s ease;
}

.case_row:hover {
  border-color: #c7d2fe;
  box-shadow: 0 12px 24px rgba(99, 102, 241, 0.12);
  cursor: pointer;
}

.case_title {
  font-size: 16px;
  font-weight: 600;
  color: #1f2937;
  margin-bottom: 8px;
}

.case_meta {
  display: flex;
  align-items: center;
  gap: 12px;
  font-size: 13px;
  color: #6b7280;
}

.case_deadline {
  display: flex;
  justify-content: space-between;
  margin-top: 10px;
  font-size: 13px;
  color: #475569;
}

.case_due {
  font-weight: 600;
  color: #4f46e5;
}

.workload_row {
  display: flex;
  flex-direction: column;
  gap: 8px;
  padding: 12px 0;
  border-bottom: 1px solid #eef2ff;
}

.workload_row:last-child {
  border-bottom: none;
}

.workload_meta {
  display: flex;
  justify-content: space-between;
  font-size: 14px;
  color: #374151;
}

.workload_trend {
  font-size: 13px;
  font-weight: 600;
}

.workload_trend.positive {
  color: #22c55e;
}

.workload_trend.negative {
  color: #ef4444;
}

.task_row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 12px 0;
  border-top: 1px solid #eef2ff;
}

.task_row:first-child {
  border-top: none;
}

.task_title {
  font-weight: 600;
  color: #1f2937;
}

.task_meta {
  display: flex;
  gap: 12px;
  font-size: 12px;
  color: #6b7280;
}

.hearing_row {
  display: grid;
  grid-template-columns: 120px 1fr auto;
  gap: 20px;
  padding: 16px 18px;
  border-radius: 16px;
  border: 1px solid #e5e7eb;
  background: #ffffff;
}

.hearing_date {
  display: flex;
  flex-direction: column;
  gap: 4px;
  font-weight: 600;
  color: #1f2937;
}

.hearing_weekday {
  font-size: 12px;
  color: #94a3b8;
  text-transform: uppercase;
}

.hearing_detail {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.hearing_title {
  font-weight: 600;
  color: #111827;
}

.hearing_meta {
  display: flex;
  gap: 12px;
  font-size: 12px;
  color: #6b7280;
}



.quick_links {
  width: 100%;
}


.quick_card:hover {
  transform: translateY(-4px);
  box-shadow: 0 18px 40px rgba(99, 102, 241, 0.15);
}

@media (max-width: 1024px) {
  .dashboard {
    padding: 24px;
  }

  .content_grid {
    grid-template-columns: 1fr;
  }

  .hearing_row {
    grid-template-columns: minmax(0, 1fr);
    grid-template-rows: auto auto auto;
  }
}
.calendar_panel {
  padding: 0;
  overflow: hidden;
}

.calendar_header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 24px 26px 12px;
}

.calendar_title {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.calendar_title h3 {
  margin: 0;
  font-size: 18px;
  font-weight: 600;
  color: #111827;
}

.calendar_controls {
  display: flex;
  align-items: center;
  gap: 12px;
}

.calendar_controls .el-button {
  min-width: 32px;

  box-shadow: 0 6px 16px rgba(79, 70, 229, 0.18);
  border: none;
  color: #4f46e5;
}

.calendar_controls .el-button:hover {
  color: #312e81;
}

.calendar_month {
  font-weight: 600;
  color: #1f2937;
  min-width: 140px;
  text-align: center;
}

.calendar_panel ::v-deep(.el-calendar__header) {
  display: none;
}

.calendar_title .panel_hint {
  margin-top: 2px;
}

.calendar_panel ::v-deep(.el-calendar__body) {
  padding: 0 26px 26px;
}

.calendar_panel ::v-deep(.el-calendar__row) {
  margin-bottom: 6px;
}

.calendar_cell {
  position: relative;
  width: 100%;
  height: 72px;
  display: flex;
  align-items: flex-start;
  justify-content: flex-end;
  padding: 10px;
  border-radius: 12px;
  transition: background 0.2s ease, border-color 0.2s ease, transform 0.2s ease;
}

.calendar_cell.weekend {
  background: rgba(148, 163, 184, 0.12);
}

.calendar_cell.current {
  background: rgba(99, 102, 241, 0.08);
}

.calendar_cell.other-month {
  opacity: 0.45;
  color: #9ca3af;
  background: transparent;
  border: none;
}

.calendar_cell.other-month:hover {
  transform: none;
  background: transparent;
}

.calendar_cell.today {
  border: 1px solid rgba(79, 70, 229, 0.45);
  background: rgba(79, 70, 229, 0.08);
}

.calendar_cell.hearing {
  background: rgba(99, 102, 241, 0.12);
  border: 1px solid rgba(99, 102, 241, 0.4);
}

.calendar_cell.hearing.today {
  background: rgba(99, 102, 241, 0.18);
}

.calendar_cell:hover {
  transform: translateY(-2px);
  background: rgba(99, 102, 241, 0.2);
}

.calendar_day {
  font-weight: 600;
  color: #1f2937;
}

.calendar_dot {
  position: absolute;
  bottom: 10px;
  left: 50%;
  transform: translateX(-50%);
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background: #6366f1;
}
</style>


























