package net.dzikoysk.funnyguilds;

import eu.okaeri.configs.exception.OkaeriException;
import net.dzikoysk.funnycommands.FunnyCommands;
import net.dzikoysk.funnyguilds.concurrency.ConcurrencyManager;
import net.dzikoysk.funnyguilds.config.ConfigurationFactory;
import net.dzikoysk.funnyguilds.config.MessageConfiguration;
import net.dzikoysk.funnyguilds.config.PluginConfiguration;
import net.dzikoysk.funnyguilds.config.tablist.TablistConfiguration;
import net.dzikoysk.funnyguilds.data.DataModel;
import net.dzikoysk.funnyguilds.data.DataPersistenceHandler;
import net.dzikoysk.funnyguilds.data.InvitationPersistenceHandler;
import net.dzikoysk.funnyguilds.data.database.Database;
import net.dzikoysk.funnyguilds.feature.command.CommandsConfiguration;
import net.dzikoysk.funnyguilds.feature.gui.GuiActionHandler;
import net.dzikoysk.funnyguilds.feature.hooks.PluginHook;
import net.dzikoysk.funnyguilds.feature.tablist.IndividualPlayerList;
import net.dzikoysk.funnyguilds.feature.tablist.TablistBroadcastHandler;
import net.dzikoysk.funnyguilds.feature.validity.GuildValidationHandler;
import net.dzikoysk.funnyguilds.feature.war.WarPacketCallbacks;
import net.dzikoysk.funnyguilds.guild.Guild;
import net.dzikoysk.funnyguilds.guild.GuildUtils;
import net.dzikoysk.funnyguilds.listener.*;
import net.dzikoysk.funnyguilds.listener.dynamic.DynamicListenerManager;
import net.dzikoysk.funnyguilds.listener.region.*;
import net.dzikoysk.funnyguilds.nms.DescriptionChanger;
import net.dzikoysk.funnyguilds.nms.GuildEntityHelper;
import net.dzikoysk.funnyguilds.nms.Reflections;
import net.dzikoysk.funnyguilds.nms.api.NmsAccessor;
import net.dzikoysk.funnyguilds.nms.api.packet.FunnyGuildsChannelHandler;
import net.dzikoysk.funnyguilds.nms.v1_10R1.V1_10R1NmsAccessor;
import net.dzikoysk.funnyguilds.nms.v1_11R1.V1_11R1NmsAccessor;
import net.dzikoysk.funnyguilds.nms.v1_12R1.V1_12R1NmsAccessor;
import net.dzikoysk.funnyguilds.nms.v1_13R2.V1_13R2NmsAccessor;
import net.dzikoysk.funnyguilds.nms.v1_14R1.V1_14R1NmsAccessor;
import net.dzikoysk.funnyguilds.nms.v1_15R1.V1_15R1NmsAccessor;
import net.dzikoysk.funnyguilds.nms.v1_16R3.V1_16R3NmsAccessor;
import net.dzikoysk.funnyguilds.nms.v1_17R1.V1_17R1NmsAccessor;
import net.dzikoysk.funnyguilds.nms.v1_8R3.V1_8R3NmsAccessor;
import net.dzikoysk.funnyguilds.nms.v1_9R2.V1_9R2NmsAccessor;
import net.dzikoysk.funnyguilds.rank.RankRecalculationTask;
import net.dzikoysk.funnyguilds.shared.bukkit.ChatUtils;
import net.dzikoysk.funnyguilds.shared.bukkit.MinecraftServerUtils;
import net.dzikoysk.funnyguilds.telemetry.metrics.MetricsCollector;
import net.dzikoysk.funnyguilds.user.User;
import net.dzikoysk.funnyguilds.user.UserCache;
import net.dzikoysk.funnyguilds.user.UserManager;
import net.dzikoysk.funnyguilds.user.UserUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;
import panda.std.Option;
import panda.utilities.ClassUtils;

import java.io.File;

public class FunnyGuilds extends JavaPlugin {

    private static FunnyGuilds       funnyguilds;
    private static FunnyGuildsLogger logger;

    private final File pluginConfigurationFile  = new File(this.getDataFolder(), "config.yml");
    private final File tablistConfigurationFile = new File(this.getDataFolder(), "tablist.yml");
    private final File messageConfigurationFile = new File(this.getDataFolder(), "messages.yml");
    private final File pluginDataFolderFile     = new File(this.getDataFolder(), "data");

    private FunnyGuildsVersion     version;
    private FunnyCommands          funnyCommands;
    private PluginConfiguration    pluginConfiguration;
    private TablistConfiguration   tablistConfiguration;
    private MessageConfiguration   messageConfiguration;
    private ConcurrencyManager     concurrencyManager;
    private DynamicListenerManager dynamicListenerManager;
    private UserManager            userManager;
    private NmsAccessor            nmsAccessor;

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

        this.nmsAccessor = this.prepareNmsAccessor();

        try {
            ConfigurationFactory configurationFactory = new ConfigurationFactory();
            this.messageConfiguration = configurationFactory.createMessageConfiguration(messageConfigurationFile);
            this.pluginConfiguration = configurationFactory.createPluginConfiguration(pluginConfigurationFile);
            this.tablistConfiguration = configurationFactory.createTablistConfiguration(tablistConfigurationFile);
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

        this.userManager = new UserManager();

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
        this.tablistBroadcastTask = Bukkit.getScheduler().runTaskTimerAsynchronously(this, new TablistBroadcastHandler(), 20L, this.tablistConfiguration.playerListUpdateInterval);
        this.rankRecalculationTask = Bukkit.getScheduler().runTaskTimerAsynchronously(this, new RankRecalculationTask(), 20L, this.pluginConfiguration.rankingUpdateInterval);

        PluginManager pluginManager = Bukkit.getPluginManager();
        pluginManager.registerEvents(new GuiActionHandler(), this);
        pluginManager.registerEvents(new EntityDamage(this), this);
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
        this.handleReload();

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

        this.dataModel.save(false);
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

    private void handleReload() {
        for (Player player : this.getServer().getOnlinePlayers()) {
            final FunnyGuildsChannelHandler channelHandler = nmsAccessor.getPacketAccessor().getOrInstallChannelHandler(player);
            channelHandler.getPacketCallbacksRegistry().registerPacketCallback(new WarPacketCallbacks(player));

            Option<User> userOption = userManager.getUser(player);

            if (userOption.isEmpty()) {
                continue;
            }

            User user = userOption.get();
            UserCache cache = user.getCache();

            cache.updateScoreboardIfNull(player);
            cache.getDummy();

            if (!this.tablistConfiguration.playerListEnable) {
                continue;
            }

            IndividualPlayerList individualPlayerList = new IndividualPlayerList(
                    user,
                    this.getNmsAccessor().getPlayerListAccessor(),
                    this.tablistConfiguration.playerList,
                    this.tablistConfiguration.playerListHeader, this.tablistConfiguration.playerListFooter,
                    this.tablistConfiguration.pages,
                    this.tablistConfiguration.playerListPing,
                    this.tablistConfiguration.playerListFillCells
            );

            cache.setPlayerList(individualPlayerList);
        }

        for (Guild guild : GuildUtils.getGuilds()) {
            if (this.pluginConfiguration.createEntityType != null) {
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

    public TablistConfiguration getTablistConfiguration() {
        return this.tablistConfiguration;
    }

    public File getTablistConfigurationFile() {
        return this.tablistConfigurationFile;
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

    public UserManager getUserManager() {
        return userManager;
    }

    public NmsAccessor getNmsAccessor() {
        return this.nmsAccessor;
    }

    public void reloadPluginConfiguration() throws OkaeriException {
        this.pluginConfiguration.load();
    }

    public void reloadTablistConfiguration() throws OkaeriException {
        this.tablistConfiguration.load();
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

    private NmsAccessor prepareNmsAccessor() {
        switch (Reflections.SERVER_VERSION) {
            case "v1_8_R3":
                return new V1_8R3NmsAccessor();
            case "v1_9_R2":
                return new V1_9R2NmsAccessor();
            case "v1_10_R1":
                return new V1_10R1NmsAccessor();
            case "v1_11_R1":
                return new V1_11R1NmsAccessor();
            case "v1_12_R1":
                return new V1_12R1NmsAccessor();
            case "v1_13_R2":
                return new V1_13R2NmsAccessor();
            case "v1_14_R1":
                return new V1_14R1NmsAccessor();
            case "v1_15_R1":
                return new V1_15R1NmsAccessor();
            case "v1_16_R3":
                return new V1_16R3NmsAccessor();
            case "v1_17_R1":
                return new V1_17R1NmsAccessor();
            default:
                throw new RuntimeException(String.format("Could not find matching NmsAccessor for currently running server version: %s",
                        Reflections.SERVER_VERSION));
        }
    }
}
