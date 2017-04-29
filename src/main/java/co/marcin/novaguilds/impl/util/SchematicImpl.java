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

package co.marcin.novaguilds.impl.util;

import co.marcin.novaguilds.NovaGuilds;
import co.marcin.novaguilds.api.util.Schematic;
import co.marcin.novaguilds.util.LoggerUtils;
import co.marcin.novaguilds.util.Meta;
import co.marcin.novaguilds.util.reflect.Reflections;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

@SuppressWarnings("ConstantConditions")
public class SchematicImpl implements Schematic {
	protected static Class<?> nBTCompressedStreamToolsClass;
	protected static Class<?> nBTTagCompoundClass;
	protected static Method aMethod;
	protected static Method getShortMethod;
	protected static Method getByteArrayMethod;
	private short width;
	private short height;
	private short length;
	private byte[] blocks;
	private byte[] data;
	private final String name;

	static {
		try {
			nBTCompressedStreamToolsClass = Reflections.getCraftClass("NBTCompressedStreamTools");
			nBTTagCompoundClass = Reflections.getCraftClass("NBTTagCompound");
			aMethod = Reflections.getMethod(nBTCompressedStreamToolsClass, "a", InputStream.class);
			getShortMethod = Reflections.getMethod(nBTTagCompoundClass, "getShort");
			getByteArrayMethod = Reflections.getMethod(nBTTagCompoundClass, "getByteArray");
		}
		catch(Exception e) {
			LoggerUtils.exception(e);
		}
	}

	/**
	 * Constructor using file name
	 * in plugins/NovaGuilds/schematic/
	 *
	 * @param fileName file name
	 * @throws FileNotFoundException when the file does not exist
	 */
	public SchematicImpl(String fileName) throws FileNotFoundException {
		this(new File(NovaGuilds.getInstance().getDataFolder() + "/schematic/", fileName));
	}

	/**
	 * Constructor using File instance
	 *
	 * @param file file instance
	 * @throws FileNotFoundException when the file does not exist
	 */
	public SchematicImpl(File file) throws FileNotFoundException {
		name = file.getName();

		if(!file.exists()) {
			throw new FileNotFoundException();
		}

		try {
			FileInputStream fis = new FileInputStream(file);
			Object nbtData = aMethod.invoke(null, fis);

			width = (short) getShortMethod.invoke(nbtData, "Width");
			height = (short) getShortMethod.invoke(nbtData, "Height");
			length = (short) getShortMethod.invoke(nbtData, "Length");
			blocks = (byte[]) getByteArrayMethod.invoke(nbtData, "Blocks");
			data = (byte[]) getByteArrayMethod.invoke(nbtData, "Data");

			fis.close();
		}
		catch(InvocationTargetException | IllegalAccessException | IOException e) {
			LoggerUtils.exception(e);
		}
	}

	@Override
	public void paste(Location location) {
		Location root = location.clone().subtract(width / 2, 1, length / 2);

		for(int y = height - 1; y >= 0; y--) {
			for(int z = 0; z < length; z++) {
				for(int x = 0; x < width; x++) {
					Location blockLocation = root.clone().add(x, y, z);
					int index = x + (y * length + z) * width;

					Block block = blockLocation.getBlock();

					Meta.protect(block);
					Meta.setMetadata(block, "state", block.getState());

					block.setTypeId(blocks[index] < 0 ? Material.SPONGE.getId() : blocks[index]);
					block.setData(data[index]);
				}
			}
		}
	}

	@Override
	public short getWidth() {
		return width;
	}

	@Override
	public short getHeight() {
		return height;
	}

	@Override
	public short getLength() {
		return length;
	}

	@Override
	public String getName() {
		return name;
	}
}
