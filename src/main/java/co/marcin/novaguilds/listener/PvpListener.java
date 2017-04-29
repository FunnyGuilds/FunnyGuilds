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
import co.marcin.novaguilds.enums.Message;
import co.marcin.novaguilds.impl.util.AbstractListener;
import co.marcin.novaguilds.manager.PlayerManager;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class PvpListener extends AbstractListener {
	@EventHandler
	public void onPlayerAttack(EntityDamageByEntityEvent event) {
		if(event.getEntity() instanceof Player) {
			Player attacker = null;
			Player player = (Player) event.getEntity();

			if(event.getDamager() instanceof Player) {
				attacker = (Player) event.getDamager();
			}
			else if(event.getDamager().getType() == EntityType.ARROW) {
				Arrow arrow = (Arrow) event.getDamager();

				if(arrow.getShooter() instanceof Player) {
					attacker = (Player) arrow.getShooter();
				}
			}

			if(attacker != null) {
				NovaPlayer nPlayer = PlayerManager.getPlayer(player);
				NovaPlayer nPlayerAttacker = PlayerManager.getPlayer(attacker);

				//team pvp
				if(!nPlayerAttacker.getName().equals(nPlayer.getName())) {
					if(nPlayerAttacker.hasGuild() && nPlayer.hasGuild()) {
						if(plugin.getPlayerManager().isGuildMate(player, attacker)) { //same guild
							if(!nPlayer.getGuild().getFriendlyPvp()) {
								Message.CHAT_PVP_TEAM.send(attacker);
								event.setCancelled(true);

								//remove the arrow
								if(event.getDamager().getType() == EntityType.ARROW) {
									event.getDamager().remove();
								}
							}
						}
						else if(plugin.getPlayerManager().isAlly(player, attacker)) { //ally
							if(!(nPlayer.getGuild().getFriendlyPvp() && nPlayerAttacker.getGuild().getFriendlyPvp())) {
								Message.CHAT_PVP_ALLY.send(attacker);
								event.setCancelled(true);

								//remove the arrow
								if(event.getDamager().getType() == EntityType.ARROW) {
									event.getDamager().remove();
								}
							}
						}
					}
				}
			}
		}
	}
}
