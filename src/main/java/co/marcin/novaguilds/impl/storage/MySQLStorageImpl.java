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
import co.marcin.novaguilds.util.reflect.Reflections;

import java.io.IOException;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.TimeUnit;

public class MySQLStorageImpl extends AbstractDatabaseStorage {
	private final String username;
	private final String database;
	private final String password;
	private final String port;
	private final String hostname;
	private Throwable failureCause;

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
		this.hostname = hostname;
		this.port = port;
		this.database = database;
		this.username = username;
		this.password = password;

		if(!setUp()) {
			throw new StorageConnectionFailedException("Failed while connecting to MySQL database", failureCause);
		}
	}

	@Override
	public void openConnection() throws SQLException, ClassNotFoundException {
		if(checkConnection()) {
			return;
		}

		Reflections.getClass("com.mysql.jdbc.Driver");
		connection = DriverManager.getConnection("jdbc:mysql://" + hostname + ":" + port + "/" + database + "?autoReconnect=true", username, password);
	}

	@Override
	public boolean connect() {
		long nanoTime = System.nanoTime();

		try {
			if(!firstConnect) {
				getConnection().isValid(1000);
				if(checkConnection()) {
					return true;
				}
			}
		}
		catch(SQLException e) {
			LoggerUtils.info("MySQL reconnect is required.");
		}

		try {
			openConnection();
			LoggerUtils.info("Connected to MySQL database in " + TimeUnit.MILLISECONDS.convert((System.nanoTime() - nanoTime), TimeUnit.NANOSECONDS) / 1000.0 + "s");

			if(firstConnect) {
				if(!checkTables()) {
					setupTables();
				}

				analyze();
				firstConnect = false;
			}

			prepareStatements();

			return true;
		}
		catch(SQLException | ClassNotFoundException | IOException e) {
			failureCause = e;
			return false;
		}
	}

	@Override
	public Integer returnGeneratedKey(Statement statement) {
		try {
			ResultSet keys = statement.getGeneratedKeys();
			keys.next();
			int id = keys.getInt(1);

			if(id == 0) {
				throw new RuntimeException("Could not get generated keys");
			}

			return id;
		}
		catch(SQLException e) {
			throw new RuntimeException("Could not get generated keys", e);
		}
	}

	@Override
	public boolean isStatementReturnGeneratedKeysSupported() {
		return true;
	}
}
