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

package co.marcin.novaguilds.impl.versionimpl.v1_9_R2;

import co.marcin.novaguilds.impl.util.AbstractBlockPositionWrapper;
import co.marcin.novaguilds.util.LoggerUtils;
import co.marcin.novaguilds.util.reflect.Reflections;
import org.bukkit.Location;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

public class BlockPositionWrapperImpl extends AbstractBlockPositionWrapper {
	protected static Class<?> blockPositionClass;
	protected static Class<?> baseBlockPositionClass;
	protected static Field xField;
	protected static Field yField;
	protected static Field zField;

	static {
		try {
			blockPositionClass = Reflections.getCraftClass("BlockPosition");
			baseBlockPositionClass = Reflections.getCraftClass("BaseBlockPosition");
			xField = Reflections.getPrivateField(baseBlockPositionClass, "a");
			yField = Reflections.getPrivateField(baseBlockPositionClass, "b");
			zField = Reflections.getPrivateField(baseBlockPositionClass, "c");
		}
		catch(ClassNotFoundException | NoSuchFieldException e) {
			LoggerUtils.exception(e);
		}
	}

	public BlockPositionWrapperImpl(Location location) {
		super(location);
	}

	public BlockPositionWrapperImpl(int x, int y, int z) {
		super(x, y, z);
	}

	public BlockPositionWrapperImpl(Object blockPosition) throws IllegalAccessException {
		super(blockPosition);

		setX(xField.getInt(blockPosition));
		setY(yField.getInt(blockPosition));
		setZ(zField.getInt(blockPosition));
	}

	@Override
	public Object getBlockPosition() {
		try {
			return blockPositionClass.getConstructor(
					int.class,
					int.class,
					int.class
			).newInstance(
					getX(),
					getY(),
					getZ()
			);
		}
		catch(InvocationTargetException | IllegalAccessException | NoSuchMethodException | InstantiationException e) {
			LoggerUtils.exception(e);
			return null;
		}
	}
}
