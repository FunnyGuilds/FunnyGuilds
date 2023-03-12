package net.dzikoysk.funnyguilds.listener;

import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.FunnyGuildsLogger;
import net.dzikoysk.funnyguilds.config.PluginConfiguration;
import net.dzikoysk.funnyguilds.config.message.MessageService;
import net.dzikoysk.funnyguilds.config.tablist.TablistConfiguration;
import net.dzikoysk.funnyguilds.damage.DamageManager;
import net.dzikoysk.funnyguilds.guild.GuildManager;
import net.dzikoysk.funnyguilds.guild.GuildRankManager;
import net.dzikoysk.funnyguilds.guild.RegionManager;
import net.dzikoysk.funnyguilds.nms.api.NmsAccessor;
import net.dzikoysk.funnyguilds.nms.heart.GuildEntityHelper;
import net.dzikoysk.funnyguilds.shared.bukkit.FunnyServer;
import net.dzikoysk.funnyguilds.user.UserManager;
import net.dzikoysk.funnyguilds.user.UserRankManager;
import org.bukkit.event.Listener;
import org.panda_lang.utilities.inject.annotations.Inject;

public abstract class AbstractFunnyListener implements Listener {

    @Inject
    public FunnyGuilds plugin;
    @Inject
    public FunnyGuildsLogger logger;

    @Inject
    public FunnyServer funnyServer;

    @Inject
    public PluginConfiguration config;
    @Inject
    public TablistConfiguration tablistConfig;

    @Inject
    public MessageService messageService;

    @Inject
    public UserManager userManager;
    @Inject
    public GuildManager guildManager;
    @Inject
    public UserRankManager userRankManager;
    @Inject
    public GuildRankManager guildRankManager;
    @Inject
    public DamageManager damageManager;
    @Inject
    public RegionManager regionManager;

    @Inject
    public NmsAccessor nmsAccessor;
    @Inject
    public GuildEntityHelper guildEntityHelper;

}
