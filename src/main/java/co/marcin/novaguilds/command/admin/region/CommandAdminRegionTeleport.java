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

package co.marcin.novaguilds.command.admin.region;

import co.marcin.novaguilds.api.basic.NovaPlayer;
import co.marcin.novaguilds.api.basic.NovaRegion;
import co.marcin.novaguilds.command.abstractexecutor.AbstractCommandExecutor;
import co.marcin.novaguilds.enums.Message;
import co.marcin.novaguilds.enums.Permission;
import co.marcin.novaguilds.enums.VarKey;
import co.marcin.novaguilds.manager.PlayerManager;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class CommandAdminRegionTeleport extends AbstractCommandExecutor.Reversed<NovaRegion> {
	@Override
	public void execute(CommandSender sender, String[] args) throws Exception {
		NovaRegion region = getParameter();
		NovaPlayer nPlayerOther;
		Player player;

		if(args.length == 0) {
			if(!(sender instanceof Player)) {
				Message.CHAT_CMDFROMCONSOLE.send(sender);
				return;
			}

			player = (Player) sender;
		}
		else { //other
			if(!Permission.NOVAGUILDS_ADMIN_REGION_TELEPORT_OTHER.has(sender)) {
				Message.CHAT_NOPERMISSIONS.send(sender);
				return;
			}

			nPlayerOther = PlayerManager.getPlayer(args[0]);

			if(nPlayerOther == null) {
				Message.CHAT_PLAYER_NOTEXISTS.send(sender);
				return;
			}

			if(!nPlayerOther.isOnline()) {
				Message.CHAT_PLAYER_NOTONLINE.send(sender);
				return;
			}

			player = nPlayerOther.getPlayer();
		}

		Map<VarKey, String> vars = new HashMap<>();
		vars.put(VarKey.GUILD_NAME, region.getGuild().getName());
		vars.put(VarKey.INDEX, String.valueOf(region.getIndex()));
		vars.put(VarKey.PLAYER_NAME, player.getName());

		Location location = region.getCenter().clone();
		location.setY(location.getWorld().getHighestBlockYAt(location));

		if(!player.equals(sender)) {
			Message.CHAT_ADMIN_REGION_TELEPORT_OTHER.clone().vars(vars).send(sender);
			Message.CHAT_ADMIN_REGION_TELEPORT_NOTIFYOTHER.clone().vars(vars).send(player);
		}
		else {
			Message.CHAT_ADMIN_REGION_TELEPORT_SELF.clone().vars(vars).send(sender);
		}

		player.teleport(location);
	}
}
