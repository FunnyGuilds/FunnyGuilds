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

import java.util.Collection;
import java.util.List;

public interface ResourceManager<T extends Resource> {
	/**
	 * Gets the class of Resource
	 *
	 * @return the class
	 */
	Class<T> getClazz();

	/**
	 * Loads data
	 *
	 * @return list with data
	 */
	List<T> load();

	/**
	 * Saves data
	 *
	 * @param t instance
	 * @return true if saved
	 */
	boolean save(T t);

	/**
	 * Saves data from a list
	 *
	 * @param list the list
	 * @return amount of saved items
	 */
	Integer save(Collection<T> list);

	/**
	 * Adds data
	 *
	 * @param t instance
	 */
	void add(T t);

	/**
	 * Removes data
	 *
	 * @param t instance
	 * @return true on success
	 */
	boolean remove(T t);

	/**
	 * Removes data from a list
	 *
	 * @param list the list
	 * @return amount of removed items
	 */
	int remove(Collection<T> list);

	/**
	 * Adds an object to save queue
	 *
	 * @param t instance
	 */
	void addToSaveQueue(T t);

	/**
	 * Adds a list of object to save queue
	 *
	 * @param list list of resources
	 */
	void addToSaveQueue(Collection<T> list);

	/**
	 * Removes an object from save queue
	 *
	 * @param t instance
	 */
	void removeFromSaveQueue(T t);

	/**
	 * Checks if an object is in save queue
	 *
	 * @param t instance
	 * @return boolean
	 */
	boolean isInSaveQueue(T t);

	/**
	 * Adds an object to removal queue
	 *
	 * @param t instance
	 */
	void addToRemovalQueue(T t);

	/**
	 * Checks if an object is in removal queue
	 *
	 * @param t instance
	 * @return boolean
	 */
	boolean isInRemovalQueue(T t);

	/**
	 * Actually deletes queued data
	 *
	 * @return amount of removed items
	 */
	int executeRemoval();

	/**
	 * Saves queued data
	 *
	 * @return amount of saved items
	 */
	int executeSave();
}
