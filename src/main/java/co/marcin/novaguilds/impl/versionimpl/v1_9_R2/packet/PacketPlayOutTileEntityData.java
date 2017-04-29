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

import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class PacketPlayOutTileEntityData extends AbstractPacket {
	enum Action {
		SPAWNPOTENTIALS(1),
		COMMANDBLOCKTEXT(2),
		BEACON(3),
		MOBHEAD(4),
		FLOWER(5),
		BANNER(6),
		STRUCTURE(7),
		END_GATEWAY(8),
		SIGN_TEXT(9);

		private final int id;

		/**
		 * Action enum constructor
		 *
		 * @param id id accepted by NMS packet
		 */
		Action(int id) {
			this.id = id;
		}

		/**
		 * Gets action id
		 *
		 * @return the id
		 */
		public int getId() {
			return id;
		}
	}

	protected static Class<?> packetPlayOutTileEntityData;
	protected static Class<?> nBTTagCompoundClass;
	protected static Class<?> blockPositionClass;
	protected static Class<?> iChatBaseComponentClass;
	protected static Class<?> chatComponentTextClass;
	protected static Class<?> craftChatMessageClass;
	protected static Class<?> chatSerializerClass;
	protected static Method nBTTagCompoundSetStringMethod;
	protected static Method nBTTagCompoundSetIntMethod;
	protected static Method craftChatMessageFromStringMethod;
	protected static Method chatSerializerAMethod;

	static {
		try {
			packetPlayOutTileEntityData = Reflections.getCraftClass("PacketPlayOutTileEntityData");
			nBTTagCompoundClass = Reflections.getCraftClass("NBTTagCompound");
			blockPositionClass = Reflections.getCraftClass("BlockPosition");
			iChatBaseComponentClass = Reflections.getCraftClass("IChatBaseComponent");
			chatComponentTextClass = Reflections.getCraftClass("ChatComponentText");
			chatSerializerClass = Reflections.getCraftClass("IChatBaseComponent$ChatSerializer");
			craftChatMessageClass = Reflections.getBukkitClass("util.CraftChatMessage");

			nBTTagCompoundSetStringMethod = Reflections.getMethod(nBTTagCompoundClass, "setString");
			nBTTagCompoundSetIntMethod = Reflections.getMethod(nBTTagCompoundClass, "setInt");
			craftChatMessageFromStringMethod = Reflections.getMethod(craftChatMessageClass, "fromString", String.class);
			chatSerializerAMethod = Reflections.getMethod(chatSerializerClass, "a", iChatBaseComponentClass);
		}
		catch(ClassNotFoundException | NoSuchMethodException e) {
			LoggerUtils.exception(e);
		}
	}

	/**
	 * Creates a packet
	 *
	 * @param location location
	 * @param action   action enum
	 * @param data     NBTTagCompound instance
	 * @throws NoSuchMethodException     when something goes wrong
	 * @throws IllegalAccessException    when something goes wrong
	 * @throws InvocationTargetException when something goes wrong
	 * @throws InstantiationException    when something goes wrong
	 */
	public PacketPlayOutTileEntityData(Location location, Action action, Object data) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
		packet = packetPlayOutTileEntityData.getConstructor(
				blockPositionClass,
				int.class,
				nBTTagCompoundClass
		).newInstance(
				new BlockPositionWrapperImpl(location).getBlockPosition(),
				action.getId(),
				data
		);
	}

	/**
	 * Creates a packet ready to change sign text
	 *
	 * @param location location
	 * @param lines    array of 4 strings
	 * @return the packet
	 * @throws IllegalAccessException    when something goes wrong
	 * @throws InstantiationException    when something goes wrong
	 * @throws InvocationTargetException when something goes wrong
	 * @throws NoSuchMethodException     when something goes wrong
	 */
	public static PacketPlayOutTileEntityData getSignChange(Location location, String[] lines) throws IllegalAccessException, InstantiationException, InvocationTargetException, NoSuchMethodException {
		Object tag = nBTTagCompoundClass.newInstance();

		nBTTagCompoundSetStringMethod.invoke(tag, "id", "0"); //FIXME what is this for?
		nBTTagCompoundSetIntMethod.invoke(tag, "x", location.getBlockX());
		nBTTagCompoundSetIntMethod.invoke(tag, "y", location.getBlockY());
		nBTTagCompoundSetIntMethod.invoke(tag, "z", location.getBlockZ());

		//sanitize lines
		Object sanitizedLines = Array.newInstance(iChatBaseComponentClass, 4);
		for(int i = 0; i < 4; ++i) {
			if(i < lines.length && lines[i] != null) {
				Object val = Array.get(craftChatMessageFromStringMethod.invoke(null, lines[i]), 0);
				Array.set(sanitizedLines, i, val);
			}
			else {
				Object val = chatComponentTextClass.getConstructor(String.class).newInstance("");
				Array.set(sanitizedLines, i, val);
			}
		}

		//set lines
		for(int i = 0; i < 4; ++i) {
			Object s = chatSerializerAMethod.invoke(null, Array.get(sanitizedLines, i));
			nBTTagCompoundSetStringMethod.invoke(tag, "Text" + (i + 1), s);
		}

		return new PacketPlayOutTileEntityData(location, Action.SIGN_TEXT, tag);
	}
}
