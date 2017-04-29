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

package co.marcin.novaguilds.impl.versionimpl.v1_8_R1.packet;

import co.marcin.novaguilds.api.util.BlockPositionWrapper;
import co.marcin.novaguilds.impl.util.AbstractPacket;
import co.marcin.novaguilds.impl.versionimpl.v1_9_R2.BlockPositionWrapperImpl;
import co.marcin.novaguilds.util.LoggerUtils;
import co.marcin.novaguilds.util.reflect.Reflections;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class PacketPlayInUpdateSign extends AbstractPacket {
	protected static Class<?> iChatBaseComponentClass;
	protected static Class<?> packetInUpdateSignClass;
	protected static Field blockPositionField;
	protected static Field linesField;
	protected static Method getTextMethod;

	private final String[] lines = new String[4];
	private BlockPositionWrapper blockPositionWrapper;

	static {
		try {
			packetInUpdateSignClass = Reflections.getCraftClass("PacketPlayInUpdateSign");
			iChatBaseComponentClass = Reflections.getCraftClass("IChatBaseComponent");

			blockPositionField = Reflections.getPrivateField(packetInUpdateSignClass, "a");
			linesField = Reflections.getPrivateField(packetInUpdateSignClass, "b");

			getTextMethod = Reflections.getMethod(iChatBaseComponentClass, "getText");
		}
		catch(Exception e) {
			LoggerUtils.exception(e);
		}
	}

	/**
	 * Converts NMS packet
	 *
	 * @param packet NMS PacketPlayInUpdateSign object
	 * @throws IllegalAccessException    when something goes wrong
	 * @throws InvocationTargetException when something goes wrong
	 */
	public PacketPlayInUpdateSign(Object packet) throws IllegalAccessException, InvocationTargetException {
		blockPositionWrapper = new BlockPositionWrapperImpl(blockPositionField.get(packet));
		Object[] components = (Object[]) linesField.get(packet);

		int index = 0;
		for(Object component : components) {
			Object line = getTextMethod.invoke(component);
			lines[index] = (String) line;
			index++;
		}
	}

	/**
	 * Gets block position wrapper
	 *
	 * @return integer
	 */
	public BlockPositionWrapper getBlockPositionWrapper() {
		return blockPositionWrapper;
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
