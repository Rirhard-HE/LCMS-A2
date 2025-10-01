import request from './request'

export default {
  list(params) {
    return request.get('/categories', { params })
  },

  get(id) {
    return request.get(`/categories/${id}`)
  },

  create(data) {
    return request.post('/categories', data)
  },

  update(id, data) {
    return request.put(`/categories/${id}`, data)
  },

  remove(id) {
    return request.delete(`/categories/${id}`)
  },
}
