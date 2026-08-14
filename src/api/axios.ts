import axios from 'axios'

// Create axios instance
const request = axios.create({
  baseURL: 'http://localhost:1910/api',
  timeout: 10000
})

// Request interceptor: automatically add JWT token
request.interceptors.request.use(
  config => {
    const token = localStorage.getItem('token')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  error => {
    return Promise.reject(error)
  }
)

// Response interceptor: return response.data directly and handle 401 unauthorized
request.interceptors.response.use(
  (response: any) => response.data,
  error => {
    if (error.response?.status === 401) {
      // Token expired or invalid, clear login state and redirect to login page
      localStorage.removeItem('token')
      localStorage.removeItem('user')
      window.location.href = '/login'
    }
    return Promise.reject(error)
  }
)

export default request
