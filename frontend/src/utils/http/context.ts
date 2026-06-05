import { LanguageEnum } from '@/enums/appEnum'
import {
  DEFAULT_LOCALE,
  FRONTEND_PROPAGATED_HEADER_KEYS,
  REQUEST_HEADERS,
  SUPPORTED_LOCALES,
  type PlatformFrontendPropagatedHeaderKey,
  type PlatformSupportedLocale
} from '@/contracts/platform-contract'
import { getClientTimeZone } from '@/utils/time'

interface HeaderWriter {
  set(name: string, value: string): void
}

export { FRONTEND_PROPAGATED_HEADER_KEYS, REQUEST_HEADERS }

export interface FrontendRequestContext
  extends Record<PlatformFrontendPropagatedHeaderKey, string> {
  requestId: string
  traceId: string
  timeZone: string
  acceptLanguage: string
}

const LANGUAGE_TAGS: Record<LanguageEnum, PlatformSupportedLocale> = {
  [LanguageEnum.ZH]: 'zh-CN',
  [LanguageEnum.EN]: 'en-US'
}

const isSupportedLocale = (locale: string): locale is PlatformSupportedLocale =>
  (SUPPORTED_LOCALES as readonly string[]).includes(locale)

const matchSupportedLocale = (locale?: string): PlatformSupportedLocale => {
  if (!locale) return DEFAULT_LOCALE
  if (isSupportedLocale(locale)) return locale

  const language = locale.split('-')[0]
  return SUPPORTED_LOCALES.find((supported) => supported.split('-')[0] === language) || DEFAULT_LOCALE
}

const SESSION_TRACE_ID_KEY = 'anjing-trace-id'

let memoryTraceId: string | null = null

export const createRequestId = (): string => {
  if (globalThis.crypto?.randomUUID) {
    return globalThis.crypto.randomUUID()
  }

  return `web-${Date.now().toString(36)}-${Math.random().toString(36).slice(2, 10)}`
}

const readSessionTraceId = (): string | null => {
  try {
    return globalThis.sessionStorage?.getItem(SESSION_TRACE_ID_KEY) || memoryTraceId
  } catch {
    return memoryTraceId
  }
}

const writeSessionTraceId = (traceId: string): void => {
  memoryTraceId = traceId

  try {
    globalThis.sessionStorage?.setItem(SESSION_TRACE_ID_KEY, traceId)
  } catch {
    // Browsers may block storage in private or embedded contexts; memory fallback still works.
  }
}

export const getOrCreateTraceId = (): string => {
  const traceId = readSessionTraceId()
  if (traceId) return traceId

  const nextTraceId = createRequestId()
  writeSessionTraceId(nextTraceId)
  return nextTraceId
}

export const resetTraceId = (): string => {
  const traceId = createRequestId()
  writeSessionTraceId(traceId)
  return traceId
}

export const getLanguageTag = (language?: LanguageEnum): string => {
  if (language && LANGUAGE_TAGS[language]) return LANGUAGE_TAGS[language]
  return matchSupportedLocale(globalThis.navigator?.language)
}

export const buildRequestContext = (language?: LanguageEnum): FrontendRequestContext => ({
  requestId: createRequestId(),
  traceId: getOrCreateTraceId(),
  timeZone: getClientTimeZone(),
  acceptLanguage: getLanguageTag(language)
})

export const buildRequestContextHeaders = (language?: LanguageEnum): Record<string, string> => {
  const context = buildRequestContext(language)

  return FRONTEND_PROPAGATED_HEADER_KEYS.reduce<Record<string, string>>((headers, key) => {
    headers[REQUEST_HEADERS[key]] = context[key]
    return headers
  }, {})
}

export const applyRequestContextHeaders = (
  headers: HeaderWriter,
  language?: LanguageEnum
): void => {
  Object.entries(buildRequestContextHeaders(language)).forEach(([name, value]) => {
    headers.set(name, value)
  })
}
