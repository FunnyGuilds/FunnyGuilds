import { NAV_CONTENT, type NavContent } from './nav-content';

export interface DownloadContent extends NavContent {
  pageTitle: string;
  pageDescription: string;
  eyebrow: string;
  headline: string;
  lede: string;
  whatYouGet: string;
  snapshot: {
    badge: string;
    title: string;
    description: string;
    buttonLabel: string;
    ciLabel: string;
    note: string;
  };
  intermediate: {
    badge: string;
    mcRange: string;
    title: string;
    description: string;
    note: string;
  };
  stable: {
    title: string;
    description: string;
    buttonLabel: string;
    historyLabel: string;
  };
  legacy: {
    badge: string;
    mcRange: string;
    title: string;
    description: string;
    buttonLabel: string;
    sizeLabel: string;
    archiveLabel: string;
  };
  mcVersionTable: {
    title: string;
    intro: string;
    currentLabel: string;
    nightlyLabel: string;
    changelogLinkLabel: string;
    changelogEyebrow: string;
    releasedLabel: string;
  };
  community: {
    fallbackName: string;
    description: string;
    buttonLabel: string;
    onlineLabel: string;
    membersLabel: string;
  };
  nextSteps: {
    introductionHref: string;
    introductionLabel: string;
    faqHref: string;
    faqLabel: string;
  };
}

export const DOWNLOAD_CONTENT: Record<'pl' | 'en', DownloadContent> = {
  pl: {
    ...NAV_CONTENT.pl,
    plHref: '/download/',
    enHref: '/en/download/',
    pageTitle: 'Pobierz — FunnyGuilds',
    pageDescription: 'Pobierz FunnyGuilds w wersji dopasowanej do Minecrafta na Twoim serwerze.',
    eyebrow: 'Pobierz',
    headline: 'Pobierz FunnyGuilds',
    lede: 'Wybierz wersję zgodną z Minecraftem na Twoim serwerze.',
    whatYouGet: 'Każda z opcji poniżej to ten sam plugin FunnyGuilds — pojedynczy plik .jar, który wrzucasz do folderu plugins/ na serwerze.',
    snapshot: {
      badge: 'Zalecane',
      title: 'Snapshot 5.x',
      description: 'Najnowszy build aktywnie rozwijanej serii 5.x — powstaje po każdej zmianie w kodzie, więc może zawierać błędy.',
      buttonLabel: 'Pobierz najnowszy build ↗',
      ciLabel: 'Lista buildów na CI ↗',
      note: 'Tylko nowa instalacja — niezgodna z wersją 4.x.',
    },
    intermediate: {
      badge: 'Wkrótce',
      mcRange: 'MC 1.21',
      title: 'FunnyGuilds 5.0.0-RC.1',
      description: 'Jedno oficjalne wydanie serii 5.x dla MC 1.21, zanim przejdzie ona na nowsze wersje Minecrafta.',
      note: 'Jeszcze niewydane — zajrzyj tu wkrótce.',
    },
    stable: {
      title: 'Wersja stabilna',
      description: 'Ostatnie oficjalne wydanie serii 4.x.',
      buttonLabel: 'Pobierz',
      historyLabel: 'Starsze wersje ↓',
    },
    legacy: {
      badge: 'Historyczna',
      mcRange: 'MC 1.7.x',
      title: 'FunnyGuilds 3.9.9.15',
      description: 'Ostatnie wydanie z obsługą Minecrafta 1.7. Nowsze wersje wymagają 1.8+.',
      buttonLabel: 'Pobierz',
      sizeLabel: '4,5 MB · 2017',
      archiveLabel: 'Pełne archiwum wersji 1.x–3.x ↗',
    },
    mcVersionTable: {
      title: 'Pełne archiwum wydań',
      intro: 'Każde wydanie FunnyGuilds wraz z zakresem obsługiwanych przez nie wersji Minecrafta. Starsze wydania (1.x–3.x) nie obsługiwały wielu wersji jednocześnie, dlatego widnieją przy dokładnej wersji, dla której je zbudowano.',
      currentLabel: 'aktualne',
      nightlyLabel: 'NIGHTLY',
      changelogLinkLabel: 'Zobacz na GitHubie ↗',
      changelogEyebrow: 'Changelog',
      releasedLabel: 'Wydano',
    },
    community: {
      fallbackName: 'Discord',
      description: 'Dołącz do społeczności FunnyGuilds — pytania, pomoc, dyskusje o rozwoju pluginu.',
      buttonLabel: 'Dołącz ↗',
      onlineLabel: 'online',
      membersLabel: 'członków',
    },
    nextSteps: {
      introductionHref: '/introduction/',
      introductionLabel: 'Wprowadzenie',
      faqHref: '/faq/',
      faqLabel: 'FAQ',
    },
  },
  en: {
    ...NAV_CONTENT.en,
    plHref: '/download/',
    enHref: '/en/download/',
    pageTitle: 'Download — FunnyGuilds',
    pageDescription: 'Download FunnyGuilds — matched to the Minecraft version on your server.',
    eyebrow: 'Download',
    headline: 'Download FunnyGuilds',
    lede: "Pick the version that matches your server's Minecraft.",
    whatYouGet: 'Every option below is the same FunnyGuilds plugin as a single .jar file — drop it into your server\'s plugins/ folder.',
    snapshot: {
      badge: 'Recommended',
      title: 'Snapshot 5.x',
      description: 'The latest build of the actively developed 5.x series — changes with every commit, may contain bugs.',
      buttonLabel: 'Download latest build ↗',
      ciLabel: 'Build list on CI ↗',
      note: 'Fresh install only — not compatible with 4.x.',
    },
    intermediate: {
      badge: 'Coming soon',
      mcRange: 'MC 1.21',
      title: 'FunnyGuilds 5.0.0-RC.1',
      description: 'One official 5.x release for MC 1.21, before the 5.x series moves on to newer Minecraft versions.',
      note: "Not released yet — check back soon.",
    },
    stable: {
      title: 'Stable version',
      description: 'The latest official release of the 4.x series.',
      buttonLabel: 'Download',
      historyLabel: 'Older versions ↓',
    },
    legacy: {
      badge: 'Legacy',
      mcRange: 'MC 1.7.x',
      title: 'FunnyGuilds 3.9.9.15',
      description: 'The last release supporting Minecraft 1.7. Newer versions require 1.8+.',
      buttonLabel: 'Download',
      sizeLabel: '4.5 MB · 2017',
      archiveLabel: 'Full 1.x–3.x archive ↗',
    },
    mcVersionTable: {
      title: 'Full release archive',
      intro: "Every FunnyGuilds release and the range of Minecraft versions it supported. Older releases (1.x–3.x) didn't support many versions at once, so they're listed under the exact version they were built against.",
      currentLabel: 'current',
      nightlyLabel: 'NIGHTLY',
      changelogLinkLabel: 'View on GitHub ↗',
      changelogEyebrow: 'Changelog',
      releasedLabel: 'Released',
    },
    community: {
      fallbackName: 'Discord',
      description: 'Join the FunnyGuilds community — questions, help, and discussion about plugin development.',
      buttonLabel: 'Join ↗',
      onlineLabel: 'online',
      membersLabel: 'members',
    },
    nextSteps: {
      introductionHref: '/en/introduction/',
      introductionLabel: 'Introduction',
      faqHref: '/en/faq/',
      faqLabel: 'FAQ',
    },
  },
};
