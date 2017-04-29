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

import co.marcin.novaguilds.api.util.reflect.FieldAccessor;
import co.marcin.novaguilds.impl.util.AbstractPacket;
import co.marcin.novaguilds.util.LoggerUtils;
import co.marcin.novaguilds.util.reflect.Reflections;

public class PacketPlayInUpdateSign extends AbstractPacket {
	protected static Class<?> packetInUpdateSignClass;
	protected static FieldAccessor<String[]> linesField;
	protected static FieldAccessor<Integer> xField;
	protected static FieldAccessor<Integer> yField;
	protected static FieldAccessor<Integer> zField;

	private final String[] lines;

	static {
		try {
			packetInUpdateSignClass = Reflections.getCraftClass("PacketPlayInUpdateSign");
			linesField = Reflections.getField(packetInUpdateSignClass, String[].class, 0);
			xField = Reflections.getField(packetInUpdateSignClass, int.class, 0);
			yField = Reflections.getField(packetInUpdateSignClass, int.class, 1);
			zField = Reflections.getField(packetInUpdateSignClass, int.class, 2);
		}
		catch(ClassNotFoundException | NoSuchFieldException e) {
			LoggerUtils.exception(e);
		}
	}

	private final int x;
	private final int y;
	private final int z;

	/**
	 * Creates the packet
	 *
	 * @param packet NMS packet
	 */
	public PacketPlayInUpdateSign(Object packet) {
		x = xField.get(packet);
		y = yField.get(packet);
		z = zField.get(packet);
		lines = linesField.get(packet);
	}

	/**
	 * Gets x coordinate
	 *
	 * @return integer
	 */
	public int getX() {
		return x;
	}

	/**
	 * Gets y coordinate
	 *
	 * @return integer
	 */
	public int getY() {
		return y;
	}

	/**
	 * Gets z coordinate
	 *
	 * @return integer
	 */
	public int getZ() {
		return z;
	}

	/**
	 * Gets sign lines
	 *
	 * @return array of 4 strings
	 */
	public String[] getLines() {
		return lines;
	}
}
