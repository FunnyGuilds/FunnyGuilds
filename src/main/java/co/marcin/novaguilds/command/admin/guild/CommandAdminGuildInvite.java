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
import org.bukkit.command.CommandSender;

import java.util.HashMap;
import java.util.Map;

public class CommandAdminGuildInvite extends AbstractCommandExecutor.Reversed<NovaGuild> {
	@Override
	public void execute(CommandSender sender, String[] args) throws Exception {
		NovaGuild guild = getParameter();

		if(args.length == 0) {
			Message.CHAT_PLAYER_ENTERNAME.send(sender);
			return;
		}

		NovaPlayer nPlayer = PlayerManager.getPlayer(args[0]);

		if(nPlayer == null) {
			Message.CHAT_PLAYER_NOTEXISTS.send(sender);
			return;
		}

		if(nPlayer.hasGuild()) {
			Message.CHAT_PLAYER_HASGUILD.send(sender);
			return;
		}

		if(nPlayer.isInvitedTo(guild)) {
			Message.CHAT_PLAYER_ALREADYINVITED.send(sender);
			return;
		}

		//all passed
		nPlayer.addInvitation(guild);

		Map<VarKey, String> vars = new HashMap<>();
		vars.put(VarKey.PLAYER_NAME, nPlayer.getName());
		vars.put(VarKey.GUILD_NAME, guild.getName());
		Message.CHAT_ADMIN_GUILD_INVITED.clone().vars(vars).send(sender);

		if(nPlayer.isOnline()) {
			Message.CHAT_PLAYER_INVITE_NOTIFY.clone().vars(vars).send(nPlayer);
		}
	}
}
