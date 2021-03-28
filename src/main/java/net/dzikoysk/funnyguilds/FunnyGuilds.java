package net.dzikoysk.funnyguilds;

import eu.okaeri.configs.ConfigManager;
import eu.okaeri.configs.exception.OkaeriException;
import eu.okaeri.configs.postprocessor.SectionSeparator;
import eu.okaeri.configs.serdes.SimpleObjectTransformer;
import eu.okaeri.configs.yaml.bukkit.YamlBukkitConfigurer;
import net.dzikoysk.funnycommands.FunnyCommands;
import net.dzikoysk.funnyguilds.basic.guild.Guild;
import net.dzikoysk.funnyguilds.basic.guild.GuildUtils;
import net.dzikoysk.funnyguilds.basic.rank.RankRecalculationTask;
import net.dzikoysk.funnyguilds.basic.user.User;
import net.dzikoysk.funnyguilds.basic.user.UserUtils;
import net.dzikoysk.funnyguilds.command.CommandsConfiguration;
import net.dzikoysk.funnyguilds.concurrency.ConcurrencyManager;
import net.dzikoysk.funnyguilds.data.DataModel;
import net.dzikoysk.funnyguilds.data.DataPersistenceHandler;
import net.dzikoysk.funnyguilds.data.InvitationPersistenceHandler;
import net.dzikoysk.funnyguilds.data.configs.MessageConfiguration;
import net.dzikoysk.funnyguilds.data.configs.PluginConfiguration;
import net.dzikoysk.funnyguilds.data.database.Database;
import net.dzikoysk.funnyguilds.element.gui.GuiActionHandler;
import net.dzikoysk.funnyguilds.element.tablist.AbstractTablist;
import net.dzikoysk.funnyguilds.element.tablist.TablistBroadcastHandler;
import net.dzikoysk.funnyguilds.hook.PluginHook;
import net.dzikoysk.funnyguilds.listener.*;
import net.dzikoysk.funnyguilds.listener.dynamic.DynamicListenerManager;
import net.dzikoysk.funnyguilds.listener.region.*;
import net.dzikoysk.funnyguilds.system.GuildValidationHandler;
import net.dzikoysk.funnyguilds.util.commons.ChatUtils;
import net.dzikoysk.funnyguilds.util.commons.bukkit.MinecraftServerUtils;
import net.dzikoysk.funnyguilds.util.metrics.MetricsCollector;
import net.dzikoysk.funnyguilds.util.nms.DescriptionChanger;
import net.dzikoysk.funnyguilds.util.nms.GuildEntityHelper;
import net.dzikoysk.funnyguilds.util.nms.PacketExtension;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;
import org.panda_lang.utilities.commons.ClassUtils;

import java.io.File;

public class FunnyGuilds extends JavaPlugin {

    private static FunnyGuilds       funnyguilds;
    private static FunnyGuildsLogger logger;

    private final File pluginConfigurationFile  = new File(this.getDataFolder(), "config.yml");
    private final File messageConfigurationFile = new File(this.getDataFolder(), "messages.yml");
    private final File pluginDataFolderFile     = new File(this.getDataFolder(), "data");

    private FunnyGuildsVersion     version;
    private FunnyCommands          funnyCommands;
    private PluginConfiguration    pluginConfiguration;
    private MessageConfiguration   messageConfiguration;
    private ConcurrencyManager     concurrencyManager;
    private DynamicListenerManager dynamicListenerManager;

    private volatile BukkitTask guildValidationTask;
    private volatile BukkitTask tablistBroadcastTask;
    private volatile BukkitTask rankRecalculationTask;

    private DataModel                    dataModel;
    private DataPersistenceHandler       dataPersistenceHandler;
    private InvitationPersistenceHandler invitationPersistenceHandler;

    private boolean isDisabling;
    private boolean forceDisabling;

    @Override
    public void onLoad() {
        funnyguilds = this;
        logger = new FunnyGuildsLogger(this);
        this.version = new FunnyGuildsVersion(this);

        try {
            Class.forName("net.md_5.bungee.api.ChatColor");
        }
        catch (Exception spigotNeeded) {
            logger.error("FunnyGuilds requires spigot to work, your server seems to be using something else");
            logger.error("If you think that is not true - contact plugin developers");
            logger.error("https://github.com/FunnyGuilds/FunnyGuilds");

            shutdown("Spigot required for service not detected!");
            return;
        }

        if (! this.getDataFolder().exists()) {
            this.getDataFolder().mkdir();
        }

        try {
            this.messageConfiguration = ConfigManager.create(MessageConfiguration.class, (it) -> {
                it.withConfigurer(new YamlBukkitConfigurer(SectionSeparator.NONE));
                it.withSerdesPack(registry -> registry.register(SimpleObjectTransformer.of(String.class, String.class, MessageConfiguration::decolor)));
                it.withBindFile(this.messageConfigurationFile);
                it.saveDefaults();
                it.load(true);
            });
            this.pluginConfiguration = ConfigManager.create(PluginConfiguration.class, (it) -> {
                it.withConfigurer(new YamlBukkitConfigurer(SectionSeparator.NONE));
                it.withBindFile(this.pluginConfigurationFile);
                it.saveDefaults();
                it.load(true);
            });
        }
        catch (Exception exception) {
            logger.error("Could not load plugin configuration", exception);
            shutdown("Critical error has been encountered!");
            return;
        }

        DescriptionChanger descriptionChanger = new DescriptionChanger(super.getDescription());
        descriptionChanger.rename(pluginConfiguration.pluginName);

        this.concurrencyManager = new ConcurrencyManager(this, pluginConfiguration.concurrencyThreads);
        this.concurrencyManager.printStatus();

        CommandsConfiguration commandsConfiguration = new CommandsConfiguration();
        this.funnyCommands = commandsConfiguration.createFunnyCommands(getServer(), this);

        this.dynamicListenerManager = new DynamicListenerManager(this);
        PluginHook.earlyInit();
    }

    @Override
    public void onEnable() {
        funnyguilds = this;

        if (this.forceDisabling) {
            return;
        }

        try {
            this.dataModel = DataModel.create(this, this.pluginConfiguration.dataModel);
            this.dataModel.load();
        }
        catch (Exception ex) {
            logger.error("Could not load data from database", ex);
            shutdown("Critical error has been encountered!");
            return;
        }

        this.dataPersistenceHandler = new DataPersistenceHandler(this);
        this.dataPersistenceHandler.startHandler();

        this.invitationPersistenceHandler = new InvitationPersistenceHandler(this);
        this.invitationPersistenceHandler.loadInvitations();
        this.invitationPersistenceHandler.startHandler();

        MetricsCollector collector = new MetricsCollector(this);
        collector.start();

        this.guildValidationTask = Bukkit.getScheduler().runTaskTimerAsynchronously(this, new GuildValidationHandler(), 100L, 20L);
        this.tablistBroadcastTask = Bukkit.getScheduler().runTaskTimerAsynchronously(this, new TablistBroadcastHandler(), 20L, this.pluginConfiguration.playerListUpdateInterval_);
        this.rankRecalculationTask = Bukkit.getScheduler().runTaskTimerAsynchronously(this, new RankRecalculationTask(), 20L, this.pluginConfiguration.rankingUpdateInterval_);

        PluginManager pluginManager = Bukkit.getPluginManager();
        pluginManager.registerEvents(new GuiActionHandler(), this);
        pluginManager.registerEvents(new EntityDamage(), this);
        pluginManager.registerEvents(new EntityInteract(), this);
        pluginManager.registerEvents(new PlayerChat(), this);
        pluginManager.registerEvents(new PlayerDeath(), this);
        pluginManager.registerEvents(new PlayerJoin(this), this);
        pluginManager.registerEvents(new PlayerLogin(), this);
        pluginManager.registerEvents(new PlayerQuit(), this);
        pluginManager.registerEvents(new GuildHeartProtectionHandler(), this);
        pluginManager.registerEvents(new TntProtection(), this);

        if (pluginConfiguration.regionsEnabled && pluginConfiguration.blockFlow) {
            pluginManager.registerEvents(new BlockFlow(), this);
        }

        if (ClassUtils.forName("org.bukkit.event.entity.EntityPlaceEvent").isPresent()) {
            pluginManager.registerEvents(new EntityPlace(), this);
        }
        else {
            logger.warning("Cannot register EntityPlaceEvent listener on this version of server");
        }

        this.dynamicListenerManager.registerDynamic(() -> pluginConfiguration.regionsEnabled,
                new BlockBreak(),
                new BlockIgnite(),
                new BlockPlace(),
                new BucketAction(),
                new EntityExplode(),
                new HangingBreak(),
                new HangingPlace(),
                new PlayerCommand(),
                new PlayerInteract(),
                new EntityProtect()
        );

        this.dynamicListenerManager.registerDynamic(() -> pluginConfiguration.regionsEnabled && pluginConfiguration.eventMove, new PlayerMove());
        this.dynamicListenerManager.registerDynamic(() -> pluginConfiguration.regionsEnabled && pluginConfiguration.eventPhysics, new BlockPhysics());
        this.dynamicListenerManager.registerDynamic(() -> pluginConfiguration.regionsEnabled && pluginConfiguration.respawnInBase, new PlayerRespawn());
        this.dynamicListenerManager.reloadAll();
        this.patch();

        this.version.isNewAvailable(this.getServer().getConsoleSender(), true);
        PluginHook.init();

        if (MinecraftServerUtils.getReloadCount() > 0) {
            Bukkit.broadcast(ChatUtils.colored(messageConfiguration.reloadWarn), "funnyguilds.admin");
        }

        logger.info("~ Created by FunnyGuilds Team ~");
    }

    @Override
    public void onDisable() {
        if (this.forceDisabling) {
            return;
        }

        this.isDisabling = true;

        this.funnyCommands.dispose();
        this.dynamicListenerManager.unregisterAll();
        GuildEntityHelper.despawnGuildHearts();

        this.guildValidationTask.cancel();
        this.tablistBroadcastTask.cancel();
        this.rankRecalculationTask.cancel();

        for (User user : UserUtils.getUsers()) {
            user.getBossBar().removeNotification();
        }

        this.dataModel.save(true);
        this.dataPersistenceHandler.stopHandler();

        this.invitationPersistenceHandler.saveInvitations();
        this.invitationPersistenceHandler.stopHandler();

        this.getServer().getScheduler().cancelTasks(this);
        this.getConcurrencyManager().awaitTermination(this.pluginConfiguration.pluginTaskTerminationTimeout);

        Database.getInstance().shutdown();

        funnyguilds = null;
    }

    public void shutdown(String content) {
        if (this.isDisabling() || this.forceDisabling) {
            return;
        }

        this.forceDisabling = true;
        logger.warning("The FunnyGuilds is going to shut down! " + content);
        this.getServer().getPluginManager().disablePlugin(this);
    }

    private void patch() {
        for (Player player : this.getServer().getOnlinePlayers()) {
            this.getServer().getScheduler().runTask(this, () -> PacketExtension.registerPlayer(player));

            User user = User.get(player);

            if (user.getCache().getScoreboard() == null) {
                if (pluginConfiguration.useSharedScoreboard) {
                    user.getCache().setScoreboard(player.getScoreboard());
                }
                else {
                    user.getCache().setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
                }
            }

            user.getCache().getDummy();

            if (!pluginConfiguration.playerListEnable) {
                continue;
            }

            AbstractTablist.createTablist(pluginConfiguration.playerList, pluginConfiguration.playerListHeader, pluginConfiguration.playerListFooter, pluginConfiguration.playerListPing, player);
        }

        for (Guild guild : GuildUtils.getGuilds()) {
            if (pluginConfiguration.createEntityType != null) {
                GuildEntityHelper.spawnGuildHeart(guild);
            }

            guild.updateRank();
        }
    }

    public boolean isDisabling() {
        return this.isDisabling;
    }

    public FunnyGuildsVersion getVersion() {
        return this.version;
    }

    public File getPluginDataFolder() {
        return this.pluginDataFolderFile;
    }

    public DataModel getDataModel() {
        return this.dataModel;
    }

    public DataPersistenceHandler getDataPersistenceHandler() {
        return this.dataPersistenceHandler;
    }

    public InvitationPersistenceHandler getInvitationPersistenceHandler() {
        return this.invitationPersistenceHandler;
    }

    public PluginConfiguration getPluginConfiguration() {
        return this.pluginConfiguration;
    }

    public File getPluginConfigurationFile() {
        return this.pluginConfigurationFile;
    }

    public MessageConfiguration getMessageConfiguration() {
        return this.messageConfiguration;
    }

    public ConcurrencyManager getConcurrencyManager() {
        return this.concurrencyManager;
    }

    public DynamicListenerManager getDynamicListenerManager() {
        return this.dynamicListenerManager;
    }

    public void reloadPluginConfiguration() throws OkaeriException {
        this.pluginConfiguration.load();
    }

    public void reloadMessageConfiguration() throws OkaeriException {
        this.messageConfiguration.load();
    }

    public static FunnyGuilds getInstance() {
        return funnyguilds;
    }

    public static FunnyGuildsLogger getPluginLogger() {
        return logger;
    }

}
