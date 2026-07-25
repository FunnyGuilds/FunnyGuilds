import { writeFile, mkdir, readdir, unlink, access } from 'node:fs/promises';
import path from 'node:path';

const REPO = 'FunnyGuilds/FunnyGuilds';
const DOWNLOADS_DIR = path.join(process.cwd(), 'public', 'downloads');
const METADATA_PATH = path.join(process.cwd(), 'src', 'lib', 'latest-release.json');
const SNAPSHOT_METADATA_PATH = path.join(process.cwd(), 'src', 'lib', 'latest-snapshot.json');
const HISTORY_PATH = path.join(process.cwd(), 'src', 'lib', 'release-history.json');

const JENKINS_JOB_URL = 'https://ci.insertt.dev/job/FunnyGuilds/job/5.x/';

// Every non-draft, non-prerelease tag except the current latest (shown separately as the
// "stable" card) — grouped by major version for display. Old releases have no jar assets
// (GitHub never archived them), so this can only ever be a browsable index, not downloads.
async function fetchAllReleases() {
  const releases = [];
  for (let page = 1; ; page++) {
    const res = await fetch(`https://api.github.com/repos/${REPO}/releases?per_page=100&page=${page}`);
    if (!res.ok) throw new Error(`GitHub API request failed: ${res.status} ${res.statusText}`);
    const batch = await res.json();
    releases.push(...batch);
    if (batch.length < 100) break;
  }
  return releases;
}

async function fetchReleaseHistory() {
  const releases = await fetchAllReleases();

  const history = releases
    .filter((r) => !r.draft && !r.prerelease)
    .slice(1) // drop the latest — already shown as its own card
    .map((r) => ({
      tag: r.tag_name.trim(),
      publishedAt: r.published_at,
      htmlUrl: r.html_url,
      major: r.tag_name.trim().match(/^(\d+)\./)?.[1] ?? '?',
    }));

  await writeFile(HISTORY_PATH, JSON.stringify(history, null, 2) + '\n');
  console.log(`Resolved release history (${history.length} older releases)`);
}

// Not mirrored (unlike the stable jar) — nightly builds are too frequent/large to sensibly
// re-host, so we just resolve Jenkins' own direct artifact URL for the latest successful build.
async function fetchSnapshotInfo() {
  const buildRes = await fetch(`${JENKINS_JOB_URL}lastSuccessfulBuild/api/json`);
  if (!buildRes.ok) throw new Error(`Jenkins API request failed: ${buildRes.status} ${buildRes.statusText}`);
  const build = await buildRes.json();

  const artifact = build.artifacts.find((a) => a.relativePath.endsWith('.jar'));
  if (!artifact) throw new Error(`No jar artifact found on Jenkins build #${build.number}`);

  // Jenkins' artifact URL is {build.url}artifact/{relativePath}; only individual path
  // segments get encoded (relativePath's own "/" separators must survive).
  const encodedPath = artifact.relativePath.split('/').map(encodeURIComponent).join('/');

  const mcMatch = artifact.fileName.match(/\(MC ([^)]+)\)/);

  await writeFile(
    SNAPSHOT_METADATA_PATH,
    JSON.stringify(
      {
        buildNumber: build.number,
        timestamp: build.timestamp,
        jarName: artifact.fileName,
        artifactUrl: `${build.url}artifact/${encodedPath}`,
        jobUrl: JENKINS_JOB_URL,
        mcRange: mcMatch ? mcMatch[1] : null,
      },
      null,
      2,
    ) + '\n',
  );

  console.log(`Resolved 5.x snapshot build #${build.number} (${artifact.fileName})`);
}

// Fixed historical build (last release supporting MC 1.7.x — see FunnyGuilds/FunnySite's
// download/cleaned archive). Immutable, so fetched once and never re-downloaded or pruned.
const LEGACY_URL = 'https://funnyguilds.dzikoysk.net/download/cleaned/3.9.9.15/funnyguilds-3.9.9.15.jar';
const LEGACY_DIR = path.join(DOWNLOADS_DIR, 'legacy');
const LEGACY_FILE = path.join(LEGACY_DIR, 'funnyguilds-3.9.9.15.jar');

async function fetchLegacyJar() {
  const alreadyExists = await access(LEGACY_FILE).then(() => true).catch(() => false);
  if (alreadyExists) return;

  const res = await fetch(LEGACY_URL);
  if (!res.ok) throw new Error(`Failed to download ${LEGACY_URL}: ${res.status}`);
  const buffer = Buffer.from(await res.arrayBuffer());

  await mkdir(LEGACY_DIR, { recursive: true });
  await writeFile(LEGACY_FILE, buffer);
  console.log(`Mirrored legacy 3.9.9.15 (${(buffer.length / (1024 * 1024)).toFixed(1)} MB)`);
}

async function main() {
  await fetchLegacyJar();
  await fetchSnapshotInfo();
  await fetchReleaseHistory();

  const releaseRes = await fetch(`https://api.github.com/repos/${REPO}/releases/latest`);
  if (!releaseRes.ok) throw new Error(`GitHub API request failed: ${releaseRes.status} ${releaseRes.statusText}`);
  const release = await releaseRes.json();

  const asset = release.assets.find((a) => a.name.endsWith('.jar'));
  if (!asset) throw new Error(`No jar asset found on release ${release.tag_name}`);

  const jarRes = await fetch(asset.browser_download_url);
  if (!jarRes.ok) throw new Error(`Failed to download ${asset.browser_download_url}: ${jarRes.status}`);
  const jarBuffer = Buffer.from(await jarRes.arrayBuffer());

  await mkdir(DOWNLOADS_DIR, { recursive: true });
  // Clear jars from older versions so public/downloads/ never accumulates stale releases.
  const existing = await readdir(DOWNLOADS_DIR).catch(() => []);
  await Promise.all(
    existing.filter((name) => name.endsWith('.jar') && name !== asset.name).map((name) => unlink(path.join(DOWNLOADS_DIR, name))),
  );
  await writeFile(path.join(DOWNLOADS_DIR, asset.name), jarBuffer);

  await writeFile(
    METADATA_PATH,
    JSON.stringify(
      {
        tagName: release.tag_name,
        publishedAt: release.published_at,
        jarName: asset.name,
        jarSize: asset.size,
        downloadPath: `/downloads/${asset.name}`,
        htmlUrl: release.html_url,
      },
      null,
      2,
    ) + '\n',
  );

  console.log(`Mirrored ${asset.name} (${(asset.size / (1024 * 1024)).toFixed(1)} MB) from release ${release.tag_name}`);
}

main().catch((err) => {
  console.error(err);
  process.exit(1);
});
