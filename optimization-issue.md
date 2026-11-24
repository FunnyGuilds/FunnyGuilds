# Improve FunnyGuilds reliability, Paper API compatibility, and performance

## Summary

Track the work identified in the `optimization` audit at `a59f7edf`. Start with data persistence and event ordering, then migrate deprecated APIs and optimize the paths that show a measurable cost. The supported range is Paper 1.21.5–1.21.11 and 26.1–26.3, with the Java version required by each server series.

This issue tracks several focused PRs. Use `GH-<issue number> <Title>` for each PR title and `Refs #<issue number>` in its description. Update the checklist as PRs merge. Close this issue after the checklist and final verification are complete.

## Reliability

- [ ] **PR 1 — Keep failed SQL saves pending for retry.** `SQLNamedStatement.executeUpdate()` can log an error and return normally. The user, guild, and region serializers then clear the change flag. Make the save outcome available to all three serializers. A failed record must remain pending, the next incremental autosave must retry it, and the remaining records must still get a chance to save. Cover a thrown `SQLException`, unavailable connection, successful write, and successful retry.
- [ ] **Stop rewriting unchanged guilds during ban checks.** Checking an empty ban currently marks the guild as changed, and the periodic task then clears it again. A scan of unbanned guilds or guilds with active bans should produce zero writes for those records. Expiring a ban should mark one change that survives save and reload. This PR can run alongside PR 1.
- [ ] **Serialize concurrent persistence operations.** After PR 1, save a consistent versioned snapshot and acknowledge the version that reached storage. Apply coherent ordering to SQL and flat-file autosave, deletion, and shutdown. Verify that a mutation during I/O remains pending, that an older save cannot restore a deleted record, and that the final save finishes before closing storage.
- [ ] **Handle region transitions in the movement event lifecycle.** Region notifications and guild events currently run in an asynchronous task that may try to cancel movement after the original handler returns. Resolve the transition and its cancellation in time, including movement across a Y boundary and teleportation between worlds at identical X/Z. Cancelling a transition must prevent subsequent entry side effects. Document the event contract for integrations.
- [ ] **Define thread ownership for guild heart interactions, scheduled guild operations, chat, and holograms.** Review Netty callbacks, war tasks, guild expiry, chat delivery, and DecentHolograms updates. Call world and third-party APIs in their supported context, order state changes consistently, and use snapshots for background work. Split by feature where needed. Verify join/quit, heart interaction, expiry, and chat delivery while scheduled work is running.

## Paper, Adventure, configuration, and build

- [ ] **Check the minimum supported API and smoke-test the packaged JAR.** Compile shared modules against the minimum API. Exercise startup, NMS adapter selection, join, login rejection, tablist, guild heart, and both GUIs on Paper 1.21.5, 1.21.11, 26.1, and 26.3. Record the builds used and assess pinning dynamic 26.x dev bundles for reproducibility.
- [ ] **Migrate compatible Paper calls.** Update dummy objective registration, team prefixes, locale resolution, plugin metadata, death messages, and component-based login rejection. Reduce Bukkit `ChatColor` and deprecated `ChatUtils` use while preserving legacy configuration and integration formats. Confirm equivalent output and fewer deprecation warnings on the checked API versions.
- [ ] **Modernize item metadata and matching.** Migrate custom name, lore, model data, damage, material changes, and skull profiles together with the relevant serializers and matchers. Existing item definitions, persisted items, and payment checks must keep working. Include equality checks for items created before and after the change.
- [ ] **Keep components through GUI rendering and use typed MiniMessage replacements.** Pass a component title to InventoryFramework, use typed placeholders for dynamic values, and cover the production parser with tests for literal tags, ampersands, section signs, HEX, reset, and multiline lore. Retain the legacy contract where PlaceholderAPI or persisted configuration requires it.
- [ ] **Resolve version-sensitive API usage.** Review the newer login event, NMS version selection and TPS access, old world-name lookups, entity interaction events, vanish metadata, and FunnyCommands' `CommandMap` usage. Record the compatibility path or blocker for each and verify support on 1.21.5. Preserve existing world identifiers in stored data, hook behavior, and login decisions.
- [ ] **Resolve the scheduled removal of `RegionManager.deleteRegion`.** Define a replacement with the same indexing and storage effects. Migrate deletion, creation rollback, and data repair callers after the persistence changes. Document any public API transition for integrations.
- [ ] **Fix YAML resource cleanup and modernize Okaeri usage.** Track down the Windows file handles behind the `@TempDir` cleanup failures. Verify that standard tests pass with cleanup enabled. Replace deprecated naming, serializer, and configuration methods while preserving YAML keys, nested configuration loading, and atomic rejection of invalid `items.yml` reloads.
- [ ] **Clean up remaining Java and Gradle deprecations.** Migrate deprecated `URL(String)`, the affected Expressible overload, and the repository filtering call. Track Gradle 10/11 warnings from paperweight and Kotlin and recheck them when changing the build toolchain. Run deprecation and removal checks against the minimum and current Paper APIs and record any intentionally retained compatibility calls.

## Performance

- [ ] **Capture a reproducible baseline.** Use spark with the same world, hooks, player and guild counts, and scenarios for each comparison. Include idle, movement, join/quit, combat, and guild creation. Record p50/p95/p99 MSPT, CPU by thread, allocations, network traffic, and storage activity. Refresh the baseline after the reliability fixes before comparing performance PRs. [Paper profiling guide](https://docs.papermc.io/paper/profiling/)
- [ ] **Scoreboard.** Measure the cost of per-observer updates, queue depth, age of the oldest entry, and mutations. Reduce duplicate work and unchanged updates while preserving viewer-specific nametags and scores.
- [ ] **Tablist.** Count placeholder evaluations, component allocations, and outbound packets. Reuse templates and render context where possible, then send updates for changed content while preserving initial join, reload, animated pages, and viewer-specific placeholders.
- [ ] **Rankings.** Measure rebuild time and how quickly a points change reaches players. Precompute sort keys and guild aggregates per snapshot and publish positions consistently. Preserve tie-breaking, exclusions, and all supported rankings.
- [ ] **Guild hearts and territory lookups.** Measure chunk packet processing, entity-ID lookups, and region queries. Evaluate indexes by world/chunk and entity ID, plus candidate deduplication for area queries. Verify equivalent behavior across worlds, chunk boundaries, region moves, and enlargements.
- [ ] **Storage throughput.** After the write-correctness PRs, measure SQL operations, save duration, and the number of pending records. Evaluate batching and a queue of changed identifiers using those measurements.
- [ ] **Review secondary costs under load.** Measure cold-chunk teleportation, WorldEdit heart placement, item matching and GUI rendering, guild lookup and completions, invitation snapshots, and map/hologram refreshes. Record the measurements and land targeted changes for material costs. Verify teleport cancellation and charges, WorldEdit rollback, and overlapping item requirements when changing those paths.

Performance PRs should include before/after results for the changed path and verify that the improvement does not increase another thread's workload or delay updates visible to players. Record measured decisions to retain existing implementations in this issue.

## Final verification

- [ ] Run the full test suite, including MySQL and MariaDB integration tests and Windows YAML cleanup.
- [ ] Smoke-test the final packaged JAR on the supported version matrix and record the results.
- [ ] Review the Polish and English documentation for any changes to behavior, commands, permissions, placeholders, or configuration. Update the README and installation guide if requirements change.
- [ ] Link merged PRs and publish the final before/after measurements in this issue.
