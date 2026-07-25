// Copy + HUD mockup strings for the landing page, one entry per locale. The isometric
// map itself (src/lib/isomap/) needs no localization — the only text it renders is the
// four territory names, which are proper nouns.
import { NAV_CONTENT, type NavContent } from './nav-content';

export interface LandingContent extends NavContent {
  pageTitle: string;
  pageDescription: string;
  eyebrow: string;
  headline: string;
  sub: string;
  tags: [string, string, string, string];
  ctaPrimary: string;
  ctaSecondary: string;
  metaParts: [string, string, string];
  hud: {
    topPoints: string;
    topKdr: string;
    topKills: string;
    scoreRank: string;
    scorePoints: string;
    scoreKdr: string;
    scoreLives: string;
  };
}

export const LANDING_CONTENT: Record<'pl' | 'en', LandingContent> = {
  pl: {
    ...NAV_CONTENT.pl,
    plHref: '/',
    enHref: '/en/',
    pageTitle: 'FunnyGuilds — terytorium, wojny i rangi gildii dla Paper/Spigot',
    pageDescription:
      'Rozbudowany, w pełni konfigurowalny plugin RPG do gildii dla serwerów Minecraft (Paper 1.8–1.21.x). Teren, wojny, rangi i społeczność w jednym pluginie.',
    eyebrow: 'Open-source · Paper / Spigot',
    headline: 'Wszystko, czego potrzebuje gildia.',
    sub: 'Teren, wojny, rangi i społeczność — w jednym pluginie, w pełni pod kontrolą administratora.',
    tags: ['Terytorium', 'Wojny', 'Rangi', 'Społeczność'],
    ctaPrimary: 'Pobierz najnowszą wersję',
    ctaSecondary: 'Zobacz kod źródłowy ↗',
    metaParts: ['Apache-2.0', 'API 1.21', 'Od 2014'],
    hud: {
      topPoints: 'TOP: Punkty',
      topKdr: 'TOP: KDR',
      topKills: 'TOP: Zabójstwa',
      scoreRank: 'Ranga',
      scorePoints: 'Punkty',
      scoreKdr: 'KDR',
      scoreLives: 'Życia',
    },
  },
  en: {
    ...NAV_CONTENT.en,
    plHref: '/',
    enHref: '/en/',
    pageTitle: 'FunnyGuilds — guild territory, war, and rank for Paper/Spigot',
    pageDescription:
      'A powerful, fully configurable RPG guild plugin for Minecraft servers (Paper 1.8–1.21.x). Territory, wars, ranks and community in one plugin.',
    eyebrow: 'Open-source · Paper / Spigot',
    headline: 'Everything a guild needs.',
    sub: 'Territory, wars, ranks and community — in one plugin, fully under the admin’s control.',
    tags: ['Territory', 'Wars', 'Ranks', 'Community'],
    ctaPrimary: 'Download latest version',
    ctaSecondary: 'View source code ↗',
    metaParts: ['Apache-2.0', 'API 1.21', 'Since 2014'],
    hud: {
      topPoints: 'TOP: Points',
      topKdr: 'TOP: KDR',
      topKills: 'TOP: Kills',
      scoreRank: 'Rank',
      scorePoints: 'Points',
      scoreKdr: 'KDR',
      scoreLives: 'Lives',
    },
  },
};
