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

package co.marcin.novaguilds.command.admin.hologram;

import co.marcin.novaguilds.api.basic.CommandWrapper;
import co.marcin.novaguilds.api.basic.NovaHologram;
import co.marcin.novaguilds.command.abstractexecutor.AbstractCommandExecutor;
import co.marcin.novaguilds.enums.Command;
import co.marcin.novaguilds.enums.Config;
import co.marcin.novaguilds.enums.Message;
import co.marcin.novaguilds.util.StringUtils;
import org.bukkit.command.CommandSender;

public class CommandAdminHologram extends AbstractCommandExecutor {
	public CommandAdminHologram() {
		commandsMap.put("list",         Command.ADMIN_HOLOGRAM_LIST);
		commandsMap.put("ls",           Command.ADMIN_HOLOGRAM_LIST);
		commandsMap.put("tp",           Command.ADMIN_HOLOGRAM_TELEPORT);
		commandsMap.put("teleport",     Command.ADMIN_HOLOGRAM_TELEPORT);
//		commandsMap.put("add",          Command.ADMIN_HOLOGRAM_ADD);
		commandsMap.put("addtop",       Command.ADMIN_HOLOGRAM_ADDTOP);
		commandsMap.put("del",          Command.ADMIN_HOLOGRAM_DELETE);
		commandsMap.put("delete",       Command.ADMIN_HOLOGRAM_DELETE);
		commandsMap.put("tphere",       Command.ADMIN_HOLOGRAM_TELEPORT_HERE);
		commandsMap.put("teleporthere", Command.ADMIN_HOLOGRAM_TELEPORT_HERE);
		commandsMap.put("movehere",     Command.ADMIN_HOLOGRAM_TELEPORT_HERE);
		commandsMap.put("move",         Command.ADMIN_HOLOGRAM_TELEPORT_HERE);
	}

	@Override
	public void execute(CommandSender sender, String[] args) throws Exception {
		if(!Config.HOLOGRAPHICDISPLAYS_ENABLED.getBoolean()) {
			Message.CHAT_ADMIN_HOLOGRAM_DISABLED.send(sender);
			return;
		}

		if(args.length == 0) {
			Message.CHAT_COMMANDS_HEADER_ADMIN_HOLOGRAM.send(sender);
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

		if(subCommand.isReversed()) {
			NovaHologram hologram = plugin.getHologramManager().getHologram(args[0]);

			if(hologram == null) {
				Message.CHAT_ADMIN_HOLOGRAM_NOTFOUND.send(sender);
				return;
			}

			subCommand.executorVariable(hologram);
		}

		subCommand.execute(sender, StringUtils.parseArgs(args, !subCommand.isReversed() ? 1 : 2));
	}
}
