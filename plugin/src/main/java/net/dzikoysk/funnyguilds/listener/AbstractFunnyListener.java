package net.dzikoysk.funnyguilds.listener;

import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.FunnyGuildsLogger;
import net.dzikoysk.funnyguilds.concurrency.ConcurrencyManager;
import net.dzikoysk.funnyguilds.config.MessageConfiguration;
import net.dzikoysk.funnyguilds.config.PluginConfiguration;
import net.dzikoysk.funnyguilds.config.tablist.TablistConfiguration;
import net.dzikoysk.funnyguilds.guild.GuildManager;
import net.dzikoysk.funnyguilds.guild.RegionManager;
import net.dzikoysk.funnyguilds.rank.RankManager;
import net.dzikoysk.funnyguilds.user.UserManager;
import org.bukkit.event.Listener;
import org.panda_lang.utilities.inject.annotations.Inject;

public abstract class AbstractFunnyListener implements Listener {

    @Inject public FunnyGuilds plugin;
    @Inject public FunnyGuildsLogger logger;

    @Inject public PluginConfiguration config;
    @Inject public MessageConfiguration messages;
    @Inject public TablistConfiguration tablistConfig;

    @Inject public ConcurrencyManager concurrencyManager;

    @Inject public UserManager userManager;
    @Inject public GuildManager guildManager;
    @Inject public RankManager rankManager;
    @Inject public RegionManager regionManager;

}
