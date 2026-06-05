#!/usr/bin/env node
const fs = require('fs')
const path = require('path')

const root = path.resolve(__dirname, '..')
const args = process.argv.slice(2)
const check = args.includes('--check')
const openapiPath = args.find((arg) => !arg.startsWith('--'))
const outputPath = path.join(root, 'frontend/src/contracts/openapi/schemas.ts')

function fail(message) {
  console.error(`generate-openapi-frontend-types: ${message}`)
  process.exit(1)
}

function readJson(file) {
  if (!file || !fs.existsSync(file)) {
    fail('usage: node scripts/generate-openapi-frontend-types.js <openapi-json-file> [--check]')
  }

  try {
    return JSON.parse(fs.readFileSync(file, 'utf8'))
  } catch (error) {
    fail(`invalid OpenAPI JSON: ${error.message}`)
  }
}

function isIdentifier(value) {
  return /^[A-Za-z_$][A-Za-z0-9_$]*$/.test(value)
}

function propertyName(value) {
  return isIdentifier(value) ? value : JSON.stringify(value)
}

function typeName(value) {
  const normalized = String(value)
    .replace(/[^A-Za-z0-9_$]/g, '_')
    .replace(/^[^A-Za-z_$]+/, '')
  return normalized || 'UnnamedSchema'
}

function refName(ref) {
  const name = String(ref || '').split('/').pop()
  return typeName(name)
}

function literal(value) {
  return JSON.stringify(value)
}

function union(values) {
  return values.map(literal).join(' | ')
}

function sortedEntries(value) {
  return Object.entries(value || {}).sort(([left], [right]) => left.localeCompare(right))
}

function schemaType(schema) {
  if (!schema || Object.keys(schema).length === 0) {
    return 'unknown'
  }

  if (schema.$ref) {
    return refName(schema.$ref)
  }

  if (Array.isArray(schema.oneOf) && schema.oneOf.length > 0) {
    return schema.oneOf.map(schemaType).join(' | ')
  }

  if (Array.isArray(schema.anyOf) && schema.anyOf.length > 0) {
    return schema.anyOf.map(schemaType).join(' | ')
  }

  if (Array.isArray(schema.allOf) && schema.allOf.length > 0) {
    return schema.allOf.map(schemaType).join(' & ')
  }

  if (Array.isArray(schema.enum) && schema.enum.length > 0) {
    return union(schema.enum)
  }

  let result
  switch (schema.type) {
    case 'string':
      result = 'string'
      break
    case 'integer':
    case 'number':
      result = 'number'
      break
    case 'boolean':
      result = 'boolean'
      break
    case 'array':
      result = `${schemaType(schema.items)}[]`
      break
    case 'object':
      if (schema.additionalProperties) {
        result = `Record<string, ${schemaType(schema.additionalProperties)}>`
      } else if (schema.properties) {
        result = inlineObjectType(schema)
      } else {
        result = 'Record<string, unknown>'
      }
      break
    default:
      result = 'unknown'
  }

  return schema.nullable ? `${result} | null` : result
}

function inlineObjectType(schema) {
  const required = new Set(schema.required || [])
  const properties = sortedEntries(schema.properties)
    .map(([name, child]) => `${propertyName(name)}${required.has(name) ? '' : '?'}: ${schemaType(child)}`)

  if (schema.additionalProperties) {
    properties.push(`[key: string]: ${schemaType(schema.additionalProperties)}`)
  }

  return properties.length ? `{ ${properties.join('; ')} }` : 'Record<string, unknown>'
}

function jsDoc(description) {
  if (!description) return []
  return [
    '/**',
    ` * ${String(description).replace(/\*\//g, '* /')}`,
    ' */'
  ]
}

function renderSchema(name, schema) {
  const lines = []
  const exportedName = typeName(name)
  lines.push(...jsDoc(schema.description))

  if (schema.type === 'object' && schema.properties) {
    const required = new Set(schema.required || [])
    lines.push(`export interface ${exportedName} {`)
    for (const [property, child] of sortedEntries(schema.properties)) {
      for (const comment of jsDoc(child.description)) {
        lines.push(`  ${comment}`)
      }
      lines.push(`  ${propertyName(property)}${required.has(property) ? '' : '?'}: ${schemaType(child)}`)
    }
    if (schema.additionalProperties) {
      lines.push(`  [key: string]: ${schemaType(schema.additionalProperties)}`)
    }
    lines.push('}')
    return lines.join('\n')
  }

  lines.push(`export type ${exportedName} = ${schemaType(schema)}`)
  return lines.join('\n')
}

function render(openapi) {
  const schemas = openapi.components?.schemas || {}
  const schemaNames = Object.keys(schemas).sort((left, right) => left.localeCompare(right))
  if (schemaNames.length === 0) {
    fail('OpenAPI JSON does not contain components.schemas')
  }

  const sections = [
    '/* eslint-disable */',
    '// Generated from OpenAPI JSON. Do not edit manually.',
    '// Run: node scripts/generate-openapi-frontend-types.js <openapi-json-file>',
    '',
    'export type JsonObject = Record<string, unknown>',
    ''
  ]

  for (const name of schemaNames) {
    sections.push(renderSchema(name, schemas[name]))
    sections.push('')
  }

  sections.push('export interface OpenApiSchemas {')
  for (const name of schemaNames) {
    const exportedName = typeName(name)
    sections.push(`  ${propertyName(name)}: ${exportedName}`)
  }
  sections.push('}')
  sections.push('')

  return sections.join('\n')
}

const openapi = readJson(openapiPath)
const output = render(openapi)

if (check) {
  if (!fs.existsSync(outputPath)) {
    fail(`missing generated file: ${path.relative(root, outputPath)}`)
  }

  const current = fs.readFileSync(outputPath, 'utf8')
  if (current !== output) {
    fail(`generated frontend OpenAPI types are stale: ${path.relative(root, outputPath)}`)
  }

  console.log('generate-openapi-frontend-types: ok')
  process.exit(0)
}

fs.mkdirSync(path.dirname(outputPath), { recursive: true })
fs.writeFileSync(outputPath, output)
console.log(`generate-openapi-frontend-types: wrote ${path.relative(root, outputPath)}`)
