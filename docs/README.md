# FunnyGuilds — website & documentation

The public site for FunnyGuilds: a landing page plus the full documentation,
built with [Astro](https://astro.build/) + [Starlight](https://starlight.astro.build/).

- **Live site:** https://funnyguilds.dzikoysk.net
- **Languages:** Polish (default, served at `/`) and English (served at `/en/`).
- **Content:** plain Markdown / MDX in `src/content/docs/`. To edit a page, just edit
  its `.mdx` file — Polish pages live directly in `src/content/docs/`, English ones in
  `src/content/docs/en/`.
- **Homepage (`/`):** a fully custom page, *not* a Starlight page — see below.

## The homepage

`/` and `/en/` are both served by `src/components/Landing.astro`, which bypasses the
Starlight page shell entirely (own `<html>`/`<head>`/nav, no sidebar) — `src/pages/index.astro`
and `src/pages/en/index.astro` are thin wrappers that just pick a locale. It's an
isometric "dynmap-style" territory map — four guild forts, a river, terrain — built as a
real 3D scene with [Three.js](https://threejs.org/) in `src/lib/isomap/`: `terrain.ts`
generates the deterministic (seeded) layout as plain data (territories, river, height tiles,
tree/cactus scatter), `geometry.ts` builds the meshes, and `scene.ts` turns it all into a
`THREE.Scene` mounted client-side onto `#map-root`. Terrain is real geometry with real
height, so a normal depth buffer + back-face culling decides what's visible, with almost no
manual occlusion logic to maintain — the one exception is water's edge walls (see
`addWaterWalls` in `geometry.ts`), needed because this camera's fixed angle only ever shows
a block's own +x/+z face. The map needs no localization (it only renders four guild tags);
all copy and the in-map tablist/scoreboard HUD mockup strings live in
`src/lib/landing-content.ts`, keyed by locale. Being seeded, the map is stable across
builds; tweak the constants at the top of `terrain.ts` (territory layout, river shape,
elevation noise) to iterate on it. The same renderer is reused by the doc-page demos
(`GuildCreationDemo`, `HeartSurroundingsDemo`, `EnlargeDemo`) and by `/map-preview/`.

The nav includes a small "PL / EN" language switcher (top right) that links between `/`
and `/en/`.

## Local development

```bash
cd docs
npm install
npm run dev      # http://localhost:4321
```

Other scripts:

```bash
npm run build    # static build into ./dist
npm run preview  # preview the production build locally
```

## Editing content

| Task | Where |
|---|---|
| Add / edit a Polish page | `src/content/docs/<slug>.mdx` |
| Add / edit an English page | `src/content/docs/en/<slug>.mdx` (keep the same `<slug>` as the Polish page) |
| Change the sidebar / nav | `astro.config.mjs` → `starlight({ sidebar })` |
| Homepage copy (`/` + `/en/`) | `src/lib/landing-content.ts` |
| Homepage layout/styles | `src/components/Landing.astro` |
| The isometric map (layout / terrain data) | `src/lib/isomap/terrain.ts` |
| The isometric map (3D scene / rendering) | `src/lib/isomap/scene.ts`, `geometry.ts` |
| Download page copy | `src/lib/download-content.ts` |
| Release list / archive table | `docs/archive/<tag>/`, indexed by `src/lib/release-archive.ts` |
| Docs theme / colors / fonts | `src/styles/custom.css` (Starlight pages only — the homepage has its own styles) |

All content pages now exist in both Polish and English (translated from the
[FunnyGuilds wiki](https://github.com/FunnyGuilds/FunnyGuilds/wiki)). Starlight falls
back to the Polish content for any future page that's missing an English translation,
so the language switcher never dead-ends.

## Deployment — Cloudflare Pages

The site is a fully static build. Deployment is done via **Cloudflare Pages' GitHub
integration** (connect the repository in the Cloudflare dashboard). Configure these
build settings:

| Setting | Value |
|---|---|
| Production branch | `5.x` |
| Root directory | `docs` |
| Build command | `npm run build` |
| Build output directory | `dist` |

`wrangler.toml` in this folder already declares `pages_build_output_dir = "dist"`.

### Custom domain

Attach `funnyguilds.dzikoysk.net` to the Pages project (Pages → Custom domains).
This replaces the current placeholder page at that domain — update DNS to the
Cloudflare Pages target when ready.

### Manual one-off deploy

```bash
npm run deploy   # builds, then runs `wrangler pages deploy ./dist`
```

Requires `wrangler login` (or `CLOUDFLARE_API_TOKEN` + `CLOUDFLARE_ACCOUNT_ID`).
