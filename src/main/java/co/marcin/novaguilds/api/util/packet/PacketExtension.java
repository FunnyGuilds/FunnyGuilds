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

package co.marcin.novaguilds.api.util.packet;

import org.bukkit.entity.Player;
import org.bukkit.event.EventPriority;

public interface PacketExtension {
	/**
	 * Registers a player
	 *
	 * @param player the player
	 */
	void registerPlayer(final Player player);

	/**
	 * Unregisters the channel
	 */
	void unregisterChannel();

	/**
	 * Sends a packet or more
	 *
	 * @param player  target player
	 * @param packets packets
	 */
	void sendPacket(Player player, Object... packets);

	interface PacketHandler {
		enum Direction {
			IN,
			OUT
		}

		/**
		 * Gets packet name
		 *
		 * @return packet name
		 */
		String getPacketName();

		/**
		 * Gets handler priority
		 *
		 * @return the priority
		 */
		EventPriority getPriority();

		/**
		 * Get the direction
		 *
		 * @return the direction
		 */
		Direction getDirection();

		/**
		 * Handles packet event
		 *
		 * @param event the event
		 */
		void handle(PacketEvent event);
	}
}
