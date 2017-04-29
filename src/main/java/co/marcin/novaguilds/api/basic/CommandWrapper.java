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

import co.marcin.novaguilds.api.storage.Resource;
import co.marcin.novaguilds.enums.Permission;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.Collection;

public interface CommandWrapper {
	enum Flag {
		NOCONSOLE,
		CONFIRM
	}

	/**
	 * Gets the permission
	 *
	 * @return the permission string
	 */
	Permission getPermission();

	/**
	 * Sets the permission
	 *
	 * @param permission permission
	 */
	void setPermission(Permission permission);

	/**
	 * Checks if a sender has permission to execute the command
	 *
	 * @param sender the sender
	 * @return boolean
	 */
	boolean hasPermission(CommandSender sender);

	/**
	 * Gets tab completer
	 *
	 * @return tab completer instance
	 */
	TabCompleter getTabCompleter();

	/**
	 * Sets the tab completer
	 *
	 * @param tabCompleter tab completer
	 */
	void setTabCompleter(TabCompleter tabCompleter);

	/**
	 * Checks if the command has a tab completer
	 *
	 * @return boolean
	 */
	boolean hasTabCompleter();

	/**
	 * Sets flags
	 *
	 * @param flags flags
	 */
	void setFlags(Flag... flags);

	/**
	 * Checks if the command has a flag
	 *
	 * @param flag flag
	 * @return boolean
	 */
	boolean hasFlag(Flag flag);

	/**
	 * Gets the flags
	 *
	 * @return collections with flags
	 */
	Collection<Flag> getFlags();

	/**
	 * Sets the executor
	 *
	 * @param executor executor instance
	 */
	void setExecutor(CommandExecutor executor);

	/**
	 * Gets the executor
	 *
	 * @return executor instance
	 */
	CommandExecutor getExecutor();

	/**
	 * Gets executor's class
	 *
	 * @return the class
	 */
	Class<? extends CommandExecutor> getExecutorClass();

	/**
	 * Checks if the executor is reversed
	 *
	 * @return boolean
	 */
	boolean isReversed();

	/**
	 * Executes the command
	 *
	 * @param sender sender
	 * @param args   arguments
	 */
	void execute(CommandSender sender, String[] args);

	/**
	 * Gets usage message
	 *
	 * @return the message
	 */
	MessageWrapper getUsageMessage();

	/**
	 * Sets the usage message
	 *
	 * @param message message
	 */
	void setUsageMessage(MessageWrapper message);

	/**
	 * Checks if a sender is allowed
	 * (if is console)
	 *
	 * @param sender the sender
	 * @return boolean
	 */
	boolean allowedSender(CommandSender sender);

	/**
	 * Gets the executor variable
	 *
	 * @return the object
	 */
	Resource getExecutorVariable();

	/**
	 * Set the executor variable
	 *
	 * @param resource the object
	 */
	void executorVariable(Resource resource);

	/**
	 * Gets the name
	 *
	 * @return name string
	 */
	String getName();

	/**
	 * Sets the name
	 *
	 * @param name string
	 */
	void setName(String name);

	/**
	 * Checks if the command has a generic command
	 *
	 * @return boolean
	 */
	boolean hasGenericCommand();

	/**
	 * Gets generic command string
	 *
	 * @return the string
	 */
	String getGenericCommand();

	/**
	 * Set the generic command
	 * /g = "g"
	 *
	 * @param genericCommand generic command string
	 */
	void setGenericCommand(String genericCommand);
}
