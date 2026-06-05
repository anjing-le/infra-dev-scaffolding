import fs from 'node:fs/promises'
import path from 'node:path'

type CleanupTarget = {
  path: string
  reason: string
}

const targets: CleanupTarget[] = [
  {
    path: 'src/views/auth/login111',
    reason: 'unused alternate login page kept from an earlier UI experiment'
  },
  {
    path: 'src/config/fastEnter.ts',
    reason: 'legacy fast-enter config; the active config is src/config/modules/fastEnter.ts'
  },
  {
    path: 'src/views/change',
    reason: 'upstream demo page, not part of the Anjing scaffold'
  },
  {
    path: 'src/views/safeguard',
    reason: 'upstream demo page, not part of the Anjing scaffold'
  },
  {
    path: 'src/views/article',
    reason: 'upstream demo page, not part of the Anjing scaffold'
  },
  {
    path: 'src/views/examples',
    reason: 'upstream demo page, not part of the Anjing scaffold'
  },
  {
    path: 'src/views/widgets',
    reason: 'upstream demo page, not part of the Anjing scaffold'
  },
  {
    path: 'src/views/template',
    reason: 'upstream demo page, not part of the Anjing scaffold'
  },
  {
    path: 'src/mock/json',
    reason: 'upstream mock data, not used by the current scaffold'
  }
]

const apply = process.argv.includes('--apply')
const root = process.cwd()

async function exists(relativePath: string): Promise<boolean> {
  try {
    await fs.stat(path.resolve(root, relativePath))
    return true
  } catch {
    return false
  }
}

async function assertFrontendRoot() {
  const packageJsonPath = path.resolve(root, 'package.json')
  const packageJson = JSON.parse(await fs.readFile(packageJsonPath, 'utf-8')) as {
    scripts?: Record<string, string>
  }

  if (packageJson.scripts?.['clean:dev'] !== 'tsx scripts/clean-dev.ts') {
    throw new Error('please run this script from the frontend directory')
  }
}

async function main() {
  await assertFrontendRoot()

  const existingTargets = []

  for (const target of targets) {
    if (await exists(target.path)) {
      existingTargets.push(target)
    }
  }

  console.log('clean-dev: Anjing scaffold cleanup')

  if (existingTargets.length === 0) {
    console.log('clean-dev: no stale optional frontend targets found')
    return
  }

  console.log('clean-dev: found stale optional targets:')
  for (const target of existingTargets) {
    console.log(`- ${target.path}: ${target.reason}`)
  }

  if (!apply) {
    console.log('clean-dev: dry run only; rerun with --apply to remove these paths')
    return
  }

  for (const target of existingTargets) {
    await fs.rm(path.resolve(root, target.path), { recursive: true, force: true })
    console.log(`clean-dev: removed ${target.path}`)
  }

  console.log('clean-dev: done')
}

main().catch((error) => {
  console.error(`clean-dev: ${error instanceof Error ? error.message : String(error)}`)
  process.exit(1)
})
