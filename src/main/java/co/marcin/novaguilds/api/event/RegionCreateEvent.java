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

import co.marcin.novaguilds.api.basic.NovaPlayer;
import co.marcin.novaguilds.api.basic.NovaRegion;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class RegionCreateEvent extends Event implements Cancellable {
	private static final HandlerList handlers = new HandlerList();
	private final NovaRegion region;
	private final NovaPlayer nPlayer;
	private final boolean admin;
	private boolean cancelled;

	/**
	 * The constructor
	 *
	 * @param region  region
	 * @param nPlayer player
	 * @param admin   true if executed in admin panel
	 */
	public RegionCreateEvent(NovaRegion region, NovaPlayer nPlayer, boolean admin) {
		this.region = region;
		this.nPlayer = nPlayer;
		this.admin = admin;
	}

	@Override
	public boolean isCancelled() {
		return cancelled;
	}

	@Override
	public void setCancelled(boolean cancel) {
		cancelled = cancel;
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

	/**
	 * Gets the region
	 *
	 * @return region instance
	 */
	public NovaRegion getRegion() {
		return region;
	}

	/**
	 * Gets the player who creates the region
	 *
	 * @return player
	 */
	public NovaPlayer getPlayer() {
		return nPlayer;
	}

	/**
	 * Checks if the event has been called
	 * in the admin panel
	 *
	 * @return true if nga
	 */
	public boolean isAdmin() {
		return admin;
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
