import { defineStore } from 'pinia'
import { login as loginApi, register as registerApi, getCurrentUser, isAuthenticated } from '@/api/auth'
import router from '@/router'

interface AuthState {
  user: any | null
  token: string | null
}

export const useAuthStore = defineStore('auth', {
  state: (): AuthState => ({
    user: getCurrentUser(),
    token: localStorage.getItem('token')
  }),

  getters: {
    isLoggedIn: (state) => !!state.token,
    username: (state) => state.user?.username || '',
    nickname: (state) => state.user?.nickname || state.user?.username || ''
  },

  actions: {
    /**
     * Login
     */
    async login(username: string, password: string) {
      try {
        const data = await loginApi(username, password)

        this.token = data.token
        this.user = {
          userId: data.userId,
          username: data.username,
          nickname: data.nickname
        }

        localStorage.setItem('token', data.token)
        localStorage.setItem('user', JSON.stringify(this.user))

        const redirectPath = router.currentRoute.value.query.redirect as string || '/dashboard'
        await router.push(redirectPath)

        return { success: true, message: 'Login successful' }
      } catch (error: any) {
        const message = error.response?.data?.message || 'Login failed'
        return { success: false, message }
      }
    },

    /**
     * Register
     */
    async register(username: string, password: string, nickname?: string) {
      try {
        await registerApi(username, password, nickname)
        return { success: true, message: 'Registration successful, please login' }
      } catch (error: any) {
        const message = error.response?.data?.message || 'Registration failed'
        return { success: false, message }
      }
    },

    /**
     * Logout
     */
    logout() {
      this.token = null
      this.user = null
      localStorage.removeItem('token')
      localStorage.removeItem('user')
      router.push('/login')
    },

    /**
     * Check auth status
     */
    checkAuth() {
      return isAuthenticated()
    }
  }
})
