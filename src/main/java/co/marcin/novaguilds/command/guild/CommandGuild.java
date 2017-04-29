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

package co.marcin.novaguilds.command.guild;

import co.marcin.novaguilds.api.basic.CommandWrapper;
import co.marcin.novaguilds.api.basic.NovaPlayer;
import co.marcin.novaguilds.command.abstractexecutor.AbstractCommandExecutor;
import co.marcin.novaguilds.enums.Command;
import co.marcin.novaguilds.enums.GuildPermission;
import co.marcin.novaguilds.enums.Message;
import co.marcin.novaguilds.manager.MessageManager;
import co.marcin.novaguilds.manager.PlayerManager;
import co.marcin.novaguilds.util.StringUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandGuild extends AbstractCommandExecutor {
	public CommandGuild() {
		commandsMap.put("pay",      Command.GUILD_BANK_PAY);
		commandsMap.put("withdraw", Command.GUILD_BANK_WITHDRAW);
		commandsMap.put("leader",   Command.GUILD_LEADER);
		commandsMap.put("info",     Command.GUILD_INFO);
		commandsMap.put("leave",    Command.GUILD_LEAVE);
		commandsMap.put("menu",     Command.GUILD_MENU);
		commandsMap.put("home",     Command.GUILD_HOME);
		commandsMap.put("region",   Command.REGION_ACCESS);
		commandsMap.put("rg",       Command.REGION_ACCESS);
		commandsMap.put("ally",     Command.GUILD_ALLY);
		commandsMap.put("kick",     Command.GUILD_KICK);
		commandsMap.put("abandon",  Command.GUILD_ABANDON);
		commandsMap.put("invite",   Command.GUILD_INVITE);
		commandsMap.put("join",     Command.GUILD_JOIN);
		commandsMap.put("create",   Command.GUILD_CREATE);
		commandsMap.put("war",      Command.GUILD_WAR);
		commandsMap.put("compass",  Command.GUILD_COMPASS);
		commandsMap.put("effect",   Command.GUILD_EFFECT);
		commandsMap.put("top",      Command.GUILD_TOP);
		commandsMap.put("items",    Command.GUILD_REQUIREDITEMS);
		commandsMap.put("pvp",      Command.GUILD_PVPTOGGLE);
		commandsMap.put("buylife",  Command.GUILD_BUYLIFE);
		commandsMap.put("buyslot",  Command.GUILD_BUYSLOT);
		commandsMap.put("c",        Command.GUILD_CHATMODE);
		commandsMap.put("chat",     Command.GUILD_CHATMODE);
		commandsMap.put("chatmode", Command.GUILD_CHATMODE);
		commandsMap.put("openinv",  Command.GUILD_OPENINVITATION);
		commandsMap.put("setname",  Command.GUILD_SET_NAME);
		commandsMap.put("name",     Command.GUILD_SET_NAME);
		commandsMap.put("settag",   Command.GUILD_SET_TAG);
		commandsMap.put("tag",      Command.GUILD_SET_TAG);
		commandsMap.put("vault",    Command.GUILD_VAULT_RESTORE);
		commandsMap.put("pi",       Command.PLAYERINFO);
		commandsMap.put("rank",     Command.GUILD_RANK_ACCESS);
	}

	@Override
	public void execute(CommandSender sender, String[] args) throws Exception {
		NovaPlayer nPlayer = PlayerManager.getPlayer(sender);

		if(args.length > 0) {
			CommandWrapper subCommand = getSubCommand(args);
			String[] newArgs = StringUtils.parseArgs(args, 1);

			if(subCommand == null) {
				Message.CHAT_UNKNOWNCMD.send(sender);
			}
			else {
				if(subCommand.isReversed() && subCommand == Command.GUILD_RANK_ACCESS) {
					subCommand.executorVariable(nPlayer.getGuild());
				}

				subCommand.execute(sender, newArgs);
			}
		}
		else {
			if(!(sender instanceof Player)) {
				Message.CHAT_CMDFROMCONSOLE.send(sender);
				return;
			}

			if(nPlayer.hasGuild()) {
				for(String message : Message.CHAT_COMMANDS_GUILD_HASGUILD.getList()) {
					GuildPermission guildPermission = null;
					if(message.startsWith("{") && org.apache.commons.lang.StringUtils.contains(message, "}")) {
						message = message.substring(1);
						String[] split = org.apache.commons.lang.StringUtils.split(message, '}');
						guildPermission = GuildPermission.fromString(split[0]);

						if(split.length == 2) {
							message = split[1];
						}
						else {
							split[0] = "";
							message = StringUtils.join(split, "}");
						}
					}

					if(guildPermission == null || nPlayer.hasPermission(guildPermission)) {
						MessageManager.sendMessage(sender, message);
					}
				}
			}
			else {
				Message.CHAT_COMMANDS_GUILD_NOGUILD.prefix(false).send(sender);
			}
		}
	}
}
