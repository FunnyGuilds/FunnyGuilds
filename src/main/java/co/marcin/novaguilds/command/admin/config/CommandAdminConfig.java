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

package co.marcin.novaguilds.command.admin.config;

import co.marcin.novaguilds.api.basic.CommandWrapper;
import co.marcin.novaguilds.command.abstractexecutor.AbstractCommandExecutor;
import co.marcin.novaguilds.enums.Command;
import co.marcin.novaguilds.enums.Message;
import co.marcin.novaguilds.util.StringUtils;
import org.bukkit.command.CommandSender;

public class CommandAdminConfig extends AbstractCommandExecutor {
	public CommandAdminConfig() {
		commandsMap.put("get",    Command.ADMIN_CONFIG_GET);
		commandsMap.put("reload", Command.ADMIN_CONFIG_RELOAD);
		commandsMap.put("reset",  Command.ADMIN_CONFIG_RESET);
		commandsMap.put("save",   Command.ADMIN_CONFIG_SAVE);
		commandsMap.put("set",    Command.ADMIN_CONFIG_SET);
	}

	@Override
	public void execute(CommandSender sender, String[] args) throws Exception {
		if(args.length == 0) {
			Message.CHAT_COMMANDS_HEADER_ADMIN_CONFIG.send(sender);
			for(CommandWrapper commandWrapper : getSubCommands()) {
				commandWrapper.getUsageMessage().send(sender);
			}
			return;
		}

		CommandWrapper subCommand = getSubCommand(args);

		if(subCommand == null) {
			Message.CHAT_UNKNOWNCMD.send(sender);
			return;
		}

		subCommand.execute(sender, StringUtils.parseArgs(args, 1));
	}
}
