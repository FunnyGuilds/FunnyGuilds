# AGENTS.md

## Commit messages

Write commits in English, imperative mood ("Add", "Fix", not "Added"/"Fixes").

- Plugin or docs changes tied to an issue: `GH-<issue number> <Title>`,
  e.g. `GH-2740 Migrate Option/PandaStream to Java 21 Optional/Stream API`.
  Fall back to the PR number when no linked issue exists.
- Build, CI, infrastructure and other non-feature changes use Conventional
  Commit prefixes instead: `ci:`, `build:`, `chore:`, `docs:`, `test:`,
  e.g. `ci: cancel superseded PR runs and publish snapshots in the CI build`.
- Subject under ~72 characters, no trailing period, no conventional-commit
  type in front of `GH-` entries (never `fix: GH-123 ...`).
- Commit as you go: one logical unit per commit, staged files only (never
  sweep unrelated changes or junk files in). Details in the body when the
  "why" is not obvious from the subject; body lines wrapped at ~72 chars.

## Merging pull requests

Merge PRs with squash merge (`gh pr merge <number> --squash`), never with a merge
commit or rebase. This keeps the history linear, matching how recent PRs landed.

- The squash title (which becomes the commit on the base branch) follows the
  same rules as commit messages above: `GH-<issue number> <Title>`, falling
  back to `GH-<PR number> <Title>` when no linked issue exists, or
  conventional prefixes (`ci:`, `build:`, `chore:`, `docs:`) for
  non-feature changes. When a PR title does not follow the convention, edit
  it (`gh pr edit <number> --title ...`) or override the squash title at
  merge time instead of merging it as-is.
- The PR title itself must already follow that format when the PR is opened
  (see global instructions), so the squash usually needs no edit.
- Before merging, confirm all required checks have passed (`gh pr checks <number>`).
- Merging happens only on explicit request (see global instructions).

## Java imports

Never use wildcard imports in Java, including static imports. Always import
each class or member individually and preserve existing explicit imports.

## Optional & Streams

Use `java.util.Optional` and `java.util.stream.Stream` (Java 21) instead of expressible
`Option`/`PandaStream`. Expressible remains a dependency for `Result`, `Pair`, `Triple`,
`Completable` and its functional interfaces.

- Use `Optional.ofNullable` for nullable sources; prefer `orElseThrow()` over `get()`;
  use `orElse(value)` for eager defaults and `orElseGet(Supplier)` for lazy ones.
  `Optional` is not `Iterable` - use `.stream()` to feed streams.
- Prefer unmodifiable collections (`Stream.toList()`, `Collectors.toUnmodifiableSet/toMap`);
  keep mutable collectors only where a caller or config genuinely mutates the result.
- Do not add new wrappers around JDK APIs without a demonstrated need -
  `shared/Optionals` and `shared/FunnyStreams` are the sanctioned helpers.

## Docs must stay in sync with the plugin

This repo ships the plugin (`plugin/`, `nms/`) alongside its documentation site (`docs/`, Astro/Starlight,
Polish default + English at `docs/src/content/docs/en/`).

Any change to the plugin that affects user- or admin-facing behavior — a new command,
permission, config option, placeholder, or a change to existing behavior/defaults — is not
done until the docs have been rechecked against it. Concretely:

- `docs/src/content/docs/commands.mdx` / `en/commands.mdx` — command list.
- `docs/src/content/docs/permissions.mdx` / `en/permissions.mdx` — permission nodes.
- `docs/src/content/docs/placeholders.mdx` / `en/placeholders.mdx` — placeholders.
- `docs/src/content/docs/configuration.mdx` / `en/configuration.mdx` and the relevant
  feature page (`guilds.mdx`, `territories.mdx`, `alliances-wars.mdx`, `rankings-combat.mdx`,
  `chat.mdx`, `tablist.mdx`, etc.) — config options and behavior.

Every PL page has an EN sibling under `en/` — update both, not just one. If a plugin change
has no doc-visible effect, say so explicitly rather than skipping the check silently.

Removing a command or config field counts: `/top` and `/ranking` were deleted from the plugin
but stayed in the docs for months, because nothing forced the recheck.

## README maintenance

The front-page README is `.github/README.md` (there is no root `README.md`). Layout is
compact, Reposilite-style: a centered header with the logo, title, badges, a one-sentence
description and a `Download | Documentation | Discord` link row, followed by two
collapsible sections — 🇵🇱 Polski first, 🇬🇧 English second.

- Every content section exists in both languages. A change to one is not done until it is
  mirrored in the other. PL links use Polish doc paths (`/download`, `/api`), EN links use
  `/en/...`.
- The download page (https://funnyguilds.dzikoysk.net/download) is the single download
  entry point; stable, nightly and legacy builds are all listed there, so the README does
  not duplicate them.
- Use absolute URLs for every image (logo, badges). Relative paths in `.github/README.md`
  resolve differently on the front page, in blob view and in external previewers, and that
  has caused breakage before.
- No GitHub alerts (`> [!WARNING]`): they do not render inside `<details>` blocks. Use a
  plain quote with an emoji instead: `> ⚠️ **...**`.
- No em dashes — plain hyphens only. No raw URLs where a labeled link reads better.
- Keep it short: details belong in the docs site, the README points there. Verify claims
  against the repo before writing them — supported versions come from `gradle.properties`
  (`mcVersions`) and the Java toolchain in `build.gradle.kts`, not from memory.

Changes to requirements (Java, Minecraft/Paper versions) or to the install flow count as
doc-visible for the rule above and cover the README too.
