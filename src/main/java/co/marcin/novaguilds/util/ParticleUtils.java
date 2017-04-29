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

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

public class ParticleUtils {
	/**
	 * Creates supernova effect where an entity exists
	 *
	 * @param entity target entity
	 * @throws IllegalAccessException    when something goes wrong
	 * @throws InvocationTargetException when something goes wrong
	 * @throws InstantiationException    when something goes wrong
	 */
	public static void createSuperNova(Entity entity) throws IllegalAccessException, InvocationTargetException, InstantiationException {
		createSuperNova(entity.getLocation());
	}

	/**
	 * Gets circle vectors
	 *
	 * @param radius    radius of a circle
	 * @param precision precision (amount of vectors)
	 * @return list of vectors
	 */
	public static List<Vector> getCircleVectors(int radius, int precision) {
		final List<Vector> list = new ArrayList<>();

		for(int i = 0; i < precision; i++) {
			double p1 = (i * Math.PI) / (precision / 2);
			double p2 = (((i == 0) ? precision : i - 1) * Math.PI) / (precision / 2);

			double x1 = Math.cos(p1) * radius;
			double x2 = Math.cos(p2) * radius;
			double z1 = Math.sin(p1) * radius;
			double z2 = Math.sin(p2) * radius;

			Vector vec = new Vector(x2 - x1, 0, z2 - z1);
			list.add(vec);
		}

		return list;
	}

	/**
	 * Creates supernova effect
	 *
	 * @param location target location
	 * @throws IllegalAccessException    when something goes wrong
	 * @throws InstantiationException    when something goes wrong
	 * @throws InvocationTargetException when something goes wrong
	 */
	public static void createSuperNova(Location location) throws IllegalAccessException, InstantiationException, InvocationTargetException {
		float speed = 1F;
		double range = 15D;

		location = location.clone();
		location.add(0, 0.5, 0);

		for(Vector vector : getCircleVectors(15, 100)) {
			ParticleEffect.SNOW_SHOVEL.send(location, vector, speed, 0, null, range);
		}
	}

	/**
	 * Gets players in a radius
	 *
	 * @param center center location
	 * @param range  range
	 * @return list of players
	 */
	public static List<Player> getPlayersInRadius(Location center, double range) {
		if(range < 1.0D) {
			throw new IllegalArgumentException("The range is lower than 1");
		}

		double squared = range * range;
		List<Player> list = new ArrayList<>();

		for(Player player : CompatibilityUtils.getOnlinePlayers()) {
			if(player.getWorld().equals(center.getWorld()) && player.getLocation().distanceSquared(center) <= squared) {
				list.add(player);
			}
		}

		return list;
	}
}
