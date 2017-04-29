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

import co.marcin.novaguilds.exception.StorageConnectionFailedException;
import co.marcin.novaguilds.impl.storage.managers.file.yaml.ResourceManagerGuildImpl;
import co.marcin.novaguilds.impl.storage.managers.file.yaml.ResourceManagerPlayerImpl;
import co.marcin.novaguilds.impl.storage.managers.file.yaml.ResourceManagerRankImpl;
import co.marcin.novaguilds.impl.storage.managers.file.yaml.ResourceManagerRegionImpl;

import java.io.File;

public class YamlStorageImpl extends AbstractFileStorage {
	/**
	 * The constructor
	 *
	 * @param dataDirectory data directory
	 * @throws StorageConnectionFailedException when something goes wrong
	 */
	public YamlStorageImpl(File dataDirectory) throws StorageConnectionFailedException {
		super(dataDirectory);
	}

	@Override
	public void registerManagers() {
		new ResourceManagerGuildImpl(this);
		new ResourceManagerPlayerImpl(this);
		new ResourceManagerRankImpl(this);
		new ResourceManagerRegionImpl(this);
	}
}
