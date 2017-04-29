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


import co.marcin.novaguilds.api.basic.NovaGuild;
import co.marcin.novaguilds.enums.AbandonCause;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class GuildAbandonEvent extends Event implements Cancellable {
	private static final HandlerList handlers = new HandlerList();
	private final NovaGuild guild;
	private boolean cancelled;
	private AbandonCause cause;

	/**
	 * The constructor
	 *
	 * @param guild the guild
	 * @param cause abandon cause
	 */
	public GuildAbandonEvent(NovaGuild guild, AbandonCause cause) {
		this.guild = guild;
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
	 * Gets the guild
	 *
	 * @return the guild
	 */
	public NovaGuild getGuild() {
		return guild;
	}

	/**
	 * Sets the cause
	 *
	 * @param cause the cause
	 */
	public void setCause(AbandonCause cause) {
		this.cause = cause;
	}

	/**
	 * Gets the cause
	 *
	 * @return the cause
	 */
	public AbandonCause getCause() {
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
