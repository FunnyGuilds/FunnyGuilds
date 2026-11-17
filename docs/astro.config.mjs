// @ts-check
import { defineConfig } from 'astro/config';
import { readFileSync, writeFileSync } from 'node:fs';
import path from 'node:path';
import starlight from '@astrojs/starlight';
import sitemap from '@astrojs/sitemap';
import mdx from '@astrojs/mdx';

// Writes /latest.info into the build output, so the deployed site serves it at
// https://funnyguilds.dzikoysk.net/latest.info - the endpoint the plugin's stable version check
// (FunnyGuildsVersion) fetches and compares against its own version byte-exactly. The content is
// the repo's updater.txt verbatim (it already carries "<version> <codename>" and gets bumped with
// releases), with its trailing newline stripped: a newline would nag every up-to-date user.
// Like release-archive.ts, this assumes the build runs from docs/.
function latestVersionInfo() {
  return {
    name: 'latest-version-info',
    hooks: {
      'astro:build:done': ({ dir, logger }) => {
        const latestVersion = readFileSync(path.join(process.cwd(), '..', 'updater.txt'), 'utf8').trim();
        if (!latestVersion) {
          throw new Error('updater.txt is empty - cannot generate latest.info');
        }

        const outDir = dir.href.endsWith('/') ? dir : new URL(dir.href + '/');
        writeFileSync(new URL('latest.info', outDir), latestVersion);
        logger.info(`Served latest.info: ${latestVersion}`);
      },
    },
  };
}

// https://astro.build/config
export default defineConfig({
  site: 'https://funnyguilds.dzikoysk.net',
  integrations: [
    latestVersionInfo(),
    starlight({
      title: 'FunnyGuilds',
      description:
        'Rozbudowany, w pełni konfigurowalny plugin RPG do gildii dla serwerów Minecraft (Paper 1.8–1.21.x).',
      logo: {
        // Same mark as the landing nav's inline SVG in Landing.astro.
        src: './src/assets/logo.svg',
        alt: 'FunnyGuilds',
      },
      favicon: '/favicon.png',
      customCss: [
        // Same font family as the landing page, not Inter/JetBrains Mono.
        '@fontsource/ibm-plex-sans/400.css',
        '@fontsource/ibm-plex-sans/500.css',
        '@fontsource/ibm-plex-sans/600.css',
        '@fontsource/ibm-plex-sans/700.css',
        '@fontsource/ibm-plex-mono/400.css',
        '@fontsource/ibm-plex-mono/500.css',
        './src/styles/custom.css',
      ],
      social: [
        {
          icon: 'github',
          label: 'GitHub',
          href: 'https://github.com/FunnyGuilds/FunnyGuilds',
        },
        {
          icon: 'discord',
          label: 'Discord',
          href: 'https://discord.gg/CYvyq3u',
        },
      ],
      // Light-only (ThemeProvider), and the landing page's own nav (Header/MobileMenuFooter).
      components: {
        ThemeProvider: './src/components/ThemeProvider.astro',
        Header: './src/components/Header.astro',
        MobileMenuFooter: './src/components/MobileMenuFooter.astro',
      },
      defaultLocale: 'root',
      locales: {
        root: { label: 'Polski', lang: 'pl' },
        en: { label: 'English', lang: 'en' },
      },
      editLink: {
        baseUrl:
          'https://github.com/FunnyGuilds/FunnyGuilds/edit/5.x/docs/',
      },
      sidebar: [
        {
          label: 'Wprowadzenie',
          translations: { en: 'Introduction' },
          items: [{ slug: 'introduction' }],
        },
        {
          label: 'Funkcje podstawowe',
          translations: { en: 'Core features' },
          items: [
            { slug: 'guilds' },
            { slug: 'territories' },
            { slug: 'alliances-wars' },
            { slug: 'rankings-combat' },
            { slug: 'chat' },
            { slug: 'tablist' },
            { slug: 'tablist-variables' },
          ],
        },
        {
          label: 'Integracje',
          translations: { en: 'Integrations' },
          items: [
            { slug: 'worldguard-worldedit' },
            { slug: 'vault' },
            { slug: 'placeholderapi' },
            { slug: 'dynmap' },
            { slug: 'decentholograms' },
          ],
        },
        {
          label: 'Materiały referencyjne',
          translations: { en: 'Reference' },
          items: [
            { slug: 'placeholders' },
            { slug: 'commands' },
            { slug: 'permissions' },
            { slug: 'configuration' },
            { slug: 'faq' },
          ],
        },
        {
          label: 'Dla programistów',
          translations: { en: 'Developers' },
          items: [{ slug: 'api' }],
        },
      ],
    }),
    mdx(),
    sitemap({
      i18n: {
        defaultLocale: 'pl',
        locales: { pl: 'pl', en: 'en' },
      },
    }),
  ],
});
