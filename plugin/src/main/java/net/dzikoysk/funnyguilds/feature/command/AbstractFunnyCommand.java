package net.dzikoysk.funnyguilds.feature.command;

import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.FunnyGuildsLogger;
import net.dzikoysk.funnyguilds.concurrency.ConcurrencyManager;
import net.dzikoysk.funnyguilds.config.MessageConfiguration;
import net.dzikoysk.funnyguilds.config.PluginConfiguration;
import net.dzikoysk.funnyguilds.config.tablist.TablistConfiguration;
import net.dzikoysk.funnyguilds.rank.RankManager;
import net.dzikoysk.funnyguilds.user.UserManager;
import org.panda_lang.utilities.inject.annotations.Inject;

public abstract class AbstractFunnyCommand {

    @Inject public FunnyGuilds plugin;
    @Inject public FunnyGuildsLogger logger;

    @Inject public PluginConfiguration pluginConfiguration;
    @Inject public MessageConfiguration messageConfiguration;
    @Inject public TablistConfiguration tablistConfiguration;

    @Inject public ConcurrencyManager concurrencyManager;

    @Inject public RankManager rankManager;
    @Inject public UserManager userManager;

}
