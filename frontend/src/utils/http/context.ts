import { LanguageEnum } from '@/enums/appEnum'
import { getClientTimeZone } from '@/utils/time'

interface HeaderWriter {
  set(name: string, value: string): void
}

export const REQUEST_HEADERS = {
  requestId: 'X-Request-Id',
  traceId: 'X-Trace-Id',
  timeZone: 'X-Time-Zone',
  acceptLanguage: 'Accept-Language'
} as const

const LANGUAGE_TAGS: Record<LanguageEnum, string> = {
  [LanguageEnum.ZH]: 'zh-CN',
  [LanguageEnum.EN]: 'en-US'
}

const createRequestId = (): string => {
  if (globalThis.crypto?.randomUUID) {
    return globalThis.crypto.randomUUID()
  }

  return `web-${Date.now().toString(36)}-${Math.random().toString(36).slice(2, 10)}`
}

export const getLanguageTag = (language?: LanguageEnum): string => {
  if (language && LANGUAGE_TAGS[language]) return LANGUAGE_TAGS[language]
  return navigator.language || LANGUAGE_TAGS[LanguageEnum.ZH]
}

export const applyRequestContextHeaders = (
  headers: HeaderWriter,
  language?: LanguageEnum
): void => {
  const requestId = createRequestId()

  headers.set(REQUEST_HEADERS.requestId, requestId)
  headers.set(REQUEST_HEADERS.traceId, requestId)
  headers.set(REQUEST_HEADERS.timeZone, getClientTimeZone())
  headers.set(REQUEST_HEADERS.acceptLanguage, getLanguageTag(language))
}
