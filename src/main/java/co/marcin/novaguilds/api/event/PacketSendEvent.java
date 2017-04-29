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

import co.marcin.novaguilds.api.util.packet.PacketEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PacketSendEvent extends Event implements Cancellable, PacketEvent {

	private static final HandlerList handlers = new HandlerList();
	private final Player player;
	private final Object packet;
	private boolean cancelled;

	/**
	 * The constructor
	 *
	 * @param packet packet object
	 * @param player receiver
	 */
	public PacketSendEvent(Object packet, Player player) {
		super(true);
		this.packet = packet;
		this.player = player;
		this.cancelled = false;
	}

	@Override
	public Player getPlayer() {
		return player;
	}

	@Override
	public Object getPacket() {
		return packet;
	}

	@Override
	public String getPacketName() {
		return this.packet.getClass().getSimpleName();
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

	@Override
	public boolean isCancelled() {
		return cancelled;
	}

	@Override
	public void setCancelled(boolean b) {
		cancelled = b;
	}

	/**
	 * Gets handler list
	 *
	 * @return handler list
	 */
	public static HandlerList getHandlerList() {
		return handlers;
	}
}
