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

package co.marcin.novaguilds.command.admin;

import co.marcin.novaguilds.api.basic.CommandWrapper;
import co.marcin.novaguilds.command.abstractexecutor.AbstractCommandExecutor;
import co.marcin.novaguilds.enums.Command;
import co.marcin.novaguilds.enums.Message;
import co.marcin.novaguilds.util.StringUtils;
import org.bukkit.command.CommandSender;

public class CommandAdmin extends AbstractCommandExecutor {
	public CommandAdmin() {
		commandsMap.put("guild",    Command.ADMIN_GUILD_ACCESS);
		commandsMap.put("g",        Command.ADMIN_GUILD_ACCESS);
		commandsMap.put("region",   Command.ADMIN_REGION_ACCESS);
		commandsMap.put("rg",       Command.ADMIN_REGION_ACCESS);
		commandsMap.put("hologram", Command.ADMIN_HOLOGRAM_ACCESS);
		commandsMap.put("h",        Command.ADMIN_HOLOGRAM_ACCESS);
		commandsMap.put("reload",   Command.ADMIN_RELOAD);
		commandsMap.put("save",     Command.ADMIN_SAVE);
		commandsMap.put("spy",      Command.ADMIN_CHATSPY);
		commandsMap.put("chatspy",  Command.ADMIN_CHATSPY);
		commandsMap.put("migrate",  Command.ADMIN_MIGRATE);
		commandsMap.put("config",   Command.ADMIN_CONFIG_ACCESS);
		commandsMap.put("player",   Command.ADMIN_PLAYER_ACCESS);
		commandsMap.put("p",        Command.ADMIN_PLAYER_ACCESS);
	}

	@Override
	public void execute(CommandSender sender, String[] args) throws Exception {
		if(args.length == 0) {
			Message.CHAT_COMMANDS_HEADER_ADMIN_MAIN.send(sender);
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
