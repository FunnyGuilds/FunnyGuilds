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
import co.marcin.novaguilds.util.LoggerUtils;
import co.marcin.novaguilds.util.StringUtils;
import co.marcin.novaguilds.util.reflect.Reflections;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class PacketPlayOutPlayerListHeaderFooter extends AbstractPacket {
	protected static Class<?> PacketPlayOutPlayerListHeaderFooterClass;
	protected static Class<?> craftChatMessageClass;
	protected static Method craftChatMessageFromStringMethod;
	protected static Field PacketPlayOutPlayerListHeaderFooterBField;
	protected static Field PacketPlayOutPlayerListHeaderFooterAField;

	static {
		try {
			craftChatMessageClass = Reflections.getBukkitClass("util.CraftChatMessage");
			craftChatMessageFromStringMethod = Reflections.getMethod(craftChatMessageClass, "fromString", String.class);
			PacketPlayOutPlayerListHeaderFooterClass = Reflections.getCraftClass("PacketPlayOutPlayerListHeaderFooter");
			PacketPlayOutPlayerListHeaderFooterAField = Reflections.getPrivateField(PacketPlayOutPlayerListHeaderFooterClass, "a");
			PacketPlayOutPlayerListHeaderFooterBField = Reflections.getPrivateField(PacketPlayOutPlayerListHeaderFooterClass, "b");
		}
		catch(ClassNotFoundException | NoSuchFieldException | NoSuchMethodException e) {
			LoggerUtils.exception(e);
		}
	}

	/**
	 * The constructor
	 *
	 * @param header header string
	 * @param footer footer string
	 * @throws IllegalAccessException    when something goes wrong
	 * @throws InstantiationException    when something goes wrong
	 * @throws InvocationTargetException when something goes wrong
	 */
	public PacketPlayOutPlayerListHeaderFooter(String header, String footer) throws IllegalAccessException, InstantiationException, InvocationTargetException {
		Object[] iChatBaseComponentHeader = (Object[]) craftChatMessageFromStringMethod.invoke(null, StringUtils.fixColors(header));
		Object[] iChatBaseComponentFooter = (Object[]) craftChatMessageFromStringMethod.invoke(null, StringUtils.fixColors(footer));
		packet = PacketPlayOutPlayerListHeaderFooterClass.newInstance();
		PacketPlayOutPlayerListHeaderFooterAField.set(packet, iChatBaseComponentHeader[0]);
		PacketPlayOutPlayerListHeaderFooterBField.set(packet, iChatBaseComponentFooter[0]);
	}
}
