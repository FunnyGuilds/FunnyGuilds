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

package co.marcin.novaguilds.impl.versionimpl.v1_7_R4.packet;

import co.marcin.novaguilds.impl.util.AbstractPacket;
import co.marcin.novaguilds.util.LoggerUtils;
import co.marcin.novaguilds.util.reflect.Reflections;
import org.bukkit.Location;

import java.lang.reflect.Field;

public class PacketPlayOutUpdateSign extends AbstractPacket {
	protected static Class<?> packetOutUpdateSignClass;
	protected static Field xField;
	protected static Field yField;
	protected static Field zField;
	protected static Field linesField;

	static {
		try {
			packetOutUpdateSignClass = Reflections.getCraftClass("PacketPlayOutUpdateSign");

			xField = Reflections.getPrivateField(packetOutUpdateSignClass, "x");
			yField = Reflections.getPrivateField(packetOutUpdateSignClass, "y");
			zField = Reflections.getPrivateField(packetOutUpdateSignClass, "z");
			linesField = Reflections.getPrivateField(packetOutUpdateSignClass, "lines");
		}
		catch(ClassNotFoundException | NoSuchFieldException e) {
			LoggerUtils.exception(e);
		}
	}

	/**
	 * The constructor
	 *
	 * @param location sign location
	 * @param lines    array of 4 strings
	 * @throws IllegalAccessException when something goes wrong
	 * @throws InstantiationException when something goes wrong
	 */
	public PacketPlayOutUpdateSign(Location location, String[] lines) throws IllegalAccessException, InstantiationException {
		packet = packetOutUpdateSignClass.newInstance();
		xField.set(packet, location.getBlockX());
		yField.set(packet, location.getBlockY());
		zField.set(packet, location.getBlockZ());
		linesField.set(packet, lines);
	}
}
