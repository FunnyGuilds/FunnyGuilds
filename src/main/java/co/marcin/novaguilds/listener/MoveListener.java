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

import co.marcin.novaguilds.api.basic.NovaPlayer;
import co.marcin.novaguilds.api.basic.NovaRegion;
import co.marcin.novaguilds.impl.util.AbstractListener;
import co.marcin.novaguilds.manager.PlayerManager;
import co.marcin.novaguilds.manager.RegionManager;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

public class MoveListener extends AbstractListener {
	@EventHandler
	public void onPlayerMove(PlayerMoveEvent event) {
		execute(event);
	}

	@EventHandler
	public void onPlayerTeleport(PlayerTeleportEvent event) {
		execute(event);
	}

	@EventHandler
	public void onPlayerChangedWorld(PlayerChangedWorldEvent event) {
		PlayerManager.getPlayer(event.getPlayer()).cancelToolProgress();
	}

	/**
	 * Performs region checking
	 *
	 * @param event move event
	 */
	private void execute(PlayerMoveEvent event) {
		Player player = event.getPlayer();
		NovaPlayer nPlayer = PlayerManager.getPlayer(player);
		Location from = event.getFrom();
		Location to = event.getTo();

		NovaRegion fromRegion = RegionManager.get(from);
		NovaRegion toRegion = RegionManager.get(to);

		//entering
		if((fromRegion == null && toRegion != null && nPlayer.getAtRegion() == null) || (fromRegion != null && toRegion != null && !fromRegion.equals(toRegion))) {
			plugin.getRegionManager().playerEnteredRegion(player, toRegion);
		}

		//exiting
		if(fromRegion != null && toRegion == null && nPlayer.getAtRegion() != null) {
			plugin.getRegionManager().playerExitedRegion(player);
		}
	}
}
