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
    inferredLabel: string;
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
    installationHref: string;
    installationLabel: string;
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
    pageDescription: 'Pobierz FunnyGuilds — dopasowane do wersji Minecrafta na Twoim serwerze.',
    eyebrow: 'Pobierz',
    headline: 'Pobierz FunnyGuilds',
    lede: 'Wybierz wersję zgodną z Minecraftem na Twoim serwerze.',
    whatYouGet: 'Każda z opcji poniżej to ten sam plugin FunnyGuilds jako pojedynczy plik .jar — wrzuć go do folderu plugins/ na serwerze.',
    snapshot: {
      badge: 'Zalecane',
      title: 'Snapshot 5.x',
      description: 'Najnowszy build z aktywnie rozwijanej gałęzi 5.x — zmienia się z każdą zmianą w kodzie, może zawierać błędy.',
      buttonLabel: 'Pobierz najnowszy build ↗',
      ciLabel: 'Lista buildów na CI ↗',
      note: 'Nowa instalacja — niekompatybilna z wersją 4.x.',
    },
    intermediate: {
      badge: 'Wkrótce',
      mcRange: 'MC 1.21',
      title: 'FunnyGuilds 5.0.0-RC.1',
      description: 'Jedno oficjalne wydanie 5.x dla MC 1.21, zanim gałąź 5.x przejdzie na nowe wersje Minecrafta.',
      note: 'Jeszcze niewydane — wróć tu wkrótce.',
    },
    stable: {
      title: 'Wersja stabilna',
      description: 'Ostatnie oficjalne wydanie gałęzi 4.x.',
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
      intro: 'Każde wydanie FunnyGuilds i zakres wersji Minecrafta, które obsługiwało — zrekonstruowane z rzeczywistych plików .jar, plików budowania (pom.xml/.classpath) i changelogów, nie z dat wydania. Starsze wydania (1.x–3.x) nie miały szerokiego wsparcia wielu wersji na raz, dlatego widnieją pod dokładną wersją, którą kompilowały, zamiast zakresu.',
      currentLabel: 'aktualne',
      inferredLabel: 'szacunkowo',
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
      installationHref: '/installation/',
      installationLabel: 'Jak zainstalować',
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
      description: 'The latest build from the actively developed 5.x branch — changes with every commit, may contain bugs.',
      buttonLabel: 'Download latest build ↗',
      ciLabel: 'Build list on CI ↗',
      note: 'Fresh install only — not compatible with 4.x.',
    },
    intermediate: {
      badge: 'Coming soon',
      mcRange: 'MC 1.21',
      title: 'FunnyGuilds 5.0.0-RC.1',
      description: 'One official 5.x release for MC 1.21, before the 5.x branch moves on to newer Minecraft versions.',
      note: "Not released yet — check back soon.",
    },
    stable: {
      title: 'Stable version',
      description: 'The latest official release of the 4.x branch.',
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
      intro: "Every FunnyGuilds release and the range of Minecraft versions it supported — reconstructed from the real .jar files, build files (pom.xml/.classpath), and changelogs, not release dates. Older releases (1.x-3.x) didn't support many versions at once, so they're listed under the exact version they compiled against instead of a range.",
      currentLabel: 'current',
      inferredLabel: 'estimated',
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
      installationHref: '/en/installation/',
      installationLabel: 'How to install',
      faqHref: '/en/faq/',
      faqLabel: 'FAQ',
    },
  },
};
