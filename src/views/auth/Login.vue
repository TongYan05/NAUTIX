<template>
  <div class="login-page">
    <!-- 天空背景 -->
    <div class="sky">
      <!-- 太阳 -->
      <div class="sun">
        <div class="sun-core"></div>
        <div class="sun-ray r1"></div>
        <div class="sun-ray r2"></div>
        <div class="sun-ray r3"></div>
        <div class="sun-ray r4"></div>
        <div class="sun-ray r5"></div>
        <div class="sun-ray r6"></div>
        <div class="sun-ray r7"></div>
        <div class="sun-ray r8"></div>
        <div class="sun-glow"></div>
      </div>

      <!-- 白云 -->
      <div class="cloud cloud-1">
        <div class="cloud-puff p1"></div>
        <div class="cloud-puff p2"></div>
        <div class="cloud-puff p3"></div>
        <div class="cloud-puff p4"></div>
      </div>
      <div class="cloud cloud-2">
        <div class="cloud-puff p1"></div>
        <div class="cloud-puff p2"></div>
        <div class="cloud-puff p3"></div>
      </div>
      <div class="cloud cloud-3">
        <div class="cloud-puff p1"></div>
        <div class="cloud-puff p2"></div>
        <div class="cloud-puff p3"></div>
        <div class="cloud-puff p4"></div>
        <div class="cloud-puff p5"></div>
      </div>
      <div class="cloud cloud-4">
        <div class="cloud-puff p1"></div>
        <div class="cloud-puff p2"></div>
        <div class="cloud-puff p3"></div>
      </div>
      <div class="cloud cloud-5">
        <div class="cloud-puff p1"></div>
        <div class="cloud-puff p2"></div>
        <div class="cloud-puff p3"></div>
        <div class="cloud-puff p4"></div>
      </div>

      <!-- 海鸥 -->
      <div class="seagull sg-1">
        <div class="wing wing-l"></div>
        <div class="wing wing-r"></div>
        <div class="body"></div>
      </div>
      <div class="seagull sg-2">
        <div class="wing wing-l"></div>
        <div class="wing wing-r"></div>
        <div class="body"></div>
      </div>
      <div class="seagull sg-3">
        <div class="wing wing-l"></div>
        <div class="wing wing-r"></div>
        <div class="body"></div>
      </div>
      <div class="seagull sg-4">
        <div class="wing wing-l"></div>
        <div class="wing wing-r"></div>
        <div class="body"></div>
      </div>
    </div>

    <!-- 海洋 -->
    <div class="ocean">
      <div class="wave wave-1"></div>
      <div class="wave wave-2"></div>
      <div class="wave wave-3"></div>
      <div class="wave wave-4"></div>

      <!-- 卡通帆船 -->
      <div class="cartoon-ship">
        <!-- 船体 -->
        <div class="ship-hull">
          <div class="hull-stripe"></div>
          <div class="hull-line hl-1"></div>
          <div class="hull-line hl-2"></div>
          <div class="porthole ph-1"></div>
          <div class="porthole ph-2"></div>
          <div class="porthole ph-3"></div>
          <!-- 龙头船首 -->
          <div class="figurehead">
            <div class="fh-eye eye-l"></div>
            <div class="fh-eye eye-r"></div>
            <div class="fh-mouth"></div>
            <div class="fh-horn horn-l"></div>
            <div class="fh-horn horn-r"></div>
          </div>
        </div>
        <!-- 甲板 -->
        <div class="ship-deck"></div>
        <!-- 船舱 -->
        <div class="cabin">
          <div class="cabin-roof"></div>
          <div class="cabin-win cw-1"></div>
          <div class="cabin-win cw-2"></div>
          <div class="cabin-win cw-3"></div>
          <div class="cabin-door"></div>
        </div>
        <!-- 桅杆和帆 -->
        <div class="mast main-mast">
          <div class="main-sail">
            <div class="sail-emblem">☠</div>
          </div>
          <div class="crow-nest"></div>
        </div>
        <div class="mast fore-mast">
          <div class="fore-sail"></div>
        </div>
        <div class="mast back-mast">
          <div class="back-sail"></div>
          <div class="mast-flag">
            <span>☠</span>
          </div>
        </div>
        <!-- 浪花 -->
        <div class="splash sp-l"></div>
        <div class="splash sp-r"></div>
        <div class="splash sp-bl"></div>
        <div class="splash sp-br"></div>
      </div>

      <!-- 水面波纹 -->
      <div class="water-ripple wr-1"></div>
      <div class="water-ripple wr-2"></div>
      <div class="water-ripple wr-3"></div>
    </div>

    <!-- 登录表单（右侧） -->
    <div class="login-panel">
      <div class="panel-inner">
        <div class="panel-header">
          <div class="logo-circle">⚓</div>
          <h1 class="panel-title">NautiX</h1>
          <p class="panel-subtitle">{{ lang.t('auth.subtitle') }}</p>
        </div>

        <el-form
          ref="loginFormRef"
          :model="loginForm"
          :rules="loginRules"
          label-width="0"
          @submit.prevent="handleLogin"
        >
          <el-form-item prop="username">
            <el-input
              v-model="loginForm.username"
              :placeholder="lang.t('auth.username')"
              size="large"
              :prefix-icon="User"
            />
          </el-form-item>

          <el-form-item prop="password">
            <el-input
              v-model="loginForm.password"
              type="password"
              :placeholder="lang.t('auth.password')"
              size="large"
              :prefix-icon="Lock"
              show-password
              @keyup.enter="handleLogin"
            />
          </el-form-item>

          <el-form-item>
            <el-button
              type="primary"
              size="large"
              :loading="loading"
              class="login-btn"
              @click="handleLogin"
            >
              {{ loading ? lang.t('auth.logging') : lang.t('auth.login') }}
            </el-button>
          </el-form-item>

          <div class="register-link">
            {{ lang.t('auth.noAccount') }}
            <router-link to="/register">{{ lang.t('auth.registerNow') }}</router-link>
          </div>
        </el-form>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, reactive, ref } from 'vue'
import { User, Lock } from '@element-plus/icons-vue'
import { ElMessage, type FormInstance, type FormRules } from 'element-plus'
import { useAuthStore } from '@/stores/auth'
import { useLang } from '@/stores/lang'

const authStore = useAuthStore()
const lang = useLang()
const loginFormRef = ref<FormInstance>()
const loading = ref(false)

const loginForm = reactive({
  username: '',
  password: ''
})

const loginRules = computed<FormRules>(() => ({
  username: [
    { required: true, message: lang.t('auth.userRequired'), trigger: 'blur' },
    { min: 3, message: lang.t('auth.userMin'), trigger: 'blur' }
  ],
  password: [
    { required: true, message: lang.t('auth.passRequired'), trigger: 'blur' },
    { min: 6, message: lang.t('auth.passMin'), trigger: 'blur' }
  ]
}))

const handleLogin = async () => {
  if (!loginFormRef.value) return

  await loginFormRef.value.validate(async (valid) => {
    if (valid) {
      loading.value = true
      const result = await authStore.login(loginForm.username, loginForm.password)
      loading.value = false

      if (result.success) {
        ElMessage.success(lang.t('auth.loginOk'))
      } else {
        ElMessage.error(lang.t('auth.loginFailed'))
      }
    }
  })
}
</script>

<style scoped>
.login-page {
  position: fixed;
  top: 0;
  left: 0;
  width: 100vw;
  height: 100vh;
  overflow: hidden;
  display: flex;
}

/* ===== 天空 ===== */
.sky {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: linear-gradient(180deg, 
    #3a9fd8 0%, 
    #5bb8e8 15%, 
    #7dd3fc 30%, 
    #a5e0fa 48%, 
    #c8ecfd 58%, 
    #b8e6f8 62%, 
    #7cc8e8 66%, 
    #4ab8e0 70%, 
    #2998c8 78%, 
    #1a7ab0 88%, 
    #0d5a8a 100%
  );
  z-index: 1;
}

/* ===== 太阳 ===== */
.sun {
  position: absolute;
  top: 30px;
  left: 50px;
  width: 160px;
  height: 160px;
  z-index: 2;
}

.sun-core {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  width: 100px;
  height: 100px;
  border-radius: 50%;
  background: radial-gradient(circle at 45% 42%, 
    #fffde7 0%, 
    #fff9c4 15%, 
    #ffee58 35%, 
    #ffc107 55%, 
    #ff9800 80%, 
    #f57c00 100%
  );
  box-shadow: 
    0 0 30px rgba(255, 193, 7, 0.7),
    0 0 60px rgba(255, 152, 0, 0.4),
    0 0 100px rgba(255, 87, 34, 0.2);
  animation: sunPulse 5s ease-in-out infinite;
}

@keyframes sunPulse {
  0%, 100% { 
    box-shadow: 0 0 30px rgba(255,193,7,0.7), 0 0 60px rgba(255,152,0,0.4), 0 0 100px rgba(255,87,34,0.2); 
    transform: translate(-50%, -50%) scale(1);
  }
  50% { 
    box-shadow: 0 0 45px rgba(255,193,7,0.9), 0 0 90px rgba(255,152,0,0.5), 0 0 140px rgba(255,87,34,0.3); 
    transform: translate(-50%, -50%) scale(1.03);
  }
}

.sun-ray {
  position: absolute;
  top: 50%;
  left: 50%;
  width: 140px;
  height: 3px;
  background: linear-gradient(90deg, rgba(255,193,7,0.5) 0%, rgba(255,193,7,0.2) 40%, transparent 100%);
  transform-origin: left center;
  border-radius: 2px;
  filter: blur(1px);
}

.r1 { transform: rotate(0deg); }
.r2 { transform: rotate(45deg); }
.r3 { transform: rotate(90deg); }
.r4 { transform: rotate(135deg); }
.r5 { transform: rotate(180deg); }
.r6 { transform: rotate(225deg); }
.r7 { transform: rotate(270deg); }
.r8 { transform: rotate(315deg); }

.sun-glow {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  width: 280px;
  height: 280px;
  border-radius: 50%;
  background: radial-gradient(circle, 
    rgba(255,235,59,0.2) 0%, 
    rgba(255,193,7,0.1) 30%, 
    rgba(255,152,0,0.05) 50%, 
    transparent 70%
  );
  animation: sunGlow 5s ease-in-out infinite;
}

@keyframes sunGlow {
  0%, 100% { transform: translate(-50%,-50%) scale(1); opacity: 1; }
  50% { transform: translate(-50%,-50%) scale(1.15); opacity: 0.8; }
}

/* ===== 白云 ===== */
.cloud {
  position: absolute;
  z-index: 3;
}

.cloud-puff {
  position: absolute;
  border-radius: 50%;
  background: radial-gradient(circle at 40% 35%, #fff, #e8f4fd);
  box-shadow: 0 4px 12px rgba(0,0,0,0.06);
}

.cloud-1 {
  top: 8%;
  left: 10%;
  animation: cloudDrift 35s linear infinite;
  animation-delay: -8s;
}
.cloud-1 .p1 { width: 80px; height: 50px; left: 0; top: 15px; }
.cloud-1 .p2 { width: 100px; height: 60px; left: 30px; top: 0; }
.cloud-1 .p3 { width: 90px; height: 55px; left: 70px; top: 10px; }
.cloud-1 .p4 { width: 70px; height: 45px; left: 100px; top: 20px; }

.cloud-2 {
  top: 22%;
  left: 55%;
  animation: cloudDrift 45s linear infinite 10s;
  animation-delay: -25s;
  transform: scale(0.7);
}
.cloud-2 .p1 { width: 70px; height: 45px; left: 0; top: 12px; }
.cloud-2 .p2 { width: 90px; height: 55px; left: 25px; top: 0; }
.cloud-2 .p3 { width: 75px; height: 48px; left: 60px; top: 8px; }

.cloud-3 {
  top: 4%;
  left: 30%;
  animation: cloudDrift 50s linear infinite 20s;
  animation-delay: -15s;
  transform: scale(1.2);
}
.cloud-3 .p1 { width: 60px; height: 40px; left: 0; top: 20px; }
.cloud-3 .p2 { width: 90px; height: 55px; left: 25px; top: 5px; }
.cloud-3 .p3 { width: 100px; height: 60px; left: 60px; top: 0; }
.cloud-3 .p4 { width: 85px; height: 50px; left: 100px; top: 10px; }
.cloud-3 .p5 { width: 65px; height: 42px; left: 135px; top: 18px; }

.cloud-4 {
  top: 30%;
  left: 70%;
  animation: cloudDrift 40s linear infinite 5s;
  animation-delay: -20s;
  transform: scale(0.5);
}
.cloud-4 .p1 { width: 75px; height: 48px; left: 0; top: 10px; }
.cloud-4 .p2 { width: 95px; height: 58px; left: 28px; top: 0; }
.cloud-4 .p3 { width: 80px; height: 50px; left: 65px; top: 8px; }

.cloud-5 {
  top: 15%;
  left: 85%;
  animation: cloudDrift 55s linear infinite 30s;
  animation-delay: -40s;
  transform: scale(0.9);
}
.cloud-5 .p1 { width: 65px; height: 42px; left: 0; top: 18px; }
.cloud-5 .p2 { width: 85px; height: 52px; left: 22px; top: 3px; }
.cloud-5 .p3 { width: 95px; height: 58px; left: 55px; top: 0; }
.cloud-5 .p4 { width: 70px; height: 44px; left: 95px; top: 14px; }

@keyframes cloudDrift {
  0% { left: -200px; }
  100% { left: 110%; }
}

/* ===== 海鸥 ===== */
.seagull {
  position: absolute;
  z-index: 4;
  width: 40px;
  height: 20px;
}

.seagull .body {
  position: absolute;
  top: 8px;
  left: 16px;
  width: 10px;
  height: 6px;
  background: #555;
  border-radius: 50%;
}

.seagull .wing {
  position: absolute;
  top: 2px;
  width: 18px;
  height: 10px;
  background: #666;
  border-radius: 50% 50% 0 0;
  transform-origin: bottom center;
}

.seagull .wing-l {
  left: 0;
  animation: wingFlapL 0.8s ease-in-out infinite;
}

.seagull .wing-r {
  right: 0;
  animation: wingFlapR 0.8s ease-in-out infinite;
}

@keyframes wingFlapL {
  0%, 100% { transform: rotate(0deg); }
  50% { transform: rotate(-25deg); }
}

@keyframes wingFlapR {
  0%, 100% { transform: rotate(0deg); }
  50% { transform: rotate(25deg); }
}

.sg-1 {
  top: 12%;
  animation: seagullFly 18s linear infinite;
  animation-delay: -4s;
}

.sg-2 {
  top: 20%;
  animation: seagullFly 22s linear infinite 5s;
  animation-delay: -13s;
  transform: scale(0.7);
}

.sg-3 {
  top: 8%;
  animation: seagullFly 15s linear infinite 10s;
  animation-delay: -6s;
  transform: scale(0.85);
}

.sg-4 {
  top: 25%;
  animation: seagullFly 25s linear infinite 15s;
  animation-delay: -20s;
  transform: scale(0.6);
}

@keyframes seagullFly {
  0% { left: -60px; }
  100% { left: 110%; }
}

/* ===== 海洋 ===== */
.ocean {
  position: absolute;
  bottom: 0;
  left: 0;
  width: 100%;
  height: 42%;
  z-index: 5;
  background: transparent;
}

/* 波浪 */
.wave {
  position: absolute;
  width: 200%;
  height: 100%;
  left: -50%;
}

.wave-1 {
  top: -30px;
  background: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 1200 120'%3E%3Cpath d='M0,60 C200,100 400,20 600,60 C800,100 1000,20 1200,60 L1200,120 L0,120 Z' fill='%234fc3f7'/%3E%3C/svg%3E") repeat-x;
  background-size: 600px 60px;
  animation: waveMove 6s linear infinite;
  opacity: 0.7;
}

.wave-2 {
  top: -15px;
  background: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 1200 120'%3E%3Cpath d='M0,60 C200,100 400,20 600,60 C800,100 1000,20 1200,60 L1200,120 L0,120 Z' fill='%2329b6f6'/%3E%3C/svg%3E") repeat-x;
  background-size: 500px 50px;
  animation: waveMove 8s linear infinite;
  opacity: 0.5;
}

.wave-3 {
  top: -8px;
  background: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 1200 120'%3E%3Cpath d='M0,60 C200,100 400,20 600,60 C800,100 1000,20 1200,60 L1200,120 L0,120 Z' fill='%231e88e5'/%3E%3C/svg%3E") repeat-x;
  background-size: 700px 70px;
  animation: waveMove 10s linear infinite;
  opacity: 0.4;
}

.wave-4 {
  top: -2px;
  background: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 1200 120'%3E%3Cpath d='M0,60 C200,100 400,20 600,60 C800,100 1000,20 1200,60 L1200,120 L0,120 Z' fill='%231565c0'/%3E%3C/svg%3E") repeat-x;
  background-size: 400px 40px;
  animation: waveMove 12s linear infinite;
  opacity: 0.3;
}

@keyframes waveMove {
  0% { transform: translateX(0); }
  100% { transform: translateX(-50%); }
}

/* 水面波纹 */
.water-ripple {
  position: absolute;
  border-radius: 50%;
  border: 2px solid rgba(255,255,255,0.15);
  animation: rippleExpand 4s ease-out infinite;
}

.wr-1 { width: 80px; height: 20px; top: 30%; left: 20%; animation-delay: 0s; }
.wr-2 { width: 100px; height: 25px; top: 50%; left: 60%; animation-delay: 1.5s; }
.wr-3 { width: 60px; height: 15px; top: 70%; left: 40%; animation-delay: 3s; }

@keyframes rippleExpand {
  0% { transform: scale(0.5); opacity: 0.8; }
  100% { transform: scale(2); opacity: 0; }
}

/* ===== 卡通帆船 ===== */
.cartoon-ship {
  position: absolute;
  top: -20px;
  width: 350px;
  height: 280px;
  z-index: 10;
  animation: shipSail 20s linear infinite;
}

@keyframes shipSail {
  0% { left: -400px; transform: translateY(0) rotate(0deg); }
  10% { transform: translateY(-8px) rotate(-2deg); }
  20% { transform: translateY(4px) rotate(1deg); }
  30% { transform: translateY(-6px) rotate(-1.5deg); }
  40% { transform: translateY(3px) rotate(0.5deg); }
  50% { left: 35%; transform: translateY(-5px) rotate(-1deg); }
  60% { transform: translateY(6px) rotate(1.5deg); }
  70% { transform: translateY(-4px) rotate(-0.5deg); }
  80% { transform: translateY(5px) rotate(1deg); }
  90% { transform: translateY(-7px) rotate(-2deg); }
  100% { left: 110%; transform: translateY(0) rotate(0deg); }
}

/* 船体 */
.ship-hull {
  position: absolute;
  bottom: 20px;
  left: 50%;
  transform: translateX(-50%);
  width: 280px;
  height: 90px;
  background: linear-gradient(180deg, #d84315 0%, #bf360c 40%, #8b2500 100%);
  border-radius: 20px 20px 50% 50% / 20px 20px 100% 100%;
  box-shadow: 0 8px 20px rgba(0,0,0,0.3), inset 0 3px 8px rgba(255,255,255,0.15);
  border: 3px solid #7f1d00;
}

.hull-stripe {
  position: absolute;
  top: 35px;
  left: 12px;
  right: 12px;
  height: 8px;
  background: linear-gradient(90deg, #ffd54f, #ffb300, #ffd54f);
  border-radius: 4px;
  box-shadow: 0 2px 4px rgba(0,0,0,0.3);
}

.hull-line {
  position: absolute;
  left: 12px;
  right: 12px;
  height: 2px;
  background: rgba(0,0,0,0.15);
  border-radius: 1px;
}

.hl-1 { top: 20px; }
.hl-2 { top: 55px; }

.porthole {
  position: absolute;
  top: 48px;
  width: 16px;
  height: 16px;
  border-radius: 50%;
  background: radial-gradient(circle at 35% 35%, #fff9c4, #ffd54f 60%, #5d4037 100%);
  box-shadow: 0 0 8px rgba(255,213,79,0.6), inset 0 1px 3px rgba(0,0,0,0.3);
  border: 2px solid #5d4037;
}

.ph-1 { left: 50px; }
.ph-2 { left: 50%; transform: translateX(-50%); }
.ph-3 { right: 50px; }

/* 龙头船首 */
.figurehead {
  position: absolute;
  top: -35px;
  left: -25px;
  width: 65px;
  height: 65px;
  background: radial-gradient(ellipse at 50% 40%, #ffb300, #f57f17 60%, #e65100 100%);
  border-radius: 50% 50% 35% 35%;
  box-shadow: inset 0 -8px 15px rgba(0,0,0,0.3), 0 5px 15px rgba(0,0,0,0.4);
  border: 2px solid #bf360c;
  z-index: 2;
}

.fh-eye {
  position: absolute;
  top: 18px;
  width: 12px;
  height: 14px;
  background: radial-gradient(circle at 40% 40%, #fff, #ff0 30%, #f80 60%, #c00 100%);
  border-radius: 50%;
  box-shadow: 0 0 8px rgba(255,100,0,0.6);
  border: 2px solid #4a2512;
}

.eye-l { left: 10px; }
.eye-r { right: 10px; }

.fh-eye::after {
  content: '';
  position: absolute;
  top: 30%;
  left: 30%;
  width: 4px;
  height: 6px;
  background: #000;
  border-radius: 50%;
}

.fh-mouth {
  position: absolute;
  bottom: 8px;
  left: 50%;
  transform: translateX(-50%);
  width: 25px;
  height: 10px;
  background: linear-gradient(180deg, #3a1a05, #1a0a02);
  border-radius: 0 0 50% 50%;
  border: 1px solid #4a2512;
}

.fh-horn {
  position: absolute;
  top: -8px;
  width: 10px;
  height: 20px;
  background: linear-gradient(to top, #f57f17, #ffd54f);
  border-radius: 50% 50% 30% 30%;
  box-shadow: 0 -2px 4px rgba(255,213,79,0.5);
}

.horn-l { left: 5px; transform: rotate(-15deg); }
.horn-r { right: 5px; transform: rotate(15deg); }

/* 甲板 */
.ship-deck {
  position: absolute;
  bottom: 100px;
  left: 50%;
  transform: translateX(-50%);
  width: 260px;
  height: 20px;
  background: linear-gradient(180deg, #8d6e63, #6d4c41);
  border-radius: 5px;
  box-shadow: inset 0 -3px 6px rgba(0,0,0,0.2);
  border: 2px solid #4e342e;
}

/* 船舱 */
.cabin {
  position: absolute;
  bottom: 115px;
  left: 50%;
  transform: translateX(-50%);
  width: 150px;
  height: 70px;
  background: linear-gradient(180deg, #ffb300, #f57f17 50%, #e65100 100%);
  border-radius: 12px 12px 4px 4px;
  box-shadow: inset 0 -8px 15px rgba(0,0,0,0.3), 0 5px 15px rgba(0,0,0,0.3);
  border: 2px solid #bf360c;
  z-index: 3;
}

.cabin-roof {
  position: absolute;
  top: -10px;
  left: -8px;
  right: -8px;
  height: 14px;
  background: linear-gradient(180deg, #ffd54f, #ffb300);
  border-radius: 8px 8px 0 0;
  box-shadow: 0 -2px 5px rgba(0,0,0,0.2);
}

.cabin-win {
  position: absolute;
  top: 15px;
  width: 20px;
  height: 20px;
  border-radius: 50%;
  background: radial-gradient(circle at 35% 35%, #fff9c4, #ffd54f 60%, #5d4037 100%);
  box-shadow: 0 0 10px rgba(255,213,79,0.5);
  border: 2px solid #5d4037;
  animation: winGlow 3s ease-in-out infinite;
}

.cw-1 { left: 12px; animation-delay: 0s; }
.cw-2 { left: 50%; transform: translateX(-50%); animation-delay: 1s; }
.cw-3 { right: 12px; animation-delay: 2s; }

@keyframes winGlow {
  0%, 100% { box-shadow: 0 0 10px rgba(255,213,79,0.5); }
  50% { box-shadow: 0 0 20px rgba(255,213,79,0.9); }
}

.cabin-door {
  position: absolute;
  bottom: 3px;
  left: 50%;
  transform: translateX(-50%);
  width: 22px;
  height: 30px;
  background: linear-gradient(180deg, #4e342e, #3e2723);
  border-radius: 10px 10px 0 0;
  border: 2px solid #3e2723;
}

.cabin-door::after {
  content: '';
  position: absolute;
  top: 45%;
  right: 3px;
  width: 4px;
  height: 4px;
  background: #ffd54f;
  border-radius: 50%;
  box-shadow: 0 0 3px rgba(255,213,79,0.8);
}

/* 桅杆 */
.mast {
  position: absolute;
  background: linear-gradient(90deg, #6d4c41, #4e342e, #6d4c41);
  border-radius: 3px;
  box-shadow: 0 3px 8px rgba(0,0,0,0.3);
  z-index: 4;
}

.main-mast {
  bottom: 130px;
  left: 50%;
  transform: translateX(-50%);
  width: 10px;
  height: 200px;
}

.fore-mast {
  bottom: 130px;
  left: 70px;
  width: 8px;
  height: 150px;
}

.back-mast {
  bottom: 130px;
  right: 70px;
  width: 8px;
  height: 150px;
}

/* 船帆 */
.main-sail {
  position: absolute;
  top: 20px;
  left: -50px;
  width: 110px;
  height: 140px;
  background: linear-gradient(180deg, #fff8e1 0%, #ffe0b2 30%, #ffcc80 60%, #ffb74d 100%);
  border-radius: 8px;
  box-shadow: inset 0 -8px 15px rgba(0,0,0,0.15), 0 4px 12px rgba(0,0,0,0.2);
  border: 2px solid #e65100;
  animation: sailWave 4s ease-in-out infinite;
}

.sail-emblem {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  font-size: 40px;
  color: rgba(0,0,0,0.15);
}

.fore-sail {
  position: absolute;
  top: 20px;
  left: -35px;
  width: 80px;
  height: 100px;
  background: linear-gradient(180deg, #fff8e1, #ffe0b2 40%, #ffcc80 100%);
  border-radius: 6px;
  box-shadow: inset 0 -6px 10px rgba(0,0,0,0.12), 0 3px 8px rgba(0,0,0,0.2);
  border: 2px solid #e65100;
  animation: sailWave 4s ease-in-out infinite 0.5s;
}

.back-sail {
  position: absolute;
  top: 20px;
  left: -35px;
  width: 80px;
  height: 100px;
  background: linear-gradient(180deg, #fff8e1, #ffe0b2 40%, #ffcc80 100%);
  border-radius: 6px;
  box-shadow: inset 0 -6px 10px rgba(0,0,0,0.12), 0 3px 8px rgba(0,0,0,0.2);
  border: 2px solid #e65100;
  animation: sailWave 4s ease-in-out infinite 1s;
}

@keyframes sailWave {
  0%, 100% { transform: skewY(0deg); }
  25% { transform: skewY(2deg); }
  75% { transform: skewY(-1.5deg); }
}

.crow-nest {
  position: absolute;
  top: -8px;
  left: 50%;
  transform: translateX(-50%);
  width: 24px;
  height: 14px;
  background: linear-gradient(180deg, #8d6e63, #5d4037);
  border-radius: 4px;
  border: 2px solid #4e342e;
  box-shadow: 0 3px 6px rgba(0,0,0,0.3);
}

.mast-flag {
  position: absolute;
  top: -12px;
  left: 50%;
  width: 30px;
  height: 18px;
  background: #212121;
  clip-path: polygon(0 0, 100% 50%, 0 100%);
  animation: flagWave 1s ease-in-out infinite;
  display: flex;
  align-items: center;
  justify-content: center;
}

.mast-flag span {
  color: #fff;
  font-size: 10px;
  margin-left: -5px;
}

@keyframes flagWave {
  0%, 100% { transform: translateX(-50%) skewY(0deg); }
  50% { transform: translateX(-50%) skewY(-8deg); }
}

/* 浪花 */
.splash {
  position: absolute;
  border-radius: 50%;
  background: radial-gradient(circle, rgba(255,255,255,0.6), transparent 70%);
  animation: splashBurst 2s ease-out infinite;
}

.sp-l { top: 70px; left: -10px; width: 40px; height: 25px; animation-delay: 0s; }
.sp-r { top: 70px; right: -10px; width: 40px; height: 25px; animation-delay: 0.4s; }
.sp-bl { bottom: 15px; left: 20px; width: 50px; height: 30px; animation-delay: 0.8s; }
.sp-br { bottom: 15px; right: 20px; width: 50px; height: 30px; animation-delay: 1.2s; }

@keyframes splashBurst {
  0% { transform: scale(0.5); opacity: 0; }
  30% { opacity: 0.8; }
  100% { transform: scale(1.8); opacity: 0; }
}

/* ===== 登录面板（右侧） ===== */
.login-panel {
  position: absolute;
  right: 60px;
  top: 50%;
  transform: translateY(-50%);
  z-index: 100;
  width: 440px;
}

.panel-inner {
  background: rgba(255, 255, 255, 0.88);
  backdrop-filter: blur(20px);
  border: 1px solid rgba(255, 255, 255, 0.5);
  border-radius: 24px;
  padding: 40px 35px;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.15), 0 0 0 1px rgba(255,255,255,0.2);
}

.panel-header {
  text-align: center;
  margin-bottom: 30px;
}

.logo-circle {
  width: 70px;
  height: 70px;
  margin: 0 auto 15px;
  border-radius: 50%;
  background: linear-gradient(135deg, #4ab8e8, #1e88e5);
  box-shadow: 0 8px 25px rgba(30, 136, 229, 0.4);
  display: flex;
  justify-content: center;
  align-items: center;
  font-size: 34px;
  animation: logoSpin 8s ease-in-out infinite;
}

@keyframes logoSpin {
  0%, 100% { transform: rotateY(0deg); }
  50% { transform: rotateY(180deg); }
}

.panel-title {
  font-size: 36px;
  font-weight: 800;
  margin: 0 0 6px 0;
  background: linear-gradient(135deg, #1e88e5, #0d47a1);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  letter-spacing: 4px;
}

.panel-subtitle {
  font-size: 12px;
  color: #78909c;
  margin: 0;
  letter-spacing: 3px;
  text-transform: uppercase;
}

.login-btn {
  width: 100%;
  height: 48px;
  font-size: 16px;
  font-weight: 700;
  background: linear-gradient(135deg, #4ab8e8, #1e88e5, #1565c0);
  border: none;
  border-radius: 12px;
  letter-spacing: 2px;
  transition: all 0.3s ease;
  box-shadow: 0 6px 20px rgba(30, 136, 229, 0.35);
  color: #fff;
}

.login-btn:hover {
  transform: translateY(-3px);
  box-shadow: 0 10px 30px rgba(30, 136, 229, 0.5);
}

.register-link {
  text-align: center;
  margin-top: 20px;
  color: #78909c;
  font-size: 14px;
}

.register-link a {
  color: #1e88e5;
  text-decoration: none;
  font-weight: 600;
  margin-left: 5px;
  transition: all 0.3s ease;
}

.register-link a:hover {
  color: #0d47a1;
  text-decoration: underline;
}

/* 表单样式 - 覆盖 Element Plus 默认 */
:deep(.el-input__wrapper) {
  background: rgba(255, 255, 255, 0.9);
  border: 1px solid #cfd8dc;
  border-radius: 12px;
  box-shadow: none;
  height: 48px;
  transition: all 0.3s ease;
}

:deep(.el-input__wrapper:hover) {
  border-color: #4ab8e8;
  box-shadow: 0 0 12px rgba(74, 184, 232, 0.2);
}

:deep(.el-input__wrapper.is-focus) {
  border-color: #1e88e5;
  box-shadow: 0 0 18px rgba(30, 136, 229, 0.25);
}

:deep(.el-input__inner) {
  color: #263238;
  font-size: 15px;
}

:deep(.el-input__inner::placeholder) {
  color: #b0bec5;
}

:deep(.el-input__prefix .el-icon) {
  color: #78909c;
  font-size: 18px;
}

:deep(.el-form-item__error) {
  color: #e53935;
}

/* 响应式 */
@media (max-width: 900px) {
  .login-panel {
    right: 20px;
    width: 380px;
  }
  .sun { left: 30px; top: 20px; transform: scale(0.7); }
}

@media (max-width: 640px) {
  .login-page { flex-direction: column; }
  .sky { height: 40%; }
  .ocean { height: 25%; }
  .login-panel {
    position: relative;
    right: auto;
    top: auto;
    transform: none;
    width: 92vw;
    margin: -30px auto 20px;
  }
  .sun { transform: scale(0.5); left: 15px; top: 10px; }
}
</style>
