import request from './request'

export default {
  list(params) {
    return request.get('/evidence', { params })
  },

  get(id) {
    return request.get(`/evidence/${id}`)
  },

  create(data) {
    return request.post('/evidence', data)
  },

  update(id, data) {
    return request.put(`/evidence/${id}`, data)
  },

  remove(id) {
    return request.delete(`/evidence/${id}`)
  },

  listByCaseId(caseId){
    return request.get(`/evidence/by-case/${caseId}`)
  },

  upload(caseId, file, title, description) {
    const formData = new FormData()
    formData.append('caseId', caseId)
    formData.append('file', file)
    if (title) formData.append('title', title)
    if (description) formData.append('description', description)
    return request.post('/evidence/upload', formData, {
      headers: { 'Content-Type': 'multipart/form-data' }
    })
  },

  updateFile(id, file, title, description) {
    const formData = new FormData()
    if (file) formData.append('file', file)
    if (title) formData.append('title', title)
    if (description) formData.append('description', description)
    return request.put(`/evidence/${id}`, formData, {
      headers: { 'Content-Type': 'multipart/form-data' }
    })
  }
}
