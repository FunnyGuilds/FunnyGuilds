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

import org.bukkit.Location;

public interface Schematic {
	/**
	 * Paste the schematic at given location
	 *
	 * @param location the location
	 */
	void paste(Location location);

	/**
	 * Gets the width
	 *
	 * @return width
	 */
	short getWidth();

	/**
	 * Gets the height
	 *
	 * @return height
	 */
	short getHeight();

	/**
	 * Gets the length
	 *
	 * @return length
	 */
	short getLength();

	/**
	 * Gets the name
	 *
	 * @return file name
	 */
	String getName();
}
