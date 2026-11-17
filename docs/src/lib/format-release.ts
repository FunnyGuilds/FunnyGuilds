import { LATEST_RELEASE as releaseMeta } from './release-archive';
import { renderChangelog } from './format-changelog';

export interface ReleaseInfo {
  version: string;
  downloadPath: string;
  sizeLabel: string;
  dateLabel: string;
  mcRange: string | null;
  changelogHtml: string;
}

export function getReleaseInfo(lang: 'pl' | 'en'): ReleaseInfo {
  const locale = lang === 'pl' ? 'pl-PL' : 'en-US';

  return {
    version: releaseMeta.tag,
    downloadPath: releaseMeta.downloadPath,
    sizeLabel: `${new Intl.NumberFormat(locale, { maximumFractionDigits: 1 }).format(releaseMeta.jarSize / (1024 * 1024))} MB`,
    dateLabel: new Intl.DateTimeFormat(locale, { year: 'numeric', month: 'long', day: 'numeric' }).format(new Date(releaseMeta.publishedAt)),
    mcRange: releaseMeta.mcRange,
    changelogHtml: renderChangelog(releaseMeta.body),
  };
}
