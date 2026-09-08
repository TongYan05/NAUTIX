import axios from 'axios'

// Create axios instance
const request = axios.create({
  baseURL: 'http://localhost:1910/api',
  timeout: 30000
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

// True when the stored token is missing, expired, or structurally invalid.
// Decoded locally only for UX; the backend still verifies every request.
function tokenExpired(): boolean {
  const token = localStorage.getItem('token')
  if (!token) return true
  try {
    let b64 = token.split('.')[1]?.replace(/-/g, '+').replace(/_/g, '/')
    if (!b64) return true
    while (b64.length % 4) b64 += '='
    const payload = JSON.parse(atob(b64))
    if (!payload.exp) return false
    return payload.exp * 1000 <= Date.now() + 5000
  } catch {
    return true
  }
}

function redirectToLogin() {
  localStorage.removeItem('token')
  localStorage.removeItem('user')
  if (window.location.pathname !== '/login') {
    window.location.href = '/login'
  }
}

// Response interceptor: return response.data directly and handle expired login.
// The backend returns 401 for missing/invalid credentials and 403 for
// access denied; a stale token can surface as either status, and an expired
// token previously left every page silently showing "no data". On any 401/403
// we clear the session and send the user to the login page to fetch a fresh
// token, after which all data pages load normally again.
request.interceptors.response.use(
  (response: any) => response.data,
  error => {
    const status = error.response?.status
    if (status === 401 || status === 403) {
      redirectToLogin()
    }
    return Promise.reject(error)
  }
)

export default request
export { tokenExpired }
