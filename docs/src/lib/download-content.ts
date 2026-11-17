import { NAV_CONTENT, type NavContent } from './nav-content';

export interface DownloadContent extends NavContent {
  pageTitle: string;
  pageDescription: string;
  eyebrow: string;
  headline: string;
  lede: string;
  whatYouGet: string;
  nightly: {
    badge: string;
    title: string;
    description: string;
    buttonLabel: string;
    ciLabel: string;
    note: string;
  };
  stable: {
    badge: string;
    title: string;
    description: string;
    buttonLabel: string;
    historyLabel: string;
  };
  fourX: {
    badge: string;
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
    stable: {
      badge: 'Zalecane',
      title: 'Wersja stabilna',
      description: 'Najnowsze oficjalne wydanie. Wymaga Papera i Javy 21; konfiguracja z 4.x nie zostanie wczytana.',
      buttonLabel: 'Pobierz',
      historyLabel: 'Starsze wersje ↓',
    },
    nightly: {
      badge: 'Nightly',
      title: 'Snapshot 5.x',
      description: 'Build powstający po każdej zmianie w kodzie, z funkcjami, które trafią do kolejnego wydania. Może zawierać błędy.',
      buttonLabel: 'Pobierz najnowszy build ↗',
      ciLabel: 'Lista buildów na CI ↗',
      note: 'Do testów, nie na serwer produkcyjny.',
    },
    fourX: {
      badge: 'Seria 4.x',
      title: 'FunnyGuilds 4.14.0',
      description: 'Ostatnie wydanie serii 4.x i jedyna opcja dla serwerów starszych niż 1.21.5. Nie jest już rozwijane.',
      buttonLabel: 'Pobierz',
      historyLabel: 'Changelog ↓',
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
    stable: {
      badge: 'Recommended',
      title: 'Stable version',
      description: 'The latest official release. Requires Paper and Java 21; a 4.x configuration will not load.',
      buttonLabel: 'Download',
      historyLabel: 'Older versions ↓',
    },
    nightly: {
      badge: 'Nightly',
      title: 'Snapshot 5.x',
      description: 'A build produced on every commit, carrying the features headed for the next release. May contain bugs.',
      buttonLabel: 'Download latest build ↗',
      ciLabel: 'Build list on CI ↗',
      note: 'For testing, not for a production server.',
    },
    fourX: {
      badge: '4.x series',
      title: 'FunnyGuilds 4.14.0',
      description: 'The last 4.x release and the only option for servers older than 1.21.5. No longer developed.',
      buttonLabel: 'Download',
      historyLabel: 'Changelog ↓',
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
