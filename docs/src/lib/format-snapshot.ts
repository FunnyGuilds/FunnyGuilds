import snapshotMeta from './latest-snapshot.json';

export interface SnapshotInfo {
  version: string;
  buildNumber: number;
  artifactUrl: string;
  jobUrl: string;
  dateLabel: string;
  mcRange: string | null;
}

export function getSnapshotInfo(lang: 'pl' | 'en'): SnapshotInfo {
  const locale = lang === 'pl' ? 'pl-PL' : 'en-US';
  // "FunnyGuilds 5.0.0-SNAPSHOT.1808 (MC 1.21.x).jar" -> "5.0.0-SNAPSHOT.1808"
  const versionMatch = snapshotMeta.jarName.match(/FunnyGuilds (\S+)/);

  return {
    version: versionMatch ? versionMatch[1] : `#${snapshotMeta.buildNumber}`,
    buildNumber: snapshotMeta.buildNumber,
    artifactUrl: snapshotMeta.artifactUrl,
    jobUrl: snapshotMeta.jobUrl,
    dateLabel: new Intl.DateTimeFormat(locale, { year: 'numeric', month: 'long', day: 'numeric' }).format(new Date(snapshotMeta.timestamp)),
    mcRange: snapshotMeta.mcRange,
  };
}
