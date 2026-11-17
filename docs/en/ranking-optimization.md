# Rankings updated on statistic changes

[Polski](../ranking-optimization.md)

Player and guild rankings update when their data changes. There is no periodic rebuild of every top, and `ranking-update-interval` has been removed.

## Behavior

- The first ordering query sorts a particular top. Loading persisted counters does not sort the ranking after every assignment.
- Subsequent changes reposition one entry: O(log n) comparisons and O(n) array shifts. Position queries use binary search.
- A member's stat change updates only the corresponding guild aggregates that have already been requested. Joining, leaving, renaming and deletion also refresh the affected entries.
- Position 1 has the highest value. Ties use case-insensitive names followed by UUIDs.
- Exempt players and guilds below `guild-min-members` retain their statistic values but have no position. Empty guilds are excluded.
- New players still receive `rank-start` points. Existing guild counter averages retain integer rounding. Guild KDR/KDA continue to distinguish ratios of totals from averages of individual ratios.

`top.enabled-user-tops` and `top.enabled-guild-tops` remain readable for configuration compatibility but no longer restrict available types. Adding a new statistic requires no whitelist edit. `top.enable-legacy-placeholders` still controls `{PTOP-1}` and `{GTOP-1}`.

When `skip-privileged-players-in-rank-positions` is enabled, `funnyguilds.ranking.exempt` checks run on the server thread. Every 40 ticks, online players and up to 32 pending offline players are checked. This avoids moving an expensive all-offline-player Vault scan onto one server tick. An offline exemption change becomes visible when its queued check is reached; configuration reload refreshes everyone. These are permission checks, not periodic top rebuilds.

## Adding statistics through the API

Declare a key and write values through the service at the place where the stat is awarded:

```java
public static final StatKey<Integer> ARENA_WINS = StatKey.integerStatKey("arena_wins");

RankService ranks = plugin.getRankService();
ranks.changeUserStatValue(user, ARENA_WINS, current -> current + 1);
ranks.getUserTopPosition(user, ARENA_WINS);
ranks.getGuildStatValue(guild, ARENA_WINS, GuildStatAggregationType.TOTAL_INT);
```

The key automatically provides tops, positions, guild aggregates and persistence. No comparator, configuration whitelist or additional SQL column is needed. New statistics start at zero. Guild averages of new statistics use floating-point values.

Keys support `Integer` and `Double`, including custom namespaces such as `StatKey.doubleStatKey(Key.key("arena", "balance"))`. One identity cannot have two different value types; NaN and infinite values are rejected. Custom aggregation strategies can use `GuildStatAggregationType.create(...)`.

Example placeholders:

| Value | Placeholder |
| --- | --- |
| First player | `{PTOP-arena_wins-1}` |
| Player position | `{POSITION-arena_wins}` |
| First guild by total | `{GTOP-arena_wins-1}` |
| First guild by average | `{GTOP-avg_arena_wins-1}` |
| Guild position | `{G-POSITION-arena_wins}` |
| Namespaced statistic | `{PTOP-arena:balance-1}` |

Existing `top.format.ptop`, `gtop`, `{VALUE}` and `{VALUE-FORMAT}` formatting remains available. Unknown statistics and absent entries use configured fallbacks. The guild `{POINTS}` placeholder still means average points; `{TOTAL-POINTS}` means the sum.

Additional statistics are stored as JSON containing identity, type and value, in a user's `stats` file field or the user table's `stats TEXT` column. SQL uses the existing missing-column migration mechanism. Old data without this field still loads. Existing counter columns remain authoritative; derived KDR/KDA values and guild aggregates are not persisted as separate copies.

`Rank`, `UserRank` and `GuildRank` remain deprecated adapters. New integrations should use `RankService`. The deleted ranking managers are no longer API. `queueSetUserStatValue` and `queueChangeUserStatValue` return `CompletableFuture`, including exceptional completion for failed updates or rejected scheduling. Closing the service drains pending updates before the final data save.

Integrations changing permissions may call `refreshUser(user)` on the server thread for an immediate exemption refresh.

## Randomized test

`RankServiceRandomizedTest` maintains an independent value and membership model. For a configurable duration it performs random writes, membership and name changes, exemptions, deletions and recreation. Every 32 operations it verifies all values, ordering and positions against rankings sorted from scratch. It covers built-in counters, KDR/KDA and additional `Integer`/`Double` statistics.

The default duration is 3 seconds, with at least 2000 operations and seed `2596`. Output includes the actual operation count. Failures report the seed, operation number and recent actions. To run longer:

```shell
./gradlew :plugin:test --tests "*RankServiceRandomizedTest" -Dfunnyguilds.rank.stressMillis=30000 -Dfunnyguilds.rank.stressSeed=20261117
```

Other tests cover parallel updates, avoiding full re-sorts, file save/load, SQL write payloads and row reads, queue handling, exemptions and placeholders. SQL tests use JDBC mocks rather than a live MySQL/MariaDB server.
