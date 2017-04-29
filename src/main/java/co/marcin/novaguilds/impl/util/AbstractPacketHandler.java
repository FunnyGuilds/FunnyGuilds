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

package co.marcin.novaguilds.impl.util;

import co.marcin.novaguilds.NovaGuilds;
import co.marcin.novaguilds.api.util.packet.PacketExtension;
import co.marcin.novaguilds.listener.PacketListener;
import org.bukkit.event.EventPriority;

public abstract class AbstractPacketHandler implements PacketExtension.PacketHandler {
	protected static final NovaGuilds plugin = NovaGuilds.getInstance();
	private final String packetName;
	protected EventPriority priority = EventPriority.NORMAL;
	private Direction direction;

	/**
	 * PacketHandler constructor
	 *
	 * @param packetName packet name
	 * @param direction  direction enum
	 */
	public AbstractPacketHandler(String packetName, Direction direction) {
		this.packetName = packetName;
		this.direction = direction;
		PacketListener.register(this);
	}

	@Override
	public final EventPriority getPriority() {
		return priority;
	}

	@Override
	public Direction getDirection() {
		return direction;
	}

	@Override
	public String getPacketName() {
		return packetName;
	}

	/**
	 * Sets handler priority
	 *
	 * @param priority the priority
	 */
	protected final void setPriority(EventPriority priority) {
		this.priority = priority;
	}
}
