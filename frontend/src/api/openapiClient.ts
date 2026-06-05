import request from '@/utils/http'
import {
  OPENAPI_OPERATIONS,
  type OpenApiHttpMethod,
  type OpenApiOperationData,
  type OpenApiOperationId,
  type OpenApiOperationRequest
} from '@/contracts/openapi/operations'
import { resolveApiPath } from './paths'

export type OpenApiPathParams = Record<string, string | number>
export type OpenApiQueryParams = Record<string, unknown>

export interface OpenApiRequestOptions<T extends OpenApiOperationId> {
  pathParams?: OpenApiPathParams
  query?: OpenApiQueryParams
  body?: OpenApiOperationRequest<T>
  showErrorMessage?: boolean
  showSuccessMessage?: boolean
}

interface OpenApiRuntimeRequestConfig {
  url: string
  method: OpenApiHttpMethod
  params?: OpenApiQueryParams
  data?: unknown
  showErrorMessage?: boolean
  showSuccessMessage?: boolean
}

const BODY_METHODS = new Set<OpenApiHttpMethod>(['POST', 'PUT', 'PATCH'])
const PATH_PARAM_PATTERN = /\{([^}]+)\}/g

export const bindOpenApiPathParams = (
  apiPath: string,
  params: OpenApiPathParams = {}
): string => {
  return apiPath.replace(PATH_PARAM_PATTERN, (token, name: string) => {
    const value = params[name]
    if (value === undefined) {
      throw new Error(`Missing OpenAPI path param: ${name}`)
    }
    return encodeURIComponent(String(value))
  })
}

export const openApiPath = <T extends OpenApiOperationId>(
  operationId: T,
  pathParams?: OpenApiPathParams
): string => {
  return bindOpenApiPathParams(OPENAPI_OPERATIONS[operationId].path, pathParams)
}

export const resolveOpenApiPath = <T extends OpenApiOperationId>(
  operationId: T,
  pathParams?: OpenApiPathParams
): string => {
  return resolveApiPath(openApiPath(operationId, pathParams))
}

export function openApiRequest<T extends OpenApiOperationId>(
  operationId: T,
  options: OpenApiRequestOptions<T> = {}
): Promise<OpenApiOperationData<T>> {
  const operation = OPENAPI_OPERATIONS[operationId]
  const config: OpenApiRuntimeRequestConfig = {
    url: openApiPath(operationId, options.pathParams),
    method: operation.method,
    showErrorMessage: options.showErrorMessage,
    showSuccessMessage: options.showSuccessMessage
  }

  if (options.query) {
    config.params = options.query
  }

  if (BODY_METHODS.has(operation.method) && options.body !== undefined) {
    config.data = options.body
  }

  return request.request<OpenApiOperationData<T>>(config)
}
