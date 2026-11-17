package net.dzikoysk.funnyguilds.rank;

import java.util.ArrayDeque;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Queue;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;
import net.dzikoysk.funnyguilds.config.PluginConfiguration;
import net.dzikoysk.funnyguilds.guild.Guild;
import net.dzikoysk.funnyguilds.guild.GuildStatKeys;
import net.dzikoysk.funnyguilds.guild.GuildManager;
import net.dzikoysk.funnyguilds.user.User;
import net.dzikoysk.funnyguilds.user.UserManager;
import net.dzikoysk.funnyguilds.user.UserStatKeys;

final class FunnyRankService implements RankService {

    private final Executor executor;
    private final PluginConfiguration config;
    private final UserManager userManager;
    private final GuildManager guildManager;
    private final Map<StatKey<?>, UserRankTracker<?>> userTrackers = new LinkedHashMap<>();
    private final Map<UUID, Boolean> userEligibility = new ConcurrentHashMap<>();
    private final Map<GuildStatAggregationType<?>, Map<StatKey<?>, GuildRankTracker<?>>> guildTrackers = new HashMap<>();
    private final Queue<Update<?>> updates = new ArrayDeque<>();
    // Updates a drain has polled but not completed yet, guarded by this. Between the poll and the
    // action re-acquiring the monitor, the update is owned by no lock, so close() needs this set to
    // know which accepted updates it still has to wait for.
    private final Set<Update<?>> inFlightUpdates = new HashSet<>();
    private boolean processingUpdates;
    // closing rejects new enqueues from the moment close() starts draining; closed rejects direct
    // operations once every accepted update has completed.
    private boolean closing;
    private boolean closed;
    // Test seam: invoked between polling an update and running it, with no monitor held, which is
    // exactly the window close() must not race through. Null outside tests; must not throw.
    volatile Runnable afterPollHook;

    FunnyRankService(Executor executor, PluginConfiguration config, UserManager userManager, GuildManager guildManager) {
        this.executor = executor;
        this.config = config;
        this.userManager = userManager;
        this.guildManager = guildManager;
        this.userManager.setRankService(this);
        this.guildManager.setRankService(this);
        this.userManager.getUsers().forEach(user -> this.userEligibility.put(user.getUUID(), this.isRankedUser(user)));
        UserStatKeys.VALUES.forEach(this::getUserRankTracker);
    }

    @Override
    public synchronized <T extends Number> T setUserStatValue(User user, StatKey<T> requestedKey, T value) {
        StatKey<T> key = StatKey.register(requestedKey);
        if (this.closed) {
            throw new IllegalStateException("Rank service is closed");
        }
        if (this.userManager.findByUuid(user.getUUID()).orElse(null) != user) {
            throw new IllegalArgumentException("User is no longer registered: " + user.getUUID());
        }
        if (key.equals(UserStatKeys.KDR) || key.equals(UserStatKeys.KDA)) {
            throw new IllegalArgumentException("KDR and KDA are derived statistics");
        }
        Objects.requireNonNull(value, "statistic value");
        key.getValueType().cast(value);
        if (value instanceof Double && !Double.isFinite(value.doubleValue())) {
            throw new IllegalArgumentException("Statistic values must be finite");
        }
        UserRankTracker<T> tracker = this.getUserRankTracker(key);
        T previous = tracker.valueOfEntity(user).orElseGet(key::zero);
        tracker.setValue(user, value);
        if (Objects.equals(previous, value)) {
            return value;
        }
        user.markChanged();
        this.updateGuildStats(user, key);
        if (key.equals(UserStatKeys.KILLS) || key.equals(UserStatKeys.DEATHS)) {
            this.updateRatio(user, UserStatKeys.KDR);
        }
        if (key.equals(UserStatKeys.KILLS) || key.equals(UserStatKeys.DEATHS) || key.equals(UserStatKeys.ASSISTS)) {
            this.updateRatio(user, UserStatKeys.KDA);
        }
        return value;
    }

    @Override
    public synchronized <T extends Number> T changeUserStatValue(User user, StatKey<T> requestedKey, UnaryOperator<T> updateFunction) {
        StatKey<T> key = StatKey.register(requestedKey);
        T current = this.getUserRankTracker(key).valueOfEntity(user).orElseGet(key::zero);
        return this.setUserStatValue(user, key, updateFunction.apply(current));
    }

    private void updateRatio(User user, StatKey<Double> key) {
        this.getUserRankTracker(key).setValue(user, this.initialValue(user, key));
        this.updateGuildStats(user, key);
    }

    private void updateGuildStats(User user, StatKey<?> key) {
        user.getGuild().ifPresent(guild -> {
            if (this.guildManager.findByUuid(guild.getUUID()).orElse(null) != guild) {
                return;
            }
            boolean changed = false;
            for (Map.Entry<GuildStatAggregationType<?>, Map<StatKey<?>, GuildRankTracker<?>>> trackers : this.guildTrackers.entrySet()) {
                if (trackers.getValue().containsKey(key) && this.updateGuildStat(guild, key, trackers.getKey())) {
                    changed = true;
                }
            }
            // The guild row persists the average of member points. When its tracker is not
            // materialized yet there is nothing to compare against, so dirty the row conservatively -
            // the serializer recomputes the average fresh at save time, which also materializes
            // the tracker for subsequent writes. Aggregates that nothing persists never dirty the row.
            if (key.equals(UserStatKeys.POINTS)) {
                GuildRankTracker<?> averageTracker = this.guildTrackers
                        .getOrDefault(GuildStatAggregationType.AVERAGE_INT, Collections.emptyMap())
                        .get(UserStatKeys.POINTS);
                changed |= averageTracker == null
                        || this.updateGuildStat(guild, UserStatKeys.POINTS, GuildStatAggregationType.AVERAGE_INT);
            }
            if (changed) {
                guild.markChanged();
            }
        });
    }

    private <T extends Number, R extends Number> boolean updateGuildStat(
            Guild guild, StatKey<T> key, GuildStatAggregationType<R> aggregation) {
        GuildRankTracker<R> tracker = this.getGuildRankTracker(key, aggregation);
        R value = aggregation.computeValue(this, guild, key);
        boolean changed = tracker.valueOfEntity(guild).map(current -> !Objects.equals(current, value)).orElse(true);
        tracker.setValue(guild, value);
        return changed;
    }

    @Override
    @SuppressWarnings("unchecked")
    public synchronized <T extends Number> UserRankTracker<T> getUserRankTracker(StatKey<T> requestedKey) {
        StatKey<T> key = StatKey.register(requestedKey);
        if (GuildStatKeys.isNative(key)) {
            throw new IllegalArgumentException("This statistic belongs to guilds: " + key.key());
        }
        UserRankTracker<T> tracker = (UserRankTracker<T>) this.userTrackers.get(key);
        if (tracker == null) {
            tracker = new UserRankTracker<>(key, this.userManager, this::isTrackedUser);
            this.userTrackers.put(key, tracker);
            tracker.initializeEntities(this.userManager.getUsers().stream()
                    .collect(Collectors.toMap(user -> user, user -> this.initialValue(user, key))));
        }
        return tracker;
    }

    private <T extends Number> T initialValue(User user, StatKey<T> key) {
        if (key.equals(UserStatKeys.POINTS)) {
            return key.getValueType().cast(Math.max(0, this.config.rankStart));
        }
        if (key.equals(UserStatKeys.KDR) || key.equals(UserStatKeys.KDA)) {
            double kills = this.getUserStatValue(user, UserStatKeys.KILLS).orElse(0);
            double deaths = this.getUserStatValue(user, UserStatKeys.DEATHS).orElse(0);
            double numerator = kills + (key.equals(UserStatKeys.KDA)
                    ? this.getUserStatValue(user, UserStatKeys.ASSISTS).orElse(0) : 0);
            return key.getValueType().cast(deaths == 0 ? numerator : numerator / deaths);
        }
        return key.zero();
    }

    @Override
    public boolean isRankedUser(User user) {
        return this.userManager.findByUuid(user.getUUID()).orElse(null) == user
                && (!this.config.skipPrivilegedPlayersInRankPositions || !user.hasPermission("funnyguilds.ranking.exempt"));
    }

    private boolean isTrackedUser(User user) {
        // Every registered user gets an eligibility entry (constructor seed and addUser -> refreshUser),
        // and writes require registration, so the default only covers exotic races - unknown users
        // are treated as ranked rather than silently dropped from tops.
        return this.userManager.findByUuid(user.getUUID()).orElse(null) == user
                && this.userEligibility.getOrDefault(user.getUUID(), true);
    }

    @Override
    @SuppressWarnings("unchecked")
    public synchronized <T extends Number, R extends Number> GuildRankTracker<R> getGuildRankTracker(
            StatKey<T> requestedKey, GuildStatAggregationType<R> aggregation) {
        StatKey<T> key = StatKey.register(requestedKey);
        Map<StatKey<?>, GuildRankTracker<?>> trackers = this.guildTrackers.computeIfAbsent(aggregation, ignored -> new HashMap<>());
        GuildRankTracker<R> tracker = (GuildRankTracker<R>) trackers.get(key);
        if (tracker == null) {
            tracker = new GuildRankTracker<>(keyForAggregation(key, aggregation), this.guildManager, this::isRankedGuild);
            trackers.put(key, tracker);
            tracker.initializeEntities(this.guildManager.getGuilds().stream()
                    .collect(Collectors.toMap(guild -> guild, guild -> aggregation.computeValue(this, guild, key))));
        }
        return tracker;
    }

    private static <R extends Number> StatKey<R> keyForAggregation(StatKey<?> key, GuildStatAggregationType<R> aggregation) {
        return new StatKey<R>() {
            @Override
            public net.kyori.adventure.key.Key key() {
                return net.kyori.adventure.key.Key.key(key.key().namespace(), key.key().value() + "-" + aggregation.key().value());
            }

            @Override
            public Class<R> getValueType() {
                return aggregation.getValueType();
            }

            @Override
            public R zero() {
                Class<R> type = aggregation.getValueType();
                if (type == Integer.class) {
                    return type.cast(0);
                }
                if (type == Long.class) {
                    return type.cast(0L);
                }
                return type.cast(0.0);
            }

            @Override
            public int compare(R first, R second) {
                Class<R> type = aggregation.getValueType();
                // Compare in the tracked type: routing Long through Double.compare would merge
                // totals that differ beyond 2^53 and corrupt positions of exact Long sums.
                if (type == Long.class) {
                    return Long.compare(first.longValue(), second.longValue());
                }
                if (type == Integer.class) {
                    return Integer.compare(first.intValue(), second.intValue());
                }
                return Double.compare(first.doubleValue(), second.doubleValue());
            }
        };
    }

    @Override
    public boolean isRankedGuild(Guild guild) {
        return this.guildManager.findByUuid(guild.getUUID()).orElse(null) == guild
                && !guild.getMembers().isEmpty() && guild.getMembers().size() >= this.config.minMembersToInclude;
    }

    @Override
    public synchronized void refreshUser(User user) {
        if (this.userManager.findByUuid(user.getUUID()).orElse(null) != user) {
            return;
        }
        this.userEligibility.put(user.getUUID(), this.isRankedUser(user));
        this.userTrackers.keySet().forEach(key -> this.refreshUserStat(user, key));
    }

    private <T extends Number> void refreshUserStat(User user, StatKey<T> key) {
        UserRankTracker<T> tracker = this.getUserRankTracker(key);
        tracker.setValue(user, tracker.valueOfEntity(user).orElseGet(() -> this.initialValue(user, key)));
    }

    @Override
    public synchronized void refreshGuild(Guild guild) {
        if (this.guildManager.findByUuid(guild.getUUID()).orElse(null) != guild) {
            return;
        }
        this.guildTrackers.forEach((aggregation, trackers) ->
                trackers.keySet().forEach(key -> this.updateGuildStat(guild, key, aggregation)));
    }

    @Override
    public synchronized void removeUser(User user) {
        this.userTrackers.values().forEach(tracker -> tracker.remove(user));
        this.userEligibility.remove(user.getUUID());
    }

    @Override
    public synchronized void removeGuild(Guild guild) {
        this.guildTrackers.values().forEach(trackers -> trackers.values().forEach(tracker -> tracker.remove(guild)));
    }

    @Override
    public synchronized void refreshAll() {
        this.userManager.getUsers().forEach(this::refreshUser);
        this.guildManager.getGuilds().forEach(this::refreshGuild);
    }

    @Override
    public synchronized Map<StatKey<?>, Number> getUserStats(User user) {
        Map<StatKey<?>, Number> values = new LinkedHashMap<>();
        this.userTrackers.forEach((key, tracker) -> tracker.valueOfEntity(user).ifPresent(value -> values.put(key, value)));
        return Collections.unmodifiableMap(values);
    }

    @Override
    public <T extends Number> CompletableFuture<T> queueSetUserStatValue(User user, StatKey<T> key, T value) {
        return this.enqueue(() -> this.setUserStatValue(user, key, value));
    }

    @Override
    public <T extends Number> CompletableFuture<T> queueChangeUserStatValue(User user, StatKey<T> key, UnaryOperator<T> update) {
        return this.enqueue(() -> this.changeUserStatValue(user, key, update));
    }

    private synchronized <T> CompletableFuture<T> enqueue(Supplier<T> action) {
        Update<T> update = new Update<>(action);
        if (this.closing) {
            update.future.completeExceptionally(new IllegalStateException("Rank service is closed"));
            return update.future;
        }
        this.updates.add(update);
        if (!this.processingUpdates) {
            this.processingUpdates = true;
            try {
                this.executor.execute(this::processUpdates);
            }
            catch (RuntimeException failure) {
                Update<?> pending;
                while ((pending = this.updates.poll()) != null) {
                    pending.future.completeExceptionally(failure);
                }
                this.processingUpdates = false;
            }
        }
        return update.future;
    }

    // The monitor is held only to poll the next update, so server-thread reads and writes can
    // interleave between queued updates instead of waiting behind a whole external batch. Clearing
    // the flag under the monitor together with the empty poll keeps the enqueue race closed: a
    // producer either sees the flag set (its update joins this drain) or schedules a new drain.
    // The polled update is remembered as in-flight until its action completes: the action has to
    // re-acquire the monitor after this method released it, so close() can slip in between the two
    // and must be able to wait for exactly these updates instead of failing them as closed.
    private void processUpdates() {
        while (true) {
            Update<?> update;
            synchronized (this) {
                update = this.updates.poll();
                if (update == null) {
                    this.processingUpdates = false;
                    return;
                }
                this.inFlightUpdates.add(update);
            }
            Runnable afterPollHook = this.afterPollHook;
            if (afterPollHook != null) {
                afterPollHook.run();
            }
            try {
                update.run();
            }
            finally {
                synchronized (this) {
                    this.inFlightUpdates.remove(update);
                }
            }
        }
    }

    @Override
    public void close() {
        List<Update<?>> pending;
        synchronized (this) {
            // Rejecting enqueues from here on makes the inline drain below the last chance for work
            // to be accepted: close completes queued updates itself instead of waiting on a
            // scheduled drain task that may never start.
            this.closing = true;
            this.processUpdates();
            pending = List.copyOf(this.inFlightUpdates);
        }
        // A polled update has left the queue but may not have started yet; an already running one
        // has not completed either. Waiting below finishes every update accepted before close while
        // holding no monitor, because those actions need it to run. An update that invoked close()
        // from within its own function is skipped: joining its future from inside the action would
        // deadlock, so it completes when its function returns, like any other failing action.
        Thread current = Thread.currentThread();
        for (Update<?> update : pending) {
            if (update.runner == current) {
                continue;
            }
            try {
                update.future.join();
            }
            catch (CompletionException ignored) {
                // A failed update still counts as completed; close must not propagate its failure.
            }
        }
        synchronized (this) {
            this.closed = true;
        }
    }

    private static final class Update<T> {
        private final Supplier<T> action;
        private final CompletableFuture<T> future = new CompletableFuture<>();
        // Set once when the action starts, so close() can recognize an update running on its own thread.
        private volatile Thread runner;

        private Update(Supplier<T> action) {
            this.action = action;
        }

        private void run() {
            this.runner = Thread.currentThread();
            try {
                this.future.complete(this.action.get());
            }
            catch (Throwable failure) {
                this.future.completeExceptionally(failure);
            }
        }
    }
}
