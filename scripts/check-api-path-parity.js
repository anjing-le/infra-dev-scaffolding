#!/usr/bin/env node
const fs = require('fs')
const path = require('path')

const root = path.resolve(__dirname, '..')
const apiConstantsPath = path.join(
  root,
  'backend/src/main/java/com/anjing/model/constants/ApiConstants.java'
)
const apiPathsPath = path.join(root, 'frontend/src/api/paths.ts')

function fail(message) {
  console.error(`check-api-path-parity: ${message}`)
  process.exit(1)
}

function read(file) {
  if (!fs.existsSync(file)) {
    fail(`missing required file: ${path.relative(root, file)}`)
  }
  return fs.readFileSync(file, 'utf8')
}

function findBlock(source, marker) {
  const markerIndex = source.indexOf(marker)
  if (markerIndex < 0) {
    fail(`missing block marker: ${marker}`)
  }

  const start = source.indexOf('{', markerIndex)
  if (start < 0) {
    fail(`missing block opening brace: ${marker}`)
  }

  let depth = 0
  for (let index = start; index < source.length; index += 1) {
    const char = source[index]
    if (char === '{') {
      depth += 1
    } else if (char === '}') {
      depth -= 1
      if (depth === 0) {
        return source.slice(start + 1, index)
      }
    }
  }

  fail(`missing block closing brace: ${marker}`)
}

function evaluateJavaExpression(expression, constants) {
  return expression
    .split('+')
    .map((part) => part.trim())
    .map((part) => {
      const literalMatch = part.match(/^"([^"]*)"$/)
      if (literalMatch) {
        return literalMatch[1]
      }

      if (part === 'PlatformContractConstants.API_PREFIX') {
        return '/api'
      }

      if (Object.prototype.hasOwnProperty.call(constants, part)) {
        return constants[part]
      }

      fail(`cannot evaluate Java API path expression: ${expression}`)
    })
    .join('')
}

function extractJavaConstants(source, className) {
  const globalConstants = {}
  for (const match of source.matchAll(/public static final String\s+(\w+)\s*=\s*([^;]+);/g)) {
    if (match.index < source.indexOf('public static class')) {
      globalConstants[match[1]] = evaluateJavaExpression(match[2], globalConstants)
    }
  }

  const block = findBlock(source, `public static class ${className}`)
  const constants = { ...globalConstants }

  for (const match of block.matchAll(/public static final String\s+(\w+)\s*=\s*([^;]+);/g)) {
    constants[match[1]] = evaluateJavaExpression(match[2], constants)
  }

  return constants
}

function extractTsModule(source, moduleName) {
  const block = findBlock(source, `${moduleName}:`)
  const values = {}

  for (const match of block.matchAll(/(\w+):\s*'([^']+)'/g)) {
    values[match[1]] = match[2]
  }

  for (const match of block.matchAll(/(\w+):\s*\([^)]*\)\s*=>\s*`([^`]+)`/g)) {
    values[match[1]] = match[2].replace(/\$\{encodePathValue\((\w+)\)\}/g, '{$1}')
  }

  return values
}

const javaSource = read(apiConstantsPath)
const tsSource = read(apiPathsPath)

const backend = {
  Auth: extractJavaConstants(javaSource, 'Auth'),
  Test: extractJavaConstants(javaSource, 'Test'),
  Common: extractJavaConstants(javaSource, 'Common')
}

const frontend = {
  auth: extractTsModule(tsSource, 'auth'),
  test: extractTsModule(tsSource, 'test'),
  common: extractTsModule(tsSource, 'common')
}

const mappings = [
  ['Auth.LOGIN_FULL', 'auth.login'],
  ['Auth.LOGOUT_FULL', 'auth.logout'],
  ['Auth.ME_FULL', 'auth.me'],
  ['Auth.REFRESH_FULL', 'auth.refresh'],
  ['Test.HEALTH_FULL', 'test.health'],
  ['Test.FEATURES_FULL', 'test.features'],
  ['Test.PING_FULL', 'test.ping'],
  ['Test.EXCEPTION_BIZ_FULL', 'test.bizException'],
  ['Test.EXCEPTION_SYSTEM_FULL', 'test.systemException'],
  ['Test.ITEMS_FULL', 'test.items'],
  ['Test.ITEM_DETAIL_FULL', 'test.itemDetail'],
  ['Common.UPLOAD_FILE_FULL', 'common.upload'],
  ['Common.UPLOAD_IMAGE_FULL', 'common.uploadImage'],
  ['Common.UPLOAD_WANG_EDITOR_FULL', 'common.uploadWangEditor'],
  ['Common.DOWNLOAD_FILE_FULL', 'common.download'],
  ['Common.DELETE_FILE_FULL', 'common.deleteFile']
]

function getBackendValue(key) {
  const [moduleName, constantName] = key.split('.')
  return backend[moduleName]?.[constantName]
}

function getFrontendValue(key) {
  const [moduleName, pathName] = key.split('.')
  return frontend[moduleName]?.[pathName]
}

for (const [backendKey, frontendKey] of mappings) {
  const backendValue = getBackendValue(backendKey)
  const frontendValue = getFrontendValue(frontendKey)

  if (!backendValue) {
    fail(`missing backend path ${backendKey}`)
  }

  if (!frontendValue) {
    fail(`missing frontend path ${frontendKey}`)
  }

  if (backendValue !== frontendValue) {
    fail(`${backendKey} (${backendValue}) does not match ${frontendKey} (${frontendValue})`)
  }
}

console.log('check-api-path-parity: ok')
