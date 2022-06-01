package net.dzikoysk.funnyguilds;

import com.google.common.collect.ImmutableSet;
import eu.okaeri.configs.exception.OkaeriException;
import java.io.File;
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
import net.dzikoysk.funnyguilds.feature.command.FunnyCommandsConfiguration;
import net.dzikoysk.funnyguilds.feature.gui.GuiActionHandler;
import net.dzikoysk.funnyguilds.feature.hooks.HookManager;
import net.dzikoysk.funnyguilds.feature.invitation.ally.AllyInvitationList;
import net.dzikoysk.funnyguilds.feature.invitation.guild.GuildInvitationList;
import net.dzikoysk.funnyguilds.feature.notification.bossbar.BossBarService;
import net.dzikoysk.funnyguilds.feature.placeholders.BasicPlaceholdersService;
import net.dzikoysk.funnyguilds.feature.placeholders.TimePlaceholdersService;
import net.dzikoysk.funnyguilds.feature.prefix.IndividualPrefixManager;
import net.dzikoysk.funnyguilds.feature.tablist.IndividualPlayerList;
import net.dzikoysk.funnyguilds.feature.tablist.TablistBroadcastHandler;
import net.dzikoysk.funnyguilds.feature.tablist.TablistPlaceholdersService;
import net.dzikoysk.funnyguilds.feature.validity.GuildValidationHandler;
import net.dzikoysk.funnyguilds.feature.war.WarPacketCallbacks;
import net.dzikoysk.funnyguilds.guild.GuildManager;
import net.dzikoysk.funnyguilds.guild.GuildRankManager;
import net.dzikoysk.funnyguilds.guild.RegionManager;
import net.dzikoysk.funnyguilds.guild.placeholders.GuildPlaceholdersService;
import net.dzikoysk.funnyguilds.listener.BlockFlow;
import net.dzikoysk.funnyguilds.listener.EntityDamage;
import net.dzikoysk.funnyguilds.listener.EntityInteract;
import net.dzikoysk.funnyguilds.listener.PlayerChat;
import net.dzikoysk.funnyguilds.listener.PlayerDeath;
import net.dzikoysk.funnyguilds.listener.PlayerJoin;
import net.dzikoysk.funnyguilds.listener.PlayerLogin;
import net.dzikoysk.funnyguilds.listener.PlayerQuit;
import net.dzikoysk.funnyguilds.listener.TntProtection;
import net.dzikoysk.funnyguilds.listener.dynamic.DynamicListenerManager;
import net.dzikoysk.funnyguilds.listener.region.BlockBreak;
import net.dzikoysk.funnyguilds.listener.region.BlockIgnite;
import net.dzikoysk.funnyguilds.listener.region.BlockPhysics;
import net.dzikoysk.funnyguilds.listener.region.BlockPlace;
import net.dzikoysk.funnyguilds.listener.region.BucketAction;
import net.dzikoysk.funnyguilds.listener.region.EntityExplode;
import net.dzikoysk.funnyguilds.listener.region.EntityPlace;
import net.dzikoysk.funnyguilds.listener.region.EntityProtect;
import net.dzikoysk.funnyguilds.listener.region.GuildHeartProtectionHandler;
import net.dzikoysk.funnyguilds.listener.region.HangingBreak;
import net.dzikoysk.funnyguilds.listener.region.HangingPlace;
import net.dzikoysk.funnyguilds.listener.region.PlayerCommand;
import net.dzikoysk.funnyguilds.listener.region.PlayerInteract;
import net.dzikoysk.funnyguilds.listener.region.PlayerMove;
import net.dzikoysk.funnyguilds.listener.region.PlayerRespawn;
import net.dzikoysk.funnyguilds.listener.region.PlayerTeleport;
import net.dzikoysk.funnyguilds.nms.DescriptionChanger;
import net.dzikoysk.funnyguilds.nms.Reflections;
import net.dzikoysk.funnyguilds.nms.api.NmsAccessor;
import net.dzikoysk.funnyguilds.nms.api.message.MessageAccessor;
import net.dzikoysk.funnyguilds.nms.api.packet.FunnyGuildsInboundChannelHandler;
import net.dzikoysk.funnyguilds.nms.api.packet.FunnyGuildsOutboundChannelHandler;
import net.dzikoysk.funnyguilds.nms.heart.GuildEntityHelper;
import net.dzikoysk.funnyguilds.nms.heart.GuildEntitySupplier;
import net.dzikoysk.funnyguilds.nms.v1_10R1.V1_10R1NmsAccessor;
import net.dzikoysk.funnyguilds.nms.v1_11R1.V1_11R1NmsAccessor;
import net.dzikoysk.funnyguilds.nms.v1_12R1.V1_12R1NmsAccessor;
import net.dzikoysk.funnyguilds.nms.v1_13R2.V1_13R2NmsAccessor;
import net.dzikoysk.funnyguilds.nms.v1_14R1.V1_14R1NmsAccessor;
import net.dzikoysk.funnyguilds.nms.v1_15R1.V1_15R1NmsAccessor;
import net.dzikoysk.funnyguilds.nms.v1_16R3.V1_16R3NmsAccessor;
import net.dzikoysk.funnyguilds.nms.v1_17R1.V1_17R1NmsAccessor;
import net.dzikoysk.funnyguilds.nms.v1_18R1.V1_18R1NmsAccessor;
import net.dzikoysk.funnyguilds.nms.v1_18R2.V1_18R2NmsAccessor;
import net.dzikoysk.funnyguilds.nms.v1_8R3.V1_8R3NmsAccessor;
import net.dzikoysk.funnyguilds.nms.v1_9R2.V1_9R2NmsAccessor;
import net.dzikoysk.funnyguilds.rank.DefaultTops;
import net.dzikoysk.funnyguilds.rank.RankRecalculationTask;
import net.dzikoysk.funnyguilds.rank.placeholders.RankPlaceholdersService;
import net.dzikoysk.funnyguilds.shared.bukkit.ChatUtils;
import net.dzikoysk.funnyguilds.shared.bukkit.FunnyServer;
import net.dzikoysk.funnyguilds.shared.bukkit.MinecraftServerUtils;
import net.dzikoysk.funnyguilds.telemetry.metrics.MetricsCollector;
import net.dzikoysk.funnyguilds.user.User;
import net.dzikoysk.funnyguilds.user.UserCache;
import net.dzikoysk.funnyguilds.user.UserManager;
import net.dzikoysk.funnyguilds.user.UserRankManager;
import net.dzikoysk.funnyguilds.user.placeholders.UserPlaceholdersService;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;
import org.panda_lang.utilities.inject.DependencyInjection;
import org.panda_lang.utilities.inject.Injector;
import panda.std.Option;
import panda.utilities.ClassUtils;

public class FunnyGuilds extends JavaPlugin {

    private static FunnyGuilds plugin;
    private static FunnyGuildsLogger logger;

    private final File pluginConfigurationFile = new File(this.getDataFolder(), "config.yml");
    private final File tablistConfigurationFile = new File(this.getDataFolder(), "tablist.yml");
    private final File messageConfigurationFile = new File(this.getDataFolder(), "messages.yml");
    private final File pluginDataFolderFile = new File(this.getDataFolder(), "data");

    private FunnyGuildsVersion version;
    private FunnyCommands funnyCommands;

    private PluginConfiguration pluginConfiguration;
    private TablistConfiguration tablistConfiguration;
    private MessageConfiguration messageConfiguration;

    private ConcurrencyManager concurrencyManager;
    private DynamicListenerManager dynamicListenerManager;
    private HookManager hookManager;
    private UserManager userManager;
    private GuildManager guildManager;
    private UserRankManager userRankManager;
    private GuildRankManager guildRankManager;
    private RegionManager regionManager;
    private FunnyServer funnyServer;
    private IndividualPrefixManager individualPrefixManager;

    private GuildInvitationList guildInvitationList;
    private AllyInvitationList allyInvitationList;

    private BasicPlaceholdersService basicPlaceholdersService;
    private TimePlaceholdersService timePlaceholdersService;
    private UserPlaceholdersService userPlaceholdersService;
    private GuildPlaceholdersService guildPlaceholdersService;
    private RankPlaceholdersService rankPlaceholdersService;
    private TablistPlaceholdersService tablistPlaceholdersService;

    private NmsAccessor nmsAccessor;
    private GuildEntityHelper guildEntityHelper;

    private BossBarService bossBarService;

    private DataModel dataModel;
    private DataPersistenceHandler dataPersistenceHandler;
    private InvitationPersistenceHandler invitationPersistenceHandler;

    private Injector injector;

    private volatile BukkitTask guildValidationTask;
    private volatile BukkitTask tablistBroadcastTask;
    private volatile BukkitTask rankRecalculationTask;

    private boolean isDisabling;
    private boolean forceDisabling;

    @Override
    public void onLoad() {
        Reflections.prepareServerVersion();

        plugin = this;
        logger = new FunnyGuildsLogger.DefaultLogger(this);
        this.version = new FunnyGuildsVersion(this);
        this.funnyServer = new FunnyServer(this.getServer());

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

        if (!this.getDataFolder().exists()) {
            this.getDataFolder().mkdir();
        }

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

        this.nmsAccessor = this.prepareNmsAccessor();
        this.guildEntityHelper = new GuildEntityHelper(this.pluginConfiguration, this.nmsAccessor);

        DescriptionChanger descriptionChanger = new DescriptionChanger(super.getDescription());
        descriptionChanger.rename(pluginConfiguration.pluginName);

        this.concurrencyManager = new ConcurrencyManager(pluginConfiguration.concurrencyThreads);
        this.concurrencyManager.printStatus();

        this.dynamicListenerManager = new DynamicListenerManager(this);

        this.hookManager = new HookManager(plugin);
        this.hookManager.setupEarlyHooks();
        this.hookManager.earlyInit();
    }

    @Override
    public void onEnable() {
        if (this.forceDisabling) {
            return;
        }

        this.userManager = new UserManager();
        this.guildManager = new GuildManager(this.pluginConfiguration);
        this.userRankManager = new UserRankManager(this.pluginConfiguration);
        this.userRankManager.register(DefaultTops.defaultUserTops(this.pluginConfiguration, this.userManager));
        this.guildRankManager = new GuildRankManager(this.pluginConfiguration);
        this.guildRankManager.register(DefaultTops.defaultGuildTops(this.guildManager));
        this.regionManager = new RegionManager(this.pluginConfiguration);

        this.individualPrefixManager = new IndividualPrefixManager(this);

        this.guildInvitationList = new GuildInvitationList(this.userManager, this.guildManager);
        this.allyInvitationList = new AllyInvitationList(this.guildManager);

        this.basicPlaceholdersService = new BasicPlaceholdersService();
        this.basicPlaceholdersService.register(this, "simple", BasicPlaceholdersService.createSimplePlaceholders(this));

        this.timePlaceholdersService = new TimePlaceholdersService();
        this.timePlaceholdersService.register(this, "time", TimePlaceholdersService.createTimePlaceholders());

        this.userPlaceholdersService = new UserPlaceholdersService();
        this.userPlaceholdersService.register(this, "player", UserPlaceholdersService.createPlayerPlaceholders(this));
        this.userPlaceholdersService.register(this, "user", UserPlaceholdersService.createUserPlaceholders(this));

        this.guildPlaceholdersService = new GuildPlaceholdersService();
        this.guildPlaceholdersService.register(this, "simple", GuildPlaceholdersService.createSimplePlaceholders(this));
        this.guildPlaceholdersService.register(this, "guild", GuildPlaceholdersService.createGuildPlaceholders(this));
        this.guildPlaceholdersService.register(this, "allies_enemies", GuildPlaceholdersService.createAlliesEnemiesPlaceholders(this));

        this.rankPlaceholdersService = new RankPlaceholdersService(logger, this.pluginConfiguration, this.messageConfiguration, this.tablistConfiguration, this.userRankManager, this.guildRankManager);

        this.tablistPlaceholdersService = new TablistPlaceholdersService(basicPlaceholdersService, timePlaceholdersService, userPlaceholdersService, guildPlaceholdersService);

        this.bossBarService = new BossBarService();

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

        this.injector = DependencyInjection.createInjector(resources -> {
            resources.on(Server.class).assignInstance(this.getServer());
            resources.on(FunnyServer.class).assignInstance(this.getFunnyServer());
            resources.on(FunnyGuilds.class).assignInstance(this);
            resources.on(FunnyGuildsLogger.class).assignInstance(FunnyGuilds::getPluginLogger);
            resources.on(PluginConfiguration.class).assignInstance(this.pluginConfiguration);
            resources.on(MessageConfiguration.class).assignInstance(this.messageConfiguration);
            resources.on(TablistConfiguration.class).assignInstance(this.tablistConfiguration);
            resources.on(ConcurrencyManager.class).assignInstance(this.concurrencyManager);
            resources.on(UserManager.class).assignInstance(this.userManager);
            resources.on(GuildManager.class).assignInstance(this.guildManager);
            resources.on(UserRankManager.class).assignInstance(this.userRankManager);
            resources.on(GuildRankManager.class).assignInstance(this.guildRankManager);
            resources.on(RegionManager.class).assignInstance(this.regionManager);
            resources.on(IndividualPrefixManager.class).assignInstance(this.individualPrefixManager);
            resources.on(GuildInvitationList.class).assignInstance(this.guildInvitationList);
            resources.on(AllyInvitationList.class).assignInstance(this.allyInvitationList);
            resources.on(BasicPlaceholdersService.class).assignInstance(this.basicPlaceholdersService);
            resources.on(TimePlaceholdersService.class).assignInstance(this.timePlaceholdersService);
            resources.on(UserPlaceholdersService.class).assignInstance(this.userPlaceholdersService);
            resources.on(GuildPlaceholdersService.class).assignInstance(this.guildPlaceholdersService);
            resources.on(RankPlaceholdersService.class).assignInstance(this.rankPlaceholdersService);
            resources.on(NmsAccessor.class).assignInstance(this.nmsAccessor);
            resources.on(MessageAccessor.class).assignInstance(this.nmsAccessor.getMessageAccessor());
            resources.on(GuildEntityHelper.class).assignInstance(this.guildEntityHelper);
            resources.on(BossBarService.class).assignInstance(this.bossBarService);
            resources.on(DataModel.class).assignInstance(this.dataModel);
        });

        MetricsCollector collector = new MetricsCollector(this);
        collector.start();

        this.guildValidationTask = Bukkit.getScheduler().runTaskTimerAsynchronously(this, new GuildValidationHandler(this), 100L, 20L);
        this.tablistBroadcastTask = Bukkit.getScheduler().runTaskTimerAsynchronously(this, new TablistBroadcastHandler(this), 20L, this.tablistConfiguration.playerListUpdateInterval);
        this.rankRecalculationTask = Bukkit.getScheduler().runTaskTimerAsynchronously(this, new RankRecalculationTask(this), 20L, this.pluginConfiguration.rankingUpdateInterval);

        try {
            FunnyCommandsConfiguration commandsConfiguration = new FunnyCommandsConfiguration();
            this.funnyCommands = commandsConfiguration.createFunnyCommands(this);
        }
        catch (Exception exception) {
            logger.error("Could not register commands", exception);
            shutdown("Critical error has been encountered!");
            return;
        }

        try {
            PluginManager pluginManager = Bukkit.getPluginManager();
            ImmutableSet.Builder<Class<? extends Listener>> setBuilder = ImmutableSet.builder();

            setBuilder
                    .add(GuiActionHandler.class)
                    .add(EntityDamage.class)
                    .add(EntityInteract.class)
                    .add(PlayerChat.class)
                    .add(PlayerDeath.class)
                    .add(PlayerJoin.class)
                    .add(PlayerLogin.class)
                    .add(PlayerQuit.class)
                    .add(GuildHeartProtectionHandler.class)
                    .add(TntProtection.class);

            if (pluginConfiguration.regionsEnabled && pluginConfiguration.blockFlow) {
                setBuilder.add(BlockFlow.class);
            }

            if (ClassUtils.forName("org.bukkit.event.entity.EntityPlaceEvent").isPresent()) {
                setBuilder.add(EntityPlace.class);
            }
            else {
                logger.warning("Cannot register EntityPlaceEvent listener on this version of server");
            }

            for (Class<? extends Listener> listenerClass : setBuilder.build()) {
                pluginManager.registerEvents(this.injector.newInstanceWithFields(listenerClass), this);
            }

            this.dynamicListenerManager.registerDynamic(() -> pluginConfiguration.regionsEnabled,
                    this.injector.newInstanceWithFields(BlockBreak.class),
                    this.injector.newInstanceWithFields(BlockIgnite.class),
                    this.injector.newInstanceWithFields(BlockPlace.class),
                    this.injector.newInstanceWithFields(BucketAction.class),
                    this.injector.newInstanceWithFields(EntityExplode.class),
                    this.injector.newInstanceWithFields(HangingBreak.class),
                    this.injector.newInstanceWithFields(HangingPlace.class),
                    this.injector.newInstanceWithFields(PlayerCommand.class),
                    this.injector.newInstanceWithFields(PlayerInteract.class),
                    this.injector.newInstanceWithFields(EntityProtect.class)
            );

            this.dynamicListenerManager.registerDynamic(() -> pluginConfiguration.regionsEnabled && pluginConfiguration.eventMove, this.injector.newInstanceWithFields(PlayerMove.class));
            this.dynamicListenerManager.registerDynamic(() -> pluginConfiguration.regionsEnabled && pluginConfiguration.eventPhysics, this.injector.newInstanceWithFields(BlockPhysics.class));
            this.dynamicListenerManager.registerDynamic(() -> pluginConfiguration.regionsEnabled && pluginConfiguration.respawnInBase, this.injector.newInstanceWithFields(PlayerRespawn.class));
            this.dynamicListenerManager.registerDynamic(() -> pluginConfiguration.regionsEnabled && pluginConfiguration.eventTeleport, this.injector.newInstanceWithFields(PlayerTeleport.class));

            this.dynamicListenerManager.reloadAll();
        }
        catch (Throwable throwable) {
            logger.error("Could not register listeners", throwable);
            shutdown("Critical error has been encountered!");
            return;
        }

        this.handleReload();

        this.version.isNewAvailable(this.getServer().getConsoleSender(), true);
        this.hookManager.setupHooks();
        this.hookManager.init();

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
        this.guildEntityHelper.despawnGuildEntities(this.guildManager);

        this.guildValidationTask.cancel();
        this.tablistBroadcastTask.cancel();
        this.rankRecalculationTask.cancel();

        this.userManager.getUsers().forEach(user -> bossBarService.getBossBarProvider(user).removeNotification());

        this.dataModel.save(false);
        this.dataPersistenceHandler.stopHandler();

        this.invitationPersistenceHandler.saveInvitations();
        this.invitationPersistenceHandler.stopHandler();

        this.getServer().getScheduler().cancelTasks(this);
        this.getConcurrencyManager().awaitTermination(this.pluginConfiguration.pluginTaskTerminationTimeout);

        Database.getInstance().shutdown();

        plugin = null;
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
            final FunnyGuildsInboundChannelHandler inboundChannelHandler = nmsAccessor.getPacketAccessor().getOrInstallInboundChannelHandler(player);
            final FunnyGuildsOutboundChannelHandler outboundChannelHandler = nmsAccessor.getPacketAccessor().getOrInstallOutboundChannelHandler(player);

            Option<User> userOption = userManager.findByPlayer(player);
            if (userOption.isEmpty()) {
                continue;
            }
            User user = userOption.get();

            inboundChannelHandler.getPacketCallbacksRegistry().registerPacketCallback(new WarPacketCallbacks(plugin, user));
            outboundChannelHandler.getPacketSuppliersRegistry().registerPacketSupplier(new GuildEntitySupplier(this.guildEntityHelper));

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
                    this.tablistConfiguration.playerListAnimated, this.tablistConfiguration.pages,
                    this.tablistConfiguration.heads.textures,
                    this.tablistConfiguration.playerListPing,
                    this.tablistConfiguration.playerListFillCells,
                    this.pluginConfiguration.top.enableLegacyPlaceholders
            );

            cache.setPlayerList(individualPlayerList);
        }

        this.guildEntityHelper.spawnGuildEntities(this.guildManager);
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
        return this.userManager;
    }

    public GuildManager getGuildManager() {
        return this.guildManager;
    }

    public UserRankManager getUserRankManager() {
        return this.userRankManager;
    }

    public GuildRankManager getGuildRankManager() {
        return this.guildRankManager;
    }

    public RegionManager getRegionManager() {
        return this.regionManager;
    }

    public FunnyServer getFunnyServer() {
        return this.funnyServer;
    }

    public IndividualPrefixManager getIndividualPrefixManager() {
        return this.individualPrefixManager;
    }

    public GuildInvitationList getGuildInvitationList() {
        return this.guildInvitationList;
    }

    public AllyInvitationList getAllyInvitationList() {
        return this.allyInvitationList;
    }

    public BasicPlaceholdersService getBasicPlaceholdersService() {
        return this.basicPlaceholdersService;
    }

    public TimePlaceholdersService getTimePlaceholdersService() {
        return this.timePlaceholdersService;
    }

    public UserPlaceholdersService getUserPlaceholdersService() {
        return this.userPlaceholdersService;
    }

    public GuildPlaceholdersService getGuildPlaceholdersService() {
        return this.guildPlaceholdersService;
    }

    public RankPlaceholdersService getRankPlaceholdersService() {
        return this.rankPlaceholdersService;
    }

    public TablistPlaceholdersService getTablistPlaceholdersService() {
        return this.tablistPlaceholdersService;
    }

    public NmsAccessor getNmsAccessor() {
        return this.nmsAccessor;
    }

    public GuildEntityHelper getGuildEntityHelper() {
        return this.guildEntityHelper;
    }

    public BossBarService getBossBarService() {
        return bossBarService;
    }

    public Injector getInjector() {
        return this.injector;
    }

    public void reloadConfiguration() throws OkaeriException {
        this.pluginConfiguration.load();
        this.tablistConfiguration.load();
        this.messageConfiguration.load();
        this.hookManager.callConfigUpdated();
    }

    public static FunnyGuilds getInstance() {
        return plugin;
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
            case "v1_18_R1":
                return new V1_18R1NmsAccessor();
            case "v1_18_R2":
                return new V1_18R2NmsAccessor();
            default:
                throw new RuntimeException(String.format("Could not find matching NmsAccessor for currently running server version: %s",
                        Reflections.SERVER_VERSION));
        }
    }
}
