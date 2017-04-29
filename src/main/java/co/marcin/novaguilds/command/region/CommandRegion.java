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

package co.marcin.novaguilds.command.region;

import co.marcin.novaguilds.api.basic.CommandWrapper;
import co.marcin.novaguilds.api.basic.NovaPlayer;
import co.marcin.novaguilds.command.abstractexecutor.AbstractCommandExecutor;
import co.marcin.novaguilds.enums.Command;
import co.marcin.novaguilds.enums.GuildPermission;
import co.marcin.novaguilds.enums.Message;
import co.marcin.novaguilds.manager.PlayerManager;
import co.marcin.novaguilds.util.StringUtils;
import org.bukkit.command.CommandSender;

public class CommandRegion extends AbstractCommandExecutor {
	public CommandRegion() {
		commandsMap.put("buy",    Command.REGION_BUY);
		commandsMap.put("create", Command.REGION_BUY);
		commandsMap.put("delete", Command.REGION_DELETE);
		commandsMap.put("del",    Command.REGION_DELETE);
	}

	@Override
	public void execute(CommandSender sender, String[] args) throws Exception {
		if(args.length > 0) {
			CommandWrapper subCommand = getSubCommand(args);

			if(subCommand == null) {
				Message.CHAT_UNKNOWNCMD.send(sender);
				return;
			}

			subCommand.execute(sender, StringUtils.parseArgs(args, 1));
		}
		else {
			NovaPlayer nPlayer = PlayerManager.getPlayer(sender);

			if(nPlayer.hasPermission(GuildPermission.REGION_CREATE)
					|| nPlayer.hasPermission(GuildPermission.REGION_REMOVE)
					|| nPlayer.hasPermission(GuildPermission.REGION_RESIZE)) {
				Message.CHAT_COMMANDS_HEADER_REGION.send(sender);
				for(CommandWrapper commandWrapper : getSubCommands()) {
					commandWrapper.getUsageMessage().send(sender);
				}
			}
			else {
				Message.CHAT_GUILD_NOGUILDPERM.send(sender);
			}
		}
	}
}
