/**
 * API 响应类型定义模块
 *
 * 提供统一的 API 响应结构类型定义
 *
 * ## 主要功能
 *
 * - 基础响应结构定义
 * - 泛型支持（适配不同数据类型）
 * - 统一的响应格式约束
 *
 * ## 使用场景
 *
 * - API 请求响应类型约束
 * - 接口数据类型定义
 * - 响应数据解析
 *
 * @module types/common/response
 * @author Art Design Pro Team
 */

/** 基础 API 响应结构（兼容后端 code:"0"/message 和前端 code:200/msg 两种格式） */
export interface BaseResponse<T = unknown> {
  /** 状态码（后端返回字符串 "0"，前端约定数字 200） */
  code: number | string
  /** 消息（前端字段名） */
  msg?: string
  /** 消息（后端字段名） */
  message?: string
  /** 数据 */
  data: T
  /** 时间戳（后端返回） */
  timestamp?: number
  /** 请求 ID，用于前后端日志关联 */
  requestId?: string
}

/** 旧接口层使用的响应命名，保留为 BaseResponse 的兼容别名 */
export type BaseResult<T = unknown> = BaseResponse<T>

/** 分页列表响应 */
export type PaginatedResponse<T = unknown> = Api.Common.PaginatedResponse<T>

/** 通用分页搜索参数 */
export type CommonSearchParams = Api.Common.CommonSearchParams
