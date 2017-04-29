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

import org.bukkit.command.CommandSender;

import java.util.Map;
import java.util.Set;

public interface CommandExecutor {
	/**
	 * Execute the command
	 *
	 * @param sender sender
	 * @param args   arguments
	 * @throws Exception bugs occur sometimes...
	 */
	void execute(CommandSender sender, String[] args) throws Exception;

	/**
	 * Gets the command
	 *
	 * @return the command
	 */
	CommandWrapper getCommand();

	/**
	 * Gets the commands map
	 *
	 * @return the map
	 */
	Map<String, CommandWrapper> getCommandsMap();

	/**
	 * Gets a set with subcommands
	 *
	 * @return set
	 */
	Set<CommandWrapper> getSubCommands();

	/**
	 * Tab completer
	 *
	 * @param sender the sender
	 * @param args arguments
	 * @return tab complete set
	 */
	Set<String> onTabComplete(CommandSender sender, String[] args);

	interface Reversed<T> extends CommandExecutor {
		/**
		 * Sets the parameter
		 *
		 * @param parameter the parameter
		 */
		void set(T parameter);

		/**
		 * Gets the parameter
		 *
		 * @return the parameter
		 */
		T getParameter();

		/**
		 * Gets parameter type class
		 *
		 * @return parameter type
		 */
		Class<T> getParameterType();
	}
}
