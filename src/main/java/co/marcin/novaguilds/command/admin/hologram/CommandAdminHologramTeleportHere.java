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

package co.marcin.novaguilds.command.admin.hologram;

import co.marcin.novaguilds.api.basic.NovaHologram;
import co.marcin.novaguilds.command.abstractexecutor.AbstractCommandExecutor;
import co.marcin.novaguilds.enums.Message;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class CommandAdminHologramTeleportHere extends AbstractCommandExecutor.Reversed<NovaHologram> {
	@Override
	public void execute(CommandSender sender, String[] args) throws Exception {
		Location targetLocation;

		if(args.length == 0) {
			targetLocation = ((Player) sender).getLocation();
		}
		else if(args.length == 1) {
			Player player = Bukkit.getPlayer(args[0]);

			if(player == null) {
				player = Bukkit.getPlayer(UUID.fromString(args[0]));
			}

			if(player == null) {
				Message.CHAT_PLAYER_NOTEXISTS.send(sender);
				return;
			}

			targetLocation = player.getLocation();
		}
		else if(args.length == 3 || args.length == 4) {
			float x, y, z;

			try {
				x = Float.parseFloat(args[0]);
				y = Float.parseFloat(args[1]);
				z = Float.parseFloat(args[2]);
			}
			catch(NumberFormatException e) {
				Message.CHAT_ENTERINTEGER.send(sender);
				return;
			}

			//Disallow performing from the console without a world provided
			if(args.length == 3 && !(sender instanceof Player)) {
				Message.CHAT_CMDFROMCONSOLE.send(sender);
				return;
			}

			//Set the world
			World world;
			if(args.length == 4) {
				world = Bukkit.getWorld(args[3]);

				//Try by uuid
				if(world == null) {
					world = Bukkit.getWorld(UUID.fromString(args[3]));
				}

				if(world == null) {
					Message.CHAT_INVALIDPARAM.send(sender);
					return;
				}
			}
			else {
				world = getParameter().getLocation().getWorld();
			}

			targetLocation = new Location(world, x, y, z);
		}
		else {
			Message.CHAT_UNKNOWNCMD.send(sender);
			return;
		}

		Validate.notNull(targetLocation);
		getParameter().teleport(targetLocation);
	}
}
