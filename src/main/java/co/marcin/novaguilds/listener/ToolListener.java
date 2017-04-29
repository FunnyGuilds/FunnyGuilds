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

package co.marcin.novaguilds.listener;

import co.marcin.novaguilds.api.basic.MessageWrapper;
import co.marcin.novaguilds.api.basic.NovaGroup;
import co.marcin.novaguilds.api.basic.NovaPlayer;
import co.marcin.novaguilds.api.basic.NovaRegion;
import co.marcin.novaguilds.api.util.RegionSelection;
import co.marcin.novaguilds.enums.Config;
import co.marcin.novaguilds.enums.GuildPermission;
import co.marcin.novaguilds.enums.Message;
import co.marcin.novaguilds.enums.Permission;
import co.marcin.novaguilds.enums.RegionMode;
import co.marcin.novaguilds.enums.RegionValidity;
import co.marcin.novaguilds.enums.VarKey;
import co.marcin.novaguilds.impl.basic.NovaGroupImpl;
import co.marcin.novaguilds.impl.util.AbstractListener;
import co.marcin.novaguilds.impl.util.RegionSelectionImpl;
import co.marcin.novaguilds.manager.GroupManager;
import co.marcin.novaguilds.manager.PlayerManager;
import co.marcin.novaguilds.manager.RegionManager;
import co.marcin.novaguilds.util.ItemStackUtils;
import co.marcin.novaguilds.util.RegionUtils;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class ToolListener extends AbstractListener {
	/**
	 * Handles all tool actions
	 *
	 * @param event player interact event
	 */
	@EventHandler
	public void onClick(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		Map<VarKey, String> vars = new HashMap<>();

		if(!ItemStackUtils.isSimilar(event.getItem(), Config.REGION_TOOL.getItemStack())) {
			return;
		}

		event.setCancelled(true);
		NovaPlayer nPlayer = PlayerManager.getPlayer(player);

		Location pointedLocation;
		try {
			pointedLocation = player.getTargetBlock((HashSet<Byte>) null, 200).getLocation();
		}
		catch(IllegalStateException ignored) {
			//This happens.
			return;
		}

		Action action = event.getAction();
		RegionSelection.Type selectionType = RegionSelection.Type.NONE;
		pointedLocation.setWorld(player.getWorld());
		NovaRegion region = RegionManager.get(pointedLocation);
		Location selectedLocation[] = new Location[2];

		if(nPlayer.getActiveSelection() != null && nPlayer.getActiveSelection().getType() != RegionSelection.Type.HIGHLIGHT) {
			selectedLocation[0] = nPlayer.getActiveSelection().getCorner(0);
			selectedLocation[1] = nPlayer.getActiveSelection().getCorner(1);
		}

		//Change RegionMode
		if((action == Action.LEFT_CLICK_AIR || action == Action.LEFT_CLICK_BLOCK) && player.isSneaking()) {
			if(!Permission.NOVAGUILDS_TOOL_CHECK.has(player) || !Permission.NOVAGUILDS_REGION_CREATE.has(player)) {
				return;
			}

			nPlayer.getPreferences().setRegionMode(nPlayer.getPreferences().getRegionMode() == RegionMode.CHECK ? RegionMode.SELECT : RegionMode.CHECK);
			nPlayer.cancelToolProgress();

			//highlight corners for resizing
			if(nPlayer.isAtRegion()
					&& nPlayer.getPreferences().getRegionMode() == RegionMode.SELECT
					&& nPlayer.hasPermission(GuildPermission.REGION_RESIZE)
					&& nPlayer.getGuild().ownsRegion(nPlayer.getAtRegion())) {
				selectionType = RegionSelection.Type.HIGHLIGHT_RESIZE;
				selectedLocation[0] = nPlayer.getAtRegion().getCorner(0);
				selectedLocation[1] = nPlayer.getAtRegion().getCorner(1);
			}

			MessageWrapper mode = nPlayer.getPreferences().getRegionMode() == RegionMode.SELECT ? Message.CHAT_REGION_TOOL_MODES_SELECT : Message.CHAT_REGION_TOOL_MODES_CHECK;

			vars.put(VarKey.MODE, mode.get());
			Message.CHAT_REGION_TOOL_TOGGLEDMODE.clone().vars(vars).send(nPlayer);
		}
		else if(nPlayer.getPreferences().getRegionMode() == RegionMode.CHECK && (action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK)) { //CHECK MODE
			if(!Permission.NOVAGUILDS_TOOL_CHECK.has(player)) {
				return;
			}

			//Reset region highlighted already
			if(nPlayer.getActiveSelection() != null
					&& (nPlayer.getActiveSelection().getType() == RegionSelection.Type.HIGHLIGHT
						|| nPlayer.getActiveSelection().getType() == RegionSelection.Type.HIGHLIGHT_RESIZE)) {
				nPlayer.getActiveSelection().reset();
			}

			if(region != null) {
				selectionType = RegionSelection.Type.HIGHLIGHT;
				selectedLocation[0] = region.getCorner(0);
				selectedLocation[1] = region.getCorner(1);

				vars.put(VarKey.GUILD_NAME, region.getGuild().getName());
				vars.put(VarKey.INDEX, String.valueOf(region.getIndex()));
				Message.CHAT_REGION_BELONGSTO.clone().vars(vars).send(nPlayer);
			}
			else {
				Message.CHAT_REGION_NOREGIONHERE.send(nPlayer);
			}
		}
		else if(event.getAction() != Action.PHYSICAL && nPlayer.getPreferences().getRegionMode() != RegionMode.CHECK) { //CREATE MODE
			Location pointedCornerLocation = pointedLocation.clone();
			pointedCornerLocation.setY(0);
			double[] cornerDistance = new double[]{
					region == null ? 1 : pointedCornerLocation.distance(region.getCorner(0).getBlock().getLocation()),
					region == null ? 1 : pointedCornerLocation.distance(region.getCorner(1).getBlock().getLocation())
			};

			if(region != null
					&& nPlayer.getPreferences().getRegionMode() != RegionMode.RESIZE
					&& (cornerDistance[0] < 1 || cornerDistance[1] < 1)
					&& Permission.NOVAGUILDS_REGION_RESIZE.has(player)
					&& region.getGuild().isMember(nPlayer)
					&& nPlayer.hasPermission(GuildPermission.REGION_RESIZE)
					&& nPlayer.getActiveSelection() != null
					&& nPlayer.getActiveSelection().getType() == RegionSelection.Type.HIGHLIGHT_RESIZE) { //resizing
				selectionType = RegionSelection.Type.RESIZE;
				nPlayer.getActiveSelection().setResizingCorner(cornerDistance[0] < 1 ? 0 : 1);
				nPlayer.getPreferences().setRegionMode(RegionMode.RESIZE);
				Message.CHAT_REGION_RESIZE_START.send(nPlayer);
			}
			else {
				selectionType = nPlayer.getPreferences().getRegionMode() == RegionMode.RESIZE ? RegionSelection.Type.RESIZE : RegionSelection.Type.CREATE;

				if(!Permission.NOVAGUILDS_REGION_CREATE.has(player)) {
					return;
				}

				int selectCorner = -1;

				if(nPlayer.getActiveSelection() != null && nPlayer.getPreferences().getRegionMode() == RegionMode.RESIZE) {
					//Toggle corner
					if(action == Action.LEFT_CLICK_AIR || action == Action.LEFT_CLICK_BLOCK) {
						nPlayer.getActiveSelection().setResizingCorner(nPlayer.getActiveSelection().getResizingCorner() == 0 ? 1 : 0);
						Message.CHAT_REGION_RESIZE_TOGGLE.send(player);
					}
					else { //select
						selectCorner = nPlayer.getActiveSelection().getResizingCorner();
					}
				}
				else {
					//Corner 0
					if(action == Action.LEFT_CLICK_AIR || action == Action.LEFT_CLICK_BLOCK) {
						selectCorner = 0;
					}
					else { //Corner 1
						selectCorner = 1;
					}
				}

				if(selectCorner != -1) {
					int oppositeCorner = selectCorner == 0 ? 1 : 0;

					if(selectedLocation[oppositeCorner] != null && !pointedLocation.getWorld().equals(selectedLocation[oppositeCorner].getWorld())) {
						nPlayer.cancelToolProgress();
						return;
					}

					selectedLocation[selectCorner] = pointedLocation;
				}
			}
		}

		if(selectionType != RegionSelection.Type.NONE) {
			RegionSelection selection;

			if(nPlayer.getActiveSelection() != null && nPlayer.getActiveSelection().getType() == selectionType) {
				selection = nPlayer.getActiveSelection();
			}
			else {
				RegionSelection activeSelection = nPlayer.getActiveSelection();
				selection = new RegionSelectionImpl(nPlayer, selectionType, region);

				if(activeSelection != null) {
					if(selectionType == RegionSelection.Type.RESIZE) {
						selection.setResizingCorner(activeSelection.getResizingCorner());
					}

					activeSelection.reset();
				}
			}

			selection.setCorner(0, selectedLocation[0]);
			selection.setCorner(1, selectedLocation[1]);

			if(selection.hasBothSelections()
					&& selectionType != RegionSelection.Type.HIGHLIGHT
					&& selectionType != RegionSelection.Type.HIGHLIGHT_RESIZE) {
				RegionValidity regionValidity = plugin.getRegionManager().checkRegionSelect(selection);
				switch(regionValidity) {
					case VALID:
						if(nPlayer.hasGuild()) {
							int regionSize = RegionUtils.checkRegionSize(selectedLocation[0], selectedLocation[1]);
							NovaGroup group = GroupManager.getGroup(nPlayer.getPlayer());
							double price;
							double ppb = group.get(NovaGroupImpl.Key.REGION_PRICEPERBLOCK);

							if(nPlayer.getPreferences().getRegionMode() == RegionMode.RESIZE) {
								price = ppb * (regionSize - selection.getSelectedRegion().getSurface());
							}
							else {
								price = ppb * regionSize + group.get(NovaGroupImpl.Key.REGION_CREATE_MONEY);
							}

							vars.put(VarKey.SIZE, String.valueOf(regionSize));
							vars.put(VarKey.PRICE, String.valueOf(price));

							Message.CHAT_REGION_SIZE.clone().vars(vars).send(nPlayer);

							if(price > 0) {
								Message.CHAT_REGION_PRICE.clone().vars(vars).send(nPlayer);

								if(!nPlayer.getGuild().hasMoney(price)) {
									vars.put(VarKey.NEEDMORE, String.valueOf(price - nPlayer.getGuild().getMoney()));
									Message.CHAT_REGION_CNOTAFFORD.clone().vars(vars).send(nPlayer);
									break;
								}
							}

							Message.CHAT_REGION_VALIDATION_VALID.send(nPlayer);
						}
						else {
							Message.CHAT_REGION_MUSTVEGUILD.send(nPlayer);
						}
						break;
					case TOOSMALL:
						vars.put(VarKey.MINSIZE, Config.REGION_MINSIZE.getString());
						Message.CHAT_REGION_VALIDATION_TOOSMALL.clone().vars(vars).send(nPlayer);
						break;
					case TOOBIG:
						vars.put(VarKey.MAXSIZE, Config.REGION_MAXSIZE.getString());
						Message.CHAT_REGION_VALIDATION_TOOBIG.clone().vars(vars).send(nPlayer);
						break;
					case OVERLAPS:
						Message.CHAT_REGION_VALIDATION_OVERLAPS.send(nPlayer);
						break;
					case TOOCLOSE:
						Message.CHAT_REGION_VALIDATION_TOOCLOSE.send(nPlayer);
						break;
					case WORLDGUARD:
						Message.CHAT_REGION_VALIDATION_WORLDGUARD.send(nPlayer);
						break;
				}

				selection.setValidity(regionValidity);
			}

			if(selection.hasBothSelections()
					&& selection.getValidity() == RegionValidity.TOOBIG
					&& (selection.getWidth() > Config.REGION_MAXSIZE.getInt() * 1.5 || selection.getLength() > Config.REGION_MAXSIZE.getInt() * 1.5)) {
				if(selection.isSent()) {
					RegionSelection activeSelection = nPlayer.getActiveSelection();
					selection.reset();

					if(selection.equals(activeSelection)) {
						nPlayer.setActiveSelection(selection);
					}
				}
				return;
			}
			selection.send();
		}
	}
}
