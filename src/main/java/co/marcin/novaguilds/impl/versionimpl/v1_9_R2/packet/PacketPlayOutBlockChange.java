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

package co.marcin.novaguilds.impl.versionimpl.v1_9_R2.packet;

import co.marcin.novaguilds.impl.util.AbstractPacket;
import co.marcin.novaguilds.impl.versionimpl.v1_9_R2.BlockPositionWrapperImpl;
import co.marcin.novaguilds.util.LoggerUtils;
import co.marcin.novaguilds.util.reflect.Reflections;
import org.bukkit.Location;
import org.bukkit.Material;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class PacketPlayOutBlockChange extends AbstractPacket {
	protected static Class<?> packetBlockChangeClass;
	protected static Class<?> worldClass;
	protected static Class<?> craftMagicNumbersClass;
	protected static Class<?> blockClass;
	protected static Field blockPositionField;
	protected static Field blockDataField;
	protected static Method getBlockMethod;
	protected static Method fromLegacyDataMethod;
	protected static Method getTypeMethod;

	static {
		try {
			packetBlockChangeClass = Reflections.getCraftClass("PacketPlayOutBlockChange");
			blockClass = Reflections.getCraftClass("Block");
			worldClass = Reflections.getCraftClass("World");
			craftMagicNumbersClass = Reflections.getBukkitClass("util.CraftMagicNumbers");

			blockPositionField = Reflections.getPrivateField(packetBlockChangeClass, "a");
			blockDataField = Reflections.getPrivateField(packetBlockChangeClass, "block");

			getBlockMethod = Reflections.getMethod(craftMagicNumbersClass, "getBlock", Material.class);
			fromLegacyDataMethod = Reflections.getMethod(blockClass, "fromLegacyData");
			getTypeMethod = Reflections.getMethod(worldClass, "getType");
		}
		catch(Exception e) {
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
	public PacketPlayOutBlockChange(Location location, Material material, int data) throws IllegalAccessException, InvocationTargetException, InstantiationException {
		Object blockPosition = new BlockPositionWrapperImpl(location).getBlockPosition();
		packet = packetBlockChangeClass.newInstance();

		Object blockData;
		if(material == null) {
			Object world = Reflections.getHandle(location.getWorld());
			blockData = getTypeMethod.invoke(world, blockPosition);
		}
		else {
			blockData = getData(material, data);
		}

		blockPositionField.set(packet, blockPosition);
		blockDataField.set(packet, blockData);
	}

	/**
	 * Gets block data
	 *
	 * @param material material
	 * @param data     data byte
	 * @return block data
	 * @throws InvocationTargetException when something goes wrong
	 * @throws IllegalAccessException    when something goes wrong
	 */
	protected Object getData(Material material, int data) throws InvocationTargetException, IllegalAccessException {
		Object block = getBlockMethod.invoke(craftMagicNumbersClass, material);
		return fromLegacyDataMethod.invoke(block, data);
	}
}
