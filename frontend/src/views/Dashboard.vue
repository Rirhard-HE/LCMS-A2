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
export default {
    name: 'Dashboard',
    data() {
        return {
            recentCases: [
                { id: 1, caseNumber: 'LC-2025-0001', title: 'Johnson v. State', category: 'Criminal', status: 'active' },
                { id: 2, caseNumber: 'LC-2025-0002', title: 'Acme LLC vs. Doe', category: 'Civil', status: 'closed' },
                { id: 3, caseNumber: 'LC-2025-0003', title: 'Green Card Appeal', category: 'Immigration', status: 'draft' }
            ],
            hearings: [
                { date: '2025-10-01' },
                { date: '2025-10-09' },
                { date: '2025-10-15' }
            ],
            calendarDate: new Date()
        }
    },
    methods: {
        goDetail(c) {
            this.$router.push(`/cases/${c.id}`)
        },
        hasHearing(day) {
            return this.hearings.some(h => h.date === day)
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
