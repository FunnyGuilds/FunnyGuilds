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

import co.marcin.novaguilds.api.basic.MessageWrapper;
import co.marcin.novaguilds.api.basic.NovaPlayer;
import co.marcin.novaguilds.command.abstractexecutor.AbstractCommandExecutor;
import co.marcin.novaguilds.enums.GuildPermission;
import co.marcin.novaguilds.enums.Message;
import co.marcin.novaguilds.manager.PlayerManager;
import co.marcin.novaguilds.util.TabUtils;
import org.bukkit.command.CommandSender;

public class CommandGuildSetName extends AbstractCommandExecutor {
	@Override
	public void execute(CommandSender sender, String[] args) throws Exception {
		NovaPlayer nPlayer = PlayerManager.getPlayer(sender);

		if(!nPlayer.hasGuild()) {
			Message.CHAT_GUILD_NOTINGUILD.send(sender);
			return;
		}

		if(!nPlayer.hasPermission(GuildPermission.SET_NAME)) {
			Message.CHAT_GUILD_NOGUILDPERM.send(sender);
			return;
		}

		if(args.length == 0) {
			Message.CHAT_GUILD_ENTERNAME.send(sender);
			return;
		}

		String newName = args[0];

		MessageWrapper validity = CommandGuildCreate.validName(newName);

		if(validity != null) {
			validity.send(sender);
			return;
		}

		plugin.getGuildManager().changeName(nPlayer.getGuild(), newName);
		Message.CHAT_GUILD_SET_NAME.send(sender);
		TabUtils.refresh(nPlayer.getGuild());
		plugin.getDynmapManager().updateGuild(nPlayer.getGuild());
	}
}
