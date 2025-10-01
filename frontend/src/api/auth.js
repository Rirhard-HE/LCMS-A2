import axios from 'axios'

export function login(data) {
  return axios.post('/api/auth/login', data)
}

export function register(data) {
  return axios.post('/api/auth/register', data)
}