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
import co.marcin.novaguilds.api.basic.NovaPlayer;
import co.marcin.novaguilds.api.storage.Storage;
import co.marcin.novaguilds.enums.PreparedStatements;
import co.marcin.novaguilds.impl.basic.NovaPlayerImpl;
import co.marcin.novaguilds.impl.storage.funnyguilds.MySQLStorageImpl;
import co.marcin.novaguilds.util.LoggerUtils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ResourceManagerPlayerImpl extends AbstractDatabaseResourceManager<NovaPlayer> {
	/**
	 * The constructor
	 *
	 * @param storage the storage
	 */
	public ResourceManagerPlayerImpl(Storage storage) {
		super(storage, NovaPlayer.class, "users");
	}

	@Override
	public List<NovaPlayer> load() {
		getStorage().connect();
		final List<NovaPlayer> list = new ArrayList<>();

		try {
			ResultSet res = getStorage().getPreparedStatement(PreparedStatements.PLAYERS_SELECT).executeQuery();
			while(res.next()) {
				NovaPlayer nPlayer = new NovaPlayerImpl(UUID.fromString(res.getString("uuid")));
				nPlayer.setAdded();

				nPlayer.setName(res.getString("name"));
				nPlayer.setPoints(res.getInt("points"));
				nPlayer.setKills(res.getInt("kills"));
				nPlayer.setDeaths(res.getInt("deaths"));

				//Set the guild
				String guildString = res.getString("guild");
				if(guildString != null && !guildString.isEmpty()) {
					NovaGuild guild = ((MySQLStorageImpl) getStorage()).guildMap.get(guildString);

					if(guild != null) {
						guild.addPlayer(nPlayer);
					}
				}

				nPlayer.setUnchanged();
				list.add(nPlayer);
			}
		}
		catch(SQLException e) {
			LoggerUtils.exception(e);
		}

		return list;
	}

	@Override
	public boolean save(NovaPlayer guild) {
		throw new IllegalArgumentException("Not supported");
	}

	@Override
	public void add(NovaPlayer guild) {
		throw new IllegalArgumentException("Not supported");
	}

	@Override
	public boolean remove(NovaPlayer guild) {
		throw new IllegalArgumentException("Not supported");
	}
}
