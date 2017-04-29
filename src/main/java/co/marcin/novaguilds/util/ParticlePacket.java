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

package co.marcin.novaguilds.util;

import co.marcin.novaguilds.api.util.reflect.FieldAccessor;
import co.marcin.novaguilds.impl.util.AbstractPacket;
import co.marcin.novaguilds.manager.ConfigManager;
import co.marcin.novaguilds.util.reflect.Reflections;
import org.apache.commons.lang.Validate;
import org.bukkit.Location;
import org.bukkit.util.Vector;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

/**
 * Credit for @DarkBlade12
 * https://github.com/DarkBlade12/ParticleEffect
 */
public class ParticlePacket extends AbstractPacket {
	protected static Class<?> packetClass;
	protected static Class<?> enumParticleClass;
	protected static Constructor<?> packetConstructor;
	protected static Field aField;
	protected static FieldAccessor<Float> bField;
	protected static FieldAccessor<Float> cField;
	protected static FieldAccessor<Float> dField;
	protected static FieldAccessor<Float> eField;
	protected static FieldAccessor<Float> fField;
	protected static FieldAccessor<Float> gField;
	protected static FieldAccessor<Float> hField;
	protected static FieldAccessor<Integer> iField;
	protected static FieldAccessor<Boolean> jField;
	protected static FieldAccessor<int[]> kField;

	static {
		try {
			packetClass = Reflections.getCraftClass(ConfigManager.getServerVersion().isOlderThan(ConfigManager.ServerVersion.MINECRAFT_1_7_R3)
					? "Packet63WorldParticles"
					: "PacketPlayOutWorldParticles");

			if(ConfigManager.getServerVersion().isNewerThan(ConfigManager.ServerVersion.MINECRAFT_1_7_R4)) {
				enumParticleClass = Reflections.getCraftClass("EnumParticle");
				jField = Reflections.getField(packetClass, "j", boolean.class);
				kField = Reflections.getField(packetClass, "k", int[].class);
			}
			packetConstructor = packetClass.getConstructor();

			aField = Reflections.getPrivateField(packetClass, "a");
			bField = Reflections.getField(packetClass, "b", float.class);
			cField = Reflections.getField(packetClass, "c", float.class);
			dField = Reflections.getField(packetClass, "d", float.class);
			eField = Reflections.getField(packetClass, "e", float.class);
			fField = Reflections.getField(packetClass, "f", float.class);
			gField = Reflections.getField(packetClass, "g", float.class);
			hField = Reflections.getField(packetClass, "h", float.class);
			iField = Reflections.getField(packetClass, "i", int.class);
		}
		catch(ClassNotFoundException | NoSuchMethodException | NoSuchFieldException e) {
			LoggerUtils.exception(e);
		}
	}

	/**
	 * The constructor
	 *
	 * @param center       center location
	 * @param effect       effect type
	 * @param offsetX      offset X
	 * @param offsetY      offset Y
	 * @param offsetZ      offset Z
	 * @param speed        speed
	 * @param amount       amount
	 * @param longDistance long distance flag
	 * @param data         particle data
	 * @throws IllegalArgumentException  when something goes wrong
	 * @throws IllegalAccessException    when something goes wrong
	 * @throws InvocationTargetException when something goes wrong
	 * @throws InstantiationException    when something goes wrong
	 */
	public ParticlePacket(Location center, ParticleEffect effect, float offsetX, float offsetY, float offsetZ, float speed, int amount, boolean longDistance, ParticleEffect.ParticleData data) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException, InstantiationException {
		Validate.isTrue(speed >= 0F, "Speed can't be lower than 0");
		Validate.isTrue(amount >= 0, "Amount can't be lower than 0");

		//Create packet
		packet = packetConstructor.newInstance();

		if(ConfigManager.getServerVersion().isOlderThan(ConfigManager.ServerVersion.MINECRAFT_1_8_R1)) {
			String exception = effect.getName();
			if(data != null) {
				exception += data.toString();
			}

			aField.set(packet, exception);
		}
		else {
			aField.set(packet, enumParticleClass.getEnumConstants()[effect.getId()]);
			jField.set(packet, longDistance);

			if(data != null) {
				int[] exception1 = data.getPacketData();
				kField.set(packet, effect == ParticleEffect.ITEM_CRACK
						? exception1
						: new int[]{exception1[0] | exception1[1] << 12});
			}
		}

		bField.set(packet, (float) center.getX());
		cField.set(packet, (float) center.getY());
		dField.set(packet, (float) center.getZ());
		eField.set(packet, offsetX);
		fField.set(packet, offsetY);
		gField.set(packet, offsetZ);
		hField.set(packet, speed);
		iField.set(packet, amount);
	}

	/**
	 * The constructor
	 *
	 * @param center       center location
	 * @param effect       effect type
	 * @param velocity     velocity vector
	 * @param speed        speed
	 * @param amount       amount
	 * @param longDistance long distance flag
	 * @param data         particle data
	 * @throws IllegalAccessException    when something goes wrong
	 * @throws InstantiationException    when something goes wrong
	 * @throws InvocationTargetException when something goes wrong
	 */
	public ParticlePacket(Location center, ParticleEffect effect, Vector velocity, float speed, int amount, boolean longDistance, ParticleEffect.ParticleData data) throws IllegalAccessException, InstantiationException, InvocationTargetException {
		this(center, effect, (float) velocity.getX(), (float) velocity.getY(), (float) velocity.getZ(), speed, amount, longDistance, data);
	}
}
