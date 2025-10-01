import request from './request'

export default {
  list(params) {
    return request.get('/cases', { params })
  },

  get(id) {
    return request.get(`/cases/${id}`)
  },

  create(data) {
    return request.post('/cases', data)
  },

  update(id, data) {
    return request.put(`/cases/${id}`, data)
  },

  remove(id) {
    return request.delete(`/cases/${id}`)
  },
}
