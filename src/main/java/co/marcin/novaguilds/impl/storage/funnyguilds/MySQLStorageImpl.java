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
import co.marcin.novaguilds.enums.Config;
import co.marcin.novaguilds.enums.PreparedStatements;
import co.marcin.novaguilds.exception.StorageConnectionFailedException;
import co.marcin.novaguilds.impl.storage.managers.AbstractResourceManager;
import co.marcin.novaguilds.impl.storage.managers.database.funnyguilds.ResourceManagerGuildImpl;
import co.marcin.novaguilds.impl.storage.managers.database.funnyguilds.ResourceManagerPlayerImpl;
import co.marcin.novaguilds.impl.storage.managers.database.funnyguilds.ResourceManagerRegionImpl;
import co.marcin.novaguilds.util.LoggerUtils;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class MySQLStorageImpl extends co.marcin.novaguilds.impl.storage.MySQLStorageImpl {
	public Map<String, NovaGuild> guildMap = new HashMap<>();

	/**
	 * Creates a new MySQL instance
	 *
	 * @param hostname Name of the host
	 * @param port     Port number
	 * @param database Database name
	 * @param username Username
	 * @param password Password
	 * @throws StorageConnectionFailedException when something goes wrong
	 */
	public MySQLStorageImpl(String hostname, String port, String database, String username, String password) throws StorageConnectionFailedException {
		super(hostname, port, database, username, password);
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
	protected void prepareStatements() {
		try {
			long nanoTime = System.nanoTime();
			LoggerUtils.info("Preparing statements...");
			preparedStatementMap.clear();
			connect();

			//Guilds select
			String guildsSelectSQL = "SELECT * FROM `" + Config.MYSQL_PREFIX.getString() + "guilds`";
			PreparedStatement guildsSelect = getConnection().prepareStatement(guildsSelectSQL);
			preparedStatementMap.put(PreparedStatements.GUILDS_SELECT, guildsSelect);

			//Players select
			String playerSelectSQL = "SELECT * FROM `" + Config.MYSQL_PREFIX.getString() + "users`";
			PreparedStatement playersSelect = getConnection().prepareStatement(playerSelectSQL);
			preparedStatementMap.put(PreparedStatements.PLAYERS_SELECT, playersSelect);

			//Regions select
			String regionsSelectSQL = "SELECT * FROM `" + Config.MYSQL_PREFIX.getString() + "regions`";
			PreparedStatement regionsSelect = getConnection().prepareStatement(regionsSelectSQL);
			preparedStatementMap.put(PreparedStatements.REGIONS_SELECT, regionsSelect);

			//Log
			LoggerUtils.info("Statements prepared in " + TimeUnit.MILLISECONDS.convert((System.nanoTime() - nanoTime), TimeUnit.NANOSECONDS) / 1000.0 + "s");
		}
		catch(SQLException e) {
			LoggerUtils.exception(e);
		}
	}

	@Override
	protected void analyze() {

	}
}
