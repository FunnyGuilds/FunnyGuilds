package net.dzikoysk.funnyguilds.feature.hooks.bungeetablist;

import codecrafter47.bungeetablistplus.api.bukkit.BungeeTabListPlusBukkitAPI;
import codecrafter47.bungeetablistplus.api.bukkit.Variable;
import java.util.Set;
import java.util.function.Function;
import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.config.PluginConfiguration;
import net.dzikoysk.funnyguilds.feature.hooks.AbstractPluginHook;
import net.dzikoysk.funnyguilds.feature.tablist.variable.DefaultTablistVariables;
import net.dzikoysk.funnyguilds.guild.GuildRankManager;
import net.dzikoysk.funnyguilds.rank.placeholders.RankPlaceholdersService;
import net.dzikoysk.funnyguilds.user.User;
import net.dzikoysk.funnyguilds.user.UserManager;
import net.dzikoysk.funnyguilds.user.UserRankManager;
import org.bukkit.entity.Player;
import panda.utilities.StringUtils;

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
        UserRankManager userRankManager = this.plugin.getUserRankManager();
        GuildRankManager guildRankManager = this.plugin.getGuildRankManager();
        RankPlaceholdersService rankPlaceholdersSerivce = this.plugin.getRankPlaceholdersService();

        DefaultTablistVariables.getFunnyVariables().forEach((id, variable) ->
                BungeeTabListPlusBukkitAPI.registerVariable(plugin, new FunctionVariable(id, player ->
                        userManager.findByPlayer(player)
                                .map(variable::get)
                                .orElseGet(StringUtils.EMPTY)
                )));

        Set<String> userTopIds = userRankManager.getTopIds();
        Set<String> guildTopIds = guildRankManager.getTopIds();

        // User TOP, positions 1-100
        for (int i = 1; i <= 100; i++) {
            final int position = i;

            userTopIds.forEach(id ->
                    BungeeTabListPlusBukkitAPI.registerVariable(plugin, new FunctionVariable("funnyguilds_ptop-" + id + "-" + position, player -> {
                        User user = userManager.findByPlayer(player).orNull();
                        return rankPlaceholdersSerivce.formatTop("{PTOP-" + id.toUpperCase() + "-" + position + "}", user);
                    })));

            if (pluginConfiguration.top.enableLegacyPlaceholders) {
                BungeeTabListPlusBukkitAPI.registerVariable(plugin, new FunctionVariable("funnyguilds_ptop-" + position, player -> {
                    User user = userManager.findByPlayer(player).orNull();
                    return rankPlaceholdersSerivce.formatRank("{PTOP-" + position + "}", user);
                }));
            }
        }

        // Guild TOP, positions 1-100
        for (int i = 1; i <= 100; i++) {
            final int position = i;

            guildTopIds.forEach(id ->
                    BungeeTabListPlusBukkitAPI.registerVariable(plugin, new FunctionVariable("funnyguilds_gtop_" + id + "-" + position, player -> {
                        User user = userManager.findByPlayer(player).orNull();
                        return rankPlaceholdersSerivce.formatTop("{GTOP-" + id.toUpperCase() + "-" + position + "}", user);
                    })));

            if (pluginConfiguration.top.enableLegacyPlaceholders) {
                BungeeTabListPlusBukkitAPI.registerVariable(plugin, new FunctionVariable("funnyguilds_gtop_" + position, player -> {
                    User user = userManager.findByPlayer(player).orNull();
                    return rankPlaceholdersSerivce.formatRank("{GTOP-" + position + "}", user);
                }));
            }
        }

        userTopIds.forEach(id ->
                BungeeTabListPlusBukkitAPI.registerVariable(plugin, new FunctionVariable("funnyguilds_position-" + id, player -> {
                    User user = userManager.findByPlayer(player).orNull();
                    return rankPlaceholdersSerivce.formatTopPosition("{POSITION-" + id.toUpperCase() + "}", user);
                })));

        guildTopIds.forEach(id ->
                BungeeTabListPlusBukkitAPI.registerVariable(plugin, new FunctionVariable("funnyguilds_g-position-" + id, player -> {
                    User user = userManager.findByPlayer(player).orNull();
                    return rankPlaceholdersSerivce.formatTopPosition("{G-POSITION-" + id.toUpperCase() + "}", user);
                })));

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
