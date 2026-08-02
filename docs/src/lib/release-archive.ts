// public/archive/<tag>/{CHANGELOG.md,*.jar} is the only source of truth for release data — no
// generated JSON, no sync script to remember to run. Each CHANGELOG.md opens with a header
// written when the version was archived, followed by `---` and the GitHub release body:
//   - **Tag:** 4.14.0
//   - **Published:** 2025-12-15T21:35:00Z
//   - **GitHub release:** https://github.com/FunnyGuilds/FunnyGuilds/releases/tag/4.14.0
// Read at build time, so "latest" and the history always match what's actually archived.
import { readdirSync, readFileSync, statSync } from 'node:fs';
import path from 'node:path';

const ARCHIVE_DIR = path.join(process.cwd(), 'public', 'archive');

export interface ArchivedRelease {
  tag: string;
  publishedAt: string;
  htmlUrl: string;
  body: string;
}

function parseChangelog(raw: string, tag: string): ArchivedRelease {
  const publishedAt = raw.match(/\*\*Published:\*\*\s*(\S+)/)?.[1] ?? '';
  const htmlUrl =
    raw.match(/\*\*GitHub release:\*\*\s*(\S+)/)?.[1] ?? `https://github.com/FunnyGuilds/FunnyGuilds/releases/tag/${tag}`;
  // First "---" only — bodies contain their own horizontal rules further down.
  const sep = raw.indexOf('\n---\n');
  const body = (sep === -1 ? raw : raw.slice(sep + 5)).trim();
  return { tag, publishedAt, htmlUrl, body };
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

// The body's "* Spigot ..." line carries exact tested versions, unlike the jar filename's rounded
// major.minor. Changelogs write it as a slash-separated list, a dash range or a single open-ended
// version, so scan the line for version-shaped tokens instead of assuming a separator.
function parseMcRange(body: string): string | null {
  const versions = body.match(/Spigot\s+([^\n]+)/)?.[1]?.match(/\d+(?:\.\d+)+/g);
  if (!versions?.length) return null;
  const [low, high] = [versions[0], versions[versions.length - 1]];
  return low === high ? low : `${low} – ${high}`;
}

const ALL_RELEASES = loadAllReleases();

export interface LatestRelease extends ArchivedRelease {
  jarSize: number;
  downloadPath: string;
  mcRange: string | null;
}

function loadLatestRelease(): LatestRelease {
  const [latest] = ALL_RELEASES;
  const dir = path.join(ARCHIVE_DIR, latest.tag);
  const jarName = readdirSync(dir).find((name) => name.endsWith('.jar'));
  if (!jarName) throw new Error(`No jar found for latest release ${latest.tag} in ${dir}`);

  return {
    ...latest,
    jarSize: statSync(path.join(dir, jarName)).size,
    downloadPath: `/archive/${latest.tag}/${jarName}`,
    mcRange: parseMcRange(latest.body),
  };
}

export const LATEST_RELEASE: LatestRelease = loadLatestRelease();

// Every archived release except the latest (shown separately).
export const RELEASE_HISTORY: ArchivedRelease[] = ALL_RELEASES.slice(1);
