import history from './release-history.json';
import { getReleaseInfo } from './format-release';
import { renderChangelog } from './format-changelog';

export interface McArchiveRelease {
  tag: string;
  dateLabel: string;
  htmlUrl: string;
  confidence: 'confirmed' | 'inferred';
  current: boolean;
  changelogHtml: string;
}

export interface McArchiveGroup {
  mcRange: string;
  releases: McArchiveRelease[];
}

// One row per *range*, not per individual MC version — a release that supports 1.8-1.20
// only needs to appear once, not once per point version in that span. Reconstructed from
// the actual git history, not from release dates or changelog prose alone — this project's
// own .classpath/pom.xml, at every commit that touched them from 2014 to 2017, name the
// exact CraftBukkit/Spigot dependency compiled against:
//   2014-10-27 .classpath: "[1.7.2] Bukkit.jar"        (project's first source commit)
//   2014-11-05..12-07     : "[1.7.2 R3] CraftBukkit.jar" (2.x line — despite 2.0's own changelog
//                            claiming "Wsparcie dla spigota 1.8" i.e. 1.8 support added, the
//                            actual compiled dependency never moved off 1.7.2; the two aren't
//                            contradictory; something can run in principle on a newer server
//                            while still only being *built* against the older API)
//   2015-01-19 (3.9.4)    : BOTH "[1.7.2 R3] CraftBukkit.jar" AND "spigot-1.8-weekly...jar" —
//                            matches 3.9.4's own changelog verbatim: it deliberately split into
//                            a 1.7.x line and a "1.8(pre)" line because Spigot's 1.8 API churn
//                            was too disruptive to support well
//   2015-02-08..2016-02-07: back to 1.7.2 only — the 1.8 experiment was shelved; a parallel
//                            "4.0"/Diorite branch pinned org.spigotmc:spigot:1.8.8-R0.1-SNAPSHOT
//                            in its own pom.xml, but never shipped as a numbered release
//   2016-12-01 ("Restored previous sources (#3.9.9.1)") onward through 3.9.9.14's own pom.xml:
//                            org.bukkit:craftbukkit:1.7.2-R0.1-SNAPSHOT, unchanged — the 4.0/
//                            Diorite work was parked and 3.9.x development resumed from where it
//                            left off. No tag exists anywhere in that 2015-03 to 2016-12 gap, so
//                            there's no shipped release this bracketing evidence doesn't cover.
//   3.9.9.15's own pom.xml : identical org.bukkit:craftbukkit:1.7.2-R0.1-SNAPSHOT — the "MC 1.7.x"
//                            broad label it carries is a naming/marketing choice (this changelog
//                            says "Craftbukkit 1.7.x"), not a different actual compiled target.
// Given hard dependency-file evidence brackets this entire span with no gaps and no contradicting
// data point, every release from 1.2.4 through 3.9.9.14 is "confirmed", not "inferred", to target
// the exact same 1.7.2-R0.1-SNAPSHOT build — kept as its own narrow "1.7.2" range, separate from
// 3.9.9.15's broader "1.7.x" one (old plugins here didn't have broad version support the way
// 4.x/5.x do, so the honest unit for this era is one exact version, not a span).
//
// 4.x (2017+): every release since 4.0.1 publishes an explicit "* Spigot <versions>" line in its
// GitHub release body (4.10.0 onward) or was directly confirmed via its own pom.xml/build files —
// direct, confirmed evidence in every case here.
const RELEASES: { tag: string; mcRange: string; confidence: 'confirmed' | 'inferred' }[] = [
  { tag: '4.13.0', mcRange: '1.8 – 1.20', confidence: 'confirmed' },
  { tag: '4.12.0-hotfix', mcRange: '1.8 – 1.19', confidence: 'confirmed' },
  { tag: '4.11.0', mcRange: '1.8 – 1.19', confidence: 'confirmed' },
  { tag: '4.10.2', mcRange: '1.8 – 1.19', confidence: 'confirmed' },
  { tag: '4.10.1', mcRange: '1.8 – 1.18', confidence: 'confirmed' },
  { tag: '4.10.0', mcRange: '1.8 – 1.18', confidence: 'confirmed' },
  { tag: '4.9.7', mcRange: '1.8', confidence: 'confirmed' },
  { tag: '4.9.6', mcRange: '1.8', confidence: 'confirmed' },
  { tag: '4.9.3', mcRange: '1.8', confidence: 'confirmed' },
  { tag: '4.8.1', mcRange: '1.8', confidence: 'confirmed' },
  { tag: '4.8.0', mcRange: '1.8', confidence: 'confirmed' },
  { tag: '4.7.0', mcRange: '1.8', confidence: 'confirmed' },
  { tag: '4.5.2', mcRange: '1.8', confidence: 'confirmed' },
  { tag: '4.5.1', mcRange: '1.8', confidence: 'confirmed' },
  { tag: '4.5.0', mcRange: '1.8', confidence: 'confirmed' },
  { tag: '4.4.1', mcRange: '1.8', confidence: 'confirmed' },
  { tag: '4.4.0', mcRange: '1.8', confidence: 'confirmed' },
  { tag: '4.3.0', mcRange: '1.8', confidence: 'confirmed' },
  { tag: '4.2.0', mcRange: '1.8', confidence: 'confirmed' },
  { tag: '4.1.1', mcRange: '1.8', confidence: 'confirmed' },
  { tag: '4.1.0', mcRange: '1.8', confidence: 'confirmed' },
  { tag: '4.0.2', mcRange: '1.8', confidence: 'confirmed' },
  { tag: '4.0.1.1', mcRange: '1.8', confidence: 'confirmed' },
  { tag: '4.0.1', mcRange: '1.8', confidence: 'confirmed' },
  { tag: '3.9.9.15', mcRange: '1.7.x', confidence: 'confirmed' },
  { tag: '3.9.9.14', mcRange: '1.7.2', confidence: 'confirmed' },
  { tag: '3.9.9.12-fix1', mcRange: '1.7.2', confidence: 'confirmed' },
  { tag: '3.9.9.11-fix1', mcRange: '1.7.2', confidence: 'confirmed' },
  { tag: '3.9.9.9', mcRange: '1.7.2', confidence: 'confirmed' },
  { tag: '3.9.9.8', mcRange: '1.7.2', confidence: 'confirmed' },
  { tag: '3.9.9.7', mcRange: '1.7.2', confidence: 'confirmed' },
  { tag: '3.9.9.6', mcRange: '1.7.2', confidence: 'confirmed' },
  { tag: '3.9.9.5', mcRange: '1.7.2', confidence: 'confirmed' },
  { tag: '3.9.9.4', mcRange: '1.7.2', confidence: 'confirmed' },
  { tag: '3.9.9.3', mcRange: '1.7.2', confidence: 'confirmed' },
  { tag: '3.9.9.2', mcRange: '1.7.2', confidence: 'confirmed' },
  { tag: '3.9.9.1', mcRange: '1.7.2', confidence: 'confirmed' },
  { tag: '3.9.9', mcRange: '1.7.2', confidence: 'confirmed' },
  { tag: '3.9.6.5', mcRange: '1.7.2', confidence: 'confirmed' },
  { tag: '3.9.6.4', mcRange: '1.7.2', confidence: 'confirmed' },
  { tag: '3.9.6.3', mcRange: '1.7.2', confidence: 'confirmed' },
  { tag: '3.9.6.2', mcRange: '1.7.2', confidence: 'confirmed' },
  { tag: '3.9.6.1', mcRange: '1.7.2', confidence: 'confirmed' },
  { tag: '3.9.6', mcRange: '1.7.2', confidence: 'confirmed' },
  { tag: '3.9.5', mcRange: '1.7.2', confidence: 'confirmed' },
  { tag: '3.9.4', mcRange: '1.7.2', confidence: 'confirmed' },
  { tag: '3.9.3', mcRange: '1.7.2', confidence: 'confirmed' },
  { tag: '3.9.2', mcRange: '1.7.2', confidence: 'confirmed' },
  { tag: '3.9.1', mcRange: '1.7.2', confidence: 'confirmed' },
  { tag: '3.9', mcRange: '1.7.2', confidence: 'confirmed' },
  { tag: '3.5.1', mcRange: '1.7.2', confidence: 'confirmed' },
  { tag: '3.5', mcRange: '1.7.2', confidence: 'confirmed' },
  { tag: '3.0.2', mcRange: '1.7.2', confidence: 'confirmed' },
  { tag: '3.0.1', mcRange: '1.7.2', confidence: 'confirmed' },
  { tag: '3.0', mcRange: '1.7.2', confidence: 'confirmed' },
  { tag: '2.7.1', mcRange: '1.7.2', confidence: 'confirmed' },
  { tag: '2.5.2', mcRange: '1.7.2', confidence: 'confirmed' },
  { tag: '2.5.1', mcRange: '1.7.2', confidence: 'confirmed' },
  { tag: '2.5', mcRange: '1.7.2', confidence: 'confirmed' },
  { tag: '2.2.2', mcRange: '1.7.2', confidence: 'confirmed' },
  { tag: '2.2.1', mcRange: '1.7.2', confidence: 'confirmed' },
  { tag: '2.2', mcRange: '1.7.2', confidence: 'confirmed' },
  { tag: '2.1.1', mcRange: '1.7.2', confidence: 'confirmed' },
  { tag: '2.1', mcRange: '1.7.2', confidence: 'confirmed' },
  { tag: '2.0.4', mcRange: '1.7.2', confidence: 'confirmed' },
  { tag: '2.0.3', mcRange: '1.7.2', confidence: 'confirmed' },
  { tag: '2.0.2', mcRange: '1.7.2', confidence: 'confirmed' },
  { tag: '2.0.1', mcRange: '1.7.2', confidence: 'confirmed' },
  { tag: '2.0', mcRange: '1.7.2', confidence: 'confirmed' },
  { tag: '1.5.7', mcRange: '1.7.2', confidence: 'confirmed' },
  { tag: '1.5.6', mcRange: '1.7.2', confidence: 'confirmed' },
  { tag: '1.5.5', mcRange: '1.7.2', confidence: 'confirmed' },
  { tag: '1.5.3', mcRange: '1.7.2', confidence: 'confirmed' },
  { tag: '1.5.1', mcRange: '1.7.2', confidence: 'confirmed' },
  { tag: '1.5.0', mcRange: '1.7.2', confidence: 'confirmed' },
  { tag: '1.4.8', mcRange: '1.7.2', confidence: 'confirmed' },
  { tag: '1.4.7', mcRange: '1.7.2', confidence: 'confirmed' },
  { tag: '1.4.4', mcRange: '1.7.2', confidence: 'confirmed' },
  { tag: '1.4.1', mcRange: '1.7.2', confidence: 'confirmed' },
  { tag: '1.3.7', mcRange: '1.7.2', confidence: 'confirmed' },
  { tag: '1.3.5', mcRange: '1.7.2', confidence: 'confirmed' },
  { tag: '1.3.4', mcRange: '1.7.2', confidence: 'confirmed' },
  { tag: '1.2.4', mcRange: '1.7.2', confidence: 'confirmed' },
];

export function getMcVersionArchive(lang: 'pl' | 'en'): McArchiveGroup[] {
  const locale = lang === 'pl' ? 'pl-PL' : 'en-US';
  const formatter = new Intl.DateTimeFormat(locale, { year: 'numeric', month: 'short' });
  const byTag = new Map(history.map((r) => [r.tag, r]));
  const release = getReleaseInfo(lang);

  const currentEntry: McArchiveRelease = {
    tag: release.version,
    dateLabel: release.dateLabel,
    htmlUrl: `https://github.com/FunnyGuilds/FunnyGuilds/releases/tag/${release.version}`,
    confidence: 'confirmed',
    current: true,
    changelogHtml: release.changelogHtml,
  };

  const historicalReleases: McArchiveRelease[] = RELEASES.map((r) => {
    const entry = byTag.get(r.tag);
    return {
      tag: r.tag,
      dateLabel: entry ? formatter.format(new Date(entry.publishedAt)) : '',
      htmlUrl: entry ? entry.htmlUrl : `https://github.com/FunnyGuilds/FunnyGuilds/releases/tag/${r.tag}`,
      confidence: r.confidence,
      current: false,
      changelogHtml: entry ? renderChangelog(entry.body ?? '') : '',
    };
  });

  // Group adjacent releases that share the exact same range into a single row - RELEASES is
  // already ordered newest-first with same-range releases kept together, so this only ever
  // merges runs that are already adjacent.
  const groups: McArchiveGroup[] = [];
  RELEASES.forEach((r, i) => {
    const last = groups[groups.length - 1];
    if (last && last.mcRange === r.mcRange) {
      last.releases.push(historicalReleases[i]);
    } else {
      groups.push({ mcRange: r.mcRange, releases: [historicalReleases[i]] });
    }
  });

  return [{ mcRange: release.mcRange ?? currentEntry.tag, releases: [currentEntry] }, ...groups];
}
