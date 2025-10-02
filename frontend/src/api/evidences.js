import { sampleEvidence } from '@/data/sampleData'

const clone = data => JSON.parse(JSON.stringify(data))
const toLower = value => (value || '').toLowerCase()
const nextEvidenceId = () => sampleEvidence.reduce((max, item) => Math.max(max, item.id), 400) + 1

const filterEvidence = params => {
  const keyword = toLower(params && params.keyword)
  let records = sampleEvidence
  if (keyword) {
    records = records.filter(item => {
      const parts = [item.title, item.description, item.fileUrl]
        .filter(Boolean)
        .map(text => text.toLowerCase())
      return parts.some(text => text.includes(keyword))
    })
  }
  return records
}

export default {
  list(params = {}) {
    const records = filterEvidence(params)
    return Promise.resolve({
      records: clone(records),
      total: records.length
    })
  },

  get(id) {
    const targetId = Number(id)
    const record = sampleEvidence.find(item => item.id === targetId) || null
    return Promise.resolve(record ? clone(record) : null)
  },

  create(data = {}) {
    const record = {
      id: nextEvidenceId(),
      caseId: data.caseId != null ? Number(data.caseId) : null,
      title: data.title || 'Untitled file',
      description: data.description || '',
      fileUrl: data.fileUrl || '#',
      uploadedBy: data.uploadedBy != null ? data.uploadedBy : null,
      createdAt: data.createdAt || new Date().toISOString(),
      sizeBytes: data.sizeBytes || 0
    }
    sampleEvidence.push(record)
    return Promise.resolve(clone(record))
  },

  update(id, data = {}) {
    const targetId = Number(id)
    const existing = sampleEvidence.find(item => item.id === targetId)
    if (!existing) {
      return Promise.resolve(null)
    }
    if (data.caseId != null) existing.caseId = Number(data.caseId)
    if (data.title != null) existing.title = data.title
    if (data.description != null) existing.description = data.description
    if (data.fileUrl != null) existing.fileUrl = data.fileUrl
    if (data.uploadedBy != null) existing.uploadedBy = data.uploadedBy
    if (data.sizeBytes != null) existing.sizeBytes = data.sizeBytes
    existing.createdAt = data.createdAt || existing.createdAt
    return Promise.resolve(clone(existing))
  },

  remove(id) {
    const targetId = Number(id)
    const index = sampleEvidence.findIndex(item => item.id === targetId)
    if (index >= 0) {
      sampleEvidence.splice(index, 1)
    }
    return Promise.resolve({ success: true })
  },

  listByCaseId(caseId) {
    const targetId = Number(caseId)
    const records = sampleEvidence.filter(item => item.caseId === targetId)
    return Promise.resolve(clone(records))
  },

  upload(caseId, file, title, description) {
    return this.create({
      caseId,
      title: title || (file && file.name) || 'Uploaded file',
      description: description || '',
      fileUrl: '#',
      uploadedBy: null,
      sizeBytes: file && file.size ? file.size : 0
    })
  },

  updateFile(id, file, title, description) {
    return this.update(id, {
      title: title || (file && file.name) || undefined,
      description: description || undefined,
      sizeBytes: file && file.size ? file.size : undefined
    })
  }
}
