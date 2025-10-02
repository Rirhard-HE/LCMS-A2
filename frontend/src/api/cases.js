import { sampleCases, sampleCategories } from '@/data/sampleData'

const clone = data => JSON.parse(JSON.stringify(data))
const findCategoryName = id => {
  const record = sampleCategories.find(item => item.id === Number(id))
  return record ? record.name : undefined
}

const ensureCaseCategories = record => {
  const names = Array.isArray(record.categoryIds)
    ? record.categoryIds.map(findCategoryName).filter(Boolean)
    : []
  return {
    ...record,
    categories: names.length ? names : record.categories || []
  }
}

const toLower = value => (value || '').toLowerCase()

const computeSummary = records => {
  const total = records.length
  const active = records.filter(item => toLower(item.status) === 'active').length
  const upcoming = records.filter(item => {
    if (!item.hearingAt) return false
    const date = new Date(item.hearingAt)
    return !Number.isNaN(date.getTime()) && date >= new Date()
  }).length
  return { total, active, upcoming }
}

const nextCaseId = () => sampleCases.reduce((max, item) => Math.max(max, item.id), 100) + 1

export function listCases(params = {}) {
  const keyword = toLower(params.keyword)
  const categoryId = params.categoryId != null ? Number(params.categoryId) : null
  const status = toLower(params.status)

  let records = sampleCases.map(ensureCaseCategories)

  if (keyword) {
    records = records.filter(item => {
      return (
        (item.title || '').toLowerCase().includes(keyword) ||
        (item.caseNum || '').toLowerCase().includes(keyword)
      )
    })
  }

  if (categoryId) {
    records = records.filter(item => Array.isArray(item.categoryIds) && item.categoryIds.includes(categoryId))
  }

  if (status) {
    records = records.filter(item => toLower(item.status) === status)
  }

  const pageNo = Number(params.pageNo) > 0 ? Number(params.pageNo) : 1
  const pageSize = Number(params.pageSize) > 0 ? Number(params.pageSize) : records.length || 1
  const start = (pageNo - 1) * pageSize
  const paginated = records.slice(start, start + pageSize)

  return Promise.resolve({
    records: clone(paginated),
    total: records.length,
    pageNo,
    pageSize,
    summary: computeSummary(records)
  })
}

export function getCase(id) {
  const targetId = Number(id)
  const record = sampleCases.map(ensureCaseCategories).find(item => item.id === targetId) || null
  return Promise.resolve(record ? clone(record) : null)
}

export function removeCase(id) {
  const targetId = Number(id)
  const index = sampleCases.findIndex(item => item.id === targetId)
  if (index >= 0) {
    sampleCases.splice(index, 1)
  }
  return Promise.resolve({ success: true })
}

export function createCase(body = {}, categoryIds = []) {
  const ids = (categoryIds.length ? categoryIds : body.categoryIds || []).map(Number)
  const now = new Date().toISOString()
  const id = nextCaseId()
  const record = {
    id,
    caseNum: body.caseNum || `CASE-${id}`,
    title: body.title || 'Untitled case',
    status: toLower(body.status) || 'draft',
    categoryIds: ids,
    categories: ids.map(findCategoryName).filter(Boolean),
    hearingAt: body.hearingAt || null,
    createdAt: body.createdAt || now,
    updatedAt: body.updatedAt || now,
    lawyerId: body.lawyerId || null,
    description: body.description || ''
  }
  sampleCases.push(record)
  return Promise.resolve(clone(record))
}

export function updateCase(id, body = {}, categoryIds = []) {
  const targetId = Number(id)
  const existing = sampleCases.find(item => item.id === targetId)
  if (!existing) {
    return Promise.resolve(null)
  }
  const ids = (categoryIds.length ? categoryIds : body.categoryIds || existing.categoryIds || []).map(Number)
  if (body.title != null) existing.title = body.title
  if (body.caseNum != null) existing.caseNum = body.caseNum
  if (body.status != null) existing.status = toLower(body.status)
  if (body.hearingAt !== undefined) existing.hearingAt = body.hearingAt
  if (body.description != null) existing.description = body.description
  if (body.lawyerId != null) existing.lawyerId = body.lawyerId
  existing.categoryIds = ids
  existing.categories = ids.map(findCategoryName).filter(Boolean)
  existing.updatedAt = body.updatedAt || new Date().toISOString()
  return Promise.resolve(clone(existing))
}

