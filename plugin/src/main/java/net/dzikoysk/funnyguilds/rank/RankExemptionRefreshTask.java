package net.dzikoysk.funnyguilds.rank;

import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.Queue;
import java.util.Set;
import java.util.UUID;
import net.dzikoysk.funnyguilds.config.PluginConfiguration;
import net.dzikoysk.funnyguilds.user.User;
import net.dzikoysk.funnyguilds.user.UserManager;
import net.dzikoysk.funnyguilds.user.UserStatKeys;
import org.bukkit.Bukkit;

/**
 * Checks external permission changes on the server thread without rebuilding any top.
 * <p>
 * Exemption flips are detected through the points tracker's inclusion state: eligibility is global
 * (a single map consulted by every tracker), so an inclusion flip observed on the points tracker
 * implies a change visible to all trackers; refreshing the user then re-evaluates each of them.
 */
public final class RankExemptionRefreshTask implements Runnable {

    private final PluginConfiguration config;
    private final UserManager userManager;
    private final RankService rankService;
    private final Queue<UUID> pendingOfflineChecks = new ArrayDeque<>();
    private static final int OFFLINE_CHECKS_PER_RUN = 32;

    public RankExemptionRefreshTask(PluginConfiguration config, UserManager userManager, RankService rankService) {
        this.config = config;
        this.userManager = userManager;
        this.rankService = rankService;
    }

    @Override
    public void run() {
        if (!this.config.skipPrivilegedPlayersInRankPositions) {
            this.pendingOfflineChecks.clear();
            return;
        }
        RankTracker<User, Integer> tracker = this.rankService.getUserRankTracker(UserStatKeys.POINTS);
        Set<UUID> online = new HashSet<>();
        Bukkit.getOnlinePlayers().forEach(player -> {
            online.add(player.getUniqueId());
            this.userManager.findByUuid(player.getUniqueId()).ifPresent(user -> this.check(user, tracker));
        });
        if (this.pendingOfflineChecks.isEmpty()) {
            this.userManager.getUsers().forEach(user -> this.pendingOfflineChecks.add(user.getUUID()));
        }
        // Offline Vault permission lookups may be expensive. Spread them over ticks instead of
        // moving the old all-player scan onto the server thread in a single large batch.
        for (int i = 0; i < OFFLINE_CHECKS_PER_RUN; i++) {
            UUID uuid = this.pendingOfflineChecks.poll();
            if (uuid == null) {
                break;
            }
            if (!online.contains(uuid)) {
                this.userManager.findByUuid(uuid).ifPresent(user -> this.check(user, tracker));
            }
        }
    }

    private void check(User user, RankTracker<User, Integer> tracker) {
        if (tracker.isEntityIncludedInTop(user) != this.rankService.isRankedUser(user)) {
            this.rankService.refreshUser(user);
        }
    }
}
