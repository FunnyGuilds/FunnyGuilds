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

package co.marcin.novaguilds.command.admin;

import co.marcin.novaguilds.command.abstractexecutor.AbstractCommandExecutor;
import co.marcin.novaguilds.enums.Message;
import co.marcin.novaguilds.util.LoggerUtils;
import org.bukkit.command.CommandSender;

public class CommandAdminSave extends AbstractCommandExecutor {
	@Override
	public void execute(CommandSender sender, String[] args) throws Exception {
		if(args.length == 1) {
			switch(args[0].toLowerCase()) {
				case "players":
					plugin.getPlayerManager().save();
					Message.CHAT_ADMIN_SAVE_PLAYERS.send(sender);
					LoggerUtils.info("Saved players");
					break;
				case "guilds":
					plugin.getGuildManager().save();
					Message.CHAT_ADMIN_SAVE_GUILDS.send(sender);
					LoggerUtils.info("Saved guilds");
					break;
				case "regions":
					plugin.getRegionManager().save();
					Message.CHAT_ADMIN_SAVE_REGIONS.send(sender);
					LoggerUtils.info("Saved regions");
					break;
				case "ranks":
					plugin.getRankManager().save();
					Message.CHAT_ADMIN_SAVE_RANKS.send(sender);
					LoggerUtils.info("Saved ranks");
					break;
				default:
					Message.CHAT_INVALIDPARAM.send(sender);
					break;
			}
		}
		else { //save all
			plugin.getRegionManager().save();
			plugin.getGuildManager().save();
			plugin.getPlayerManager().save();
			plugin.getRankManager().save();
			Message.CHAT_ADMIN_SAVE_ALL.send(sender);
			LoggerUtils.info("Saved all data");
		}
	}
}
