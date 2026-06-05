/**
 * 认证模块类型定义
 *
 * @module api/model/authModel
 */

import type {
  AuthTokenResponse,
  CurrentUserResponse,
  LoginRequest,
  RefreshTokenRequest
} from '@/contracts/openapi/schemas'

/** 登录参数 */
export type LoginParams = LoginRequest

/** 登录响应（兼容后端 accessToken/refreshToken 字段） */
export type LoginResponse = AuthTokenResponse & {
  token?: string
}

/** 用户信息 */
export type UserInfo = CurrentUserResponse & {
  buttons?: string[]
}

/** 刷新 Token 参数 */
export type RefreshTokenParams = RefreshTokenRequest
