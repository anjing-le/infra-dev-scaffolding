#!/usr/bin/env bash
set -euo pipefail

ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
cd "$ROOT"

fail() {
  echo "check-template: $*" >&2
  exit 1
}

require_file() {
  local file="$1"
  [[ -f "$file" ]] || fail "missing required file: $file"
}

require_file README.md
require_file LICENSE
require_file CONTRIBUTING.md
require_file project_document/ROADMAP.md
require_file project_document/STATUS.md
require_file project_document/PROJECT_CONSTRAINTS.md
require_file project_document/NEW_MODULE_GUIDE.md
require_file project_document/UI_DESIGN_GUIDE.md
require_file project_document/DEMO_EVIDENCE.md
require_file project_document/ci/quality-gate.yml
require_file project_document/RELEASE_CHECKLIST.md
require_file project_document/COPY_GUIDE.md
require_file project_document/TEMPLATE_BOUNDARIES.md
require_file project_document/AI_ASSETS.md
require_file project_document/ENVIRONMENT_PROFILE_GUIDE.md
require_file project_document/LOCAL_STARTUP_GUIDE.md
require_file project_document/PLATFORM_CONTRACT_GUIDE.md
require_file project_document/OPENAPI_CONTRACT_GUIDE.md
require_file project_document/SERVICE_BOUNDARY_GUIDE.md
require_file project_document/SHARED_KERNEL_GUIDE.md
require_file contracts/platform-contract.json
require_file contracts/service-boundaries.json
require_file scripts/check-api-constants.js
require_file scripts/check-api-path-parity.js
require_file scripts/check-backend-controller-contracts.js
require_file scripts/check-scaffold-governance.js
require_file scripts/check-frontend-api-boundaries.js
require_file scripts/check-frontend-openapi-boundaries.js
require_file scripts/check-frontend-context-contract.js
require_file scripts/check-backend-context-contract.js
require_file scripts/check-async-context-contract.js
require_file scripts/check-frontend-time-contract.js
require_file scripts/generate-platform-contract-backend.js
require_file scripts/generate-platform-contract-frontend.js
require_file scripts/generate-service-boundaries-backend.js
require_file scripts/generate-service-boundaries-frontend.js
require_file scripts/check-platform-contract.js
require_file scripts/check-error-codes.js
require_file scripts/check-openapi-contract.js
require_file scripts/check-openapi-runtime-contract.js
require_file scripts/generate-openapi-frontend-types.js
require_file scripts/check-service-boundaries.js
require_file scripts/check-shared-kernel.js
require_file scripts/check-remote-http-contract.js
require_file scripts/check-contracts.sh
require_file scripts/quality-gate.sh
require_file scripts/probe-backend-dev.sh
require_file backend/src/main/java/com/anjing/client/ServiceEndpointResolver.java
require_file backend/src/main/java/com/anjing/client/ServiceEndpoint.java
require_file backend/src/main/java/com/anjing/client/ServiceEndpointRegistry.java
require_file backend/src/main/java/com/anjing/client/ConfiguredServiceEndpointRegistry.java
require_file backend/src/main/java/com/anjing/client/ConfiguredServiceEndpointResolver.java
require_file backend/src/main/java/com/anjing/client/RemoteCallerResolver.java
require_file backend/src/main/java/com/anjing/client/DefaultRemoteCallerResolver.java
require_file backend/src/main/java/com/anjing/client/RemoteCallPolicy.java
require_file backend/src/main/java/com/anjing/client/RemoteCallPolicyContext.java
require_file backend/src/main/java/com/anjing/client/ConfiguredRemoteCallPolicy.java
require_file backend/src/main/java/com/anjing/client/NoopRemoteCallPolicy.java
require_file backend/src/main/java/com/anjing/client/RemoteCallObserver.java
require_file backend/src/main/java/com/anjing/client/RemoteCallObservation.java
require_file backend/src/main/java/com/anjing/client/NoopRemoteCallObserver.java
require_file backend/.env.example
require_file backend/src/main/resources/application.yml
require_file backend/src/main/resources/application-dev.yml
require_file backend/src/main/resources/application-test.yml
require_file backend/src/main/resources/application-prod.yml
require_file frontend/package.json
require_file frontend/LICENSE
require_file frontend/.env
require_file frontend/.env.development
require_file frontend/.env.production
require_file frontend/src/api/openapiClient.ts
require_file frontend/src/contracts/openapi/schemas.ts
require_file frontend/src/contracts/openapi/operations.ts
require_file frontend/src/contracts/service-boundaries.ts

status_doc='project_document/STATUS.md'
for token in \
  '2026-06-07' \
  'S0 构建与入口收口' \
  'S1 工程母版收口' \
  'S2 AI 协作资产收口' \
  'S3 后续项目复用验证' \
  'SCAFFOLD_ADOPTION_PROMPT.md' \
  './scripts/quality-gate.sh' \
  './scripts/check-template.sh' \
  './scripts/smoke-copy.sh' \
  'mvn -q -DskipTests package' \
  'pnpm build' \
  'pnpm -s clean:dev' \
  'records' \
  'request.del' \
  'infra-skill-hub'
do
  rg -q --fixed-strings "$token" "$status_doc" \
    || fail "status document is missing required token: $token"
done

constraints_doc='project_document/PROJECT_CONSTRAINTS.md'
for token in \
  '清晰优先，简单优先，可验证优先' \
  '非示例 Controller 返回值必须使用 `APIResponse<T>`' \
  '页面和组件不要直接依赖 `@/contracts/openapi/**`' \
  'node scripts/check-backend-controller-contracts.js' \
  'node scripts/check-scaffold-governance.js' \
  'node scripts/check-frontend-openapi-boundaries.js' \
  './scripts/quality-gate.sh'
do
  rg -q --fixed-strings "$token" "$constraints_doc" \
    || fail "project constraints document is missing required token: $token"
done

for token in \
  'MIT License' \
  'Anjing Contributors'
do
  rg -q --fixed-strings "$token" LICENSE \
    || fail "root LICENSE is missing required token: $token"
done

for token in \
  'project_document/PROJECT_CONSTRAINTS.md' \
  'project_document/NEW_MODULE_GUIDE.md' \
  'project_document/UI_DESIGN_GUIDE.md' \
  'project_document/DEMO_EVIDENCE.md' \
  './scripts/check-template.sh' \
  './scripts/smoke-copy.sh' \
  'project_document/STATUS.md' \
  'frontend/LICENSE' \
  'Art Design Pro'
do
  rg -q --fixed-strings "$token" CONTRIBUTING.md \
    || fail "CONTRIBUTING.md is missing required token: $token"
done

node scripts/check-scaffold-governance.js

project_info="$(
  node -e '
    const fs = require("fs");
    const pkg = JSON.parse(fs.readFileSync("frontend/package.json", "utf8"));
    const pom = fs.readFileSync("backend/pom.xml", "utf8").replace(/<parent>[\s\S]*?<\/parent>/, "");
    const artifact = pom.match(/<artifactId>([^<]+)<\/artifactId>/)?.[1] || "";
    const app = fs.readFileSync("backend/src/main/resources/application.yml", "utf8").match(/^\s{4}name:\s*([^\s#]+)/m)?.[1] || "";
    console.log([pkg.name || "", artifact, app].join("\n"));
  '
)"

frontend_name="$(printf '%s\n' "$project_info" | sed -n '1p')"
backend_artifact="$(printf '%s\n' "$project_info" | sed -n '2p')"
spring_name="$(printf '%s\n' "$project_info" | sed -n '3p')"

[[ -n "$frontend_name" ]] || fail "frontend package name is empty"
[[ -n "$backend_artifact" ]] || fail "backend artifactId is empty"
[[ -n "$spring_name" ]] || fail "spring.application.name is empty"
[[ "$frontend_name" == "$backend_artifact" ]] \
  || fail "frontend package name ($frontend_name) and backend artifactId ($backend_artifact) differ"
[[ "$backend_artifact" == "$spring_name" ]] \
  || fail "backend artifactId ($backend_artifact) and spring.application.name ($spring_name) differ"

count_files() {
  find "$1" -maxdepth 1 -type f -name "$2" ! -name "$3" | wc -l | tr -d '[:space:]'
}

[[ "$(count_files frontend/.cursor/rules '*.mdc' 'README.mdc')" == "11" ]] \
  || fail "frontend Cursor rules count must be 11"
[[ "$(count_files backend/.cursor/rules '*.mdc' 'README.mdc')" == "4" ]] \
  || fail "backend Cursor rules count must be 4"
[[ "$(count_files frontend/.cursor/prompts '*.md' 'README.md')" == "4" ]] \
  || fail "frontend Cursor prompts count must be 4"
[[ "$(count_files backend/.cursor/prompts '*.md' 'README.md')" == "2" ]] \
  || fail "backend Cursor prompts count must be 2"

stale_pattern='agent-dev-scaffolding|apifoxmock|6400575|6097373|Daymychen/art-design-pro'
if rg -n "$stale_pattern" README.md CONTRIBUTING.md project_document backend frontend \
  --glob '!frontend/node_modules/**' \
  --glob '!frontend/dist/**' \
  --glob '!backend/target/**'
then
  fail "stale template identity or mock endpoint found"
fi

cursor_stale_pattern='Agent Dev Scaffolding|Art Design Pro|UnoCSS|agent-dev-scaffolding|apifoxmock|6400575|6097373|Daymychen/art-design-pro'
if rg -n "$cursor_stale_pattern" frontend/.cursor backend/.cursor project_document/AI_ASSETS.md --hidden
then
  fail "stale Cursor rule/prompt identity or deprecated frontend stack found"
fi

prompt_contract_pattern='\[ModuleName\]Service|\[moduleName\]Api|src/api/models|request\.delete|request\.get/post/put/delete'
if rg -n "$prompt_contract_pattern" frontend/.cursor/prompts frontend/.cursor/rules backend/.cursor/prompts backend/.cursor/rules --hidden
then
  fail "Cursor rules or prompts contain deprecated API generation contract"
fi

for stale_path in \
  frontend/src/views/auth/login111 \
  frontend/src/config/fastEnter.ts
do
  [[ ! -e "$stale_path" ]] || fail "stale frontend path exists: $stale_path"
done

sample_present=0
[[ -f backend/src/main/java/com/anjing/controller/TestController.java ]] && sample_present=1
[[ -d backend/src/main/java/com/anjing/example ]] && sample_present=1

if [[ "$sample_present" -eq 1 ]]; then
  require_file backend/src/main/java/com/anjing/annotation/ScaffoldSample.java

  if [[ -f backend/src/main/java/com/anjing/controller/TestController.java ]]; then
    rg -q '@ScaffoldSample' backend/src/main/java/com/anjing/controller/TestController.java \
      || fail "TestController must be marked with @ScaffoldSample"
  fi

  if [[ -d backend/src/main/java/com/anjing/example ]]; then
    rg -q '@ScaffoldSample' backend/src/main/java/com/anjing/example/package-info.java \
      || fail "backend example package must be marked with @ScaffoldSample"
    rg -q '@ScaffoldSample' backend/src/main/java/com/anjing/example/statemachine/package-info.java \
      || fail "backend statemachine example package must be marked with @ScaffoldSample"
  fi
fi

if git rev-parse --is-inside-work-tree >/dev/null 2>&1; then
  tracked_build_outputs="$(git ls-files frontend/dist backend/target backend/logs)"
  [[ -z "$tracked_build_outputs" ]] || fail "build outputs are tracked by git"
fi

./scripts/check-contracts.sh

echo "check-template: ok"
