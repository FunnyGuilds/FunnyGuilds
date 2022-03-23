package net.dzikoysk.funnyguilds.listener;

import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.FunnyGuildsLogger;
import net.dzikoysk.funnyguilds.concurrency.ConcurrencyManager;
import net.dzikoysk.funnyguilds.config.MessageConfiguration;
import net.dzikoysk.funnyguilds.config.PluginConfiguration;
import net.dzikoysk.funnyguilds.config.tablist.TablistConfiguration;
import net.dzikoysk.funnyguilds.feature.prefix.IndividualPrefixManager;
import net.dzikoysk.funnyguilds.guild.GuildManager;
import net.dzikoysk.funnyguilds.guild.GuildRankManager;
import net.dzikoysk.funnyguilds.guild.RegionManager;
import net.dzikoysk.funnyguilds.nms.api.NmsAccessor;
import net.dzikoysk.funnyguilds.nms.api.message.MessageAccessor;
import net.dzikoysk.funnyguilds.nms.heart.GuildEntityHelper;
import net.dzikoysk.funnyguilds.user.UserManager;
import net.dzikoysk.funnyguilds.user.UserRankManager;
import org.bukkit.Server;
import org.bukkit.event.Listener;
import org.panda_lang.utilities.inject.annotations.Inject;

public abstract class AbstractFunnyListener implements Listener {

    @Inject
    public FunnyGuilds plugin;
    @Inject
    public FunnyGuildsLogger logger;

    @Inject
    public Server server;

    @Inject
    public PluginConfiguration config;
    @Inject
    public MessageConfiguration messages;
    @Inject
    public TablistConfiguration tablistConfig;

    @Inject
    public ConcurrencyManager concurrencyManager;

    @Inject
    public UserManager userManager;
    @Inject
    public GuildManager guildManager;
    @Inject
    public UserRankManager userRankManager;
    @Inject
    public GuildRankManager guildRankManager;
    @Inject
    public RegionManager regionManager;
    @Inject
    public IndividualPrefixManager individualPrefixManager;

    @Inject
    public NmsAccessor nmsAccessor;
    @Inject
    public MessageAccessor messageAccessor;
    @Inject
    public GuildEntityHelper guildEntityHelper;

}
