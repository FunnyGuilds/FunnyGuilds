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

package co.marcin.novaguilds.impl.versionimpl.v1_8_R3.packet;

import co.marcin.novaguilds.impl.util.AbstractPacket;
import co.marcin.novaguilds.util.LoggerUtils;
import co.marcin.novaguilds.util.reflect.Reflections;
import org.bukkit.entity.Player;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PacketPlayOutPlayerInfo extends AbstractPacket {
	public enum EnumPlayerInfoAction {
		ADD_PLAYER,
		UPDATE_GAME_MODE,
		UPDATE_LATENCY,
		UPDATE_DISPLAY_NAME,
		REMOVE_PLAYER
	}

	protected static Class<?> enumPlayerInfoActionClass;
	protected static Class<?> packetPlayerOutPlayerInfoClass;
	protected static Constructor<?> packetPlayerOutPlayerInfoConstructor;

	protected final List<Object> list = new ArrayList<>();

	static {
		try {
			enumPlayerInfoActionClass = Reflections.getCraftClass("PacketPlayOutPlayerInfo$EnumPlayerInfoAction");
			packetPlayerOutPlayerInfoClass = Reflections.getCraftClass("PacketPlayOutPlayerInfo");
			packetPlayerOutPlayerInfoConstructor = packetPlayerOutPlayerInfoClass.getConstructor(
					enumPlayerInfoActionClass,
					Iterable.class
			);
		}
		catch(ClassNotFoundException | NoSuchMethodException e) {
			LoggerUtils.exception(e);
		}
	}

	/**
	 * The constructor
	 *
	 * @param action  action enum
	 * @param players list of players
	 * @throws IllegalAccessException    when something goes wrong
	 * @throws InvocationTargetException when something goes wrong
	 * @throws InstantiationException    when something goes wrong
	 */
	public PacketPlayOutPlayerInfo(EnumPlayerInfoAction action, Iterable<Player> players) throws IllegalAccessException, InvocationTargetException, InstantiationException {
		Object craftEnum = Reflections.getEnumConstant(enumPlayerInfoActionClass, action.name());
		List<Object> list = new ArrayList<>();

		if(players != null) {
			for(Player player : players) {
				list.add(Reflections.getHandle(player));
			}
		}

		packet = packetPlayerOutPlayerInfoConstructor.newInstance(craftEnum, list);
	}

	/**
	 * The constructor
	 *
	 * @param action  action enum
	 * @param players array of players
	 * @throws IllegalAccessException    when something goes wrong
	 * @throws InvocationTargetException when something goes wrong
	 * @throws InstantiationException    when something goes wrong
	 */
	public PacketPlayOutPlayerInfo(EnumPlayerInfoAction action, Player... players) throws IllegalAccessException, InstantiationException, InvocationTargetException {
		this(action, Arrays.asList(players));
	}


	/**
	 * The constructor with no players
	 *
	 * @param action action enum
	 * @throws IllegalAccessException    when something goes wrong
	 * @throws InvocationTargetException when something goes wrong
	 * @throws InstantiationException    when something goes wrong
	 */
	public PacketPlayOutPlayerInfo(EnumPlayerInfoAction action) throws IllegalAccessException, InstantiationException, InvocationTargetException {
		this(action, new ArrayList<Player>());
	}
}
