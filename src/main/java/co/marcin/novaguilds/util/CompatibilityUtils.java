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

package co.marcin.novaguilds.util;

import co.marcin.novaguilds.api.util.reflect.MethodInvoker;
import co.marcin.novaguilds.manager.ConfigManager;
import co.marcin.novaguilds.util.reflect.Reflections;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.UUID;

public class CompatibilityUtils {
	private static Method getOnlinePlayersMethod;
	protected static Method addPlayerToTeamMethod;
	protected static Field boardField;
	protected static Class<?> boardClass;
	protected static Class<?> craftTeamClass;
	protected static Class<?> mojangNameLookupClass;
	protected static MethodInvoker<String> lookupNameMethod;

	static {
		try {
			getOnlinePlayersMethod = Server.class.getMethod("getOnlinePlayers");
			boardClass = Reflections.getCraftClass("Scoreboard");
			craftTeamClass = Reflections.getBukkitClass("scoreboard.CraftScoreboard");
			boardField = Reflections.getPrivateField(craftTeamClass, "board");
			addPlayerToTeamMethod = Reflections.getMethod(boardClass, "addPlayerToTeam");
			mojangNameLookupClass = Reflections.getBukkitClass("util.MojangNameLookup");
			lookupNameMethod = Reflections.getMethod(mojangNameLookupClass, String.class, "lookupName");
		}
		catch(NoSuchMethodException e) {
			LoggerUtils.exception(e);
		}
		catch(ClassNotFoundException | NoSuchFieldException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Gets online players
	 *
	 * @return Collection of online players
	 */
	@SuppressWarnings("unchecked")
	public static Collection<Player> getOnlinePlayers() {
		Collection<Player> collection = new HashSet<>();

		try {
			if(getOnlinePlayersMethod.getReturnType().equals(Collection.class)) {
				collection = ((Collection) getOnlinePlayersMethod.invoke(Bukkit.getServer()));
			}
			else {
				Player[] array = ((Player[]) getOnlinePlayersMethod.invoke(Bukkit.getServer()));
				Collections.addAll(collection, array);
			}
		}
		catch(Exception e) {
			LoggerUtils.exception(e);
		}

		return collection;
	}

	/**
	 * Gets item in player's hand
	 * Fixes issues with 2 hands introduced in 1.9
	 *
	 * @param player player
	 * @return boolean
	 */
	@SuppressWarnings("deprecation")
	public static ItemStack getItemInMainHand(Player player) {
		if(ConfigManager.getServerVersion().isOlderThan(ConfigManager.ServerVersion.MINECRAFT_1_9_R1)) {
			return player.getItemInHand();
		}
		else {
			return player.getInventory().getItemInMainHand();
		}
	}

	/**
	 * Gets clicked inventory
	 * For API older than 1.8
	 *
	 * @param event inventory click event
	 * @return inventory
	 */
	public static Inventory getClickedInventory(InventoryClickEvent event) {
		int slot = event.getRawSlot();
		InventoryView view = event.getView();

		if(slot < 0) {
			return null;
		}
		else if(view.getTopInventory() != null && slot < view.getTopInventory().getSize()) {
			return view.getTopInventory();
		}
		else {
			return view.getBottomInventory();
		}
	}

	/**
	 * Adds an entry to a team
	 *
	 * @param team   team
	 * @param string entry string
	 */
	public static void addTeamEntry(Team team, String string) {
		if(ConfigManager.getServerVersion().isNewerThan(ConfigManager.ServerVersion.MINECRAFT_1_7_R2)) {
			team.addEntry(string);
		}
		else {
			try {
				Scoreboard sb = team.getScoreboard();
				Object board = boardField.get(sb);
				addPlayerToTeamMethod.invoke(board, string, team.getName());
			}
			catch(IllegalAccessException | InvocationTargetException e) {
				LoggerUtils.exception(e);
			}
		}
	}

	/**
	 * Allows getOfflinePlayer in main thread
	 *
	 * @param id uuid
	 * @return offline player
	 */
	public static OfflinePlayer getOfflinePlayer(UUID id) {
		if(ConfigManager.getServerVersion().isNewerThan(ConfigManager.ServerVersion.MINECRAFT_1_7_R2)) {
			return Bukkit.getOfflinePlayer(id);
		}
		else {
			String name = lookupNameMethod.invoke(null, id);
			if(name == null) {
				name = "InvalidUUID";
			}

			//noinspection deprecation
			return Bukkit.getOfflinePlayer(name);
		}
	}
}
