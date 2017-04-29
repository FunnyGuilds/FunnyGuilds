/*
 *     NovaGuilds - Bukkit plugin
 *     Copyright (C) 2017 Marcin (CTRL) Wieczorek
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>
 */

package co.marcin.novaguilds;

import co.marcin.novaguilds.api.NovaGuildsAPI;
import co.marcin.novaguilds.api.basic.NovaPlayer;
import co.marcin.novaguilds.api.basic.TabList;
import co.marcin.novaguilds.api.event.PlayerInteractEntityEvent;
import co.marcin.novaguilds.api.manager.ErrorManager;
import co.marcin.novaguilds.api.storage.Storage;
import co.marcin.novaguilds.api.util.SignGUI;
import co.marcin.novaguilds.api.util.packet.PacketExtension;
import co.marcin.novaguilds.api.util.reflect.FieldAccessor;
import co.marcin.novaguilds.enums.Config;
import co.marcin.novaguilds.enums.Dependency;
import co.marcin.novaguilds.enums.EntityUseAction;
import co.marcin.novaguilds.exception.FatalNovaGuildsException;
import co.marcin.novaguilds.exception.StorageConnectionFailedException;
import co.marcin.novaguilds.impl.storage.StorageConnector;
import co.marcin.novaguilds.impl.util.AbstractListener;
import co.marcin.novaguilds.impl.util.ScoreboardStatsHook;
import co.marcin.novaguilds.impl.util.bossbar.BossBarUtils;
import co.marcin.novaguilds.impl.util.logging.WrappedLogger;
import co.marcin.novaguilds.listener.VanishListener;
import co.marcin.novaguilds.listener.VaultListener;
import co.marcin.novaguilds.manager.CommandManager;
import co.marcin.novaguilds.manager.ConfigManager;
import co.marcin.novaguilds.manager.DependencyManager;
import co.marcin.novaguilds.manager.DynmapManager;
import co.marcin.novaguilds.manager.ErrorManagerImpl;
import co.marcin.novaguilds.manager.GroupManager;
import co.marcin.novaguilds.manager.GuildManager;
import co.marcin.novaguilds.manager.HologramManager;
import co.marcin.novaguilds.manager.ListenerManager;
import co.marcin.novaguilds.manager.MessageManager;
import co.marcin.novaguilds.manager.PlayerManager;
import co.marcin.novaguilds.manager.RankManager;
import co.marcin.novaguilds.manager.RegionManager;
import co.marcin.novaguilds.manager.TaskManager;
import co.marcin.novaguilds.util.CompatibilityUtils;
import co.marcin.novaguilds.util.EnchantmentGlow;
import co.marcin.novaguilds.util.LoggerUtils;
import co.marcin.novaguilds.util.TabUtils;
import co.marcin.novaguilds.util.TagUtils;
import co.marcin.novaguilds.util.VersionUtils;
import co.marcin.novaguilds.util.reflect.Reflections;
import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import org.bukkit.Bukkit;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.plugin.PluginLogger;
import org.bukkit.plugin.java.JavaPlugin;
import org.mcstats.Metrics;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class NovaGuilds extends JavaPlugin implements NovaGuildsAPI {
	/*
	 * Dioricie nasz, któryś jest w Javie, święć się bugi Twoje, przyjdź ficzery Twoje,
	 * bądź kod Twój jako w gicie tak i w mavenie, stacktrace naszego powszedniego
	 * daj nam dzisiaj, i daj nam buildy Twoje, jako i my commity dajemy,
	 * i nie wódź nas na wycieki pamięci, ale daj nam Bugi.
	 * Escape. ~Bukkit.PL
	 */

	private static NovaGuilds instance;

	private final DependencyManager dependencyManager;
	private final ListenerManager   listenerManager;
	private final HologramManager   hologramManager;
	private final CommandManager    commandManager;
	private final MessageManager    messageManager;
	private final RegionManager     regionManager;
	private final PlayerManager     playerManager;
	private final ConfigManager     configManager;
	private final DynmapManager     dynmapManager;
	private final ErrorManager      errorManager;
	private final GuildManager      guildManager;
	private final GroupManager      groupManager;
	private final RankManager       rankManager;
	private final TaskManager       taskManager;

	private PacketExtension packetExtension;
	private Storage storage;
	private SignGUI signGUI;
	private final Map<ConfigManager.ServerVersion, Constructor<? extends TabList>> tabListConstructorMap = new HashMap<>();

	/**
	 * The constructor
	 */
	public NovaGuilds() {
		instance = this;

		dependencyManager = new DependencyManager();
		hologramManager   = new HologramManager();
		listenerManager   = new ListenerManager();
		messageManager    = new MessageManager();
		commandManager    = new CommandManager();
		regionManager     = new RegionManager();
		playerManager     = new PlayerManager();
		configManager     = new ConfigManager();
		dynmapManager     = new DynmapManager();
		errorManager      = new ErrorManagerImpl();
		guildManager      = new GuildManager();
		groupManager      = new GroupManager();
		rankManager       = new RankManager();
		taskManager       = new TaskManager();
	}

	@Override
	public void onLoad() {
		try {
			getConfigManager().reload();
			getDependencyManager().setUp();
		}
		catch(Exception e) {
			LoggerUtils.exception(e);
		}
	}

	@Override
	public void onEnable() {
		try {
			if(FatalNovaGuildsException.fatal) {
				throw new FatalNovaGuildsException("The plugin has crashed");
			}

			//managers
			getDependencyManager().setupEconomy();
			getMessageManager().load();
			getCommandManager().setUp();
			getGroupManager().load();
			getListenerManager().registerListeners();
			getDynmapManager().init();

			//Version check
			new Thread() {
				@Override
				public void run() {
					VersionUtils.checkVersion();
				}
			}.start();

			//Setups the wrapped logger
			setupWrappedLogger();

			setUpStorage();

			//Data loading
			getGuildManager().load();
			LoggerUtils.info("Guilds data loaded");
			getRegionManager().load();
			LoggerUtils.info("Regions data loaded");
			getRankManager().loadDefaultRanks();
			getPlayerManager().load();
			LoggerUtils.info("Players data loaded");

			LoggerUtils.info("Post checks running");
			getGuildManager().postCheck();

			getRankManager().load();
			LoggerUtils.info("Ranks data loaded");

			//HologramManager
			if(Config.HOLOGRAPHICDISPLAYS_ENABLED.getBoolean()) {
				getHologramManager().load();
			}

			ConfigManager.ServerVersion serverVersion = ConfigManager.getServerVersion();
			if(Config.ADVANCEDENTITYUSE.getBoolean() &&
					(serverVersion == ConfigManager.ServerVersion.MINECRAFT_1_7_R2
							|| serverVersion == ConfigManager.ServerVersion.MINECRAFT_1_7_R3
							|| serverVersion == ConfigManager.ServerVersion.MINECRAFT_1_7_R4)) {
				new co.marcin.novaguilds.impl.versionimpl.v1_7_R4.PacketListenerImpl();
			}

			//Packet extension
			switch(serverVersion) {
				case MINECRAFT_1_7_R2:
				case MINECRAFT_1_7_R3:
					packetExtension = new co.marcin.novaguilds.impl.versionimpl.v1_7_R4.PacketExtensionImpl();
					break;
				case MINECRAFT_1_7_R4:
					packetExtension = new co.marcin.novaguilds.impl.versionimpl.v1_7_R4.PacketExtensionImpl();
					break;
				case MINECRAFT_1_8_R1:
				case MINECRAFT_1_8_R2:
				case MINECRAFT_1_8_R3:
				case MINECRAFT_1_9_R1:
				case MINECRAFT_1_9_R2:
				case MINECRAFT_1_10_R1:
				case MINECRAFT_1_10_R2:
				case MINECRAFT_1_11_R1:
				default:
					packetExtension = new co.marcin.novaguilds.impl.versionimpl.v1_8_R3.PacketExtensionImpl();
					break;
			}

			//SignGUI
			if(Config.SIGNGUI_ENABLED.getBoolean()) {
				switch(serverVersion) {
					case MINECRAFT_1_7_R2:
					case MINECRAFT_1_7_R3:
						signGUI = new co.marcin.novaguilds.impl.versionimpl.v1_7_R3.SignGUIImpl();
						break;
					case MINECRAFT_1_7_R4:
						signGUI = new co.marcin.novaguilds.impl.versionimpl.v1_7_R4.SignGUIImpl();
						break;
					case MINECRAFT_1_8_R1:
						signGUI = new co.marcin.novaguilds.impl.versionimpl.v1_8_R1.SignGUIImpl();
						break;
					case MINECRAFT_1_8_R2:
					case MINECRAFT_1_8_R3:
						signGUI = new co.marcin.novaguilds.impl.versionimpl.v1_8_R3.SignGUIImpl();
						break;
					case MINECRAFT_1_9_R1:
						signGUI = new co.marcin.novaguilds.impl.versionimpl.v1_9_R1.SignGUIImpl();
						break;
					case MINECRAFT_1_9_R2:
					case MINECRAFT_1_10_R1:
					case MINECRAFT_1_10_R2:
					case MINECRAFT_1_11_R1:
					default:
						signGUI = new co.marcin.novaguilds.impl.versionimpl.v1_9_R2.SignGUIImpl();
						break;
				}
			}

			if(Config.TABLIST_ENABLED.getBoolean()) {
				final Map<ConfigManager.ServerVersion, Class<? extends TabList>> tabListClassMap = new HashMap<>();
				tabListClassMap.put(ConfigManager.ServerVersion.MINECRAFT_1_7_R2, co.marcin.novaguilds.impl.versionimpl.v1_7_R4.TabListImpl.class);
				tabListClassMap.put(ConfigManager.ServerVersion.MINECRAFT_1_7_R3, co.marcin.novaguilds.impl.versionimpl.v1_7_R4.TabListImpl.class);
				tabListClassMap.put(ConfigManager.ServerVersion.MINECRAFT_1_7_R4, co.marcin.novaguilds.impl.versionimpl.v1_7_R4.TabListImpl.class);
				tabListClassMap.put(ConfigManager.ServerVersion.MINECRAFT_1_8_R1, co.marcin.novaguilds.impl.versionimpl.v1_8_R1.TabListImpl.class);
				tabListClassMap.put(ConfigManager.ServerVersion.MINECRAFT_1_8_R2, co.marcin.novaguilds.impl.versionimpl.v1_8_R3.TabListImpl.class);
				tabListClassMap.put(ConfigManager.ServerVersion.MINECRAFT_1_8_R3, co.marcin.novaguilds.impl.versionimpl.v1_8_R3.TabListImpl.class);
				tabListClassMap.put(ConfigManager.ServerVersion.MINECRAFT_1_9_R1, co.marcin.novaguilds.impl.versionimpl.v1_8_R3.TabListImpl.class);
				tabListClassMap.put(ConfigManager.ServerVersion.MINECRAFT_1_9_R2, co.marcin.novaguilds.impl.versionimpl.v1_8_R3.TabListImpl.class);
				tabListClassMap.put(ConfigManager.ServerVersion.MINECRAFT_1_10_R1, co.marcin.novaguilds.impl.versionimpl.v1_10_R1.TabListImpl.class);
				tabListClassMap.put(ConfigManager.ServerVersion.MINECRAFT_1_11_R1, co.marcin.novaguilds.impl.versionimpl.v1_10_R1.TabListImpl.class);

				for(ConfigManager.ServerVersion version : ConfigManager.ServerVersion.values()) {
					Class<? extends TabList> tabListClass = tabListClassMap.get(version);
					Constructor<? extends TabList> tabListConstructor = null;

					if(tabListClass != null) {
						tabListConstructor = tabListClass.getConstructor(NovaPlayer.class);
					}

					tabListConstructorMap.put(version, tabListConstructor);
				}

				if(tabListConstructorMap.get(serverVersion) == null) {
					Config.TABLIST_ENABLED.set(false);
					LoggerUtils.error("TabList not found for version " + serverVersion.getString());
				}
			}

			//Register players (for reload)
			for(Player p : CompatibilityUtils.getOnlinePlayers()) {
				getPacketExtension().registerPlayer(p);
			}

			if(!Config.ADVANCEDENTITYUSE.getBoolean()) {
				new AbstractListener() {
					@EventHandler(priority = EventPriority.LOWEST)
					public void onPlayerInteractEntity(org.bukkit.event.player.PlayerInteractEntityEvent event) {
						PlayerInteractEntityEvent clickEvent = new PlayerInteractEntityEvent(event.getPlayer(), event.getRightClicked(), EntityUseAction.INTERACT);
						ListenerManager.getLoggedPluginManager().callEvent(clickEvent);
						event.setCancelled(clickEvent.isCancelled());
					}
				};

				if(ConfigManager.getServerVersion().isNewerThan(ConfigManager.ServerVersion.MINECRAFT_1_7_R4)) {
					new AbstractListener() {
						@EventHandler(priority = EventPriority.LOWEST)
						public void onPlayerInteractAtEntity(PlayerInteractAtEntityEvent event) {
							PlayerInteractEntityEvent interactEntityEvent = new PlayerInteractEntityEvent(event.getPlayer(), event.getRightClicked(), EntityUseAction.INTERACT_AT);
							ListenerManager.getLoggedPluginManager().callEvent(interactEntityEvent);
							event.setCancelled(interactEntityEvent.isCancelled());
						}
					};
				}
			}

			if(signGUI == null) {
				Config.SIGNGUI_ENABLED.set(false);
			}

			if(Config.VAULT_ENABLED.getBoolean()) {
				new VaultListener();
			}

			if(getDependencyManager().isEnabled(Dependency.VANISHNOPACKET)) {
				new VanishListener();
			}

			if(getDependencyManager().isEnabled(Dependency.SCOREBOARDSTATS)) {
				new ScoreboardStatsHook();
			}

			//Tablist/tag update
			TagUtils.refresh();
			TabUtils.refresh();

			//metrics
			setupMetrics();

			//Register glow enchantment
			try {
				FieldAccessor<Boolean> acceptingNewField = Reflections.getField(Enchantment.class, "acceptingNew", boolean.class);
				acceptingNewField.set(true);
				Enchantment.registerEnchantment(new EnchantmentGlow());
				acceptingNewField.set(false);
			}
			catch(Exception e) {
				LoggerUtils.exception(e);
			}

			//Run tasks
			getTaskManager().runTasks();

			LoggerUtils.info("#" + VersionUtils.getBuildCurrent() + " (" + VersionUtils.getCommit() + ") Enabled");
		}
		catch(Exception e) {
			LoggerUtils.exception(e);
		}
	}

	@Override
	public void onDisable() {
		if(FatalNovaGuildsException.fatal) {
			LoggerUtils.info("#" + VersionUtils.getBuildCurrent() + " (FATAL) Disabled");
			return;
		}

		getTaskManager().stopTasks();
		getGuildManager().save();
		getRegionManager().save();
		getPlayerManager().save();
		getRankManager().save();
		LoggerUtils.info("Saved all data");

		if(getPacketExtension() != null) {
			getPacketExtension().unregisterChannel();
		}

		if(getSignGUI() != null) {
			getSignGUI().destroy();
		}

		//remove boss bars
		if(Config.BOSSBAR_ENABLED.getBoolean()) {
			for(Player player : CompatibilityUtils.getOnlinePlayers()) {
				BossBarUtils.removeBar(player);
			}
		}

		//removing holograms
		if(Config.HOLOGRAPHICDISPLAYS_ENABLED.getBoolean()) {
			//Save holograms
			getHologramManager().save();

			for(Hologram h : HologramsAPI.getHolograms(this)) {
				h.delete();
			}
		}

		for(Player p : CompatibilityUtils.getOnlinePlayers()) {
			PlayerManager.getPlayer(p).cancelToolProgress();
		}

		for(NovaPlayer nPlayer : getPlayerManager().getPlayers()) {
			if(nPlayer.getActiveSelection() != null) {
				nPlayer.getActiveSelection().reset();
			}
		}

		LoggerUtils.info("#" + VersionUtils.getBuildCurrent() + " Disabled");
	}

	@Override
	public GuildManager getGuildManager() {
		return guildManager;
	}

	@Override
	public RegionManager getRegionManager() {
		return regionManager;
	}

	@Override
	public PlayerManager getPlayerManager() {
		return playerManager;
	}

	@Override
	public CommandManager getCommandManager() {
		return commandManager;
	}

	@Override
	public MessageManager getMessageManager() {
		return messageManager;
	}

	@Override
	public HologramManager getHologramManager() {
		return hologramManager;
	}

	@Override
	public ConfigManager getConfigManager() {
		return configManager;
	}

	@Override
	public DynmapManager getDynmapManager() {
		return dynmapManager;
	}

	@Override
	public GroupManager getGroupManager() {
		return groupManager;
	}

	@Override
	public TaskManager getTaskManager() {
		return taskManager;
	}

	@Override
	public ListenerManager getListenerManager() {
		return listenerManager;
	}

	@Override
	public ErrorManager getErrorManager() {
		return errorManager;
	}

	@Override
	public Storage getStorage() {
		return storage;
	}

	@Override
	public TabList createTabList(ConfigManager.ServerVersion serverVersion, NovaPlayer nPlayer) {
		if(!Config.TABLIST_ENABLED.getBoolean()) {
			throw new IllegalArgumentException("TabList is disabled");
		}

		try {
			return tabListConstructorMap.get(serverVersion).newInstance(nPlayer);
		}
		catch(IllegalAccessException | InstantiationException | InvocationTargetException e) {
			LoggerUtils.exception(e);
			Config.TABLIST_ENABLED.set(false);
			return null;
		}
	}

	@Override
	public TabList createTabList(NovaPlayer nPlayer) {
		return createTabList(ConfigManager.getServerVersion(), nPlayer);
	}

	@Override
	public RankManager getRankManager() {
		return rankManager;
	}

	@Override
	public PacketExtension getPacketExtension() {
		return packetExtension;
	}

	@Override
	public DependencyManager getDependencyManager() {
		return dependencyManager;
	}

	/**
	 * Gets the instance
	 *
	 * @return the instance
	 */
	public static NovaGuilds getInstance() {
		return instance;
	}

	/**
	 * Sets up the storage
	 *
	 * @throws FatalNovaGuildsException if fails
	 */
	public void setUpStorage() throws FatalNovaGuildsException {
		try {
			storage = new StorageConnector(getConfigManager().getDataStorageType()).getStorage();
		}
		catch(StorageConnectionFailedException | IllegalArgumentException e) {
			if(e instanceof IllegalArgumentException) {
				if(e.getCause() == null || !(e.getCause() instanceof StorageConnectionFailedException)) {
					throw (IllegalArgumentException) e;
				}

				LoggerUtils.error(e.getMessage());
			}

			if(getConfigManager().isSecondaryDataStorageType()) {
				throw new FatalNovaGuildsException("Storage connection failed", e);
			}

			getConfigManager().setToSecondaryDataStorageType();
			setUpStorage();
		}
	}

	/**
	 * Setups metrics
	 *
	 * @throws IOException if fails
	 */
	private void setupMetrics() throws IOException {
		Metrics metrics = new Metrics(this);
		Metrics.Graph guildsAndUsersGraph = metrics.createGraph("Guilds and users");

		guildsAndUsersGraph.addPlotter(new Metrics.Plotter("Guilds") {
			@Override
			public int getValue() {
				return getGuildManager().getGuilds().size();
			}
		});

		guildsAndUsersGraph.addPlotter(new Metrics.Plotter("Users") {
			@Override
			public int getValue() {
				return getPlayerManager().getPlayers().size();
			}
		});

		metrics.start();
	}

	/**
	 * Runs a runnable
	 *
	 * @param runnable Runnable implementation
	 * @param delay    delay in timeUnit
	 * @param timeUnit time unit
	 */
	public static void runTaskLater(Runnable runnable, long delay, TimeUnit timeUnit) {
		Bukkit.getScheduler().runTaskLater(instance, runnable, timeUnit.toSeconds(delay) * 20);
	}

	/**
	 * Runs a runnable
	 *
	 * @param runnable Runnable implementation
	 */
	public static void runTask(Runnable runnable) {
		Bukkit.getScheduler().runTask(instance, runnable);
	}

	/**
	 * Gets sign gui
	 *
	 * @return SignGUI implementation
	 */
	public SignGUI getSignGUI() {
		return signGUI;
	}

	/**
	 * Setups the wrapped logger
	 *
	 * @throws NoSuchFieldException   when something goes wrong
	 * @throws IllegalAccessException when something goes wrong
	 */
	private void setupWrappedLogger() throws NoSuchFieldException, IllegalAccessException {
		FieldAccessor<PluginLogger> loggerField = Reflections.getField(JavaPlugin.class, "logger", PluginLogger.class);
		loggerField.set(this, new WrappedLogger(this));
	}
}
