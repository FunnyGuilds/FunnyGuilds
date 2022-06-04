package net.dzikoysk.funnyguilds.feature.hooks.mvdwplaceholderapi;

import be.maximvdw.placeholderapi.PlaceholderAPI;
import java.util.Set;
import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.config.PluginConfiguration;
import net.dzikoysk.funnyguilds.feature.hooks.AbstractPluginHook;
import net.dzikoysk.funnyguilds.feature.tablist.TablistPlaceholdersService;
import net.dzikoysk.funnyguilds.guild.GuildRankManager;
import net.dzikoysk.funnyguilds.rank.placeholders.RankPlaceholdersService;
import net.dzikoysk.funnyguilds.user.User;
import net.dzikoysk.funnyguilds.user.UserManager;
import net.dzikoysk.funnyguilds.user.UserRankManager;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import panda.std.stream.PandaStream;

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
        UserRankManager userRankManager = this.plugin.getUserRankManager();
        GuildRankManager guildRankManager = this.plugin.getGuildRankManager();
        RankPlaceholdersService rankPlaceholdersService = this.plugin.getRankPlaceholdersService();
        TablistPlaceholdersService tablistPlaceholdersService = this.plugin.getTablistPlaceholdersService();

        tablistPlaceholdersService.getPlaceholdersKeys().forEach(key ->
                PlaceholderAPI.registerPlaceholder(this.plugin, "funnyguilds_" + key.toLowerCase(), event -> {
                    OfflinePlayer target = event.getOfflinePlayer();
                    if (target == null) {
                        return "";
                    }

                    return userManager.findByUuid(target.getUniqueId())
                            .map(user -> tablistPlaceholdersService.formatIdentifier(key, user))
                            .orElseGet("none");
                }));

        Set<String> userTopIds = userRankManager.getTopIds();
        Set<String> guildTopIds = guildRankManager.getTopIds();

        // User TOP, positions 1-100
        for (int i = 1; i <= 100; i++) {
            int position = i;

            PandaStream.of(userTopIds).forEach(id ->
                    PlaceholderAPI.registerPlaceholder(this.plugin, "funnyguilds_ptop-" + id + "-" + position, event -> {
                        User user = userManager.findByPlayer(event.getPlayer()).orNull();
                        return rankPlaceholdersService.formatTop("{PTOP-" + id.toUpperCase() + "-" + position + "}", user);
                    }));

            if (pluginConfiguration.top.enableLegacyPlaceholders) {
                PlaceholderAPI.registerPlaceholder(this.plugin, "funnyguilds_ptop-" + position, event -> {
                    User user = userManager.findByPlayer(event.getPlayer()).orNull();
                    return rankPlaceholdersService.formatRank("{PTOP-" + position + "}", user);
                });
            }
        }

        // Guild TOP, positions 1-100
        for (int i = 1; i <= 100; i++) {
            int position = i;

            PandaStream.of(guildTopIds).forEach(id ->
                    PlaceholderAPI.registerPlaceholder(this.plugin, "funnyguilds_gtop-" + id + "-" + position, event -> {
                        User user = userManager.findByPlayer(event.getPlayer()).orNull();
                        return rankPlaceholdersService.formatTop("{GTOP-" + id.toUpperCase() + "-" + position + "}", user);
                    }));

            if (pluginConfiguration.top.enableLegacyPlaceholders) {
                PlaceholderAPI.registerPlaceholder(this.plugin, "funnyguilds_gtop-" + position, event -> {
                    User user = userManager.findByPlayer(event.getPlayer()).orNull();
                    return rankPlaceholdersService.formatRank("{GTOP-" + position + "}", user);
                });
            }
        }

        PandaStream.of(userTopIds).forEach(id ->
                PlaceholderAPI.registerPlaceholder(this.plugin, "funnyguilds_position-" + id, event -> {
                    User user = userManager.findByPlayer(event.getPlayer()).orNull();
                    return rankPlaceholdersService.formatTopPosition("{POSITION-" + id.toUpperCase() + "}", user);
                }));

        PandaStream.of(guildTopIds).forEach(id ->
                PlaceholderAPI.registerPlaceholder(this.plugin, "funnyguilds_g-position-" + id, event -> {
                    User user = userManager.findByPlayer(event.getPlayer()).orNull();
                    return rankPlaceholdersService.formatTopPosition("{G-POSITION-" + id.toUpperCase() + "}", user);
                }));

        return HookInitResult.SUCCESS;
    }

    public static String replacePlaceholders(Player user, String base) {
        return PlaceholderAPI.replacePlaceholders(user, base);
    }

}
