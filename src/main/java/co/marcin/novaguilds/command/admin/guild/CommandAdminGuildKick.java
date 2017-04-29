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

package co.marcin.novaguilds.command.admin.guild;

import co.marcin.novaguilds.api.basic.NovaGuild;
import co.marcin.novaguilds.api.basic.NovaPlayer;
import co.marcin.novaguilds.command.abstractexecutor.AbstractCommandExecutor;
import co.marcin.novaguilds.enums.Message;
import co.marcin.novaguilds.enums.VarKey;
import co.marcin.novaguilds.manager.PlayerManager;
import co.marcin.novaguilds.util.TabUtils;
import co.marcin.novaguilds.util.TagUtils;
import org.bukkit.command.CommandSender;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class CommandAdminGuildKick extends AbstractCommandExecutor {
	@Override
	public void execute(CommandSender sender, String[] args) throws Exception {
		if(args.length == 0) { //no playername
			Message.CHAT_PLAYER_ENTERNAME.send(sender);
			return;
		}

		NovaPlayer nPlayerKick = PlayerManager.getPlayer(args[0]);

		if(nPlayerKick == null) { //no player
			Message.CHAT_PLAYER_NOTEXISTS.send(sender);
			return;
		}

		if(!nPlayerKick.hasGuild()) {
			Message.CHAT_PLAYER_HASNOGUILD.send(sender);
			return;
		}

		NovaGuild guild = nPlayerKick.getGuild();

		if(nPlayerKick.isLeader()) {
			Message.CHAT_ADMIN_GUILD_KICK_LEADER.send(sender);
			return;
		}

		//all passed
		guild.removePlayer(nPlayerKick);

		Map<VarKey, String> vars = new HashMap<>();
		vars.put(VarKey.PLAYER_NAME, nPlayerKick.getName());
		vars.put(VarKey.GUILD_NAME, guild.getName());
		Message.BROADCAST_GUILD_KICKED.clone().vars(vars).broadcast();

		//tab/tag
		TagUtils.refresh();
		TabUtils.refresh();
		nPlayerKick.cancelToolProgress();
	}

	@Override
	protected Collection<String> tabCompleteOptions(CommandSender sender, String[] args) {
		Set<String> options = new HashSet<>();
		NovaPlayer nPlayer = PlayerManager.getPlayer(sender);

		for(NovaPlayer guildMember : plugin.getPlayerManager().getOnlinePlayers()) {
			if(!guildMember.isLeader() && !guildMember.equals(nPlayer)) {
				options.add(guildMember.getName());
			}
		}

		return options;
	}
}
