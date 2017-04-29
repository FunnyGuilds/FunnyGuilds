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

package co.marcin.novaguilds.impl.storage.managers.file.yaml.funnyguilds;

import co.marcin.novaguilds.api.basic.NovaGuild;
import co.marcin.novaguilds.api.basic.NovaPlayer;
import co.marcin.novaguilds.api.storage.Storage;
import co.marcin.novaguilds.impl.basic.NovaPlayerImpl;
import co.marcin.novaguilds.impl.storage.funnyguilds.YamlStorageImpl;
import co.marcin.novaguilds.impl.storage.managers.file.yaml.AbstractYAMLResourceManager;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ResourceManagerPlayerImpl extends AbstractYAMLResourceManager<NovaPlayer> {
	/**
	 * The constructor
	 *
	 * @param storage the storage
	 */
	public ResourceManagerPlayerImpl(Storage storage) {
		super(storage, NovaPlayer.class, "users/");
	}

	@Override
	public List<NovaPlayer> load() {
		final List<NovaPlayer> list = new ArrayList<>();

		for(File playerFile : getFiles()) {
			if(!playerFile.isFile()) {
				continue;
			}

			FileConfiguration configuration = loadConfiguration(playerFile);

			if(configuration != null) {
//				LoggerUtils.debug(playerFile.getName());
				NovaPlayer nPlayer = new NovaPlayerImpl(UUID.fromString(configuration.getString("uuid")));
				nPlayer.setAdded();
				nPlayer.setName(configuration.getString("name"));

//				List<String> invitedToStringList = configuration.getStringList("invitedto");
//				List<NovaGuild> invitedToList = new UUIDOrNameToGuildConverterImpl().convert(invitedToStringList);

//				nPlayer.setInvitedTo(invitedToList);
				nPlayer.setPoints(configuration.getInt("points"));
				nPlayer.setKills(configuration.getInt("kills"));
				nPlayer.setDeaths(configuration.getInt("deaths"));

				//Set the guild
				NovaGuild guild = ((YamlStorageImpl) getStorage()).playerGuildMap.get(nPlayer.getName());

				if(guild != null) {
					guild.addPlayer(nPlayer);
				}

				nPlayer.setUnchanged();
				list.add(nPlayer);
			}
		}

		return list;
	}

	@Override
	public boolean save(NovaPlayer novaPlayer) {
		throw new IllegalArgumentException("Not supported");
	}

	@Override
	public boolean remove(NovaPlayer novaPlayer) {
		throw new IllegalArgumentException("Not supported");
	}

	@Override
	public File getFile(NovaPlayer novaPlayer) {
		throw new IllegalArgumentException("Not supported");
	}
}
