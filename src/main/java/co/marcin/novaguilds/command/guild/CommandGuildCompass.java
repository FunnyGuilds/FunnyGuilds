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

import co.marcin.novaguilds.api.basic.NovaPlayer;
import co.marcin.novaguilds.command.abstractexecutor.AbstractCommandExecutor;
import co.marcin.novaguilds.enums.Message;
import co.marcin.novaguilds.manager.PlayerManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandGuildCompass extends AbstractCommandExecutor {
	@Override
	public void execute(CommandSender sender, String[] args) throws Exception {
		Player player = (Player) sender;
		NovaPlayer nPlayer = PlayerManager.getPlayer(sender);

		if(!nPlayer.hasGuild()) {
			Message.CHAT_GUILD_NOTINGUILD.send(sender);
			return;
		}

		if(nPlayer.getPreferences().isCompassPointingGuild()) { //disable
			nPlayer.getPreferences().setCompassPointingGuild(false);
			player.setCompassTarget(player.getWorld().getSpawnLocation());
			Message.CHAT_GUILD_COMPASSTARGET_OFF.send(sender);
		}
		else { //enable
			nPlayer.getPreferences().setCompassPointingGuild(true);
			player.setCompassTarget(nPlayer.getGuild().getHome());
			Message.CHAT_GUILD_COMPASSTARGET_ON.send(sender);
		}
	}
}
