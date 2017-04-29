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

package co.marcin.novaguilds.api.util;

public interface BlockPositionWrapper {
	/**
	 * Gets x coordinate
	 *
	 * @return integer
	 */
	int getX();

	/**
	 * Gets y coordinate
	 *
	 * @return integer
	 */
	int getY();

	/**
	 * Gets z coordinate
	 *
	 * @return integer
	 */
	int getZ();

	/**
	 * Sets x coordinate
	 *
	 * @param x integer
	 */
	void setX(int x);

	/**
	 * Sets y coordinate
	 *
	 * @param y integer
	 */
	void setY(int y);

	/**
	 * Sets z coordinate
	 *
	 * @param z integer
	 */
	void setZ(int z);

	/**
	 * Gets BlockPosition object
	 *
	 * @return BlockPosition instance
	 */
	Object getBlockPosition();
}
