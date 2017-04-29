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

package co.marcin.novaguilds.impl.storage.managers.file.yaml;

import co.marcin.novaguilds.api.storage.Resource;
import co.marcin.novaguilds.api.storage.Storage;
import co.marcin.novaguilds.impl.storage.managers.file.AbstractFileResourceManager;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

public abstract class AbstractYAMLResourceManager<T extends Resource> extends AbstractFileResourceManager<T> {
	/**
	 * The constructor
	 *
	 * @param storage       the storage
	 * @param clazz         type class
	 * @param directoryPath the path
	 */
	protected AbstractYAMLResourceManager(Storage storage, Class clazz, String directoryPath) {
		super(storage, clazz, directoryPath);
	}

	/**
	 * Gets data from YAML
	 *
	 * @param t instance
	 * @return configuration
	 */
	protected FileConfiguration getData(T t) {
		return loadConfiguration(getFile(t));
	}

	/**
	 * Loads FileConfiguration from file
	 *
	 * @param file the file
	 * @return the configuration
	 */
	protected FileConfiguration loadConfiguration(File file) {
		return file.exists() ? YamlConfiguration.loadConfiguration(file) : null;
	}
}
