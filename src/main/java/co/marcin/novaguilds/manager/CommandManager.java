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
import co.marcin.novaguilds.api.basic.CommandExecutor;
import co.marcin.novaguilds.api.basic.CommandExecutorHandler;
import co.marcin.novaguilds.api.basic.CommandWrapper;
import co.marcin.novaguilds.api.basic.NovaPlayer;
import co.marcin.novaguilds.enums.Command;
import co.marcin.novaguilds.enums.Message;
import co.marcin.novaguilds.enums.Permission;
import co.marcin.novaguilds.util.LoggerUtils;
import co.marcin.novaguilds.util.StringUtils;
import org.bukkit.command.CommandException;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommandManager {
	private static final NovaGuilds plugin = NovaGuilds.getInstance();
	private final Map<String, String> aliases = new HashMap<>();
	private final Map<CommandWrapper, CommandExecutor> executors = new HashMap<>();
	private static final org.bukkit.command.CommandExecutor genericExecutor = new GenericExecutor();

	/**
	 * Sets up the manager
	 */
	public void setUp() {
		Command.init();

		ConfigurationSection section = plugin.getConfig().getConfigurationSection("aliases");

		for(String key : section.getKeys(false)) {
			for(String alias : section.getStringList(key)) {
				aliases.put(alias, key);
			}
		}

		LoggerUtils.info("Enabled");
	}

	/**
	 * Gets the command that an alias points to
	 *
	 * @param alias the alias
	 * @return the main command
	 */
	public String getMainCommand(String alias) {
		return aliases.get(alias);
	}

	/**
	 * Checks if an alias exists
	 *
	 * @param alias the alias
	 * @return boolean
	 */
	public boolean existsAlias(String alias) {
		return aliases.containsKey(alias);
	}

	/**
	 * Registers a command executor
	 *
	 * @param command  command enum
	 * @param executor the executor
	 */
	public void registerExecutor(CommandWrapper command, CommandExecutor executor) {
		if(!executors.containsKey(command)) {
			executors.put(command, executor);

			if(command.hasGenericCommand()) {
				PluginCommand genericCommand = plugin.getCommand(command.getGenericCommand());

				if(executor instanceof org.bukkit.command.CommandExecutor) {
					genericCommand.setExecutor((org.bukkit.command.CommandExecutor) executor);
				}
				else {
					genericCommand.setExecutor(genericExecutor);
				}
			}
		}
	}

	/**
	 * Executes a command
	 *
	 * @param command command enum
	 * @param sender  sender instance
	 * @param args    command arguments
	 */
	public void execute(CommandWrapper command, CommandSender sender, String[] args) {
		CommandExecutor executor = getExecutor(command);

		if(!command.hasPermission(sender)) {
			Message.CHAT_NOPERMISSIONS.send(sender);
			return;
		}

		if(!command.allowedSender(sender)) {
			Message.CHAT_CMDFROMCONSOLE.send(sender);
			return;
		}

		NovaPlayer nPlayer = PlayerManager.getPlayer(sender);

		if((sender instanceof Player) && (command.hasFlag(CommandWrapper.Flag.CONFIRM) && !Permission.NOVAGUILDS_ADMIN_NOCONFIRM.has(sender) && (nPlayer.getCommandExecutorHandler() == null || nPlayer.getCommandExecutorHandler().getState() != CommandExecutorHandler.State.CONFIRMED))) {
			nPlayer.newCommandExecutorHandler(command, args);
			nPlayer.getCommandExecutorHandler().executorVariable(command.getExecutorVariable());
		}
		else {
			if(executor instanceof CommandExecutor.Reversed) {
				((CommandExecutor.Reversed) executor).set(command.getExecutorVariable());
				command.executorVariable(null);
			}

			try {
				executor.execute(sender, args);
			}
			catch(Exception e) {
				LoggerUtils.exception(new CommandException("Unhandled exception executing command '" + command.getName() + "' in plugin NovaGuilds", e));
			}
		}
	}

	/**
	 * Gets an executor
	 *
	 * @param command command enum
	 * @return command executor
	 */
	public CommandExecutor getExecutor(CommandWrapper command) {
		return executors.get(command);
	}

	public static class GenericExecutor implements org.bukkit.command.CommandExecutor, TabCompleter {
		@Override
		public boolean onCommand(CommandSender sender, org.bukkit.command.Command command, String label, String[] args) {
			CommandWrapper commandWrapper = Command.getByGenericCommand(command.getName());

			if(commandWrapper == null) {
				return false;
			}

			commandWrapper.execute(sender, StringUtils.parseQuotedArguments(args));
			return true;
		}

		@Override
		public List<String> onTabComplete(CommandSender sender, org.bukkit.command.Command command, String alias, String[] args) {
			CommandWrapper commandWrapper = Command.getByGenericCommand(command.getName());
			CommandWrapper finalCommand = commandWrapper;

			if(commandWrapper == null) {
				return new ArrayList<>();
			}

			int index = 0, lastSuccessfulIndex = 0;
			while(index < args.length) {
				CommandWrapper finalCommandCheck = finalCommand.getExecutor().getCommandsMap().get(args[index]);

				if(finalCommandCheck != null) {
					finalCommand = finalCommandCheck;
					lastSuccessfulIndex = index;
				}

				index++;
			}

			if(finalCommand != commandWrapper) {
				lastSuccessfulIndex++;
			}

			return new ArrayList<>(finalCommand.getExecutor().onTabComplete(sender, StringUtils.parseArgs(args, lastSuccessfulIndex)));
		}
	}
}
