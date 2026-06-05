import { DEFAULT_TIME_ZONE } from '@/contracts/platform-contract'

export type DateInput = Date | string | number | null | undefined

export interface FormatDateTimeOptions extends Intl.DateTimeFormatOptions {
  locale?: string
  timeZone?: string
}

export { DEFAULT_TIME_ZONE }

export const getClientTimeZone = (): string => {
  try {
    return Intl.DateTimeFormat().resolvedOptions().timeZone || DEFAULT_TIME_ZONE
  } catch {
    return DEFAULT_TIME_ZONE
  }
}

export const toDate = (value: DateInput): Date | null => {
  if (value === null || value === undefined || value === '') return null
  const date = value instanceof Date ? value : new Date(value)
  return Number.isNaN(date.getTime()) ? null : date
}

export const toIsoString = (value: DateInput): string | null => {
  const date = toDate(value)
  return date ? date.toISOString() : null
}

export const formatDateTime = (
  value: DateInput,
  options: FormatDateTimeOptions = {}
): string => {
  const date = toDate(value)
  if (!date) return ''

  const {
    locale = navigator.language || 'zh-CN',
    timeZone = getClientTimeZone(),
    ...dateTimeOptions
  } = options

  return new Intl.DateTimeFormat(locale, {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit',
    second: '2-digit',
    hour12: false,
    timeZone,
    ...dateTimeOptions
  }).format(date)
}

export const formatDate = (
  value: DateInput,
  options: FormatDateTimeOptions = {}
): string => {
  return formatDateTime(value, {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: undefined,
    minute: undefined,
    second: undefined,
    ...options
  })
}
