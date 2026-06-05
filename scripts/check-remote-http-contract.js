#!/usr/bin/env node
const fs = require('fs')
const path = require('path')

const root = path.resolve(__dirname, '..')
const serviceBoundariesPath = path.join(root, 'contracts/service-boundaries.json')
const platformContractPath = path.join(root, 'contracts/platform-contract.json')

const files = {
  platform: 'backend/src/main/java/com/anjing/model/constants/PlatformContractConstants.java',
  properties: 'backend/src/main/java/com/anjing/config/properties/RemoteHttpClientProperties.java',
  request: 'backend/src/main/java/com/anjing/client/RemoteHttpRequest.java',
  client: 'backend/src/main/java/com/anjing/client/RemoteHttpClient.java',
  remoteWrapper: 'backend/src/main/java/com/anjing/util/RemoteCallWrapper.java',
  application: 'backend/src/main/resources/application.yml',
  example: 'backend/src/main/java/com/anjing/example/RemoteCallExampleService.java',
  test: 'backend/src/test/java/com/anjing/client/RemoteHttpClientTest.java',
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
requireToken(files.properties, 'ServiceBoundaryConstants.APPLICATION_ID')
requireToken(files.request, 'private String serviceId')
requireToken(files.request, 'private String path')
requireToken(files.client, 'getFromService')
requireToken(files.client, 'postToService')
requireToken(files.client, 'ParameterizedTypeReference')
requireToken(files.client, 'exchange(RemoteHttpRequest request, ParameterizedTypeReference<R> responseType)')
requireToken(files.client, 'responseSpec.body(responseType)')
requireToken(files.client, 'resolveUrl')
requireToken(files.client, 'joinUrl')
requireToken(files.client, 'properties.getServiceBaseUrls()')
requireToken(files.application, 'service-base-urls:')
requireToken(files.platform, 'BACKEND_PROPAGATED_HEADER_KEYS')
requireToken(files.remoteWrapper, 'PlatformContractConstants.BACKEND_PROPAGATED_HEADER_KEYS')
requireToken(files.remoteWrapper, 'appendContextHeader')
requireToken(files.example, 'ServiceBoundaryConstants.APPLICATION_ID')
requireToken(files.example, '.serviceId(ServiceBoundaryConstants.APPLICATION_ID)')
requireToken(files.example, '.path(ApiConstants.Test.PING_FULL)')
requireToken(files.example, 'new ParameterizedTypeReference<APIResponse<String>>()')
requireToken(files.test, 'APIResponse<PageResult<ItemView>>')
requireToken(files.test, 'MockRestServiceServer')
requireToken(files.test, 'ParameterizedTypeReference<APIResponse<PageResult<ItemView>>>')
requireToken(files.guide, 'ServiceBoundaryConstants.Auth.OWNER')
requireToken(files.guide, '.path(ApiConstants.Auth.ME_FULL)')
requireToken(files.guide, 'ServiceBoundaryConstants.APPLICATION_ID')
requireToken(files.guide, 'new ParameterizedTypeReference<APIResponse<CurrentUserResponse>>()')
requireToken(files.guide, 'service-base-urls:')

let serviceBoundaries
try {
  serviceBoundaries = JSON.parse(fs.readFileSync(serviceBoundariesPath, 'utf8'))
} catch (error) {
  fail(`invalid contracts/service-boundaries.json: ${error.message}`)
}

let platformContract
try {
  platformContract = JSON.parse(fs.readFileSync(platformContractPath, 'utf8'))
} catch (error) {
  fail(`invalid contracts/platform-contract.json: ${error.message}`)
}

const applicationId = serviceBoundaries.applicationId
if (!applicationId) {
  fail('contracts/service-boundaries.json must define applicationId')
}

requireToken(files.application, `${applicationId}:`)
requireToken(files.example, 'ServiceBoundaryConstants.APPLICATION_ID')

const sampleSource = read(files.example)
if (/\.url\("http:\/\/localhost/.test(sampleSource) || sampleSource.includes('serverPort + ApiConstants')) {
  fail('RemoteCallExampleService should use serviceId + path instead of composing localhost URLs')
}

const guideSource = read(files.guide)
if (guideSource.includes('.url("http://infra-auth')) {
  fail('REMOTE_CALL_GUIDE should demonstrate serviceId + path for internal service calls')
}

const requestHeaders = platformContract.requestHeaders || {}
const backendPropagatedHeaders = platformContract.backendPropagatedHeaders || []
if (!backendPropagatedHeaders.length) {
  fail('contracts/platform-contract.json must define backendPropagatedHeaders')
}
for (const key of backendPropagatedHeaders) {
  if (!requestHeaders[key]) {
    fail(`backendPropagatedHeaders contains unknown request header key: ${key}`)
  }
  requireToken(files.remoteWrapper, `case "${key}"`)
  requireToken(files.guide, requestHeaders[key])
}

console.log('check-remote-http-contract: ok')
