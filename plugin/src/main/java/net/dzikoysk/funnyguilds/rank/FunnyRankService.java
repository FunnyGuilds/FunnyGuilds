package net.dzikoysk.funnyguilds.rank;

import java.util.ArrayDeque;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;
import net.dzikoysk.funnyguilds.config.PluginConfiguration;
import net.dzikoysk.funnyguilds.guild.Guild;
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
    private boolean processingUpdates;
    private boolean closed;

    FunnyRankService(Executor executor, PluginConfiguration config, UserManager userManager, GuildManager guildManager) {
        this.executor = executor;
        this.config = config;
        this.userManager = userManager;
        this.guildManager = guildManager;
        this.userManager.getUsers().forEach(user -> this.userEligibility.put(user.getUUID(), this.isRankedUser(user)));
        UserStatKeys.VALUES.forEach(this::getUserRankTracker);
    }

    @Override
    public synchronized <T extends Number> T setUserStatValue(User user, StatKey<T> key, T value) {
        if (this.closed) {
            throw new IllegalStateException("Rank service is closed");
        }
        if (this.userManager.findByUuid(user.getUUID()).orNull() != user) {
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
    public synchronized <T extends Number> T changeUserStatValue(User user, StatKey<T> key, UnaryOperator<T> updateFunction) {
        T current = this.getUserRankTracker(key).valueOfEntity(user).orElseGet(key::zero);
        return this.setUserStatValue(user, key, updateFunction.apply(current));
    }

    private void updateRatio(User user, StatKey<Double> key) {
        this.getUserRankTracker(key).setValue(user, this.initialValue(user, key));
        this.updateGuildStats(user, key);
    }

    private void updateGuildStats(User user, StatKey<?> key) {
        user.getGuild().peek(guild -> {
            if (this.guildManager.findByUuid(guild.getUUID()).orNull() == guild) {
                this.guildTrackers.forEach((aggregation, trackers) -> {
                    if (trackers.containsKey(key)) {
                        this.updateGuildStat(guild, key, aggregation);
                    }
                });
                guild.markChanged();
            }
        });
    }

    private <T extends Number, R extends Number> void updateGuildStat(
            Guild guild, StatKey<T> key, GuildStatAggregationType<R> aggregation) {
        this.getGuildRankTracker(key, aggregation).setValue(guild, aggregation.computeValue(this, guild, key));
    }

    @Override
    @SuppressWarnings("unchecked")
    public synchronized <T extends Number> UserRankTracker<T> getUserRankTracker(StatKey<T> key) {
        StatKey.register(key);
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
        return this.userManager.findByUuid(user.getUUID()).orNull() == user
                && (!this.config.skipPrivilegedPlayersInRankPositions || !user.hasPermission("funnyguilds.ranking.exempt"));
    }

    private boolean isTrackedUser(User user) {
        return this.userManager.findByUuid(user.getUUID()).orNull() == user
                && this.userEligibility.getOrDefault(user.getUUID(), true);
    }

    @Override
    @SuppressWarnings("unchecked")
    public synchronized <T extends Number, R extends Number> GuildRankTracker<R> getGuildRankTracker(
            StatKey<T> key, GuildStatAggregationType<R> aggregation) {
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
                return aggregation.getValueType() == Integer.class
                        ? aggregation.getValueType().cast(0) : aggregation.getValueType().cast(0.0);
            }

            @Override
            public int compare(R first, R second) {
                return Double.compare(first.doubleValue(), second.doubleValue());
            }
        };
    }

    @Override
    public boolean isRankedGuild(Guild guild) {
        return this.guildManager.findByUuid(guild.getUUID()).orNull() == guild
                && !guild.getMembers().isEmpty() && guild.getMembers().size() >= this.config.minMembersToInclude;
    }

    @Override
    public synchronized void refreshUser(User user) {
        if (this.userManager.findByUuid(user.getUUID()).orNull() != user) {
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
        if (this.guildManager.findByUuid(guild.getUUID()).orNull() != guild) {
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
        if (this.closed) {
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

    private synchronized void processUpdates() {
        try {
            Update<?> update;
            while ((update = this.updates.poll()) != null) {
                update.run();
            }
        }
        finally {
            this.processingUpdates = false;
        }
    }

    @Override
    public synchronized void close() {
        this.processUpdates();
        this.closed = true;
    }

    private static final class Update<T> {
        private final Supplier<T> action;
        private final CompletableFuture<T> future = new CompletableFuture<>();

        private Update(Supplier<T> action) {
            this.action = action;
        }

        private void run() {
            try {
                this.future.complete(this.action.get());
            }
            catch (Throwable failure) {
                this.future.completeExceptionally(failure);
            }
        }
    }
}
