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
import org.bukkit.Material;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class PacketPlayOutBlockChange extends AbstractPacket {
	protected static Class<?> packetPlayOutBlockChangeClass;
	protected static Class<?> blockClass;
	protected static Class<?> worldClass;
	protected static Field xField;
	protected static Field yField;
	protected static Field zField;
	protected static Field blockField;
	protected static Field dataField;
	protected static Method getBlockAtMethod;
	protected static Method getByIdMethod;

	static {
		try {
			packetPlayOutBlockChangeClass = Reflections.getCraftClass("PacketPlayOutBlockChange");
			blockClass = Reflections.getCraftClass("Block");
			worldClass = Reflections.getCraftClass("World");

			xField = Reflections.getPrivateField(packetPlayOutBlockChangeClass, "a");
			yField = Reflections.getPrivateField(packetPlayOutBlockChangeClass, "b");
			zField = Reflections.getPrivateField(packetPlayOutBlockChangeClass, "c");
			blockField = Reflections.getPrivateField(packetPlayOutBlockChangeClass, "block");
			dataField = Reflections.getPrivateField(packetPlayOutBlockChangeClass, "data");

			getBlockAtMethod = Reflections.getMethod(worldClass, "getType", int.class, int.class, int.class);
			getByIdMethod = Reflections.getMethod(blockClass, "getById");
		}
		catch(NoSuchFieldException | ClassNotFoundException | NoSuchMethodException e) {
			LoggerUtils.exception(e);
		}
	}


	/**
	 * Creates the packet
	 *
	 * @param location location
	 * @param material material
	 * @param data     data byte (color, type etc.)
	 * @throws IllegalAccessException    when something goes wrong
	 * @throws InvocationTargetException when something goes wrong
	 * @throws InstantiationException    when something goes wrong
	 */
	public PacketPlayOutBlockChange(Location location, Material material, int data) throws IllegalAccessException, InstantiationException, InvocationTargetException {
		packet = packetPlayOutBlockChangeClass.newInstance();

		xField.set(packet, location.getBlockX());
		yField.set(packet, location.getBlockY());
		zField.set(packet, location.getBlockZ());

		Object block;
		if(material == null) {
			block = getBlockAtMethod.invoke(
					Reflections.getHandle(location.getWorld()),
					location.getBlockX(),
					location.getBlockY(),
					location.getBlockZ()
			);
		}
		else {
			Object id = material.getId();
			block = getByIdMethod.invoke(null, id);
		}

		blockField.set(packet, block);
		dataField.set(packet, data);
	}
}
