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

package co.marcin.novaguilds.impl.storage;

import co.marcin.novaguilds.NovaGuilds;
import co.marcin.novaguilds.api.basic.NovaGuild;
import co.marcin.novaguilds.api.storage.Migrant;
import co.marcin.novaguilds.api.storage.Resource;
import co.marcin.novaguilds.api.storage.ResourceManager;
import co.marcin.novaguilds.api.storage.Storage;
import co.marcin.novaguilds.enums.Config;
import co.marcin.novaguilds.util.LoggerUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class MigrantImpl implements Migrant {
	private final Storage fromStorage;
	private final Storage toStorage;

	/**
	 * The constructor
	 *
	 * @param fromStorage from storage
	 * @param toStorage   to storage
	 */
	public MigrantImpl(Storage fromStorage, Storage toStorage) {
		this.fromStorage = fromStorage;
		this.toStorage = toStorage;
	}

	@Override
	public Storage getFromStorage() {
		return fromStorage;
	}

	@Override
	public Storage getToStorage() {
		return toStorage;
	}

	@Override
	public void migrate() {
		boolean deleteInvalidTemp = Config.DELETEINVALID.getBoolean();
		Config.DELETEINVALID.set(false);
		Map<Class<? extends Resource>, Collection<? extends Resource>> dataMap = new LinkedHashMap<>();

		for(Map.Entry<Class<? extends Resource>, ResourceManager<? extends Resource>> fromResourceManagerEntry : getFromStorage().getResourceManagers().entrySet()) {
			List<? extends Resource> data = fromResourceManagerEntry.getValue().load();
			dataMap.put(fromResourceManagerEntry.getKey(), data);
		}

		for(Map.Entry<Class<? extends Resource>, Collection<? extends Resource>> entry : dataMap.entrySet()) {
			Collection<? extends Resource> data = entry.getValue();
			ResourceManager toResourceManager = getToStorage().getResourceManager(entry.getKey());

			for(Resource resource : new ArrayList<>(data)) {
				resource.setNotAdded();

				if(entry.getKey() == NovaGuild.class && !NovaGuilds.getInstance().getGuildManager().postCheck((NovaGuild) resource)) {
					LoggerUtils.error(resource.getUUID() + " failed postCheck");
					data.remove(resource);
				}
			}

			//noinspection unchecked
			toResourceManager.addToSaveQueue(data);
			LoggerUtils.info("Migrating " + data.size() + " of type " + entry.getKey().getSimpleName());
		}

		Config.DELETEINVALID.set(deleteInvalidTemp);
	}

	@Override
	public void save() {
		for(ResourceManager resourceManager : getToStorage().getResourceManagers().values()) {
			resourceManager.executeSave();
		}
	}
}
