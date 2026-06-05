import { openApiRequest } from './openapiClient'
import type { LoginParams, LoginResponse, RefreshTokenParams, UserInfo } from './model/authModel'

/**
 * 登录
 * @param params 登录参数
 * @returns 登录响应
 */
export function fetchLogin(params: LoginParams): Promise<LoginResponse> {
  return openApiRequest('login', {
    body: params
  })
}

/**
 * 获取用户信息
 * @returns 用户信息
 */
export function fetchGetUserInfo(): Promise<UserInfo> {
  return openApiRequest('getCurrentUser')
}

/**
 * 刷新 Token
 * @param params refresh token 参数
 * @returns 登录响应
 */
export function fetchRefreshToken(params: RefreshTokenParams): Promise<LoginResponse> {
  return openApiRequest('refreshToken', {
    body: params
  })
}
