/**
 * API path registry.
 *
 * Keep endpoint strings here so API modules and generated code reuse one contract.
 */

const encodePathValue = (value: string | number): string => encodeURIComponent(String(value))

export const ApiPaths = {
  auth: {
    login: '/api/auth/login',
    logout: '/api/auth/logout',
    me: '/api/auth/me',
    verify: '/api/auth/verify',
    currentUser: '/api/auth/current-user',
    verify2FA: '/auth/login/verify-2fa',
    sendOtp: '/auth/otp/send',
    binding: '/auth/binding',
    bindStore: (storeNo: string) => `/auth/binding/${encodePathValue(storeNo)}`,
    tenantMembers: '/auth/tenant/account/list',
    userInfo: '/auth/user/info',
    register: '/auth/register',
    updatePassword: '/auth/user/password',
    userBasic: '/auth/user/basic',
    avatarUpload: '/auth/user/avatar'
  },
  system: {
    users: '/api/user/list',
    roles: '/api/role/list',
    simpleMenus: '/api/v3/system/menus/simple'
  }
} as const

export type ApiPaths = typeof ApiPaths
