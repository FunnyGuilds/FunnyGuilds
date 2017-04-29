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

import co.marcin.novaguilds.api.basic.NovaGroup;
import co.marcin.novaguilds.api.basic.NovaGuild;
import co.marcin.novaguilds.api.basic.NovaPlayer;
import co.marcin.novaguilds.api.basic.NovaRegion;
import co.marcin.novaguilds.api.event.RegionCreateEvent;
import co.marcin.novaguilds.api.event.RegionResizeEvent;
import co.marcin.novaguilds.api.util.RegionSelection;
import co.marcin.novaguilds.command.abstractexecutor.AbstractCommandExecutor;
import co.marcin.novaguilds.enums.Config;
import co.marcin.novaguilds.enums.GuildPermission;
import co.marcin.novaguilds.enums.Message;
import co.marcin.novaguilds.enums.RegionMode;
import co.marcin.novaguilds.enums.RegionValidity;
import co.marcin.novaguilds.enums.VarKey;
import co.marcin.novaguilds.impl.basic.NovaGroupImpl;
import co.marcin.novaguilds.impl.basic.NovaRegionImpl;
import co.marcin.novaguilds.manager.GroupManager;
import co.marcin.novaguilds.manager.ListenerManager;
import co.marcin.novaguilds.manager.PlayerManager;
import co.marcin.novaguilds.util.RegionUtils;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;

import java.util.UUID;

public class CommandRegionBuy extends AbstractCommandExecutor {
	@Override
	public void execute(CommandSender sender, String[] args) throws Exception {
		NovaPlayer nPlayer = PlayerManager.getPlayer(sender);

		if(!nPlayer.hasGuild()) {
			Message.CHAT_GUILD_NOTINGUILD.send(sender);
			return;
		}

		NovaGuild guild = nPlayer.getGuild();

		if(!nPlayer.hasPermission(nPlayer.getPreferences().getRegionMode() == RegionMode.RESIZE ? GuildPermission.REGION_RESIZE : GuildPermission.REGION_CREATE)) {
			Message.CHAT_GUILD_NOGUILDPERM.send(sender);
			return;
		}

		RegionSelection activeSelection = nPlayer.getActiveSelection();

		if(activeSelection == null || !activeSelection.hasBothSelections()) {
			Message.CHAT_REGION_VALIDATION_NOTSELECTED.send(sender);
			return;
		}

		Location selectedLocation0 = activeSelection.getCorner(0);
		Location selectedLocation1 = activeSelection.getCorner(1);

		RegionValidity selectionValidity = plugin.getRegionManager().checkRegionSelect(activeSelection);

		if(selectionValidity != RegionValidity.VALID) {
			Message.CHAT_REGION_VALIDATION_NOTVALID.send(sender);
			return;
		}

		int regionSize = RegionUtils.checkRegionSize(selectedLocation0, selectedLocation1);

		if(guild.getRegions().size() >= Config.REGION_MAXAMOUNT.getInt() && nPlayer.getPreferences().getRegionMode() != RegionMode.RESIZE) {
			Message.CHAT_REGION_MAXAMOUNT.clone().setVar(VarKey.AMOUNT, Config.REGION_MAXAMOUNT.getInt()).send(nPlayer);
			return;
		}

		//region's price
		double price;
		NovaGroup group = GroupManager.getGroup(sender);
		double ppb = group.get(NovaGroupImpl.Key.REGION_PRICEPERBLOCK);

		if(nPlayer.getPreferences().getRegionMode() == RegionMode.RESIZE) {
			price = ppb * (regionSize - activeSelection.getSelectedRegion().getSurface());
		}
		else {
			price = ppb * regionSize + group.get(NovaGroupImpl.Key.REGION_CREATE_MONEY);
		}

		if(price > 0 && guild.getMoney() < price) {
			Message.CHAT_GUILD_NOTENOUGHMONEY.send(sender);
			return;
		}

		Cancellable event;
		NovaRegion region;

		if(nPlayer.getPreferences().getRegionMode() == RegionMode.RESIZE) {
			region = activeSelection.getSelectedRegion();

			event = new RegionResizeEvent(region, nPlayer, activeSelection, false);
			ListenerManager.getLoggedPluginManager().callEvent((Event) event);

			if(!event.isCancelled()) {
				region.setCorner(0, activeSelection.getCorner(0));
				region.setCorner(1, activeSelection.getCorner(1));
				plugin.getDynmapManager().updateRegion(region);

				Message.CHAT_REGION_RESIZE_SUCCESS.send(sender);
			}
		}
		else {
			region = new NovaRegionImpl(UUID.randomUUID(), nPlayer.getActiveSelection());
			event = new RegionCreateEvent(region, nPlayer, false);

			if(!event.isCancelled()) {
				nPlayer.getGuild().addRegion(region);
				plugin.getDynmapManager().addRegion(region);
				Message.CHAT_REGION_CREATED.send(sender);
			}
		}

		if(!event.isCancelled()) {
			if(price > 0) {
				guild.takeMoney(price);
			}

			nPlayer.cancelToolProgress();
			plugin.getRegionManager().checkAtRegionChange();
		}
	}
}
