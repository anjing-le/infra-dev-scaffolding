#!/usr/bin/env node
const fs = require('fs')
const path = require('path')

const root = path.resolve(__dirname, '..')
const contractPath = path.join(root, 'contracts/platform-contract.json')
const outputPath = path.join(
  root,
  'backend/src/main/java/com/anjing/model/constants/PlatformContractConstants.java'
)
const checkMode = process.argv.includes('--check')

function fail(message) {
  console.error(`generate-platform-contract-backend: ${message}`)
  process.exit(1)
}

function javaString(value) {
  return JSON.stringify(String(value))
}

function javaArray(values) {
  return `{ ${values.map(javaString).join(', ')} }`
}

function upperSnake(value) {
  return String(value)
    .replace(/([a-z0-9])([A-Z])/g, '$1_$2')
    .replace(/[^a-zA-Z0-9]+/g, '_')
    .replace(/^_|_$/g, '')
    .toUpperCase()
}

function generateContent(contract) {
  const headers = Object.entries(contract.requestHeaders)
    .map(([key, value]) => `        public static final String ${upperSnake(key)} = ${javaString(value)};`)
    .join('\n')
  const errorRanges = contract.errorCodeRanges.map((item) => item.range)

  return `package com.anjing.model.constants;\n\n/**\n * Generated from contracts/platform-contract.json. Do not edit manually.\n * Run: node scripts/generate-platform-contract-backend.js\n */\npublic final class PlatformContractConstants {\n\n    public static final int SCHEMA_VERSION = ${contract.schemaVersion};\n    public static final String API_PREFIX = ${javaString(contract.apiPrefix)};\n\n    private PlatformContractConstants() {\n    }\n\n    public static final class Response {\n        public static final String SUCCESS_CODE = ${javaString(contract.responseEnvelope.successCode)};\n        public static final String[] FIELDS = ${javaArray(contract.responseEnvelope.fields)};\n\n        private Response() {\n        }\n    }\n\n    public static final class Pagination {\n        public static final String[] FIELDS = ${javaArray(contract.pagination.fields)};\n        public static final int FIRST_PAGE = ${contract.pagination.firstPage};\n\n        private Pagination() {\n        }\n    }\n\n    public static final class Headers {\n${headers}\n\n        private Headers() {\n        }\n    }\n\n    public static final class Time {\n        public static final String DEFAULT_TIME_ZONE = ${javaString(contract.time.defaultTimeZone)};\n        public static final String CLIENT_TIME_ZONE_HEADER = Headers.TIME_ZONE;\n        public static final String LOCALE_HEADER = Headers.ACCEPT_LANGUAGE;\n\n        private Time() {\n        }\n    }\n\n    public static final class ErrorCodes {\n        public static final String[] RANGES = ${javaArray(errorRanges)};\n        public static final String[] RETRYABLE_RANGES = ${javaArray(contract.retryableErrorCodeRanges)};\n\n        private ErrorCodes() {\n        }\n    }\n}\n`
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

const content = generateContent(contract)

if (checkMode) {
  if (!fs.existsSync(outputPath)) {
    fail('missing backend/src/main/java/com/anjing/model/constants/PlatformContractConstants.java')
  }

  const current = fs.readFileSync(outputPath, 'utf8')
  if (current !== content) {
    fail('backend/src/main/java/com/anjing/model/constants/PlatformContractConstants.java is out of date')
  }

  console.log('generate-platform-contract-backend: ok')
  process.exit(0)
}

fs.mkdirSync(path.dirname(outputPath), { recursive: true })
fs.writeFileSync(outputPath, content)
console.log('generate-platform-contract-backend: wrote backend/src/main/java/com/anjing/model/constants/PlatformContractConstants.java')
