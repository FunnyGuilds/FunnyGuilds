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

package co.marcin.novaguilds.manager;

import co.marcin.novaguilds.NovaGuilds;
import co.marcin.novaguilds.api.util.reflect.FieldAccessor;
import co.marcin.novaguilds.enums.Config;
import co.marcin.novaguilds.enums.Dependency;
import co.marcin.novaguilds.exception.AdditionalTaskException;
import co.marcin.novaguilds.exception.FatalNovaGuildsException;
import co.marcin.novaguilds.exception.MissingDependencyException;
import co.marcin.novaguilds.util.LoggerUtils;
import co.marcin.novaguilds.util.reflect.Reflections;
import com.sk89q.worldguard.protection.flags.DefaultFlag;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.StateFlag;
import net.milkbowl.vault.economy.Economy;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DependencyManager {
	private static final NovaGuilds plugin = NovaGuilds.getInstance();
	private final Map<Dependency, Plugin> pluginMap = new HashMap<>();
	private Economy economy;

	/**
	 * Sets up the manager
	 *
	 * @throws FatalNovaGuildsException when something goes wrong
	 */
	public void setUp() throws FatalNovaGuildsException {
		try {
			checkDependencies();
		}
		catch(MissingDependencyException e) {
			throw new FatalNovaGuildsException("Could not satisfy dependencies", e);
		}
	}

	/**
	 * Checks dependencies
	 *
	 * @throws MissingDependencyException when something goes wrong
	 */
	public void checkDependencies() throws MissingDependencyException {
		pluginMap.clear();

		for(Dependency dependency : Dependency.values()) {
			Plugin plugin = getPlugin(dependency.getName());

			if(plugin != null) {
				pluginMap.put(dependency, plugin);
				LoggerUtils.info("Found plugin " + dependency.getName());

				if(dependency.hasAdditionalTasks()) {
					for(AdditionalTask additionalTask : dependency.getAdditionalTasks()) {
						try {
							LoggerUtils.info("Running additional task '" + additionalTask.getClass().getSimpleName() + "' for " + dependency.getName());
							additionalTask.run();
							additionalTask.onSuccess();
						}
						catch(Exception e) {
							additionalTask.onFail();
							AdditionalTaskException taskException = new AdditionalTaskException("Could not pass additional task '" + additionalTask.getClass().getSimpleName() + "' for " + dependency.getName(), e);

							if(!additionalTask.isFatal()) {
								LoggerUtils.exception(taskException);
								continue;
							}

							throw new MissingDependencyException("Invalid dependency " + dependency.getName(), taskException);
						}
					}
				}
			}
			else {
				if(dependency.isHardDependency()) {
					throw new MissingDependencyException("Missing dependency " + dependency.getName());
				}
				else {
					LoggerUtils.info("Could not find plugin: " + dependency.getName() + ", disabling certain features");
				}
			}
		}

		//Set config values varying if dependencies are missing
		Config.BOSSBAR_ENABLED.set(Config.BOSSBAR_ENABLED.getBoolean() && (ConfigManager.getServerVersion().isNewerThan(ConfigManager.ServerVersion.MINECRAFT_1_8_R2) || plugin.getDependencyManager().isEnabled(Dependency.BARAPI) || plugin.getDependencyManager().isEnabled(Dependency.BOSSBARAPI)));
		Config.BOSSBAR_RAIDBAR_ENABLED.set(Config.BOSSBAR_RAIDBAR_ENABLED.getBoolean() && Config.BOSSBAR_ENABLED.getBoolean());
		Config.HOLOGRAPHICDISPLAYS_ENABLED.set(Config.HOLOGRAPHICDISPLAYS_ENABLED.getBoolean() && plugin.getDependencyManager().isEnabled(Dependency.HOLOGRAPHICDISPLAYS));
	}

	/**
	 * Checks if a dependency is enabled
	 *
	 * @param dependency dependency enum
	 * @return boolean
	 */
	public boolean isEnabled(Dependency dependency) {
		return pluginMap.containsKey(dependency);
	}

	/**
	 * Setups economy
	 */
	public void setupEconomy() {
		RegisteredServiceProvider<Economy> rsp = Bukkit.getServicesManager().getRegistration(Economy.class);
		economy = rsp.getProvider();
		Validate.notNull(economy);
	}

	/**
	 * Gets a plugin by its name
	 *
	 * @param name plugin's name
	 * @return plugin instance
	 */
	private Plugin getPlugin(String name) {
		return ListenerManager.getLoggedPluginManager().getPlugin(name);
	}

	/**
	 * Gets the object of a plugin
	 *
	 * @param dependency dependency enum
	 * @param cast       class to cast
	 * @param <T>        class to cast
	 * @return plugin instance
	 */
	@SuppressWarnings("unchecked")
	public <T extends Plugin> T get(Dependency dependency, Class<T> cast) {
		return (T) pluginMap.get(dependency);
	}

	public static class HolographicDisplaysAPIChecker extends AdditionalTask {
		/**
		 * Checks if HolographicDisplays' API class is present
		 */
		public HolographicDisplaysAPIChecker() {
			super(true);
		}

		@Override
		public void run() throws ClassNotFoundException {
			Reflections.getClass("com.gmail.filoghost.holographicdisplays.api.HologramsAPI");
		}
	}

	public static class WorldGuardFlagInjector extends AdditionalTask {
		/**
		 * Injects WorldGuard flag to the plugin
		 */
		public WorldGuardFlagInjector() {
			super(false);
		}

		@Override
		public void run() throws Exception {
			if(!Config.REGION_WORLDGUARD.getBoolean()) {
				LoggerUtils.info("Skipping WorldGuardFlag Injector. Disabled in config");
				return;
			}

			plugin.getRegionManager().createWorldGuardFlag();
			FieldAccessor<Flag[]> defaultFlagFlagListField = Reflections.getField(DefaultFlag.class, "flagsList", Flag[].class);
			defaultFlagFlagListField.setNotFinal();
			Flag[] array = defaultFlagFlagListField.get(null);
			List<Flag> list = new ArrayList<>();
			Collections.addAll(list, array);
			list.add((StateFlag) RegionManager.WORLDGUARD_FLAG);
			defaultFlagFlagListField.set(list.toArray(new Flag[list.size()]));
			LoggerUtils.info("Successfully injected WorldGuard Flag");
		}

		@Override
		public void onFail() {
			Config.REGION_WORLDGUARD.set(false);
			LoggerUtils.info("WorldGuard region checking disabled due to additional task failure.");
		}
	}

	public interface RunnableWithException {
		/**
		 * Runs.
		 *
		 * @throws Exception when something goes wrong
		 */
		void run() throws Exception;
	}

	public static abstract class AdditionalTask implements RunnableWithException {
		private final boolean fatal;

		/**
		 * The constructor
		 *
		 * @param fatal should a failure cause the plugin to disable?
		 */
		public AdditionalTask(boolean fatal) {
			this.fatal = fatal;
		}

		/**
		 * Checks if the task is fatal
		 *
		 * @return boolean
		 */
		public boolean isFatal() {
			return fatal;
		}

		/**
		 * Gets invoked on failure
		 */
		public void onFail() {

		}

		/**
		 * Gets invoked on success
		 */
		public void onSuccess() {

		}
	}

	/**
	 * Gets the Economy
	 *
	 * @return economy instance
	 */
	public Economy getEconomy() {
		return economy;
	}
}
