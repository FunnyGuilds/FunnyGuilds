import releaseMeta from './latest-release.json';
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
  const sizeMb = releaseMeta.jarSize / (1024 * 1024);
  // Match against the extension-stripped name — MC.<range> is always the trailing segment, and
  // the range itself contains dots (e.g. "1.8-1.21"), so anchoring on ".jar" avoids swallowing it.
  const mcMatch = releaseMeta.jarName.replace(/\.jar$/, '').match(/MC\.(.+-.+)$/);

  return {
    version: releaseMeta.tagName,
    downloadPath: releaseMeta.downloadPath,
    sizeLabel: `${new Intl.NumberFormat(locale, { maximumFractionDigits: 1 }).format(sizeMb)} MB`,
    dateLabel: new Intl.DateTimeFormat(locale, { year: 'numeric', month: 'long', day: 'numeric' }).format(new Date(releaseMeta.publishedAt)),
    mcRange: mcMatch ? mcMatch[1].replace('-', ' – ') : null,
    changelogHtml: renderChangelog(releaseMeta.body ?? ''),
  };
}
