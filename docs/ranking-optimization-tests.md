# Incremental ranking validation

The implementation is reconciled with `5.x@b8d0753c`. Public behavior and API
changes are documented in both languages in `src/content/docs/rankings-combat.mdx`,
`placeholders.mdx`, `configuration.mdx` and `api.mdx`, and their `en/` siblings.

## Run

```shell
./gradlew :plugin:test
./gradlew :plugin:test --tests '*RankServiceRandomizedTest' -Dfunnyguilds.rank.stressMillis=30000 -Dfunnyguilds.rank.stressSeed=20261117
```

The randomized test defaults to 3 seconds and at least 2000 operations. These
can be changed with `funnyguilds.rank.stressMillis`, `funnyguilds.rank.stressSeed`
and `funnyguilds.rank.stressMinOperations`. A failure includes the seed,
operation index and last 24 actions. A fixed seed reproduces the same operation
prefix; the duration controls how long that sequence runs on a particular machine.

## Coverage

- Independent oracle: 160 players, 12 guilds, built-in counters, KDR/KDA, custom
  integer/double statistics, and guild-owned war counters. The model owns its
  values, names and member sets, and sorts from scratch every 32 operations.
- Random actions include writes, queued updates, joins/transfers/leaves, renames,
  exclusions, membership replacement, deletion/recreation, guild threshold
  changes, and war counter changes. Every top's values, ordering, counts,
  entity-to-position and position-to-entity queries are checked.
- A 30-second run with seed `20261117` completed 301418 mutations without an
  oracle mismatch. This is a correctness stress test, not a server TPS benchmark.
- A comparison-count test verifies that loading unrequested tops does not sort
  them and that a subsequent update uses binary searches instead of re-sorting
  4096 entries. Unchanged writes do not reposition entries.
- Parallel producers verify 4000 updates without lost increments or duplicate
  entries. Permission checks are asserted to stay on the server thread.
- Queue tests cover exceptions, rejected scheduling, removed users and draining
  pending updates before shutdown. Permission polling tests bound offline lookups.
- Real YAML serialization/deserialization and typed custom stat persistence are
  tested. SQL tests verify schema definitions, serializer inputs and row loading
  with JDBC mocks; they do not claim a live database round-trip.
- Existing component formatting, suffixes, guild native war tops, identity
  protections and startup validation remain covered by the current 5.x suite.

## Local environment

The linked worktree needed a temporary Gradle init script to point grgit at the
common checkout (the bundled JGit does not resolve this worktree's HEAD), and to
exclude unchanged NMS implementation artifacts for plugin/API tests. The final
5.x validation uses the declared library versions without substituting the old
prototype's unavailable `6.8.0-SNAPSHOT` dependency.

The existing MySQL/MariaDB container migration suites are disabled upstream;
they are not counted as passed integration tests. No server deployment or
production database migration is performed by these tests.

## Final checks

- Plugin/API suite: 139 passed, 8 pre-existing disabled container tests, 0 failures.
- Astro/Starlight production build: 43 pages built successfully in PL and EN.
- `git diff --check`: clean.
