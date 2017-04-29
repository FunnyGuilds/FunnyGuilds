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

import co.marcin.novaguilds.impl.util.AbstractPacket;
import co.marcin.novaguilds.impl.versionimpl.v1_9_R2.BlockPositionWrapperImpl;
import co.marcin.novaguilds.util.LoggerUtils;
import co.marcin.novaguilds.util.reflect.Reflections;
import org.bukkit.Location;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

public class PacketPlayOutUpdateSign extends AbstractPacket {
	protected static Class<?> packetOutUpdateSignClass;
	protected static Class<?> chatComponentTextClass;
	protected static Field worldField;
	protected static Field blockPositionField;
	protected static Field linesField;
	protected static Constructor<?> chatComponentTextConstructor;

	static {
		try {
			packetOutUpdateSignClass = Reflections.getCraftClass("PacketPlayOutUpdateSign");
			chatComponentTextClass = Reflections.getCraftClass("ChatComponentText");

			worldField = Reflections.getPrivateField(packetOutUpdateSignClass, "a");
			blockPositionField = Reflections.getPrivateField(packetOutUpdateSignClass, "b");
			linesField = Reflections.getPrivateField(packetOutUpdateSignClass, "c");

			chatComponentTextConstructor = chatComponentTextClass.getConstructor(String.class);
		}
		catch(NoSuchMethodException | ClassNotFoundException | NoSuchFieldException e) {
			LoggerUtils.exception(e);
		}
	}

	/**
	 * The constructor
	 *
	 * @param location sign location
	 * @param lines    array of 4 strings
	 * @throws IllegalAccessException    when something goes wrong
	 * @throws InstantiationException    when something goes wrong
	 * @throws InvocationTargetException when something goes wrong
	 */
	public PacketPlayOutUpdateSign(Location location, String[] lines) throws IllegalAccessException, InstantiationException, InvocationTargetException {
		packet = packetOutUpdateSignClass.newInstance();
		Object blockPosition = new BlockPositionWrapperImpl(location).getBlockPosition();
		worldField.set(packet, Reflections.getHandle(location.getWorld()));
		blockPositionField.set(packet, blockPosition);

		int n = 4;
		Object array = Array.newInstance(chatComponentTextClass, n);
		for(int i = 0; i < n; i++) {
			Object val = chatComponentTextConstructor.newInstance(lines[i]);

			Array.set(array, i, val);
		}

		linesField.set(packet, array);
	}
}
