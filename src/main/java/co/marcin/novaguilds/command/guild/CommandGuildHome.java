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
import co.marcin.novaguilds.api.basic.NovaRegion;
import co.marcin.novaguilds.command.abstractexecutor.AbstractCommandExecutor;
import co.marcin.novaguilds.enums.GuildPermission;
import co.marcin.novaguilds.enums.Message;
import co.marcin.novaguilds.enums.Permission;
import co.marcin.novaguilds.enums.VarKey;
import co.marcin.novaguilds.impl.basic.NovaGroupImpl;
import co.marcin.novaguilds.manager.GroupManager;
import co.marcin.novaguilds.manager.PlayerManager;
import co.marcin.novaguilds.manager.RegionManager;
import co.marcin.novaguilds.util.InventoryUtils;
import co.marcin.novaguilds.util.StringUtils;
import co.marcin.novaguilds.util.TabUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class CommandGuildHome extends AbstractCommandExecutor {
	@Override
	public void execute(CommandSender sender, String[] args) throws Exception {
		NovaPlayer nPlayer = PlayerManager.getPlayer(sender);

		if(!nPlayer.hasGuild()) {
			Message.CHAT_GUILD_NOTINGUILD.send(sender);
			return;
		}

		Player player = (Player) sender;

		if(args.length > 0 && args[0].equalsIgnoreCase("set")) {
			if(!Permission.NOVAGUILDS_GUILD_HOME_SET.has(sender)) {
				Message.CHAT_NOPERMISSIONS.send(sender);
				return;
			}

			if(!nPlayer.hasPermission(GuildPermission.HOME_SET)) {
				Message.CHAT_GUILD_NOGUILDPERM.send(sender);
				return;
			}

			NovaRegion region = RegionManager.get(player);

			if(region == null && nPlayer.getGuild().hasRegion()) {
				Message.CHAT_GUILD_SETHOME_OUTSIDEREGION.send(sender);
				return;
			}

			if(region != null && !region.getGuild().isMember(nPlayer)) {
				Message.CHAT_GUILD_SETHOME_OVERLAPS.send(sender);
				return;
			}

			nPlayer.getGuild().setHome(player.getLocation());
			Message.CHAT_GUILD_SETHOME_SUCCESS.send(sender);
			TabUtils.refresh(nPlayer.getGuild());
			plugin.getDynmapManager().updateGuildHome(nPlayer.getGuild());
		}
		else {
			if(!nPlayer.hasPermission(GuildPermission.HOME_TELEPORT)) {
				Message.CHAT_GUILD_NOGUILDPERM.send(sender);
				return;
			}

			//items
			List<ItemStack> homeItems = GroupManager.getGroup(sender).get(NovaGroupImpl.Key.HOME_ITEMS);

			if(!homeItems.isEmpty()) {
				List<ItemStack> missingItems = InventoryUtils.getMissingItems(player.getInventory(), homeItems);

				if(!missingItems.isEmpty()) {
					Message.CHAT_CREATEGUILD_NOITEMS.send(sender);
					sender.sendMessage(StringUtils.getItemList(missingItems));

					return;
				}
			}

			//money
			double homeMoney = GroupManager.getGroup(sender).get(NovaGroupImpl.Key.HOME_MONEY);
			if(homeMoney > 0 && !nPlayer.hasMoney(homeMoney)) {
				Message.CHAT_GUILD_NOTENOUGHMONEY.clone().setVar(VarKey.REQUIREDMONEY, homeMoney).send(sender);
				return;
			}

			nPlayer.takeMoney(homeMoney);
			InventoryUtils.removeItems(player, homeItems);
			plugin.getGuildManager().delayedTeleport(player, nPlayer.getGuild().getHome(), Message.CHAT_GUILD_HOME);
		}
	}
}
