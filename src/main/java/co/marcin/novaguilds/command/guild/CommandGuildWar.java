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

import co.marcin.novaguilds.NovaGuilds;
import co.marcin.novaguilds.api.basic.MessageWrapper;
import co.marcin.novaguilds.api.basic.NovaGuild;
import co.marcin.novaguilds.api.basic.NovaPlayer;
import co.marcin.novaguilds.command.abstractexecutor.AbstractCommandExecutor;
import co.marcin.novaguilds.enums.GuildPermission;
import co.marcin.novaguilds.enums.Message;
import co.marcin.novaguilds.enums.VarKey;
import co.marcin.novaguilds.manager.GuildManager;
import co.marcin.novaguilds.manager.MessageManager;
import co.marcin.novaguilds.manager.PlayerManager;
import co.marcin.novaguilds.util.StringUtils;
import co.marcin.novaguilds.util.TabUtils;
import co.marcin.novaguilds.util.TagUtils;
import org.bukkit.command.CommandSender;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class CommandGuildWar extends AbstractCommandExecutor {
	@Override
	public void execute(CommandSender sender, String[] args) throws Exception {
		NovaPlayer nPlayer = PlayerManager.getPlayer(sender);

		if(!nPlayer.hasGuild()) {
			Message.CHAT_PLAYER_HASNOGUILD.send(sender);
			return;
		}

		NovaGuild guild = nPlayer.getGuild();

		if(args.length == 0) { //List wars
			Message.CHAT_GUILD_WAR_LIST_WARSHEADER.send(sender);
			MessageWrapper guildNameFormat = Message.CHAT_GUILD_WAR_LIST_ITEM;

			if(!guild.getWars().isEmpty()) {
				final Collection<MessageWrapper> warNamesSet = new HashSet<>();
				for(NovaGuild guildLoop : guild.getWars()) {
					warNamesSet.add(guildNameFormat.clone().setVar(VarKey.GUILD_NAME, guildLoop.getName()));
				}

				MessageManager.sendPrefixMessage(sender, StringUtils.join(warNamesSet, Message.CHAT_GUILD_WAR_LIST_SEPARATOR));
			}
			else {
				Message.CHAT_GUILD_WAR_LIST_NOWARS.send(sender);
			}

			if(!guild.getNoWarInvitations().isEmpty()) {
				Message.CHAT_GUILD_WAR_LIST_NOWARINVHEADER.send(sender);
				final Collection<MessageWrapper> noWarInvitationNamesSet = new HashSet<>();

				for(NovaGuild guildLoop : guild.getNoWarInvitations()) {
					noWarInvitationNamesSet.add(guildNameFormat.clone().setVar(VarKey.GUILD_NAME, guildLoop.getName()));
				}

				MessageManager.sendPrefixMessage(sender, StringUtils.join(noWarInvitationNamesSet, Message.CHAT_GUILD_WAR_LIST_SEPARATOR));
			}

			return;
		}

		String guildName = args[0];

		NovaGuild cmdGuild = GuildManager.getGuildFind(guildName);

		if(cmdGuild == null) {
			Message.CHAT_GUILD_COULDNOTFIND.send(sender);
			return;
		}

		if(guild.isWarWith(cmdGuild)) { //no war inv
			Map<VarKey, String> vars = new HashMap<>();

			if(guild.isNoWarInvited(cmdGuild)) { //accepting no-war
				if(!nPlayer.hasPermission(GuildPermission.WAR_INVITE_ACCEPT)) {
					Message.CHAT_GUILD_NOGUILDPERM.send(sender);
					return;
				}

				guild.removeNoWarInvitation(cmdGuild);
				guild.removeWar(cmdGuild);
				cmdGuild.removeWar(guild);

				//broadcast
				vars.put(VarKey.GUILD1, guild.getName());
				vars.put(VarKey.GUILD2, cmdGuild.getName());
				Message.BROADCAST_GUILD_NOWAR.vars(vars).broadcast();
			}
			else {
				if(cmdGuild.isNoWarInvited(guild)) { //canceling invitation
					if(!nPlayer.hasPermission(GuildPermission.WAR_INVITE_CANCEL)) {
						Message.CHAT_GUILD_NOGUILDPERM.send(sender);
						return;
					}

					cmdGuild.removeNoWarInvitation(guild);
					Message.CHAT_GUILD_WAR_NOWAR_CANCEL_SUCCESS.clone().setVar(VarKey.GUILD_NAME, cmdGuild.getName()).send(sender);
					Message.CHAT_GUILD_WAR_NOWAR_CANCEL_NOTIFY.clone().setVar(VarKey.GUILD_NAME, guild.getName()).broadcast(cmdGuild);
				}
				else { //inviting to no-war
					if(!nPlayer.hasPermission(GuildPermission.WAR_INVITE_SEND)) {
						Message.CHAT_GUILD_NOGUILDPERM.send(sender);
						return;
					}

					cmdGuild.addNoWarInvitation(guild);
					vars.put(VarKey.GUILD_NAME, cmdGuild.getName());
					Message.CHAT_GUILD_WAR_NOWAR_INVITE_SUCCESS.vars(vars).send(sender);

					//notify the guild
					vars.clear();
					vars.put(VarKey.GUILD_NAME, guild.getName());
					Message.CHAT_GUILD_WAR_NOWAR_INVITE_NOTIFY.vars(vars).broadcast(cmdGuild);
				}
			}
		}
		else { //new war
			if(!nPlayer.hasPermission(GuildPermission.WAR_START)) {
				Message.CHAT_GUILD_NOGUILDPERM.send(sender);
				return;
			}

			if(guild.getName().equalsIgnoreCase(cmdGuild.getName())) {
				Message.CHAT_GUILD_WAR_YOURGUILDWAR.send(sender);
				return;
			}

			if(guild.isAlly(cmdGuild)) {
				Message.CHAT_GUILD_WAR_ALLY.send(sender);
				return;
			}

			guild.addWar(cmdGuild);
			cmdGuild.addWar(guild);

			//broadcasts
			Map<VarKey, String> vars = new HashMap<>();
			vars.put(VarKey.GUILD1, guild.getName());
			vars.put(VarKey.GUILD2, cmdGuild.getName());
			Message.BROADCAST_GUILD_WAR.vars(vars).broadcast();
			TagUtils.refresh();
			TabUtils.refresh();
			plugin.getRegionManager().checkRaidInit(nPlayer);
		}
	}

	@Override
	protected Collection<String> tabCompleteOptions(CommandSender sender, String[] args) {
		Set<String> options = new HashSet<>();
		NovaPlayer nPlayer = PlayerManager.getPlayer(sender);

		for(NovaGuild guild : NovaGuilds.getInstance().getGuildManager().getGuilds()) {
			if(!nPlayer.hasGuild() || !guild.equals(nPlayer.getGuild())) {
				options.add(guild.getTag().toLowerCase());
				options.add(guild.getName().toLowerCase());
			}
		}

		return options;
	}
}
