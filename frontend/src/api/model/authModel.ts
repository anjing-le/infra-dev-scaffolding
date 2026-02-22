/**
 * 认证模块类型定义
 *
 * @module api/model/authModel
 */

/** 登录参数 */
export interface LoginParams {
  username: string
  password: string
}

/** 登录响应（兼容后端 accessToken/refreshToken 字段） */
export interface LoginResponse {
  token?: string
  accessToken?: string
  refreshToken?: string
  tokenType?: string
  expiresIn?: number
}

/** 用户信息 */
export interface UserInfo {
  buttons?: string[]
  permissions?: string[]
  roles: string[]
  userId: number
  userName: string
  nickName?: string
  email?: string
  avatar?: string
}

