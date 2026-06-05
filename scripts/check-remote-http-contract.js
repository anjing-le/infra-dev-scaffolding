#!/usr/bin/env node
const fs = require('fs')
const path = require('path')

const root = path.resolve(__dirname, '..')
const serviceBoundariesPath = path.join(root, 'contracts/service-boundaries.json')

const files = {
  properties: 'backend/src/main/java/com/anjing/config/properties/RemoteHttpClientProperties.java',
  request: 'backend/src/main/java/com/anjing/client/RemoteHttpRequest.java',
  client: 'backend/src/main/java/com/anjing/client/RemoteHttpClient.java',
  application: 'backend/src/main/resources/application.yml',
  example: 'backend/src/main/java/com/anjing/example/RemoteCallExampleService.java',
  guide: 'project_document/REMOTE_CALL_GUIDE.md'
}

function fail(message) {
  console.error(`check-remote-http-contract: ${message}`)
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

requireToken(files.properties, 'private Map<String, String> serviceBaseUrls')
requireToken(files.request, 'private String serviceId')
requireToken(files.request, 'private String path')
requireToken(files.client, 'getFromService')
requireToken(files.client, 'postToService')
requireToken(files.client, 'resolveUrl')
requireToken(files.client, 'joinUrl')
requireToken(files.client, 'properties.getServiceBaseUrls()')
requireToken(files.application, 'service-base-urls:')
requireToken(files.example, '.path(ApiConstants.Test.PING_FULL)')
requireToken(files.guide, '.serviceId("infra-auth")')
requireToken(files.guide, '.path(ApiConstants.Auth.ME_FULL)')
requireToken(files.guide, 'service-base-urls:')

let serviceBoundaries
try {
  serviceBoundaries = JSON.parse(fs.readFileSync(serviceBoundariesPath, 'utf8'))
} catch (error) {
  fail(`invalid contracts/service-boundaries.json: ${error.message}`)
}

const applicationId = serviceBoundaries.applicationId
if (!applicationId) {
  fail('contracts/service-boundaries.json must define applicationId')
}

requireToken(files.application, `${applicationId}:`)
requireToken(files.example, `.serviceId("${applicationId}")`)

const sampleSource = read(files.example)
if (/\.url\("http:\/\/localhost/.test(sampleSource) || sampleSource.includes('serverPort + ApiConstants')) {
  fail('RemoteCallExampleService should use serviceId + path instead of composing localhost URLs')
}

const guideSource = read(files.guide)
if (guideSource.includes('.url("http://infra-auth')) {
  fail('REMOTE_CALL_GUIDE should demonstrate serviceId + path for internal service calls')
}

console.log('check-remote-http-contract: ok')
