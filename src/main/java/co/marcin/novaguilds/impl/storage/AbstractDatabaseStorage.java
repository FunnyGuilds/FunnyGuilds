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

import co.marcin.novaguilds.api.storage.Database;
import co.marcin.novaguilds.api.util.DatabaseAnalyzer;
import co.marcin.novaguilds.enums.Config;
import co.marcin.novaguilds.enums.DataStorageType;
import co.marcin.novaguilds.enums.PreparedStatements;
import co.marcin.novaguilds.impl.storage.managers.database.ResourceManagerGuildImpl;
import co.marcin.novaguilds.impl.storage.managers.database.ResourceManagerPlayerImpl;
import co.marcin.novaguilds.impl.storage.managers.database.ResourceManagerRankImpl;
import co.marcin.novaguilds.impl.storage.managers.database.ResourceManagerRegionImpl;
import co.marcin.novaguilds.impl.util.DatabaseAnalyzerImpl;
import co.marcin.novaguilds.util.IOUtils;
import co.marcin.novaguilds.util.LoggerUtils;
import org.apache.commons.lang.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public abstract class AbstractDatabaseStorage extends AbstractStorage implements Database {
	protected Connection connection;
	protected boolean firstConnect = true;
	protected final Map<PreparedStatements, PreparedStatement> preparedStatementMap = new HashMap<>();

	@Override
	public boolean checkConnection() throws SQLException {
		return connection != null && !connection.isClosed();
	}

	@Override
	public final Connection getConnection() {
		return connection;
	}

	@Override
	public boolean closeConnection() throws SQLException {
		if(connection == null) {
			return false;
		}

		connection.close();
		return true;
	}

	/**
	 * Reconnects
	 *
	 * @return true if success
	 */
	public abstract boolean connect();

	/**
	 * Returns generated key (id)
	 *
	 * @param statement The statement
	 * @return Generated id
	 */
	public abstract Integer returnGeneratedKey(Statement statement);

	/**
	 * Says whether does the implementation support statement's generated keys or not
	 *
	 * @return true if yes
	 */
	public abstract boolean isStatementReturnGeneratedKeysSupported();

	@Override
	public void registerManagers() {
		new ResourceManagerGuildImpl(this);
		new ResourceManagerPlayerImpl(this);
		new ResourceManagerRankImpl(this);
		new ResourceManagerRegionImpl(this);
	}

	@Override
	public boolean setUp() {
		return connect();
	}

	/**
	 * Prepares the statements
	 */
	protected void prepareStatements() {
		try {
			long nanoTime = System.nanoTime();
			LoggerUtils.info("Preparing statements...");
			preparedStatementMap.clear();
			connect();

			int returnKeys = isStatementReturnGeneratedKeysSupported() ? Statement.RETURN_GENERATED_KEYS : Statement.NO_GENERATED_KEYS;

			//Guilds insert (id, uuid, tag, name, leader, spawn, allies, alliesinv, war, nowarinv, money, points, lives, timerest, lostlive, activity, created, bankloc, slots, openinv, banner)
			String guildsInsertSQL = "INSERT INTO `" + Config.MYSQL_PREFIX.getString() + "guilds` VALUES(null,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?);";
			PreparedStatement guildsInsert = getConnection().prepareStatement(guildsInsertSQL, returnKeys);
			preparedStatementMap.put(PreparedStatements.GUILDS_INSERT, guildsInsert);

			//Guilds select
			String guildsSelectSQL = "SELECT * FROM `" + Config.MYSQL_PREFIX.getString() + "guilds`";
			PreparedStatement guildsSelect = getConnection().prepareStatement(guildsSelectSQL);
			preparedStatementMap.put(PreparedStatements.GUILDS_SELECT, guildsSelect);

			//Guilds delete
			String guildsDeleteSQL = "DELETE FROM `" + Config.MYSQL_PREFIX.getString() + "guilds` WHERE `uuid`=?";
			PreparedStatement guildsDelete = getConnection().prepareStatement(guildsDeleteSQL);
			preparedStatementMap.put(PreparedStatements.GUILDS_DELETE, guildsDelete);

			//Guilds update
			String guildsUpdateSQL = "UPDATE `" + Config.MYSQL_PREFIX.getString() + "guilds` SET `tag`=?, `name`=?, `leader`=?, `spawn`=?, `allies`=?, `alliesinv`=?, `war`=?, `nowarinv`=?, `money`=?, `points`=?, `lives`=?, `timerest`=?, `lostlive`=?, `activity`=?, `bankloc`=?, `slots`=?, `openinv`=?, `banner`=? WHERE `uuid`=?";
			PreparedStatement guildsUpdate = getConnection().prepareStatement(guildsUpdateSQL);
			preparedStatementMap.put(PreparedStatements.GUILDS_UPDATE, guildsUpdate);


			//Players insert (id, uuid, name, guild, invitedto, points, kills, deaths)
			String playersInsertSQL = "INSERT INTO `" + Config.MYSQL_PREFIX.getString() + "players` VALUES(null,?,?,?,?,?,?,?)";
			PreparedStatement playersInsert = getConnection().prepareStatement(playersInsertSQL, returnKeys);
			preparedStatementMap.put(PreparedStatements.PLAYERS_INSERT, playersInsert);

			//Players select
			String playerSelectSQL = "SELECT * FROM `" + Config.MYSQL_PREFIX.getString() + "players`";
			PreparedStatement playersSelect = getConnection().prepareStatement(playerSelectSQL);
			preparedStatementMap.put(PreparedStatements.PLAYERS_SELECT, playersSelect);

			//Players update
			String playersUpdateSQL = "UPDATE `" + Config.MYSQL_PREFIX.getString() + "players` SET `invitedto`=?, `guild`=?, `points`=?, `kills`=?, `deaths`=? WHERE `uuid`=?";
			PreparedStatement playersUpdate = getConnection().prepareStatement(playersUpdateSQL);
			preparedStatementMap.put(PreparedStatements.PLAYERS_UPDATE, playersUpdate);

			//Players delete
			String playersDeleteSQL = "DELETE FROM `" + Config.MYSQL_PREFIX.getString() + "players` WHERE `uuid`=?";
			PreparedStatement playersDelete = getConnection().prepareStatement(playersDeleteSQL);
			preparedStatementMap.put(PreparedStatements.PLAYERS_DELETE, playersDelete);


			//Regions insert (id, uuid, loc_1, loc_2, guild, world)
			String regionsInsertSQL = "INSERT INTO `" + Config.MYSQL_PREFIX.getString() + "regions` VALUES(null,?,?,?,?,?);";
			PreparedStatement regionsInsert = getConnection().prepareStatement(regionsInsertSQL, returnKeys);
			preparedStatementMap.put(PreparedStatements.REGIONS_INSERT, regionsInsert);

			//Regions select
			String regionsSelectSQL = "SELECT * FROM `" + Config.MYSQL_PREFIX.getString() + "regions`";
			PreparedStatement regionsSelect = getConnection().prepareStatement(regionsSelectSQL);
			preparedStatementMap.put(PreparedStatements.REGIONS_SELECT, regionsSelect);

			//Regions delete
			String regionsDeleteSQL = "DELETE FROM `" + Config.MYSQL_PREFIX.getString() + "regions` WHERE `uuid`=?";
			PreparedStatement regionsDelete = getConnection().prepareStatement(regionsDeleteSQL);
			preparedStatementMap.put(PreparedStatements.REGIONS_DELETE, regionsDelete);

			//Regions update
			String regionsUpdateSQL = "UPDATE `" + Config.MYSQL_PREFIX.getString() + "regions` SET `loc_1`=?, `loc_2`=?, `guild`=?, `world`=? WHERE `uuid`=?";
			PreparedStatement regionsUpdate = getConnection().prepareStatement(regionsUpdateSQL);
			preparedStatementMap.put(PreparedStatements.REGIONS_UPDATE, regionsUpdate);


			//Ranks insert (id, uuid, name, guild, permissions, players, default, clone)
			String ranksInsertSQL = "INSERT INTO `" + Config.MYSQL_PREFIX.getString() + "ranks` VALUES(null,?,?,?,?,?,?,?);";
			PreparedStatement ranksInsert = getConnection().prepareStatement(ranksInsertSQL, returnKeys);
			preparedStatementMap.put(PreparedStatements.RANKS_INSERT, ranksInsert);

			//Ranks select
			String ranksSelectSQL = "SELECT * FROM `" + Config.MYSQL_PREFIX.getString() + "ranks`";
			PreparedStatement ranksSelect = getConnection().prepareStatement(ranksSelectSQL);
			preparedStatementMap.put(PreparedStatements.RANKS_SELECT, ranksSelect);

			//Ranks delete
			String ranksDeleteSQL = "DELETE FROM `" + Config.MYSQL_PREFIX.getString() + "ranks` WHERE `uuid`=?";
			PreparedStatement ranksDelete = getConnection().prepareStatement(ranksDeleteSQL);
			preparedStatementMap.put(PreparedStatements.RANKS_DELETE, ranksDelete);

			//Ranks delete (guild)
			String ranksDeleteGuildSQL = "DELETE FROM `" + Config.MYSQL_PREFIX.getString() + "ranks` WHERE `guild`=?";
			PreparedStatement ranksDeleteGuild = getConnection().prepareStatement(ranksDeleteGuildSQL);
			preparedStatementMap.put(PreparedStatements.RANKS_DELETE_GUILD, ranksDeleteGuild);

			//Ranks update
			String ranksUpdateSQL = "UPDATE `" + Config.MYSQL_PREFIX.getString() + "ranks` SET `name`=?, `guild`=?, `permissions`=?, `members`=?, `def`=?, `clone`=? WHERE `uuid`=?";
			PreparedStatement ranksUpdate = getConnection().prepareStatement(ranksUpdateSQL);
			preparedStatementMap.put(PreparedStatements.RANKS_UPDATE, ranksUpdate);

			//Log
			LoggerUtils.info("Statements prepared in " + TimeUnit.MILLISECONDS.convert((System.nanoTime() - nanoTime), TimeUnit.NANOSECONDS) / 1000.0 + "s");
		}
		catch(SQLException e) {
			LoggerUtils.exception(e);
		}
	}

	/**
	 * Gets a prepared statement
	 *
	 * @param statement the enum
	 * @return the statement
	 * @throws SQLException when something goes wrong
	 */
	public PreparedStatement getPreparedStatement(PreparedStatements statement) throws SQLException {
		if(preparedStatementMap.isEmpty() || !preparedStatementMap.containsKey(statement)) {
			prepareStatements();
		}

		if(preparedStatementMap.get(statement) != null && !(this instanceof SQLiteStorageImpl) && preparedStatementMap.get(statement).isClosed()) {
			prepareStatements();
		}

		PreparedStatement preparedStatement = preparedStatementMap.get(statement);
		preparedStatement.clearParameters();

		return preparedStatement;
	}

	/**
	 * Checks if tables exist in the database
	 *
	 * @return boolean
	 * @throws SQLException when something goes wrong
	 */
	protected boolean checkTables() throws SQLException {
		DatabaseMetaData md = getConnection().getMetaData();
		ResultSet rs = md.getTables(null, null, Config.MYSQL_PREFIX.getString() + "%", null);
		return rs.next();
	}

	/**
	 * Adds tables to the database
	 * @throws SQLException when something goes wrong
	 * @throws IOException  when something goes wrong
	 */
	protected void setupTables() throws SQLException, IOException {
		for(String tableCode : getSqlActions()) {
			Statement statement = getConnection().createStatement();
			statement.executeUpdate(tableCode);
			LoggerUtils.info("Table added to the database!");
		}
	}

	/**
	 * Analyzes the database
	 */
	protected void analyze() {
		try {
			LoggerUtils.info("Analyzing the database...");
			DatabaseAnalyzer analyzer = new DatabaseAnalyzerImpl(getConnection());

			for(String action : getSqlActions()) {
				if(action.contains("CREATE TABLE")) {
					String table = StringUtils.split(action, '`')[1];
					LoggerUtils.info("Table: " + table, false);
					analyzer.analyze(table, action);
				}
			}

			analyzer.update();
		}
		catch(Exception e) {
			LoggerUtils.exception(e);
		}
	}

	/**
	 * Gets an array of SQL create table codes
	 *
	 * @return the array of strings
	 */
	private String[] getSqlActions() throws IOException {
		InputStream inputStream = plugin.getResource("sql/" + (plugin.getConfigManager().getDataStorageType() == DataStorageType.MYSQL ? "mysql" : "sqlite") + ".sql");
		String sqlString = IOUtils.inputStreamToString(inputStream);

		if(sqlString.isEmpty() || !sqlString.contains("--")) {
			LoggerUtils.error("Invalid SQL");
			return new String[0];
		}

		sqlString = StringUtils.replace(sqlString, "{SQLPREFIX}", Config.MYSQL_PREFIX.getString());
		return sqlString.split("--");
	}
}
