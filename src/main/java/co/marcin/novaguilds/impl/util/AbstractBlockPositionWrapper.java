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

import co.marcin.novaguilds.api.util.BlockPositionWrapper;
import org.bukkit.Location;

public abstract class AbstractBlockPositionWrapper implements BlockPositionWrapper {
	private int x;
	private int y;
	private int z;

	/**
	 * The constructor
	 *
	 * @param location location object
	 */
	public AbstractBlockPositionWrapper(Location location) {
		this(location.getBlockX(), location.getBlockY(), location.getBlockZ());
	}

	/**
	 * The constructor
	 *
	 * @param x x coordinate
	 * @param y y coordinate
	 * @param z z coordinate
	 */
	public AbstractBlockPositionWrapper(int x, int y, int z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	/**
	 * The constructor
	 *
	 * @param blockPosition NMS BlockPosition object
	 */
	public AbstractBlockPositionWrapper(Object blockPosition) {

	}

	@Override
	public int getX() {
		return x;
	}

	@Override
	public int getY() {
		return y;
	}

	@Override
	public int getZ() {
		return z;
	}

	@Override
	public void setX(int x) {
		this.x = x;
	}

	@Override
	public void setY(int y) {
		this.y = y;
	}

	@Override
	public void setZ(int z) {
		this.z = z;
	}
}
