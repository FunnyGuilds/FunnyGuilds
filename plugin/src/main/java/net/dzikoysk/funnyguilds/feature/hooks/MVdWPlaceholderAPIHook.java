package net.dzikoysk.funnyguilds.feature.hooks;

import be.maximvdw.placeholderapi.PlaceholderAPI;
import java.util.Map.Entry;
import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.feature.tablist.variable.DefaultTablistVariables;
import net.dzikoysk.funnyguilds.feature.tablist.variable.TablistVariable;
import net.dzikoysk.funnyguilds.rank.RankUtils;
import net.dzikoysk.funnyguilds.user.User;
import net.dzikoysk.funnyguilds.user.UserManager;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import panda.utilities.StringUtils;

public final class MVdWPlaceholderAPIHook {

    public static void initPlaceholderHook() {
        FunnyGuilds plugin = FunnyGuilds.getInstance();
        UserManager userManager = plugin.getUserManager();

        for (Entry<String, TablistVariable> variable : DefaultTablistVariables.getFunnyVariables().entrySet()) {
            PlaceholderAPI.registerPlaceholder(plugin, "funnyguilds_" + variable.getKey(), event -> {
                OfflinePlayer target = event.getOfflinePlayer();

                if (target == null) {
                    return StringUtils.EMPTY;
                }

                return userManager.findByUuid(target.getUniqueId())
                        .map(user -> variable.getValue().get(user))
                        .orElseGet("none");
            });
        }

        // Guild TOP, positions 1-100
        for (int i = 1; i <= 100; i++) {
            final int index = i;
            PlaceholderAPI.registerPlaceholder(plugin, "funnyguilds_gtop-" + index, event -> {
                User user = userManager.findByPlayer(event.getPlayer()).getOrNull();
                return RankUtils.parseRank(user, "{GTOP-" + index + "}");
            });
        }

        // User TOP, positions 1-100
        for (int i = 1; i <= 100; i++) {
            final int index = i;
            PlaceholderAPI.registerPlaceholder(plugin, "funnyguilds_ptop-" + index, event -> RankUtils.parseRank(null, "{PTOP-" + index + "}"));
        }

        FunnyGuilds.getPluginLogger().info("MVdWPlaceholderAPI hook has been enabled!");
    }

    public static String replacePlaceholders(Player user, String base) {
        return PlaceholderAPI.replacePlaceholders(user, base);
    }

    private MVdWPlaceholderAPIHook() {}

}
