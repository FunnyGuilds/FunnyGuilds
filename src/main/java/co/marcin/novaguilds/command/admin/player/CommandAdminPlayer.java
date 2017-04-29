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

package co.marcin.novaguilds.command.admin.player;

import co.marcin.novaguilds.api.basic.CommandWrapper;
import co.marcin.novaguilds.api.basic.NovaPlayer;
import co.marcin.novaguilds.command.abstractexecutor.AbstractCommandExecutor;
import co.marcin.novaguilds.enums.Command;
import co.marcin.novaguilds.enums.Message;
import co.marcin.novaguilds.manager.PlayerManager;
import co.marcin.novaguilds.util.StringUtils;
import org.bukkit.command.CommandSender;

public class CommandAdminPlayer extends AbstractCommandExecutor {
	public CommandAdminPlayer() {
		commandsMap.put("setpoints", Command.ADMIN_PLAYER_SET_POINTS);
		commandsMap.put("points",    Command.ADMIN_PLAYER_SET_POINTS);
	}

	@Override
	public void execute(CommandSender sender, String[] args) throws Exception {
		if(args.length == 0) {
			Message.CHAT_COMMANDS_HEADER_ADMIN_PLAYER.send(sender);
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
			NovaPlayer nPlayer = PlayerManager.getPlayer(args[0]);

			if(nPlayer == null) {
				Message.CHAT_PLAYER_NOTEXISTS.send(sender);
				return;
			}

			subCommand.executorVariable(nPlayer);
		}

		subCommand.execute(sender, StringUtils.parseArgs(args, !subCommand.isReversed() ? 1 : 2));
	}
}
