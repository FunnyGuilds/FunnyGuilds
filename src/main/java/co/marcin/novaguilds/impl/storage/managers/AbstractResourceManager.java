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

package co.marcin.novaguilds.impl.storage.managers;

import co.marcin.novaguilds.NovaGuilds;
import co.marcin.novaguilds.api.storage.Resource;
import co.marcin.novaguilds.api.storage.ResourceManager;
import co.marcin.novaguilds.api.storage.Storage;

import java.util.Collection;
import java.util.HashSet;

public abstract class AbstractResourceManager<T extends Resource> implements ResourceManager<T> {
	protected final NovaGuilds plugin = NovaGuilds.getInstance();
	private final Class<T> clazz;
	private final Storage storage;
	private final Collection<T> removalQueue = new HashSet<>();
	private final Collection<T> saveQueue = new HashSet<>();

	/**
	 * The constructor
	 *
	 * @param storage the storage
	 * @param clazz   type class
	 */
	protected AbstractResourceManager(Storage storage, Class<T> clazz) {
		this.storage = storage;
		this.clazz = clazz;
		register(clazz);
	}

	@Override
	public Class<T> getClazz() {
		return clazz;
	}

	@Override
	public Integer save(Collection<T> list) {
		int count = 0;

		for(T t : list) {
			if(save(t)) {
				count++;
			}
		}

		return count;
	}

	@Override
	public int remove(Collection<T> list) {
		int count = 0;

		for(T t : list) {
			if(remove(t)) {
				count++;
			}
		}

		return count;
	}

	/**
	 * Gets the storage
	 *
	 * @return the storage
	 */
	protected Storage getStorage() {
		return storage;
	}

	/**
	 * Registers the manager
	 *
	 * @param clazz type class
	 */
	private void register(Class clazz) {
		getStorage().registerResourceManager(clazz, this);
	}

	@Override
	public int executeRemoval() {
		int count = remove(removalQueue);
		removalQueue.clear();
		return count;
	}

	@Override
	public void addToSaveQueue(T t) {
		saveQueue.add(t);
	}

	@Override
	public void addToSaveQueue(Collection<T> list) {
		for(T t : list) {
			addToSaveQueue(t);
		}
	}

	@Override
	public void removeFromSaveQueue(T t) {
		if(isInSaveQueue(t)) {
			saveQueue.remove(t);
		}
	}

	@Override
	public boolean isInSaveQueue(T t) {
		return saveQueue.contains(t);
	}

	@Override
	public void addToRemovalQueue(T t) {
		removalQueue.add(t);
	}

	@Override
	public boolean isInRemovalQueue(T t) {
		return removalQueue.contains(t);
	}

	@Override
	public int executeSave() {
		int count = save(saveQueue);
		saveQueue.clear();
		return count;
	}
}
