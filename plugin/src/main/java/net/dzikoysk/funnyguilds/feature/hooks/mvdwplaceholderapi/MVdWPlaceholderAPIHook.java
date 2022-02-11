package net.dzikoysk.funnyguilds.feature.hooks.mvdwplaceholderapi;

import be.maximvdw.placeholderapi.PlaceholderAPI;
import java.util.Map.Entry;
import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.config.PluginConfiguration;
import net.dzikoysk.funnyguilds.feature.hooks.AbstractPluginHook;
import net.dzikoysk.funnyguilds.feature.tablist.variable.DefaultTablistVariables;
import net.dzikoysk.funnyguilds.feature.tablist.variable.TablistVariable;
import net.dzikoysk.funnyguilds.rank.RankManager;
import net.dzikoysk.funnyguilds.rank.RankUtils;
import net.dzikoysk.funnyguilds.user.User;
import net.dzikoysk.funnyguilds.user.UserManager;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import panda.utilities.StringUtils;

public class MVdWPlaceholderAPIHook extends AbstractPluginHook {

    private final FunnyGuilds plugin;

    public MVdWPlaceholderAPIHook(String name, FunnyGuilds plugin) {
        super(name);
        this.plugin = plugin;
    }

    @Override
    public HookInitResult init() {
        PluginConfiguration pluginConfiguration = this.plugin.getPluginConfiguration();
        UserManager userManager = this.plugin.getUserManager();
        RankManager rankManager = this.plugin.getRankManager();

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

        // User TOP, positions 1-100
        for (int i = 1; i <= 100; i++) {
            final int index = i;

            rankManager.getUserTopMap().forEach((id, top) -> {
                PlaceholderAPI.registerPlaceholder(plugin, "funnyguilds_ptop-" + id + "-" + index, event -> {
                    User user = userManager.findByPlayer(event.getPlayer()).getOrNull();
                    return RankUtils.parseTop(user, "{PTOP-" + id.toUpperCase() + "-" + index + "}");
                });
            });

            if (pluginConfiguration.top.enableLegacyPlaceholders) {
                PlaceholderAPI.registerPlaceholder(plugin, "funnyguilds_ptop-" + index, event -> {
                    User user = userManager.findByPlayer(event.getPlayer()).getOrNull();
                    return RankUtils.parseRank(user, "{PTOP-" + index + "}");
                });
            }
        }


        // Guild TOP, positions 1-100
        for (int i = 1; i <= 100; i++) {
            final int index = i;

            rankManager.getGuildTopMap().forEach((id, top) -> {
                PlaceholderAPI.registerPlaceholder(plugin, "funnyguilds_gtop-" + id + "-" + index, event -> {
                    User user = userManager.findByPlayer(event.getPlayer()).getOrNull();
                    return RankUtils.parseTop(user, "{GTOP-" + id.toUpperCase() + "-" + index + "}");
                });
            });

            if (pluginConfiguration.top.enableLegacyPlaceholders) {
                PlaceholderAPI.registerPlaceholder(plugin, "funnyguilds_gtop-" + index, event -> {
                    User user = userManager.findByPlayer(event.getPlayer()).getOrNull();
                    return RankUtils.parseRank(user, "{GTOP-" + index + "}");
                });
            }
        }

        return HookInitResult.SUCCESS;
    }

    public String replacePlaceholders(Player user, String base) {
        return PlaceholderAPI.replacePlaceholders(user, base);
    }

}
