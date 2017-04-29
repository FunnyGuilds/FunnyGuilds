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
import co.marcin.novaguilds.enums.Permission;
import co.marcin.novaguilds.impl.util.AbstractListener;
import co.marcin.novaguilds.manager.ConfigManager;
import co.marcin.novaguilds.manager.PlayerManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.EquipmentSlot;

public class PlayerInfoListener extends AbstractListener {
	@EventHandler(priority = EventPriority.HIGH)
	public void onPlayerClickPlayer(PlayerInteractEntityEvent event) {
		if(ConfigManager.getServerVersion().isNewerThan(ConfigManager.ServerVersion.MINECRAFT_1_8_R3) && event.getHand() == EquipmentSlot.OFF_HAND) {
			return;
		}

		Player player = event.getPlayer();
		if((event.getRightClicked() instanceof Player) && player.isSneaking()) {
			if(Permission.NOVAGUILDS_PLAYERINFO.has(player)) {
				NovaPlayer nCPlayer = PlayerManager.getPlayer(event.getRightClicked());
				plugin.getPlayerManager().sendPlayerInfo(player, nCPlayer);
			}
		}
	}
}
