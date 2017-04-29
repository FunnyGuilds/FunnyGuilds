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

import co.marcin.novaguilds.api.basic.NovaRegion;
import co.marcin.novaguilds.enums.AbandonCause;
import org.apache.commons.lang.Validate;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class RegionDeleteEvent extends Event implements Cancellable {
	public enum Cause {
		ADMIN,
		DELETE,
		ABANDON,
		RAID;

		/**
		 * Gets a Cause from AbandonCause enum
		 *
		 * @param abandonCause guild abandon cause
		 * @return cause enum
		 */
		public static Cause fromGuildAbandonCause(AbandonCause abandonCause) {
			switch(abandonCause) {
				case ADMIN:
				case ADMIN_ALL:
					return RegionDeleteEvent.Cause.ADMIN;
				case INACTIVE:
				case INVALID:
				case UNLOADED:
					return RegionDeleteEvent.Cause.ABANDON;
				case RAID:
					return RegionDeleteEvent.Cause.RAID;
				case PLAYER:
					return RegionDeleteEvent.Cause.DELETE;
			}

			return null;
		}
	}

	private static final HandlerList handlers = new HandlerList();
	private final Cause cause;
	private final NovaRegion region;
	private boolean cancelled;

	/**
	 * The constructor
	 *
	 * @param region region
	 * @param cause  the cause
	 */
	public RegionDeleteEvent(NovaRegion region, Cause cause) {
		Validate.notNull(cause, "Region delete cause cannot be null");

		this.region = region;
		this.cause = cause;
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
	 * Gets cause
	 *
	 * @return the cause
	 */
	public Cause getCause() {
		return cause;
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
