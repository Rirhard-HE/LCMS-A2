import request from './request'
import qs from 'qs'

export function listCases(params) {
  return request.get('/cases', { params })
}

export function getCase(id) {
  return request.get(`/cases/${id}`)
}
export function removeCase(id) {
  return request.delete(`/cases/${id}`)
}

export function createCase(body, categoryIds = []) {
  return request.post('/cases', body, {
    params: { categoryId: categoryIds },
    paramsSerializer: p => qs.stringify(p, { arrayFormat: 'repeat' })
  })
}

export function updateCase(id, body, categoryIds = []) {
  return request.put(`/cases/${id}`, body, {
    params: { categoryId: categoryIds },
    paramsSerializer: p => qs.stringify(p, { arrayFormat: 'repeat' })
  })
}
