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
import co.marcin.novaguilds.api.basic.MessageWrapper;
import co.marcin.novaguilds.api.basic.NovaGuild;
import co.marcin.novaguilds.api.basic.NovaPlayer;
import co.marcin.novaguilds.api.basic.NovaRaid;
import co.marcin.novaguilds.api.event.GuildAbandonEvent;
import co.marcin.novaguilds.api.storage.ResourceManager;
import co.marcin.novaguilds.enums.AbandonCause;
import co.marcin.novaguilds.enums.Config;
import co.marcin.novaguilds.enums.DataStorageType;
import co.marcin.novaguilds.enums.Message;
import co.marcin.novaguilds.enums.VarKey;
import co.marcin.novaguilds.impl.basic.NovaGroupImpl;
import co.marcin.novaguilds.impl.basic.NovaGuildImpl;
import co.marcin.novaguilds.runnable.RunnableTeleportRequest;
import co.marcin.novaguilds.util.ItemStackUtils;
import co.marcin.novaguilds.util.LoggerUtils;
import co.marcin.novaguilds.util.NumberUtils;
import co.marcin.novaguilds.util.StringUtils;
import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class GuildManager {
	private static final NovaGuilds plugin = NovaGuilds.getInstance();
	private final Map<String, NovaGuild> guilds = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);

	/**
	 * Gets a guild by its name
	 *
	 * @param name guild's name
	 * @return the instance
	 */
	public static NovaGuild getGuildByName(String name) {
		return plugin.getGuildManager().guilds.get(name);
	}

	/**
	 * Gets a guild by its tag
	 *
	 * @param tag guild's tag
	 * @return the instance
	 */
	public static NovaGuild getGuildByTag(String tag) {
		for(NovaGuild guild : plugin.getGuildManager().getGuilds()) {
			if(StringUtils.removeColors(guild.getTag()).equalsIgnoreCase(tag)) {
				return guild;
			}
		}
		return null;
	}

	/**
	 * Gets a guild by its unique ID
	 *
	 * @param uuid guild's uuid
	 * @return the instance
	 */
	public static NovaGuild getGuild(UUID uuid) {
		for(NovaGuild guild : plugin.getGuildManager().getGuilds()) {
			if(guild.getUUID().equals(uuid)) {
				return guild;
			}
		}
		return null;
	}

	/**
	 * Find by player/tag/guildname
	 *
	 * @param mixed mixed string
	 * @return guild instance
	 */
	public static NovaGuild getGuildFind(String mixed) {
		try {
			return getGuild(UUID.fromString(mixed));
		}
		catch(IllegalArgumentException e) {
			NovaGuild guild = getGuildByName(mixed);

			if(guild == null) {
				guild = getGuildByTag(mixed);
			}

			if(guild == null) {
				NovaPlayer nPlayer = PlayerManager.getPlayer(mixed);

				if(nPlayer == null) {
					return null;
				}

				guild = nPlayer.getGuild();
			}

			return guild;
		}
	}

	/**
	 * Gets all loaded guilds
	 *
	 * @return collection of guilds
	 */
	public Collection<NovaGuild> getGuilds() {
		return guilds.values();
	}

	/**
	 * Checks if a guild exists
	 *
	 * @param guildName guild name
	 * @return boolean
	 */
	public boolean exists(String guildName) {
		return guilds.containsKey(guildName);
	}

	/**
	 * Loads guilds
	 */
	public void load() {
		guilds.clear();
		for(NovaGuild guild : getResourceManager().load()) {
			if(guilds.containsKey(guild.getName())) {
				if(Config.DELETEINVALID.getBoolean()) {
					getResourceManager().addToRemovalQueue(guild);
				}

				LoggerUtils.error("Removed guild with doubled name (" + guild.getName() + ")");
				continue;
			}

			guilds.put(guild.getName(), guild);
		}

		LoggerUtils.info("Loaded " + guilds.size() + " guilds.");

		loadVaultHolograms();
		LoggerUtils.info("Generated bank holograms.");
	}

	/**
	 * Adds a guild
	 *
	 * @param guild guild instance
	 */
	public void add(NovaGuild guild) {
		guilds.put(guild.getName(), guild);
	}

	/**
	 * Saves a guild
	 *
	 * @param guild guild instance
	 */
	public void save(NovaGuild guild) {
		getResourceManager().save(guild);
	}

	/**
	 * Saves all guilds
	 */
	public void save() {
		long startTime = System.nanoTime();
		int count = getResourceManager().executeSave() + getResourceManager().save(getGuilds());
		LoggerUtils.info("Guilds data saved in " + TimeUnit.MILLISECONDS.convert((System.nanoTime() - startTime), TimeUnit.NANOSECONDS) / 1000.0 + "s (" + count + " guilds)");

		startTime = System.nanoTime();
		count = getResourceManager().executeRemoval();
		LoggerUtils.info("Guilds removed in " + TimeUnit.MILLISECONDS.convert((System.nanoTime() - startTime), TimeUnit.NANOSECONDS) / 1000.0 + "s (" + count + " guilds)");
	}

	/**
	 * Deletes a guild
	 *
	 * @param guild guild instance
	 * @param cause abandon cause
	 */
	public void delete(NovaGuild guild, AbandonCause cause) {
		getResourceManager().addToRemovalQueue(guild);
		guilds.remove(guild.getName());
		guild.destroy(cause);
	}

	/**
	 * Deletes a guild
	 *
	 * @param event guild abandon event
	 */
	public void delete(GuildAbandonEvent event) {
		if(event.isCancelled()) {
			return;
		}

		delete(event.getGuild(), event.getCause());
	}

	/**
	 * Changes guild name
	 *
	 * @param guild   guild instance
	 * @param newName new name
	 */
	public void changeName(NovaGuild guild, String newName) {
		guilds.remove(guild.getName());
		guilds.put(newName, guild);
		guild.setName(newName);
	}

	/**
	 * Gets raid a guild is taking part in
	 *
	 * @param guild guild instance
	 * @return list of raids
	 */
	public List<NovaRaid> getRaidsTakingPart(NovaGuild guild) {
		final List<NovaRaid> list = new ArrayList<>();
		for(NovaGuild raidGuild : getGuilds()) {
			if(raidGuild.isRaid() && raidGuild.getRaid().getGuildAttacker().equals(guild)) {
				list.add(raidGuild.getRaid());
			}
		}

		return list;
	}

	/**
	 * Does the post check
	 * Unloads invalid guilds
	 */
	public void postCheck() {
		int i = 0;

		for(NovaGuild guild : new ArrayList<>(getGuilds())) {
			if(!postCheck(guild)) {
				i++;
				continue;
			}

			plugin.getDynmapManager().addGuild(guild);
		}

		LoggerUtils.info("Postcheck finished. Found " + i + " invalid guilds");
	}

	/**
	 * Performs a post check of a guild
	 *
	 * @param guild guild
	 * @return true if guild was valid
	 */
	public boolean postCheck(NovaGuild guild) {
		boolean remove = false;
		guild.postSetUp();

		if(((NovaGuildImpl) guild).getLeaderName() != null) {
			LoggerUtils.info("(" + guild.getName() + ") Leader's name is set. Probably leader is null");
		}

		if(guild.getLeader() == null) {
			LoggerUtils.info("(" + guild.getName() + ") Leader is null");
			remove = true;
		}

		if(guild.getPlayers().isEmpty()) {
			LoggerUtils.info("(" + guild.getName() + ") 0 players");
			remove = true;
		}

		if(guild.getHome() == null) {
			LoggerUtils.info("(" + guild.getName() + ") home location is null");
			remove = true;
		}

		if(guild.getId() <= 0 && plugin.getConfigManager().getDataStorageType() != DataStorageType.FLAT) {
			LoggerUtils.info("(" + guild.getName() + ") ID <= 0 !");
			remove = true;
		}

		if(remove) {
			LoggerUtils.info("Unloaded guild " + guild.getName());

			if(Config.DELETEINVALID.getBoolean()) {
				GuildAbandonEvent guildAbandonEvent = new GuildAbandonEvent(guild, AbandonCause.INVALID);
				ListenerManager.getLoggedPluginManager().callEvent(guildAbandonEvent);

				if(!guildAbandonEvent.isCancelled()) {
					delete(guild, AbandonCause.INVALID);
					LoggerUtils.info("DELETED guild " + guild.getName());
				}
			}
			else {
				if(guilds.containsKey(guild.getName())) {
					guilds.remove(guild.getName());
				}

				guild.destroy(AbandonCause.UNLOADED);
			}

			guild.unload();
			return false;
		}

		return true;
	}

	/**
	 * Gets top guilds by points
	 *
	 * @param count amount of guilds
	 * @return list of guilds
	 */
	public List<NovaGuild> getTopGuildsByPoints(int count) {
		final List<NovaGuild> guildsByPoints = new ArrayList<>(guilds.values());

		Collections.sort(guildsByPoints, new Comparator<NovaGuild>() {
			public int compare(NovaGuild o1, NovaGuild o2) {
				return o2.getPoints() - o1.getPoints();
			}
		});

		final List<NovaGuild> guildsLimited = new ArrayList<>();

		int i = 0;
		for(NovaGuild guild : guildsByPoints) {
			guildsLimited.add(guild);

			i++;
			if(i == count) {
				break;
			}
		}

		return guildsLimited;
	}

	/**
	 * Gets the list of guilds
	 * sorted by most inactive
	 *
	 * @return list of guilds
	 */
	public List<NovaGuild> getMostInactiveGuilds() {
		final List<NovaGuild> guildsByInactive = new ArrayList<>(guilds.values());

		Collections.sort(guildsByInactive, new Comparator<NovaGuild>() {
			public int compare(NovaGuild o1, NovaGuild o2) {
				return (int) (NumberUtils.systemSeconds() - o2.getInactiveTime()) - (int) (NumberUtils.systemSeconds() - o1.getInactiveTime());
			}
		});

		return guildsByInactive;
	}

	/**
	 * Loads vault holograms for each guild
	 */
	private void loadVaultHolograms() {
		for(NovaGuild guild : getGuilds()) {
			if(guild.getVaultLocation() != null) {
				appendVaultHologram(guild);
			}
		}
	}

	/**
	 * Checks if an itemstack is the vault item
	 *
	 * @param itemStack itemstack
	 * @return boolean
	 */
	public boolean isVaultItemStack(ItemStack itemStack) {
		return ItemStackUtils.isSimilar(itemStack, Config.VAULT_ITEM.getItemStack());
	}

	/**
	 * Appends the vault hologram
	 *
	 * @param guild guild instance
	 */
	public void appendVaultHologram(NovaGuild guild) {
		if(Config.HOLOGRAPHICDISPLAYS_ENABLED.getBoolean()) {
			if(Config.VAULT_HOLOGRAM_ENABLED.getBoolean()) {
				checkVaultDestroyed(guild);
				if(guild.getVaultLocation() != null) {
					if(guild.getVaultHologram() == null) {
						Location hologramLocation = guild.getVaultLocation().clone();
						hologramLocation.add(0.5, 2, 0.5);
						Hologram hologram = HologramsAPI.createHologram(plugin, hologramLocation);
						hologram.getVisibilityManager().setVisibleByDefault(false);
						for(String hologramLine : Config.VAULT_HOLOGRAM_LINES.getStringList()) {
							if(hologramLine.startsWith("[ITEM]")) {
								hologramLine = hologramLine.substring(6);
								ItemStack itemStack = ItemStackUtils.stringToItemStack(hologramLine);
								if(itemStack != null) {
									hologram.appendItemLine(itemStack);
								}
							}
							else {
								hologram.appendTextLine(StringUtils.fixColors(hologramLine));
							}
						}

						guild.setVaultHologram(hologram);

						for(Player player : guild.getOnlinePlayers()) {
							guild.showVaultHologram(player);
						}
					}
				}
			}
		}
	}

	/**
	 * Checks if a block is the vault
	 *
	 * @param block the block
	 * @return boolean
	 */
	public boolean isVaultBlock(Block block) {
		if(block.getType() == Config.VAULT_ITEM.getItemStack().getType()) {
			for(NovaGuild guild : getGuilds()) {
				checkVaultDestroyed(guild);
				if(guild.getVaultLocation() != null) {
					if(guild.getVaultLocation().getWorld().equals(block.getWorld())) {
						if(guild.getVaultLocation().distance(block.getLocation()) < 1) {
							return true;
						}
					}
				}
			}
		}
		return false;
	}

	/**
	 * Fixes vault hologram and vault location
	 * if it has been disabled abnormally
	 *
	 * @param guild guild instance
	 */
	public static void checkVaultDestroyed(NovaGuild guild) {
		if(guild.getVaultLocation() != null) {
			if(guild.getVaultLocation().getBlock().getType() != Material.CHEST) {
				guild.setVaultLocation(null);
				Hologram hologram = guild.getVaultHologram();

				if(hologram != null) {
					hologram.delete();
				}

				guild.setVaultHologram(null);
			}
		}
	}

	/**
	 * Teleports a player with a delay
	 *
	 * @param player   target player
	 * @param location target location
	 * @param message  teleport message
	 */
	public void delayedTeleport(Player player, Location location, MessageWrapper message) {
		Runnable task = new RunnableTeleportRequest(player, location, message);
		int delay = GroupManager.getGroup(player) == null ? 0 : GroupManager.getGroup(player).get(NovaGroupImpl.Key.HOME_DELAY);

		if(delay > 0) {
			Map<VarKey, String> vars = new HashMap<>();
			vars.put(VarKey.DELAY, String.valueOf(GroupManager.getGroup(player).get(NovaGroupImpl.Key.HOME_DELAY)));
			NovaGuilds.runTaskLater(task, delay, TimeUnit.SECONDS);
			Message.CHAT_DELAYEDTELEPORT.clone().vars(vars).send(player);
		}
		else {
			task.run();
		}
	}

	/**
	 * Generates a message with top guilds
	 *
	 * @return list of strings
	 */
	public List<String> getTopGuilds() {
		int limit = Config.LEADERBOARD_GUILD_ROWS.getInt();
		int i = 1;

		final List<String> list = new ArrayList<>();
		Map<VarKey, String> vars = new HashMap<>();

		for(NovaGuild guild : plugin.getGuildManager().getTopGuildsByPoints(limit)) {
			vars.clear();
			vars.put(VarKey.GUILD_NAME, guild.getName());
			vars.put(VarKey.N, String.valueOf(i));
			vars.put(VarKey.GUILD_POINTS, String.valueOf(guild.getPoints()));
			list.add(Message.HOLOGRAPHICDISPLAYS_TOPGUILDS_ROW.clone().vars(vars).get());
			i++;
		}

		return list;
	}

	/**
	 * Cleans inactive guilds
	 */
	public void cleanInactiveGuilds() {
		int count = 0;

		for(NovaGuild guild : plugin.getGuildManager().getMostInactiveGuilds()) {
			if(NumberUtils.systemSeconds() - guild.getInactiveTime() < Config.CLEANUP_INACTIVETIME.getSeconds()) {
				break;
			}

			//fire event
			GuildAbandonEvent guildAbandonEvent = new GuildAbandonEvent(guild, AbandonCause.INACTIVE);
			ListenerManager.getLoggedPluginManager().callEvent(guildAbandonEvent);

			if(!guildAbandonEvent.isCancelled()) {
				Map<VarKey, String> vars = new HashMap<>();
				vars.put(VarKey.GUILD_NAME, guild.getName());
				Message.BROADCAST_ADMIN_GUILD_CLEANUP.clone().vars(vars).broadcast();
				LoggerUtils.info("Abandoned guild " + guild.getName() + " due to inactivity.");
				count++;

				plugin.getGuildManager().delete(guildAbandonEvent);
			}
		}

		LoggerUtils.info("Guilds cleanup finished, removed " + count + " guilds.");
	}

	/**
	 * Gets the resource manager
	 *
	 * @return the manager
	 */
	public ResourceManager<NovaGuild> getResourceManager() {
		return plugin.getStorage().getResourceManager(NovaGuild.class);
	}
}
