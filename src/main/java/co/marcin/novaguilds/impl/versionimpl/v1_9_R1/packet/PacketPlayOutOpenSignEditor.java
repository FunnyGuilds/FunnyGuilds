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

package co.marcin.novaguilds.impl.versionimpl.v1_9_R1.packet;

import co.marcin.novaguilds.api.util.BlockPositionWrapper;
import co.marcin.novaguilds.impl.util.AbstractPacket;
import co.marcin.novaguilds.impl.versionimpl.v1_9_R1.BlockPositionWrapperImpl;
import co.marcin.novaguilds.util.LoggerUtils;
import co.marcin.novaguilds.util.reflect.Reflections;
import org.bukkit.Location;

import java.lang.reflect.InvocationTargetException;

public class PacketPlayOutOpenSignEditor extends AbstractPacket {
	protected static Class<?> packetOpenSignEditorClass;
	protected static Class<?> blockPositionClass;

	static {
		try {
			packetOpenSignEditorClass = Reflections.getCraftClass("PacketPlayOutOpenSignEditor");
			blockPositionClass = Reflections.getCraftClass("BlockPosition");
		}
		catch(Exception e) {
			LoggerUtils.exception(e);
		}
	}

	/**
	 * The constructor
	 *
	 * @param location location
	 * @throws NoSuchMethodException     when something goes wrong
	 * @throws IllegalAccessException    when something goes wrong
	 * @throws InvocationTargetException when something goes wrong
	 * @throws InstantiationException    when something goes wrong
	 */
	public PacketPlayOutOpenSignEditor(Location location) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
		BlockPositionWrapper blockPosition = new BlockPositionWrapperImpl(location);
		packet = packetOpenSignEditorClass.getConstructor(blockPositionClass).newInstance(blockPosition.getBlockPosition());
	}
}
