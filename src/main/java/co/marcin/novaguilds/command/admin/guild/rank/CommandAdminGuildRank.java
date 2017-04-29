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

package co.marcin.novaguilds.command.admin.guild.rank;

import co.marcin.novaguilds.api.basic.CommandWrapper;
import co.marcin.novaguilds.api.basic.NovaGuild;
import co.marcin.novaguilds.api.basic.NovaPlayer;
import co.marcin.novaguilds.api.basic.NovaRank;
import co.marcin.novaguilds.api.storage.Resource;
import co.marcin.novaguilds.command.abstractexecutor.AbstractCommandExecutor;
import co.marcin.novaguilds.enums.Command;
import co.marcin.novaguilds.enums.Message;
import co.marcin.novaguilds.manager.PlayerManager;
import co.marcin.novaguilds.util.StringUtils;
import org.bukkit.command.CommandSender;

public class CommandAdminGuildRank extends AbstractCommandExecutor.Reversed<NovaGuild> {
	private final boolean admin;

	public CommandAdminGuildRank() {
		this(true);
	}

	public CommandAdminGuildRank(boolean admin) {
		this.admin = admin;

		if(admin) {
			commandsMap.put("list", Command.ADMIN_GUILD_RANK_LIST);
			commandsMap.put("ls", Command.ADMIN_GUILD_RANK_LIST);
			commandsMap.put("del", Command.ADMIN_GUILD_RANK_DELETE);
			commandsMap.put("delete", Command.ADMIN_GUILD_RANK_DELETE);
		}
		else {
			commandsMap.put("list", Command.GUILD_RANK_LIST);
			commandsMap.put("ls", Command.GUILD_RANK_LIST);
			commandsMap.put("del", Command.GUILD_RANK_DELETE);
			commandsMap.put("delete", Command.GUILD_RANK_DELETE);
		}
	}

	@Override
	public void execute(CommandSender sender, String[] args) throws Exception {
		NovaGuild guild;

		if(admin) {
			if(args.length == 0) {
				Message.CHAT_COMMANDS_HEADER_ADMIN_GUILD_RANK.send(sender);
				for(CommandWrapper commandWrapper : getSubCommands()) {
					commandWrapper.getUsageMessage().send(sender);
				}
				return;
			}

			guild = getParameter();
		}
		else {
			if(args.length == 0) {
				Message.CHAT_COMMANDS_HEADER_GUILD_RANK.send(sender);
				for(CommandWrapper commandWrapper : getSubCommands()) {
					commandWrapper.getUsageMessage().send(sender);
				}
				return;
			}

			NovaPlayer nPlayer = PlayerManager.getPlayer(sender);
			guild = nPlayer.getGuild();
		}

		CommandWrapper subCommand = getSubCommand(args);

		if(subCommand == null) {
			Message.CHAT_UNKNOWNCMD.send(sender);
			return;
		}

		if(guild == null) {
			Message.CHAT_GUILD_COULDNOTFIND.send(sender);
			return;
		}

		int argCut = 1;
		if(subCommand == Command.ADMIN_GUILD_RANK_LIST || subCommand == Command.GUILD_RANK_LIST) {
			subCommand.executorVariable(guild);
		}
		else {
			Resource rank = null;
			String rankName = args[0];

			for(NovaRank rankLoop : getParameter().getRanks()) {
				if(rankLoop.getName().equalsIgnoreCase(rankName)) {
					rank = rankLoop;
					break;
				}
			}

			if(rank == null) {
				Message.CHAT_ADMIN_GUILD_RANK_NOTFOUND.send(sender);
				return;
			}

			subCommand.executorVariable(rank);
			argCut = 2;
		}

		subCommand.execute(sender, StringUtils.parseArgs(args, argCut));
	}
}
