import { LATEST_RELEASE, RELEASE_HISTORY, type ArchivedRelease } from './release-archive';
import { getReleaseInfo } from './format-release';
import { renderChangelog } from './format-changelog';

const RELEASE_URL = 'https://github.com/FunnyGuilds/FunnyGuilds/releases/tag/';

export interface McArchiveRelease {
  tag: string;
  dateLabel: string;
  htmlUrl: string;
  current: boolean;
  changelogHtml: string;
}

export interface McArchiveGroup {
  mcRange: string;
  releases: McArchiveRelease[];
}

// Pre-4.x changelogs rarely state a server version, so these come from the build files instead:
// every .classpath/pom.xml from the first 2014 commit through 3.9.9.14 pins the same
// craftbukkit 1.7.2-R0.1-SNAPSHOT. 3.9.9.15 is listed under the broader "1.7.x" it advertised.
// 4.x onwards is read from each release's own requirements line — see parseMcRange.
const PRE_4X_RANGE = (tag: string) => (tag === '3.9.9.15' ? '1.7.x' : '1.7.2');

function rangeOf(release: ArchivedRelease): string {
  return (release.tag.startsWith('4.') ? release.mcRange : null) ?? PRE_4X_RANGE(release.tag);
}

export function changelogId(tag: string): string {
  return `cl-${tag.replace(/[^a-zA-Z0-9]/g, '-')}`;
}

export function getMcVersionArchive(lang: 'pl' | 'en'): McArchiveGroup[] {
  const locale = lang === 'pl' ? 'pl-PL' : 'en-US';
  const formatter = new Intl.DateTimeFormat(locale, { year: 'numeric', month: 'short' });
  const release = getReleaseInfo(lang);

  const groups: McArchiveGroup[] = [
    {
      mcRange: release.mcRange ?? rangeOf(LATEST_RELEASE),
      releases: [
        {
          tag: release.version,
          dateLabel: release.dateLabel,
          htmlUrl: RELEASE_URL + release.version,
          current: true,
          changelogHtml: release.changelogHtml,
        },
      ],
    },
  ];

  // RELEASE_HISTORY is newest-first, so releases sharing a range are already adjacent and each
  // run collapses into one row.
  for (const entry of RELEASE_HISTORY) {
    const mcRange = rangeOf(entry);
    const row: McArchiveRelease = {
      tag: entry.tag,
      dateLabel: formatter.format(new Date(entry.publishedAt)),
      htmlUrl: entry.htmlUrl,
      current: false,
      changelogHtml: renderChangelog(entry.body),
    };

    const last = groups[groups.length - 1];
    if (last.mcRange === mcRange && !last.releases[0].current) last.releases.push(row);
    else groups.push({ mcRange, releases: [row] });
  }

  return groups;
}
