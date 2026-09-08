<template>
  <div class="ai-fab-root">
    <!-- 召唤出的对话面板 -->
    <transition name="ai-panel">
      <div v-show="open" class="ai-panel">
        <div class="ai-panel-header">
          <div class="ai-panel-title">
            <span class="ai-orb"></span>
            <span>{{ lang.t('ai.title') }}</span>
          </div>
          <div class="ai-panel-actions">
            <button class="ai-lang-btn" :title="lang.t('header.langTip')" @click.stop="lang.toggle()">
              {{ lang.lang === 'zh' ? 'EN' : '中' }}
            </button>
            <el-button text size="small" class="clear-btn" :disabled="aiStore.messages.length === 0" @click="aiStore.clear()">
              {{ lang.t('ai.clear') }}
            </el-button>
            <el-button text size="small" class="close-btn" @click="open = false">
              <el-icon><Close /></el-icon>
            </el-button>
          </div>
        </div>

        <div ref="scrollRef" class="ai-panel-body">
          <div v-if="aiStore.messages.length === 0" class="ai-empty">
            <div class="empty-avatar">
              <el-icon :size="30"><Service /></el-icon>
            </div>
            <p class="empty-title">{{ lang.t('ai.greetTitle') }}</p>
            <p class="empty-desc">{{ lang.t('ai.greetDesc') }}</p>
            <div class="empty-hints">
              <el-tag v-for="s in aiStore.suggestions" :key="s" class="hint-tag" effect="plain" @click="pick(s)">
                {{ s }}
              </el-tag>
            </div>
          </div>

          <div
            v-for="(msg, index) in aiStore.messages"
            :key="index"
            class="msg-row"
            :class="msg.role === 'user' ? 'row-user' : 'row-bot'"
          >
            <div class="msg-avatar">
              <el-icon v-if="msg.role === 'user'"><User /></el-icon>
              <el-icon v-else><Service /></el-icon>
            </div>
            <div class="msg-bubble" :class="{ 'bubble-error': msg.intent === 'error' }">
              <pre class="msg-text">{{ msg.content }}</pre>
              <span class="msg-time">{{ formatTime(msg.time) }}</span>
            </div>
          </div>

          <div v-if="aiStore.loading" class="msg-row row-bot">
            <div class="msg-avatar"><el-icon><Service /></el-icon></div>
            <div class="msg-bubble">
              <span class="typing">{{ lang.t('ai.thinking') }}<span class="dots">...</span></span>
            </div>
          </div>
        </div>

        <div class="ai-panel-input">
          <el-input
            v-model="input"
            type="textarea"
            :rows="2"
            resize="none"
            :placeholder="lang.t('ai.placeholder')"
            :disabled="aiStore.loading"
            @keydown.enter.exact.prevent="submit"
          />
          <el-button type="primary" :loading="aiStore.loading" :disabled="!input.trim()" @click="submit">
            {{ lang.t('ai.send') }}
          </el-button>
        </div>
      </div>
    </transition>

    <!-- 悬浮召唤按钮 -->
    <transition name="ai-fab">
      <button v-show="!open" class="ai-fab" :title="lang.t('ai.fabTip')" @click="open = true">
        <span class="ai-fab-ring"></span>
        <el-icon class="ai-fab-icon"><Service /></el-icon>
        <span class="ai-fab-badge" />
      </button>
    </transition>
  </div>
</template>

<script setup lang="ts">
import { nextTick, ref, watch } from 'vue'
import { Close, Service, User } from '@element-plus/icons-vue'
import { useAIStore } from '@/stores/ai'
import { useLang } from '@/stores/lang'

const aiStore = useAIStore()
const lang = useLang()
const open = ref(false)
const input = ref('')
const scrollRef = ref<HTMLElement | null>(null)

function submit() {
  const text = input.value.trim()
  if (!text || aiStore.loading) return
  input.value = ''
  aiStore.send(text)
}

function pick(text: string) {
  if (aiStore.loading) return
  aiStore.send(text)
}

function formatTime(iso: string) {
  const d = new Date(iso)
  if (Number.isNaN(d.getTime())) return ''
  return d.toLocaleTimeString(lang.lang === 'zh' ? 'zh-CN' : 'en-AU', { hour12: false })
}

// 展开时按当前语言加载常见问题（切换语言后 suggestions 会被清空）
watch(open, (v) => {
  if (v) {
    aiStore.loadSuggestions()
  }
})

// 新消息自动滚到底部
watch(
  () => [aiStore.messages.length, aiStore.loading],
  async () => {
    await nextTick()
    if (scrollRef.value) {
      scrollRef.value.scrollTop = scrollRef.value.scrollHeight
    }
  }
)
</script>

<style scoped>
.ai-fab {
  position: fixed;
  right: 28px;
  bottom: 28px;
  width: 60px;
  height: 60px;
  border-radius: 50%;
  border: none;
  cursor: pointer;
  z-index: 3000;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #38bdf8 0%, #2563eb 55%, #7c3aed 100%);
  box-shadow: 0 10px 30px rgba(37, 99, 235, 0.5), 0 0 0 1px rgba(125, 211, 252, 0.35) inset;
  transition: transform 0.25s cubic-bezier(0.34, 1.56, 0.64, 1), box-shadow 0.25s ease;
}
.ai-fab:hover {
  transform: translateY(-4px) scale(1.06);
  box-shadow: 0 16px 40px rgba(37, 99, 235, 0.65), 0 0 0 1px rgba(125, 211, 252, 0.5) inset;
}
.ai-fab:active {
  transform: scale(0.94);
}
.ai-fab-ring {
  position: absolute;
  inset: -6px;
  border-radius: 50%;
  border: 1.5px solid rgba(56, 189, 248, 0.45);
  animation: fabRing 2.4s ease-out infinite;
  pointer-events: none;
}
@keyframes fabRing {
  0% { transform: scale(0.86); opacity: 0.9; }
  70%, 100% { transform: scale(1.25); opacity: 0; }
}
.ai-fab-icon {
  font-size: 26px;
  color: #fff;
  filter: drop-shadow(0 2px 6px rgba(0, 0, 0, 0.35));
}
.ai-fab-badge {
  position: absolute;
  top: 2px;
  right: 2px;
  width: 13px;
  height: 13px;
  border-radius: 50%;
  background: #34d399;
  border: 2.5px solid #0b1220;
  box-shadow: 0 0 10px rgba(52, 211, 153, 0.9);
}

.ai-panel {
  position: fixed;
  right: 28px;
  bottom: 100px;
  width: 396px;
  max-width: calc(100vw - 32px);
  height: 580px;
  max-height: calc(100vh - 140px);
  z-index: 3000;
  display: flex;
  flex-direction: column;
  background: linear-gradient(180deg, rgba(16, 34, 56, 0.96), rgba(8, 19, 34, 0.97));
  backdrop-filter: blur(18px);
  color: #e5e7eb;
  border-radius: 18px;
  overflow: hidden;
  border: 1px solid rgba(56, 189, 248, 0.22);
  box-shadow: 0 28px 70px rgba(2, 8, 23, 0.65), 0 0 0 1px rgba(125, 211, 252, 0.06) inset;
}

.ai-panel-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 13px 16px;
  background: linear-gradient(90deg, rgba(37, 99, 235, 0.35), rgba(124, 58, 237, 0.28));
  border-bottom: 1px solid rgba(56, 189, 248, 0.18);
}
.ai-panel-title {
  display: flex;
  align-items: center;
  gap: 9px;
  font-size: 15px;
  font-weight: 700;
  color: #dbeafe;
  letter-spacing: 0.3px;
}
.ai-orb {
  width: 10px;
  height: 10px;
  border-radius: 50%;
  background: radial-gradient(circle at 35% 35%, #7dd3fc, #2563eb);
  box-shadow: 0 0 10px rgba(56, 189, 248, 0.9);
  animation: orbPulse 2s ease-in-out infinite;
}
@keyframes orbPulse {
  0%, 100% { box-shadow: 0 0 6px rgba(56, 189, 248, 0.6); }
  50% { box-shadow: 0 0 14px rgba(56, 189, 248, 1); }
}
.ai-panel-actions {
  display: flex;
  align-items: center;
  gap: 4px;
}
.ai-lang-btn {
  border: 1px solid rgba(125, 211, 252, 0.45);
  background: rgba(13, 31, 49, 0.6);
  color: #93c5fd;
  font-size: 11.5px;
  font-weight: 700;
  padding: 3px 9px;
  border-radius: 999px;
  cursor: pointer;
  transition: all 0.15s ease;
}
.ai-lang-btn:hover {
  background: rgba(56, 189, 248, 0.2);
  color: #e0f2fe;
}
.clear-btn,
.close-btn {
  color: #9ca3af;
}

.ai-panel-body {
  flex: 1;
  overflow-y: auto;
  padding: 16px;
  display: flex;
  flex-direction: column;
  gap: 12px;
}
.ai-empty {
  margin: auto;
  max-width: 310px;
  text-align: center;
}
.empty-avatar {
  width: 62px;
  height: 62px;
  margin: 0 auto 12px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #bfdbfe;
  background: linear-gradient(135deg, rgba(56, 189, 248, 0.25), rgba(124, 58, 237, 0.25));
  border: 1px solid rgba(125, 211, 252, 0.35);
  box-shadow: 0 8px 24px rgba(37, 99, 235, 0.3);
}
.empty-title {
  font-size: 16px;
  font-weight: 600;
  color: #dbeafe;
  margin: 0 0 8px;
}
.empty-desc {
  font-size: 12px;
  line-height: 1.7;
  color: #94a3b8;
  margin: 0 0 16px;
}
.empty-hints {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  justify-content: center;
}
.hint-tag {
  cursor: pointer;
  background: transparent;
  border-color: rgba(56, 189, 248, 0.35);
  color: #93c5fd;
  transition: all 0.15s ease;
}
.hint-tag:hover {
  background: rgba(56, 189, 248, 0.14);
  color: #bfdbfe;
  transform: translateY(-1px);
}

.msg-row {
  display: flex;
  gap: 8px;
  align-items: flex-start;
  animation: msgIn 0.25s ease;
}
@keyframes msgIn {
  from { opacity: 0; transform: translateY(8px); }
  to { opacity: 1; transform: none; }
}
.row-user {
  flex-direction: row-reverse;
}
.msg-avatar {
  flex: 0 0 28px;
  height: 28px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, rgba(56, 189, 248, 0.25), rgba(56, 189, 248, 0.1));
  border: 1px solid rgba(56, 189, 248, 0.3);
  color: #93c5fd;
  font-size: 14px;
}
.row-user .msg-avatar {
  background: linear-gradient(135deg, rgba(167, 139, 250, 0.3), rgba(167, 139, 250, 0.12));
  border-color: rgba(167, 139, 250, 0.35);
  color: #ddd6fe;
}
.msg-bubble {
  max-width: 80%;
  background: rgba(22, 50, 77, 0.8);
  border: 1px solid rgba(56, 189, 248, 0.14);
  border-radius: 12px;
  border-top-left-radius: 4px;
  padding: 9px 13px;
}
.row-user .msg-bubble {
  background: linear-gradient(135deg, #2563eb, #1d4ed8);
  border-color: rgba(96, 165, 250, 0.4);
  border-radius: 12px;
  border-top-right-radius: 4px;
  color: #f9fafb;
}
.bubble-error {
  border-color: rgba(248, 113, 113, 0.4);
  background: rgba(69, 20, 25, 0.6);
}
.msg-text {
  margin: 0;
  font-family: inherit;
  font-size: 12.5px;
  line-height: 1.75;
  white-space: pre-wrap;
  word-break: break-word;
}
.msg-time {
  display: block;
  margin-top: 4px;
  font-size: 10px;
  color: #64748b;
}
.typing {
  font-size: 12.5px;
  color: #93c5fd;
}
.dots {
  animation: blink 1.2s infinite;
}
@keyframes blink {
  0%, 20% { opacity: 0.2; }
  50% { opacity: 1; }
  100% { opacity: 0.2; }
}

.ai-panel-input {
  display: flex;
  gap: 8px;
  align-items: flex-end;
  padding: 12px 14px;
  background: rgba(13, 28, 47, 0.85);
  border-top: 1px solid rgba(56, 189, 248, 0.14);
}
.ai-panel-input :deep(.el-textarea__inner) {
  background: rgba(8, 19, 34, 0.9);
  border-color: rgba(56, 189, 248, 0.2);
  color: #e5e7eb;
  box-shadow: none;
  border-radius: 10px;
}
.ai-panel-input :deep(.el-textarea__inner:focus) {
  border-color: rgba(56, 189, 248, 0.5);
}

/* 展开/收起动画 */
.ai-panel-enter-active,
.ai-panel-leave-active {
  transition: opacity 0.22s ease, transform 0.22s ease;
  transform-origin: bottom right;
}
.ai-panel-enter-from,
.ai-panel-leave-to {
  opacity: 0;
  transform: translateY(18px) scale(0.95);
}
.ai-fab-enter-active,
.ai-fab-leave-active {
  transition: opacity 0.2s ease, transform 0.2s ease;
}
.ai-fab-enter-from,
.ai-fab-leave-to {
  opacity: 0;
  transform: scale(0.6);
}
</style>
