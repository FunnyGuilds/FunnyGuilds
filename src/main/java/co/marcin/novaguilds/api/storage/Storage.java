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

package co.marcin.novaguilds.api.storage;

import java.util.Map;

public interface Storage {
	/**
	 * Set up the directories, connect to database etc.
	 *
	 * @return true if successful
	 */
	boolean setUp();

	/**
	 * Gets resource manager
	 *
	 * @param clazz type class
	 * @param <T>   type parameter
	 * @return the manager
	 */
	<T extends Resource> ResourceManager<T> getResourceManager(Class<T> clazz);

	/**
	 * Gets all resource managers
	 *
	 * @return list of resource managers
	 */
	Map<Class<? extends Resource>, ResourceManager<? extends Resource>> getResourceManagers();

	/**
	 * Registers resource manager
	 *
	 * @param clazz           type class
	 * @param resourceManager the manager
	 * @param <T>             type parameter
	 */
	<T extends Resource> void registerResourceManager(Class<T> clazz, ResourceManager<T> resourceManager);

	/**
	 * Registers managers
	 */
	void registerManagers();

	/**
	 * Saves all data
	 */
	void save();
}
