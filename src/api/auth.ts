import request from './axios'

interface LoginResponse {
  token: string
  userId: number
  username: string
  nickname: string
}

interface RegisterResponse {
  message: string
}

/**
 * User login
 */
export async function login(username: string, password: string): Promise<LoginResponse> {
  return await request.post('/auth/login', { username, password })
}

/**
 * User register
 */
export async function register(username: string, password: string, nickname?: string): Promise<RegisterResponse> {
  return await request.post('/auth/register', { username, password, nickname })
}

/**
 * Get current user info
 */
export function getCurrentUser() {
  const userStr = localStorage.getItem('user')
  if (userStr) {
    return JSON.parse(userStr)
  }
  return null
}

/**
 * Logout
 */
export function logout() {
  localStorage.removeItem('token')
  localStorage.removeItem('user')
  window.location.href = '/login'
}

/**
 * Check if logged in
 */
export function isAuthenticated() {
  const token = localStorage.getItem('token')
  return !!token
}
