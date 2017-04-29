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

package co.marcin.novaguilds.api.event;

import co.marcin.novaguilds.enums.EntityUseAction;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

public class PlayerInteractEntityEvent extends PlayerEvent implements Cancellable {
	private static final HandlerList handlers = new HandlerList();
	protected final Entity clickedEntity;
	private final EntityUseAction action;
	private boolean cancelled = false;

	/**
	 * The constructor
	 *
	 * @param player        the player who clicked
	 * @param clickedEntity clicked entity
	 * @param action        action
	 */
	public PlayerInteractEntityEvent(Player player, Entity clickedEntity, EntityUseAction action) {
		super(player);
		this.clickedEntity = clickedEntity;
		this.action = action;
	}

	@Override
	public boolean isCancelled() {
		return cancelled;
	}

	@Override
	public void setCancelled(boolean cancel) {
		this.cancelled = cancel;
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

	/**
	 * Gets the entity that was clicked by the player.
	 *
	 * @return entity clicked by player
	 */
	public Entity getEntity() {
		return this.clickedEntity;
	}

	/**
	 * Gets use action
	 *
	 * @return entity use action
	 */
	public EntityUseAction getAction() {
		return action;
	}

	/**
	 * Gets handler list
	 *
	 * @return the handler list
	 */
	public static HandlerList getHandlerList() {
		return handlers;
	}
}
