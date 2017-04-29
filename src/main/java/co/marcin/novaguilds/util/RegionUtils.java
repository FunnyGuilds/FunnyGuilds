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

import co.marcin.novaguilds.NovaGuilds;
import co.marcin.novaguilds.api.basic.NovaRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.awt.Rectangle;
import java.awt.geom.Area;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public final class RegionUtils {
	private RegionUtils() {

	}

	/**
	 * Gets all blocks on a border of a region
	 * Only highest blocks
	 *
	 * @param region region
	 * @return list of blocks
	 */
	public static List<Block> getBorderBlocks(NovaRegion region) {
		return getBorderBlocks(region.getCorner(0), region.getCorner(1));
	}

	/**
	 * Gets all blocks on a border of a rectangle specified by two opposite corners
	 * Only highest blocks
	 *
	 * @param l1 corner 1
	 * @param l2 corner 2
	 * @return list of blocks
	 */
	public static List<Block> getBorderBlocks(Location l1, Location l2) {
		final List<Block> blocks = new ArrayList<>();

		//World world = region.getWorld();
		World world = l1.getWorld() == null ? Bukkit.getWorlds().get(0) : l1.getWorld();

		int x;
		int z;

		int xs;
		int zs;

		int x1 = l1.getBlockX();
		int x2 = l2.getBlockX();
		int z1 = l1.getBlockZ();
		int z2 = l2.getBlockZ();

		int t;

		int difX = Math.abs(x1 - x2) + 1;
		int difZ = Math.abs(z1 - z2) + 1;

		if(l1.getBlockX() < l2.getBlockX()) {
			xs = l1.getBlockX();
		}
		else {
			xs = l2.getBlockX();
		}

		if(l1.getBlockZ() < l2.getBlockZ()) {
			zs = l1.getBlockZ();
		}
		else {
			zs = l2.getBlockZ();
		}

		for(t = 0; t < difX; t++) {
			x = xs + t;

			Block highestBlock1 = world.getHighestBlockAt(x, z1);
			Block highestBlock2 = world.getHighestBlockAt(x, z2);
			int highest1 = highestBlock1.getY() - (highestBlock1.getType() == Material.SNOW ? 0 : 1);
			int highest2 = highestBlock2.getY() - (highestBlock2.getType() == Material.SNOW ? 0 : 1);

			blocks.add(world.getBlockAt(x, highest1, z1));
			blocks.add(world.getBlockAt(x, highest2, z2));
		}


		for(t = 0; t < difZ; t++) {
			z = zs + t;

			Block highestBlock1 = world.getHighestBlockAt(x1, z);
			Block highestBlock2 = world.getHighestBlockAt(x2, z);
			int highest1 = highestBlock1.getY() - (highestBlock1.getType() == Material.SNOW ? 0 : 1);
			int highest2 = highestBlock2.getY() - (highestBlock2.getType() == Material.SNOW ? 0 : 1);

			blocks.add(world.getBlockAt(x1, highest1, z));
			blocks.add(world.getBlockAt(x2, highest2, z));
		}

		return blocks;
	}

	/**
	 * Sends a true block to a player
	 *
	 * @param player target player
	 * @param block  block
	 */
	@SuppressWarnings("deprecation")
	public static void resetBlock(Player player, Block block) {
		if(player == null || block == null) {
			return;
		}

		Material material = block.getWorld().getBlockAt(block.getLocation()).getType();
		byte data = block.getWorld().getBlockAt(block.getLocation()).getData();

		player.sendBlockChange(block.getLocation(), material, data);
	}

	/**
	 * Gets center location of a rectangle specified by two opposite corners
	 *
	 * @param l1 corner 1
	 * @param l2 corner 2
	 * @return center location
	 */
	public static Location getCenterLocation(Location l1, Location l2) {
		int width = Math.abs(l1.getBlockX() - l2.getBlockX());
		int height = Math.abs(l1.getBlockZ() - l2.getBlockZ());

		int newX = l1.getBlockX() + (l1.getBlockX() < l2.getBlockX() ? width : -width) / 2;
		int newZ = l1.getBlockZ() + (l1.getBlockZ() < l2.getBlockZ() ? height : -height) / 2;

		return new Location(l1.getWorld(), newX, 0, newZ);
	}

	/**
	 * Checks the area of a rectangle specified by two opposite corners
	 *
	 * @param l1 corner 1
	 * @param l2 corner 2
	 * @return the area
	 */
	public static int checkRegionSize(Location l1, Location l2) {
		int x1 = l1.getBlockX();
		int x2 = l2.getBlockX();
		int z1 = l1.getBlockZ();
		int z2 = l2.getBlockZ();

		int difX = Math.abs(x1 - x2) + 1;
		int difZ = Math.abs(z1 - z2) + 1;

		return difX * difZ;
	}

	/**
	 * Converts ConfigurationSection to a location
	 * valid key names are:
	 * world, x, y, z, yaw, pitch
	 *
	 * @param section section
	 * @return location
	 */
	public static Location sectionToLocation(ConfigurationSection section) {
		World world;
		try {
			world = NovaGuilds.getInstance().getServer().getWorld(UUID.fromString(section.getString("world")));
		}
		catch(IllegalArgumentException e) {
			world = NovaGuilds.getInstance().getServer().getWorld(section.getString("world"));
		}

		double x = section.getDouble("x");
		double y = section.getDouble("y");
		double z = section.getDouble("z");
		float yaw = (float) section.getDouble("yaw");
		float pitch = (float) section.getDouble("pitch");

		if(world == null) {
			return null;
		}

		return new Location(world, x, y, z, yaw, pitch);
	}

	/**
	 * Deserializes 2D location string
	 * separated by a semicolon
	 * (x;z)
	 *
	 * @param string serialized location
	 * @return deserialized location
	 */
	public static Location deserializeLocation2D(String string) {
		String[] split = string.split(";");
		return new Location(null, Integer.parseInt(split[0]), 0, Integer.parseInt(split[1]));
	}

	/**
	 * Converts a WorldGuard region to Area
	 *
	 * @param region region
	 * @return area
	 */
	public static Area toArea(ProtectedRegion region) {
		int x = region.getMinimumPoint().getBlockX();
		int z = region.getMinimumPoint().getBlockZ();
		int width = region.getMaximumPoint().getBlockX() - x;
		int height = region.getMaximumPoint().getBlockZ() - z;
		return new Area(new Rectangle(x, z, width, height));
	}
}
