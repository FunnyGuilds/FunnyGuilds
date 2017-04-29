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

package co.marcin.novaguilds.api.basic;

import co.marcin.novaguilds.api.util.ChatBroadcast;
import co.marcin.novaguilds.api.util.VarKeyApplicable;
import co.marcin.novaguilds.enums.Permission;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Set;

public interface MessageWrapper extends VarKeyApplicable<MessageWrapper> {
	enum Flag {
		/**
		 * Should not be displayed with prefix
		 */
		NOPREFIX,

		/**
		 * Can be displayed as a title
		 */
		TITLE,

		/**
		 * Consists of a list of messages
		 */
		LIST,

		/**
		 * No color restoring after filling a variable
		 */
		NOAFTERVARCOLOR
	}

	/**
	 * Gets the flags
	 *
	 * @return set with flags
	 */
	Set<Flag> getFlags();

	/**
	 * Gets a flag
	 *
	 * @param flag the flag
	 * @return true if message has that flag
	 */
	boolean hasFlag(Flag flag);

	/**
	 * Tells if the message is suitable for Title
	 *
	 * @return true/false
	 */
	boolean getTitle();

	/**
	 * Gets message's yaml path
	 *
	 * @return the path
	 */
	String getPath();

	/**
	 * Gets message name
	 *
	 * @return message name
	 */
	String getName();

	/**
	 * Sets the path
	 *
	 * @param path yaml path
	 */
	void setPath(String path);

	/**
	 * Tells if the prefix is turned on
	 *
	 * @return prefix status true/false
	 */
	boolean isPrefix();

	/**
	 * Checks if the message is empty
	 * In default implementation
	 * string equal to 'none'
	 *
	 * @return true if empty
	 */
	boolean isEmpty();

	/**
	 * Sends the Message to a player
	 *
	 * @param sender receiver
	 */
	void send(CommandSender sender);

	/**
	 * Send the message to a player using NovaPlayer instance
	 *
	 * @param nPlayer receiver NovaPlayer
	 */
	void send(NovaPlayer nPlayer);

	/**
	 * Sets whether the prefix should be displayed
	 *
	 * @param prefix prefix status
	 * @return Message instance
	 */
	MessageWrapper prefix(boolean prefix);

	/**
	 * Broadcasts the message to all players of a guild
	 *
	 * @param guild the guild
	 */
	void broadcast(NovaGuild guild);

	/**
	 * Broadcasts the message to all players
	 */
	void broadcast();

	/**
	 * Broadcasts the message to all players with a certain permission
	 *
	 * @param permission the permission enum
	 */
	void broadcast(Permission permission);

	/**
	 * Gets the message string
	 *
	 * @return message string
	 */
	String get();

	/**
	 * Sets a value to loaded messages
	 *
	 * @param string the value
	 */
	void set(String string);

	/**
	 * Sets a value to loaded messages
	 *
	 * @param list the list
	 */
	void set(List<String> list);

	/**
	 * Gets an ItemStacks from the Message
	 *
	 * @return ItemStack instance
	 */
	ItemStack getItemStack();

	/**
	 * Gets a list
	 *
	 * @return the list
	 */
	List<String> getList();

	/**
	 * Gets a ConfigurationSection
	 *
	 * @return the ConfigurationSection
	 */
	ConfigurationSection getConfigurationSection();

	/**
	 * Gets Message's neighbours (excluding itself)
	 *
	 * @return a list of Messages in one ConfigurationSection
	 */
	List<MessageWrapper> getNeighbours();

	/**
	 * Gets the path of ConfigurationSection the Message's in
	 *
	 * @return the path
	 */
	String getParentPath();

	/**
	 * Creates new chat broadcast
	 *
	 * @return chat broadcast instance using this message instance
	 */
	ChatBroadcast newChatBroadcast();

	/**
	 * Clones the wrapper
	 *
	 * @return wrapper to clone
	 */
	MessageWrapper clone();
}
