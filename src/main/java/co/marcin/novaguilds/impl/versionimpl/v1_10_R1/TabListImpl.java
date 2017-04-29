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

package co.marcin.novaguilds.impl.versionimpl.v1_10_R1;

import co.marcin.novaguilds.api.basic.NovaPlayer;
import co.marcin.novaguilds.api.util.Packet;
import co.marcin.novaguilds.api.util.reflect.FieldAccessor;
import co.marcin.novaguilds.api.util.reflect.MethodInvoker;
import co.marcin.novaguilds.enums.Config;
import co.marcin.novaguilds.impl.basic.AbstractTabList;
import co.marcin.novaguilds.impl.versionimpl.v1_8_R1.packet.PacketPlayOutPlayerListHeaderFooter;
import co.marcin.novaguilds.impl.versionimpl.v1_8_R3.packet.PacketPlayOutPlayerInfo;
import co.marcin.novaguilds.util.LoggerUtils;
import co.marcin.novaguilds.util.StringUtils;
import co.marcin.novaguilds.util.TabUtils;
import co.marcin.novaguilds.util.reflect.PacketSender;
import co.marcin.novaguilds.util.reflect.Reflections;
import com.google.common.collect.Iterables;
import com.google.common.collect.Multimap;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

public class TabListImpl extends AbstractTabList {
	protected static Class<?> enumGamemodeClass;
	protected static Class<?> packetPlayerOutPlayerInfoClass;
	protected static Class<?> playerInfoDataClass;
	protected static Class<?> iChatBaseComponentClass;
	protected static Class<?> craftChatMessageClass;
	protected static Class<?> gameProfileClass;
	protected static Class<?> entityPlayerClass;
	protected static Class<?> craftOfflinePlayerClass;
	protected static Class<?> minecraftSessionServiceClass;
	protected static Class<?> minecraftServerClass;
	protected static Class<?> propertyMapClass;
	protected static Constructor<?> playerInfoDataConstructor;
	protected static Constructor<?> gameProfileConstructor;
	protected static Method craftChatMessageFromStringMethod;
	protected static Method minecraftServerGetMinecraftServerMethod;
	protected static Method craftOfflinePlayerGetProfileMethod;
	protected static Method entityPlayerGetProfileMethod;
	protected static Method minecraftSessionServiceFillProfilePropertiesMethod;
	protected static Method gameProfileGetPropertiesMethod;
	protected static MethodInvoker<Collection> propertyMapGetMethod;
	protected static Method propertyMapPutAllMethod;
	protected static Field packetPlayerOutPlayerInfoBField;
	protected static FieldAccessor<?> minecraftServerMinecraftSessionServiceField;

	protected final Object[] profiles = new Object[80];
	protected boolean first = true;
	protected static final String uuid = "00000000-0000-%s-0000-000000000000";
	protected static final String token = "!@#$^*";
	protected static final List<Object> skinProfiles = new ArrayList<>();

	static {
		try {
			enumGamemodeClass = Reflections.getCraftClass("EnumGamemode");
			packetPlayerOutPlayerInfoClass = Reflections.getCraftClass("PacketPlayOutPlayerInfo");
			playerInfoDataClass = Reflections.getCraftClass("PacketPlayOutPlayerInfo$PlayerInfoData");
			iChatBaseComponentClass = Reflections.getCraftClass("IChatBaseComponent");
			craftChatMessageClass = Reflections.getBukkitClass("util.CraftChatMessage");
			gameProfileClass = Reflections.getClass("com.mojang.authlib.GameProfile");
			entityPlayerClass = Reflections.getCraftClass("EntityPlayer");
			craftOfflinePlayerClass = Reflections.getBukkitClass("CraftOfflinePlayer");
			minecraftSessionServiceClass = Reflections.getClass("com.mojang.authlib.minecraft.MinecraftSessionService");
			minecraftServerClass = Reflections.getCraftClass("MinecraftServer");
			propertyMapClass = Reflections.getClass("com.mojang.authlib.properties.PropertyMap");
			craftChatMessageFromStringMethod = Reflections.getMethod(craftChatMessageClass, "fromString", String.class);
			minecraftServerGetMinecraftServerMethod = Reflections.getMethod(minecraftServerClass, "getServer");
			craftOfflinePlayerGetProfileMethod = Reflections.getMethod(craftOfflinePlayerClass, "getProfile");
			entityPlayerGetProfileMethod = Reflections.getMethod(entityPlayerClass, "getProfile");
			minecraftSessionServiceFillProfilePropertiesMethod = Reflections.getMethod(minecraftSessionServiceClass, "fillProfileProperties");
			gameProfileGetPropertiesMethod = Reflections.getMethod(gameProfileClass, "getProperties");
			propertyMapGetMethod = Reflections.getMethod(propertyMapClass, Collection.class, "get");
			propertyMapPutAllMethod = Reflections.getMethod(propertyMapClass, "putAll", Multimap.class);
			packetPlayerOutPlayerInfoBField = Reflections.getPrivateField(packetPlayerOutPlayerInfoClass, "b");
			minecraftServerMinecraftSessionServiceField = Reflections.getField(minecraftServerClass, minecraftSessionServiceClass, 0);

			playerInfoDataConstructor = playerInfoDataClass.getConstructor(
					packetPlayerOutPlayerInfoClass,
					gameProfileClass,
					int.class,
					enumGamemodeClass,
					iChatBaseComponentClass
			);

			gameProfileConstructor = gameProfileClass.getConstructor(UUID.class, String.class);
		}
		catch(ClassNotFoundException | NoSuchMethodException | NoSuchFieldException e) {
			LoggerUtils.exception(e);
		}
	}

	public TabListImpl(NovaPlayer nPlayer) {
		super(nPlayer);

		try {
			//Texture
			Object targetProfile;

			if(Bukkit.getOnlineMode()) {
				Collection<String> headSkinStringCollection = new ArrayList<>();

				if(Config.TABLIST_TEXTURE.isList()) {
					headSkinStringCollection.addAll(Config.TABLIST_TEXTURE.getStringList());
				}
				else {
					headSkinStringCollection.add(Config.TABLIST_TEXTURE.getString());
				}

				for(String headSkinString : headSkinStringCollection) {
					Player online = Bukkit.getPlayerExact(headSkinString);

					if(online != null) {
						targetProfile = entityPlayerGetProfileMethod.invoke(Reflections.getHandle(online));
					}
					else {
						targetProfile = craftOfflinePlayerGetProfileMethod.invoke(Bukkit.getOfflinePlayer(headSkinString));
					}

					if(Iterables.getFirst(propertyMapGetMethod.invoke(gameProfileGetPropertiesMethod.invoke(targetProfile), "textures"), null) == null) {
						Object server = minecraftServerGetMinecraftServerMethod.invoke(null);
						Object service = minecraftServerMinecraftSessionServiceField.get(server);
						targetProfile = minecraftSessionServiceFillProfilePropertiesMethod.invoke(service, targetProfile, true);
						skinProfiles.add(targetProfile);
					}
				}
			}
		}
		catch(IllegalAccessException | InvocationTargetException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void send() {
		try {
			if(!getPlayer().isOnline()) {
				return;
			}

			TabUtils.fillVars(this);
			final List<Packet> packets = new ArrayList<>();

			Packet addPlayerPacket = new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER);
			Packet updateNamePacket = new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.UPDATE_DISPLAY_NAME);
			List<Object> addPlayerList = new ArrayList<>();
			List<Object> updateNameList = new ArrayList<>();

			for(int i = 0; i < 80; i++) {
				String line;
				if(i < lines.size()) {
					line = lines.get(i);
				}
				else {
					line = "";
				}

				if(profiles[i] == null) {
					profiles[i] = gameProfileConstructor.newInstance(
							UUID.fromString(String.format(uuid, digit(i))),
							token + digit(i)
					);
				}

				line = StringUtils.replaceVarKeyMap(line, getVars());
				line = StringUtils.fixColors(line);

				Object gameProfile = profiles[i];
				Object gameMode = enumGamemodeClass.getEnumConstants()[0];
				Object lineCompound = Array.get(craftChatMessageFromStringMethod.invoke(null, line), 0);
				Object targetProfile = skinProfiles.get(i % skinProfiles.size());

				if(targetProfile != null) {
					propertyMapPutAllMethod.invoke(gameProfileGetPropertiesMethod.invoke(gameProfile), gameProfileGetPropertiesMethod.invoke(targetProfile));
				}

				Object playerInfoData = playerInfoDataConstructor.newInstance(
						null,
						gameProfile,
						0,
						gameMode,
						lineCompound
				);

				if(first) {
					addPlayerList.add(playerInfoData);
				}

				updateNameList.add(playerInfoData);
			}

			if(first) {
				first = false;
			}

			packets.add(addPlayerPacket);
			packets.add(updateNamePacket);

			if(!Config.TABLIST_HEADER.isEmpty() || !Config.TABLIST_FOOTER.isEmpty()) {
				packets.add(new PacketPlayOutPlayerListHeaderFooter(
						Config.TABLIST_HEADER.vars(getVars()).getString(),
						Config.TABLIST_FOOTER.vars(getVars()).getString())
				);
			}

			packetPlayerOutPlayerInfoBField.set(addPlayerPacket.getPacket(), addPlayerList);
			packetPlayerOutPlayerInfoBField.set(updateNamePacket.getPacket(), updateNameList);
			PacketSender.sendPacket(getPlayer().getPlayer(), packets.toArray());
		}
		catch(IllegalAccessException | InvocationTargetException | InstantiationException e) {
			LoggerUtils.exception(e);
		}
	}

	/**
	 * Adds a zero to one digit numbers
	 *
	 * @param i integer
	 * @return string
	 */
	protected final String digit(int i) {
		return i > 9 ? "" + i : "0" + i;
	}
}
