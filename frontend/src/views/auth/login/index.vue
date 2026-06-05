<!-- 登录页面 -->
<template>
  <div class="login-page">
    <AuthTopBar class="login-top-bar" />

    <main class="login-main">
      <section class="auth-right-wrap">
        <div class="form">
          <div class="form-head">
            <div class="brand">
              <ArtLogo class="brand-logo" size="40" />
            </div>
            <div class="account-control">
              <ArtSvgIcon icon="ri:user-3-line" class="account-icon" />
              <ElSelect v-model="formData.account" class="account-select" @change="setupAccount">
                <ElOption
                  v-for="account in accounts"
                  :key="account.key"
                  :label="account.label"
                  :value="account.key"
                >
                  <span>{{ account.label }}</span>
                </ElOption>
              </ElSelect>
            </div>
          </div>
          <ElForm
            ref="formRef"
            :model="formData"
            :rules="rules"
            :key="formKey"
            class="auth-form"
            @keyup.enter="handleSubmit"
          >
            <ElFormItem prop="username">
              <ElInput
                class="custom-height"
                :placeholder="$t('login.placeholder.username')"
                v-model.trim="formData.username"
              />
            </ElFormItem>
            <ElFormItem prop="password">
              <ElInput
                class="custom-height"
                :placeholder="$t('login.placeholder.password')"
                v-model.trim="formData.password"
                type="password"
                autocomplete="off"
                show-password
              />
            </ElFormItem>

            <!-- 推拽验证 -->
            <div class="verify-row">
              <div
                class="relative z-[2] overflow-hidden select-none rounded-lg border border-transparent tad-300"
                :class="{ '!border-[#FF4E4F]': !isPassing && isClickPass }"
              >
                <ArtDragVerify
                  ref="dragVerify"
                  v-model:value="isPassing"
                  text=""
                  textColor="var(--art-gray-700)"
                  successText=""
                  :progressBarBg="getCssVar('--el-color-primary')"
                  :background="isDark ? '#26272F' : '#F1F1F4'"
                  handlerBg="var(--default-box-color)"
                />
              </div>
              <p
                class="absolute top-0 z-[1] px-px mt-2 text-xs text-[#f56c6c] tad-300"
                :class="{ 'translate-y-10': !isPassing && isClickPass }"
              >
                {{ $t('login.placeholder.slider') }}
              </p>
            </div>

            <div class="form-options">
              <ElTooltip :content="$t('login.rememberPwd')" placement="top">
                <ElCheckbox
                  v-model="formData.rememberPassword"
                  :aria-label="$t('login.rememberPwd')"
                />
              </ElTooltip>
              <div class="form-option-actions">
                <ElTooltip :content="$t('login.forgetPwd')" placement="top">
                  <RouterLink
                    class="icon-link"
                    :aria-label="$t('login.forgetPwd')"
                    :to="{ name: 'ForgetPassword' }"
                  >
                    <ArtSvgIcon icon="ri:key-2-line" />
                  </RouterLink>
                </ElTooltip>
                <ElTooltip :content="$t('login.register')" placement="top">
                  <RouterLink
                    class="icon-link"
                    :aria-label="$t('login.register')"
                    :to="{ name: 'Register' }"
                  >
                    <ArtSvgIcon icon="ri:user-add-line" />
                  </RouterLink>
                </ElTooltip>
              </div>
            </div>

            <div class="action-row">
              <ElTooltip :content="$t('login.btnText')" placement="top">
                <ElButton
                  class="icon-action"
                  type="primary"
                  :aria-label="$t('login.btnText')"
                  @click="handleSubmit"
                  :loading="loading"
                  v-ripple
                >
                  <ArtSvgIcon icon="ri:arrow-right-line" />
                </ElButton>
              </ElTooltip>
              <ElTooltip :content="$t('login.guestBtnText')" placement="top">
                <ElButton
                  class="icon-action"
                  plain
                  :aria-label="$t('login.guestBtnText')"
                  @click="handleGuestLogin"
                  :loading="guestLoading"
                  v-ripple
                >
                  <ArtSvgIcon icon="ri:compass-3-line" />
                </ElButton>
              </ElTooltip>
            </div>
          </ElForm>
        </div>
      </section>
    </main>
  </div>
</template>

<script setup lang="ts">
  import AppConfig from '@/config'
  import { useUserStore } from '@/store/modules/user'
  import { getCssVar } from '@/utils/ui'
  import { useI18n } from 'vue-i18n'
  import { HttpError } from '@/utils/http/error'
  import { fetchLogin } from '@/api/auth'
  import { ElNotification, type FormInstance, type FormRules } from 'element-plus'
  import { useSettingStore } from '@/store/modules/setting'

  defineOptions({ name: 'Login' })

  const settingStore = useSettingStore()
  const { isDark } = storeToRefs(settingStore)
  const { t, locale } = useI18n()
  const formKey = ref(0)

  // 监听语言切换，重置表单
  watch(locale, () => {
    formKey.value++
  })

  type AccountKey = 'super' | 'admin' | 'user'

  export interface Account {
    key: AccountKey
    label: string
    userName: string
    password: string
    roles: string[]
  }

  const accounts = computed<Account[]>(() => [
    {
      key: 'super',
      label: t('login.roles.super'),
      userName: 'Super',
      password: '123456',
      roles: ['R_SUPER']
    },
    {
      key: 'admin',
      label: t('login.roles.admin'),
      userName: 'Admin',
      password: '123456',
      roles: ['R_ADMIN']
    },
    {
      key: 'user',
      label: t('login.roles.user'),
      userName: 'User',
      password: '123456',
      roles: ['R_USER']
    }
  ])

  const dragVerify = ref()

  const userStore = useUserStore()
  const router = useRouter()
  const route = useRoute()
  const isPassing = ref(false)
  const isClickPass = ref(false)

  const systemName = AppConfig.systemInfo.name
  const defaultHomePath = '/dashboard/console'
  const formRef = ref<FormInstance>()

  const resolveLoginRedirect = (redirect?: string) => {
    return redirect && redirect !== '/' ? redirect : defaultHomePath
  }

  const formData = reactive({
    account: '',
    username: '',
    password: '',
    rememberPassword: true
  })

  const rules = computed<FormRules>(() => ({
    username: [{ required: true, message: t('login.placeholder.username'), trigger: 'blur' }],
    password: [{ required: true, message: t('login.placeholder.password'), trigger: 'blur' }]
  }))

  const loading = ref(false)
  const guestLoading = ref(false)

  onMounted(() => {
    setupAccount('super')
  })

  // 设置账号
  const setupAccount = (key: AccountKey) => {
    const selectedAccount = accounts.value.find((account: Account) => account.key === key)
    formData.account = key
    formData.username = selectedAccount?.userName ?? ''
    formData.password = selectedAccount?.password ?? ''
  }

  // 登录
  const handleSubmit = async () => {
    if (!formRef.value) return

    try {
      // 表单验证
      const valid = await formRef.value.validate()
      if (!valid) return

      // 拖拽验证
      if (!isPassing.value) {
        isClickPass.value = true
        return
      }

      loading.value = true

      // 登录请求
      const { username, password } = formData

      const loginRes = await fetchLogin({
        username,
        password
      })

      const token = loginRes.token || loginRes.accessToken
      const refreshToken = loginRes.refreshToken

      if (!token) {
        throw new Error('Login failed - no token received')
      }

      // 存储 token 和登录状态
      userStore.setToken(token, refreshToken)
      userStore.setLoginStatus(true)

      // 登录成功处理
      showLoginSuccessNotice()

      // 获取 redirect 参数，如果存在则跳转到指定页面，否则跳转到首页
      const redirect = route.query.redirect as string
      router.push(resolveLoginRedirect(redirect))
    } catch (error) {
      // 处理 HttpError
      if (error instanceof HttpError) {
        // console.log(error.code)
      } else {
        // 处理非 HttpError
        // ElMessage.error('登录失败，请稍后重试')
        console.error('[Login] Unexpected error:', error)
      }
    } finally {
      loading.value = false
      resetDragVerify()
    }
  }

  // 重置拖拽验证
  const resetDragVerify = () => {
    dragVerify.value.reset()
  }

  // 登录成功提示
  const showLoginSuccessNotice = () => {
    setTimeout(() => {
      ElNotification({
        title: t('login.success.title'),
        type: 'success',
        duration: 2500,
        zIndex: 10000,
        message: `${t('login.success.message')}, ${systemName}!`
      })
    }, 1000)
  }

  // 游客登录
  const handleGuestLogin = async () => {
    try {
      guestLoading.value = true

      // 游客登录使用特殊的 guest token
      const guestToken = 'guest_token'
      
      // 存储 token 和登录状态
      userStore.setToken(guestToken, '')
      userStore.setLoginStatus(true)
      
      // 设置游客用户信息，避免后续请求 /api/user/info
      userStore.setUserInfo({
        userId: 0,
        userName: 'Guest',
        nickName: t('login.guestSuccess.title'),
        avatar: '',
        roles: ['R_GUEST'],
        permissions: []
      })

      // 游客登录成功提示
      setTimeout(() => {
        ElNotification({
          title: t('login.guestSuccess.title'),
          type: 'info',
          duration: 2500,
          zIndex: 10000,
          message: `${t('login.guestSuccess.message')}, ${systemName}!`
        })
      }, 500)

      // 跳转到首页
      const redirect = route.query.redirect as string
      router.push(resolveLoginRedirect(redirect))
    } catch (error) {
      console.error('[Guest Login] Error:', error)
    } finally {
      guestLoading.value = false
    }
  }
</script>

<style scoped>
  @import './style.css';
</style>

<style lang="scss" scoped>
  :deep(.login-top-bar h1) {
    display: none;
  }

  :deep(.el-form-item) {
    margin-bottom: 18px;
  }

  :deep(.el-input__wrapper),
  :deep(.el-select__wrapper) {
    min-height: 44px;
    padding: 0 2px;
    background: transparent;
    border: 0 !important;
    border-bottom: 1px dashed var(--default-border-dashed) !important;
    border-radius: 0;
    box-shadow: none !important;
  }

  :deep(.el-input__wrapper:hover),
  :deep(.el-select__wrapper:hover) {
    border-bottom-color: var(--theme-color) !important;
    box-shadow: none !important;
  }

  :deep(.el-input__wrapper.is-focus),
  :deep(.el-select__wrapper.is-focused) {
    background: transparent;
    border-bottom-color: #111820 !important;
    box-shadow: none !important;
  }

  :deep(.el-input__inner) {
    color: #111820;
    font-size: 14px;
  }

  :deep(.el-input__inner::placeholder) {
    color: #9aa3ad;
  }

  :deep(.account-select) {
    width: 100%;
    transition: width 0.2s ease;
  }

  :deep(.account-select .el-select__wrapper) {
    min-height: 34px;
    padding: 0 12px;
    border: 1px dashed var(--default-glass-border) !important;
    border-radius: 999px;
    box-shadow: none !important;
  }

  :deep(.account-select .el-select__selected-item) {
    opacity: 0;
    transition: opacity 0.16s ease;
  }

  .account-control:hover :deep(.account-select .el-select__selected-item),
  .account-control:focus-within :deep(.account-select .el-select__selected-item) {
    opacity: 1;
  }

  :deep(.account-select .el-select__suffix) {
    opacity: 0;
    transition: opacity 0.16s ease;
  }

  .account-control:hover :deep(.account-select .el-select__suffix),
  .account-control:focus-within :deep(.account-select .el-select__suffix) {
    opacity: 1;
  }

  :deep(.drag_verify) {
    overflow: hidden;
    border: 1px dashed var(--default-glass-border) !important;
    box-shadow: none !important;
  }

  :deep(.drag_verify .dv_handler) {
    border-right: 1px dashed var(--default-glass-border);
    box-shadow: none;
  }

  :deep(.drag_verify .dv_text) {
    font-size: 0;
  }

  :deep(.el-button) {
    border-radius: 8px;
  }

  :deep(.el-button--primary) {
    border-color: #101820;
    color: #fff;
    background-color: #101820;
  }

  :deep(.el-button--primary:hover),
  :deep(.el-button--primary:focus) {
    border-color: #26313d;
    color: #fff;
    background-color: #26313d;
  }

  :deep(.el-checkbox__input.is-checked .el-checkbox__inner) {
    border-color: #101820;
    background-color: #101820;
  }

  :deep(.el-checkbox__label) {
    display: none;
  }

  :deep(.icon-action .art-svg-icon),
  .icon-link :deep(.art-svg-icon) {
    font-size: 18px;
    color: inherit !important;
  }

  :global(.dark) :deep(.el-input__wrapper),
  :global(.dark) :deep(.el-select__wrapper) {
    background: transparent;
    border-bottom-color: var(--default-border-dashed) !important;
    box-shadow: none !important;
  }

  :global(.dark) :deep(.el-input__wrapper.is-focus),
  :global(.dark) :deep(.el-select__wrapper.is-focused) {
    background: transparent;
    border-bottom-color: rgb(255 255 255 / 72%) !important;
    box-shadow: none !important;
  }

  :global(.dark) :deep(.account-select .el-select__wrapper) {
    border-color: var(--default-glass-border) !important;
    box-shadow: none !important;
  }

  :global(.dark) :deep(.el-input__inner) {
    color: #f8fafc;
  }

  :global(.dark) :deep(.drag_verify) {
    border-color: var(--default-glass-border) !important;
    box-shadow: none !important;
  }
</style>
