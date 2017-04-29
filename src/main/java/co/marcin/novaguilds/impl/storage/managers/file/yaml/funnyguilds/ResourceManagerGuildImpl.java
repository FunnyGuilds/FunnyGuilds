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
import co.marcin.novaguilds.api.storage.Storage;
import co.marcin.novaguilds.enums.Config;
import co.marcin.novaguilds.impl.basic.NovaGuildImpl;
import co.marcin.novaguilds.impl.storage.funnyguilds.YamlStorageImpl;
import co.marcin.novaguilds.impl.storage.managers.file.yaml.AbstractYAMLResourceManager;
import co.marcin.novaguilds.impl.util.converter.AbstractConverter;
import co.marcin.novaguilds.util.LoggerUtils;
import co.marcin.novaguilds.util.NumberUtils;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ResourceManagerGuildImpl extends AbstractYAMLResourceManager<NovaGuild> {
	/**
	 * The constructor
	 *
	 * @param storage the storage
	 */
	public ResourceManagerGuildImpl(Storage storage) {
		super(storage, NovaGuild.class, "guilds/");
	}

	@Override
	public List<NovaGuild> load() {
		final List<NovaGuild> list = new ArrayList<>();

		for(File guildFile : getFiles()) {
			FileConfiguration configuration = loadConfiguration(guildFile);

			if(configuration != null) {
				NovaGuild.LoadingWrapper<String> loadingWrapper = new NovaGuildImpl.LoadingWrapperImpl<>(new AbstractConverter<String, NovaGuild>() {
					@Override
					public NovaGuild convert(String s) {
						return ((YamlStorageImpl) getStorage()).guildMap.get(s);
					}
				});

				List<String> alliesList = configuration.getStringList("allies");
				List<String> warsList = configuration.getStringList("enemies");

				NovaGuild guild = new NovaGuildImpl(UUID.fromString(configuration.getString("uuid")), loadingWrapper);

				guild.setAdded();
				guild.setName(configuration.getString("name"));
				guild.setTag(configuration.getString("tag"));
				guild.setLeaderName(configuration.getString("owner"));
				guild.setPoints(configuration.getInt("points"));
				guild.setLives(configuration.getInt("lives"));
				guild.setTimeCreated(configuration.getLong("born") / 1000);
				guild.setInactiveTime(NumberUtils.systemSeconds());
				guild.setSlots(Config.GUILD_SLOTS_START.getInt());

				//Loading wrapper
				loadingWrapper.setAllies(alliesList);
				loadingWrapper.setWars(warsList);

				//home
				String[] homeSplit = configuration.getString("home").split(",");
				World homeWorld = plugin.getServer().getWorld(homeSplit[0]);

				if(homeWorld == null) {
					LoggerUtils.error("Found invalid world: " + homeSplit[0] + " (guild: " + guild.getName() + ")");
					guild.unload();
					continue;
				}

				for(String member : configuration.getStringList("members")) {
					((YamlStorageImpl) getStorage()).playerGuildMap.put(member, guild);
				}

				((YamlStorageImpl) getStorage()).guildMap.put(guild.getName(), guild);
				Location homeLocation = new Location(homeWorld, Integer.parseInt(homeSplit[1]), Integer.parseInt(homeSplit[2]), Integer.parseInt(homeSplit[3]));
				guild.setHome(homeLocation);
				guild.setUnchanged();
				list.add(guild);
			}
		}

		return list;
	}

	@Override
	public boolean save(NovaGuild guild) {
		throw new IllegalArgumentException("Not supported");
	}

	@Override
	public boolean remove(NovaGuild guild) {
		throw new IllegalArgumentException("Not supported");
	}

	@Override
	public File getFile(NovaGuild guild) {
		return new File(getDirectory(), guild.getName() + ".yml");
	}
}
