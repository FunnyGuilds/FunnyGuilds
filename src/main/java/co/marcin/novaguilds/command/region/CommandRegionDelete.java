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

package co.marcin.novaguilds.command.region;

import co.marcin.novaguilds.api.basic.NovaPlayer;
import co.marcin.novaguilds.api.basic.NovaRegion;
import co.marcin.novaguilds.api.event.RegionDeleteEvent;
import co.marcin.novaguilds.command.abstractexecutor.AbstractCommandExecutor;
import co.marcin.novaguilds.enums.GuildPermission;
import co.marcin.novaguilds.enums.Message;
import co.marcin.novaguilds.manager.ListenerManager;
import co.marcin.novaguilds.manager.PlayerManager;
import co.marcin.novaguilds.util.NumberUtils;
import org.bukkit.command.CommandSender;

public class CommandRegionDelete extends AbstractCommandExecutor {
	@Override
	public void execute(CommandSender sender, String[] args) throws Exception {
		NovaPlayer nPlayer = PlayerManager.getPlayer(sender);

		if(!nPlayer.hasGuild()) {
			Message.CHAT_GUILD_NOTINGUILD.send(sender);
			return;
		}

		if(!nPlayer.hasPermission(GuildPermission.REGION_REMOVE)) {
			Message.CHAT_GUILD_NOGUILDPERM.send(sender);
			return;
		}

		if(!nPlayer.getGuild().hasRegion()) {
			Message.CHAT_GUILD_HASNOREGION.send(sender);
			return;
		}

		NovaRegion region;
		if(args.length == 0) {
			region = nPlayer.getAtRegion();
		}
		else {
			String indexString = args[0];

			if(!NumberUtils.isNumeric(indexString)) {
				Message.CHAT_ENTERINTEGER.send(sender);
				return;
			}

			int index = Integer.parseInt(indexString);

			region = nPlayer.getGuild().getRegion(index);
		}

		if(region == null) {
			Message.CHAT_REGION_NOTFOUND.send(sender);
			return;
		}

		RegionDeleteEvent event = new RegionDeleteEvent(region, RegionDeleteEvent.Cause.DELETE);
		ListenerManager.getLoggedPluginManager().callEvent(event);

		if(!event.isCancelled()) {
			Message.CHAT_REGION_DELETED.send(sender);
			plugin.getRegionManager().remove(region);
			plugin.getDynmapManager().removeRegion(region);
		}
	}
}
