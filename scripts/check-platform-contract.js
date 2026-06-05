#!/usr/bin/env node
const fs = require('fs')
const path = require('path')

const root = path.resolve(__dirname, '..')
const contractPath = path.join(root, 'contracts/platform-contract.json')

const files = {
  backendPlatform: 'backend/src/main/java/com/anjing/model/constants/PlatformContractConstants.java',
  apiConstants: 'backend/src/main/java/com/anjing/model/constants/ApiConstants.java',
  requestHeaders: 'backend/src/main/java/com/anjing/model/constants/RequestHeaderConstants.java',
  apiResponse: 'backend/src/main/java/com/anjing/model/response/APIResponse.java',
  pageResult: 'backend/src/main/java/com/anjing/model/response/PageResult.java',
  frontendPlatform: 'frontend/src/contracts/platform-contract.ts',
  frontendContext: 'frontend/src/utils/http/context.ts',
  frontendResponse: 'frontend/src/utils/http/response.ts',
  frontendResponseTypes: 'frontend/src/types/common/response.ts',
  frontendApiTypes: 'frontend/src/types/api/api.d.ts',
  frontendTime: 'frontend/src/utils/time/index.ts',
  errorGuide: 'project_document/ERROR_CODE_GUIDE.md',
  apiContractGuide: 'project_document/API_CONTRACT_GUIDE.md'
}

function fail(message) {
  console.error(`check-platform-contract: ${message}`)
  process.exit(1)
}

function read(relativeFile) {
  const file = path.join(root, relativeFile)
  if (!fs.existsSync(file)) {
    fail(`missing required file: ${relativeFile}`)
  }
  return fs.readFileSync(file, 'utf8')
}

function requireToken(relativeFile, token) {
  const source = read(relativeFile)
  if (!source.includes(token)) {
    fail(`${relativeFile} is missing token: ${token}`)
  }
}

function requirePattern(relativeFile, pattern, description) {
  const source = read(relativeFile)
  if (!pattern.test(source)) {
    fail(`${relativeFile} is missing ${description}: ${pattern}`)
  }
}

if (!fs.existsSync(contractPath)) {
  fail('missing contracts/platform-contract.json')
}

let contract
try {
  contract = JSON.parse(fs.readFileSync(contractPath, 'utf8'))
} catch (error) {
  fail(`invalid contracts/platform-contract.json: ${error.message}`)
}

if (contract.schemaVersion !== 1) {
  fail('schemaVersion must be 1')
}

if (contract.apiPrefix !== '/api') {
  fail('apiPrefix must be /api')
}

requireToken(files.backendPlatform, `API_PREFIX = "${contract.apiPrefix}"`)
requireToken(files.apiConstants, 'API_PREFIX = PlatformContractConstants.API_PREFIX')
requireToken(files.frontendPlatform, `"apiPrefix": "${contract.apiPrefix}"`)

const responseFields = contract.responseEnvelope?.fields || []
if (!responseFields.length) {
  fail('responseEnvelope.fields must not be empty')
}

requireToken(files.backendPlatform, `SUCCESS_CODE = "${contract.responseEnvelope.successCode}"`)
requireToken(files.apiResponse, 'SUCCESS_CODE = PlatformContractConstants.Response.SUCCESS_CODE')
requireToken(files.frontendPlatform, `"successCode": "${contract.responseEnvelope.successCode}"`)
requireToken(files.frontendResponse, "import { API_SUCCESS_CODE }")

for (const field of responseFields) {
  requirePattern(files.apiResponse, new RegExp(`private\\s+[\\w<>]+\\s+${field}\\s*;`), `APIResponse field ${field}`)
  requireToken(files.backendPlatform, `"${field}"`)
  requireToken(files.frontendPlatform, `"${field}"`)
  requirePattern(files.frontendResponseTypes, new RegExp(`\\b${field}\\??:`), `frontend response field ${field}`)
  requireToken(files.apiContractGuide, `\`${field}\``)
}

const paginationFields = contract.pagination?.fields || []
if (!paginationFields.length) {
  fail('pagination.fields must not be empty')
}

for (const field of paginationFields) {
  requirePattern(files.pageResult, new RegExp(`private\\s+[\\w<>]+\\s+${field}\\s*;`), `PageResult field ${field}`)
  requireToken(files.backendPlatform, `"${field}"`)
  requireToken(files.frontendPlatform, `"${field}"`)
  requirePattern(files.frontendApiTypes, new RegExp(`\\b${field}:`), `frontend pagination field ${field}`)
  requireToken(files.apiContractGuide, `"${field}"`)
}

const requestHeaders = contract.requestHeaders || {}
requireToken(files.frontendPlatform, 'FRONTEND_PROPAGATED_HEADER_KEYS = PLATFORM_CONTRACT.frontendPropagatedHeaders')
for (const [key, value] of Object.entries(requestHeaders)) {
  requireToken(files.backendPlatform, `"${value}"`)
  requireToken(files.requestHeaders, `PlatformContractConstants.Headers.${key.replace(/([a-z0-9])([A-Z])/g, '$1_$2').toUpperCase()}`)
  requireToken(files.frontendPlatform, `"${key}": "${value}"`)

  if (contract.frontendPropagatedHeaders?.includes(key)) {
    requireToken(files.frontendContext, 'FRONTEND_PROPAGATED_HEADER_KEYS')
    requireToken(files.frontendContext, 'REQUEST_HEADERS[key]')
    requireToken(files.frontendContext, `${key}:`)
  }
}

requireToken(files.frontendPlatform, `"defaultTimeZone": "${contract.time.defaultTimeZone}"`)
requireToken(files.backendPlatform, `DEFAULT_TIME_ZONE = "${contract.time.defaultTimeZone}"`)
requireToken(files.frontendTime, "import { DEFAULT_TIME_ZONE }")
requireToken(files.frontendPlatform, `"clientTimeZoneHeader": "${contract.time.clientTimeZoneHeader}"`)
requireToken(files.frontendPlatform, `"localeHeader": "${contract.time.localeHeader}"`)
requireToken(files.frontendContext, 'timeZone: getClientTimeZone()')
requireToken(files.frontendContext, 'acceptLanguage: getLanguageTag(language)')
requireToken(files.apiResponse, contract.time.serverCurrentTimeSource)

for (const item of contract.errorCodeRanges || []) {
  requireToken(files.backendPlatform, `"${item.range}"`)
  requireToken(files.errorGuide, `\`${item.range}\``)
  requireToken(files.errorGuide, item.name)
}

for (const range of contract.retryableErrorCodeRanges || []) {
  requireToken(files.backendPlatform, `"${range}"`)
  requireToken(files.errorGuide, `\`${range}\``)
}

console.log('check-platform-contract: ok')
