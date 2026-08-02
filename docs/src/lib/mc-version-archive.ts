import { RELEASE_HISTORY as history } from './release-archive';
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

// One row per supported MC range, newest first. Ranges come from each release's own build files
// (.classpath/pom.xml), not from its release date: 1.x–3.x compiled against a single exact
// version rather than a span, so they're listed under that version instead of a range.
const RELEASES: { mcRange: string; tags: string[] }[] = [
  { mcRange: '1.8 – 1.20', tags: ['4.13.0'] },
  { mcRange: '1.8 – 1.19', tags: ['4.12.0-hotfix', '4.11.0', '4.10.2'] },
  { mcRange: '1.8 – 1.18', tags: ['4.10.1', '4.10.0'] },
  {
    mcRange: '1.8',
    tags: [
      '4.9.7', '4.9.6', '4.9.3', '4.8.1', '4.8.0', '4.7.0', '4.5.2', '4.5.1', '4.5.0', '4.4.1',
      '4.4.0', '4.3.0', '4.2.0', '4.1.1', '4.1.0', '4.0.2', '4.0.1.1', '4.0.1',
    ],
  },
  { mcRange: '1.7.x', tags: ['3.9.9.15'] },
  {
    mcRange: '1.7.2',
    tags: [
      '3.9.9.14', '3.9.9.12-fix1', '3.9.9.11-fix1', '3.9.9.9', '3.9.9.8', '3.9.9.7', '3.9.9.6',
      '3.9.9.5', '3.9.9.4', '3.9.9.3', '3.9.9.2', '3.9.9.1', '3.9.9', '3.9.6.5', '3.9.6.4',
      '3.9.6.3', '3.9.6.2', '3.9.6.1', '3.9.6', '3.9.5', '3.9.4', '3.9.3', '3.9.2', '3.9.1',
      '3.9', '3.5.1', '3.5', '3.0.2', '3.0.1', '3.0', '2.7.1', '2.5.2', '2.5.1', '2.5', '2.2.2',
      '2.2.1', '2.2', '2.1.1', '2.1', '2.0.4', '2.0.3', '2.0.2', '2.0.1', '2.0', '1.5.7', '1.5.6',
      '1.5.5', '1.5.3', '1.5.1', '1.5.0', '1.4.8', '1.4.7', '1.4.4', '1.4.1', '1.3.7', '1.3.5',
      '1.3.4', '1.2.4',
    ],
  },
];

export function changelogId(tag: string): string {
  return `cl-${tag.replace(/[^a-zA-Z0-9]/g, '-')}`;
}

export function getMcVersionArchive(lang: 'pl' | 'en'): McArchiveGroup[] {
  const locale = lang === 'pl' ? 'pl-PL' : 'en-US';
  const formatter = new Intl.DateTimeFormat(locale, { year: 'numeric', month: 'short' });
  const byTag = new Map(history.map((r) => [r.tag, r]));
  const release = getReleaseInfo(lang);

  const current: McArchiveGroup = {
    mcRange: release.mcRange ?? '?',
    releases: [
      {
        tag: release.version,
        dateLabel: release.dateLabel,
        htmlUrl: RELEASE_URL + release.version,
        current: true,
        changelogHtml: release.changelogHtml,
      },
    ],
  };

  return [
    current,
    ...RELEASES.map(({ mcRange, tags }) => ({
      mcRange,
      releases: tags.map((tag) => {
        const entry = byTag.get(tag);
        return {
          tag,
          dateLabel: entry ? formatter.format(new Date(entry.publishedAt)) : '',
          htmlUrl: entry?.htmlUrl ?? RELEASE_URL + tag,
          current: false,
          changelogHtml: entry ? renderChangelog(entry.body) : '',
        };
      }),
    })),
  ];
}
