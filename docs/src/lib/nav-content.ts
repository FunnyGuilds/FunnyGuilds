export interface NavContent {
  lang: 'pl' | 'en';
  nav: { home: string; docs: string; download: string; configurator: string };
  docsHref: string;
  configHref: string;
  downloadHref: string;
  plHref: string;
  enHref: string;
}

export type NavActive = 'home' | 'docs' | 'download' | 'configurator';

export interface NavItem {
  href: string;
  label: string;
  active: boolean;
}

// Single source of truth for the 4 top-nav tabs, so Nav.astro (landing/download's own
// page shell) and Header.astro (Starlight's docs shell) can't drift apart on hrefs — the
// actual bug that shipped once already, when Header.astro hardcoded its own copy.
export function getNavItems(t: NavContent, active: NavActive): NavItem[] {
  return [
    { href: t.lang === 'pl' ? '/' : '/en/', label: t.nav.home, active: active === 'home' },
    { href: t.docsHref, label: t.nav.docs, active: active === 'docs' },
    { href: t.downloadHref, label: t.nav.download, active: active === 'download' },
    { href: t.configHref, label: t.nav.configurator, active: active === 'configurator' },
  ];
}

// Shared by Header.astro/MobileMenuFooter.astro, which (unlike Nav.astro) serve many
// different docs pages and must derive the current page's own PL/EN sibling from its path.
export function getLocalePaths(pathname: string): { plHref: string; enHref: string } {
  const isEn = pathname === '/en' || pathname.startsWith('/en/');
  return {
    plHref: isEn ? pathname.replace(/^\/en(\/|$)/, '/') : pathname,
    enHref: isEn ? pathname : '/en' + (pathname === '/' ? '/' : pathname),
  };
}

export const NAV_CONTENT: Record<'pl' | 'en', Omit<NavContent, 'plHref' | 'enHref'>> = {
  pl: {
    lang: 'pl',
    nav: { home: 'Strona główna', docs: 'Dokumentacja', download: 'Pobierz', configurator: 'Konfigurator' },
    docsHref: '/introduction/',
    configHref: '/configuration/',
    downloadHref: '/download/',
  },
  en: {
    lang: 'en',
    nav: { home: 'Home', docs: 'Documentation', download: 'Download', configurator: 'Configurator' },
    docsHref: '/en/introduction/',
    configHref: '/en/configuration/',
    downloadHref: '/en/download/',
  },
};
