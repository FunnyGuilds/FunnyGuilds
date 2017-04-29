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

package co.marcin.novaguilds.api.basic;

import co.marcin.novaguilds.api.storage.Resource;
import org.bukkit.Location;

import java.util.List;

public interface NovaHologram extends Resource {
	/**
	 * Gets the name of the hologram
	 *
	 * @return the name string
	 */
	String getName();

	/**
	 * Gets the location
	 *
	 * @return Location instance
	 */
	Location getLocation();

	/**
	 * Gets a list of lines
	 *
	 * @return the list
	 */
	List<String> getLines();

	/**
	 * Sets the name
	 *
	 * @param name new name string
	 */
	void setName(String name);

	/**
	 * Sets the location
	 *
	 * @param location new location
	 */
	void setLocation(Location location);

	/**
	 * Adds a line
	 *
	 * @param line the string
	 */
	void addLine(String line);

	/**
	 * Clears the lines
	 */
	void clearLines();

	/**
	 * Adds a list of lines
	 *
	 * @param lines the list
	 */
	void addLine(List<String> lines);

	/**
	 * Refreshes the hologram
	 */
	void refresh();

	/**
	 * Teleports the hologram to a new location
	 *
	 * @param location the location
	 */
	void teleport(Location location);

	/**
	 * Creates the hologram
	 */
	void create();

	/**
	 * Deletes the hologram
	 */
	void delete();

	/**
	 * Returns if the hologram is a TOP hologram
	 *
	 * @return true if TOP
	 */
	boolean isTop();

	/**
	 * Returns if the hologram is deleted
	 *
	 * @return true if deleted
	 */
	boolean isDeleted();

	/**
	 * Sets the boolean of being a TOP hologram
	 *
	 * @param top the boolean
	 */
	void setTop(boolean top);
}
