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

package co.marcin.novaguilds.impl.storage.managers.database.funnyguilds;

import co.marcin.novaguilds.api.basic.NovaGuild;
import co.marcin.novaguilds.api.storage.Storage;
import co.marcin.novaguilds.enums.Config;
import co.marcin.novaguilds.enums.PreparedStatements;
import co.marcin.novaguilds.impl.basic.NovaGuildImpl;
import co.marcin.novaguilds.impl.storage.funnyguilds.MySQLStorageImpl;
import co.marcin.novaguilds.util.LoggerUtils;
import co.marcin.novaguilds.util.NumberUtils;
import co.marcin.novaguilds.util.StringUtils;
import org.bukkit.Location;
import org.bukkit.World;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ResourceManagerGuildImpl extends AbstractDatabaseResourceManager<NovaGuild> {
	/**
	 * The constructor
	 *
	 * @param storage the storage
	 */
	public ResourceManagerGuildImpl(Storage storage) {
		super(storage, NovaGuild.class, "guilds");
	}

	@Override
	public List<NovaGuild> load() {
		getStorage().connect();
		final List<NovaGuild> list = new ArrayList<>();

		try {
			PreparedStatement statement = getStorage().getPreparedStatement(PreparedStatements.GUILDS_SELECT);
			ResultSet res = statement.executeQuery();

			while(res.next()) {
				String homeCoordinates = res.getString("home");

				Location homeLocation = null;
				if(!homeCoordinates.isEmpty()) {
					String[] homeSplit = org.apache.commons.lang.StringUtils.split(homeCoordinates, ',');
					if(homeSplit.length == 4) {
						String worldString = homeSplit[0];
						World world = plugin.getServer().getWorld(worldString);

						if(world != null) {
							int x = Integer.parseInt(homeSplit[1]);
							int y = Integer.parseInt(homeSplit[2]);
							int z = Integer.parseInt(homeSplit[3]);
							homeLocation = new Location(world, x, y, z);
						}
					}
				}

				//Loading wrapper
				NovaGuild.LoadingWrapper<String> loadingWrapper = new NovaGuildImpl.LoadingWrapper37MigrationImpl();
				loadingWrapper.setAllies(StringUtils.split(res.getString("allies"), ","));
				loadingWrapper.setWars(StringUtils.split(res.getString("enemies"), ","));

				NovaGuild guild = new NovaGuildImpl(UUID.fromString(res.getString("uuid")), loadingWrapper);

				guild.setAdded();
				guild.setId(1000);
				guild.setPoints(res.getInt("points"));
				guild.setName(res.getString("name"));
				guild.setTag(res.getString("tag"));
				guild.setLeaderName(res.getString("owner"));
				guild.setLives(res.getInt("lives"));
				guild.setTimeCreated(res.getLong("born") / 1000);
				guild.setHome(homeLocation);
				guild.setInactiveTime(NumberUtils.systemSeconds());
				guild.setSlots(Config.GUILD_SLOTS_START.getInt());

				//set unchanged
				guild.setUnchanged();
				((MySQLStorageImpl) getStorage()).guildMap.put(guild.getName(), guild);
				list.add(guild);
			}
		}
		catch(SQLException e) {
			LoggerUtils.info("An error occurred while loading guilds!");
			LoggerUtils.exception(e);
		}

		return list;
	}

	@Override
	public boolean save(NovaGuild guild) {
		throw new IllegalArgumentException("Not supported");
	}

	@Override
	public void add(NovaGuild guild) {
		throw new IllegalArgumentException("Not supported");
	}

	@Override
	public boolean remove(NovaGuild guild) {
		throw new IllegalArgumentException("Not supported");
	}
}
