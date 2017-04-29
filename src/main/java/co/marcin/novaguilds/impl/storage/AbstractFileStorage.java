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
import co.marcin.novaguilds.util.LoggerUtils;

import java.io.File;

public abstract class AbstractFileStorage extends AbstractStorage {
	private final File dataDirectory;
	private final File playersDirectory;
	private final File guildsDirectory;
	private final File regionsDirectory;
	private final File ranksDirectory;

	/**
	 * AbstractFileStorage constructor
	 *
	 * @param dataDirectory data directory
	 * @throws StorageConnectionFailedException when something goes wrong
	 */
	public AbstractFileStorage(File dataDirectory) throws StorageConnectionFailedException {
		this.dataDirectory = dataDirectory;
		playersDirectory = new File(dataDirectory, "player/");
		guildsDirectory = new File(dataDirectory, "guild/");
		regionsDirectory = new File(dataDirectory, "region/");
		ranksDirectory = new File(dataDirectory, "rank/");

		if(!setUp()) {
			throw new StorageConnectionFailedException("Failed creating directories");
		}
	}

	@Override
	public boolean setUp() {
		if(!dataDirectory.exists()) {
			if(dataDirectory.mkdir()) {
				LoggerUtils.info("Data directory created");
			}
		}

		if(dataDirectory.exists()) {
			if(!playersDirectory.exists()) {
				if(playersDirectory.mkdir()) {
					LoggerUtils.info("Players directory created");
				}
			}

			if(!guildsDirectory.exists()) {
				if(guildsDirectory.mkdir()) {
					LoggerUtils.info("Guilds directory created");
				}
			}

			if(!regionsDirectory.exists()) {
				if(regionsDirectory.mkdir()) {
					LoggerUtils.info("Regions directory created");
				}
			}

			if(!ranksDirectory.exists()) {
				if(ranksDirectory.mkdir()) {
					LoggerUtils.info("Ranks directory created");
				}
			}
		}
		else {
			LoggerUtils.error("Could not setup directories!");
			LoggerUtils.error("Switching to secondary data storage type!");
			return false;
		}

		return true;
	}

	/**
	 * Gets data directory
	 *
	 * @return the directory
	 */
	public final File getDirectory() {
		return dataDirectory;
	}
}
