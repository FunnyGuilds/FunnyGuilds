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
import co.marcin.novaguilds.enums.Config;
import co.marcin.novaguilds.enums.Message;
import co.marcin.novaguilds.enums.Permission;
import co.marcin.novaguilds.enums.VarKey;
import co.marcin.novaguilds.manager.GuildManager;
import co.marcin.novaguilds.manager.MessageManager;
import co.marcin.novaguilds.manager.PlayerManager;
import co.marcin.novaguilds.util.NumberUtils;
import co.marcin.novaguilds.util.StringUtils;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class CommandGuildInfo extends AbstractCommandExecutor {
	@Override
	public void execute(CommandSender sender, String[] args) throws Exception {
		String guildName;
		NovaPlayer nPlayer = PlayerManager.getPlayer(sender);

		if(args.length > 0) {
			guildName = args[0];
		}
		else {
			if(!(sender instanceof Player)) {
				Message.CHAT_CMDFROMCONSOLE.send(sender);
				return;
			}

			if(nPlayer.hasGuild()) {
				guildName = nPlayer.getGuild().getName();
			}
			else {
				Message.CHAT_GUILD_NOTINGUILD.send(sender);
				return;
			}
		}

		//searching by name
		NovaGuild guild = GuildManager.getGuildFind(guildName);

		if(guild == null) {
			Message.CHAT_GUILD_NAMENOTEXIST.send(sender);
			return;
		}

		Map<VarKey, String> vars = new HashMap<>();
		List<String> guildInfoMessages;

		if((sender instanceof Player && nPlayer.hasGuild() && guild.isMember(nPlayer)) || Permission.NOVAGUILDS_ADMIN_GUILD_FULLINFO.has(sender)) {
			guildInfoMessages = Message.CHAT_GUILDINFO_FULLINFO.getList();
		}
		else {
			guildInfoMessages = Message.CHAT_GUILDINFO_INFO.getList();
		}

		MessageManager.sendPrefixMessage(sender, guildInfoMessages.get(0));

		int i;
		List<NovaPlayer> playerList = guild.getPlayers();
		String playerColor;

		//players list
		MessageWrapper playerRowFormat = Message.CHAT_GUILDINFO_ROW_PLAYER;
		Collection<MessageWrapper> playerNamesSet = new HashSet<>();

		if(!playerList.isEmpty()) {
			for(NovaPlayer nPlayerList : guild.getPlayers()) {
				if(nPlayerList.isOnline() && !plugin.getPlayerManager().isVanished(nPlayerList.getPlayer())) {
					playerColor = Message.CHAT_GUILDINFO_PLAYERCOLOR_ONLINE.get();
				}
				else {
					playerColor = Message.CHAT_GUILDINFO_PLAYERCOLOR_OFFLINE.get();
				}

				playerNamesSet.add(playerRowFormat
						.clone()
						.setVar(VarKey.COLOR, playerColor)
						.setVar(VarKey.PLAYER_NAME, nPlayerList.getName())
						.setVar(VarKey.LEADER_PREFIX, nPlayerList.isLeader() ? Message.CHAT_GUILDINFO_LEADERPREFIX.get() : "")
				);
			}
		}

		String players = StringUtils.join(playerNamesSet, Message.CHAT_GUILDINFO_PLAYERSEPARATOR);

		//allies
		String allies = "";
		if(!guild.getAllies().isEmpty()) {
			MessageWrapper allyFormat = Message.CHAT_GUILDINFO_ROW_ALLY;
			Collection<MessageWrapper> allySet = new HashSet<>();

			for(NovaGuild allyGuild : guild.getAllies()) {
				allySet.add(allyFormat.clone().setVar(VarKey.GUILD_NAME, allyGuild.getName()));
			}

			allies = StringUtils.join(allySet, Message.CHAT_GUILDINFO_PLAYERSEPARATOR);
		}

		//wars
		String wars = "";
		if(!guild.getWars().isEmpty()) {
			MessageWrapper warFormat = Message.CHAT_GUILDINFO_ROW_WAR;
			final Collection<MessageWrapper> warSet = new HashSet<>();

			for(NovaGuild war : guild.getWars()) {
				warSet.add(warFormat.clone().setVar(VarKey.GUILD_NAME, war.getName()));
			}

			wars = StringUtils.join(warSet, Message.CHAT_GUILDINFO_PLAYERSEPARATOR);
		}

		vars.put(VarKey.RANK, "");
		vars.put(VarKey.GUILD_NAME, guild.getName());
		vars.put(VarKey.LEADER, guild.getLeader().getName());
		vars.put(VarKey.TAG, guild.getTag());
		vars.put(VarKey.MONEY, String.valueOf(guild.getMoney()));
		vars.put(VarKey.PLAYERS, players);
		vars.put(VarKey.PLAYERSCOUNT, String.valueOf(guild.getPlayers().size()));
		vars.put(VarKey.SLOTS, String.valueOf(guild.getSlots()));
		vars.put(VarKey.GUILD_POINTS, String.valueOf(guild.getPoints()));
		vars.put(VarKey.GUILD_LIVES, String.valueOf(guild.getLives()));
		vars.put(VarKey.GUILD_OPENINVITATION, Message.getOnOff(guild.isOpenInvitation()));

		//live regeneration time
		long liveRegenerationTime = Config.LIVEREGENERATION_REGENTIME.getSeconds() - (NumberUtils.systemSeconds() - guild.getLostLiveTime());
		String liveRegenerationString = StringUtils.secondsToString(liveRegenerationTime);

		long timeWait = (guild.getTimeRest() + Config.RAID_TIMEREST.getSeconds()) - NumberUtils.systemSeconds();

		vars.put(VarKey.LIVEREGENERATIONTIME, liveRegenerationString);
		vars.put(VarKey.GUILD_TIME_REST, StringUtils.secondsToString(timeWait));

		//time created and protection
		long createdAgo = NumberUtils.systemSeconds() - guild.getTimeCreated();
		long protectionLeft = Config.GUILD_CREATEPROTECTION.getSeconds() - createdAgo;

		vars.put(VarKey.CREATEDAGO, StringUtils.secondsToString(createdAgo, TimeUnit.HOURS));
		vars.put(VarKey.PROTLEFT, StringUtils.secondsToString(protectionLeft, TimeUnit.HOURS));

		//home location coordinates
		Location homeLocation = guild.getHome();
		if(homeLocation != null) {
			vars.put(VarKey.X, String.valueOf(homeLocation.getBlockX()));
			vars.put(VarKey.Y, String.valueOf(homeLocation.getBlockY()));
			vars.put(VarKey.Z, String.valueOf(homeLocation.getBlockZ()));
		}

		//put wars and allies into vars
		vars.put(VarKey.ALLIES, allies);
		vars.put(VarKey.WARS, wars);

		for(i = 1; i < guildInfoMessages.size(); i++) {
			boolean skip = false;
			String guildInfoMessage = guildInfoMessages.get(i);

			//lost live
			if(liveRegenerationTime <= 0 && guildInfoMessage.contains(VarKey.LIVEREGENERATIONTIME.getNameWithBrackets())) {
				skip = true;
			}

			//Time rest
			if(timeWait <= 0 && guildInfoMessage.contains(VarKey.GUILD_TIME_REST.getNameWithBrackets())) {
				skip = true;
			}

			//home location
			if((guildInfoMessage.contains(VarKey.X.getNameWithBrackets())
					|| guildInfoMessage.contains(VarKey.Y.getNameWithBrackets())
					|| guildInfoMessage.contains(VarKey.Z.getNameWithBrackets()))
					&& guild.getHome() == null) {
				skip = true;
			}

			//allies
			if(guildInfoMessage.contains(VarKey.ALLIES.getNameWithBrackets()) && allies.isEmpty()) {
				skip = true;
			}

			//displaying wars
			if(guildInfoMessage.contains(VarKey.WARS.getNameWithBrackets()) && wars.isEmpty()) {
				skip = true;
			}

			if(guildInfoMessage.contains(VarKey.PROTLEFT.getNameWithBrackets()) && protectionLeft <= 0) {
				skip = true;
			}

			if(guildInfoMessage.contains(VarKey.CREATEDAGO.getNameWithBrackets()) && protectionLeft > 0) {
				skip = true;
			}

			if(!skip) {
				guildInfoMessage = MessageManager.replaceVarKeyMap(guildInfoMessage, vars);
				MessageManager.sendMessage(sender, guildInfoMessage);
			}
		}
	}

	@Override
	protected Collection<String> tabCompleteOptions(CommandSender sender, String[] args) {
		Set<String> options = new HashSet<>();

		for(NovaGuild guild : NovaGuilds.getInstance().getGuildManager().getGuilds()) {
			options.add(guild.getTag().toLowerCase());
			options.add(guild.getName().toLowerCase());
		}

		int limit = 0;
		for(NovaPlayer nPlayerLoop : NovaGuilds.getInstance().getPlayerManager().getPlayers()) {
			if(limit > 100) {
				break;
			}

			if(!nPlayerLoop.getName().startsWith(args[args.length - 1])) {
				continue;
			}

			if(!nPlayerLoop.hasGuild()) {
				continue;
			}

			options.add(nPlayerLoop.getName());
			limit++;
		}

		return options;
	}
}
