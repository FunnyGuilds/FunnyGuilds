// docs/archive/<tag>/CHANGELOG.md is the only source of truth for release data — no generated
// JSON, no sync script to remember to run. Each one opens with a header written when the version
// was archived, followed by `---` and the GitHub release body:
//   - **Tag:** 4.14.0
//   - **Published:** 2025-12-15T21:35:00Z
//   - **GitHub release:** https://github.com/FunnyGuilds/FunnyGuilds/releases/tag/4.14.0
//   - **Jar:** FunnyGuilds.4.14.0.1762.MC.1.8-1.21.jar
//   - **Size:** 10930158
// Read at build time, so "latest" and the history always match what's actually archived.
//
// The jars themselves are not kept here: they live on their GitHub releases, and Jar/Size record
// what the download button needs to name and describe the file without a copy of it.
import { readdirSync, readFileSync } from 'node:fs';
import path from 'node:path';

const ARCHIVE_DIR = path.join(process.cwd(), 'archive');

// Release assets are served from GitHub's CDN with no bandwidth quota, unlike raw links to
// LFS-tracked files, which bill against the organisation's LFS allowance on every download.
const JAR_BASE_URL = 'https://github.com/FunnyGuilds/FunnyGuilds/releases/download';

export interface ArchivedRelease {
  tag: string;
  publishedAt: string;
  htmlUrl: string;
  /** Supported Minecraft versions, from the changelog's own requirements line; null if it has none. */
  mcRange: string | null;
  /** Published asset filename and its size in bytes; absent for versions with no archived jar. */
  jarName: string | null;
  jarSize: number | null;
  body: string;
}

function parseChangelog(raw: string, tag: string): ArchivedRelease {
  const publishedAt = raw.match(/\*\*Published:\*\*\s*(\S+)/)?.[1] ?? '';
  const htmlUrl =
    raw.match(/\*\*GitHub release:\*\*\s*(\S+)/)?.[1] ?? `https://github.com/FunnyGuilds/FunnyGuilds/releases/tag/${tag}`;
  // First "---" only — bodies contain their own horizontal rules further down.
  const sep = raw.indexOf('\n---\n');
  const body = (sep === -1 ? raw : raw.slice(sep + 5)).trim();
  const jarName = raw.match(/\*\*Jar:\*\*\s*(\S+)/)?.[1] ?? null;
  const jarSize = Number(raw.match(/\*\*Size:\*\*\s*(\d+)/)?.[1]) || null;
  return { tag, publishedAt, htmlUrl, mcRange: parseMcRange(body), jarName, jarSize, body };
}

function loadAllReleases(): ArchivedRelease[] {
  const releases = readdirSync(ARCHIVE_DIR).flatMap((tag) => {
    try {
      const raw = readFileSync(path.join(ARCHIVE_DIR, tag, 'CHANGELOG.md'), 'utf8');
      return [parseChangelog(raw, tag)];
    } catch {
      return [];
    }
  });
  releases.sort((a, b) => new Date(b.publishedAt).getTime() - new Date(a.publishedAt).getTime());
  return releases;
}

// Every 4.x changelog ends with a requirements bullet naming the tested server versions
// ("* Spigot 1.8.8 - 1.16.5", "* Spigot 1.8.8/1.9.4/...", "* Craftbukkit 1.8+"), which is more
// precise than the jar filename's rounded major.minor. Anchored to the bullet so a passing
// mention of Spigot in the prose above can't be picked up instead. Pre-4.x changelogs mostly
// have no such line — see PRE_4X_RANGE in mc-version-archive.ts.
function parseMcRange(body: string): string | null {
  const line = body.match(/^\s*[*-]\s*(?:Spigot|CraftBukkit|Bukkit)\s*(.+)$/im)?.[1];
  const versions = line?.match(/\d+(?:\.\d+)+/g);
  if (!versions?.length) return null;
  const [low, high] = [versions[0], versions[versions.length - 1]];
  return low === high ? low : `${low} – ${high}`;
}

const ALL_RELEASES = loadAllReleases();

export interface LatestRelease extends ArchivedRelease {
  jarName: string;
  jarSize: number;
  downloadPath: string;
}

function loadLatestRelease(): LatestRelease {
  const [latest] = ALL_RELEASES;
  if (!latest.jarName || !latest.jarSize) {
    throw new Error(`${latest.tag} is the newest archived release but its changelog has no Jar/Size header`);
  }

  return {
    ...latest,
    jarName: latest.jarName,
    jarSize: latest.jarSize,
    downloadPath: `${JAR_BASE_URL}/${latest.tag}/${latest.jarName}`,
  };
}

export const LATEST_RELEASE: LatestRelease = loadLatestRelease();

// Every archived release except the latest (shown separately).
export const RELEASE_HISTORY: ArchivedRelease[] = ALL_RELEASES.slice(1);
