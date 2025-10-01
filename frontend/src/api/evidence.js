
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
}
