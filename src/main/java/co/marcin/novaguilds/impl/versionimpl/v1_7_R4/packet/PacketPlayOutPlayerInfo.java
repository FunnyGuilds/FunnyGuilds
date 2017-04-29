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

import co.marcin.novaguilds.api.util.reflect.FieldAccessor;
import co.marcin.novaguilds.impl.util.AbstractPacket;
import co.marcin.novaguilds.util.LoggerUtils;
import co.marcin.novaguilds.util.reflect.Reflections;
import com.google.common.base.Charsets;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.UUID;

public class PacketPlayOutPlayerInfo extends AbstractPacket {
	public enum Action {
		ADD_PLAYER(0),
		UPDATE_GAMEMODE(1),
		UPDATE_LATENCY(2),
		UPDATE_DISPLAY_NAME(3),
		REMOVE_PLAYER(4);

		private final int number;

		/**
		 * The constructor
		 *
		 * @param number action number
		 */
		Action(int number) {
			this.number = number;
		}

		/**
		 * Gets action number
		 *
		 * @return number
		 */
		public int getNumber() {
			return number;
		}

		/**
		 * Gets an action by its id
		 *
		 * @param id action id
		 * @return action enum
		 */
		public static Action byId(int id) {
			for(Action action : values()) {
				if(id == action.number) {
					return action;
				}
			}

			return null;
		}
	}

	protected static Class<?> packetClass;
	protected static Class<?> gameProfileClass;
	protected static Field playerField;
	protected static FieldAccessor<Integer> pingField;
	protected static FieldAccessor<String> usernameField;
	protected static Field gamemodeField;
	protected static FieldAccessor<Integer> actionField;
	protected static FieldAccessor<Boolean> booleanField;
	protected static Constructor<?> gameProfileConstructor;
	protected static final Class<?>[] typesClass = new Class<?>[]{String.class, boolean.class, int.class};

	private static int type = 0;

	private final String username;
	private final Action action;
	private final int ping;

	static {
		try {
			packetClass = Reflections.getCraftClass("PacketPlayOutPlayerInfo");

			try {
				if(packetClass.getConstructor(typesClass) == null) {
					type = 1;
				}
			}
			catch(Exception e) {
				type = 1;
			}

			if(type == 1) {
				gameProfileClass = Reflections.getClass("net.minecraft.util.com.mojang.authlib.GameProfile");
				usernameField = Reflections.getField(packetClass, "username", String.class);
				gamemodeField = Reflections.getPrivateField(packetClass, "gamemode");
				pingField = Reflections.getField(packetClass, "ping", int.class);
				playerField = Reflections.getPrivateField(packetClass, "player");
				actionField = Reflections.getField(packetClass, "action", int.class);
				gameProfileConstructor = gameProfileClass.getConstructor(UUID.class, String.class);
			}
			else {
				usernameField = Reflections.getField(packetClass, "a", String.class);
				booleanField = Reflections.getField(packetClass, "b", boolean.class);
				pingField = Reflections.getField(packetClass, "c", int.class);
			}
		}
		catch(ClassNotFoundException | NoSuchFieldException | NoSuchMethodException e) {
			LoggerUtils.exception(e);
		}
	}

	/**
	 * The constructor
	 *
	 * @param string tab slot text
	 * @param action action enum
	 * @param ping TODO
	 * @throws IllegalAccessException    when something goes wrong
	 * @throws NoSuchMethodException     when something goes wrong
	 * @throws InvocationTargetException when something goes wrong
	 * @throws InstantiationException    when something goes wrong
	 */
	public PacketPlayOutPlayerInfo(String string, Action action, int ping) throws IllegalAccessException, NoSuchMethodException, InvocationTargetException, InstantiationException {
		this.username = string;
		this.action = action;
		this.ping = ping;

		if(type == 0) {
			packet = packetClass.getConstructor(typesClass).newInstance(string, action != Action.REMOVE_PLAYER, ping);
		}
		else if(type == 1) {
			UUID uuid = UUID.nameUUIDFromBytes(("OfflinePlayer:" + string).getBytes(Charsets.UTF_8));
			Object profile = gameProfileConstructor.newInstance(uuid, string);

			packet = packetClass.newInstance();
			usernameField.set(packet, string);
			gamemodeField.set(packet, 1);
			pingField.set(packet, ping);
			playerField.set(packet, profile);
			actionField.set(packet, action.getNumber());
		}
	}

	/**
	 * The constructor
	 *
	 * @param packet packet instance
	 * @throws IllegalAccessException when something goes wrong
	 */
	public PacketPlayOutPlayerInfo(Object packet) throws IllegalAccessException {
		if(type == 0) {
			username = usernameField.get(packet);
			ping = pingField.get(packet);
			action = booleanField.get(packet) ? Action.ADD_PLAYER : Action.REMOVE_PLAYER;
		}
		else {
			username = usernameField.get(packet);
			ping = pingField.get(packet);
			action = Action.byId(actionField.get(packet));
		}
	}

	/**
	 * Gets player username
	 * (row content)
	 *
	 * @return username
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * Gets the action
	 *
	 * @return action enum
	 */
	public Action getAction() {
		return action;
	}

	/**
	 * Gets the ping
	 *
	 * @return the ping
	 */
	public int getPing() {
		return ping;
	}
}
