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
import co.marcin.novaguilds.api.basic.NovaGroup;
import co.marcin.novaguilds.impl.basic.NovaGroupImpl;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class GroupManager {
	private static final NovaGuilds plugin = NovaGuilds.getInstance();
	private final Map<String, NovaGroup> groups = new HashMap<>();

	/**
	 * Loads groups
	 */
	public void load() {
		groups.clear();
		Set<String> groupsNames = plugin.getConfig().getConfigurationSection("groups").getKeys(false);
		groupsNames.add("admin");

		for(String groupName : groupsNames) {
			groups.put(groupName, new NovaGroupImpl(groupName));
		}
	}

	/**
	 * Gets the group of a player
	 *
	 * @param player player
	 * @return the group
	 */
	public static NovaGroup getGroup(Player player) {
		Map<String, NovaGroup> groups = plugin.getGroupManager().getGroups();
		String groupName = "default";

		if(player == null) {
			return getGroup(groupName);
		}

		if(player.hasPermission("novaguilds.group.admin")) {
			return getGroup("admin");
		}

		for(String name : groups.keySet()) {
			if(player.hasPermission("novaguilds.group." + name) && !name.equalsIgnoreCase("default")) {
				groupName = name;
				break;
			}
		}

		return getGroup(groupName);
	}

	/**
	 * Gets the group of a command sender
	 *
	 * @param sender command sender
	 * @return the group
	 */
	public static NovaGroup getGroup(CommandSender sender) {
		if(sender instanceof Player) {
			return getGroup((Player) sender);
		}
		else {
			return getGroup("admin");
		}
	}

	/**
	 * Gets a group by its name
	 *
	 * @param groupName group name
	 * @return the group
	 */
	public static NovaGroup getGroup(String groupName) {
		Map<String, NovaGroup> groups = plugin.getGroupManager().getGroups();

		if(groups.containsKey(groupName)) {
			return groups.get(groupName);
		}

		return null;
	}

	/**
	 * Gets all groups
	 *
	 * @return map with groups and their names (as keys)
	 */
	public Map<String, NovaGroup> getGroups() {
		return groups;
	}
}
