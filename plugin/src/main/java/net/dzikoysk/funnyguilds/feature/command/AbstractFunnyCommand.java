package net.dzikoysk.funnyguilds.feature.command;

import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.FunnyGuildsLogger;
import net.dzikoysk.funnyguilds.concurrency.ConcurrencyManager;
import net.dzikoysk.funnyguilds.config.MessageConfiguration;
import net.dzikoysk.funnyguilds.config.PluginConfiguration;
import net.dzikoysk.funnyguilds.config.tablist.TablistConfiguration;
import net.dzikoysk.funnyguilds.guild.GuildManager;
import net.dzikoysk.funnyguilds.guild.GuildRankManager;
import net.dzikoysk.funnyguilds.guild.RegionManager;
import net.dzikoysk.funnyguilds.user.UserManager;
import net.dzikoysk.funnyguilds.user.UserRankManager;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.panda_lang.utilities.inject.annotations.Inject;

public abstract class AbstractFunnyCommand {

    @Inject public FunnyGuilds plugin;
    @Inject public FunnyGuildsLogger logger;

    @Inject public PluginConfiguration config;
    @Inject public MessageConfiguration messages;
    @Inject public TablistConfiguration tablistConfig;

    @Inject public ConcurrencyManager concurrencyManager;

    @Inject public UserManager userManager;
    @Inject public GuildManager guildManager;
    @Inject public UserRankManager userRankManager;
    @Inject public GuildRankManager guildRankManager;
    @Inject public RegionManager regionManager;

    protected void sendMessage(CommandSender sender, String message) {
        if (message == null || message.isEmpty()) {
            return;
        }
        sender.sendMessage(message);
    }

    protected void broadcastMessage(String message) {
        if (message == null || message.isEmpty()) {
            return;
        }
        Bukkit.broadcastMessage(message);
    }

}
