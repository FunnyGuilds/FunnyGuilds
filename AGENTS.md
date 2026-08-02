# AGENTS.md

## Docs must stay in sync with the plugin

This repo ships the plugin (`plugin/`, `funnyguilds-core/`, `funnyguilds-server-api/`,
`funnyguilds-targets/`, `nms/`) alongside its documentation site (`docs/`, Astro/Starlight,
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
