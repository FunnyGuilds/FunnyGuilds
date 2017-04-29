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

package co.marcin.novaguilds.api;

import co.marcin.novaguilds.api.basic.NovaPlayer;
import co.marcin.novaguilds.api.basic.TabList;
import co.marcin.novaguilds.api.manager.ErrorManager;
import co.marcin.novaguilds.api.storage.Storage;
import co.marcin.novaguilds.api.util.packet.PacketExtension;
import co.marcin.novaguilds.manager.CommandManager;
import co.marcin.novaguilds.manager.ConfigManager;
import co.marcin.novaguilds.manager.DependencyManager;
import co.marcin.novaguilds.manager.DynmapManager;
import co.marcin.novaguilds.manager.GroupManager;
import co.marcin.novaguilds.manager.GuildManager;
import co.marcin.novaguilds.manager.HologramManager;
import co.marcin.novaguilds.manager.ListenerManager;
import co.marcin.novaguilds.manager.MessageManager;
import co.marcin.novaguilds.manager.PlayerManager;
import co.marcin.novaguilds.manager.RankManager;
import co.marcin.novaguilds.manager.RegionManager;
import co.marcin.novaguilds.manager.TaskManager;

/**
 * NovaGuilds API
 *
 * @author Marcin Wieczorek
 */
public interface NovaGuildsAPI {
	/**
	 * Returns the instance of RegionManager
	 *
	 * @return RegionManager
	 */
	RegionManager getRegionManager();

	/**
	 * Returns the instance of GuildManager
	 *
	 * @return GuildManager
	 */
	GuildManager getGuildManager();

	/**
	 * Returns the instance of PlayerManager
	 *
	 * @return PlayerManager
	 */
	PlayerManager getPlayerManager();

	/**
	 * Returns the instance of MessageManager
	 *
	 * @return MessageManager
	 */
	MessageManager getMessageManager();

	/**
	 * Returns the instance of CommandManager
	 *
	 * @return CommandManager
	 */
	CommandManager getCommandManager();

	/**
	 * Returns the instance of ConfigManager
	 *
	 * @return ConfigManager
	 */
	ConfigManager getConfigManager();

	/**
	 * Returns the instance of DynmapManager
	 *
	 * @return dynmap manager
	 */
	DynmapManager getDynmapManager();

	/**
	 * Returns the instance of GroupManager
	 *
	 * @return GroupManager
	 */
	GroupManager getGroupManager();

	/**
	 * Returns the instance of HologramManager
	 *
	 * @return HologramManager
	 */
	HologramManager getHologramManager();

	/**
	 * Returns the instance of RankManager
	 *
	 * @return RankManager
	 */
	RankManager getRankManager();

	/**
	 * Returns the instance of TaskManager
	 *
	 * @return TaskManager
	 */
	TaskManager getTaskManager();

	/**
	 * Gets listener manager
	 *
	 * @return the ListenerManager
	 */
	ListenerManager getListenerManager();

	/**
	 * Returns the PacketExtension
	 *
	 * @return the PacketExtension
	 */
	PacketExtension getPacketExtension();

	/**
	 * Returns the DependencyManager
	 *
	 * @return the DependencyManager
	 */
	DependencyManager getDependencyManager();

	/**
	 * Returns the ErrorManager
	 *
	 * @return the ErrorManager
	 */
	ErrorManager getErrorManager();

	/**
	 * Returns the storage
	 *
	 * @return the storage
	 */
	Storage getStorage();

	/**
	 * Creates a TabList for a player
	 *
	 * @param nPlayer player instance
	 * @param version version
	 * @return tablist object
	 */
	TabList createTabList(ConfigManager.ServerVersion version, NovaPlayer nPlayer);

	/**
	 * Creates a TabList for a player
	 *
	 * @param nPlayer player instance
	 * @return tablist object
	 */
	TabList createTabList(NovaPlayer nPlayer);
}
