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

package co.marcin.novaguilds.util.reflect;

import co.marcin.novaguilds.NovaGuilds;
import co.marcin.novaguilds.api.util.Packet;
import co.marcin.novaguilds.util.ParticleUtils;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PacketSender {
	/**
	 * Sends packets to players
	 *
	 * @param players list of players
	 * @param packets packets
	 */
	public static void sendPacket(List<Player> players, Object... packets) {
		for(Player player : players) {
			sendPacket(player, packets);
		}
	}

	/**
	 * Sends packets to players
	 *
	 * @param players array of players
	 * @param packets packets
	 */
	@Deprecated
	public static void sendPacket(Player[] players, Object... packets) {
		sendPacket(Arrays.asList(players), packets);
	}

	/**
	 * Sends packets to a player
	 *
	 * @param player  player
	 * @param packets packets
	 */
	public static void sendPacket(Player player, Object... packets) {
		final List<Object> packetList = new ArrayList<>();

		for(Object packet : packets) {
			if(packet instanceof Packet) {
				packetList.add(((Packet) packet).getPacket());
			}
			else {
				packetList.add(packet);
			}
		}

		NovaGuilds.getInstance().getPacketExtension().sendPacket(player, packetList.toArray());
	}

	/**
	 * Sends packets to players in radius
	 *
	 * @param center  center location
	 * @param range   range
	 * @param packets packets
	 */
	public void sendPacket(Location center, double range, Object... packets) {
		sendPacket(ParticleUtils.getPlayersInRadius(center, range), packets);
	}
}
