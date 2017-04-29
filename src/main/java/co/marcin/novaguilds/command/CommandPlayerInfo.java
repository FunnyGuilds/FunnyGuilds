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

package co.marcin.novaguilds.command;

import co.marcin.novaguilds.api.basic.NovaPlayer;
import co.marcin.novaguilds.command.abstractexecutor.AbstractCommandExecutor;
import co.marcin.novaguilds.enums.Message;
import co.marcin.novaguilds.manager.PlayerManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandPlayerInfo extends AbstractCommandExecutor {
	@Override
	public void execute(CommandSender sender, String[] args) throws Exception {
		NovaPlayer nCPlayer;
		if(args.length == 0) {
			if(!(sender instanceof Player)) {
				Message.CHAT_CMDFROMCONSOLE.send(sender);
				return;
			}

			nCPlayer = PlayerManager.getPlayer(sender);
		}
		else {
			nCPlayer = PlayerManager.getPlayer(args[0]);

			if(nCPlayer == null) {
				Message.CHAT_PLAYER_NOTEXISTS.send(sender);
				return;
			}
		}

		plugin.getPlayerManager().sendPlayerInfo(sender, nCPlayer);
	}
}
