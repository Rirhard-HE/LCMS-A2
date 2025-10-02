import { sampleCategories } from '@/data/sampleData'

const clone = data => JSON.parse(JSON.stringify(data))
const toLower = value => (value || '').toLowerCase()

const nextCategoryId = () => sampleCategories.reduce((max, item) => Math.max(max, item.id), 0) + 1

export default {
  list(params = {}) {
    const name = toLower(params.name)
    let records = sampleCategories
    if (name) {
      records = records.filter(item => toLower(item.name).includes(name))
    }
    const page = Number(params.page) > 0 ? Number(params.page) : 1
    const size = Number(params.size) > 0 ? Number(params.size) : records.length || 1
    const start = (page - 1) * size
    const paginated = records.slice(start, start + size)
    return Promise.resolve({
      records: clone(paginated),
      total: records.length,
      page,
      size
    })
  },

  get(id) {
    const targetId = Number(id)
    const record = sampleCategories.find(item => item.id === targetId) || null
    return Promise.resolve(record ? clone(record) : null)
  },

  create(data = {}) {
    const now = new Date().toISOString()
    const record = {
      id: nextCategoryId(),
      name: data.name || 'New Category',
      description: data.description || '',
      createdAt: now,
      updatedAt: now,
      owner: data.owner || 'Unassigned'
    }
    sampleCategories.push(record)
    return Promise.resolve(clone(record))
  },

  update(id, data = {}) {
    const targetId = Number(id)
    const existing = sampleCategories.find(item => item.id === targetId)
    if (!existing) {
      return Promise.resolve(null)
    }
    if (data.name != null) existing.name = data.name
    if (data.description != null) existing.description = data.description
    if (data.owner != null) existing.owner = data.owner
    existing.updatedAt = new Date().toISOString()
    return Promise.resolve(clone(existing))
  },

  remove(id) {
    const targetId = Number(id)
    const index = sampleCategories.findIndex(item => item.id === targetId)
    if (index >= 0) {
      sampleCategories.splice(index, 1)
    }
    return Promise.resolve({ success: true })
  }
}
