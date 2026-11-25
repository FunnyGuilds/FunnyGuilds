package net.dzikoysk.funnyguilds;

import com.google.common.collect.ImmutableSet;
import eu.okaeri.configs.exception.OkaeriConfigException;
import eu.okaeri.configs.exception.OkaeriException;
import java.io.File;
import java.lang.reflect.Method;
import java.util.Optional;
import java.util.Map;
import java.util.function.Consumer;
import net.dzikoysk.funnycommands.FunnyCommands;
import net.dzikoysk.funnyguilds.config.ConfigurationFactory;
import net.dzikoysk.funnyguilds.config.PluginConfiguration;
import net.dzikoysk.funnyguilds.config.message.MessageService;
import net.dzikoysk.funnyguilds.config.sections.ScoreboardConfiguration;
import net.dzikoysk.funnyguilds.config.sections.PanelConfiguration;
import net.dzikoysk.funnyguilds.config.sections.items.ItemsConfiguration;
import net.dzikoysk.funnyguilds.config.tablist.TablistConfiguration;
import net.dzikoysk.funnyguilds.damage.DamageManager;
import net.dzikoysk.funnyguilds.data.DataModel;
import net.dzikoysk.funnyguilds.data.DataModelLoader;
import net.dzikoysk.funnyguilds.data.DataPersistenceHandler;
import net.dzikoysk.funnyguilds.data.InvitationPersistenceHandler;
import net.dzikoysk.funnyguilds.data.database.Database;
import net.dzikoysk.funnyguilds.feature.base.GuildBaseService;
import net.dzikoysk.funnyguilds.feature.audit.GuildAuditService;
import net.dzikoysk.funnyguilds.feature.command.FunnyCommandsConfiguration;
import net.dzikoysk.funnyguilds.feature.command.ExtensionCommandRegistry;
import net.dzikoysk.funnyguilds.feature.diplomacy.GuildDiplomacyService;
import net.dzikoysk.funnyguilds.feature.purchase.GuildPurchaseService;
import net.dzikoysk.funnyguilds.feature.hooks.HookManager;
import net.dzikoysk.funnyguilds.feature.items.GuildItemRequirementChecker;
import net.dzikoysk.funnyguilds.feature.items.GuildItemSetService;
import net.dzikoysk.funnyguilds.feature.invitation.ally.AllyInvitationList;
import net.dzikoysk.funnyguilds.feature.invitation.guild.GuildInvitationList;
import net.dzikoysk.funnyguilds.feature.invitation.guild.GuildMembershipService;
import net.dzikoysk.funnyguilds.feature.panel.GuildRoleNameInput;
import net.dzikoysk.funnyguilds.feature.panel.PanelModule;
import net.dzikoysk.funnyguilds.feature.panel.PanelAccessAdapter;
import net.dzikoysk.funnyguilds.feature.items.gui.GuiItemBuilder;
import net.dzikoysk.funnyguilds.module.api.GuildPanelAccess;
import net.dzikoysk.funnyguilds.feature.placeholders.BasicPlaceholdersService;
import net.dzikoysk.funnyguilds.feature.placeholders.TimePlaceholdersService;
import net.dzikoysk.funnyguilds.feature.scoreboard.ScoreboardGlobalUpdateSyncTask;
import net.dzikoysk.funnyguilds.feature.scoreboard.ScoreboardService;
import net.dzikoysk.funnyguilds.feature.scoreboard.dummy.DummyManager;
import net.dzikoysk.funnyguilds.feature.scoreboard.nametag.IndividualNameTagManager;
import net.dzikoysk.funnyguilds.feature.tablist.TablistBroadcastHandler;
import net.dzikoysk.funnyguilds.feature.tablist.TablistPlaceholdersService;
import net.dzikoysk.funnyguilds.feature.tablist.TablistRenderer;
import net.dzikoysk.funnyguilds.feature.validity.GuildValidationHandler;
import net.dzikoysk.funnyguilds.feature.war.WarPacketCallbacks;
import net.dzikoysk.funnyguilds.guild.GuildManager;
import net.dzikoysk.funnyguilds.guild.GuildRankManager;
import net.dzikoysk.funnyguilds.guild.RegionManager;
import net.dzikoysk.funnyguilds.guild.permission.GuildPermissionChecker;
import net.dzikoysk.funnyguilds.guild.permission.GuildRoleService;
import net.dzikoysk.funnyguilds.guild.placeholders.GuildPlaceholdersService;
import net.dzikoysk.funnyguilds.listener.BlockFlow;
import net.dzikoysk.funnyguilds.listener.EntityDamage;
import net.dzikoysk.funnyguilds.listener.EntityInteract;
import net.dzikoysk.funnyguilds.listener.PistonUse;
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
import net.dzikoysk.funnyguilds.nms.Reflections;
import net.dzikoysk.funnyguilds.nms.api.NmsAccessor;
import net.dzikoysk.funnyguilds.nms.api.packet.FunnyGuildsInboundChannelHandler;
import net.dzikoysk.funnyguilds.nms.api.packet.FunnyGuildsOutboundChannelHandler;
import net.dzikoysk.funnyguilds.nms.heart.GuildEntityHelper;
import net.dzikoysk.funnyguilds.nms.heart.GuildEntitySupplier;
import net.dzikoysk.funnyguilds.rank.DefaultTops;
import net.dzikoysk.funnyguilds.rank.RankRecalculationTask;
import net.dzikoysk.funnyguilds.rank.RankSystem;
import net.dzikoysk.funnyguilds.rank.placeholders.RankPlaceholdersService;
import net.dzikoysk.funnyguilds.shared.ExceptionUtils;
import net.dzikoysk.funnyguilds.shared.FunnyIOUtils;
import net.dzikoysk.funnyguilds.shared.FunnyTask;
import net.dzikoysk.funnyguilds.shared.adventure.MiniLegacyHelper;
import net.dzikoysk.funnyguilds.shared.bukkit.FunnyServer;
import net.dzikoysk.funnyguilds.shared.bukkit.NmsUtils;
import net.dzikoysk.funnyguilds.telemetry.metrics.MetricsCollector;
import net.dzikoysk.funnyguilds.user.User;
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
import panda.std.Result;
import net.dzikoysk.funnyguilds.shared.Optionals;
import static java.lang.String.format;

public class FunnyGuilds extends JavaPlugin {

    private static FunnyGuilds plugin;
    private static FunnyGuildsLogger logger;

    private final File pluginConfigurationFile = new File(this.getDataFolder(), "config.yml");
    private final File itemsConfigurationFile = new File(this.getDataFolder(), "items.yml");
    private final GuildBaseService guildBaseService = new GuildBaseService(this);
    private final GuildDiplomacyService guildDiplomacyService = new GuildDiplomacyService(this);
    private final GuildPurchaseService guildPurchaseService = new GuildPurchaseService(this);
    private final GuildRoleService guildRoleService = new GuildRoleService();
    private final PanelModule panelModule = new PanelModule(this, this::findPanelAccess, this::sendPanelDisabled,
            MiniLegacyHelper::miniMessage, GuiItemBuilder::buildSimple);
    private final GuildAuditService guildAuditService = new GuildAuditService(this);
    private final GuildMembershipService guildMembershipService = new GuildMembershipService(this);
    private final File tablistConfigurationFile = new File(this.getDataFolder(), "tablist.yml");
    private final File pluginLanguageFolderFile = new File(this.getDataFolder(), "lang");
    private final File pluginDataFolderFile = new File(this.getDataFolder(), "data");

    private FunnyGuildsVersion version;
    private FunnyCommands funnyCommands;

    private PluginConfiguration pluginConfiguration;
    private ItemsConfiguration itemsConfiguration;
    private TablistConfiguration tablistConfiguration;

    private MessageService messageService;

    private GuildItemSetService guildItemSetService;
    private GuildItemRequirementChecker guildItemRequirementChecker;

    private DynamicListenerManager dynamicListenerManager;
    private HookManager hookManager;
    private UserManager userManager;
    private GuildManager guildManager;
    private UserRankManager userRankManager;
    private GuildRankManager guildRankManager;
    private DamageManager damageManager;
    private RankSystem rankSystem;
    private RegionManager regionManager;
    private FunnyServer funnyServer;
    private GuildPermissionChecker guildPermissionChecker;

    private Optional<IndividualNameTagManager> individualNameTagManager = Optional.empty();
    private Optional<DummyManager> dummyManager = Optional.empty();

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
    private Optional<TablistRenderer> tablistRenderer = Optional.empty();

    private Optional<Database> database = Optional.empty();
    private DataModel dataModel;
    private DataPersistenceHandler dataPersistenceHandler;
    private InvitationPersistenceHandler invitationPersistenceHandler;

    private Injector injector;

    private volatile Optional<BukkitTask> guildValidationTask = Optional.empty();
    private volatile Optional<BukkitTask> tablistBroadcastTask = Optional.empty();
    private volatile Optional<BukkitTask> rankRecalculationTask = Optional.empty();

    private volatile Optional<BukkitTask> nameTagUpdateTask = Optional.empty();
    private volatile Optional<BukkitTask> dummyUpdateTask = Optional.empty();
    private volatile Optional<BukkitTask> scoreboardQueueUpdateTask = Optional.empty();

    private boolean isDisabling;
    private boolean forceDisabling;

    @Override
    public void onLoad() {
        Reflections.prepareServerVersion();
        MiniLegacyHelper.miniMessage(); // Invoke class to call static initializer

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

            this.shutdown("Spigot required for service not detected!");
            return;
        }

        Result<File, String> createResult = FunnyIOUtils.createFile(this.getDataFolder(), true);
        if (createResult.isErr()) {
            this.shutdown(createResult.getError());
            return;
        }

        try {
            this.pluginConfiguration = ConfigurationFactory.createPluginConfiguration(this.pluginConfigurationFile);
            this.itemsConfiguration = ConfigurationFactory.createItemsConfiguration(this.itemsConfigurationFile);
            this.panelModule.reload();
            this.tablistConfiguration = ConfigurationFactory.createTablistConfiguration(this.tablistConfigurationFile);
        }
        catch (Exception exception) {
            OkaeriConfigException configException = ExceptionUtils.findCause(exception, OkaeriConfigException.class, 5);
            if ((configException != null) && configException.isMessageSufficient()) {
                logger.error("Could not load plugin configuration");
                for (String line : configException.getMessage().split("\n")) {
                    logger.error(line);
                }
            }
            else {
                logger.error("Could not load plugin configuration", exception);
            }
            this.shutdown("Critical error has been encountered!");
            return;
        }

        try {
            this.nmsAccessor = prepareNmsAccessor();
        }
        catch (Throwable th) {
            String currentVersion = "Unknown";
            try {
                Method getMinecraftVersion = Server.class.getMethod("getMinecraftVersion");
                currentVersion = (String) getMinecraftVersion.invoke(this.getServer());
            } catch (Throwable ignored) {
                try {
                    currentVersion = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
                } catch (Throwable ignored2) { }
            }

            logger.error(format("Version '%s' is not supported yet, please reach us on issue tracker or on Discord that can be found here: https://github.com/FunnyGuilds/FunnyGuilds", currentVersion), th);
            this.shutdown("Critical error has been encountered!");
            return;
        }
        this.guildEntityHelper = new GuildEntityHelper(this.pluginConfiguration, this.nmsAccessor);

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

        try {
            Result<File, String> createResult = FunnyIOUtils.createFile(this.pluginLanguageFolderFile, true);
            if (createResult.isErr()) {
                this.shutdown(createResult.getError());
                return;
            }
            this.messageService = MessageService.prepareMessageService(this, this.pluginLanguageFolderFile);
        }
        catch (Exception exception) {
            logger.error("Could not initialize message service", exception);
            this.shutdown("Critical error has been encountered!");
            return;
        }
        this.userManager = new UserManager(this.pluginConfiguration);

        this.guildItemSetService = new GuildItemSetService(this.itemsConfiguration);
        this.guildItemRequirementChecker = new GuildItemRequirementChecker();
        this.guildManager = new GuildManager(this.pluginConfiguration);
        this.userRankManager = new UserRankManager(this.pluginConfiguration);
        this.userRankManager.register(DefaultTops.defaultUserTops(this.pluginConfiguration, this.userManager));
        this.guildRankManager = new GuildRankManager(this.pluginConfiguration);
        this.guildRankManager.register(DefaultTops.defaultGuildTops(this.guildManager));
        this.damageManager = new DamageManager();
        this.rankSystem = RankSystem.create(this.pluginConfiguration);
        this.regionManager = new RegionManager(this.pluginConfiguration);
        this.guildPermissionChecker = GuildPermissionChecker.create(this);

        this.prepareScoreboardServices();

        this.guildInvitationList = new GuildInvitationList(this.userManager, this.guildManager);
        this.allyInvitationList = new AllyInvitationList(this.guildManager);

        this.basicPlaceholdersService = new BasicPlaceholdersService(this.messageService);
        this.basicPlaceholdersService.register(this, "simple", BasicPlaceholdersService.createSimplePlaceholders(this));

        this.timePlaceholdersService = new TimePlaceholdersService(this.messageService);
        this.timePlaceholdersService.register(this, "time", TimePlaceholdersService.createTimePlaceholders());

        this.userPlaceholdersService = new UserPlaceholdersService(this.messageService);
        this.userPlaceholdersService.register(this, "player", UserPlaceholdersService.createPlayerPlaceholders(this));
        this.userPlaceholdersService.register(this, "user", UserPlaceholdersService.createUserPlaceholders(this));

        this.guildPlaceholdersService = new GuildPlaceholdersService(this.messageService);
        this.guildPlaceholdersService.register(this, "simple", GuildPlaceholdersService.createSimplePlaceholders(this));
        this.guildPlaceholdersService.register(this, "guild", GuildPlaceholdersService.createGuildPlaceholders(this));
        this.guildPlaceholdersService.register(this, "members", GuildPlaceholdersService.createMemberPlaceholders(this));

        this.rankPlaceholdersService = new RankPlaceholdersService(
                this.messageService,
                this.pluginConfiguration,
                this.messageService,
                this.userRankManager,
                this.guildRankManager
        );
        this.tablistPlaceholdersService = new TablistPlaceholdersService(
                this.messageService,
                this.basicPlaceholdersService,
                this.timePlaceholdersService,
                this.userPlaceholdersService,
                this.guildPlaceholdersService
        );

        if (pluginConfiguration.dataModel.isSQL()) {
            try {
                this.database = Optional.of(new Database());
            } catch (Exception ex) {
                logger.error("Could not create data from database", ex);
                this.shutdown("Critical error has been encountered!");
                return;
            }
        }

        Result<DataModel, String> dataLoadResult = new DataModelLoader(logger).load(() -> {
            // Serializers can access the model through the plugin while it is loading.
            this.dataModel = DataModel.create(this, this.pluginConfiguration.dataModel);
            return this.dataModel;
        });
        if (dataLoadResult.isErr()) {
            this.shutdown(dataLoadResult.getError());
            return;
        }

        this.dataPersistenceHandler = new DataPersistenceHandler(this);
        this.dataPersistenceHandler.startHandler();

        this.invitationPersistenceHandler = new InvitationPersistenceHandler(this);
        this.invitationPersistenceHandler.loadInvitations();
        this.invitationPersistenceHandler.startHandler();

        this.injector = DependencyInjection.createInjector(resources -> {
            resources.on(Server.class).assignInstance(this.getServer());
            resources.on(FunnyServer.class).assignInstance(this.funnyServer);
            resources.on(FunnyGuilds.class).assignInstance(this);
            resources.on(FunnyGuildsLogger.class).assignInstance(FunnyGuilds::getPluginLogger);
            resources.on(PluginConfiguration.class).assignInstance(this.pluginConfiguration);
            resources.on(ItemsConfiguration.class).assignInstance(this.itemsConfiguration);
            resources.on(TablistConfiguration.class).assignInstance(this.tablistConfiguration);
            resources.on(MessageService.class).assignInstance(this.messageService);
            resources.on(UserManager.class).assignInstance(this.userManager);
            resources.on(GuildManager.class).assignInstance(this.guildManager);
            resources.on(UserRankManager.class).assignInstance(this.userRankManager);
            resources.on(GuildRankManager.class).assignInstance(this.guildRankManager);
            resources.on(RegionManager.class).assignInstance(this.regionManager);
            resources.on(GuildPermissionChecker.class).assignInstance(this.guildPermissionChecker);
            resources.on(DamageManager.class).assignInstance(this.damageManager);
            resources.on(RankSystem.class).assignInstance(this.rankSystem);
            resources.on(GuildInvitationList.class).assignInstance(this.guildInvitationList);
            resources.on(AllyInvitationList.class).assignInstance(this.allyInvitationList);
            resources.on(BasicPlaceholdersService.class).assignInstance(this.basicPlaceholdersService);
            resources.on(TimePlaceholdersService.class).assignInstance(this.timePlaceholdersService);
            resources.on(UserPlaceholdersService.class).assignInstance(this.userPlaceholdersService);
            resources.on(GuildPlaceholdersService.class).assignInstance(this.guildPlaceholdersService);
            resources.on(RankPlaceholdersService.class).assignInstance(this.rankPlaceholdersService);
            resources.on(NmsAccessor.class).assignInstance(this.nmsAccessor);
            resources.on(GuildEntityHelper.class).assignInstance(this.guildEntityHelper);
            resources.on(GuildItemSetService.class).assignInstance(this.guildItemSetService);
            resources.on(GuildItemRequirementChecker.class).assignInstance(this.guildItemRequirementChecker);
            resources.on(DataModel.class).assignInstance(this.dataModel);
        });

        MetricsCollector collector = new MetricsCollector(this);
        collector.start();

        this.guildValidationTask = Optional.of(Bukkit.getScheduler().runTaskTimerAsynchronously(this, new GuildValidationHandler(this), 100L, 20L));
        this.rankRecalculationTask = Optional.of(Bukkit.getScheduler().runTaskTimerAsynchronously(this, new RankRecalculationTask(this), 20L, this.pluginConfiguration.rankingUpdateInterval));

        try {
            ExtensionCommandRegistry extensionCommands = new ExtensionCommandRegistry();
            this.panelModule.registerCommands(extensionCommands);
            this.funnyCommands = FunnyCommandsConfiguration.createFunnyCommands(this, extensionCommands,
                    Map.of(PanelModule.COMMAND_ID, this.pluginConfiguration.commands.panel));
        }
        catch (Exception exception) {
            logger.error("Could not register commands", exception);
            this.shutdown("Critical error has been encountered!");
            return;
        }

        try {
            PluginManager pluginManager = Bukkit.getPluginManager();
            ImmutableSet.Builder<Class<? extends Listener>> setBuilder = ImmutableSet.builder();

            setBuilder
                    .add(EntityDamage.class)
                    .add(EntityInteract.class)
                    .add(EntityPlace.class)
                    .add(PlayerChat.class) 
                    .add(PlayerDeath.class)
                    .add(PlayerJoin.class)
                    .add(PlayerLogin.class)
                    .add(PlayerQuit.class)
                    .add(GuildHeartProtectionHandler.class)
                    .add(TntProtection.class);

            if (this.pluginConfiguration.regionsEnabled && (this.pluginConfiguration.blockFlow || !this.pluginConfiguration.blockFlowOnRegion.isEmpty())) {
                setBuilder.add(BlockFlow.class);
            }

            for (Class<? extends Listener> listenerClass : setBuilder.build()) {
                pluginManager.registerEvents(this.injector.newInstanceWithFields(listenerClass), this);
            }
            this.panelModule.registerListeners();
            pluginManager.registerEvents(this.guildAuditService, this);
            this.getServer().getScheduler().runTaskTimer(this, this.guildAuditService::pruneExpiredEntries, 6000L, 6000L);

            this.dynamicListenerManager.registerDynamic(() -> this.pluginConfiguration.regionsEnabled,
                    this.injector.newInstanceWithFields(BlockBreak.class),
                    this.injector.newInstanceWithFields(BlockIgnite.class),
                    this.injector.newInstanceWithFields(BlockPlace.class),
                    this.injector.newInstanceWithFields(PistonUse.class),
                    this.injector.newInstanceWithFields(BucketAction.class),
                    this.injector.newInstanceWithFields(EntityExplode.class),
                    this.injector.newInstanceWithFields(HangingBreak.class),
                    this.injector.newInstanceWithFields(HangingPlace.class),
                    this.injector.newInstanceWithFields(PlayerCommand.class),
                    this.injector.newInstanceWithFields(PlayerInteract.class),
                    this.injector.newInstanceWithFields(EntityProtect.class)
            );

            this.dynamicListenerManager.registerDynamic(
                    () -> this.pluginConfiguration.regionsEnabled && this.pluginConfiguration.eventMove,
                    this.injector.newInstanceWithFields(PlayerMove.class)
            );
            this.dynamicListenerManager.registerDynamic(
                    () -> this.pluginConfiguration.regionsEnabled && this.pluginConfiguration.eventPhysics,
                    this.injector.newInstanceWithFields(BlockPhysics.class)
            );
            this.dynamicListenerManager.registerDynamic(
                    () -> this.pluginConfiguration.regionsEnabled && this.pluginConfiguration.respawnInBase,
                    this.injector.newInstanceWithFields(PlayerRespawn.class)
            );
            this.dynamicListenerManager.registerDynamic(
                    () -> this.pluginConfiguration.regionsEnabled && this.pluginConfiguration.eventTeleport,
                    this.injector.newInstanceWithFields(PlayerTeleport.class)
            );

            this.dynamicListenerManager.reloadAll();
        }
        catch (Throwable throwable) {
            logger.error("Could not register listeners", throwable);
            this.shutdown("Critical error has been encountered!");
            return;
        }

        this.handleReload();

        this.version.isNewAvailable(this.getServer().getConsoleSender(), true);
        this.hookManager.setupHooks();
        this.hookManager.init();

        if (NmsUtils.getReloadCount() > 0) {
            this.messageService.getMessage(config -> config.reloadWarn)
                    .all()
                    .permission("funnyguilds.admin")
                    .send();
        }

        logger.info("~ Created by FunnyGuilds Team ~");
    }

    public void reloadTablistRendering() {
        this.tablistRenderer = Optional.empty();
        this.tablistBroadcastTask.ifPresent(task -> Bukkit.getScheduler().cancelTask(task.getTaskId()));
        this.tablistBroadcastTask = Optional.empty();

        this.tablistRenderer = Optionals.when(
                this.tablistConfiguration.enabled,
                () -> new TablistRenderer(
                        this.nmsAccessor.getPlayerListAccessor(),
                        this.userManager,
                        this.tablistConfiguration.cells,
                        this.tablistConfiguration.header,
                        this.tablistConfiguration.footer,
                        this.tablistConfiguration.animated,
                        this.tablistConfiguration.pages,
                        this.tablistConfiguration.heads.textures,
                        this.tablistConfiguration.cellsPing,
                        this.tablistConfiguration.fillCells
                )
        );
        this.tablistRenderer.ifPresent(renderer -> this.tablistBroadcastTask = Optional.of(Bukkit.getScheduler()
                .runTaskTimerAsynchronously(
                        this,
                        new TablistBroadcastHandler(renderer),
                        20L,
                        this.tablistConfiguration.updateInterval
                )));
    }

    @Override
    public void onDisable() {
        if (this.forceDisabling) {
            return;
        }

        this.isDisabling = true;
        this.panelModule.nameInput().clear();

        this.funnyCommands.dispose();
        this.dynamicListenerManager.unregisterAll();
        this.guildEntityHelper.despawnGuildEntities(this.guildManager);

        this.guildValidationTask.ifPresent(BukkitTask::cancel);
        this.tablistBroadcastTask.ifPresent(BukkitTask::cancel);
        this.rankRecalculationTask.ifPresent(BukkitTask::cancel);

        this.dataPersistenceHandler.shutdown();

        this.invitationPersistenceHandler.saveInvitations();
        this.invitationPersistenceHandler.stopHandler();

        this.getServer().getScheduler().cancelTasks(this);
        plugin = null;
    }

    public void shutdown(String content) {
        if (this.isDisabling || this.forceDisabling) {
            return;
        }

        this.forceDisabling = true;
        logger.warning("The FunnyGuilds is going to shut down! " + content);
        this.getServer().getPluginManager().disablePlugin(this);
    }

    private void handleReload() {
        for (Player player : this.getServer().getOnlinePlayers()) {
            Optional<User> userOption = this.userManager.findByPlayer(player);
            if (userOption.isEmpty()) {
                continue;
            }
            User user = userOption.orElseThrow();

            FunnyGuildsInboundChannelHandler inboundChannelHandler = this.nmsAccessor.getPacketAccessor().getOrInstallInboundChannelHandler(player);
            inboundChannelHandler.getPacketCallbacksRegistry().registerPacketCallback(new WarPacketCallbacks(plugin, user));

            FunnyGuildsOutboundChannelHandler outboundChannelHandler = this.nmsAccessor.getPacketAccessor().getOrInstallOutboundChannelHandler(player);
            outboundChannelHandler.getPacketSuppliersRegistry().setOwner(player);
            outboundChannelHandler.getPacketSuppliersRegistry().registerPacketSupplier(new GuildEntitySupplier(this.guildEntityHelper));
        }
        
        this.reloadTablistRendering();
        this.getIndividualNameTagManager().map(ScoreboardGlobalUpdateSyncTask::new).ifPresent(this::scheduleFunnyTasks);
        this.getDummyManager().map(ScoreboardGlobalUpdateSyncTask::new).ifPresent(this::scheduleFunnyTasks);

        this.guildEntityHelper.spawnGuildEntities(this.guildManager);
    }

    public void scheduleFunnyTasks(FunnyTask... tasks) {
        for (FunnyTask task : tasks) {
            switch (task.getType()) {
                case SYNC:
                    Bukkit.getScheduler().runTask(this, task);
                    break;
                case ASYNC:
                    Bukkit.getScheduler().runTaskAsynchronously(this, task);
                    break;
            }
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

    public Optional<Database> getDatabase() {
        return this.database;
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

    public ItemsConfiguration getItemsConfiguration() {
        return this.itemsConfiguration;
    }

    public PanelModule getPanelModule() {
        return this.panelModule;
    }

    private Optional<GuildPanelAccess> findPanelAccess(Player player) {
        Optional<GuildPanelAccess> session = this.userManager.findByPlayer(player)
                .flatMap(user -> user.getGuild().map(guild -> new PanelAccessAdapter(this, player, user, guild)));
        if (session.isEmpty()) {
            this.messageService.getMessage(config -> config.generalHasNoGuild).receiver(player).send();
        }
        return session;
    }

    private void sendPanelDisabled(Player player) {
        this.messageService.getMessage(config -> config.panelDisabled).receiver(player).send();
    }

    public PanelConfiguration getPanelConfiguration() {
        return this.panelModule.configuration();
    }

    public GuildBaseService getGuildBaseService() {
        return this.guildBaseService;
    }

    public GuildDiplomacyService getGuildDiplomacyService() {
        return this.guildDiplomacyService;
    }

    public GuildPurchaseService getGuildPurchaseService() {
        return this.guildPurchaseService;
    }

    public GuildRoleService getGuildRoleService() {
        return this.guildRoleService;
    }

    public GuildRoleNameInput getGuildRoleNameInput() {
        return this.panelModule.nameInput();
    }

    public GuildAuditService getGuildAuditService() {
        return this.guildAuditService;
    }

    public GuildItemSetService getGuildItemSetService() {
        return this.guildItemSetService;
    }

    public GuildItemRequirementChecker getGuildItemRequirementChecker() {
        return this.guildItemRequirementChecker;
    }

    public MessageService getMessageService() {
        return this.messageService;
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

    public DamageManager getDamageManager() {
        return this.damageManager;
    }

    public RegionManager getRegionManager() {
        return this.regionManager;
    }

    public FunnyServer getFunnyServer() {
        return this.funnyServer;
    }
    
    public GuildPermissionChecker getGuildPermissionChecker() {
        return this.guildPermissionChecker;
    }

    public Optional<IndividualNameTagManager> getIndividualNameTagManager() {
        return this.individualNameTagManager;
    }

    public Optional<DummyManager> getDummyManager() {
        return this.dummyManager;
    }

    public Optional<TablistRenderer> getTablistRenderer() {
        return this.tablistRenderer;
    }

    public GuildInvitationList getGuildInvitationList() {
        return this.guildInvitationList;
    }

    public GuildMembershipService getGuildMembershipService() {
        return this.guildMembershipService;
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

    public Injector getInjector() {
        return this.injector;
    }

    public void reloadConfiguration() throws OkaeriException {
        this.reloadConfiguration(error -> {});
    }

    public void reloadConfiguration(Consumer<String> reportItemsError) throws OkaeriException {
        this.pluginConfiguration.load();
        try {
            this.itemsConfiguration.load();
        }
        catch (OkaeriException | IllegalArgumentException exception) {
            getPluginLogger().warning("Keeping the previous items.yml settings. " + exception.getMessage());
            reportItemsError.accept(exception.getMessage());
        }
        this.tablistConfiguration.load();
        this.panelModule.reload();
        this.messageService.reload();
        this.hookManager.callConfigUpdated();
        this.prepareScoreboardServices();
    }

    private void prepareScoreboardServices() {
        this.nameTagUpdateTask.ifPresent(BukkitTask::cancel);
        this.dummyUpdateTask.ifPresent(BukkitTask::cancel);
        this.scoreboardQueueUpdateTask.ifPresent(BukkitTask::cancel);

        ScoreboardConfiguration scoreboardConfig = this.pluginConfiguration.scoreboard;
        if (!scoreboardConfig.enabled) {
            return;
        }
        ScoreboardService scoreboardService = new ScoreboardService(this.pluginConfiguration);

        this.individualNameTagManager = Optionals.when(
                scoreboardConfig.nametag.enabled,
                () -> new IndividualNameTagManager(this.pluginConfiguration, this.userManager, this.guildPermissionChecker, scoreboardService)
        );
        this.nameTagUpdateTask = this.individualNameTagManager.map(manager -> Bukkit.getScheduler().runTaskTimer(
                plugin,
                () -> manager.updatePlayers(false),
                100,
                scoreboardConfig.nametag.updateRate.getSeconds() * 20L
        ));

        this.dummyManager = Optionals.when(
                scoreboardConfig.dummy.enabled,
                () -> new DummyManager(this.pluginConfiguration, this.userManager, scoreboardService)
        );
        this.dummyUpdateTask = this.dummyManager.map(manager -> Bukkit.getScheduler().runTaskTimer(
                plugin,
                () -> manager.updatePlayers(false),
                100,
                scoreboardConfig.dummy.updateRate.getSeconds() * 20L
        ));

        this.scoreboardQueueUpdateTask = Optionals.when(
                this.individualNameTagManager.isPresent() || this.dummyManager.isPresent(),
                () -> Bukkit.getScheduler().runTaskTimer(
                        plugin,
                        () -> {
                            for (int i = 0; i < scoreboardConfig.queueConfiguration.maxUpdatesInTick; i++) {
                                boolean nameTagUpdated = this.individualNameTagManager.filter(IndividualNameTagManager::popAndUpdate).isPresent();
                                boolean dummyUpdated = this.dummyManager.filter(DummyManager::popAndUpdate).isPresent();
                                if (!nameTagUpdated && !dummyUpdated) {
                                    break;
                                }
                            }
                        },
                        100,
                        scoreboardConfig.queueConfiguration.updateRate
                )
        );
    }

    public static FunnyGuilds getInstance() {
        return plugin;
    }

    public static FunnyGuildsLogger getPluginLogger() {
        return logger;
    }

    private static NmsAccessor prepareNmsAccessor() throws IllegalStateException {
        return NmsAccessor.instance();
    }

}
