package net.dzikoysk.funnyguilds.feature.hooks.bungeetablist;

import codecrafter47.bungeetablistplus.api.bukkit.BungeeTabListPlusBukkitAPI;
import codecrafter47.bungeetablistplus.api.bukkit.Variable;
import java.util.Map.Entry;
import java.util.function.Function;
import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.config.PluginConfiguration;
import net.dzikoysk.funnyguilds.feature.hooks.AbstractPluginHook;
import net.dzikoysk.funnyguilds.feature.tablist.variable.DefaultTablistVariables;
import net.dzikoysk.funnyguilds.feature.tablist.variable.TablistVariable;
import net.dzikoysk.funnyguilds.rank.RankManager;
import net.dzikoysk.funnyguilds.rank.RankUtils;
import net.dzikoysk.funnyguilds.user.User;
import net.dzikoysk.funnyguilds.user.UserManager;
import org.bukkit.entity.Player;

public class BungeeTabListPlusHook extends AbstractPluginHook {

    private final FunnyGuilds plugin;

    public BungeeTabListPlusHook(String name, FunnyGuilds plugin) {
        super(name);
        this.plugin = plugin;
    }

    @Override
    public HookInitResult init() {
        PluginConfiguration pluginConfiguration = this.plugin.getPluginConfiguration();
        UserManager userManager = this.plugin.getUserManager();
        RankManager rankManager = this.plugin.getRankManager();

        for (Entry<String, TablistVariable> variable : DefaultTablistVariables.getFunnyVariables().entrySet()) {
            BungeeTabListPlusBukkitAPI.registerVariable(plugin, new FunctionVariable(variable.getKey(), player -> userManager.findByPlayer(player)
                    .map(user -> variable.getValue().get(user))
                    .orElseGet("")));
        }

        // User TOP, positions 1-100
        for (int i = 1; i <= 100; i++) {
            final int index = i;

            rankManager.getUserTopMap().forEach((id, top) -> {
                BungeeTabListPlusBukkitAPI.registerVariable(plugin, new FunctionVariable("funnyguilds_ptop-" + id + "-" + index, player -> {
                    User user = userManager.findByPlayer(player).getOrNull();
                    return RankUtils.parseTop(user, "{PTOP-" + id.toUpperCase() + "-" + index + "}");
                }));
            });

            if (pluginConfiguration.top.enableLegacyPlaceholders) {
                BungeeTabListPlusBukkitAPI.registerVariable(plugin, new FunctionVariable("funnyguilds_ptop-" + index, player -> {
                    User user = userManager.findByPlayer(player).getOrNull();
                    return RankUtils.parseRank(user, "{PTOP-" + index + "}");
                }));
            }
        }

        // Guild TOP, positions 1-100
        for (int i = 1; i <= 100; i++) {
            final int index = i;

            rankManager.getUserTopMap().forEach((id, top) -> {
                BungeeTabListPlusBukkitAPI.registerVariable(plugin, new FunctionVariable("funnyguilds_gtop_" + id + "-" + index, player -> {
                    User user = userManager.findByPlayer(player).getOrNull();
                    return RankUtils.parseTop(user, "{GTOP-" + id.toUpperCase() + "-" + index + "}");
                }));
            });

            if (pluginConfiguration.top.enableLegacyPlaceholders) {
                BungeeTabListPlusBukkitAPI.registerVariable(plugin, new FunctionVariable("funnyguilds_gtop_" + index, player -> {
                    User user = userManager.findByPlayer(player).getOrNull();
                    return RankUtils.parseRank(user, "{GTOP-" + index + "}");
                }));
            }
        }

        rankManager.getUserTopMap().forEach((id, top) -> {
            BungeeTabListPlusBukkitAPI.registerVariable(plugin, new FunctionVariable("funnyguilds_position-" + id, player -> {
                User user = userManager.findByPlayer(player).getOrNull();
                return RankUtils.parseTopPosition(user, "{POSITION-" + id.toUpperCase() + "}");
            }));
        });

        rankManager.getGuildTopMap().forEach((id, top) -> {
            BungeeTabListPlusBukkitAPI.registerVariable(plugin, new FunctionVariable("funnyguilds_g-position-" + id, player -> {
                User user = userManager.findByPlayer(player).getOrNull();
                return RankUtils.parseTopPosition(user, "{G-POSITION-" + id.toUpperCase() + "}");
            }));
        });

        return HookInitResult.SUCCESS;
    }

    private static class FunctionVariable extends Variable {

        private final Function<Player, String> replacement;

        public FunctionVariable(String name, Function<Player, String> replacement) {
            super(name);
            this.replacement = replacement;
        }

        @Override
        public String getReplacement(Player player) {
            return this.replacement.apply(player);
        }

    }

}
