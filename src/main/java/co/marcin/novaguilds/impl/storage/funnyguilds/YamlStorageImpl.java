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

package co.marcin.novaguilds.impl.storage.funnyguilds;

import co.marcin.novaguilds.api.basic.NovaGuild;
import co.marcin.novaguilds.api.basic.NovaRank;
import co.marcin.novaguilds.exception.StorageConnectionFailedException;
import co.marcin.novaguilds.impl.storage.managers.AbstractResourceManager;
import co.marcin.novaguilds.impl.storage.managers.file.yaml.funnyguilds.ResourceManagerGuildImpl;
import co.marcin.novaguilds.impl.storage.managers.file.yaml.funnyguilds.ResourceManagerPlayerImpl;
import co.marcin.novaguilds.impl.storage.managers.file.yaml.funnyguilds.ResourceManagerRegionImpl;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class YamlStorageImpl extends co.marcin.novaguilds.impl.storage.YamlStorageImpl {
	public Map<String, NovaGuild> playerGuildMap = new HashMap<>();
	public Map<String, NovaGuild> guildMap = new HashMap<>();

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
	public boolean setUp() {
		return getDirectory().exists();
	}

	@Override
	public void registerManagers() {
		new ResourceManagerGuildImpl(this);
		new ResourceManagerPlayerImpl(this);
		new ResourceManagerRegionImpl(this);
		new AbstractResourceManager<NovaRank>(this, NovaRank.class) {
			@Override
			public List<NovaRank> load() {
				return new ArrayList<>();
			}

			@Override
			public boolean save(NovaRank novaRank) {
				throw new IllegalArgumentException("Not supported");
			}

			@Override
			public void add(NovaRank novaRank) {
				throw new IllegalArgumentException("Not supported");
			}

			@Override
			public boolean remove(NovaRank novaRank) {
				throw new IllegalArgumentException("Not supported");
			}
		};
	}

	@Override
	public void save() {

	}
}
